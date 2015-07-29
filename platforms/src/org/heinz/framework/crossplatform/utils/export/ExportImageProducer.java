
package org.heinz.framework.crossplatform.utils.export;

import java.awt.image.BufferedImage;

public interface ExportImageProducer {

	int getNumPages();

	BufferedImage createExportImage(int page);

}
