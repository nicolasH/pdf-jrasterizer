/**
 * 
 */
package net.niconomicon.jrasterizer;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * @author Nicolas Hoibian copyright August 2010
 * 
 */

public class PDFFileFilter extends FileFilter {

	public boolean accept(File f) {
		if (f.isDirectory()) { return true; }

		String ext = getLowerCaseExt(f);

		if (null != ext && "pdf".compareTo(ext) == 0) { return true; }

		return false;
	}

	public String getDescription() {
		return "pdf files";
	}

	/**
	 * 
	 * @param File
	 *            the file
	 * @return the file's extension in lowercase, without the "."
	 */
	public static String getLowerCaseExt(File f) {
		return getLowerCaseExt(f.getName());
	}

	/**
	 * 
	 * @param String
	 *            a file name
	 * @return the file's extension in lowercase, without the "."
	 */
	public static String getLowerCaseExt(String s) {
		String ext = null;
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

}
