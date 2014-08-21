/*******************************************************************************
 * Copyright (c) 2014 Pivotal Software, Inc. 
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
package org.cloudfoundry.ide.eclipse.server.ui.internal.wizards;

import org.cloudfoundry.ide.eclipse.server.ui.internal.IEventSource;

public class ApplicationDeploymentEvent implements IEventSource<ApplicationDeploymentEvent> {
	public static final ApplicationDeploymentEvent APP_NAME_CHANGE_EVENT = new ApplicationDeploymentEvent(
			"Application Name Changed");

	public static final ApplicationDeploymentEvent APPLICATION_URL_CHANGED = new ApplicationDeploymentEvent(
			"Application URL Changed");

	public static final ApplicationDeploymentEvent BUILD_PACK_URL = new ApplicationDeploymentEvent("Build Pack");

	public static final ApplicationDeploymentEvent MEMORY = new ApplicationDeploymentEvent("Memory");

	private final String name;

	public ApplicationDeploymentEvent(String name) {
		this.name = name;
	}

	@Override
	public ApplicationDeploymentEvent getSource() {
		return this;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getName();
	}

}
