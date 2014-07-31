package eu.lod2.edcat.contextPlugin;

import eu.lod2.edcat.utils.JsonLD;
import eu.lod2.edcat.utils.JsonLdContext;
import eu.lod2.query.Db;
import org.openrdf.model.Model;
import org.openrdf.model.impl.URIImpl;

/**
 * Created by yyz on 7/30/14.
 */
public class loadTest {
    public static void main(String[] args) throws Throwable{
        JsonLdContext jsonLdContext=new
                JsonLdContext(ContextController.class.getResource("/eu/lod2/edcat/jsonld/nif.jsonld"));
        String nif="{\n" +
                "  \"@context\": {\"nif\":\"http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#\"},\n" +
                "  \"nif:title\": \"test\",\n" +
                "  \"@type\": [\"nif:String\",\"nif:Word\",\"nif:RFC5147String\"],\n" +
                "  \"@id\":\"http://127.0.0.1:8080/context/article/53a49fbdcf5dd21815bbd77a#char=151,163\"\n" +
                "}";
        JsonLD json= JsonLD.parse(nif);
        json.setContext( jsonLdContext );
        Model statements=json.getStatements();
        Db.add(statements, new URIImpl("http://lod2.tenforce.com/edcat/context/config/"));
    }
}
