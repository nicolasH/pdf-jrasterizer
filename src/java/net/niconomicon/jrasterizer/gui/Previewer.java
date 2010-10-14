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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.JLabel;
import javax.swing.JPanel;

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

	Executor previewQueue;

	public Previewer(PDFRasterizerGUI gui) {
		super();
		this.gui = gui;
		previewQueue = Executors.newSingleThreadExecutor();
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
		
		if (gui.service == null) { return; }

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
		ErrorReporter.displayError("Creating PDF extracts at different sizes...");
		for (int page = 1; page <= maxPage; page++) {
			System.out.println("Page " + page + " ...");
			Dimension d = gui.service.getImageDimensions(page, defaultBiggerSize);
			double ratio = (double) (double) d.width / (double) d.height;
			SinglePreview choo = new SinglePreview(null, page, maxPage, null, extractSide, ratio, gui);
			c = new GridBagConstraints();
			c.gridx = 0;// sizes.length;
			c.gridy = page - 1;
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			this.add(choo, c);
			this.revalidate();
			for (int step = 0; step < sizes.length; step++) {
				previewQueue.execute(new PostPonner(page, step, maxPage));
			}
		}
		this.revalidate();
		previewQueue.execute(new Runnable() {
			public void run() {
				ErrorReporter.displayError("Click on 'view' to see the full page renderered at that size.");
			}
		});
	}

	public class PostPonner implements Runnable {

		int x;
		int maxPage;
		int page;

		public PostPonner(int page, int x, int maxPage) {
			this.page = page;
			this.maxPage = maxPage;
			this.x = x;
		}

		public void run() {
			Dimension d;
			int side = sizes[x];
			d = gui.service.getImageDimensions(page, side);
			SinglePreview pre;
			BufferedImage img = gui.service.getExtract(page, side, extractSide);
			pre = new SinglePreview(img, page, maxPage, d, extractSide, 0, gui);
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = x + 1;
			c.gridy = page - 1;
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.NORTHWEST;
			add(pre, c);
			revalidate();
		}
	}
}
