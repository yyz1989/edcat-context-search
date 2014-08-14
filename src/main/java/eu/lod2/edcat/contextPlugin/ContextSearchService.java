package eu.lod2.edcat.contextPlugin;

import eu.lod2.query.Db;
import org.openrdf.model.Model;
import org.openrdf.model.impl.LinkedHashModel;
import org.springframework.stereotype.Service;

/**
 * Created by yyz on 7/29/14.
 */
@Service
public class ContextSearchService {
    /**
     * Given a list of tags, return a graph containing the title, description,
     * number of tags found of all qualified datasets
     * @param tagIds a list of Ids of tags to be matched against datasets
     * @return a new graph containing the title, description, number of tags found
     * of all qualified datasets
     */
    public Model getDatasetModel(String[] tagIds) {
        String where = tagsToWhereClause(tagIds);
        return constructDatasetModel(where);
    }

    /**
     * Given a list of tags, return all the datasets containing at least one tag
     * @param where a sparql where clause describing tags to be matched
     *              against datasets
     * @return a new graph containing the title, description, number of tags found
     * of all qualified datasets
     */
    private Model constructDatasetModel(String where) {
        /**
         * The following SPARQL query contains 3 levels of queries: the bottom
         * query collects all the referenced URIs containing at least one given tag;
         * the middle level query collects the source URIs of referenced URIs which
         * actually represent the datasets; the top level query collects the title,
         * description of datasets containing at least one given tag and provide the
         * number of tags found in the same dataset. The final result will be
         * constructed to a new graph.
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
                            "           ?ref <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#sourceUrl> ?dataset ." +
                            "           { " +
                            "               SELECT DISTINCT ?ref (COUNT(?tag) AS ?count) " +
                            "               WHERE {" +
                            "                   GRAPH <http://lod2.tenforce.com/edcat/context/config/> {" +
                            "                       ?segment <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#referenceContext> ?ref . " +
                            "                       ?segment <http://purl.org/dc/terms/identifier> ?tagId ." +
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
     * Converts the given tags marked by the context app to a SPARQL where
     * clause that can be injected in a SPARQL query
     * @param tagIds  Ids of tags found by the context app in a given text
     *
     * @return A SPARQL where clause as String to inject in a SPARQL query
     */
    private String tagsToWhereClause(String[] tagIds) {
        if (tagIds.length==0) return "";
        String where = String.format("FILTER (STR(?tagId) = \"%s\"", tagIds[0]);
        for (int i=1; i<tagIds.length; i++) {
            where += String.format(" || STR(?tagId) = \"%s\" ", tagIds[i]);
        }
        where += ")";
        return where;
    }

}
