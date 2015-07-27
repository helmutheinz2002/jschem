/*
 * Exporter.java
 *
 * Created on July 14, 2006, 7:55 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.heinz.framework.crossplatform.utils.export;

import java.io.File;

public interface Exporter {
	ExportFormat[] getSupportedFormats();
	void export(ExportFormat exportFormat, File outputFile);
}
