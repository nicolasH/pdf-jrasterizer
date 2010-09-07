/**
 * 
 */
package net.niconomicon.jrasterizer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.sun.media.jai.widget.DisplayJAI;

/**
 * @author niko
 * 
 */
public class SinglePreview extends JPanel {
	BufferedImage extract;
	int width;
	int height;
	int resolution;

	public SinglePreview(BufferedImage img, int resolution, int width, int height) {
		super(new GridBagLayout());
		this.extract = img;
		this.resolution = resolution;
		this.width = width;
		this.height = height;
		init();
	}

	private void init() {
		DisplayJAI d = new DisplayJAI();
		d.set(extract);

		JLabel l;
		// this.setLayout(new GridBagLayout());
		GridBagConstraints c;

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 5;
		this.add(d, c);

		l = new JLabel("Resolution :");
		c = new GridBagConstraints();
		c.gridy = 0;
		c.gridx = 1;
		this.add(l, c);

		l = new JLabel(" ");
		c = new GridBagConstraints();
		c.gridy = 1;
		c.gridx = 1;
		this.add(l, c);

		l = new JLabel("Dimensions :");
		c = new GridBagConstraints();
		c.gridy = 2;
		c.gridx = 1;
		this.add(l, c);

		l = new JLabel("~ " + resolution + " dpi");
		c = new GridBagConstraints();
		c.gridy = 0;
		c.gridx = 2;
		this.add(l, c);

		l = new JLabel(" ");
		c = new GridBagConstraints();
		c.gridy = 1;
		c.gridx = 2;
		this.add(l, c);

		l = new JLabel(width + "x" + height);
		c = new GridBagConstraints();
		c.gridy = 2;
		c.gridx = 2;
		this.add(l, c);
		
		this.revalidate();
	}

}
