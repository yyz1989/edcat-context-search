package eu.lod2.edcat.contextPlugin;

import eu.lod2.edcat.utils.DcatURI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yyz on 7/29/14.
 */
public class ContextController {
    private final Logger logger= LoggerFactory.getLogger(this.getClass());
    public static final String SEARCH_PATH= DcatURI.CATALOG_LIST_PATH+"/context";
    private ContextService contextService;


}
