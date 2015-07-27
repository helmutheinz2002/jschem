package org.heinz.eda.schem;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.heinz.eda.schem.model.Library;
import org.heinz.eda.schem.model.SchemOptions;
import org.heinz.eda.schem.model.Schematics;
import org.heinz.eda.schem.model.Sheet;
import org.heinz.eda.schem.model.SheetSize;
import org.heinz.eda.schem.model.components.AbstractComponent;
import org.heinz.eda.schem.model.expresssch.ExpressImport;
import org.heinz.eda.schem.model.xml.XmlSchematicsReader;
import org.heinz.eda.schem.model.xml.XmlSchematicsWriter;
import org.heinz.eda.schem.ui.SchemActions;
import org.heinz.eda.schem.ui.SchemClipBoard;
import org.heinz.eda.schem.ui.SchemEditToolbar;
import org.heinz.eda.schem.ui.SchemMenuBar;
import org.heinz.eda.schem.ui.SchemStatusBar;
import org.heinz.eda.schem.ui.SchemTabbook;
import org.heinz.eda.schem.ui.SchemToolbar;
import org.heinz.eda.schem.ui.SheetPanel;
import org.heinz.eda.schem.ui.dialog.ComponentFileChooser;
import org.heinz.eda.schem.ui.dialog.ComponentPropertyDialog;
import org.heinz.eda.schem.ui.dialog.SheetSizePanel;
import org.heinz.eda.schem.ui.dialog.library.LibraryUpdatePanel;
import org.heinz.eda.schem.ui.export.SchemExportFormat;
import org.heinz.eda.schem.ui.options.OptionsEditor;
import org.heinz.eda.schem.ui.tools.LibraryTool;
import org.heinz.eda.schem.ui.undo.sheet.UndoAddSheet;
import org.heinz.eda.schem.ui.undo.sheet.UndoDeleteSheet;
import org.heinz.eda.schem.ui.undo.sheet.UndoMoveSheet;
import org.heinz.eda.schem.ui.undo.sheet.UndoRenameSheet;
import org.heinz.eda.schem.util.ComponentFileFilter;
import org.heinz.eda.schem.util.SchemFileFilter;
import org.heinz.eda.schem.util.SnapGridHelper;
import org.heinz.framework.crossplatform.Application;
import org.heinz.framework.crossplatform.ApplicationListener;
import org.heinz.framework.crossplatform.CrossPlatform;
import org.heinz.framework.crossplatform.Document;
import org.heinz.framework.crossplatform.DocumentAdapter;
import org.heinz.framework.crossplatform.EditTool;
import org.heinz.framework.crossplatform.EditToolBar;
import org.heinz.framework.crossplatform.EditToolBarFactory;
import org.heinz.framework.crossplatform.EditToolBarListener;
import org.heinz.framework.crossplatform.ExportProvider;
import org.heinz.framework.crossplatform.MenuBarFactory;
import org.heinz.framework.crossplatform.Platform;
import org.heinz.framework.crossplatform.StatusBar;
import org.heinz.framework.crossplatform.StatusBarFactory;
import org.heinz.framework.crossplatform.ToolBar;
import org.heinz.framework.crossplatform.ToolBarFactory;
import org.heinz.framework.crossplatform.dialog.StandardDialog;
import org.heinz.framework.crossplatform.platforms.basic.AbstractFileSelection;
import org.heinz.framework.crossplatform.platforms.basic.ApplicationActions;
import org.heinz.framework.crossplatform.utils.ApplicationUndoManager;
import org.heinz.framework.crossplatform.utils.DefaultMessages;
import org.heinz.framework.crossplatform.utils.DocumentCloseVetoException;
import org.heinz.framework.crossplatform.utils.DocumentProperty;
import org.heinz.framework.crossplatform.utils.IconLoader;
import org.heinz.framework.crossplatform.utils.Translator;
import org.heinz.framework.crossplatform.utils.export.ExportFormat;
import org.heinz.framework.crossplatform.utils.export.ExportHelper;
import org.heinz.framework.crossplatform.utils.export.Exporter;
import org.heinz.framework.utils.AboutHelper;
import org.heinz.framework.utils.AbstractOptions;
import org.heinz.framework.utils.DefaultFileExtensionEnsurer;
import org.heinz.framework.utils.FileExtensionEnsurer;
import org.heinz.framework.utils.cmdline.CommandLineParser;

