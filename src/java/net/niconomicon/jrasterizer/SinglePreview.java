/**
 * 
 */
package net.niconomicon.jrasterizer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import com.sun.media.jai.widget.DisplayJAI;

/**
 * @author niko
 * 
 */
public class SinglePreview extends JPanel {
	BufferedImage extract;
	int imgWidth;
	int imgHeight;
	int resolution;

	public SinglePreview(BufferedImage img, int resolution, int width, int height) {
		super(new BorderLayout());
		this.extract = img;
		this.resolution = resolution;
		this.imgWidth = width;
		this.imgHeight = height;
		init();
		this.setPreferredSize(new Dimension(202, 202));
	}

	private void init() {
		DisplayJAI d = new DisplayJAI();
		d.set(extract);

		JPanel labels = new JPanel(new GridBagLayout());
		JLabel l;
		GridBagConstraints c;

		labels.setOpaque(false);
		l = new JLabel("Resolution : ");
		c = new GridBagConstraints();
		c.gridy = 0;
		c.gridx = 0;
		c.anchor = GridBagConstraints.NORTHEAST;
		labels.add(l, c);

		l = new JLabel("Dimensions : ");
		c = new GridBagConstraints();
		c.gridy = 1;
		c.gridx = 0;
		c.anchor = GridBagConstraints.NORTHEAST;
		labels.add(l, c);

		l = new JLabel(" ~ " + resolution + " dpi");
		c = new GridBagConstraints();
		c.gridy = 0;
		c.gridx = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		labels.add(l, c);

		l = new JLabel(imgWidth + "x" + imgHeight + " pixels");
		c = new GridBagConstraints();
		c.gridy = 1;
		c.gridx = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		labels.add(l, c);

		l = new JLabel("Extract (200x200 pixels) : ");
		c = new GridBagConstraints();
		c.gridy = 2;
		c.gridx = 0;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.CENTER;
		labels.add(l, c);

		labels.revalidate();
		// this.add(d, Integer.valueOf(1));
		// this.add(labels, Integer.valueOf(2));
		// this.revalidate();
		this.add(d, BorderLayout.CENTER);
		this.add(labels, BorderLayout.NORTH);

	}

}
