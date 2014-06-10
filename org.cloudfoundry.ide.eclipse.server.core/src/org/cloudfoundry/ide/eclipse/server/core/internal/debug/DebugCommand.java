/*******************************************************************************
 * Copyright (c) 2012, 2014 Pivotal Software, Inc. 
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, 
 * Version 2.0 (the "License�); you may not use this file except in compliance 
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *  
 *  Contributors:
 *     Pivotal Software, Inc. - initial API and implementation
 ********************************************************************************/
package org.cloudfoundry.ide.eclipse.server.core.internal.debug;

import java.util.HashMap;
import java.util.Map;

import org.cloudfoundry.ide.eclipse.server.core.internal.ApplicationAction;
import org.cloudfoundry.ide.eclipse.server.core.internal.CloudFoundryServer;
import org.cloudfoundry.ide.eclipse.server.core.internal.client.CloudFoundryApplicationModule;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IModule;


/**
 * Base debug command that performs a debug connection to a given cloud foundry
 * server and also registers a command to a debugger connection tracker. Note
 * that the registration of the command is automatic, and only one application
 * with a unique ID can be registered, regardless of how many debug commands are
 * associated with that application. An application's unique ID is determined by
 * the server in which the application is running and the application ID.
 * 
 */
public abstract class DebugCommand {

	private final CloudFoundryServer server;

	private final IModule[] modules;

	private final ApplicationAction debugAction;

	private String debugConnectionID;

	private ICloudFoundryDebuggerListener listener;

	/**
	 * Keeps track of which applications have been connected to the debugger.
	 * Only one debug command can be registered for a distinct application.
	 * Existing debug commands are not automatically overwritten unless first
	 * removed.
	 */
	private static final Map<String, DebugCommand> debuggerConnectionMap = new HashMap<String, DebugCommand>();

	public DebugCommand(CloudFoundryServer server, IModule[] modules, ApplicationAction debugAction,
			ICloudFoundryDebuggerListener listener) {
		this.server = server;
		this.modules = modules;
		this.debugAction = debugAction;
		this.listener = listener;
	}

	public ICloudFoundryDebuggerListener getListener() {
		return listener;
	}

	/**
	 * Identifier used to track whether an application is connected to a
	 * debugger.
	 * @return non-null identifier.
	 */
	public String getDebuggerConnectionIdentifier() {
		if (debugConnectionID == null) {
			debugConnectionID = getDebuggerConnectionIdentifier(getCloudFoundryServer(), getModules());
		}
		return debugConnectionID;
	}

	/**
	 * Computes a new debugger connection identifier based on the given server
	 * and modules.
	 * @param server must not be null
	 * @param modules must not be null or empty
	 * @return non-null debugger connection identifier
	 */
	public static String getDebuggerConnectionIdentifier(CloudFoundryServer server, IModule[] modules) {
		StringBuilder idBuffer = new StringBuilder();

		idBuffer.append(server.getUrl());
		idBuffer.append(server.getUsername());

		
		CloudFoundryApplicationModule appModule = server.getExistingCloudModule(modules[0]);
		if (appModule != null) {
			idBuffer.append(appModule.getDeployedApplicationName());
		}
		return idBuffer.toString();
	}

	public ApplicationAction getDebugApplicationAction() {
		return debugAction;
	}

	protected CloudFoundryServer getCloudFoundryServer() {
		return server;
	}

	public IModule[] getModules() {
		return modules;
	}

	public String getApplicationID() {

		CloudFoundryApplicationModule appModule = getApplicationModule();
		if (appModule != null) {
			return appModule.getDeployedApplicationName();
		}
		return null;
	}

	public CloudFoundryApplicationModule getApplicationModule() {
		return getCloudFoundryServer().getExistingCloudModule(modules[0]);
	}

	public void run(IProgressMonitor monitor) {
		addDebuggerConnection();
		connect(monitor);
	}

	/**
	 * Run command behaviour during connection phase
	 * @param monitor
	 */
	abstract protected void connect(IProgressMonitor monitor);

	abstract public String getCommandName();

	/**
	 * If a listener already exists for the same connection ID, the existing
	 * listener will be used
	 * @param command
	 * @param listener
	 */
	protected void addDebuggerConnection() {
		synchronized (debuggerConnectionMap) {
			String id = getDebuggerConnectionIdentifier();
			if (!debuggerConnectionMap.containsKey(id) && id != null && id.length() > 0) {
				debuggerConnectionMap.put(id, this);
			}
		}
	}

	/**
	 * Gets a debug command associated with the given debugger connection ID, if
	 * one is registered. This would indicate that the application associated
	 * with the debug command is currently connected to a debugger
	 * @param connectionID associated with the application connected to a
	 * debugger
	 * @return debug command for application currently connected to a debugger,
	 * or null if application is not connected to a debugger
	 */
	public static DebugCommand getDebugCommand(String connectionID) {
		synchronized (debuggerConnectionMap) {
			return debuggerConnectionMap.get(connectionID);
		}
	}

	/**
	 * Removes the debug command from the connection register. This is called
	 * when the application is no longer connected to a debugger. If the
	 * application is already disconnected from a debugger, this method has no
	 * effect.
	 */
	public void removeFromConnectionRegister() {
		synchronized (debuggerConnectionMap) {
			debuggerConnectionMap.remove(getDebuggerConnectionIdentifier());
		}
	}

	/**
	 * Determines if the application associated with the given module and server
	 * is connected to a debugger. Note that this does not check if the
	 * application is currently running in the server. It only checks if the
	 * application is connected to a debugger, although implicitly any
	 * application connected to a debugger is also running, but not vice-versa.
	 * 
	 * @param server
	 * @param modules
	 * @return true if associated application is connected to a debugger. False
	 * otherwise
	 */
	public static boolean isConnectedToDebugger(CloudFoundryServer server, IModule[] modules) {
		synchronized (debuggerConnectionMap) {
			DebugCommand command = getDebugCommand(getDebuggerConnectionIdentifier(server, modules));
			return command != null;
		}
	}

}
