/**
 * 
 */
package net.niconomicon.jrasterizer.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.niconomicon.jrasterizer.PDFToImageRendererPixels;
import net.niconomicon.jrasterizer.utils.TestMemory;

/**
 * @author Nicolas Hoibian
 * 
 */
public class Previewer extends JPanel {

	public int extractSide = 200;
	public static final int LIMIT = 10000;
//	public static final int[] sizes = new int[] { 500, 1000, 2000, 4000 };// , 6000, 9000 };
	public static final int[] sizes = new int[] { 4000, 2000, 1000, 500 };// , 6000, 9000 };
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
		PDFToImageRendererPixels renderer;
		renderer = new PDFToImageRendererPixels(pdffile);

		int maxPage = renderer.getPageCount();

		setPrefSize(maxPage);
		this.getParent().validate();

		this.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.gridwidth = sizes.length;
		c.gridy = maxPage;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.REMAINDER;
		c.anchor = GridBagConstraints.PAGE_END;
		JLabel l = new JLabel("  ");
		l.setSize(extractSide * sizes.length, 1);
		l.setMinimumSize(new Dimension(extractSide * (sizes.length + 1), 1));

		this.add(l, c);

		this.revalidate();

		for (int page = 1; page <= maxPage; page++) {
			// for (int step = sizes.length - 1; step >= 0; step--) {
			for (int step = 0; step < sizes.length; step++) {
				int side = sizes[step];
				Dimension d = renderer.getImageDimForSideLength(page, side);
				// System.out.print("Page " + page + " - Trying to get the extract for dim : " + d + " ...");
				SinglePreview pre;
				BufferedImage img = renderer.getExtract(page, side, extractSide);
				pre = new SinglePreview(img, page, maxPage, d, gui);
				c = new GridBagConstraints();
				// c.gridx = sizes.length - step - 1;
				c.gridx = step;
				c.gridy = page - 1;
				c.fill = GridBagConstraints.NONE;
				c.anchor = GridBagConstraints.FIRST_LINE_START;
				this.add(pre, c);
				this.revalidate();
			}
			Dimension d = renderer.getImageDimForSideLength(1, sizes[0]);
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
			TestMemory.printMemoryInfo();
			if (TestMemory.getAvailableMemory() < 0.20) {
				System.out.println("Cleaning up the renderer.");
				renderer = null;
				try {
					Thread.sleep(1000);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				System.gc();
				renderer = new PDFToImageRendererPixels(pdffile);
				TestMemory.printMemoryInfo();
			}
		}
		this.revalidate();
	}
}
