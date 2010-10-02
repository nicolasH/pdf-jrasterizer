/**
 * 
 */
package net.niconomicon.jrasterizer.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.SpinnerNumberModel;

import net.niconomicon.jrasterizer.PDFToImageRenderer;
import net.niconomicon.jrasterizer.PDFToImageRenderer.UNIT;

/**
 * @author Nicolas Hoibian
 * 
 */
public class RenderAction implements Runnable, ActionListener {

	public void actionPerformed(ActionEvent e) {
		if (null != model) {
			side = model.getNumber().intValue();
		}
		Thread imageRendererThread = new Thread(this);
		imageRendererThread.start();
	}

	File file;
	int side = -1;
	int page;
	PDFRasterizerGUI gui;
	SpinnerNumberModel model;

	public RenderAction(int side, SpinnerNumberModel model, int page, PDFRasterizerGUI gui) {
		this.gui = gui;
		this.file = gui.getCurrentFile();
		this.side = side;
		this.page = page;
		this.model = model;
	}

	public void run() {
		try {
			BufferedImage image = PDFToImageRenderer.getImageFromPDFAtLocation(file.getAbsolutePath(), page, UNIT.PIXEL, side);
			gui.setImage(image);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
