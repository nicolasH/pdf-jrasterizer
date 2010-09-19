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
		int side = 12000;
		System.out.println("Page sizes for max size = " + side);
		PDFToImageRendererPixels ren;
		int pages = 0;
		for (int n = 1; n < 10; n++) {
			if (n == 1) {
				ren = new PDFToImageRendererPixels();
				ren.setPDFFromFile(pdf);
				pages = ren.getPageCount();
				Dimension d = ren.getImageDimForSideLength(1, side);
				System.out.println("Dimension : " + d.width + " * " + d.height + " MP : " + ((double) (d.width * d.height)) / (1000.0 * 1000.0));
			}
			ren = null;
			for (int i = 1; i <= pages; i++) {
				ren = new PDFToImageRendererPixels();
				ren.setPDFFromFile(pdf);
				BufferedImage img = ren.getExtract(i, side, 200);
				img = null;
				System.out.print("page : " + i + " Before reset : Memory infos : ");
				TestMemory.printMemoryInfo();
				ren = null;
				System.gc();
				Thread.sleep(1000);
				System.gc();
				System.out.print("page : " + i + " After reset  : Memory infos : ");
				TestMemory.printMemoryInfo();
			}
			ren = null;
			Thread.sleep(1000);
			System.gc();
			Thread.sleep(1000);
			System.out.print("After iteration : " + n + " Memory infos : ");
			TestMemory.printMemoryInfo();
		}
	}
}
