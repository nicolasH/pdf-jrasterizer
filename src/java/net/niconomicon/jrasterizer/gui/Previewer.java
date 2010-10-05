/**
 * 
 */
package net.niconomicon.jrasterizer.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.niconomicon.jrasterizer.PDFToImage;
import net.niconomicon.jrasterizer.PDFToImageRenderer;
import net.niconomicon.jrasterizer.PDFToImageRenderer.UNIT;
import net.niconomicon.jrasterizer.utils.TestMemory;

/**
 * @author Nicolas Hoibian
 * 
 */
public class Previewer extends JPanel {

	public int extractSide = 200;
	public static final int LIMIT = 10000;
	public static final int[] sizes = new int[] { 500, 1000, 2000, 4000 };// , 6000, 9000 };
	public static final int defaultBiggerSize = 5000;
	// public static final int[] sizes = new int[] { 4000, 2000, 1000, 500 };// , 6000, 9000 };
	PDFRasterizerGUI gui;
	String pdfFile;

	public Previewer(PDFRasterizerGUI gui) {
		super();
		this.gui = gui;
		setPrefSize(2);
	}

	private void setPrefSize(int lines) {
		this.setPreferredSize(new Dimension((extractSide + 2) * (sizes.length + 1), (extractSide * lines) + 20));

	}

	public String getPDFToPreview() {
		return pdfFile;
	}

	public void setPDFToPreview(File pdffile) throws IOException {
		this.removeAll();
		this.revalidate();
		pdfFile = pdffile.getAbsolutePath();
		int n = this.getComponentCount();
		for (int i = 0; i < n; i++) {
			this.getComponent(i).setVisible(false);
		}
		this.repaint();

		int maxPage = gui.service.getPageCount();

		setPrefSize(maxPage);
		this.getParent().validate();

		this.setLayout(new GridBagLayout());

		GridBagConstraints c;
		// spacer bottom.
		c = new GridBagConstraints();
		c.gridwidth = sizes.length;
		c.gridy = maxPage;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.REMAINDER;
		c.anchor = GridBagConstraints.PAGE_END;
		JLabel l = new JLabel("  ");
		l.setSize(extractSide * sizes.length, 1);
		l.setMinimumSize(new Dimension(extractSide * (sizes.length + 1), 1));
		this.add(l, c);

		// spacer right
		c = new GridBagConstraints();
		c.gridx = sizes.length + 1;
		c.gridwidth = 1;
		c.gridheight = maxPage;
		c.gridy = 0;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.REMAINDER;
		c.anchor = GridBagConstraints.PAGE_END;
		l = new JLabel("  ");
		l.setSize(1, extractSide * maxPage);
		l.setMinimumSize(new Dimension(1, extractSide * maxPage));
		this.add(l, c);

		this.revalidate();

		for (int page = 1; page <= maxPage; page++) {
			for (int step = 0; step < sizes.length; step++) {
				int side = sizes[step];
				Dimension d = gui.service.getImageDimensions(page, side);
				System.out.print("Page " + page + " - Trying to get the extract for dim : " + d + " ...");
				SinglePreview pre;
				BufferedImage img = gui.service.getExtract(page, side, extractSide);
				pre = new SinglePreview(img, page, maxPage, d, gui);
				c = new GridBagConstraints();
				c.gridx = step;
				c.gridy = page - 1;
				c.fill = GridBagConstraints.NONE;
				c.anchor = GridBagConstraints.NORTHWEST;
				System.out.println("imageDim : " + img.getWidth() + " by " + img.getHeight() + " pre : " + c.gridx + "," + c.gridy + " size :" + pre.getSize() + " " + pre.getPreferredSize());
				this.add(pre, c);
				this.revalidate();
			}
			Dimension d = gui.service.getImageDimensions(page, defaultBiggerSize);
			double ratio = (double) (double) d.width / (double) d.height;
			SinglePreviewSizeChooser choo = new SinglePreviewSizeChooser(page, maxPage, extractSide, ratio, gui);
			c = new GridBagConstraints();
			c.gridx = sizes.length;
			c.gridy = page - 1;
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			this.add(choo, c);
			this.revalidate();
			System.out.print("Page " + page + " - ");
		}
		this.revalidate();
	}
}
