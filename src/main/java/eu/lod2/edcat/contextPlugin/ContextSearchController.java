package eu.lod2.edcat.contextPlugin;

import eu.lod2.edcat.contextPlugin.hooks.contexts.*;
import eu.lod2.edcat.contextPlugin.hooks.handlers.*;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;

/**
 * Created by yyz on 7/29/14.
 */
@Controller
public class ContextSearchController {

    private final Logger logger= LoggerFactory.getLogger(this.getClass());
    public static final String SEARCH_PATH= "/context/search";
    public static final String LOAD_PATH="/context/load";
    private final JsonLdContext jsonLdContext=new
            JsonLdContext(ContextSearchController.class.getResource("/eu/lod2/edcat/jsonld/nif.jsonld"));

    @Autowired
    private ContextSearchService contextSearchService;

    //GET ../edcat/context/search?tagIds=Id1,Id2,Id3,...
    @RequestMapping(value = SEARCH_PATH, method = RequestMethod.GET,
            produces = "application/json;charset=UTF-8")
    public ResponseEntity<Object> contextSearch(HttpServletRequest request, @RequestParam(value = "tagIds[]") String[] tagIds) throws Throwable{
        HookManager.callHook(PreSearchHandler.class, "handlePreSearch", new PreSearchContext(request));
        JsonLdContext jsonLdContext = new JsonLdContext(JsonLdContext.Kind.Dataset);
        Model statements= contextSearchService.getDatasetModel(tagIds);
        ResponseFormatter formatter=new CompactedListFormatter(jsonLdContext);
        Object body=formatter.format(statements);
        ResponseEntity<Object> response = new ResponseEntity<Object>(body, new HttpHeaders(), HttpStatus.OK);
        HookManager.callHook(PostSearchHandler.class, "handlePostSearch", new PostSearchContext(request, response, statements));
        return response;
    }

    //POST ../edcat/context/load
    @RequestMapping(value=LOAD_PATH, method = RequestMethod.POST,
            consumes = "application/json;charset=UTF-8")
    public ResponseEntity<Object> contextLoad(HttpServletRequest request) throws Throwable {
        HookManager.callHook(PreLoadHandler.class, "handlePreLoad", new PreLoadContext(request));
        InputStream in=request.getInputStream();
        JsonLD json= JsonLD.parse(in);
        in.close();
        json.setContext( jsonLdContext );
        Model statements=json.getStatements();
        HookManager.callHook(AtLoadHandler.class, "handleAtLoad", new AtLoadContext(request, statements));
        Db.add(statements, new URIImpl("http://lod2.tenforce.com/edcat/context/config/"));
        ResponseFormatter formatter = new CompactedObjectFormatter( jsonLdContext );
        Object body = formatter.format( statements );
        ResponseEntity<Object> response = new ResponseEntity<Object>( body, new HttpHeaders(), HttpStatus.OK );
        HookManager.callHook(PostLoadHandler.class, "handlePostLoad", new PostLoadContext(request, response, statements));
        return response;
    }
}
