package net.flexmojos.m2e.internal.project;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.adobe.flexbuilder.project.FlexProjectManager;
import com.adobe.flexbuilder.project.FlexServerType;
import com.adobe.flexbuilder.project.IMutableFlexProjectSettings;
import com.adobe.flexbuilder.project.actionscript.IMutableActionScriptProjectSettings;
import com.adobe.flexbuilder.project.internal.FlexProjectSettings;

/**
 * This class is responsible of instantiate a project settings object and returns it as an interface. Because the
 * connector is build against flexbuilder 4.7 components, to not break backward-compatibility, calling a non-existing
 * method on the target object will not throw exceptions. Instead, it will be logged and reported to the user.
 * 
 * @author Sylvain Lecoy (sylvain.lecoy@gmail.com)
 *
 */
public class FB47ProjectManager implements IProjectManager {

  private static class FlexProxyAdapter extends AbstractProxyAdapter implements InvocationHandler {

    public FlexProxyAdapter(IProject project, boolean overrideHTMLWrapperDefault) {
      this.project = project;
//      int major = com.adobe.flexbuilder.project.FlexProjectConstants.AMT_FB4_MAJOR_VERSION;
//      int minor = com.adobe.flexbuilder.project.FlexProjectConstants.AMT_FB4_MINOR_VERSION;
      this.settings = FlexProjectManager.createFlexProjectDescription(
          project.getName(),
          project.getLocation(),
          overrideHTMLWrapperDefault,
          FlexServerType.NO_SERVER /* Since its a Maven project, the server is on another module. */);
    }

    @Override
    public void saveDescription(IProject project, IProgressMonitor monitor) throws CoreException {
      FlexProjectSettings flexProjectSettings = (FlexProjectSettings) settings;
      flexProjectSettings.saveDescription(project, monitor);
    }

  }

  /* (non-Javadoc)
   * @see net.flexmojos.m2e.internal.IProjectManager#createFlexProjectDescription(org.eclipse.core.resources.IProject, boolean)
   */
  @Override
  public IMutableFlexProjectSettings createFlexProjectDescription(IProject project, boolean overrideHTMLWrapperDefault) {
    return (IMutableFlexProjectSettings) Proxy.newProxyInstance(
        FB47ProjectManager.class.getClassLoader(),
        new Class[] {IMutableFlexProjectSettings.class},
        new FlexProxyAdapter(project, overrideHTMLWrapperDefault));
  }

  /* (non-Javadoc)
   * @see net.flexmojos.m2e.internal.IProjectManager#saveDescription(org.eclipse.core.resources.IProject, com.adobe.flexbuilder.project.actionscript.IMutableActionScriptProjectSettings, org.eclipse.core.runtime.IProgressMonitor)
   */
  @Override
  public void saveDescription(IProject project, IMutableActionScriptProjectSettings settings, IProgressMonitor monitor) throws CoreException {
    AbstractProxyAdapter abstractProxyAdapter = ((AbstractProxyAdapter)Proxy.getInvocationHandler(settings));
    abstractProxyAdapter.saveDescription(project, monitor);
  }

}
