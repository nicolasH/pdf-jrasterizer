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

	public Previewer() {
		super();
		this.setLayout(new GridLayout(0, 6));
		this.setPreferredSize(new Dimension(810, 420));
	}

	public void setPDFToPreview(PDFToImageRenderer renderer) {
		this.removeAll();
		this.setLayout(new GridLayout(0, 4));
		for (int i = 50; i <= 200; i += 50) {
			System.out.println("Trying to get the extract at resolution : " + i);
			BufferedImage img = renderer.getExtract(1, i, 200);
			System.out.println("icon infos : " + img.getHeight() + " by " + img.getWidth());
			Dimension d = renderer.getImageDimForResolution(1, i);
			System.out.println(d);
			SinglePreview pre = new SinglePreview(img, i, d.width, d.height);
			this.add(pre);
			this.revalidate();

		}
		this.revalidate();
	}
}
