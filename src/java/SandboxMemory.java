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
		PDFToImageRendererPixels ren = new PDFToImageRendererPixels();
		ren.setPDFFromFile(pdf);
		int pages = ren.getPageCount();
		int side = 1700;
		System.out.println("Page sizes for max size = " + side);
		for (int i = 1; i <= pages; i++) {
			Dimension d = ren.getImageDimForSideLength(i, side);
			System.out.println("Page : " + i + " dimensions : " + d.width + " x " + d.height + " = " + (d.width * d.height) / (1000 * 1000) + " megapixels");
			d = null;
		}
		for (int n = 0; n < 50; n++) {
			for (int i = 1; i <= pages; i++) {
				BufferedImage img = ren.getExtract(i, side, 200);
				img=null;
				System.gc();
			}
			Thread.sleep(1000);
			System.gc();
			Thread.sleep(1000);
			System.out.print("Iteration : " + n + " Memory infos : ");
			TestMemory.printMemoryInfo();
		}
	}
}
