/**
 * 
 */
package net.niconomicon.jrasterizer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import com.sun.pdfview.PDFFile;

/**
 * @author Nicolas Hoibian copyright September 2010
 * 
 */
public abstract class PDFToImageRenderer {

	String pdfFileName;
	PDFFile pdf;

	public PDFToImageRenderer(String fileName) throws IOException {
		setPDFFromFileLocation(fileName);
	}

	public PDFToImageRenderer(File file) throws IOException {
		setPDFFromFile(file);
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

}
