/**
 * 
 */
package net.niconomicon.jrasterizer;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import net.niconomicon.jrasterizer.PDFToImageRenderer.UNIT;
import net.niconomicon.jrasterizer.utils.TestMemory;

/**
 * This class triggers the flushPage for each page when the available memory gets under 40 percent of the original
 * available memory.
 * 
 * @author Nicolas Hoibian
 * 
 */
public class RendererService implements PDFToImage {

	public static final long CHECK_INTERVAL = 50;
	PDFToImage renderer;
	File pdffile;

	RASTERIZER_TYPE type;
	double referenceRez;
	PDFToImageRenderer current;
	Thread switcherThread;

	public boolean shouldStop = false;

	Object lock;

	public enum RASTERIZER_TYPE {
		DPI, PIXELS
	};

	private RendererService(RASTERIZER_TYPE type, File pdf)throws IOException {
		this.type = type;
		pdffile = pdf;
		lock = new Object();
		switcherThread = new Thread(new RendererSwitcher());
		switcherThread.start();
	}

	public static RendererService createService(RASTERIZER_TYPE type, File pdf) throws IOException{
		return new RendererService(type, pdf);
	}

	/**
	 * @see net.niconomicon.jrasterizer.PDFToImage#setReferenceResolution(double)
	 */
	public void setReferenceResolution(double ref) {
		this.referenceRez = ref;
		synchronized (lock) {
			current.setReferenceResolution(ref);
		}
	}

	/**
	 * @see net.niconomicon.jrasterizer.PDFToImage#getFileLocation()
	 */
	public String getFileLocation() {
		return pdffile.getAbsolutePath();
	}

	/**
	 * @see net.niconomicon.jrasterizer.PDFToImage#setPDFFromFileLocation(java.lang.String)
	 */
	public void setPDFFromFileLocation(String pdfLocation) throws IOException {
		this.pdffile = new File(pdfLocation);
		synchronized (lock) {
			current.setPDFFromFileLocation(pdfLocation);
		}
	}

	/**
	 * @see net.niconomicon.jrasterizer.PDFToImage#setPDFFromFile(java.io.File)
	 */
	public void setPDFFromFile(File pdfLocation) throws IOException {
		this.pdffile = pdfLocation;
		synchronized (lock) {
			current.setPDFFromFile(pdfLocation);

		}
	}

	/**
	 * @see net.niconomicon.jrasterizer.PDFToImage#getPageCount()
	 */
	public int getPageCount() {
		synchronized (lock) {
			return current.getPageCount();
		}
	}

	/**
	 * @see net.niconomicon.jrasterizer.PDFToImage#getImageDimensions(int, int)
	 */
	public Dimension getImageDimensions(int pageNum, int info) {
		synchronized (lock) {
			return current.getImageDimensions(pageNum, info);
		}
	}

	/**
	 * @see net.niconomicon.jrasterizer.PDFToImage#getImageFromPDF(int, int)
	 */
	public BufferedImage getImageFromPDF(int pageNum, int info) {
		synchronized (lock) {
			return current.getImageFromPDF(pageNum, info);
		}
	}

	/**
	 * @see net.niconomicon.jrasterizer.PDFToImage#getExtract(int, int, int)
	 */
	public BufferedImage getExtract(int pageNum, int info, int clipSize) {
		synchronized (lock) {
			return current.getExtract(pageNum, info, clipSize);
		}
	}

	public void flushPage(int page) {
		synchronized (current) {
			current.flushPage(page);
		}
	}

	public void flushAllPages() {
		synchronized (current) {
			current.flushAllPages();
		}
	}

	private class RendererSwitcher implements Runnable {
		PDFToImageRenderer a;

		double baselineMemory;
		double threshold = 0.4;

		public RendererSwitcher() throws IOException{
			a = initRenderer();
			current = a;
			baselineMemory = TestMemory.getAvailableMemory();
			threshold = 0.40 * baselineMemory;
			System.out.println("Threshold :" + threshold);
		}

		public void run() {
			double lastMem = baselineMemory;
			double mem;
			boolean emergency = false;
			while (!shouldStop) {
				try {
					synchronized (lock) {
						mem = TestMemory.getAvailableMemory();
						if (mem < threshold) {
							if (lastMem >= threshold && !emergency) {
								System.out.println("taking action : free mem = " + ("" + mem).substring(0, 5) + "% - Flushing all pages");
								a.flushAllPages();
							}
							System.gc();
							System.runFinalization();
						}
					}
					lastMem = mem;
					Thread.sleep(CHECK_INTERVAL);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}

		public PDFToImageRenderer initRenderer()throws IOException {
			PDFToImageRenderer ren = null;

			UNIT unit = UNIT.PIXEL;

			switch (type) {
			case DPI:
				unit = UNIT.DPI;
				break;
			case PIXELS:
				unit = UNIT.PIXEL;
				break;
			}
				ren = new PDFToImageRenderer(unit, pdffile);
			return ren;
		}
	}
}
