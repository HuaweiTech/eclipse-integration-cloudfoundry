/*******************************************************************************
 * Copyright (c) 2013, 2014 Pivotal Software, Inc. 
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
package org.cloudfoundry.ide.eclipse.server.ui.internal;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.cloudfoundry.client.lib.domain.CloudDomain;
import org.cloudfoundry.ide.eclipse.server.core.internal.ApplicationUrlLookupService;
import org.cloudfoundry.ide.eclipse.server.core.internal.CloudApplicationURL;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * Allows users to edit or add an application URL based on a list of existing
 * URL Cloud domains
 */
public class CloudApplicationUrlPart extends UIPart {

	protected final ApplicationUrlLookupService urlLookup;

	private String appURL;

	private String selectedDomain;

	private Text subDomainText;

	private Text fullURLText;

	private Combo domainCombo;

	/**
	 * Set to the Control that originates the event, and Set to null after ALL
	 * other related controls are updated. The purpose of this is to prevent
	 * multiple notifications and URL validations, if setting value in one
	 * control also causes values in another control to be set.
	 */
	private Control modifyEventSource;

	private List<String> domains = new ArrayList<String>();

	public CloudApplicationUrlPart(ApplicationUrlLookupService urlLookup) {
		this.urlLookup = urlLookup;
	}

	public void refreshDomains() {
		domains.clear();
		List<CloudDomain> cloudDomains = urlLookup.getDomains();
		if (cloudDomains != null) {
			for (CloudDomain cloudDomain : cloudDomains) {
				domains.add(cloudDomain.getName());
			}
		}

		setDomainInUI();

	}

	protected void setDomainInUI() {
		if (domains != null && !domains.isEmpty() && domainCombo != null && !domainCombo.isDisposed()) {
			domainCombo.setItems(domains.toArray(new String[0]));
			int selectionIndex = 0;
			// If there is already a selected domain, find it and select it in
			// the UI
			if (selectedDomain != null) {
				for (int i = 0; i < domains.size(); i++) {
					if (domains.get(i).equals(selectedDomain)) {
						selectionIndex = i;
						break;
					}
				}
			}
			else {
				// Otherwise, by default, select the first domain
				selectedDomain = domains.get(selectionIndex);
			}
			domainCombo.select(selectionIndex);
		}
	}

	/**
	 * Note: Triggers validation of the URL, and also notifies any listeners if
	 * it is validate.
	 * @param fullUrl
	 */
	public void updateFullUrl(String fullUrl) {
		if (fullUrl != null && fullURLText != null && !fullURLText.isDisposed()) {
			fullURLText.setText(fullUrl);
		}
	}

	/**
	 * Note: Triggers validation of the URL, and also notifies any listeners if
	 * it is validate.
	 * 
	 * @param subDomain
	 */
	public void updateUrlSubdomain(String subDomain) {
		if (subDomainText != null && !subDomainText.isDisposed()) {
			subDomainText.setText(subDomain);
		}
	}

	/**
	 * Note that the part will adapt the given parent into a 2 column parent
	 * that does not grab vertically. This is to allow other two column controls
	 * in the same parent to have the same column widths as the controls for the
	 * URL part. Callers are responsible for creating an appropriate parent that
	 * only contains the URL part, if they do not wish for other controls
	 * outside this part to be affected.
	 */
	public Composite createPart(Composite parent) {

		Composite subDomainComp = parent;
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(subDomainComp);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(subDomainComp);

		Label label = new Label(subDomainComp, SWT.NONE);

		label.setText("Subdomain:");
		GridDataFactory.fillDefaults().grab(false, false).align(SWT.FILL, SWT.CENTER).applyTo(label);

		subDomainText = new Text(subDomainComp, SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(subDomainText);

		subDomainText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent arg0) {
				if (modifyEventSource == null) {
					modifyEventSource = subDomainText;
					setUrlFromSubdomainOrDomain();
				}
			}
		});

		label = new Label(subDomainComp, SWT.NONE);
		label.setText("Domain:");
		GridDataFactory.fillDefaults().grab(false, false).align(SWT.FILL, SWT.CENTER).applyTo(label);

		domainCombo = new Combo(subDomainComp, SWT.BORDER | SWT.READ_ONLY);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(domainCombo);
		domainCombo.setEnabled(true);
		domainCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (modifyEventSource == null) {
					modifyEventSource = domainCombo;
					int selectionIndex = domainCombo.getSelectionIndex();
					if (selectionIndex != -1 && selectionIndex < domains.size()) {
						selectedDomain = domains.get(selectionIndex);
						setUrlFromSubdomainOrDomain();
					}
				}
			}
		});

		setDomainInUI();

		label = new Label(subDomainComp, SWT.NONE);

		label.setText("Deployed URL:");
		GridDataFactory.fillDefaults().grab(false, false).align(SWT.FILL, SWT.CENTER).applyTo(label);

		fullURLText = new Text(subDomainComp, SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(fullURLText);

		fullURLText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent arg0) {
				if (modifyEventSource == null) {
					modifyEventSource = fullURLText;
					appURL = fullURLText.getText();
					validateFullURL();
				}
			}
		});

		return subDomainComp;

	}

	public String getCurrentDomain() {
		return selectedDomain;
	}

	protected void setUrlFromSubdomainOrDomain() {

		String subdomain = subDomainText != null && !subDomainText.isDisposed() ? subDomainText.getText() : "";
		StringWriter urlWriter = new StringWriter();

		if (subdomain != null) {
			urlWriter.append(subdomain);

			if (selectedDomain != null) {
				urlWriter.append('.');
			}
		}
		if (selectedDomain != null) {
			urlWriter.append(selectedDomain);
		}

		appURL = urlWriter.toString();

		if (fullURLText != null && !fullURLText.isDisposed()) {
			fullURLText.setText(appURL);
		}

		modifyEventSource = null;

		IStatus status = Status.OK_STATUS;

		try {
			getCloudApplicationUrl(appURL);
		}
		catch (CoreException ce) {
			status = ce.getStatus();
		}
		notifyURLChanged(appURL, status);

	}

	protected void validateFullURL() {
		IStatus status = Status.OK_STATUS;
		// Try to parse the url based on the known list of domains.
		// If not found, allow it anyway.
		try {
			CloudApplicationURL cloudAppURL = getCloudApplicationUrl(appURL);

			String subDomain = cloudAppURL.getSubdomain();
			selectedDomain = cloudAppURL.getDomain();
			subDomainText.setText(subDomain);
			setDomainInUI();
		}
		catch (CoreException e) {
			// Otherwise, perform simple validation
			status = urlLookup.simpleValidation(appURL);
		}

		modifyEventSource = null;
		notifyURLChanged(appURL, status);
	}

	protected CloudApplicationURL getCloudApplicationUrl(String appURL) throws CoreException {
		return urlLookup.getCloudApplicationURL(appURL);
	}

	protected void notifyURLChanged(String appURL, IStatus status) {
		notifyStatusChange(appURL, status);
	}
}
