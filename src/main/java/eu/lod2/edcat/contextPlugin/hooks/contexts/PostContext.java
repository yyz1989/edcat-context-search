package eu.lod2.edcat.contextPlugin.hooks.contexts;

import eu.lod2.hooks.contexts.base.PostContextBase;
import org.openrdf.model.Model;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public class PostContext implements PostContextBase {

  /** Request as sent by the user. */
  private HttpServletRequest request;

  /** Statements which have been inserted in this request */
  private Model statements;

  /** Response which will be sent to the user. */
  private ResponseEntity<Object> response;

  /**
   * Constructs a new PostContext with all variables set.
   */
  public PostContext( HttpServletRequest request, ResponseEntity<Object> response , Model statements ){
    setRequest( request );
    setResponse( response );
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
   * Retrieves the statements which have been inserted into the database for this request.
   *
   * @return Statements which have been inserted.
   */
  public Model getStatements(){
    return statements;
  }

  /**
   * Sets the statements which have been inserted in this request.
   *
   * @param statements Model containing the statements.
   */
  protected void setStatements( Model statements ) {
    this.statements = statements;
  }

  /**
   * Sets the response which is to be sent to the user in this context.
   *
   * @param response Response which is to be sent to the user.
   */
  private void setResponse( ResponseEntity<Object> response){
    this.response = response;
  }

  /**
   * Retrieves the response object which will be sent to the user.
   *
   * @return ResponseEntity which is to be sent to the user.
   */
  public ResponseEntity<Object> getResponse(){
    return response;
  }

}
