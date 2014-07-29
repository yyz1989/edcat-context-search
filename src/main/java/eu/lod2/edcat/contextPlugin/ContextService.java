package eu.lod2.edcat.contextPlugin;

import eu.lod2.edcat.utils.QueryResult;
import eu.lod2.query.Db;
import org.apache.commons.lang3.StringUtils;
import org.openrdf.model.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by yyz on 7/29/14.
 */

public class ContextService {

    public List<String> searchDatasets(Map<String[], String[]> searchCriteria, Map<String, String> context) {
        String[] where = searchCriteriaToWhereClauses(searchCriteria, context);
        List<String> datasets = sparqlSelect(where[0], where[1]);
        return datasets;
    }

    private List<String> sparqlSelect(String whereIsDataset, String whereIsCatalog) {
        /**
         * The following SPARQL query contains two levels of queries: the subquery
         * collects all the datasets linked to the catalogs satisfying the constaints
         * for catalogs; while the parent query collects all the datasets satisfying the
         * constraints for datasets. The returned values are qualified catalogs linking
         * to the qualified datasets.
         */
        QueryResult sparqlResults = Db.query("" +
                        " @PREFIX " +
                        " SELECT DISTINCT ?dataset " +
                        " WHERE {" +
                        "    GRAPH ?dataset { " +
                        "    ?dateset a dcat:Dataset ." +
                        "    $whereIsDataset "+
                        "    { "+
                        "       SELECT DISTINCT ?dataset "+
                        "       WHERE {"+
                        "         GRAPH ?catalog {"+
                        "           ?catalog a dcat:Catalog . " +
                        "           ?catalog dcat:dataset ?dataset . "+
                        "           $whereIsCatalog "+
                        "         } "+
                        "       }" +
                        "    } "+
                        "  } "+
                        "}",
                "whereIsDataset", whereIsDataset, "whereIsCatalog", whereIsCatalog);
        List<String> datasets = new ArrayList<String>();
        if (sparqlResults.size() > 0 )
            for (Map<String, String> sparqlResult : sparqlResults)
                datasets.add(sparqlResult.get("dataset"));
        return datasets;
    }

    /**
     * Converts the given search criteria to two SPARQL where clauses for SPARQL queries performed on datasets and catalogs respectively
     * @param searchCriteria  Map with search criteria mapping keywords to a value (e.g. [distribution][title] -> ["My title"])
     * @param context Context to convert the keywords to predicate URIs
     *
     * @return A string array of SPARQL where clauses (actually two) to be injected in a query respectively
     */
    private String[] searchCriteriaToWhereClauses(Map<String[], String[]> searchCriteria, Map<String, String> context) {
        int count = 0;
        String whereIsCatalog = "";
        String whereIsDataset = "";
        for (String[] keywords : searchCriteria.keySet()) {
            ArrayList<String> predicates = new ArrayList<String>(Arrays.asList(keywordsToPredicates(keywords, context)));
            ArrayList<String> values = new ArrayList<String>(Arrays.asList(searchCriteria.get(keywords)));
            /** The constraints for datasets in a catalog search will always start with
             *  the predicate dcat:dataset (e.g., search[dataset][theme][label]="wukwukfoo")
             *  and therefore can be recognized and added into two where clauses
             */
            if(predicates.get(0).equals("<http://www.w3.org/ns/dcat#dataset>")) {
                predicates.remove(0);
                for (String value : values) {
                    if (isSimpleStringValue(value)) {
                        String tmpVar = "?" + count;
                        whereIsDataset += String.format(" ?dataset %s %s .", StringUtils.join(predicates, "/"), tmpVar);
                        whereIsDataset += String.format(" FILTER(str(%s)=%s)", tmpVar, value);
                        count++;
                    } else
                        whereIsDataset += String.format(" ?dataset %s %s .", StringUtils.join(predicates, "/"), value);
                }
            }
            else {
                for (String value : values) {
                    if (isSimpleStringValue(value)) {
                        String tmpVar = "?" + count;
                        whereIsCatalog += String.format(" ?catalog %s %s .", StringUtils.join(predicates, "/"), tmpVar);
                        whereIsCatalog += String.format(" FILTER(str(%s)=%s)", tmpVar, value);
                        count++;
                    } else
                        whereIsCatalog += String.format(" ?catalog %s %s .", StringUtils.join(predicates, "/"), value);
                }
            }
        }
        return new String[]{whereIsDataset,whereIsCatalog};
    }

    /**
     * Returns the predicate URIs of the keywords in the String array
     *
     * @param keywords  Array with keywords
     * @param context Context to convert the keywords to a predicate URI.
     *                If the keyword is not found in the keywordMap, the keyword is considered to be a predicate URI.
     *
     * @return String Array with the predicate URIs (e.g. [<http://www.w3.org/ns/dcat#distribution>, <http://purl.org/dc/terms/title>])
     */
    private String[] keywordsToPredicates(String[] keywords, Map<String, String> context) {
        String[] predicates = new String[keywords.length];
        for (int i = 0; i < keywords.length; i++) {
            String uri = context.get(keywords[i]);
            if (uri == null)
                uri = keywords[i]; // use the keyword itself as predicate URI
            predicates[i] = "<" + uri + ">";
            // TODO handle case where predicate starts with @ (eg. uri -> @id)
        }
        return predicates;
    }

    /**
     * Returns whether the given value is a simple String (i.e. not a URI, no language annotation, etc.)
     */
    private boolean isSimpleStringValue(String value) {
        return value != null && value.endsWith("\"");
    }
}