import com.lowagie.text.Rectangle;

public class SchemApplication implements ApplicationListener, ActionListener, EditToolBarListener, SchemDocumentProperty {
	private static final float ITEXT_UNIT = 2540f / 72.0f;
	private static final int PASTE_OFFSET = 10;

	private Application application;
	private Library library;
	private String workDir;
	private List initialFiles = new ArrayList();
	private SchemStatusBar statusBar;
	private Point pasteOffset;
	
	public SchemApplication(String[] args) {
		this.application = CrossPlatform.getPlatform().getApplication();
		
		application.setTitle(SchemConstants.PROGRAM_NAME);
		application.setIconImage(IconLoader.instance().loadIcon("menu/icon.png").getImage());
		
		workDir = System.getProperty("user.home") + File.separator + "." + SchemConstants.PROGRAM_NAME.toLowerCase();
		CommandLineParser.instance().parseArguments(args);
		for(Iterator it=CommandLineParser.instance().getArguments().iterator(); it.hasNext();)
			initialFiles.add(new File((String) it.next()));
		
		Translator.instance().addBundle("JSchem");
		new SchemClipBoard();
		new SchemOptions(workDir);
		SchemActions.instance().addActionListener(this);
		ApplicationActions.instance().addActionListener(this);
		
		application.addApplicationListener(this);
		application.setMenuBarFactory(new MenuBarFactory() {
			public JMenuBar createMenuBar() {
				return new SchemMenuBar(SchemApplication.this);
			}
		});
		application.setToolBarFactory(new ToolBarFactory() {
			public ToolBar createToolBar() {
				return new SchemToolbar();
			}
		});
		application.setEditToolBarFactory(new EditToolBarFactory() {
			public EditToolBar createEditToolBar() {
				SchemEditToolbar editToolbar = new SchemEditToolbar();
				editToolbar.addEditToolBarListener(SchemApplication.this);
				return editToolbar;
			}
		});
		application.setStatusBarFactory(new StatusBarFactory() {
			public StatusBar createStatusBar() {
				return statusBar = new SchemStatusBar();
			}
		});
		
		application.start();
	}

	public void about() {
		application.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		List libraries = new ArrayList();
		libraries.add(new AboutHelper.LibraryInfo("JGoodies Looks", "www.jgoodies.com"));
		libraries.add(new AboutHelper.LibraryInfo("iText", "www.lowagie.com/iText"));
		libraries.add(new AboutHelper.LibraryInfo("Windows registry dll", "www.trustice.com/java/jnireg"));
		libraries.add(new AboutHelper.LibraryInfo("ExpressLib", "sourceforge.net/projects/expresslib"));
		libraries.add(new AboutHelper.LibraryInfo("Crystal SVG Icons", "www.everaldo.com"));
		String text = AboutHelper.getAboutBoxText(SchemConstants.PROGRAM_DESCRIPTION, SchemConstants.DATE, "ABOUT_COPYRIGHT", "ABOUT_GPL_LICENSE", libraries);
		application.setCursor(Cursor.getDefaultCursor());
		JOptionPane.showMessageDialog(application.getOptionPaneOwner(), text, Translator.translate("ABOUT_TITLE"), JOptionPane.INFORMATION_MESSAGE);
	}

	public void updateVersion() {
		updateLibrary(true);
	}
	
