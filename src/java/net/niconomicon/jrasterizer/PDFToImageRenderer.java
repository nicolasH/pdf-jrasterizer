package net.niconomicon.jrasterizer;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

/**
 * 
 */

/**
 * @author Nicolas Hoibian copyright August 2010
 * 
 */
public class PDFToImageRenderer {

	public static final double PDF_ASSUMED_RESOLUTION = 72.0;

	public final double usedDefaultResolution;

	PDFFile pdf = null;

	/**
	 * Create a
	 */
	public PDFToImageRenderer() {
		usedDefaultResolution = PDF_ASSUMED_RESOLUTION;
	}

	public PDFToImageRenderer(double pdfResolution) {
		usedDefaultResolution = pdfResolution;
	}

	public Dimension getImageDimForResolution(int pageNum, int resolution) {
		if (null == pdf) { return new Dimension(-1, -1); }
		int numPages = pdf.getNumPages();

		if (pageNum < 1) { throw new IllegalArgumentException("You want to get infos about the page #" + pageNum + " but the pdf starts at page #1"); }
		if (pageNum > numPages) { throw new IllegalArgumentException("You want to get infos the page #" + pageNum + " but the pdf only has #" + numPages + " pages"); }

		PDFPage page = pdf.getPage(pageNum);
		Rectangle2D r2d = page.getBBox();

		double width = r2d.getWidth();
		double height = r2d.getHeight();
		double r = resolution / usedDefaultResolution;
		width *= r;
		height *= r;

		// Getting the correct dimensions.
		Dimension pageSize = page.getUnstretchedSize((int) width, (int) height, null);
		return pageSize;
	}

	/**
	 * @throws IllegalArgumentException
	 *             if the page you want is out of range. if the pdf has not been set correctly.
	 * 
	 * @param pageNum
	 *            The page number starts from 1.
	 * @param resolution
	 *            In DPI. The size of the image will be proportional to <code> usedDefaultResolution * resolution</code>
	 * 
	 * @return the image resulting from rendering the PDF at the given resolution.
	 */
	public BufferedImage getImageFromPDF(int pageNum, int resolution) {

		if (pdf == null) { throw new IllegalArgumentException("You want to render the page #" + pageNum + " but no PDF has been set."); }

		int numPages = pdf.getNumPages();

		if (pageNum < 1) { throw new IllegalArgumentException("You want to render the page #" + pageNum + " but the pdf starts at page #1"); }
		if (pageNum > numPages) { throw new IllegalArgumentException("You want to render the page #" + pageNum + " but the pdf only has #" + numPages + " pages"); }

		PDFPage page = pdf.getPage(pageNum);
		Rectangle2D r2d = page.getBBox();

		double width = r2d.getWidth();
		double height = r2d.getHeight();
		double r = resolution / usedDefaultResolution;
		width *= r;
		height *= r;

		// Getting the correct dimensions.
		Dimension pageSize = page.getUnstretchedSize((int) width, (int) height, null);

		// get the new image, waiting until the pdf has been fully rendered.
		Image image = page.getImage(pageSize.width, pageSize.height, null, null, true, true);

		return (BufferedImage) image;
	}

	/**
	 * 
	 * @param pageNum
	 * @param resolution
	 * @param clipSize
	 * @return a clip from the center of the page, of at most clipSizexclipSize pixels.
	 */
	public BufferedImage getExtract(int pageNum, int resolution, int clipSize) {
		if (pdf == null) { throw new IllegalArgumentException("You want to render the page #" + pageNum + " but no PDF has been set."); }

		int numPages = pdf.getNumPages();

		if (pageNum < 1) { throw new IllegalArgumentException("You want to render the page #" + pageNum + " but the pdf starts at page #1"); }
		if (pageNum > numPages) { throw new IllegalArgumentException("You want to render the page #" + pageNum + " but the pdf only has #" + numPages + " pages"); }

		PDFPage page = pdf.getPage(pageNum);
		Rectangle2D r2d = page.getBBox();

		double width = r2d.getWidth();
		double height = r2d.getHeight();
		double r = resolution / usedDefaultResolution;
		width *= r;
		height *= r;

		// Getting the correct dimensions.
		Dimension pageSize = page.getUnstretchedSize((int) width, (int) height, null);
		int cX = (int) (((double) (width - clipSize)) / 2);
		int cY = (int) (((double) (height - clipSize)) / 2);
		if (cX < 0) {
			cX = 0;
		}
		if (cY < 0) {
			cY = 0;
		}
		int cW = (int) Math.min(width, clipSize);
		int cH = (int) Math.min(height, clipSize);

		Rectangle clip = new Rectangle(cX, cY, cW, cH);
		// get the new image, waiting until the pdf has been fully rendered.
		Image image = page.getImage(pageSize.width, pageSize.height, clip, null, true, true);

		return (BufferedImage) image;

	}

