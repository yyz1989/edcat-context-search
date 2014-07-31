package eu.lod2.edcat.contextPlugin.hooks.handlers;

import eu.lod2.edcat.contextPlugin.hooks.contexts.PreLoadContext;
import eu.lod2.edcat.contextPlugin.hooks.contexts.PreSearchContext;
import eu.lod2.hooks.handlers.HookHandler;

/**
 * Implement if you are a provider for the PreLoadHandler
 * <p/>
 * Implementing this interface requires the hook to exist.  If you don't want to depend
 * on the hook being loaded, check out {@link eu.lod2.hooks.handlers.OptionalHookHandler}.
 * The supplied {@code args} are the same as the ones specified in this interface.
 * The name for this hook is {@code "eu.lod2.edcat.contextPlugin.hooks.handlers.PreLoadHandler"}.
 */
public interface PreLoadHandler extends HookHandler {

  /**
   * Called before a Load action is constructed
   * <p/>
   * This hook allows you to alter the request or possibly abort the action.
   *
   * @param context Contains all information the consumer provides for this provider.
   * @throws eu.lod2.hooks.handlers.dcat.ActionAbortException Throwing this exception will abort the Load action.
   * @see eu.lod2.edcat.contextPlugin.hooks.contexts.PreLoadContext
   */
  public void handlePreLoad(PreLoadContext context);

}
