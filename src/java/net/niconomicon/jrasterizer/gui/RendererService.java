/**
 * 
 */
package net.niconomicon.jrasterizer.gui;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import net.niconomicon.jrasterizer.PDFToImage;
import net.niconomicon.jrasterizer.PDFToImageRenderer;
import net.niconomicon.jrasterizer.PDFToImageRenderer.UNIT;
import net.niconomicon.jrasterizer.utils.TestMemory;

import com.sun.pdfview.PDFFile;

/**
 * As there seems to be a memory leak in the {@link PDFFile} class, this class tries to work around it by always having
 * at least one renderer available.;
 * 
 * @author Nicolas Hoibian
 * 
 */
public class RendererService implements PDFToImage {

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

	private RendererService(RASTERIZER_TYPE type, File pdf) {
		this.type = type;
		pdffile = pdf;
		lock = new Object();
		switcherThread = new Thread(new RendererSwitcher());
		switcherThread.start();
	}

	public static RendererService createService(RASTERIZER_TYPE type, File pdf) {
		return new RendererService(type, pdf);
	}

	/* (non-Javadoc)
	 * @see net.niconomicon.jrasterizer.PDFToImage#setReferenceResolution(double)
	 */
	public void setReferenceResolution(double ref) {
		this.referenceRez = ref;
		synchronized (lock) {
			current.setReferenceResolution(ref);
		}
	}

	/* (non-Javadoc)
	 * @see net.niconomicon.jrasterizer.PDFToImage#getFileLocation()
	 */
	public String getFileLocation() {
		return pdffile.getAbsolutePath();
	}

	/* (non-Javadoc)
	 * @see net.niconomicon.jrasterizer.PDFToImage#setPDFFromFileLocation(java.lang.String)
	 */
	public void setPDFFromFileLocation(String pdfLocation) throws IOException {
		this.pdffile = new File(pdfLocation);
		synchronized (lock) {
			current.setPDFFromFileLocation(pdfLocation);
		}
	}

	/* (non-Javadoc)
	 * @see net.niconomicon.jrasterizer.PDFToImage#setPDFFromFile(java.io.File)
	 */
	public void setPDFFromFile(File pdfLocation) throws IOException {
		this.pdffile = pdfLocation;
		synchronized (lock) {
			current.setPDFFromFile(pdfLocation);

		}
	}

	/* (non-Javadoc)
	 * @see net.niconomicon.jrasterizer.PDFToImage#getPageCount()
	 */
	public int getPageCount() {
		synchronized (lock) {
			return current.getPageCount();
		}
	}

	/* (non-Javadoc)
	 * @see net.niconomicon.jrasterizer.PDFToImage#getImageDimensions(int, int)
	 */
	public Dimension getImageDimensions(int pageNum, int info) {
		synchronized (lock) {
			return current.getImageDimensions(pageNum, info);
		}
	}

	/* (non-Javadoc)
	 * @see net.niconomicon.jrasterizer.PDFToImage#getImageFromPDF(int, int)
	 */
	public BufferedImage getImageFromPDF(int pageNum, int info) {
		synchronized (lock) {
			return current.getImageFromPDF(pageNum, info);
		}
	}

	/* (non-Javadoc)
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
		PDFToImageRenderer b;

		double baselineMemory;
		double threshold = 0.35;
		double emergencyThreshold = 0.15;

		public RendererSwitcher() {
			a = null;
			b = null;
			a = initRenderer();
			current = a;
			b = initRenderer();
			baselineMemory = TestMemory.getAvailableMemory();
			threshold = 0.40 * baselineMemory;
			emergencyThreshold = 0.2 * baselineMemory;
			System.out.println("Threshold :" + threshold);
		}

		// public void run() {
		// double lastMem = baselineMemory;
		// double mem;
		// boolean emergency = false;
		// while (!shouldStop) {
		// mem = TestMemory.getAvailableMemory();
		// try {
		// boolean shouldResetA = false;
		// boolean shouldResetB = false;
		// synchronized (lock) {
		// if (mem < threshold && lastMem >= threshold && !emergency) {
		// // take action
		// System.out.println("taking action : " + mem);
		// if (a == current && b != null) {
		// System.out.println("Switching current to b");
		// current = b;
		// shouldResetA = true;
		// } else {
		// if (b == current && a != null) {
		// System.out.println("Switching current to a");
		// current = a;
		// shouldResetB = true;
		// } else {
		// System.err.println("Should not ever come here !!!!!!!!! There was a problem when trying to decide which renderer to reset.");
		// }
		// }
		// }
		// }
		// if (shouldResetA) {
		// synchronized (a) {
		// a.flushAllPages();
		// System.runFinalization();
		// System.gc();
		// }
		// }
		// if (shouldResetB) {
		// synchronized (b) {
		// b.flushAllPages();
		// System.runFinalization();
		// System.gc();
		// }
		// }
		//
		// if (mem < threshold) {
		// System.runFinalization();
		// System.gc();
		// }
		// Thread.sleep(50);
		//
		// } catch (Exception ex) {
		// ex.printStackTrace();
		// }
		// lastMem = mem;
		// }
		// }

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
								// take action
								System.out.println("taking action : free mem = " + ("" + mem).substring(0, 5) + "% - Flushing all pages");
								a.flushAllPages();
							}
							System.gc();
							System.runFinalization();
						}
					}
					lastMem = mem;
					Thread.sleep(50);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}

		public PDFToImageRenderer initRenderer() {
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
			try {
				ren = new PDFToImageRenderer(unit, pdffile);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return ren;
		}
	}
}
