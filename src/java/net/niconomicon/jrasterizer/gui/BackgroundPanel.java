/**
 * 
 */
package net.niconomicon.jrasterizer.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * @author Nicolas Hoibian
 * 
 */
public class BackgroundPanel extends JPanel {
	BufferedImage image;

	public void setImage(BufferedImage image) {
		this.image = image;
		if (null != image) {
			Dimension d = new Dimension(image.getWidth(), image.getHeight());
			this.setSize(d);
			this.setPreferredSize(d);
			this.setMaximumSize(d);
			this.setMinimumSize(d);
			this.setBounds(0, 0, d.width, d.height);
		}
	}

	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		if (image != null) {
			g2d.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
		}
		super.paintComponent(g);
	}

}
