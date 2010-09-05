/**
 * 
 */
package net.niconomicon.jrasterizer;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author Nicolas Hoibian
 * 
 */
public class Previewer extends JPanel {

	public Previewer() {
		super();
		this.setLayout(new GridLayout(2, 0));
		this.setPreferredSize(new Dimension(400, 400));
	}

	public void setPDFToPreview(PDFToImageRenderer renderer) {
		this.removeAll();
		for (int i = 50; i <= 300; i += 50) {
			System.out.println("Trying to get the extract at resolution : " + i);
			ImageIcon ic = new ImageIcon(renderer.getExtract(1, i, 200));
			System.out.println("icon infos : " + ic.getIconHeight() + " by " + ic.getIconWidth());
			JLabel l = new JLabel("Resolution : " + i);
			this.add(l);

		}
		this.revalidate();
	}
}
