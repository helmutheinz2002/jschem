
package org.heinz.eda.schem.model.expresssch;

import java.awt.Font;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.heinz.eda.schem.model.Orientation;
import org.heinz.eda.schem.model.SchemOptions;
import org.heinz.eda.schem.model.Schematics;
import org.heinz.eda.schem.model.Sheet;
import org.heinz.eda.schem.model.SheetSize;
import org.heinz.eda.schem.model.components.AbstractComponent;
import org.heinz.eda.schem.model.components.Arc;
import org.heinz.eda.schem.model.components.Component;
import org.heinz.eda.schem.model.components.Line;
import org.heinz.eda.schem.model.components.Pin;
import org.heinz.eda.schem.model.components.Square;
import org.heinz.eda.schem.model.components.Symbol;
import org.heinz.eda.schem.model.components.Text;
import org.heinz.eda.schem.model.components.Wire;

import express.sch.importer.ExpressSchReader;
import express.sch.objects.Circuit;
import express.sch.objects.CircuitObject;
import express.sch.objects.CompoundObject;

public class ExpressImport {

	public static Schematics importFile(File file) throws IOException {
		ExpressSchReader reader = new ExpressSchReader();
		Circuit circuit = reader.readFile(file);
		return digest(circuit);
	}

	public static Component importComponent(InputStream is) throws IOException {
		ExpressSchReader reader = new ExpressSchReader();
		CompoundObject component = reader.readCompoundObject(is);
		return convertComponent(component);
	}

	private static Schematics digest(Circuit circuit) {
		Schematics schematics = new Schematics();

		for(Enumeration e = circuit.children(); e.hasMoreElements();) {
			express.sch.objects.Sheet iSheet = (express.sch.objects.Sheet) e.nextElement();

			Sheet sheet = new Sheet(iSheet.name, new SheetSize(toMetric(iSheet.width), toMetric(iSheet.height)), false);
			schematics.addSheet(sheet);

			for(Enumeration objects = iSheet.children(); objects.hasMoreElements();) {
				CircuitObject co = (CircuitObject) objects.nextElement();

				if((co instanceof express.sch.objects.Component) || (co instanceof express.sch.objects.Symbol)) {
					List subComponents = new ArrayList();

					for(Enumeration subObjects = co.children(); subObjects.hasMoreElements();) {
						CircuitObject so = (CircuitObject) subObjects.nextElement();
						AbstractComponent component = createPrimitiveComponent(so);
						if(component != null) {
							sheet.addComponent(component);
							subComponents.add(component);
						}
					}

					if(co instanceof express.sch.objects.Component) {
						express.sch.objects.Component iComponent = (express.sch.objects.Component) co;
						Component group = sheet.groupComponent(subComponents);
						Point base = group.getPosition();

						setAttributes(group.getAttributeText(Component.KEY_PART_ID), base, iComponent.partId, null);
						setAttributes(group.getAttributeText(Component.KEY_PART_NAME), base, iComponent.partName, null);
						Text orderNr = group.getAttributeText(Component.KEY_ORDER_NO);
						orderNr.setText(iComponent.orderNo.text);
						orderNr.setVisible(false);
					} else if(co instanceof express.sch.objects.Symbol) {
						express.sch.objects.Symbol iSymbol = (express.sch.objects.Symbol) co;
						Symbol group = sheet.groupSymbol(subComponents);
						Point base = group.getPosition();

						setAttributes(group.getAttributeText(Symbol.KEY_NET_NAME), base, iSymbol.netName, null);
					}
				}
				AbstractComponent component = createPrimitiveComponent(co);
				if(component != null) {
					sheet.addComponent(component);
				}
			}
		}

		return schematics;
	}

	public static Component convertComponent(CompoundObject co) {
		int num = co.getChildCount();
		List components = new ArrayList();

		for(int i = 0; i < num; i++) {
			CircuitObject o = (CircuitObject) co.getChildAt(i);
			AbstractComponent so = createPrimitiveComponent(o);
			components.add(so);
		}

		Component group;
		if(co instanceof express.sch.objects.Component) {
			group = new Component(0, 0);
		} else {
			group = new Symbol(0, 0);
		}

		Sheet.group(components, group, null);
		return group;
	}

