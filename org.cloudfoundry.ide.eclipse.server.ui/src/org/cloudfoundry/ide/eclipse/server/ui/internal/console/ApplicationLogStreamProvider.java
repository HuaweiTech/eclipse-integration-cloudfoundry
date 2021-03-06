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
package org.cloudfoundry.ide.eclipse.server.ui.internal.console;

import org.cloudfoundry.ide.eclipse.server.core.internal.log.LogContentType;

public class ApplicationLogStreamProvider extends ConsoleStreamProvider {

	private static final LogContentType[] SUPPORTED = new LogContentType[] { StandardLogContentType.APPLICATION_LOG,
			StandardLogContentType.APPLICATION_LOG_STD_OUT, StandardLogContentType.APPLICATION_LOG_STS_ERROR,
			StandardLogContentType.APPLICATION_LOG_UNKNOWN };

	@Override
	public ConsoleStream getStream(LogContentType type) {

		for (LogContentType tp : SUPPORTED) {
			if (tp.equals(type)) {
				return new ApplicationLogConsoleStream();
			}
		}

		return null;
	}

	@Override
	public LogContentType[] getSupportedTypes() {
		return SUPPORTED;
	}

}
