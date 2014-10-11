package org.cloudfoundry.ide.eclipse.server.ui.internal.actions;

import org.cloudfoundry.ide.eclipse.server.core.internal.CloudFoundryPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.internal.view.servers.RemoveModuleAction;

/**
 * It is a menu action responsible for deleting the cloud application
 * which has been published to Cloud Foundry.
 * 
 * <p>
 * Just re-write <code>removeModules()</code> method of its super-class
 * <code>RemoveModuleAction</code> to avoid publishing all the bound
 * cloud applications remaining in current server instance.
 * 
 *
 */
@SuppressWarnings("restriction")
public class RemoveCloudApplicationAction extends RemoveModuleAction {
	
	/**
	 * Constructs an instance of <code>RemoveCloudApplicationAction</code>.
	 * 
	 * @param shell the shell to show the dialog to confirm the deletion
	 * @param server the server currently holding the target module
	 * @param module the module instance to be removed
	 */
	public RemoveCloudApplicationAction(Shell shell, IServer server, IModule module) {
		super(shell, server, module);
	}
	
	protected void removeModules(final IModule[] modules) {
		try {
			final ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
			dialog.setBlockOnOpen(false);
			dialog.setCancelable(true);
			IRunnableWithProgress runnable = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) {
					try {
						IServerWorkingCopy wc = server.createWorkingCopy();
						if (monitor.isCanceled()) {
							return;
						}
						wc.modifyModules(null, modules, monitor);
						if (monitor.isCanceled()) {
							return;
						}
						server = wc.save(true, monitor);
					} catch (CoreException ce) {
						logError(ce);
					}
				}
			};
			dialog.run(true, true, runnable);
		} catch (Exception e) {
			logError(e);
		}
	}
	
	private void logError(Throwable t) {
		CloudFoundryPlugin.logError(t);
	}
}
