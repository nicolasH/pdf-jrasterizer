/**
 * 
 */
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

import net.niconomicon.jrasterizer.utils.FastClipper;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

/**
 * @author Nicolas Hoibian copyright September 2010
 * 
 */
public class PDFToImageRenderer implements PDFToImage {

	String pdfFileName;
	PDFFile pdf;

	public enum UNIT {
		DPI, PIXEL
	};

	UNIT unit;

	public static final double PDF_ASSUMED_RESOLUTION = 72.0;

	double documentDPI = PDF_ASSUMED_RESOLUTION;

	public PDFToImageRenderer(UNIT unit, String fileName) throws IOException {
		this.unit = unit;
		setPDFFromFileLocation(fileName);
	}

	public PDFToImageRenderer(UNIT unit, File file) throws IOException {
		this.unit = unit;
		setPDFFromFile(file);
	}

	public void setReferenceResolution(double ref) {
		documentDPI = ref;
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
	 * 
	 * @return the location of the PDF file that is the base of this instance.
	 */
	public String getFileLocation() {
		return pdfFileName;
	}

	/**
	 * Load the PDF at the given location and set it @see {@link #setPDF(PDFFile)}.
	 * 
	 * @param pdfLocation
	 *            the path to the PDF file.
	 * @throws IOException
	 *             if there is a problem opening the PDF.
	 */
	public void setPDFFromFileLocation(String pdfLocation) throws IOException {
		setPDFFromFile(new File(pdfLocation));
	}

	/**
	 * Load the PDF at the given location and set it @see {@link #setPDF(PDFFile)}.
	 * 
	 * @param pdfLocation
	 *            the PDF file.
	 * @throws IOException
	 *             if there is a problem opening the PDF.
	 */
	public void setPDFFromFile(File pdfLocation) throws IOException {
		this.pdfFileName = pdfLocation.getAbsolutePath();
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
	 * 
	 * @return the number of pages in the pdf, 0 if the pdf has not been set.
	 */
	public int getPageCount() {
		if (null == pdf) { return 0; }
		return pdf.getNumPages();
	}

	/**
	 * 
	 * @param pageNum
	 * @param info
	 *            will be treated as dpi if you initialized the renderer with unit type DPI , as pixels otherwise.
	 * @return 0,0 if the pdf is not set yet.
	 */
	public Dimension getImageDimensions(int pageNum, int info) {
		if (null == pdf) { return new Dimension(0, 0); }
		int numPages = pdf.getNumPages();

		if (pageNum < 1) { throw new IllegalArgumentException("You want to get infos about the page #" + pageNum + " but the pdf starts at page #1"); }
		if (pageNum > numPages) { throw new IllegalArgumentException("You want to get infos the page #" + pageNum + " but the pdf only has #" + numPages + " pages"); }

		PDFPage page = pdf.getPage(pageNum);
		double width = info;
		double height = info;
		if (UNIT.DPI == unit) {
			Rectangle2D r2d = page.getBBox();

			width = r2d.getWidth();
			height = r2d.getHeight();
			double r = info / documentDPI;
			width *= r;
			height *= r;
		}
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
	public BufferedImage getImageFromPDF(int pageNum, int info) {

		if (pdf == null) { throw new IllegalArgumentException("You want to render the page #" + pageNum + " but no PDF has been set."); }

		int numPages = pdf.getNumPages();

		if (pageNum < 1) { throw new IllegalArgumentException("You want to render the page #" + pageNum + " but the pdf starts at page #1"); }
		if (pageNum > numPages) { throw new IllegalArgumentException("You want to render the page #" + pageNum + " but the pdf only has #" + numPages + " pages"); }

		Dimension pageSize = getImageDimensions(pageNum, info);

		PDFPage page = pdf.getPage(pageNum);

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
	public BufferedImage getExtract(int pageNum, int info, int clipSize) {
		if (pdf == null) { throw new IllegalArgumentException("You want to render the page #" + pageNum + " but no PDF has been set."); }

		int numPages = pdf.getNumPages();

		if (pageNum < 1) { throw new IllegalArgumentException("You want to render the page #" + pageNum + " but the pdf starts at page #1"); }
		if (pageNum > numPages) { throw new IllegalArgumentException("You want to render the page #" + pageNum + " but the pdf only has #" + numPages + " pages"); }

		Dimension pageSize = getImageDimensions(pageNum, info);

		int cX = (int) (((double) (pageSize.getWidth() - clipSize)) / 2);
		int cY = (int) (((double) (pageSize.getHeight() - clipSize)) / 2);
		if (cX < 0) {
			cX = 0;
		}
		if (cY < 0) {
			cY = 0;
		}
		int cW = (int) Math.min(pageSize.width, clipSize);
		int cH = (int) Math.min(pageSize.height, clipSize);

		Rectangle clip = new Rectangle(cX, cY, cW, cH);
		// get the new image, waiting until the pdf has been fully rendered.
		// BufferedImage image = (BufferedImage) page.getImage(pageSize.width, pageSize.height, null, null, true, true);
		BufferedImage image = getImageFromPDF(pageNum,info);
//		BufferedImage image = (BufferedImage) pdf.getPage(pageNum).getImage(pageSize.width, pageSize.height, null, null, true, true);
		BufferedImage ret = FastClipper.fastClip(image, clip);
		image = null;
		return ret;
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
	public static BufferedImage getImageFromPDFAtLocation(String pdfLocation, int pageNum, UNIT unit, int info) throws IOException {
		PDFFile pdf = getPDFFile(new File(pdfLocation));
		if (pdf == null) { throw new IllegalArgumentException("You want to render the page #" + pageNum + " but the PDF could not be open."); }

		int numPages = pdf.getNumPages();

		if (pageNum < 1) { throw new IllegalArgumentException("You want to render the page #" + pageNum + " but the pdf starts at page #1"); }
		if (pageNum > numPages) { throw new IllegalArgumentException("You want to render the page #" + pageNum + " but the pdf only has #" + numPages + " pages"); }

		PDFPage page = pdf.getPage(pageNum);

		// Getting the correct dimensions.
		PDFToImage ren = new PDFToImageRenderer(unit, pdfLocation);
		Image image = ren.getImageFromPDF(pageNum, info);
		// get the new image, waiting until the pdf has been fully rendered.

		return (BufferedImage) image;
	}

}