	public void updateLibrary(boolean silent) {
		List updateActions = library.checkLibrary();
		
		if(!silent && !Library.hasPendingUpdates(updateActions)) {
			// Nothing to do
			JOptionPane.showMessageDialog(application.getOptionPaneOwner(), Translator.translate("LIBRARY_UP_TO_DATE"), Translator.translate("LIBRARY_UPDATE"), JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		if(Library.isAllNew(updateActions))
			// All files are new, so there is no need to bother user
			library.updateLibrary(updateActions);
		else {
			// User must decide what to do
			while(Library.hasPendingUpdates(updateActions)) {
				LibraryUpdatePanel updatePanel = new LibraryUpdatePanel();
				updatePanel.display(updateActions);
				Dimension d = application.getSize();
				int res = StandardDialog.showDialog(application.getDialogOwner(null), updatePanel, new Dimension(d.width * 2 / 3, d.height * 2 / 3));
				
				if(res == StandardDialog.OK_PRESSED)
					library.updateLibrary(updateActions);
				else
					break;
			}
		}
	}

	public void applicationStarted() {
		library = new Library(workDir);
		
		registerFileType();
		
		if(!SchemOptions.instance().getStringOption(SchemOptions.PROPERTY_LAST_PROGRAM_VERSION).equals(SchemConstants.PROGRAM_VERSION)) {
			updateVersion();
			SchemOptions.instance().setOption(SchemOptions.PROPERTY_LAST_PROGRAM_VERSION, SchemConstants.PROGRAM_VERSION);
		}
		
		application.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
       	for(Iterator it=initialFiles.iterator(); it.hasNext();)
       		loadFile((File) it.next());
       	
       	if(application.getDocuments().size() == 0)
       		newFile(null, null);
       	
		application.setCursor(Cursor.getDefaultCursor());
	}

	private void registerFileType() {
		Platform platform = CrossPlatform.getPlatform(); 
		try {
			String path = System.getProperty("executable.path");
			String name = System.getProperty("executable.name");
			if((path != null) && (name != null)) {
				String exePath = path + name; 
				platform.registerFileType("jsch", SchemConstants.PROGRAM_NAME + "." + SchemConstants.PROGRAM_VERSION, exePath);
			}
		} catch(Exception ex) {
			JOptionPane.showMessageDialog(platform.getApplication().getOptionPaneOwner(), Translator.translate("CANNOT_REGISTER_FILETYPE"), Translator.translate("ERROR"), JOptionPane.ERROR_MESSAGE);
		}
	}

	private void setTitle(Document document) {
		File file = (File) document.getProperty(DOC_PROPERTY_FILE);
		Schematics schematics = ((SchemTabbook) document.getDocumentPane()).getSchematics();
		String title = (file == null) ? Translator.translate("NEW_SCHEMATIC") : file.getAbsolutePath();
		if((schematics != null) && schematics.isDirty())
			title += " *";
		document.setTitle(title);
	}

	private Document newFile(Schematics schematics, File file) {
		final Document document = application.createDocument();
		document.setIconImage(IconLoader.instance().loadIcon("menu/icon.png").getImage());

		if(schematics == null)
			schematics = new Schematics(Translator.translate("SHEET"));
		
		document.setProperty(DOC_PROPERTY_FILE, file);
		
		schematics.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				setTitle(document);
			}
		});
		
		SchemTabbook schemTabbook = new SchemTabbook(schematics);
		schemTabbook.addPropertyChangeListener(statusBar);
		document.addStateInfoProvider(schemTabbook);
		