	private static AbstractComponent createPrimitiveComponent(CircuitObject co) {
		AbstractComponent component = null;

		if(co instanceof express.sch.objects.Line) {
			express.sch.objects.Line iLine = (express.sch.objects.Line) co;
			component = new Line(toMetric(iLine.x), toMetric(iLine.y), toMetric(iLine.x2 - iLine.x), toMetric(iLine.y2 - iLine.y));
		} else if(co instanceof express.sch.objects.Rectangle) {
			express.sch.objects.Rectangle iRect = (express.sch.objects.Rectangle) co;
			component = new Square(toMetric(iRect.x), toMetric(iRect.y), toMetric(iRect.x2 - iRect.x), toMetric(iRect.y2 - iRect.y));
		} else if(co instanceof express.sch.objects.Circle) {
			express.sch.objects.Circle iCircle = (express.sch.objects.Circle) co;
			Orientation o = convertToOrientation(iCircle.direction);
			if(iCircle.shape != 6) {
				o = o.getPrevOrientation();
			}
			component = new Arc(toMetric(iCircle.x), toMetric(iCircle.y), toMetric(iCircle.radius), iCircle.shape - 1);
			component.setOrientation(o);
		} else if(co instanceof express.sch.objects.Text) {
			express.sch.objects.Text iText = (express.sch.objects.Text) co;
			String fontName = SchemOptions.instance().getStringOption(SchemOptions.PROPERTY_TEXT_FONT_NAME);
			int fontSize = (int) (1.3 * (double) toMetric(iText.attributes.size));
			int fontStyle = iText.attributes.style == express.ExpressConstants.FONT_BOLD ? Font.BOLD : Font.PLAIN;
			component = new Text(toMetric(iText.x), toMetric(iText.y), iText.text, fontName, fontSize, fontStyle);
			component.setOrientation(convertToOrientation(iText.attributes.direction));
		} else if(co instanceof express.sch.objects.Pin) {
			express.sch.objects.Pin iPin = (express.sch.objects.Pin) co;
			Pin pin = new Pin(toMetric(iPin.x), toMetric(iPin.y));

			setAttributes(pin.getAttributeText(Pin.KEY_PIN_NO), null, iPin.pinNoText, "0");
			setAttributes(pin.getAttributeText(Pin.KEY_PIN_NAME), null, iPin.pinNameText, null);
			component = pin;
		} else if(co instanceof express.sch.objects.Wire) {
			express.sch.objects.Wire iWire = (express.sch.objects.Wire) co;
			component = new Wire(toMetric(iWire.x), toMetric(iWire.y), toMetric(iWire.x2 - iWire.x), toMetric(iWire.y2 - iWire.y));
		}

		if(component != null) {
			component.setColor(SchemOptions.instance().getColorOption(SchemOptions.PROPERTY_COMPONENT_COLOR));
		}
		return component;
	}

	private static int toMetric(int inch) {
		double id = (double) inch;
		double me = id * 2.4;
		int p = (int) me;
		return p;
	}

	private static Orientation convertToOrientation(int direction) {
		if((direction < 1) || (direction > 4)) {
			direction = 1;
		}
		Orientation o = Orientation.DIRECTIONS[direction - 1];
		return o;
	}

	private static void setAttributes(Text attr, Point basePoint, express.sch.objects.Text iText, String hiddenName) {
		int fontSize = toMetric(iText.attributes.size);
		Orientation orientation = convertToOrientation(iText.attributes.direction);
		int x = toMetric(iText.x);
		int y = toMetric(iText.y);
		if(basePoint != null) {
			x = x - basePoint.x;
			y = y - basePoint.y;
		}
		attr.setText(iText.text);
		attr.setVisible(iText.attributes.visible && (iText.text.length() > 0) && !iText.text.equals(hiddenName));
		attr.setOrientation(orientation);
		attr.setFontName(SchemOptions.instance().getStringOption(SchemOptions.PROPERTY_TEXT_FONT_NAME));
		attr.setFontSize(fontSize);
		attr.setFontStyle(Font.PLAIN);
		attr.setX(x);
		attr.setY(y);
	}

}
