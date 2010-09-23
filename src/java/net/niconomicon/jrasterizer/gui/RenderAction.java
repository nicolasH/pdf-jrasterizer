/**
 * 
 */
package net.niconomicon.jrasterizer.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import net.niconomicon.jrasterizer.PDFToImageRendererPixels;

/**
 * @author Nicolas Hoibian
 * 
 */
public class RenderAction implements Runnable, ActionListener {

	public void actionPerformed(ActionEvent e) {
		Thread imageRendererThread = new Thread(this);
		imageRendererThread.start();
	}

	File file;
	int side;
	int page;
	PDFRasterizerGUI gui;

	public RenderAction(int side, int page, PDFRasterizerGUI gui) {
		this.gui = gui;
		this.file = gui.getCurrentFile();
		this.side = side;
		this.page = page;
	}

	public void run() {
		try {
			BufferedImage image = PDFToImageRendererPixels.getImageFromPDFAtLocation(file.getAbsolutePath(), page, side);
			gui.setImage(image);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
