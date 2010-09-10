/**
 * 
 */
package net.niconomicon.jrasterizer;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import com.sun.media.jai.widget.DisplayJAI;

/**
 * @author Nicolas Hoibian
 * 
 */
public class Previewer extends JPanel {

	public int step_dpi = 66;
	public int step_count = 5;

	public int extract_side = 200;
	public static final int LIMIT = 4000;

	public Previewer() {
		super();
		this.setPreferredSize(new Dimension((extract_side + 2) * step_count, 420));
	}

	public void setPDFToPreview(PDFToImageRenderer renderer) {
		this.removeAll();
		this.revalidate();
		int n = this.getComponentCount();
		for (int i = 0; i < n; i++) {
			this.getComponent(i).setVisible(false);
		}
		this.repaint();
		this.setLayout(new GridLayout(0, step_count));
		int maxPage = renderer.getPageCount();
		for (int page = 1; page <= maxPage; page++) {
			for (int step = 1; step <= step_count; step++) {
				int res = step * step_dpi;
				System.out.println("Trying to get the extract at resolution : " + res);

				Dimension d = renderer.getImageDimForResolution(1, res);
				SinglePreview pre;
				if (d.getHeight() >= LIMIT || d.getWidth() >= LIMIT) {
					System.out.println(d);
					pre = new SinglePreview(null, page, maxPage, res, extract_side, d.width, d.height);
				} else {
					BufferedImage img = renderer.getExtract(page, res, extract_side);
					System.out.println("icon infos : " + img.getHeight() + " by " + img.getWidth());
					pre = new SinglePreview(img, page, maxPage, res, extract_side, d.width, d.height);
				}
				this.add(pre);
				this.revalidate();
			}
		}
		this.revalidate();
	}
}
