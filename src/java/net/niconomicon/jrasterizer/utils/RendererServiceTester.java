/**
 * 
 */
package net.niconomicon.jrasterizer.utils;

import java.awt.image.BufferedImage;
import java.io.File;

import net.niconomicon.jrasterizer.RendererService;
import net.niconomicon.jrasterizer.RendererService.RASTERIZER_TYPE;

/**
 * @author Nicolas Hoibian
 * 
 */
public class RendererServiceTester {

	public static void main(String[] args) throws Exception {
		File f = new File("/Users/niko/02_ApplicationFlow.pdf");
		System.out.println("Initializing ...");
		RendererService service = RendererService.createService(RASTERIZER_TYPE.PIXELS, f);
		service.getImageDimensions(1, 500);
		int n = 50;
		System.out.println("Starting");
		int maxPage = service.getPageCount();
		for (int i = 0; i < n; i++) {
			for (int page = 1; page <= maxPage; page++) {
				BufferedImage img = service.getImageFromPDF(page, 10000);
				System.out.println("Iteration : " + i + " Page :" + page + " dims : " + img.getWidth() + " x " + img.getHeight() + " Available memory : " + TestMemory.getAvailableMemory());
				img = null;
				Thread.sleep(100);
			}
		}
		service.shouldStop = true;
	}
}
