/**
 * 
 */
package net.niconomicon.jrasterizer.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
	public static final int[] sizes = new int[] { 1000, 3000, 6000, 9000 };

	PDFRasterizerGUI gui;
	String pdfFile;

	public Previewer(PDFRasterizerGUI gui) {
		super();
		this.gui = gui;
		setPrefSize(2);
	}

	private void setPrefSize(int lines) {
		this.setPreferredSize(new Dimension((extractSide + 2) * sizes.length, extractSide * lines));

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
		this.setLayout(new GridLayout(0, sizes.length));
		for (int page = 1; page <= maxPage; page++) {
			for (int step = sizes.length - 1; step >= 0; step--) {
				int side = sizes[step];
				Dimension d = renderer.getImageDimForSideLength(page, side);
				// System.out.print("Page " + page + " - Trying to get the extract for dim : " + d + " ...");
				SinglePreview pre;
				BufferedImage img = renderer.getExtract(page, side, extractSide);
				pre = new SinglePreview(img, page, maxPage, d, gui);
				this.add(pre);
				this.revalidate();
				// System.out.println("done");
			}

			renderer = null;
			try {
				Thread.sleep(1000);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			System.gc();
			if (maxPage > 1) {
				System.out.print("Page " + page + " - ");
				TestMemory.printMemoryInfo();
				renderer = new PDFToImageRendererPixels(pdffile);
				TestMemory.printMemoryInfo();
			}
		}

		this.revalidate();
	}
}
