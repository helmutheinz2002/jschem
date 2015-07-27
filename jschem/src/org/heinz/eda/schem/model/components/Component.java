package org.heinz.eda.schem.model.components;

import java.awt.Graphics;


public class Component extends AbstractComponent {
	public static final String KEY_PART_ID = "PART_ID";
	public static final String KEY_PART_NAME = "PART_NAME";
	public static final String KEY_ORDER_NO = "ORDER_NR";
	public static final String KEY_MODEL_NAME = "PART_MODEL";
	public static final String KEY_SUB_CIRCUIT = "SUB_CIRCUIT";
	
	public Component() {
	}
	
	public Component(int x, int y) {
		super(x, y);
		initTexts();
	}
	
	public Component(Component c) {
		super(c);
	}
	
	public AbstractComponent duplicate() {
		return new Component(this);
	}
	
	protected void init() {
		super.init();
		group = true;
	}
	
	protected void initTexts() {
		group = true;
		int y = 0;
		y = addAttributeText(KEY_PART_ID, "", y, false);
		y = addAttributeText(KEY_PART_NAME, "", y, false);
		y = addAttributeText(KEY_ORDER_NO, "", y, false);
		y = addAttributeText(KEY_MODEL_NAME, "", y, false);
	}
	
	public boolean supportsAutoIdAssignment() {
		return true;
	}
	
	protected void draw(Graphics g, double zoom, boolean selected) {
		// nothing to draw here
	}
}
