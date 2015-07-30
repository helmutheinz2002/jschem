package org.heinz.eda.schem.model.components;

import java.awt.Graphics;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import org.heinz.eda.schem.model.SchemOptions;
import org.heinz.eda.schem.util.ExtRect;
import org.heinz.eda.schem.util.GridHelper;
import org.heinz.eda.schem.util.LineHelper;
import org.heinz.framework.utils.xml.XmlProperty;
import org.heinz.framework.utils.xml.XmlPropertyConverterInteger;

public class Line extends AbstractComponent {

    static {
        PROPERTIES.put(new XmlProperty("dx", XmlPropertyConverterInteger.instance()), Line.class);
        PROPERTIES.put(new XmlProperty("dy", XmlPropertyConverterInteger.instance()), Line.class);
    }

    private int dx;

    private int dy;

    protected boolean stickyHandles = false;

    protected Handle baseHandle;

    protected Handle offsetHandle;

    public Line() {
    }

    public Line(int x, int y, int dx, int dy) {
        super(x, y);
        this.dx = dx;
        this.dy = dy;
    }

    public Line(Line line) {
        super(line);
        dx = line.dx;
        dy = line.dy;
    }

    @Override
    public boolean hasBecomeInvalid() {
        return (dx == 0) && (dy == 0);
    }

    @Override
    public void snapToGrid(int snapGrid) {
        fireWillChange();
        super.snapToGrid(snapGrid);
        int ox = GridHelper.snapToGrid(dx, snapGrid);
        int oy = GridHelper.snapToGrid(dy, snapGrid);
        setOffset(ox, oy, true);
        fireChanged();
    }

    @Override
    protected void addHandles() {
        addHandle(baseHandle = new Handle(this, true, stickyHandles) {

            @Override
            protected Point getPosition() {
                return new Point(0, 0);
            }

            @Override
            public void setPosition(Point offset, boolean dragging) {
                Line.this.setPosition(getX() + offset.x, getY() + offset.y, false, dragging);
                offset = getOrientation().unTransform(offset);
                setOffset(dx - offset.x, dy - offset.y, false);
            }

        });
        addHandle(offsetHandle = new Handle(this, true, stickyHandles) {

            @Override
            protected Point getPosition() {
                return new Point(dx, dy);
            }

            @Override
            public void setPosition(Point offset, boolean dragging) {
                offset = getOrientation().unTransform(offset);
                setOffset(dx + offset.x, dy + offset.y, false);
            }

        });
    }

    @Override
    protected void draw(Graphics g, double zoom, boolean selected) {
        setStroke(g, zoom);
        g.setColor(getColor(selected));
        int x2 = (int) (dx * zoom);
        int y2 = (int) (dy * zoom);
        g.drawLine(0, 0, x2, y2);
    }

    @Override
    protected ExtRect getBounds() {
        if (!isVisible()) {
            return null;
        }

        ExtRect b = new ExtRect(0, 0, dx, dy);
        return b;
    }

    @Override
    public boolean contains(int x, int y, int clickTolerance) {
        if (!isVisible()) {
            return false;
        }

        int w = SchemOptions.instance().getIntOption(SchemOptions.PROPERTY_LINE_WIDTH);
        clickTolerance += w;

        return LineHelper.contains(x, y, 0, 0, dx, dy, clickTolerance);
    }

    @Override
    public AbstractComponent duplicate() {
        return new Line(this);
    }

    public void setOffset(int dx, int dy, boolean withHandles) {
        Point oldPos = offsetHandle.getHandlePosition().absPos;

        fireWillChange();
        this.dx = dx;
        this.dy = dy;
        fireChanged();

        if (withHandles) {
            Point newPos = offsetHandle.getHandlePosition().absPos;
            Map handles = new HashMap();
            handles.put(offsetHandle, oldPos);
            Point offset = new Point(newPos.x - oldPos.x, newPos.y - oldPos.y);
            fireHandlesMoved(handles, offset, false);
        }
    }

    public Point getOffset() {
        return new Point(dx, dy);
    }

    public int getDx() {
        return dx;
    }

    public void setDx(int dx) {
        fireWillChange();
        this.dx = dx;
        fireChanged();
    }

    public int getDy() {
        return dy;
    }

    public void setDy(int dy) {
        fireWillChange();
        this.dy = dy;
        fireChanged();
    }

}
