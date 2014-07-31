package eu.lod2.edcat.contextPlugin;

import eu.lod2.edcat.contextPlugin.hooks.contexts.PostSearchContext;
import eu.lod2.edcat.contextPlugin.hooks.contexts.PreSearchContext;
import eu.lod2.edcat.contextPlugin.hooks.handlers.PostSearchHandler;
import eu.lod2.edcat.contextPlugin.hooks.handlers.PreSearchHandler;
import eu.lod2.edcat.format.CompactedListFormatter;
import eu.lod2.edcat.format.CompactedObjectFormatter;
import eu.lod2.edcat.format.ResponseFormatter;
import eu.lod2.edcat.utils.JsonLD;
import eu.lod2.edcat.utils.JsonLdContext;
import eu.lod2.hooks.util.HookManager;
import eu.lod2.query.Db;
import org.openrdf.model.Model;
import org.openrdf.model.impl.URIImpl;
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

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;

/**
 * Created by yyz on 7/29/14.
 */
@Controller
public class ContextController {

    private final Logger logger= LoggerFactory.getLogger(this.getClass());
    public static final String SEARCH_PATH= "/contexts/search/{tags[]}";
    public static final String LOAD_PATH="/contexts/load";
    public static final String CONTEXT_PATH="http://tfvirt-lod2-dcat/contexts";
    private final JsonLdContext jsonLdContext=new
            JsonLdContext(ContextController.class.getResource("/eu/lod2/edcat/jsonld/nif.jsonld"));

    @Autowired
    private ContextService contextService;

    //GET ../edcat/contexts/a,b,c,d
    @RequestMapping(value = SEARCH_PATH, method = RequestMethod.GET,
            produces = "application/json;charset=UTF-8")
    public ResponseEntity<Object> contextSearch(HttpServletRequest request, @PathVariable(value="tags") String[] tags) throws Throwable{
        HookManager.callHook(PreSearchHandler.class, "handlePreSearch", new PreSearchContext(request));
        JsonLdContext jsonLdContext = new JsonLdContext(JsonLdContext.Kind.Dataset);
        Model statements=contextService.getDatasetModel(tags);
        ResponseFormatter formatter=new CompactedListFormatter(jsonLdContext);
        Object body=formatter.format(statements);
        ResponseEntity<Object> response = new ResponseEntity<Object>(body, new HttpHeaders(), HttpStatus.OK);
        HookManager.callHook(PostSearchHandler.class, "handlePostSearch", new PostSearchContext(request, response, statements));
        return response;
    }

    @RequestMapping(value=LOAD_PATH, method = RequestMethod.POST,
            consumes = "application/json;charset=UTF-8")
    public ResponseEntity<Object> contextLoad(HttpServletRequest request) throws Throwable {
        InputStream in=request.getInputStream();
        JsonLD json= JsonLD.parse(in);
        in.close();
        json.setContext( jsonLdContext );
        Model statements=json.getStatements();
        Db.add(statements, new URIImpl("http://lod2.tenforce.com/edcat/context/config/"));
        ResponseFormatter formatter = new CompactedObjectFormatter( jsonLdContext );
        Object body = formatter.format( statements );
        ResponseEntity<Object> response = new ResponseEntity<Object>( body, new HttpHeaders(), HttpStatus.OK );
        return response;
    }
}
