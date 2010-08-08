package net.niconomicon.jrasterizer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import com.sun.media.jai.widget.DisplayJAI;

/**
 * @author Nicolas Hoibian copyright August 2010
 * 
 */

public class PDFRasterizerGUI {
	JFrame frame;

	public static final String USER_HOME = "user.home";

	JPanel contentPane;

	DisplayJAI jaiPanel;

	SavePanel savePanel;
	OpenPanel openPanel;

	BufferedImage img;

	public PDFRasterizerGUI() {

		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		contentPane = new JPanel(new BorderLayout());

		openPanel = new OpenPanel(this);
		savePanel = new SavePanel(this);

		jaiPanel = new DisplayJAI();
		JScrollPane sp = new JScrollPane(jaiPanel);

		JTabbedPane p = new JTabbedPane();
		p.add(openPanel.contentPane);
		p.add(savePanel.contentPane);
		contentPane.add(p, BorderLayout.NORTH);
		contentPane.add(sp, BorderLayout.CENTER);
		contentPane.setPreferredSize(new Dimension(700, 600));
		frame.setContentPane(contentPane);

		frame.pack();
		frame.setVisible(true);

	}

	public void setPDFFile(File f) {
		savePanel.setCurrentFile(f);
	}

	public void setImage(BufferedImage image) {
		this.img = image;
		jaiPanel.set(this.img);
	}

	public BufferedImage getImage() {
		return img;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PDFRasterizerGUI rast = new PDFRasterizerGUI();
	}

}
