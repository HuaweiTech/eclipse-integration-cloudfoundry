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
package org.cloudfoundry.ide.eclipse.internal.server.ui.actions;

import org.cloudfoundry.ide.eclipse.internal.server.core.client.CloudFoundryServerBehaviour;
import org.cloudfoundry.ide.eclipse.internal.server.core.client.ICloudFoundryOperation;
import org.cloudfoundry.ide.eclipse.internal.server.ui.CloudFoundryImages;
import org.cloudfoundry.ide.eclipse.internal.server.ui.editor.CloudFoundryApplicationsEditorPage;
import org.cloudfoundry.ide.eclipse.internal.server.ui.editor.ServicesHandler;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * @author Terry Denney
 * @author Steffen Pingel
 * @author Christian Dupuis
 */
public class DeleteServicesAction extends CloudFoundryEditorAction {

	private final CloudFoundryServerBehaviour serverBehaviour;

	private final ServicesHandler servicesHandler;

	public DeleteServicesAction(IStructuredSelection selection, CloudFoundryServerBehaviour serverBehaviour,
			CloudFoundryApplicationsEditorPage editorPage) {
		super(editorPage, RefreshArea.ALL);
		this.serverBehaviour = serverBehaviour;

		setText("Delete");
		setImageDescriptor(CloudFoundryImages.REMOVE);

		servicesHandler = new ServicesHandler(selection);
	}

	@Override
	public String getJobName() {
		return "Deleting services";
	}

	@Override
	public ICloudFoundryOperation getOperation(IProgressMonitor monitor) throws CoreException {
		return serverBehaviour.getDeleteServicesOperation(servicesHandler.getServiceNames());
	}

	@Override
	public void run() {

		boolean confirm = MessageDialog.openConfirm(getEditorPage().getSite().getShell(), "Delete Services",
				"Are you sure you want to delete " + servicesHandler.toString() + " from the services list?");
		if (confirm) {
			super.run();
		}
	}

}
