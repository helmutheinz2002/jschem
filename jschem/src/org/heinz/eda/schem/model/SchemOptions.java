
package org.heinz.eda.schem.model;

import java.awt.Color;
import java.awt.Font;

import org.heinz.framework.utils.AbstractOptions;

public class SchemOptions extends AbstractOptions {

	public static final String PROPERTY_GRID_VISIBLE = defineOption("Settings.gridVisible", true);

	public static final String PROPERTY_SMART_JUNCTIONS = defineOption("Settings.smartJunctions", true);

	public static final String PROPERTY_SMART_JUNCTIONS_OUTLINE = defineOption("Settings.smartJunctionsOutline", false);

	public static final String PROPERTY_GRID_SNAP = defineOption("Settings.gridSnap", true);

	public static final String PROPERTY_GRID_SPACING = defineOption("Settings.gridSpacing", new Integer(500));

	public static final String PROPERTY_GRID_SNAP_SPACING = defineOption("Settings.gridSnapSpacing", new Integer(50));

	public static final String PROPERTY_ZOOM_DEFAULT = defineOption("Settings.zoomDefault", new Double(0.04));

	public static final String PROPERTY_ZOOM_INCREMENT = defineOption("Settings.zoomIncrement", new Double(1.2));

	public static final String PROPERTY_DEFAULT_SHEET_SIZE = defineOption("Settings.defaultSheetSize", new Integer(SheetSize.A4));

	public static final String PROPERTY_PIN_RADIUS = defineOption("Settings.pinRadius", new Integer(50));

	public static final String PROPERTY_CORNER_RADIUS = defineOption("Settings.cornerRadius", new Integer(50));

	public static final String PROPERTY_TEXT_FONT_SIZE = defineOption("Settings.FontFontSize", new Integer(160));

	public static final String PROPERTY_TEXT_FONT_NAME = defineOption("Settings.FontFontName", "Monospaced");

	public static final String PROPERTY_TEXT_FONT_STYLE = defineOption("Settings.FontFontStyle", new Integer(Font.PLAIN));

	public static final String PROPERTY_LINE_WIDTH = defineOption("Settings.lineWidth", new Integer(30));

	public static final String PROPERTY_LAST_PROGRAM_VERSION = defineOption("Settings.lastProgramVersion", "");

	public static final String PROPERTY_LAST_LIB_DIR = defineOption("Settings.lastLibDir", "");

	public static final String PROPERTY_ANTIALIASING = defineOption("Settings.antiAliasing", checkSystemProperty("apple.awt.antialiasing", "on"));

	public static final String PROPERTY_FILL_WITH_SELECTION_COLOR = defineOption("Settings.fillWithSelectionColor", false);

	public static final String PROPERTY_LAST_SHEET_EXPORT_EXTENSION = defineOption("Settings.lastSheetExportExtension", "png");

	public static final String PROPERTY_LAST_SCHEMATICS_EXPORT_EXTENSION = defineOption("Settings.lastSchematicsExportExtension", "pdf");

	public static final String PROPERTY_SELECTION_CONTAINS = defineOption("Settings.selectionContains", true);

	public static final String PROPERTY_AUTONUMBER_PAGE_OFFSET = defineOption("Settings.autonumberPageOffset", new Integer(100));

	public static final String PROPERTY_REGISTER_FILETYPE = defineOption("Settings.registerFileType", true);

	public static final String PROPERTY_SHEET_COLOR = defineOption("Settings.sheetColor", Color.white);

	public static final String PROPERTY_PAGE_BORDER_COLOR = defineOption("Settings.pageBorderColor", Color.gray);

	public static final String PROPERTY_SELECTED_COLOR = defineOption("Settings.selectedColor", Color.blue);

	public static final String PROPERTY_SELECTION_BOX_COLOR = defineOption("Settings.selectionBoxColor", Color.red);

	public static final String PROPERTY_GRID_COLOR = defineOption("Settings.gridColor", Color.black);

	public static final String PROPERTY_COMPONENT_COLOR = defineOption("Settings.componentColor", Color.black);

	public static final String PROPERTY_HANDLE_FILL_COLOR = defineOption("Settings.handleFillColor", Color.yellow);

	public static final String PROPERTY_BASE_RECENT_COMPONENT = "Settings.recentComponent";

	private static SchemOptions instance;

	@SuppressWarnings("LeakingThisInConstructor")
	public SchemOptions(String dirName) {
		super(dirName, instance);
		instance = this;
	}

	public static SchemOptions instance() {
		return instance;
	}

}
