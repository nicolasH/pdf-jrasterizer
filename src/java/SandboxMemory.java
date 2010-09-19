import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;

import net.niconomicon.jrasterizer.PDFToImageRendererPixels;

/**
 * 
 */

/**
 * @author niko
 * 
 */
public class SandboxMemory {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		File pdf = new File(args[0]);
		System.out.println("Selected pdf on which to run the test : " + args[0]);
		int side = 1700;
		System.out.println("Page sizes for max size = " + side);
		for (int n = 1; n < 50; n++) {
			PDFToImageRendererPixels ren = new PDFToImageRendererPixels();
			ren.setPDFFromFile(pdf);
			int pages = ren.getPageCount();
			for (int i = 1; i <= pages; i++) {
				BufferedImage img = ren.getExtract(i, side, 200);
				img=null;
				System.gc();
			}
			ren = null;
			Thread.sleep(1000);
			System.gc();
			Thread.sleep(1000);
			System.out.print("Iteration : " + n + " Memory infos : ");
			TestMemory.printMemoryInfo();
		}
	}
}
