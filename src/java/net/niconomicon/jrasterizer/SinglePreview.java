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
	final int imgWidth;
	final int imgHeight;
	final int resolution;
	final int page;
	final int maxPage;
	final int extractSide;

	public SinglePreview(BufferedImage img, int page, int pages, int resolution, int extractSide, int width, int height) {
		super(new BorderLayout());
		this.extract = img;
		this.resolution = resolution;
		this.page = page;
		this.maxPage = pages;
		this.extractSide = extractSide;
		this.imgWidth = width;
		this.imgHeight = height;
		init();
	}

	private void init() {
		this.setPreferredSize(new Dimension(extractSide, extractSide));

		DisplayJAI d = new DisplayJAI();
		d.set(extract);

		JPanel labels = new JPanel(new GridBagLayout());
		JLabel l;
		GridBagConstraints c;

		l = new JLabel("Page");
		c = new GridBagConstraints();
		c.gridy = 0;
		c.gridx = 0;
		c.anchor = GridBagConstraints.NORTHWEST;
		labels.add(l, c);

		l = new JLabel("Resolution");
		c = new GridBagConstraints();
		c.gridy = 1;
		c.gridx = 0;
		c.anchor = GridBagConstraints.NORTHWEST;
		labels.add(l, c);

		l = new JLabel("Dimensions");
		c = new GridBagConstraints();
		c.gridy = 2;
		c.gridx = 0;
		c.anchor = GridBagConstraints.NORTHWEST;
		labels.add(l, c);

		l = new JLabel(": " + page + " /" + maxPage);
		c = new GridBagConstraints();
		c.gridy = 0;
		c.gridx = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		labels.add(l, c);

		l = new JLabel(": ~ " + resolution + " dpi");
		c = new GridBagConstraints();
		c.gridy = 1;
		c.gridx = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		labels.add(l, c);

		l = new JLabel(": " + imgWidth + "x" + imgHeight + " px");
		c = new GridBagConstraints();
		c.gridy = 2;
		c.gridx = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		labels.add(l, c);

		labels.setBackground(new Color(192, 192, 192, 192));

		labels.revalidate();

		// Boundaries
		d.setBounds(1, 1, extractSide, extractSide);

		int lh = 60;
		int ly = extractSide - lh;
		labels.setBounds(1, ly + 1, extractSide, lh);

		JLabel background = new JLabel(" ");
		background.setBackground(Color.black);
		background.setForeground(Color.black);
		background.setBounds(0, 0, 202, 202);
		background.setOpaque(true);
		JLayeredPane pane = new JLayeredPane();
		pane.add(background, new Integer(1));
		pane.add(d, new Integer(2));
		pane.add(labels, new Integer(3));

		this.add(pane, BorderLayout.CENTER);
	}

}
