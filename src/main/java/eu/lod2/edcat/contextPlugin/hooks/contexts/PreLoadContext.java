package eu.lod2.edcat.contextPlugin.hooks.contexts;

import eu.lod2.hooks.contexts.base.PreContextBase;

import javax.servlet.http.HttpServletRequest;

public class PreLoadContext implements PreContextBase {

  /** Request as sent by the user. */
  private HttpServletRequest request;

  /**
   * Constructs a new PreLoadContext with all variables set.
   */
  public PreLoadContext(HttpServletRequest request){
    setRequest( request );
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

}
