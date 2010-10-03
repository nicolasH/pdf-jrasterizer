/**
 * 
 */
package net.niconomicon.jrasterizer;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import com.sun.pdfview.PDFFile;

/**
 * @author Nicolas Hoibian
 * 
 */
public interface PDFToImage {

	public abstract void setReferenceResolution(double ref);

	/**
	 * 
	 * @return the location of the PDF file that is the base of this instance.
	 */
	public abstract String getFileLocation();

	/**
	 * Load the PDF at the given location and set it @see {@link #setPDF(PDFFile)}.
	 * 
	 * @param pdfLocation
	 *            the path to the PDF file.
	 * @throws IOException
	 *             if there is a problem opening the PDF.
	 */
	public abstract void setPDFFromFileLocation(String pdfLocation) throws IOException;

	/**
	 * Load the PDF at the given location and set it @see {@link #setPDF(PDFFile)}.
	 * 
	 * @param pdfLocation
	 *            the PDF file.
	 * @throws IOException
	 *             if there is a problem opening the PDF.
	 */
	public abstract void setPDFFromFile(File pdfLocation) throws IOException;

	/**
	 * 
	 * @return the number of pages in the pdf, 0 if the pdf has not been set.
	 */
	public abstract int getPageCount();

	/**
	 * 
	 * @param pageNum
	 * @param info
	 *            will be treated as dpi if you initialized the renderer with unit type DPI , as pixels otherwise.
	 * @return 0,0 if the pdf is not set yet.
	 */
	public abstract Dimension getImageDimensions(int pageNum, int info);

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
	public abstract BufferedImage getImageFromPDF(int pageNum, int info);

	/**
	 * 
	 * @param pageNum
	 * @param resolution
	 * @param clipSize
	 * @return a clip from the center of the page, of at most clipSizexclipSize pixels.
	 */
	public abstract BufferedImage getExtract(int pageNum, int info, int clipSize);

}