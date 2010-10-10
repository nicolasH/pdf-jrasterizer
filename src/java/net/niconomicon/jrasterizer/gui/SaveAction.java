/**
 * 
 */
package net.niconomicon.jrasterizer.gui;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.SpinnerNumberModel;

/**
 * This Runnable saves the image of a pdf page to a file.
 * 
 * @author Nicolas Hoibian
 * 
 */
public class SaveAction implements Runnable {

	String file;
	String imageFormat;
	int info;
	int page;

	PDFRasterizerGUI gui;
	SpinnerNumberModel model;

	/**
	 * @param page the page that will be rendered
	 * @param info either the desired DPI or maximum dimension of the page
	 * @param format the desired file format of the final image
	 * @param finalName the name of the file to write the image to
	 * @param gui a link to the gui instance, which hold a reference to the current PDF and PDF RendererService
	 */
	public SaveAction(int page, int info, String format, String finalName, PDFRasterizerGUI gui) {
		this.gui = gui;
		this.file = finalName;
		this.info = info;
		this.imageFormat = format;
		this.page = page;
	}

	public void run() {
		System.out.println("saving image for page " + page);
		try {
			BufferedImage img = gui.service.getImageFromPDF(page, info);
			try {
				File dest = new File(file);
				ImageIO.write(img, imageFormat, dest);

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
