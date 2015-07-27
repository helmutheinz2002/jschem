package org.heinz.framework.crossplatform.utils.export;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import org.heinz.framework.crossplatform.ExportProvider;
import org.heinz.framework.crossplatform.platforms.basic.AbstractFileSelection;
import org.heinz.framework.crossplatform.utils.CustomFileFilter;
import org.heinz.framework.crossplatform.utils.Translator;
import org.heinz.framework.utils.AbstractOptions;
import org.heinz.framework.utils.ViewUtils;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;

/**
 *
 * @author bwalter
 */
public class ExportHelper {
	public static final String PROPERTY_AUTHOR = "author";
	public static final String PROPERTY_SUBJECT = "subject";
	public static final String PROPERTY_HEADER = "header";
	public static final String PROPERTY_FOOTER = "footer";
	public static final String PROPERTY_SHOW_FOOTER_PAGE_NR = "showFooterPageNr";
	public static final String PROPERTY_SHOW_HEADER_PAGE_NR = "showHeaderPageNr";
	
	private static final int DEFAULT_MARGIN = 30;
	
	public static BufferedImage createEmptyImage(int h) {
		return createEmptyImage(700, h);
	}
	
	public static BufferedImage createEmptyImage(int w, int h) {
		BufferedImage img = new BufferedImage(w + 10, h, BufferedImage.TYPE_3BYTE_BGR);
		Graphics g = img.getGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, img.getWidth() + 1, img.getHeight() + 1);
		g.dispose();
		return img;
	}
	
	//----------------------------------------------------------------------
	
	public static void exportSimpleImageDocument(String author, File outputFile, ExportFormat exportFormat, BufferedImage image, Rectangle pageSize) {
		try {
			if(exportFormat.isImage()) {
				exportFormat.exportImageToFile(image, outputFile);
			} else {
				Map exportProperties = getDefaultProperties(author);
				exportProperties.remove(PROPERTY_FOOTER);
				exportProperties.remove(PROPERTY_HEADER);
				
				Document document = openBasicDocument(author, pageSize, outputFile, exportFormat, exportProperties);
				
				File tf = findTempFile();
				ImageIO.write(image, "png", tf);
				
				Image i = Image.getInstance(tf.getAbsolutePath());
				float border = (float) 2 * DEFAULT_MARGIN;
				Rectangle is = new Rectangle(pageSize.width() - border, pageSize.height() - border);
				i.scaleToFit(is.width(), is.height());
				document.add(i);
				
				document.close();
				
				tf.delete();
			}
		} catch(Exception de) {
			de.printStackTrace();
		}
		
	}
	
	public static void exportMultiImageDocument(String author, File outputFile, ExportFormat exportFormat, ExportImageProducer imageProducer, Rectangle pageSize) {
		try {
			float border = (float) 2 * DEFAULT_MARGIN;
			Rectangle is = new Rectangle(pageSize.width() - border, pageSize.height() - border);
			
			Map exportProperties = getDefaultProperties(author);
			exportProperties.remove(PROPERTY_FOOTER);
			exportProperties.remove(PROPERTY_HEADER);
			
			Document document = openBasicDocument(author, pageSize, outputFile, exportFormat, exportProperties);
			
			List tempFiles = new ArrayList();
			int pages = imageProducer.getNumPages();
			
			for(int i=0; i<pages; i++) {
				BufferedImage image = imageProducer.createExportImage(i);

				if(i > 0)
					document.newPage();
				
				File tf = findTempFile();
				ImageIO.write(image, "png", tf);
			
				Image img = Image.getInstance(tf.getAbsolutePath());
				img.scaleToFit(is.width(), is.height());
				document.add(img);
				tempFiles.add(tf);
			}
			
			document.close();
			
			for(Iterator it=tempFiles.iterator(); it.hasNext();)
				((File) it.next()).delete();
		} catch(Exception de) {
			de.printStackTrace();
		}
		
	}
	
	//----------------------------------------------------------------------
	
	private static Document openBasicDocument(String author, Rectangle pageSize, File outputFile, ExportFormat exportFormat, Map exportProperties) throws FileNotFoundException, DocumentException {
		return openBasicDocument(author, pageSize, outputFile, null, exportFormat, exportProperties, null, null, false);
	}
	
	private static Document openBasicDocument(String author, Rectangle pageSize, File outputFile, OutputStream outStream, ExportFormat exportFormat, Map exportProperties, String title, String intro, boolean showDate) throws FileNotFoundException, DocumentException {
		// Neues Dokument anlegen
		Document document = new Document(pageSize, DEFAULT_MARGIN, DEFAULT_MARGIN, DEFAULT_MARGIN, DEFAULT_MARGIN);
		
		// Damit nix schiefgeht
		if(exportProperties == null)
			exportProperties = getDefaultProperties(author);
		
		// Datei eroeffnen
		if(outStream == null)
			outStream = new FileOutputStream(outputFile);
		
		/*DocWriter writer =*/ ExportFormat.getWriter(document, exportFormat, outStream);
		
		// Autor und Subject einsetzen
		if(exportProperties.get(PROPERTY_AUTHOR) != null)
			document.addAuthor(exportProperties.get(PROPERTY_AUTHOR).toString());
		if(exportProperties.get(PROPERTY_SUBJECT) != null)
			document.addAuthor(exportProperties.get(PROPERTY_SUBJECT).toString());
		
		// Kopf- und Fusszeilen einsetzen
		HeaderFooter header = null;
		HeaderFooter footer =	null;

		String headerString = (String) exportProperties.get(PROPERTY_HEADER);
		String footerString = (String) exportProperties.get(PROPERTY_FOOTER);
		boolean showHeaderPageNr = getBool(exportProperties, PROPERTY_SHOW_HEADER_PAGE_NR);
		boolean showFooterPageNr = getBool(exportProperties, PROPERTY_SHOW_FOOTER_PAGE_NR);
			
		if(headerString != null)
			header = new HeaderFooter(new Phrase(headerString + (showHeaderPageNr?" ":"")), showHeaderPageNr);
		if(exportProperties.get(PROPERTY_FOOTER) != null)
			footer = new HeaderFooter(new Phrase(footerString + (showFooterPageNr?" ":"")), showFooterPageNr);
		
		if(header != null) {
			header.setAlignment(Element.ALIGN_CENTER);
			document.setHeader(header);
		}
		if(footer != null) {
			footer.setAlignment(Element.ALIGN_CENTER);
			document.setFooter(footer);
		}
		
		// Titel und Intro einsetzen
		document.open();
		
		Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD, Color.black);
		Font introFont = new Font(Font.HELVETICA, 14, Font.NORMAL, Color.black);
		Font dateFont = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.black);
		
		if(title != null) {
			Paragraph tp = new Paragraph(title, titleFont);
			tp.setAlignment(Paragraph.ALIGN_CENTER);
			document.add(tp);
		}
		
		if(intro != null) {
			Paragraph ip = new Paragraph(intro, introFont);
			ip.setAlignment(Paragraph.ALIGN_LEFT);
			document.add(ip);
		}
		
		if(showDate) {
			DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
			String dateTime = df.format(new Date());
			Paragraph dp = new Paragraph(dateTime, dateFont);
			dp.setAlignment(Paragraph.ALIGN_LEFT);
			document.add(dp);
		}
		
		return document;
	}
	
	public static Map getDefaultProperties(String author) {
		Map props = new HashMap();
		props.put(ExportHelper.PROPERTY_AUTHOR, author);
		props.put(ExportHelper.PROPERTY_FOOTER, Translator.translate("SHEET"));
		props.put(ExportHelper.PROPERTY_SHOW_FOOTER_PAGE_NR, new Boolean(true));
		return props;
	}
	
	private static boolean getBool(Map props, String property) {
		try {
			Boolean b = (Boolean) props.get(property);
			return b.booleanValue();
		} catch(Exception e) {
		}
		return false;
	}
	
	public static ExportFormat[] getDefaultImageExportFormats() {
		return new ExportFormat[] { ExportFormat.PDF, ExportFormat.JPG, ExportFormat.PNG, ExportFormat.GIF };
	}
	
	public static ExportFormat[] getDefaultMultiImageExportFormats() {
		return new ExportFormat[] { ExportFormat.PDF, ExportFormat.RTF };
	}
	
	public static JFileChooser getFileChooser(String dirName, String lastExtension, ExportFormat[] formats) {
		if((dirName == null) || (dirName.length() == 0))
			dirName = System.getProperty("user.home");
		
		File dir = new File(dirName);
		JFileChooser fc = new JFileChooser(dir);
		CustomFileFilter currentFilter = null;
		if((lastExtension == null) || (lastExtension.length() == 0))
			lastExtension = ExportFormat.PDF.getExtension();
		
		CustomFileFilter[] filters = getFileFilters(formats);
		
		for(int i=0; i<filters.length; i++) {
			CustomFileFilter ff = filters[i];
			ExportFormat ef = (ExportFormat) (filters[i].getFormatObject());
			String ext = ef.getExtension();
			if(ext.equals(lastExtension))
				currentFilter = ff;
			fc.addChoosableFileFilter(ff);
		}
		
		fc.setAcceptAllFileFilterUsed(false);
		fc.setFileFilter(currentFilter);
		fc.applyComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
		return fc;
	}
	
	private static CustomFileFilter[] getFileFilters(ExportFormat[] exportFormats) {
		CustomFileFilter[] ffs = new CustomFileFilter[exportFormats.length];
		
		for(int i=0; i<exportFormats.length; i++) {
			ExportFormat ef = exportFormats[i];
			CustomFileFilter ff = new CustomFileFilter(new String[] { ef.getExtension() }, Translator.translate(ef.getDescription()), ef);
			ffs[i] = ff;
		}
		
		return ffs;
	}
	
	public static String ensureExtension(String fileName, String extension) {
		if(fileName.endsWith("." + extension))
			return fileName;
		
		return fileName + "." + extension;
	}
	
	public static String export(ExportProvider provider, final Exporter exporter, String lastExtension) throws Exception {
		String dir = provider.getOptions().getStringOption(AbstractOptions.PROPERTY_LAST_EXPORT_DIR);
		
		JFileChooser fc = ExportHelper.getFileChooser(dir, lastExtension, exporter.getSupportedFormats());
		if(fc.showSaveDialog(provider.getComponent()) == JFileChooser.APPROVE_OPTION) {
			CustomFileFilter ff = (CustomFileFilter) fc.getFileFilter();
			final ExportFormat exf = (ExportFormat) ff.getFormatObject();
			File file = fc.getSelectedFile();
			final String fileName = ExportHelper.ensureExtension(file.getAbsolutePath(), exf.getExtension());
			provider.getOptions().setOption(AbstractOptions.PROPERTY_LAST_EXPORT_DIR, file.getParentFile().getAbsolutePath());
			
			final File nf = new File(fileName); 
			if(nf.exists() && !AbstractFileSelection.confirmOverwrite(provider.getComponent(), nf))
				return null;
			
			JFrame f = null;
			
			try {
				f = (JFrame) provider.getComponent();
			} catch(Exception ex) {
				f = (JFrame) SwingUtilities.getWindowAncestor(provider.getComponent());
			}
			
			final JDialog d = new JDialog(f, true);
			d.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			final Exception[] error = new Exception[1];
			
			final Thread exportThread = new Thread() {
				public void run() {
					try {
						try { Thread.sleep(1000); } catch(Exception ex) {}
						exporter.export(exf, nf);
					} catch(Exception t) {
						t.printStackTrace();
						error[0] = t;
					} finally {
						d.setVisible(false);
					}
				}
			};
			
			d.addWindowListener(new WindowAdapter() {
				public void windowOpened(WindowEvent e) {
					exportThread.start();
				}
			});
			d.getContentPane().setLayout(new GridBagLayout());
			d.getContentPane().add(new JLabel(Translator.translate("PLEASE_WAIT")), new GridBagConstraints());
			d.pack();
			d.setSize(300, 100);
			ViewUtils.centerOn(d, f);
			
			d.setVisible(true);
			
			if(error[0] != null)
				throw error[0];
			
			return exf.getExtension();
		}
		return null;
	}
	
	private static File findTempFile() {
		return findTempFile("");
	}
	
	private static File findTempFile(String extension) {
		String tempDir = System.getProperty("java.io.tmpdir");
		if(extension.length() > 0)
			extension = "." + extension;
		
		for(int i=0;; i++) {
			String fn = tempDir + File.separator + "jschem" + i + extension;
			File f = new File(fn);
			if(!f.exists())
				return f;
		}
	}
}
