/**
 * 
 */
package net.niconomicon.jrasterizer;

import java.awt.FileDialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * @author Nicolas Hoibian copyright August 2010
 * 
 */

public class SavePanel {

	JTextField to;
	JTextField as;

	JComboBox formats;
	JFileChooser dirChooser;
	FileDialog dirChooserOSX;

	PDFRasterizerGUI gui;

	JButton save;

	JPanel contentPane;
	File originalPDF;

	public SavePanel(PDFRasterizerGUI gui) {
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

		JButton chooseS = new JButton("Choose directory to save in");
		chooseS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread t = new Thread(new RootDirSetter());
				t.start();
			}
		});

		to = new JTextField();
		as = new JTextField();

		JLabel labelFormats = new JLabel("  Image format : ");
		JLabel labelFileName = new JLabel(" Image name : ");
		formats = new JComboBox(new String[] { "png", "jpg" });
		formats.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String txt = as.getText();
				// replace any existing.
				if (txt.lastIndexOf(".") > 0) {
					txt = txt.substring(0, txt.lastIndexOf("."));
				}
				txt += "." + formats.getSelectedItem();
				as.setText(txt);

			}
		});
		save = new JButton("Save Image");
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread saver = new Thread(new SaveImageToFormat((String) formats.getSelectedItem()));
				save.setEnabled(false);
				saver.start();
			}
		});

		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;
		c.gridwidth = 2;
		contentPane.add(chooseS, c);

		c = new GridBagConstraints();
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.anchor = GridBagConstraints.LINE_END;
		contentPane.add(to, c);

		c = new GridBagConstraints();
		c.gridy = 1;
		contentPane.add(labelFormats, c);

		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.EAST;
		contentPane.add(formats, c);

		c = new GridBagConstraints();
		contentPane.add(labelFileName, c);

		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		contentPane.add(as, c);

		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_END;
		contentPane.add(save, c);
	};

	public void setCurrentFile(File pdfFile) {
		originalPDF = pdfFile;
		SwingUtilities.invokeLater(new DefaultFileSetter());
	}

	private class DefaultFileSetter implements Runnable {

		public void run() {
			to.setText(originalPDF.getParent());
			String fName = originalPDF.getName();
			if (fName.toLowerCase().endsWith(".pdf")) {
				// get extension, replace it with the selected element
				fName = fName.substring(0, fName.lastIndexOf("."));
			}
			fName += "_page_1." + formats.getSelectedItem();
			as.setText(fName);
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

		public SaveImageToFormat(String format) {
			this.format = format;
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
			BufferedImage img = gui.getImage();
			String pathToSavedFile = to.getText();
			pathToSavedFile += (pathToSavedFile.endsWith(File.separator) ? "" : File.separator);
			pathToSavedFile += as.getText();
			System.out.println("file : " + pathToSavedFile);
			try {
				File dest = new File(pathToSavedFile);
				ImageIO.write(img, (String) formats.getSelectedItem(), dest);

			} catch (Exception ex) {
				ex.printStackTrace();
			}
			save.setEnabled(true);
		}
	}
}
