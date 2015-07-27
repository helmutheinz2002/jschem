package org.heinz.eda.schem.ui;

import javax.swing.JFrame;

public class OrgSchemApplication extends JFrame /*implements ActionListener, OSXApp*/ {
	/*
	private static final int PASTE_OFFSET = 10;
	private static final float ITEXT_UNIT = 2540f / 72.0f;
	private static final int MAX_RECENT_FILES = 5;
	
	private SchemTabbook schemTabbook;
	private SchemMenuBar menuBar;
	private EditToolbar editToolbar;
	private SchemToolbar schemToolbar;
	private SchemStatusBar infoBar;
	private Schematics schematics;
	private File file;
	private String workDir;
	private Library library;
	private Point pasteOffset = new Point();
	private OutputStreamWindow osw;

	public OrgSchemApplication(OutputStreamWindow osw, final String fileName) {
		super();

		this.osw = osw;
		new Translator("translations/JSchem");
		new SchemClipBoard();
		
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setTitle();
		
		Toolkit.getDefaultToolkit().setDynamicLayout(true);
		
		init();
		newFile(null);
		
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        
        int ww = d.width * 4 / 5;
        int wh = d.height * 4 / 5;
        setSize(ww, wh);
        setLocation((d.width - ww)/2, (d.height - wh)/2);
        
		WindowsXPAdapter.init();
		WindowsXPAdapter.associateFileType();
		
		addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				library = new Library(workDir);
				
				if(!SchemOptions.instance().getStringOption(SchemOptions.PROPERTY_LAST_PROGRAM_VERSION).equals(SchemConstants.PROGRAM_VERSION)) {
					updateVersion();
					SchemOptions.instance().setOption(SchemOptions.PROPERTY_LAST_PROGRAM_VERSION, SchemConstants.PROGRAM_VERSION);
				}
				
		        if(fileName != null)
					loadFile(new File(fileName));
			}
			
			public void windowClosing(WindowEvent e) {
				quit();
			}
		});
	}
	
	private void newFile(Schematics schematics) {
		if(schemTabbook != null) {
			getContentPane().remove(schemTabbook);
			schemTabbook.removePropertyChangeListener(infoBar);
		}
		
		if(schematics == null)
			schematics = new Schematics(Translator.translate("SHEET"));
		
		this.schematics = schematics;

		schematics.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				setTitle();
			}
		});
		schemTabbook = new SchemTabbook(schematics);
		schemTabbook.addPropertyChangeListener(infoBar);
		getContentPane().add(BorderLayout.CENTER, schemTabbook);
		
		editToolbar.selectTool(SelectionTool.instance());
		file = null;
		
		ExtendedUndoManager.instance().discardAllEdits();
		
		invalidate();
	}
	
	private void init() {
		workDir = System.getProperty("user.home") + File.separator + "." + SchemConstants.PROGRAM_NAME.toLowerCase();
		File wd = new File(workDir);
		wd.mkdirs();
		new SchemOptions(workDir);

		ImageIcon icon = (ImageIcon) IconLoader.instance().loadIcon("icons/menu/icon.png");
		setIconImage(icon.getImage());
		menuBar = new SchemMenuBar(null);
		setJMenuBar(menuBar);
		menuBar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadFile(new File(e.getActionCommand()));
			}
		});
		
		SchemActions.instance().addActionListener(this);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(BorderLayout.NORTH, schemToolbar = new SchemToolbar());
		editToolbar = new EditToolbar() {
			protected EditTool getNextTool(EditTool tool) {
				if((tool == WireSplitTool.instance()) || (tool == ZoomTool.instance()) || (tool == LibraryTool.instance()))
					return SelectionTool.instance();
				return tool;
			}
		};
		getContentPane().add(BorderLayout.WEST, editToolbar);
		
		infoBar = new SchemStatusBar(osw.getButton());
		getContentPane().add(BorderLayout.SOUTH, infoBar);
	}
	
	public void updateVersion() {
		updateLibrary(true);
	}
	
	public void updateLibrary(boolean silent) {
		List updateActions = library.checkLibrary();
		
		if(!silent && !Library.hasPendingUpdates(updateActions)) {
			// Nothing to do
			JOptionPane.showMessageDialog(this, Translator.translate("LIBRARY_UP_TO_DATE"), Translator.translate("LIBRARY_UPDATE"), JOptionPane.INFORMATION_MESSAGE);
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
				Dimension d = getSize();
				int res = OkCancelDialog.showDialog(this, updatePanel, new Dimension(d.width * 2 / 3, d.height * 2 / 3));
				
				if(res == OkCancelDialog.OK_PRESSED)
					library.updateLibrary(updateActions);
				else
					break;
			}
		}
	}

	public void openFile(final String fileName) {
		File f = new File(fileName);
		loadFile(f);
	}
	
	public void about() {
		String translationCredits = "<p><table>"; 
		Map translators = Translator.instance().getAvailableTranslations();
		for(Iterator it=translators.keySet().iterator(); it.hasNext();) {
			String language = (String) it.next();
			String translator = (String) translators.get(language);
			translationCredits += "<tr><td><b>" + language + "</b></td><td>" + translator + "</td></tr>";
		}
		translationCredits += "</table><br>";
		
		JOptionPane.showMessageDialog(this,
				"<html><h2>"
				+ SchemConstants.PROGRAM_NAME + " " + SchemConstants.PROGRAM_VERSION
				+ "</h2>"
				+ "<table>"
				+ "<tr><td><b>" + Translator.translate("BUILD") + "</td><td>" + SchemConstants.DATE + "</td></tr>"
				+ "<tr><td><b>" + Translator.translate("OPERATING_SYSTEM") + "</td><td>" + CrossPlatform.getOsInfo() + "</td></tr>"
				+ "<tr><td><b>" + Translator.translate("JAVA_RUNTIME") + "</td><td>" + CrossPlatform.getJavaInfo() + "</td></tr>"
				+ "</table>"
				+ "<br>" + Translator.translate("ABOUT_TEXT")
				+ "<p><hr><br>"
				+ Translator.translate("ABOUT_LICENSE")
				+ "<p><hr><br>"
				+ Translator.translate("ABOUT_CREDITS")
				+ "<p><table>" +
						"<tr><td><b>JGoodies Looks</b></td><td>www.jgoodies.com</td></tr>" +
						"<tr><td><b>iText</b></td><td>www.lowagie.com/iText</td></tr>" +
						"<tr><td><b>Windows registry dll</b></td><td>www.trustice.com/java/jnireg</td></tr>" +
						"<tr><td><b>ExpressLib</b></td><td>sourceforge.net/projects/expresslib</td></tr>" +
						"</table><br>"
				+ "<p><hr><br>"
				+ Translator.translate("TRANSLATION_CREDITS")
				+ translationCredits,
				Translator.translate("ABOUT_TITLE"), JOptionPane.INFORMATION_MESSAGE);
	}

	public void preferences() {
		OkCancelDialog.showDialog(this, new OptionsEditor(), new Dimension(400, 300));
	}

	private boolean askProceed() {
		int res = JOptionPane.showConfirmDialog(this, Translator.translate("PROCEED_CONFIRMATION"),
				Translator.translate("EXIT_TITLE"), JOptionPane.YES_NO_OPTION);
		return (res == JOptionPane.YES_OPTION);
	}
	
	public void quit() {
		if(schematics.isDirty() && !askProceed())
			return;
		
		exitApp();
	}
	
	private void setTitle() {
		String title = SchemConstants.PROGRAM_NAME;
		if(file != null)
			title += " - " + file.getAbsolutePath();
		if((schematics != null) && schematics.isDirty())
			title += " *";
		setTitle(title);
	}
	
	private File selectFile(boolean save) {
		String title = Translator.translate(save ? "SAVE_SCHEMATICS" : "OPEN_SCHEMATICS");
		
		if(MacOSXAdapter.MAC_OS_X)
			return MacOSXAdapter.selectFile(this, file, save, title);
		
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle(title);
    	fc.setFileFilter(SchemFileFilter.instance(save));
    	if(file != null)
    		fc.setSelectedFile(file);
    	if(save) {
    		if(fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
    			return fc.getSelectedFile();
    	} else {
    		if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
    			return fc.getSelectedFile();
    	}
    	
    	return null;
	}
	
	public void saveAs() {
		File f = selectFile(true);
		
		if(f != null) {
			String fn = f.getName();
			if(fn.indexOf('.') < 0)
				f = new File(f.getAbsoluteFile() + "." + SchemFileFilter.SCHEM_EXTENSION);
			
			if(f.exists() && !MacOSXAdapter.MAC_OS_X) {
				int r = JOptionPane.showConfirmDialog(this, f.getAbsolutePath() + "\n\n" + Translator.translate("FILE_EXISTS_CONFIRMATION"),
						Translator.translate("FILE_EXISTS_TITLE"), JOptionPane.YES_NO_OPTION);
				if(r != JOptionPane.YES_OPTION)
					return;
			}
			file = f;
			save(file);
			addToRecentFiles(file);
			setTitle();
		}
	}

	public void save(File file) {
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
		} catch(Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, file.getAbsolutePath() + "\n\n" + Translator.translate("SAVE_ERROR"), Translator.translate("ERROR"), JOptionPane.ERROR_MESSAGE);
		}
	}

	public void load() {
		File f = selectFile(false);
		
		if(f != null)
			loadFile(f);
	}

	public void addToRecentFiles(File file) {
		List fileList = SchemOptions.instance().getListOption(SchemOptions.PROPERTY_BASE_RECENT_FILE);
		
		String path = file.getAbsolutePath();
		int idx = fileList.indexOf(path);
		if(idx >= 0)
			fileList.remove(idx);

		fileList.add(0, path);
		if(fileList.size() > MAX_RECENT_FILES)
			fileList = fileList.subList(0, MAX_RECENT_FILES);
		
		SchemOptions.instance().setOption(SchemOptions.PROPERTY_BASE_RECENT_FILE, fileList);
	}
	
	public void loadFile(File f) {
		if(schematics.isDirty() && !askProceed())
			return;
		
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
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
				ExpressImport importer = new ExpressImport();
				newSchematics = importer.importFile(f);
			}
			
			newSchematics.setDirty(false);
			newFile(newSchematics);
			file = f;
			addToRecentFiles(f);
			setTitle();
			schemTabbook.setSelectedIndex(0);
			adjustSnapGrid();
		} catch(FileNotFoundException fex) {
			message = "FILE_NOT_FOUND";
		} catch(Exception cex) {
			cex.printStackTrace();
			message = "FILE_INVALID_CONTENTS";
		} finally {
			setCursor(Cursor.getDefaultCursor());
		}
		
		if(message != null)
			JOptionPane.showMessageDialog(this, f.getAbsolutePath() + "\n\n" + Translator.translate(message), Translator.translate("ERROR"), JOptionPane.ERROR_MESSAGE);
	}
	
	private void adjustSnapGrid() {
		List onGrid = new ArrayList();
		List offGrid = new ArrayList();
		int guessGrid = schematics.guessSnapGrid(onGrid, offGrid);
		boolean gridSnap = SchemOptions.instance().getBoolOption(SchemOptions.PROPERTY_GRID_SNAP);
		int currentGrid = SchemOptions.instance().getIntOption(SchemOptions.PROPERTY_GRID_SNAP_SPACING);
		
		if(guessGrid < 5) {
			if(gridSnap) {
				int res = JOptionPane.showConfirmDialog(this, Translator.translate("COULD_NOT_GUESS_GRID"), Translator.translate("GRID"), JOptionPane.YES_NO_OPTION);
				if(res == JOptionPane.YES_OPTION)
					SchemOptions.instance().setOption(SchemOptions.PROPERTY_GRID_SNAP, new Boolean(false));
			}
			return;
		}
		
		if(!gridSnap || (gridSnap && ((guessGrid % currentGrid) != 0))) {
			String msg = Translator.translate("SNAP_GRID_DETECTED") + " " + ((double) guessGrid/100.0) + "mm\n" + Translator.translate("SWITCH_TO_GUESSED_GRID");
			int res = JOptionPane.showConfirmDialog(this, msg, Translator.translate("GRID"), JOptionPane.YES_NO_OPTION);
			if(res == JOptionPane.YES_OPTION) {
				SchemOptions.instance().setOption(SchemOptions.PROPERTY_GRID_SNAP, new Boolean(true));
				SchemOptions.instance().setOption(SchemOptions.PROPERTY_GRID_SNAP_SPACING, new Integer(guessGrid));
				SchemOptions.instance().setOption(SchemOptions.PROPERTY_GRID_VISIBLE, new Boolean(true));
				int visibleGrid = GridHelper.guessVisibleGrid(guessGrid);
				SchemOptions.instance().setOption(SchemOptions.PROPERTY_GRID_SPACING, new Integer(visibleGrid));
				
//				if(offGrid.size() > 0) {
//					res = JOptionPane.showConfirmDialog(this, "Grid erzwingen? "+offGrid.size(), Translator.translate("GRID"), JOptionPane.YES_NO_OPTION);
//					if(res == JOptionPane.YES_OPTION)
//						try {
//						schematics.enforceSnapGrid();
//						} catch(Exception ex) {
//							ex.printStackTrace();
//						}
//				}
			}
		}
	}
	
	private void loadFromLib() {
		String dir = SchemOptions.instance().getStringOption(SchemOptions.PROPERTY_LAST_LIB_DIR);
		if((dir == null) || (dir.length() == 0))
			dir = library.getLibraryDir();
		ComponentFileChooser fc = new ComponentFileChooser(dir);
		if(fc.showOpenDialog(this) == ComponentFileChooser.APPROVE_OPTION) {
			AbstractComponent c = fc.getSelectedComponent();
			if(c != null) {
				SchemOptions.instance().setOption(SchemOptions.PROPERTY_LAST_LIB_DIR, fc.getCurrentDirectory().getAbsolutePath());
				SheetPanel sp = schemTabbook.getCurrentEditor(); 
				sp.clearSelection();
				Sheet s = schemTabbook.getCurrentSheet();
				Point cp = sp.getCenter();
				cp = sp.constrainPoint(cp.x, cp.y, false);
				c.setPosition(cp.x, cp.y);
				s.addComponent(c);
				sp.addToSelection(c);
				
				LibraryTool.instance().addToRecentComponents(fc.getSelectedFile());
				ExtendedUndoManager.instance().addEdit(new UndoNew(s, c));
			}
		}
	}
	
	private void saveToLib() {
		String dir = SchemOptions.instance().getStringOption(SchemOptions.PROPERTY_LAST_LIB_DIR);
		if((dir == null) || (dir.length() == 0))
			dir = library.getLibraryDir();
		ComponentFileChooser fc = new ComponentFileChooser(dir);
		if(fc.showSaveDialog(this) == ComponentFileChooser.APPROVE_OPTION) {
			File f = fc.getSelectedFile();
			
			String fn = f.getAbsolutePath();
			if(!ComponentFileFilter.instance().hasExtension(fn))
				fn += "." + ComponentFileFilter.COMPONENT_EXTENSION;
			
			f = new File(fn);
			if(f.exists()) {
				int r = JOptionPane.showConfirmDialog(this, fn + "\n\n" + Translator.translate("COMPONENT_EXISTS_CONFIRMATION"),
						Translator.translate("COMPONENT_EXISTS_TITLE"), JOptionPane.YES_NO_OPTION);
				if(r != JOptionPane.YES_OPTION)
					return;
			}

			SchemOptions.instance().setOption(SchemOptions.PROPERTY_LAST_LIB_DIR, fc.getCurrentDirectory().getAbsolutePath());
			AbstractComponent c = (AbstractComponent) schemTabbook.getCurrentEditor().getSelection().get(0);
			try {
				c = c.duplicate();
				c.setPosition(0, 0);
				Library.saveComponent(c, f);
			} catch(Exception ex) {
				JOptionPane.showMessageDialog(this, Translator.translate("COMPONENT_SAVE_ERROR") + "\n\n" + ex.getClass() + " " + ex.getMessage(), Translator.translate("ERROR"), JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private void export(Exporter exporter, String extOptionKey) {
		try {
			String ext = ExportHelper.export(this, exporter, SchemOptions.instance().getStringOption(extOptionKey));
			if(ext != null)
				SchemOptions.instance().setOption(extOptionKey, ext);
		} catch(Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, Translator.translate("FILE_EXPORT_FAILED"), Translator.translate("ERROR"), JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void exportSheet() {
		Exporter exporter = new Exporter() {
			public void export(ExportFormat exportFormat, File outputFile) {
				Sheet sheet = schemTabbook.getCurrentEditor().getSheet();
				SheetSize ss = sheet.getSize();
				Rectangle size = new Rectangle((float) ss.width / ITEXT_UNIT, (float) ss.height / ITEXT_UNIT);
				ExportHelper.exportSimpleImageDocument(Locale.getDefault(), outputFile, exportFormat, sheet.getImage(), size);
			}

			public ExportFormat[] getSupportedFormats() {
				return ExportHelper.getDefaultImageExportFormats();
			}
		};
		
		export(exporter, SchemOptions.PROPERTY_LAST_SHEET_EXPORT_EXTENSION);
	}
	
	private void exportSchematics() {
		Exporter exporter = new Exporter() {
			public void export(ExportFormat exportFormat, File outputFile) {
				Sheet sheet = schemTabbook.getCurrentEditor().getSheet();
				SheetSize ss = sheet.getSize();
				Rectangle size = new Rectangle((float) ss.width / ITEXT_UNIT, (float) ss.height / ITEXT_UNIT);
				ExportHelper.exportMultiImageDocument(Locale.getDefault(), outputFile, exportFormat, schemTabbook, size);
			}

			public ExportFormat[] getSupportedFormats() {
				return ExportHelper.getDefaultMultiImageExportFormats();
			}
		};
		
		export(exporter, SchemOptions.PROPERTY_LAST_SCHEMATICS_EXPORT_EXTENSION);
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == SchemActions.instance().exitItem) {
			quit();
		} else if (e.getSource() == SchemActions.instance().optionsItem) {
			preferences();
		} else if (e.getSource() == SchemActions.instance().libraryItem) {
			loadFromLib();
		} else if (e.getSource() == SchemActions.instance().newItem) {
			newFile(null);
		} else if (e.getSource() == SchemActions.instance().saveItem) {
			if(file != null)
				save(file);
			else
				saveAs();
		} else if (e.getSource() == SchemActions.instance().saveAsItem) {
			saveAs();
		} else if (e.getSource() == SchemActions.instance().openItem) {
			load();
		} else if (e.getSource() == SchemActions.instance().tutorialItem) {
			JOptionPane.showMessageDialog(this, "<html><h1>Test</h1><img src=\"/icons/menu/down.png\"></html>");
		} else if (e.getSource() == SchemActions.instance().exportSheetItem) {
			exportSheet();
		} else if (e.getSource() == SchemActions.instance().exportSchematicsItem) {
			exportSchematics();
		} else if (e.getSource() == SchemActions.instance().zoomFitItem) {
			schemTabbook.getCurrentEditor().zoomToFit();
		} else if (e.getSource() == SchemActions.instance().zoomInItem) {
			double zoom = SchemOptions.instance().getDoubleOption(SchemOptions.PROPERTY_ZOOM);
			double zoomStep = SchemOptions.instance().getDoubleOption(SchemOptions.PROPERTY_ZOOM_INCREMENT);
			SchemOptions.instance().setOption(SchemOptions.PROPERTY_ZOOM, new Double(zoom * zoomStep));
		} else if (e.getSource() == SchemActions.instance().zoomOutItem) {
			double zoom = SchemOptions.instance().getDoubleOption(SchemOptions.PROPERTY_ZOOM);
			double zoomStep = SchemOptions.instance().getDoubleOption(SchemOptions.PROPERTY_ZOOM_INCREMENT);
			SchemOptions.instance().setOption(SchemOptions.PROPERTY_ZOOM, new Double(zoom / zoomStep));
		} else if (e.getSource() == SchemActions.instance().groupComponentItem) {
			SheetPanel sheetPanel = schemTabbook.getCurrentEditor();
			AbstractComponent group = sheetPanel.getSheet().groupComponent(sheetPanel.getSelection());
			sheetPanel.addToSelection(group);
		} else if (e.getSource() == SchemActions.instance().groupSymbolItem) {
			SheetPanel sheetPanel = schemTabbook.getCurrentEditor();
			AbstractComponent group = sheetPanel.getSheet().groupSymbol(sheetPanel.getSelection());
			sheetPanel.addToSelection(group);
		} else if (e.getSource() == SchemActions.instance().ungroupItem) {
			SheetPanel sheetPanel = schemTabbook.getCurrentEditor();
			Sheet sheet = sheetPanel.getSheet();
			List selection = sheetPanel.getSelection();
			List comps = sheet.ungroup((Component) selection.get(0));
			sheetPanel.addToSelection(comps);
		} else if (e.getSource() == SchemActions.instance().newSheetItem) {
			Sheet newSheet = schematics.addSheet(Translator.translate("SHEET"), true);
			ExtendedUndoManager.instance().addEdit(new UndoAddSheet(schematics, newSheet));
		} else if (e.getSource() == SchemActions.instance().sheetLeftItem) {
			Sheet sheet = schemTabbook.getCurrentSheet();
			schematics.moveSheet(sheet, true);
			ExtendedUndoManager.instance().addEdit(new UndoMoveSheet(schematics, sheet, true));
		} else if (e.getSource() == SchemActions.instance().sheetRightItem) {
			Sheet sheet = schemTabbook.getCurrentSheet();
			schematics.moveSheet(sheet, false);
			ExtendedUndoManager.instance().addEdit(new UndoMoveSheet(schematics, sheet, false));
		} else if (e.getSource() == SchemActions.instance().sheetSizeItem) {
			Sheet sheet = schemTabbook.getCurrentSheet();
			OkCancelDialog.showDialog(this, new SheetSizePanel(sheet), new Dimension(300, 200));
		} else if (e.getSource() == SchemActions.instance().renameSheetItem) {
			Sheet sheet = schemTabbook.getCurrentSheet();
			String name = sheet.getTitle();
			name = (String) JOptionPane.showInputDialog(this, Translator.translate("RENAME_SHEET"), Translator.translate("RENAME_SHEET_TITLE"),
							JOptionPane.QUESTION_MESSAGE, null, null, name);
			if (name != null) {
				String oldTitle = sheet.getTitle();
				sheet.setTitle(name);
				ExtendedUndoManager.instance().addEdit(new UndoRenameSheet(sheet, oldTitle));
			}
		} else if (e.getSource() == SchemActions.instance().deleteSheetItem) {
			Sheet sheet = schemTabbook.getCurrentSheet();
			int r = JOptionPane.showConfirmDialog(this, Translator.translate("DELETE_SHEET"), Translator.translate("DELETE_SHEET_TITLE"),
					JOptionPane.YES_NO_OPTION);
			if (r == JOptionPane.YES_OPTION) {
				int idx = schematics.removeSheet(sheet, false);
				ExtendedUndoManager.instance().addEdit(new UndoDeleteSheet(schematics, sheet, idx));
			}
		} else if (e.getSource() == SchemActions.instance().aboutItem) {
			about();
		} else if (e.getSource() == SchemActions.instance().deleteItem) {
			SheetPanel sp = schemTabbook.getCurrentEditor(); 
			List sel = sp.getSelection();
			ExtendedUndoManager.instance().addEdit(new UndoDelete(sp.getSheet(), sel));
			sp.removeSelection();
		} else if (e.getSource() == SchemActions.instance().cutItem) {
			copy();
			schemTabbook.getCurrentEditor().removeSelection();
		} else if (e.getSource() == SchemActions.instance().copyItem) {
			copy();
		} else if (e.getSource() == SchemActions.instance().pasteItem) {
			paste();
		} else if (e.getSource() == SchemActions.instance().undoItem) {
			ExtendedUndoManager.instance().undo();
		} else if (e.getSource() == SchemActions.instance().redoItem) {
			ExtendedUndoManager.instance().redo();
		} else if (e.getSource() == SchemActions.instance().selectAllItem) {
			schemTabbook.getCurrentEditor().selectAll();
		} else if (e.getSource() == SchemActions.instance().toFrontItem) {
			schemTabbook.getCurrentEditor().selectionToFront();
		} else if (e.getSource() == SchemActions.instance().toBackItem) {
			schemTabbook.getCurrentEditor().selectionToBack();
		} else if (e.getSource() == SchemActions.instance().updateLibraryItem) {
			updateLibrary(false);
		} else if (e.getSource() == SchemActions.instance().propertiesItem) {
			SheetPanel sp = schemTabbook.getCurrentEditor();
			AbstractComponent c = (AbstractComponent) sp.getSelection().get(0);
			ComponentPropertyDialog.openDialog(this, c);
		} else if (e.getSource() == SchemActions.instance().saveComponentItem) {
			saveToLib();
		} else {
			Action action = (Action) e.getSource();
			setTool(action);
		}
	}
	
	private void copy() {
		pasteOffset = new Point();

		List components = new ArrayList();
		for(Iterator it=schemTabbook.getCurrentEditor().getSelection().iterator(); it.hasNext();) {
			AbstractComponent c = (AbstractComponent) it.next();
			c = c.duplicate();
			components.add(c);
		}
		SchemClipBoard.instance().post(components);
	}

	private void paste() {
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
		
		ExtendedUndoManager.instance().addEdit(new UndoNew(s, newObjects));
	}

	private void setTool(Action a) {
		EditTool tool = editToolbar.getToolForAction(a);
		if(tool != null) {
			schemTabbook.setEditTool(tool);
			schemToolbar.addExtraObjects(tool.getToolbarObjects());
		}
	}
	
	private void exitApp() {
		System.exit(0);
	}
	*/
}
