package util.system.thumb;

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;


/**
 * this frame contains the screenshot of the display and wait for user 
 * to draw marquee selection
 * @author omotelet
 *
 */

public class ScreenShotFrame extends JFrame {

	
	public static int ITSShift = 20;
	public JLabel itsLabel;
	public BufferedImage itsImage;
	public Point itsStart;
	public Point itsStop;
	ThumbNotificationBox itsBox;
	
	public ScreenShotFrame(BufferedImage aImage, ThumbNotificationBox aBox,String aExplanationText) {
		super(aExplanationText);
		setCursor(
				Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		itsBox = aBox;
		itsImage = aImage;
		itsLabel = new JLabel(new ImageIcon(aImage)) {
			@Override
			public void paint(Graphics aG) {
				super.paint(aG);
				paintMarquee((Graphics2D)aG);
			}
		};
		getContentPane().add(itsLabel);	
		itsLabel.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {
				startMarquee(e);
			}
			public void mouseReleased(MouseEvent e) {
				stopMarquee(e);
			}
			
		});
		itsLabel.addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {
				drawMarquee(e);
			}
			public void mouseMoved(MouseEvent e) {}
			});	
		pack();
		setAlwaysOnTop(true);
		setVisible(true);	
	}
	
	/**
	 * start the marquee selection
	 * 
	 */
	public void startMarquee(MouseEvent e) {
		itsStart = e.getPoint();
	}
	
	/**
	 * stop the marquee selection and give the selection 
	 * to the thumbmanager
	 */
	public void stopMarquee(MouseEvent e) {
		itsStop = e.getPoint();
		int minX = (int) Math.min(itsStart.getX(),itsStop.getX());
		int minY = (int)Math.min(itsStart.getY(),itsStop.getY());
		int maxX = (int)Math.max(itsStart.getX(),itsStop.getX());
		int maxY = (int)Math.max(itsStart.getY(),itsStop.getY());		
		//FIXME pblm shift in screenshot
		itsBox.push(itsImage.getSubimage(minX, minY+ITSShift, maxX-minX,maxY-minY+ITSShift));
		setVisible(false);
		dispose();
	}
	
	public void drawMarquee(MouseEvent e) {
		itsStop= e.getPoint();
		itsLabel.repaint();
	}
	
	public void paintMarquee(Graphics2D g) {
		if (itsStart !=null && itsStop!=null) {
			int minX = (int)Math.min(itsStart.getX(),itsStop.getX());
			int minY = (int)Math.min(itsStart.getY(),itsStop.getY());
			int maxX = (int)Math.max(itsStart.getX(),itsStop.getX());
			int maxY = (int)Math.max(itsStart.getY(),itsStop.getY());
			g.setStroke(new BasicStroke(2,
					BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1f,
					new float[] { 5, 3 }, 0));
			g.drawRect(minX,minY, maxX-minX,maxY-minY);
			//g.drawString(""+minX+","+minY+","+ (maxX-minX)+","+(maxY-minY),10,10);
		}
	}
	
}
