package eu.lod2.edcat.contextPlugin;

import eu.lod2.query.Db;
import org.openrdf.model.Model;
import org.openrdf.model.impl.LinkedHashModel;

/**
 * Created by yyz on 7/29/14.
 */

public class ContextService {

    public Model getDatasetModel(String[] tags) {
        String where = tagsToWhereClause(tags);
        return constructDatasetModel(where);
    }

    private Model constructDatasetModel(String where) {
        /**
         * The following SPARQL query contains two levels of queries: the subquery
         * collects all the datasets linked to the catalogs satisfying the constaints
         * for catalogs; while the parent query collects all the datasets satisfying the
         * constraints for datasets. The returned values are qualified catalogs linking
         * to the qualified datasets.
         */
        if (where.equals("")) return new LinkedHashModel();
        else {
            Model sparqlResults = Db.construct("" +
                            " @PREFIX " +
                            " CONSTRUCT { " +
                            "   ?dataset dct:title ?title." +
                            "   ?dataset dct:description ?description." +
                            "   ?dataset <http://edcat.tenforce.com/terms#count> ?count. " +
                            " }" +
                            " WHERE { " +
                            "   GRAPH ?dataset {" +
                            "   ?dataset dct:title ?title. " +
                            "   ?dataset dct:description ?description. " +
                            "   {" +
                            "       SELECT ?dataset ?count" +
                            "       WHERE {" +
                            "       GRAPH ?ref { " +
                            "           ?ref nif:sourceUrl ?dataset ." +
                            "           { " +
                            "               SELECT DISTINCT ?ref (COUNT(?tag) AS ?count) " +
                            "               WHERE {" +
                            "                   GRAPH ?graph {" +
                            "                       ?segment nif:referenceContext ?ref. " +
                            "                       ?segment nif:anchorOf ?tag ." +
                            "                       $where " +
                            "                   } " +
                            "               }" +
                            "               GROUP BY ?ref " +
                            "           } " +
                            "       } " +
                            "       }" +
                            "   }" +
                            " }" +
                            "}",
                    "where", where);
            return sparqlResults;
        }
    }


    /**
     * Converts the given search criteria to a SPARQL where clause that can be injected in a SPARQL query
     * @param tags  Map with search criteria mapping keywords to a value (e.g. [distribution][title] -> ["My title"])
     *
     * @return A SPARQL where clause as String to inject in a SPARQL query
     */
    private String tagsToWhereClause(String[] tags) {
        if (tags.length==0) return "";
        String where = "FILTER (str(?tag) = "+tags[0];
        for (int i=1; i<tags.length; i++) {
            where += String.format(" || str(?tag) = %s ", tags[i]);
        }
        where += ")";
        return where;
    }

}
