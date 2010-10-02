/**
 * 
 */
package net.niconomicon.jrasterizer.gui;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import com.sun.pdfview.PDFFile;

import net.niconomicon.jrasterizer.PDFToImage;
import net.niconomicon.jrasterizer.PDFToImageRenderer;
import net.niconomicon.jrasterizer.PDFToImageRenderer.UNIT;
import net.niconomicon.jrasterizer.utils.SandboxMemory;
import net.niconomicon.jrasterizer.utils.TestMemory;

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
	static RendererService singleton;
	RASTERIZER_TYPE type;
	double referenceRez;
	PDFToImageRenderer current;

	public enum RASTERIZER_TYPE {
		DPI, PIXELS
	};

	private RendererService(RASTERIZER_TYPE type, File pdf) {
		this.type = type;
		pdffile = pdf;
	}

	public static void createService(RASTERIZER_TYPE type, File pdf) {
		singleton = new RendererService(type, pdf);
	}

	/* (non-Javadoc)
	 * @see net.niconomicon.jrasterizer.PDFToImage#setReferenceResolution(double)
	 */
	public void setReferenceResolution(double ref) {
		this.referenceRez = ref;
		synchronized (current) {
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
		synchronized (current) {
			current.setPDFFromFileLocation(pdfLocation);
		}
	}

	/* (non-Javadoc)
	 * @see net.niconomicon.jrasterizer.PDFToImage#setPDFFromFile(java.io.File)
	 */
	public void setPDFFromFile(File pdfLocation) throws IOException {
		this.pdffile = pdfLocation;
		synchronized (current) {
			current.setPDFFromFile(pdfLocation);

		}
	}

	/* (non-Javadoc)
	 * @see net.niconomicon.jrasterizer.PDFToImage#getPageCount()
	 */
	public int getPageCount() {
		synchronized (current) {
			return current.getPageCount();
		}
	}

	/* (non-Javadoc)
	 * @see net.niconomicon.jrasterizer.PDFToImage#getImageDimensions(int, int)
	 */
	public Dimension getImageDimensions(int pageNum, int info) {
		synchronized (current) {
			return current.getImageDimensions(pageNum, info);
		}
	}

	/* (non-Javadoc)
	 * @see net.niconomicon.jrasterizer.PDFToImage#getImageFromPDF(int, int)
	 */
	public BufferedImage getImageFromPDF(int pageNum, int info) {
		synchronized (current) {
			return current.getImageFromPDF(pageNum, info);
		}
	}

	/* (non-Javadoc)
	 * @see net.niconomicon.jrasterizer.PDFToImage#getExtract(int, int, int)
	 */
	public BufferedImage getExtract(int pageNum, int info, int clipSize) {
		synchronized (current) {
			return current.getExtract(pageNum, info, clipSize);
		}
	}

	private class RendererSwitcher implements Runnable {
		public boolean shouldStop = false;
		PDFToImageRenderer a;
		PDFToImageRenderer b;

		public void run() {
			initRenderer(a);
			initRenderer(b);

			while (!shouldStop) {
				if (TestMemory.getAvailableMemory() < .20) {
					boolean shouldResetA = false;
					boolean shouldResetB = false;
					synchronized (current) {
						if (a == current && b != null) {
							current = b;
							shouldResetA = true;
						} else {
							if (b == current && a != null) {
								current = a;
								shouldResetB = true;
							} else {
								System.err.println("Should not ever come here !!!!!!!!! There was a problem when trying to decide which renderer to reset.");
							}
						}
					}
					if (shouldResetA) {
						initRenderer(a);
					}
					if (shouldResetB) {
						initRenderer(b);
					}
				}
				try {
					Thread.sleep(500);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

		}

		public void initRenderer(PDFToImageRenderer ren) {
			ren = null;

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
		}
	}
}