	/**
	 * 
	 * @return the number of pages in the pdf, 0 if the pdf has not been set.
	 */
	public int getPageCount() {
		if (null == pdf) { return 0; }
		return pdf.getNumPages();
	}

	/**
	 * Set the pdf file on which the rendering is going to be performed.
	 * 
	 * @param pdfFile
	 */
	public void setPDF(PDFFile pdfFile) {
		this.pdf = pdfFile;
	}

	/**
	 * Load the PDF at the given location and set it @see {@link #setPDF(PDFFile)}.
	 * 
	 * @param pdfLocation
	 *            the path to the PDF file.
	 * @throws IOException
	 *             if there is a problem opening the PDF.
	 */
	public void setPDFFromFile(File pdfLocation) throws IOException {
		PDFFile pdfFile = getPDFFile(pdfLocation);
		setPDF(pdfFile);
	}

	/**
	 * Loads the PDFFile from the given location.
	 * 
	 * @param filePath
	 *            location of the PDF file.
	 * @return The PDFFile representation of the file at the given location (if any).
	 * @throws IOException
	 */
	public static PDFFile getPDFFile(File filePath) throws IOException {
		RandomAccessFile raf = new RandomAccessFile(filePath, "r");
		FileChannel fc = raf.getChannel();
		ByteBuffer buf = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
		return new PDFFile(buf);
	}

	/**
	 * A utility method that open the PDF at the given location and returns an image of the given page. Blocks while the
	 * image is being rendered.
	 * 
	 * @param pdfLocation
	 * @param pageNum
	 * @param pdfResolution
	 *            The resolution of the PDF, in DPI. Assumed to be {@value #PDF_ASSUMED_RESOLUTION} if the given value
	 *            is 0.
	 * @param imageResolution
	 *            The resolution you want the image to be compared to the default PDF Resolution.
	 * @return an image of the page at the given resolution.
	 * @throws IOException
	 */
	public static BufferedImage getImageFromPDFAtLocation(String pdfLocation, int pageNum, double pdfResolution, int imageResolution) throws IOException {
		PDFFile pdf = getPDFFile(new File(pdfLocation));
		if (pdf == null) { throw new IllegalArgumentException("You want to render the page #" + pageNum + " but the PDF could not be open."); }

		int numPages = pdf.getNumPages();

		if (pageNum < 1) { throw new IllegalArgumentException("You want to render the page #" + pageNum + " but the pdf starts at page #1"); }
		if (pageNum > numPages) { throw new IllegalArgumentException("You want to render the page #" + pageNum + " but the pdf only has #" + numPages + " pages"); }

		PDFPage page = pdf.getPage(pageNum);
		Rectangle2D r2d = page.getBBox();

		double width = r2d.getWidth();
		double height = r2d.getHeight();
		if (0 == pdfResolution) {
			pdfResolution = PDF_ASSUMED_RESOLUTION;
		}
		double r = imageResolution / pdfResolution;
		width *= r;
		height *= r;

		// Getting the correct dimensions.
		Dimension pageSize = page.getUnstretchedSize((int) width, (int) height, null);

		// get the new image, waiting until the pdf has been fully rendered.
		Image image = page.getImage(pageSize.width, pageSize.height, null, null, true, true);

		return (BufferedImage) image;
	}

}
