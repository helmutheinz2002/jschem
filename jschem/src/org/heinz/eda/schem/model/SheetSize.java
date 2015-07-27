package org.heinz.eda.schem.model;

import java.io.Serializable;

public class SheetSize implements Serializable {
	public static final int LETTER = 0;
	public static final int LEGAL = 1;
	public static final int TABLOID = 2;
	public static final int A4 = 3;
	public static final int A3 = 4;
	public static final int B4 = 5;
	
	public static final SheetSize[] SIZES = {
		new SheetSize(27940, 21590, "Letter (11\" x 8.5\")", LETTER),
		new SheetSize(35560, 21590, "Legal (14\" x 8.5\")", LEGAL),
		new SheetSize(43180, 27940, "Tabloid (17\" x 11\")", TABLOID),
		new SheetSize(29700, 21000, "A4 (297mm x 210mm)", A4),
		new SheetSize(42000, 29700, "A3 (420mm x 297mm)", A3),
		new SheetSize(35300, 25000, "B4 (353mm x 250mm)", B4)
	};
	
	public final int width;
	public final int height;
	public final int key;
	public final String label;
	
	public SheetSize(int width, int height) {
		this(width, height, "", -1);
	}
	
	private SheetSize(int width, int height, String label, int key) {
		this.width = width;
		this.height = height;
		this.label = label;
		this.key = key;
	}
	
	public String toString() {
		return label;
	}
}