		document.setDocumentPane(schemTabbook);
		setTitle(document);
		return document;
	}

	public boolean saveAs(Document document) {
		File f = selectFile(true);
		
		if(f != null) {
			save(document, f);
			SchemOptions.instance().addToRecentFiles(f);
			setTitle(document);
			return true;
		}
		
		return false;
	}

	public boolean save(Document document) {
		File file = (File) document.getProperty(DocumentProperty.DOC_PROPERTY_FILE);

		if(file != null)
			save(document, file);
		else
			return saveAs(document);
		
		return true;
	}
	
	public void save(Document document, File file) {
		SchemTabbook tb = (SchemTabbook) document.getDocumentPane();
		Schematics schematics = tb.getSchematics();
		
		try {
			String s = XmlSchematicsWriter.toXml(schematics);
			FileOutputStream fos = new FileOutputStream(file);
			ZipOutputStream zos = new ZipOutputStream(fos);
			ZipEntry entry = new ZipEntry("schematics.xml");
			zos.putNextEntry(entry);
			byte[] bytes = s.getBytes("UTF-8");
			zos.write(bytes, 0, bytes.length);
			zos.closeEntry();
			zos.close();
			fos.close();
			schematics.setDirty(false);
			document.setProperty(DocumentProperty.DOC_PROPERTY_FILE, file);
		} catch(Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(document.getContainer(), file.getAbsolutePath() + "\n\n" + Translator.translate("SAVE_ERROR"), Translator.translate("ERROR"), JOptionPane.ERROR_MESSAGE);
		}
	}

	public void loadFile() {
		File f = selectFile(false);
		if(f != null)
			loadFile(f);
	}

	private void loadFile(File f) {
		CrossPlatform.getPlatform().getApplication().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		String message = null;
		try {
			Schematics newSchematics = null;
			if(f.getAbsolutePath().endsWith(SchemFileFilter.SCHEM_EXTENSION)) {
				XmlSchematicsReader r = new XmlSchematicsReader();
				SAXParser p = SAXParserFactory.newInstance().newSAXParser();
				ZipFile zipFile = new ZipFile(f);
				ZipEntry entry = (ZipEntry) zipFile.entries().nextElement();
				p.parse(zipFile.getInputStream(entry), r.getHandler());
				newSchematics = r.getSchematics();
			} else {
				newSchematics = ExpressImport.importFile(f);
			}
			
			newSchematics.setDirty(false);
			newFile(newSchematics, f);
			SchemOptions.instance().addToRecentFiles(f);
			SnapGridHelper.adjustSnapGrid(newSchematics, application.getOptionPaneOwner());
		} catch(FileNotFoundException fex) {
			message = "FILE_NOT_FOUND";
		} catch(Exception cex) {
			cex.printStackTrace();
			message = "FILE_INVALID_CONTENTS";
		} finally {
			CrossPlatform.getPlatform().getApplication().setCursor(Cursor.getDefaultCursor());
		}
		
		if(message != null) {
			Component c = CrossPlatform.getPlatform().getApplication().getOptionPaneOwner();
			JOptionPane.showMessageDialog(c, f.getAbsolutePath() + "\n\n" + Translator.translate(message), Translator.translate("ERROR"), JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void loadFromLib(Document document) {
		String dir = SchemOptions.instance().getStringOption(SchemOptions.PROPERTY_LAST_LIB_DIR);
		if((dir == null) || (dir.length() == 0))
			dir = library.getLibraryDir();
		ComponentFileChooser fc = new ComponentFileChooser(dir, false);
		if(fc.showOpenDialog(document.getContainer()) == ComponentFileChooser.APPROVE_OPTION) {
			AbstractComponent c = fc.getSelectedComponent();
			if(c != null) {
				SchemOptions.instance().setOption(SchemOptions.PROPERTY_LAST_LIB_DIR, fc.getCurrentDirectory().getAbsolutePath());
				SchemTabbook schemTabbook = (SchemTabbook) document.getDocumentPane();
				SheetPanel sp = schemTabbook.getCurrentEditor(); 
				sp.clearSelection();
				Sheet s = schemTabbook.getCurrentSheet();
				Point cp = sp.getCenter();
				cp = sp.constrainPoint(cp.x, cp.y, false);
				c.setPosition(cp.x, cp.y);
				s.addComponent(c);
				sp.addToSelection(c);
				
				EditToolBar editToolBar = document.getEditToolBar();
				((LibraryTool) editToolBar.getTool(LibraryTool.class)).addToRecentComponents(fc.getSelectedFile());
//				ApplicationUndoManager.instance().getUndoManager(document).addEdit(new UndoNew(s, c));
			}
		}
	}

	private void saveToLib(Document document) {
		String dir = SchemOptions.instance().getStringOption(SchemOptions.PROPERTY_LAST_LIB_DIR);
		if((dir == null) || (dir.length() == 0))
			dir = library.getLibraryDir();
		ComponentFileChooser fc = new ComponentFileChooser(dir, true);
		if(fc.showSaveDialog(document.getContainer()) == ComponentFileChooser.APPROVE_OPTION) {
			File f = fc.getSelectedFile();
			
			String fn = f.getAbsolutePath();
			if(!ComponentFileFilter.instance(true).hasExtension(fn))
				fn += "." + ComponentFileFilter.COMPONENT_EXTENSION_JSCHEM;
			
			f = new File(fn);
			if(f.exists() && !AbstractFileSelection.confirmOverwrite(document.getContainer(), f, "COMPONENT_EXISTS_CONFIRMATION", "COMPONENT_EXISTS_TITLE"))
				return;

			SchemOptions.instance().setOption(SchemOptions.PROPERTY_LAST_LIB_DIR, fc.getCurrentDirectory().getAbsolutePath());
			SchemTabbook schemTabbook = (SchemTabbook) document.getDocumentPane();
			AbstractComponent c = (AbstractComponent) schemTabbook.getCurrentEditor().getSelection().get(0);
			try {
				c = c.duplicate();
				c.setPosition(0, 0);
				Library.saveComponent(c, f);
			} catch(Exception ex) {
				JOptionPane.showMessageDialog(document.getContainer(), Translator.translate("COMPONENT_SAVE_ERROR") + "\n\n" + ex.getClass() + " " + ex.getMessage(), Translator.translate("ERROR"), JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private SchemTabbook getCurrentTabbook() {
		Document document = application.getActiveDocument();
		SchemTabbook stb = (SchemTabbook) document.getDocumentPane();
		return stb;
	}
	
	private File selectFile(boolean save) {
		String title = Translator.translate(save ? "SAVE_SCHEMATICS" : "OPEN_SCHEMATICS");
		FileExtensionEnsurer extEnsurer = (save ? new DefaultFileExtensionEnsurer(SchemFileFilter.SCHEM_EXTENSION) : null);
		File f = CrossPlatform.getPlatform().getApplication().selectFile(null, SchemFileFilter.instance(save), extEnsurer, save, title);
    	return f;
	}

	public void openFile(String filename) {
		File f = new File(filename);
		loadFile(f);
	}

	public void preferences() {
		StandardDialog.showDialog(application.getDialogOwner(null), new OptionsEditor(), new Dimension(400, 300));
	}

	public void quit() {
		int dirty = 0;
		for(Iterator it=application.getDocuments().iterator(); it.hasNext();) {
			Document d = (Document) it.next();
			SchemTabbook tb = (SchemTabbook) d.getDocumentPane();
			Schematics s = tb.getSchematics();
			if(s.isDirty())
				dirty++;
		}

		int res = DefaultMessages.OPTION_REVIEW;
		if(dirty > 0) {
			if(dirty > 1)
				res = DefaultMessages.askQuit(application.getOptionPaneOwner());
			
			if(res == DefaultMessages.OPTION_CANCEL)
				return;
			if(res == DefaultMessages.OPTION_REVIEW) {
				try {
					application.closeAllDocuments();
				} catch (DocumentCloseVetoException e) {
					return;
				}
			}
		}

		System.exit(0);
	}

	private void export(final Document document, Exporter exporter, String extOptionKey) {
		ExportProvider exportProvider = new ExportProvider() {
			public Component getComponent() {
				return document.getContainer();
			}

			public AbstractOptions getOptions() {
				return SchemOptions.instance();
			}
		};
		
		try {
			String ext = ExportHelper.export(exportProvider, exporter, SchemOptions.instance().getStringOption(extOptionKey));
			if(ext != null)
				SchemOptions.instance().setOption(extOptionKey, ext);
		} catch(Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(getCurrentTabbook(), Translator.translate("FILE_EXPORT_FAILED"), Translator.translate("ERROR"), JOptionPane.ERROR_MESSAGE);
		}
	}

	private void exportSheet(final Document document) {
		Exporter exporter = new Exporter() {
			public void export(ExportFormat exportFormat, File outputFile) {
				SchemTabbook schemTabbook = (SchemTabbook) document.getDocumentPane();
				Sheet sheet = schemTabbook.getCurrentEditor().getSheet();
				
				if(exportFormat instanceof SchemExportFormat) {
					SchemExportFormat sef = (SchemExportFormat) exportFormat;
					try {
						sef.exportSheet(sheet, outputFile);
					} catch (IOException e) {
						e.printStackTrace();
						throw new IllegalArgumentException(e.getMessage());
					}
					return;
				}
				SheetSize ss = sheet.getSize();
				Rectangle size = new Rectangle((float) ss.width / ITEXT_UNIT, (float) ss.height / ITEXT_UNIT);
				ExportHelper.exportSimpleImageDocument(SchemConstants.PROGRAM_DESCRIPTION, outputFile, exportFormat, sheet.getImage(), size);
			}

			public ExportFormat[] getSupportedFormats() {
				return SchemExportFormat.getDefaultSheetExportFormats();
			}
		};
		
		export(document, exporter, SchemOptions.PROPERTY_LAST_SHEET_EXPORT_EXTENSION);
	}
	
	private void exportSchematics(final Document document) {
		Exporter exporter = new Exporter() {
			public void export(ExportFormat exportFormat, File outputFile) {
				SchemTabbook schemTabbook = (SchemTabbook) document.getDocumentPane();

				if(exportFormat instanceof SchemExportFormat) {
					SchemExportFormat sef = (SchemExportFormat) exportFormat;
					try {
						sef.exportSchematic(schemTabbook.getSchematics(), outputFile);
					} catch (IOException e) {
						e.printStackTrace();
						throw new IllegalArgumentException(e.getMessage());
					}
					return;
				}

				Sheet sheet = schemTabbook.getCurrentEditor().getSheet();
				SheetSize ss = sheet.getSize();
				Rectangle size = new Rectangle((float) ss.width / ITEXT_UNIT, (float) ss.height / ITEXT_UNIT);
				ExportHelper.exportMultiImageDocument(SchemConstants.PROGRAM_DESCRIPTION, outputFile, exportFormat, schemTabbook, size);
			}

			public ExportFormat[] getSupportedFormats() {
				return SchemExportFormat.getDefaultSchematicsExportFormats();
			}
		};
		
		export(document, exporter, SchemOptions.PROPERTY_LAST_SCHEMATICS_EXPORT_EXTENSION);
	}
	public void actionPerformed(ActionEvent e) {
		SchemActions sa = SchemActions.instance();
		ApplicationActions aa = ApplicationActions.instance();
		
		if(e.getSource() == aa.newItem)
			newFile(null, null);
		else if(e.getSource() == aa.openItem)
			loadFile();
		else if(e.getSource() == aa.zoomInItem)
			getCurrentTabbook().getCurrentEditor().zoomIn();
		else if(e.getSource() == aa.zoomOutItem)
			getCurrentTabbook().getCurrentEditor().zoomOut();
		else if(e.getSource() == aa.zoomFitItem)
			getCurrentTabbook().getCurrentEditor().zoomToFit();
		else if(e.getSource() instanceof JMenuBar)
			openFile((String) e.getActionCommand());
		else if(e.getSource() == aa.saveItem)
			save(application.getActiveDocument());
		else if(e.getSource() == aa.saveAsItem)
			saveAs(application.getActiveDocument());
		else if(e.getSource() == sa.exportSchematicsItem)
			exportSchematics(application.getActiveDocument());
		else if(e.getSource() == sa.exportSheetItem)
			exportSheet(application.getActiveDocument());
		else if(e.getSource() == sa.libraryItem)
			loadFromLib(application.getActiveDocument());
		else if(e.getSource() == sa.saveComponentItem)
			saveToLib(application.getActiveDocument());
		else if (e.getSource() == sa.updateLibraryItem)
			updateLibrary(false);
		else if (e.getSource() == aa.propertiesItem) {
			Document document = application.getActiveDocument();
			SheetPanel sp = ((SchemTabbook) document.getDocumentPane()).getCurrentEditor();
			AbstractComponent c = (AbstractComponent) sp.getSelection().get(0);
			ComponentPropertyDialog.openDialog(application.getDialogOwner(document), c);
		} else if (e.getSource() == sa.groupComponentItem) {
			SheetPanel sheetPanel = getCurrentTabbook().getCurrentEditor();
			AbstractComponent group = sheetPanel.getSheet().groupComponent(sheetPanel.getSelection());
			sheetPanel.addToSelection(group);
		} else if (e.getSource() == sa.groupSymbolItem) {
			SheetPanel sheetPanel = getCurrentTabbook().getCurrentEditor();
			AbstractComponent group = sheetPanel.getSheet().groupSymbol(sheetPanel.getSelection());
			sheetPanel.addToSelection(group);
		} else if (e.getSource() == sa.ungroupItem) {
			SheetPanel sheetPanel = getCurrentTabbook().getCurrentEditor();
			Sheet sheet = sheetPanel.getSheet();
			List selection = sheetPanel.getSelection();
			List comps = sheet.ungroup((org.heinz.eda.schem.model.components.Component) selection.get(0));
			sheetPanel.addToSelection(comps);
		} else if (e.getSource() == sa.newSheetItem) {
			Schematics schematics = getCurrentTabbook().getSchematics();
			Sheet newSheet = schematics.addSheet(Translator.translate("SHEET"), true);
			ApplicationUndoManager.instance().getCurrentUndoManager().addEdit(new UndoAddSheet(schematics, newSheet));
		} else if (e.getSource() == sa.sheetLeftItem) {
			Sheet sheet = getCurrentTabbook().getCurrentSheet();
			Schematics schematics = getCurrentTabbook().getSchematics();
			schematics.moveSheet(sheet, true);
			ApplicationUndoManager.instance().getCurrentUndoManager().addEdit(new UndoMoveSheet(schematics, sheet, true));
		} else if (e.getSource() == sa.sheetRightItem) {
			Sheet sheet = getCurrentTabbook().getCurrentSheet();
			Schematics schematics = getCurrentTabbook().getSchematics();
			schematics.moveSheet(sheet, false);
			ApplicationUndoManager.instance().getCurrentUndoManager().addEdit(new UndoMoveSheet(schematics, sheet, false));
		} else if (e.getSource() == sa.sheetSizeItem) {
			Sheet sheet = getCurrentTabbook().getCurrentSheet();
			Document document = application.getActiveDocument();
			StandardDialog.showDialog(application.getDialogOwner(document), new SheetSizePanel(sheet), new Dimension(300, 200));
		} else if (e.getSource() == sa.renameSheetItem) {
			Sheet sheet = getCurrentTabbook().getCurrentSheet();
			String name = sheet.getTitle();
			name = (String) JOptionPane.showInputDialog(application.getActiveDocument().getContainer(), Translator.translate("RENAME_SHEET"), Translator.translate("RENAME_SHEET_TITLE"),
							JOptionPane.QUESTION_MESSAGE, null, null, name);
			if (name != null) {
				String oldTitle = sheet.getTitle();
				sheet.setTitle(name);
				ApplicationUndoManager.instance().getCurrentUndoManager().addEdit(new UndoRenameSheet(sheet, oldTitle));
			}
		} else if (e.getSource() == sa.deleteSheetItem) {
			Sheet sheet = getCurrentTabbook().getCurrentSheet();
			int r = JOptionPane.showConfirmDialog(application.getActiveDocument().getContainer(), Translator.translate("DELETE_SHEET"), Translator.translate("DELETE_SHEET_TITLE"),
					JOptionPane.YES_NO_OPTION);
			if (r == JOptionPane.YES_OPTION) {
				Schematics schematics = getCurrentTabbook().getSchematics();
				int idx = schematics.removeSheet(sheet, false);
				ApplicationUndoManager.instance().getCurrentUndoManager().addEdit(new UndoDeleteSheet(schematics, sheet, idx));
			}
		} else if (e.getSource() == aa.deleteItem) {
			SheetPanel sp = getCurrentTabbook().getCurrentEditor(); 
			//List sel = sp.getSelection();
			//ApplicationUndoManager.instance().getCurrentUndoManager().addEdit(new UndoDelete(sp.getSheet(), sel));
			sp.removeSelection();
		} else if (e.getSource() == aa.cutItem) {
			copy();
			getCurrentTabbook().getCurrentEditor().removeSelection();
		} else if (e.getSource() == aa.copyItem) {
			copy();
		} else if (e.getSource() == aa.pasteItem) {
			paste();
		} else if (e.getSource() == aa.undoItem) {
			ApplicationUndoManager.instance().getCurrentUndoManager().undo();
		} else if (e.getSource() == aa.redoItem) {
			ApplicationUndoManager.instance().getCurrentUndoManager().redo();
		} else if (e.getSource() == aa.selectAllItem) {
			getCurrentTabbook().getCurrentEditor().selectAll();
		} else if (e.getSource() == aa.deselectAllItem) {
			getCurrentTabbook().getCurrentEditor().clearSelection();
		} else if (e.getSource() == sa.toFrontItem) {
			getCurrentTabbook().getCurrentEditor().selectionToFront();
		} else if (e.getSource() == sa.toBackItem) {
			getCurrentTabbook().getCurrentEditor().selectionToBack();
		} else if (e.getSource() == sa.autoNumberItem) {
			int pageNr = getCurrentTabbook().getSelectedIndex() + 1;
			int partsPerSheet = SchemOptions.instance().getIntOption(SchemOptions.PROPERTY_AUTONUMBER_PAGE_OFFSET);
			getCurrentTabbook().getCurrentEditor().getSheet().autoAssignIds(pageNr * partsPerSheet + 1);
			//getCurrentTabbook().getSchematics().setAuthorInfo();
		}
	}

	private void copy() {
		Document document = application.getActiveDocument();
		pasteOffset = new Point();
		SchemTabbook schemTabbook = (SchemTabbook) document.getDocumentPane();

		List components = new ArrayList();
		for(Iterator it=schemTabbook.getCurrentEditor().getSelection().iterator(); it.hasNext();) {
			AbstractComponent c = (AbstractComponent) it.next();
			c = c.duplicate();
			components.add(c);
		}
		SchemClipBoard.instance().post(components);
	}

	private void paste() {
		Document document = application.getActiveDocument();
		SchemTabbook schemTabbook = (SchemTabbook) document.getDocumentPane();
		
		SheetPanel p = schemTabbook.getCurrentEditor();
		p.clearSelection();
		Sheet s = schemTabbook.getCurrentSheet();
		
		Point off = p.constrainScreenPoint(PASTE_OFFSET, PASTE_OFFSET, false);
		pasteOffset.translate(off.x, off.y);
		
		List newObjects = new ArrayList();
		for(Iterator it=SchemClipBoard.instance().iterator(); it.hasNext();) {
			AbstractComponent c = (AbstractComponent) it.next();
			c = c.duplicate();
			newObjects.add(c);
			Point pos = c.getPosition();
			c.setPosition(pos.x + pasteOffset.x, pos.y + pasteOffset.y);
			s.addComponent(c, true);
			p.addToSelection(c);
		}
		
//		ApplicationUndoManager.instance().getCurrentUndoManager().addEdit(new UndoNew(s, newObjects));
	}

	public void documentCreated(final Document document) {
		document.addDocumentListener(new DocumentAdapter() {
			public void documentClosing(Document document, boolean inApplicationQuit) throws DocumentCloseVetoException {
				SchemTabbook tb = (SchemTabbook) document.getDocumentPane();
				Schematics schematics = tb.getSchematics();
				if(!schematics.isDirty())
					document.dispose();
				else {
					document.setSelected();
					int ask = DefaultMessages.askSave(document.getContainer());
					if(ask == DefaultMessages.OPTION_CANCEL)
						throw new DocumentCloseVetoException();
					else if(ask == DefaultMessages.OPTION_SAVE) {
						if(!save(document))
							throw new DocumentCloseVetoException();
					}
					document.dispose();
				}
			}
		});
	}

	public void toolChanged(Document document, EditTool tool) {
		SchemTabbook schemTabbook = (SchemTabbook) document.getDocumentPane();
		schemTabbook.setEditTool(tool);
		application.getToolBar(document).addExtraObjects(tool.getToolbarObjects());
	}
}
