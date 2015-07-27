package org.heinz.framework.crossplatform.utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;

import javax.imageio.ImageIO;

public class SplashScreen extends Frame {
	private static final int DEFAULT_X = 600;
	private static final int DEFAULT_Y = 400;
	private static long MIN_DISPLAY_TIME = 2000;
	private static int TITLE_FONT_SIZE = 76;
	private static int DESCRIPTION_FONT_SIZE = 20;
	private static int COPYRIGHT_FONT_SIZE = 14;
	private static int FRAME_WIDTH = 3;
	
	private SplashScreenInfo info;
	private BufferedImage image;
	private Window mainWindow;
	private Thread splashThread;
	private Runnable startApplicationRunnable;
	private long earliestSplashStopTime;
	
	public SplashScreen(SplashScreenInfo info) {
		this.info = info;
		
		setUndecorated(true);
		if(!setOnTop())
			MIN_DISPLAY_TIME = 0;
		
		try {
			image = ImageIO.read(getClass().getResourceAsStream(info.imgName));
		} catch(Exception e) {
			e.printStackTrace();
			image = createEmergencyImage();
		}
		
		drawStrings(image);
	}
	
	private boolean setOnTop() {
		try {
			Method m = getClass().getMethod("setAlwaysOnTop", new Class[] { boolean.class });
			m.invoke(this, new Object[] { new Boolean(true) });
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	private void drawStrings(BufferedImage img) {
		Graphics2D g = (Graphics2D) img.getGraphics();
		int w = img.getWidth();
		int h = img.getHeight();
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		drawFramedText(g, w, h * 4/9 - TITLE_FONT_SIZE, TITLE_FONT_SIZE, info.title, true);
		drawFramedText(g, w, h * 3/5, DESCRIPTION_FONT_SIZE, info.description, true);
		drawFramedText(g, w, h - 2*COPYRIGHT_FONT_SIZE, COPYRIGHT_FONT_SIZE, info.copyright, false);
		
		try {
			BufferedImage java = ImageIO.read(getClass().getResourceAsStream("/data/icons/splash/java.png"));
			int ix = w - java.getWidth() - 8;
			int iy = h - java.getHeight() - 8;
			g.drawImage(java, ix, iy, null);
		} catch(Exception e) {
			// no Java logo
		}
		
		g.dispose();
	}
	
	private void drawFramedText(Graphics g, int iw, int y, int fontSize, String text, boolean drawFrame) {
		Graphics2D g2d = (Graphics2D) g;
		Font f = new Font("SansSerif", Font.BOLD, fontSize);
		FontMetrics fm = g.getFontMetrics(f);
		Rectangle2D fr = f.getStringBounds(text, g2d.getFontRenderContext());
		
		int h = fontSize * 8/5;
		int r = h / 2;
		int r2 = 2 * r;
		int w = (int) fr.getWidth() + fontSize * 3/2;
		int x = (iw - w) / 2;
		
		if(drawFrame) {
			Stroke orgStroke = g2d.getStroke();
			Stroke stroke = new BasicStroke(FRAME_WIDTH);
			g2d.setStroke(stroke);
			g.setColor(Color.white);
			g.fillArc(x, y, r2, r2, 0, 360);
			g.fillArc(x+w-r2, y, r2, r2, 0, 360);
			g.setColor(Color.black);
			g.drawArc(x, y, r2, r2, 0, 360);
			g.drawArc(x+w-r2, y, r2, r2, 0, 360);
			g.setColor(Color.white);
			g.fillRect(x + r, y, w - r2, h);
			g.setColor(Color.black);
			g.drawLine(x + r, y, x + w - r, y);
			g.drawLine(x + r, y + h, x+ w - r, y + h);
			g2d.setStroke(orgStroke);
		}
		
		g.setColor(Color.black);
		g.setFont(f);
		g.drawString(text, (iw - (int) fr.getWidth()) / 2, y + (h - fontSize)/2 + fm.getAscent() - fontSize/10);
	}
	
	private BufferedImage createEmergencyImage() {
		BufferedImage img = new BufferedImage(DEFAULT_X, DEFAULT_Y, BufferedImage.TYPE_INT_ARGB);
		Graphics g = img.getGraphics();
		g.setColor(new Color(220, 220, 220));
		g.fillRect(0, 0, DEFAULT_X, DEFAULT_Y);
		g.setColor(Color.black);
		g.drawRect(0, 0, DEFAULT_X-1, DEFAULT_Y-1);
		g.dispose();
		return img;
	}

	public void setMainWindow(Window mainWindow) {
		this.mainWindow = mainWindow;
		if(mainWindow.isVisible())
			stopSplash();
		else
			this.mainWindow.addWindowListener(new WindowAdapter() {
				public void windowOpened(WindowEvent e) {
					stopSplash();
				}
			});
	}
	
	public Dimension getImageSize() {
		if(image == null)
			return new Dimension(DEFAULT_X, DEFAULT_Y);
		
		return new Dimension(image.getWidth(null), image.getHeight(null));
	}
	
	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, this); 
	}
	
	public void stopSplash() {
		new Thread() {
			public void run() {
				long now = System.currentTimeMillis(); 
				if(now < earliestSplashStopTime) {
					long stillToDisplay = earliestSplashStopTime - now; 
					try {
						Thread.sleep(stillToDisplay);
					} catch (InterruptedException e) {
						// doesn't really matter
					}
				}
				splashThread.interrupt();
			}
		}.start();
	}
	
	public void startSplash(Runnable startAppRunnable) {
		startApplicationRunnable = startAppRunnable;
		
		splashThread = new Thread() {
			public void run() {
				setSize(getImageSize());
				setLocationRelativeTo(null);
				addWindowListener(new WindowAdapter() {
					public void windowOpened(WindowEvent e) {
						earliestSplashStopTime = System.currentTimeMillis() + MIN_DISPLAY_TIME;
						new Thread(startApplicationRunnable).start();
					}
				});
				setVisible(true);
				
				try {
					Thread.sleep(60000);
				} catch (InterruptedException e) {
				}
				dispose();	
			}
		};
		splashThread.start();
	}
}