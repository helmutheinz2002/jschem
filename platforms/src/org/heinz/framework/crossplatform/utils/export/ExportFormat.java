/*
 * ExportFormat.java
 *
 * Created on April 19, 2006, 12:32 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.heinz.framework.crossplatform.utils.export;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.heinz.framework.crossplatform.utils.gif.Gif89Encoder;

import com.lowagie.text.DocWriter;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.html.HtmlWriter;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.rtf.RtfWriter2;

/**
 *
 * @author bwalter
 */
public class ExportFormat {

	private static final List ALL_FORMATS = new ArrayList();

	public static final ExportFormat PDF = new ExportFormat("pdf", "EXPORT_FORMAT_PDF", false);

	public static final ExportFormat RTF = new ExportFormat("rtf", "EXPORT_FORMAT_RTF", false);

	public static final ExportFormat GIF = new ExportFormat("gif", "EXPORT_FORMAT_GIF", true);

	public static final ExportFormat JPG = new ExportFormat("jpg", "EXPORT_FORMAT_JPG", true);

	public static final ExportFormat PNG = new ExportFormat("png", "EXPORT_FORMAT_PNG", true);

	public static final ExportFormat BMP = new ExportFormat("bmp", "EXPORT_FORMAT_BMP", true);

	public static final String IMAGE_DIR_SUFFIX = "_img";

	private final String extension;

	private final String description;

	private final boolean image;

	@SuppressWarnings("LeakingThisInConstructor")
	protected ExportFormat(String extension, String description, boolean image) {
		this.extension = extension;
		this.description = description;
		this.image = image;

		ALL_FORMATS.add(this);
	}

	public String getExtension() {
		return extension;
	}

	public String getDescription() {
		return description;
	}

	public boolean isImage() {
		return image;
	}

	public static ExportFormat getExportFormatByExtension(String extension) {
		for(Iterator it = ALL_FORMATS.iterator(); it.hasNext();) {
			ExportFormat exf = (ExportFormat) it.next();
			if(exf.getExtension().equals(extension)) {
				return exf;
			}
		}

		return null;
	}

	public void exportImageToFile(BufferedImage img, File outputFile) throws IOException {
		if(!image) {
			throw new UnsupportedOperationException("Not an image export format");
		}

		if(this == GIF) {
			Gif89Encoder encoder = new Gif89Encoder(img);
			try (FileOutputStream os = new FileOutputStream(outputFile)) {
				encoder.encode(os);
			}
		} else {
			ImageIO.write(img, getExtension(), outputFile);
		}
	}

	public static DocWriter getWriter(Document document, ExportFormat format, OutputStream ostream) throws DocumentException {
		if(format == PDF) {
			return PdfWriter.getInstance(document, ostream);
		}
		if(format == RTF) {
			return RtfWriter2.getInstance(document, ostream);
		}
		if(format.isImage()) {
			throw new IllegalArgumentException("No DocWriter for image formats");
		}

		return null;
	}

	public static String getImageDirName(File outputFile) {
		String baseName = outputFile.getAbsolutePath();
		baseName = baseName.substring(0, baseName.lastIndexOf('.'));
		baseName += IMAGE_DIR_SUFFIX;
		File f = new File(baseName);
		String s = f.getName();
		return s;
	}

	public static void setImagePath(DocWriter writer, String imgPath) {
		if(writer instanceof HtmlWriter) {
			((HtmlWriter) writer).setImagepath(imgPath);
		}
	}

	public static File ensureImageDir(File outputFile) {
		String dir = outputFile.getParentFile().getAbsolutePath();
		dir = dir + File.separator + ExportFormat.getImageDirName(outputFile);
		File picDir = new File(dir);
		picDir.mkdir();
		return picDir;
	}

}
