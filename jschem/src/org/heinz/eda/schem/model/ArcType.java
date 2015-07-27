package org.heinz.eda.schem.model;

public class ArcType {
	public static final int ARC_FULL = 0;
	public static final int ARC_300 = 1;
	public static final int ARC_210 = 2;
	public static final int ARC_HALF = 3;
	public static final int ARC_QUARTER = 4;
	public static final int ARC_CORNER = 5;
	
	public static final ArcType[] ARC_TYPES = {
		new ArcType(0, 360, "ARC_360", "arc_360.png"),
		new ArcType(30, 300, "ARC_300", "arc_300.png"),
		new ArcType(75, 210, "ARC_210", "arc_210.png"),
		new ArcType(90, 180, "ARC_180", "arc_180.png"),
		new ArcType(135, 90, "ARC_90", "arc_90.png"),
		new ArcType(90, 90, "ARC_CORNER", "arc_90_corner.png")
	};
	
	public final int startAngle;
	public final int arcAngle;
	public final String name;
	public final String icon;
	
	private ArcType(int startAngle, int endAngle, String name, String icon) {
		this.startAngle = startAngle;
		this.arcAngle = endAngle;
		this.name = name;
		this.icon = icon;
	}
}
