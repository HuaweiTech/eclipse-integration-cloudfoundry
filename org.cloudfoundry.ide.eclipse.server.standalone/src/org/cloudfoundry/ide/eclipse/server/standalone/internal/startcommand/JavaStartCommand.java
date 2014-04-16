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
package org.cloudfoundry.ide.eclipse.server.standalone.internal.startcommand;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class JavaStartCommand extends StartCommand {

	public static final String DEFAULT_LIB = "lib";

	public static final IPath DEFAULT_LIB_PATH = Path.EMPTY.append(DEFAULT_LIB);

	public JavaStartCommand() {
		super();
	}

	@Override
	public String getStartCommand() {
		StringWriter writer = new StringWriter();
		writer.append("java");
		if (getArgs() != null) {
			writer.append(" ");
			writer.append(getArgs());
		}
		return writer.toString();
	}

	protected String getClassPathOptionArg() {
		StringWriter options = new StringWriter();
		options.append(DEFAULT_LIB);
		options.append("/");
		options.append("*");
		options.append(":");
		options.append(".");
		return options.toString();
	}

	public String getArgs() {
		StringWriter options = new StringWriter();
		options.append("$JAVA_OPTS");
		options.append(" ");
		options.append("-cp");
		options.append(" ");
		options.append(getClassPathOptionArg());
		return options.toString();
	}

	@Override
	public StartCommandType getDefaultStartCommandType() {
		return StartCommandType.Java;
	}

	/**
	 * May be empty, but never null
	 * @return
	 */
	public List<StartCommandType> getStartCommandTypes() {
		return Arrays.asList(new StartCommandType[] { StartCommandType.Java, StartCommandType.Other });
	}

}