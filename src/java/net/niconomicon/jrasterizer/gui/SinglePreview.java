/**
 * 
 */
package net.niconomicon.jrasterizer.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

/**
 * @author niko
 * 
 */
public class SinglePreview extends JPanel {
	BufferedImage extract;
	final Dimension imageSize;
	// final int resolution;
	final int page;
	final int maxPage;
	final int extractSide;
	final PDFRasterizerGUI gui;
	final static int labelPanelHeigth = 60;

	public SinglePreview(BufferedImage img, int page, int pages, Dimension imageFullSize, PDFRasterizerGUI gui) {
		super(new BorderLayout());
		this.extract = img;
		// this.resolution = resolution;
		this.page = page;
		this.maxPage = pages;
		this.imageSize = imageFullSize;
		this.gui = gui;
		this.extractSide = img.getRaster().getBounds().width;
		init();
	}

	public static Dimension getFuturDims(int extractSize) {
		return new Dimension(extractSize + 2, extractSize + 2 + labelPanelHeigth);
	}

	private void init() {
		this.setPreferredSize(new Dimension(extractSide, extractSide));

		Color transparentBackground = new Color(150, 150, 150, 192);

		JButton b = new JButton("view");
		b.setOpaque(false);

		b.addActionListener(new RenderAction(Math.max(imageSize.height, imageSize.width), null, page, gui));
		JPanel labels = new JPanel(new GridBagLayout());
		JLabel l;
		GridBagConstraints c;

		int y = 0;
		l = new JLabel("Page");
		c = new GridBagConstraints();
		c.gridy = y++;
		c.gridx = 0;
		c.anchor = GridBagConstraints.NORTHWEST;
		labels.add(l, c);

		l = new JLabel("Dimensions");
		c = new GridBagConstraints();
		c.gridy = y++;
		c.gridx = 0;
		c.anchor = GridBagConstraints.NORTHWEST;
		labels.add(l, c);

		c = new GridBagConstraints();
		c.gridy = y++;
		c.gridx = 0;
		c.anchor = GridBagConstraints.NORTHWEST;
		labels.add(b, c);

		y = 0;
		l = new JLabel(": " + page + " /" + maxPage);
		c = new GridBagConstraints();
		c.gridy = y++;
		c.gridx = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		labels.add(l, c);

		l = new JLabel(": " + imageSize.width + "x" + imageSize.height + " px");
		c = new GridBagConstraints();
		c.gridy = y++;
		c.gridx = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		labels.add(l, c);

		b = new JButton("save");
		b.addActionListener(new SaveAction());
		c = new GridBagConstraints();
		c.gridy = y++;
		c.gridx = 1;
		c.anchor = GridBagConstraints.NORTHEAST;
		labels.add(b, c);

		labels.setBackground(new Color(150, 150, 150, 255));

		labels.revalidate();

		// Boundaries

		int ly = extractSide - labelPanelHeigth;
		labels.setBounds(1, ly + 1, extractSide, labelPanelHeigth);

		JLabel background = new JLabel(" ");
		background.setBackground(Color.black);
		background.setForeground(Color.black);
		background.setBounds(0, 0, 202, 202);
		background.setOpaque(true);

		JPanel image = new BackgroundPanel();
		image.setLayout(new BorderLayout());
		if (extract == null) {
			l = new JLabel("Too big to preview");
			image.add(l, BorderLayout.NORTH);
			image.setOpaque(true);
		} else {
			image.setOpaque(false);
		}
		image.setBounds(0, 0, extractSide, extractSide);
		JLayeredPane pane = new JLayeredPane();
		pane.add(background, new Integer(1));
		pane.add(image, new Integer(2));
		pane.add(labels, new Integer(3));

		this.add(pane, BorderLayout.CENTER);
		this.setBorder(BorderFactory.createLineBorder(Color.black));
	}

	public class BackgroundPanel extends JPanel {
		protected void paintComponent(Graphics g) {
			g.drawImage(extract, 0, 0, extractSide, extractSide, null);
			super.paintComponent(g);
		}

	}

	public class SaveAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			gui.saveDialog.save(page, maxPage, imageSize);
		}
	}
}
