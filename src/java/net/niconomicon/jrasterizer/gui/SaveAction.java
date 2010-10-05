/**
 * 
 */
package net.niconomicon.jrasterizer.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.SpinnerNumberModel;

import net.niconomicon.jrasterizer.PDFToImageRenderer;
import net.niconomicon.jrasterizer.PDFToImageRenderer.UNIT;

/**
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

	public SaveAction(int page, int info, String format, String finalName, PDFRasterizerGUI gui) {
		this.gui = gui;
		this.file = finalName;
		this.info = info;
		this.imageFormat = format;
		this.page = page;
	}

	public void run() {
		System.out.println("saving image for page "+page);
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
