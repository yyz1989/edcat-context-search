package eu.lod2.edcat.contextPlugin;

import eu.lod2.edcat.utils.DcatURI;
import eu.lod2.edcat.utils.QueryResult;
import eu.lod2.query.Db;
import org.apache.commons.lang3.StringUtils;
import org.openrdf.model.Model;

import java.util.*;

/**
 * Created by yyz on 7/29/14.
 */

public class ContextService {

    public List<String> searchDatasets(Map<String[], String[]> searchCriteria, Map<String, String> context) {
        String where = searchCriteriaToWhereClause(searchCriteria, context);
        List<String> datasets = sparqlSelect(where);
        return datasets;
    }

    private Vector<Map<String, String>> selectDatasetURIs(String where) {
        /**
         * The following SPARQL query contains two levels of queries: the subquery
         * collects all the datasets linked to the catalogs satisfying the constaints
         * for catalogs; while the parent query collects all the datasets satisfying the
         * constraints for datasets. The returned values are qualified catalogs linking
         * to the qualified datasets.
         */
        QueryResult sparqlResults = Db.query("" +
                        " @PREFIX " +
                        " SELECT ?dataset ?count" +
                        " WHERE {" +
                        "    GRAPH ?ref { " +
                        "    ?ref nif:sourceUrl ?dataset ."+
                        "    { "+
                        "       SELECT DISTINCT ?ref (COUNT(?tag) AS ?count) "+
                        "       WHERE {"+
                        "         GRAPH ?graph {"+
                        "           ?segment nif:referenceContext ?ref. " +
                        "           ?segment nif:anchorOf ?tag ."+
                        "           $where "+
                        "         } "+
                        "       }" +
                        "       GROUP BY ?ref "+
                        "    } "+
                        "  } "+
                        "}",
                "where", where);
        return sparqlResults;
    }

    /**
     * Returns the datasets of a given catalog matching the given where clause
     * @param where Where clause the datasets must comply to
     * @param catalogId Id of the catalog to search datasets in
     *
     * @return  List of URIs of datasets matching the given where clause in the given catalog
     */
    private Map<String, String> selectDataset(String uri) {
        QueryResult sparqlResults = Db.query("" +
                        " @PREFIX" +
                        " SELECT ?title ?description" +
                        " WHERE {" +
                        "   GRAPH $graph { " +
                        "     $graph dct:title ?title."+
                        "     $graph dct:description ?description."+
                        "   }" +
                        " }",
                        "graph", uri);

        Map<String, String> result = new HashMap<String, String>();
        if (sparqlResults.size() > 0 ) result=sparqlResults.firstElement();
        return result;
    }

    /**
     * Converts the given search criteria to a SPARQL where clause that can be injected in a SPARQL query
     * @param searchCriteria  Map with search criteria mapping keywords to a value (e.g. [distribution][title] -> ["My title"])
     * @param context Context to convert the keywords to predicate URIs
     *
     * @return A SPARQL where clause as String to inject in a SPARQL query
     */
    private String searchCriteriaToWhereClause(Map<String[], String[]> searchCriteria, Map<String, String> context) {
        int count = 0;
        String where = "";

        for (String[] keywords : searchCriteria.keySet()) {
            String[] predicates = keywordsToPredicates(keywords, context);
            System.out.println(Arrays.toString(predicates));
            String[] values = searchCriteria.get(keywords);
            System.out.println(Arrays.toString(values));
            for (String value : values) {
                if (isSimpleStringValue(value)) {
                    String tmpVar = "?" + count;
                    where += String.format(" ?concept %s %s .", StringUtils.join(predicates, "/"), tmpVar);
                    where += String.format(" FILTER(str(%s)=%s)", tmpVar, value);
                    count++;
                } else
                    where += String.format(" ?concept %s %s .", StringUtils.join(predicates, "/"), value);
            }
        }
        return where;
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
