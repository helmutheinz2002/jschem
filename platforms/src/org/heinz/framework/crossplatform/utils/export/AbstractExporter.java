/*
 * AbstractExporter.java
 *
 * Created on July 24, 2006, 2:44 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.heinz.framework.crossplatform.utils.export;

/**
 *
 * @author bwalter
 */
public abstract class AbstractExporter implements Exporter {

	private final ExportFormat[] supportedExportFormats;

	/** Creates a new instance of AbstractExporter
	 * @param supportedExportFormats */
	public AbstractExporter(ExportFormat[] supportedExportFormats) {
		this.supportedExportFormats = supportedExportFormats;
	}

	@Override
	public ExportFormat[] getSupportedFormats() {
		return supportedExportFormats;
	}

}
