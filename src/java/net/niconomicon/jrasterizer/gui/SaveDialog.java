/**
 * 
 */
package net.niconomicon.jrasterizer.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.security.AllPermission;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * @author Nicolas Hoibian copyright August 2010
 * 
 */

public class SaveDialog {

	JLabel to;
	JLabel finalName;

	JLabel dimensions;
	JLabel pageLabel;

	JTextField as;

	JRadioButton png;
	JRadioButton jpg;

	JRadioButton pageCurrent;
	JRadioButton pageAll;

	JFileChooser dirChooser;
	FileDialog dirChooserOSX;

	PDFRasterizerGUI gui;

	JPanel contentPane;
	File originalPDF;

	int page = 0;
	Dimension imageDim;
	int maxPage;

	public SaveDialog(PDFRasterizerGUI gui) {
		this.gui = gui;
		init();
	}

	private void init() {
		contentPane = new JPanel(new GridBagLayout());
		contentPane.setName("Save Image");

		// Choosing the destination :
		if (System.getProperty("os.name").toLowerCase().contains("mac")) {
			System.setProperty("apple.awt.fileDialogForDirectories", "true");
			dirChooserOSX = new FileDialog(JFrame.getFrames()[0]);
		} else {

			dirChooser = new JFileChooser();
			dirChooser.setAcceptAllFileFilterUsed(false);
			dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			dirChooser.setDialogTitle("Choose directory to save the tile source");
			dirChooser.setCurrentDirectory(new File(System.getProperty(PDFRasterizerGUI.USER_HOME)));
		}

		JButton chooseS = new JButton("Directory");
		chooseS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread t = new Thread(new RootDirSetter());
				t.start();
			}
		});

		to = new JLabel();
		finalName = new JLabel();
		as = new JTextField();
		as.setSize(25, 100);

		pageLabel = new JLabel();
		dimensions = new JLabel();
		pageAll = new JRadioButton("all pages");
		pageCurrent = new JRadioButton("this page");
		ButtonGroup pages = new ButtonGroup();
		pages.add(pageAll);
		pages.add(pageCurrent);

		pageCurrent.addActionListener(new FinalNameSetter());
		pageAll.addActionListener(new FinalNameSetter());
		pageCurrent.setSelected(true);

		png = new JRadioButton("png");
		jpg = new JRadioButton("jpg");
		png.setToolTipText("Lossless compression - bigger files than jpg");
		png.setToolTipText("Lossy compression - smaller files than png");

		ButtonGroup formats = new ButtonGroup();
		formats.add(jpg);
		formats.add(png);

		png.addActionListener(new FinalNameSetter());
		jpg.addActionListener(new FinalNameSetter());
		png.setSelected(true);

		// save = new JButton("Save Image");
		// save.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent e) {
		// Thread saver = new Thread(new SaveImageToFormat((String) formats.getSelectedItem()));
		// save.setEnabled(false);
		// saver.start();
		// }
		// });

		GridBagConstraints c;
		int y = 0;
		int x = 0;

		c = new GridBagConstraints();
		c.gridy = y++;
		c.gridx = x;
		c.anchor = c.LINE_END;
		contentPane.add(new JLabel("Page : "), c);

		c = new GridBagConstraints();
		c.gridy = y++;
		c.gridx = x;
		c.anchor = c.LINE_END;
		contentPane.add(new JLabel("Dimensions : "), c);

		c = new GridBagConstraints();
		c.gridy = y++;
		c.gridx = x;
		c.anchor = c.LINE_END;
		contentPane.add(new JLabel("Format : "), c);

		c = new GridBagConstraints();
		c.gridy = y++;
		c.gridx = x;
		c.anchor = c.LINE_END;
		contentPane.add(new JLabel("Pages : "), c);

		c = new GridBagConstraints();
		c.gridy = y++;
		c.gridx = x;
		c.anchor = c.LINE_END;
		contentPane.add(chooseS, c);

		c = new GridBagConstraints();
		c.gridy = y++;
		c.gridx = x;
		c.anchor = c.LINE_END;
		contentPane.add(new JLabel("Name : "), c);

		c = new GridBagConstraints();
		c.gridy = y++;
		c.gridx = x;
		c.anchor = c.LINE_END;
		contentPane.add(new JLabel("Final file : "), c);

		y = 0;
		x = 1;

		c = new GridBagConstraints();
		c.gridy = y++;
		c.gridx = x;
		c.gridwidth = 2;
		c.anchor = c.LINE_START;
		contentPane.add(pageLabel, c);

		c = new GridBagConstraints();
		c.gridy = y++;
		c.gridx = x;
		c.gridwidth = 2;
		c.anchor = c.LINE_START;
		contentPane.add(dimensions, c);

		c = new GridBagConstraints();
		c.gridy = y;
		c.gridx = x;
		c.anchor = c.LINE_START;
		contentPane.add(png, c);
		c = new GridBagConstraints();
		c.gridy = y++;
		c.gridx = x + 1;
		c.anchor = c.LINE_START;
		contentPane.add(jpg, c);

		c = new GridBagConstraints();
		c.gridy = y;
		c.gridx = x;
		c.anchor = c.LINE_START;
		contentPane.add(pageCurrent, c);

		c = new GridBagConstraints();
		c.gridy = y++;
		c.gridx = x + 1;
		c.anchor = c.LINE_START;
		contentPane.add(pageAll, c);

		c = new GridBagConstraints();
		c.gridy = y++;
		c.gridx = x;
		c.gridwidth = 2;
		c.fill = c.REMAINDER;
		c.anchor = c.LINE_START;
		contentPane.add(to, c);

		c = new GridBagConstraints();
		c.gridy = y++;
		c.gridx = x;
		c.gridwidth = 2;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		contentPane.add(as, c);

		c = new GridBagConstraints();
		c.gridy = y++;
		c.gridx = x;
		c.gridwidth = 2;
		c.gridheight = 3;
		c.weightx = 1.0;
		c.weighty = 3.0;
		c.fill = c.REMAINDER;
		c.anchor = c.NORTHWEST;
		contentPane.add(finalName, c);
		// finalName.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		contentPane.setSize(new Dimension(400, 200));
		contentPane.setPreferredSize(new Dimension(400, 200));
		contentPane.setMinimumSize(new Dimension(400, 200));
	}

	public void setCurrentFile(File pdfFile) {
		originalPDF = pdfFile;
		SwingUtilities.invokeLater(new DefaultFileSetter());
	}

	public void save(int page, int maxPage, Dimension dim) {
		dimensions.setText(dim.width + " by " + dim.height + " pixels");
		pageLabel.setText("" + page);
		pageCurrent.setText("this page (" + page + ")");
		pageAll.setText("all " + maxPage + "pages");
		this.page = page;
		this.maxPage = maxPage;
		setFinalName();
		int answer = JOptionPane.showOptionDialog(gui.frame, contentPane, "Save rasterized page", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[] { "Save", "Cancel" }, null);
		System.out.println("The returned value was " + answer);
		if (answer == 0) {
			Thread saver = new Thread(new SaveImageToFormat());
			// save.setEnabled(false);
			saver.start();
			System.out.println("Should save the pages, you know");
		}
	}

	public void setFinalName() {
		if (pageCurrent.isSelected()) {
			finalName.setText(to.getText() + File.separator + as.getText() + "." + getImageFormat());
		} else {
			if (maxPage == 1) {
				finalName.setText(to.getText() + File.separator + as.getText() + "_1." + getImageFormat());
				return;
			}
			String text = "<html><body>";
			for (int i = 1; i <= Math.min(2, maxPage); i++) {
				text += to.getText() + File.separator + as.getText() + "_" + i + "." + getImageFormat() + "<br>";
			}
			text += "...</body></html>";
			finalName.setText(text);
		}
	}

	public String getImageFormat() {
		String format = png.isSelected() ? "png" : "";
		format += jpg.isSelected() ? "jpg" : "";
		return format;
	}

	private class DefaultFileSetter implements Runnable {

		public void run() {
			to.setText(originalPDF.getParent());
			setStartDir(originalPDF);
			String fName = originalPDF.getName();
			if (fName.toLowerCase().endsWith(".pdf")) {
				// get extension, replace it with the selected element 
				fName = fName.substring(0, fName.lastIndexOf("."));
			}
			as.setText(fName);
			finalName.setText(originalPDF.getParent() + File.separator + fName);
		}
	}

	public void setStartDir(File dir) {
		if (System.getProperty("os.name").toLowerCase().contains("mac")) {
			dirChooserOSX.setDirectory(dir.getParent());
		} else {
			dirChooser.setSelectedFile(dir.getParentFile());
		}
	}

	private class FinalNameSetter implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			setFinalName();
		}
	}

	private class RootDirSetter implements Runnable {

		public void run() {
			// this block until ## is working on mac.
			if (null != dirChooserOSX) {
				dirChooserOSX.setModal(true);// only from java 1.6 : setModalityType(ModalityType.APPLICATION_MODAL);
				dirChooserOSX.setVisible(true);
				String dir = dirChooserOSX.getDirectory();
				String file = dirChooserOSX.getFile();
				System.out.println("Returned with directory : " + dir + file);
				if (null == dir || null == file) { return; }
				File f = new File(dir + file);
				String path;
				if (f.isDirectory()) {
					path = dir + file;
				} else {
					path = dir;
				}
				to.setText(path);

				return;
			}
			// ##
			String s = " some file";

			int returnVal = dirChooser.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					String path = dirChooser.getSelectedFile().getCanonicalPath();
					to.setText(path);
					String wh = dirChooser.getSelectedFile().getAbsolutePath();
				} catch (Exception ex) {
					// outputFileName.setText("cannot Open File");
					to.setText("cannot open file");
					ex.printStackTrace();
				}
			}
		}
	}

	private class SaveImageToFormat implements Runnable {
		String format;
		String destinationFile;

		public SaveImageToFormat() {
			this.format = getImageFormat();
			String s = to.getText();
			String otherS = as.getText();
			if (otherS.endsWith(File.separator)) {
				destinationFile = otherS;
			} else {
				destinationFile = otherS + File.separator;
			}
			if (!s.endsWith(format)) {
				destinationFile += "." + format;
			} else {
				destinationFile += "." + format;
			}
		}

		public void run() {
			int start, stop = 0;
			if (pageAll.isSelected()) {
				start = 1;
				stop = maxPage;
			} else {
				start = stop = page;
			}
			for (int p = start; p <= stop; p++) {
				BufferedImage img = gui.getImage(p, imageDim);
				String pathToSavedFile = finalName.getText();
				pathToSavedFile += (pathToSavedFile.endsWith(File.separator) ? "" : File.separator);
				pathToSavedFile += as.getText();
				System.out.println("file : " + pathToSavedFile);
				try {
					File dest = new File(pathToSavedFile);
					ImageIO.write(img, format, dest);

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}
}
