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

	public int stepDpi = 66;
	public int stepCount = 5;

	public int extractSide = 200;
	public static final int LIMIT = 4000;

	public Previewer() {
		super();
		this.setPreferredSize(new Dimension((extractSide + 2) * stepCount, extractSide));
	}

	public void setPDFToPreview(PDFToImageRendererDPI renderer) {
		this.removeAll();
		this.revalidate();
		int n = this.getComponentCount();
		for (int i = 0; i < n; i++) {
			this.getComponent(i).setVisible(false);
		}
		this.repaint();
		int maxPage = renderer.getPageCount();
		this.setPreferredSize(new Dimension((extractSide + 2) * stepCount, extractSide * maxPage));
		this.getParent().validate();
		
		this.setLayout(new GridLayout(0, stepCount));
		for (int page = 1; page <= maxPage; page++) {
			for (int step = 1; step <= stepCount; step++) {
				int res = step * stepDpi;
				System.out.println("Trying to get the extract at resolution : " + res);

				Dimension d = renderer.getImageDimForResolution(1, res);
				SinglePreview pre;
				if (d.getHeight() >= LIMIT || d.getWidth() >= LIMIT) {
					System.out.println(d);
					pre = new SinglePreview(null, page, maxPage, res, extractSide, d.width, d.height);
				} else {
					BufferedImage img = renderer.getExtract(page, res, extractSide);
					System.out.println("icon infos : " + img.getHeight() + " by " + img.getWidth());
					pre = new SinglePreview(img, page, maxPage, res, extractSide, d.width, d.height);
				}
				this.add(pre);
				this.revalidate();
			}
		}
		this.revalidate();
	}
}
