/**
 * 
 */
package net.niconomicon.jrasterizer.gui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import net.niconomicon.jrasterizer.PDFToImageRenderer;
import net.niconomicon.jrasterizer.PDFToImageRendererPixels;
import net.niconomicon.jrasterizer.utils.TestMemory;

/**
 * @author Nicolas Hoibian
 * 
 */
public class PDFRasterizerSingleton {

	PDFToImageRenderer renderer;
	File pdffile;
	static PDFRasterizerSingleton singleton;

	public BufferedImage getImage(int page, int dpi) {

		// Dimension d = renderer.getImageDimForSideLength(page, 0);
		// BufferedImage img = renderer.getExtract(page, side, extractSide);
		// Dimension d = renderer.getImageDimForSideLength(1, 500);
		System.out.print("Page " + page + " - ");
		TestMemory.printMemoryInfo();
		if (TestMemory.getAvailableMemory() < 0.20) {
			System.out.println("Cleaning up the renderer.");
			renderer = null;
			System.gc();
			try {
				renderer = new PDFToImageRendererPixels(pdffile);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			TestMemory.printMemoryInfo();
		}
		return null;
	}

	public enum RASTERIZER_TYPE {
		DPI, PIXELS
	};

	public static PDFRasterizerSingleton getInstance(RASTERIZER_TYPE type, File pdf) {
		singleton = new PDFRasterizerSingleton();
		return singleton;
	}
}
