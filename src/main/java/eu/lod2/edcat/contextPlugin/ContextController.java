package eu.lod2.edcat.contextPlugin;

import eu.lod2.edcat.format.CompactedListFormatter;
import eu.lod2.edcat.format.ResponseFormatter;
import eu.lod2.edcat.utils.DcatURI;
import eu.lod2.edcat.utils.JsonLdContext;
import org.openrdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by yyz on 7/29/14.
 */
@Controller
public class ContextController {
    private final Logger logger= LoggerFactory.getLogger(this.getClass());
    public static final String SEARCH_PATH= DcatURI.CATALOG_LIST_PATH+"/context"
            +"/{tags[]}";

    @Autowired
    private ContextService contextService;

    //GET ../catalogs/context/a,b,c,d
    @RequestMapping(value = SEARCH_PATH, method = RequestMethod.GET,
            produces = "application/json;charset=UTF-8")
    public ResponseEntity<Object> contextSearch(@PathVariable(value="tags") String[] tags) throws Throwable{
        JsonLdContext jsonLdContext = new JsonLdContext(JsonLdContext.Kind.Dataset);
        Model statements=contextService.getDatasetModel(tags);
        ResponseFormatter formatter=new CompactedListFormatter(jsonLdContext);
        Object body=formatter.format(statements);
        ResponseEntity<Object> response = new ResponseEntity<Object>(body, new HttpHeaders(), HttpStatus.OK);
        return response;
    }
}
