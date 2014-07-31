package eu.lod2.edcat.contextPlugin.hooks.contexts;

import eu.lod2.hooks.contexts.base.AtContextBase;
import eu.lod2.hooks.contexts.base.PreContextBase;
import org.openrdf.model.Model;

import javax.servlet.http.HttpServletRequest;

public class AtLoadContext implements AtContextBase {

  /** Request as sent by the user. */
  private HttpServletRequest request;

    /** Statements of RDF triples. */
  private Model statements;

  /**
   * Constructs a new AtLoadContext with all variables set.
   */
  public AtLoadContext(HttpServletRequest request, Model statements){
    setRequest( request );
    setStatements( statements );
  }

  /**
   * Retrieves the request object as sent by the user.
   *
   * @return HttpServletRequest as sent by the user.
   */
  public HttpServletRequest getRequest() {
    return request;
  }

  /**
   * Sets the request as sent by the user in this context.
   *
   * @param request Request as sent by the user.
   */
  protected void setRequest( HttpServletRequest request){
    this.request = request;
  }

    /**
     * Retrieves the statements object
     * @return statements of RDF triples
     */
    public Model getStatements() {
        return statements;
    }

    /**
     * Sets the statements for this context
     * @param statements Statements of RDF triples
     */
    protected void setStatements(Model statements) {
        this.statements = statements;
    }

}
