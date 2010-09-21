package net.niconomicon.jrasterizer;

import java.awt.Dimension;
import java.awt.Graphics2D;
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
 * @author Nicolas Hoibian copyright September 2010
 * 
 */
public class PDFToImageRendererPixels extends PDFToImageRenderer {

	PDFFile pdf = null;

	public PDFToImageRendererPixels(String fileName) throws IOException {
		super(fileName);
	}

	public PDFToImageRendererPixels(File file) throws IOException {
		super(file);
	}

	/**
	 * a thin wrapper over PDFPage.getUnstretchedSize()
	 * 
	 * @param pageNum
	 * @param maxSideLength
	 * @return the dimension of page, stretched so that the biggest side is equal to maxSideLength
	 */
	public Dimension getImageDimForSideLength(int pageNum, int maxSideLength) {
		if (null == pdf) { return new Dimension(-1, -1); }
		int numPages = pdf.getNumPages();

		if (pageNum < 1) { throw new IllegalArgumentException("You want to get infos about the page #" + pageNum + " but the pdf starts at page #1"); }
		if (pageNum > numPages) { throw new IllegalArgumentException("You want to get infos the page #" + pageNum + " but the pdf only has #" + numPages + " pages"); }

		PDFPage page = pdf.getPage(pageNum);
		// ///////////
		Dimension pageSize = page.getUnstretchedSize(maxSideLength, maxSideLength, null);
		return pageSize;
	}

	/**
	 * @throws IllegalArgumentException
	 *             if the page you want is out of range. if the pdf has not been set correctly.
	 * 
	 * @param pageNum
	 *            The page number starts from 1.
	 * @param biggestSide
	 *            In pixels. The biggest side of the image will be close to this.
	 * 
	 * @return the image resulting from rendering the PDF at the given resolution.
	 */
	public BufferedImage getImageFromPDFForSize(int pageNum, int biggestSide) {

		if (pdf == null) { throw new IllegalArgumentException("You want to render the page #" + pageNum + " but no PDF has been set."); }

		int numPages = pdf.getNumPages();

		if (pageNum < 1) { throw new IllegalArgumentException("You want to render the page #" + pageNum + " but the pdf starts at page #1"); }
		if (pageNum > numPages) { throw new IllegalArgumentException("You want to render the page #" + pageNum + " but the pdf only has #" + numPages + " pages"); }

		PDFPage page = pdf.getPage(pageNum);

		// Getting the correct dimensions.
		Dimension pageSize = page.getUnstretchedSize((int) biggestSide, (int) biggestSide, null);

		// get the new image, waiting until the pdf has been fully rendered.
		Image image = page.getImage(pageSize.width, pageSize.height, null, null, true, true);

		return (BufferedImage) image;
	}

	/**
	 * 
	 * @param pageNum
	 * @param biggestSide
	 * @param clipSize
	 * @return a clip from the center of the page, of at most clipSizexclipSize pixels.
	 */
	public BufferedImage getExtract(int pageNum, int biggestSide, int clipSize) {
		if (pdf == null) { throw new IllegalArgumentException("You want to render the page #" + pageNum + " but no PDF has been set."); }

		int numPages = pdf.getNumPages();

		if (pageNum < 1) { throw new IllegalArgumentException("You want to render the page #" + pageNum + " but the pdf starts at page #1"); }
		if (pageNum > numPages) { throw new IllegalArgumentException("You want to render the page #" + pageNum + " but the pdf only has #" + numPages + " pages"); }

		Dimension pageSize = getImageDimForSideLength(pageNum, biggestSide);

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
		// System.out.println("Clip : " + clip);
		// get the new image, waiting until the pdf has been fully rendered.
		// BufferedImage image = (BufferedImage) page.getImage(pageSize.width, pageSize.height, null, null, true, true);
		BufferedImage image = (BufferedImage) pdf.getPage(pageNum).getImage(pageSize.width, pageSize.height, null, null, true, true);
		BufferedImage ret = image.getSubimage(cX, cY, cW, cH);
		// supposedly releasing 'image'
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
	public static BufferedImage getImageFromPDFAtLocation(String pdfLocation, int pageNum, int imageSize, int imageResolution) throws IOException {
		PDFFile pdf = getPDFFile(new File(pdfLocation));
		if (pdf == null) { throw new IllegalArgumentException("You want to render the page #" + pageNum + " but the PDF could not be open."); }

		int numPages = pdf.getNumPages();

		if (pageNum < 1) { throw new IllegalArgumentException("You want to render the page #" + pageNum + " but the pdf starts at page #1"); }
		if (pageNum > numPages) { throw new IllegalArgumentException("You want to render the page #" + pageNum + " but the pdf only has #" + numPages + " pages"); }

		PDFPage page = pdf.getPage(pageNum);

		// Getting the correct dimensions.
		Dimension pageSize = page.getUnstretchedSize((int) imageSize, (int) imageSize, null);

		// get the new image, waiting until the pdf has been fully rendered.
		Image image = page.getImage(pageSize.width, pageSize.height, null, null, true, true);

		return (BufferedImage) image;
	}

}
