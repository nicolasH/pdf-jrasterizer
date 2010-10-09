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
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author niko
 * 
 */
public class SinglePreview extends JPanel {
	BufferedImage extract;
	Dimension imageSize;
	// final int resolution;
	final int page;
	final int maxPage;
	final int extractSide;
	final PDFRasterizerGUI gui;
	JLabel dimensionLabel;
	final double ratio;
	int labelPanelHeigth = 60;
	final SpinnerNumberModel spinnerModel;

	public SinglePreview(BufferedImage img, int page, int pages, Dimension imageFullSize, int extractSide, double ratio, PDFRasterizerGUI gui) {
		super(new BorderLayout());
		this.extract = img;
		this.page = page;
		this.maxPage = pages;
		this.imageSize = imageFullSize;
		this.gui = gui;
		this.extractSide = extractSide;

		this.ratio = ratio;
		spinnerModel = new SpinnerNumberModel(5000, 500, 12000, 100);
		spinnerModel.addChangeListener(new UpdateResolutionAction());

		init();
	}

	private void init() {
		this.setPreferredSize(new Dimension(extractSide, extractSide));

		JButton b = new JButton("view");
		b.setOpaque(false);
		if (null == extract) {
			b.addActionListener(new RenderAction(-1, spinnerModel, page, gui));
		} else {
			b.addActionListener(new RenderAction(Math.max(imageSize.height, imageSize.width), null, page, gui));
		}
		JPanel labels = new JPanel(new GridBagLayout());
		JLabel l;
		GridBagConstraints c;

		JSpinner spinner = new JSpinner(spinnerModel);

		int y = 0;

		if (null == extract) {
			l = new JLabel("Choose size");
			c = new GridBagConstraints();
			c.gridy = y++;
			c.gridx = 0;
			c.anchor = GridBagConstraints.WEST;
			labels.add(l, c);
		}
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

		if (null == extract) {
			c = new GridBagConstraints();
			c.gridy = y++;
			c.gridx = 1;
			c.anchor = GridBagConstraints.WEST;
			labels.add(spinner, c);
		}
		l = new JLabel(": " + page + " /" + maxPage);
		c = new GridBagConstraints();
		c.gridy = y++;
		c.gridx = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		labels.add(l, c);

		if (null != imageSize) {
			dimensionLabel = new JLabel(": " + imageSize.width + "x" + imageSize.height + " px");
		} else {
			dimensionLabel = new JLabel(": " + spinnerModel.getNumber().intValue() + "x" + (int) (spinnerModel.getNumber().intValue() / ratio) + " px");
		}
		c = new GridBagConstraints();
		c.gridy = y++;
		c.gridx = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		labels.add(dimensionLabel, c);

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

		if (null == extract) {
			labelPanelHeigth = 87;
		} else {
			labelPanelHeigth = 60;
		}
		int ly = extractSide - labelPanelHeigth;
		labels.setBounds(0, ly, extractSide, labelPanelHeigth);

		JLabel background = new JLabel(" ");
		background.setBackground(Color.black);
		background.setForeground(Color.black);
		background.setBounds(0, 0, extractSide, extractSide);
		background.setOpaque(true);

		JPanel image = new BackgroundPanel();
		image.setLayout(new BorderLayout());
		if (extract == null) {
			l = new JLabel("<html><body><center><p>Choose the maximum side of the image then click 'view' to see it.</p></center></body><html>");
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

	private class UpdateResolutionAction implements ChangeListener {

		public void stateChanged(ChangeEvent e) {
			SwingUtilities.invokeLater(new UpdateResolutionLabel());
		}
	}

	private class UpdateResolutionLabel implements Runnable {
		public void run() {
			int maxSide = spinnerModel.getNumber().intValue();
			dimensionLabel.setText(": " + maxSide + "x" + (int) (maxSide / ratio) + " px");
			dimensionLabel.revalidate();
		}

	}

	public void setExtractImage(BufferedImage extract) {
		this.extract = extract;
		this.revalidate();
	}

	public class BackgroundPanel extends JPanel {
		protected void paintComponent(Graphics g) {
			g.drawImage(extract, 0, 0, extractSide, extractSide, null);
			super.paintComponent(g);
		}
	}

	public class SaveAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			gui.showSaveImageDialog(page, maxPage, imageSize);
		}
	}
}
