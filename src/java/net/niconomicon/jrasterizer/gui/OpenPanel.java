/**
 * 
 */
package net.niconomicon.jrasterizer.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.niconomicon.jrasterizer.PDFFileFilter;

/**
 * @author Nicolas Hoibian copyright August 2010
 * 
 */
public class OpenPanel {

	JPanel contentPane;

	JFileChooser sourceChooser;

	PDFFileFilter pdfFilter;

	Thread imageRendererThread;
	PDFRasterizerGUI gui;

	File pdfFile;

	JButton choose;
	JButton preview;
	JButton save;

	public OpenPanel(PDFRasterizerGUI gui) {
		this.gui = gui;
		init();
	}

	private void init() {

		contentPane = new JPanel(new GridBagLayout());

		choose = new JButton("Choose pdf to rasterize");
		choose.addActionListener(new InputActionListener());
		pdfFilter = new PDFFileFilter();

		sourceChooser = new JFileChooser();
		sourceChooser.setAcceptAllFileFilterUsed(false);
		sourceChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		sourceChooser.setFileFilter(pdfFilter);
		sourceChooser.setDialogTitle("Open image or pdf");
		sourceChooser.setCurrentDirectory(new File(System.getProperty(PDFRasterizerGUI.USER_HOME)));
		// later : choose destination, type, resolution. Later

		preview = new JButton("Back to previews");
		preview.addActionListener(new ShowPDFResolutionsPreviewListener());
		preview.setVisible(false);

		save = new JButton("Save");
		// save.addActionListener(new ShowPDFResolutionsPreviewListener());
		save.setVisible(false);

		// //////////////
		// layout
		GridBagConstraints c;
		c = new GridBagConstraints();
		c.gridy = 0;
		contentPane.add(choose, c);

		c = new GridBagConstraints();
		c.gridy = 0;
		c.weightx = 1.0;
		c.fill = c.HORIZONTAL;
		contentPane.add(new JLabel(" "), c);

		c = new GridBagConstraints();
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_END;
		contentPane.add(save, c);

		c = new GridBagConstraints();
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_END;
		contentPane.add(preview, c);

		contentPane.setName("OpenPDF");
	}

	public void switchToViewMode(boolean goToViewMode) {
		if (goToViewMode) {
			choose.setVisible(false);
			preview.setVisible(true);
			save.setVisible(true);
		} else {
			choose.setVisible(true);
			preview.setVisible(false);
			save.setVisible(false);
		}

	}

	private class SetPDFAction implements Runnable {

		public void run() {
			try {
				gui.setPDFFile(pdfFile);
				gui.showExtracts();

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private class ExtractAction implements Runnable {

		public void run() {
			try {
				gui.showExtracts();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private class InputActionListener implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {

			String s = " some file";
			int returnVal = sourceChooser.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				System.out.println("You chose to open this file: " + sourceChooser.getSelectedFile().getName());
				s = sourceChooser.getSelectedFile().getName();

				try {
					pdfFile = sourceChooser.getSelectedFile();
					Thread t = new Thread(new SetPDFAction());
					t.start();
					// Thread.sleep(100);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	// private class ShowPDFImageListener implements ActionListener {
	//
	// public void actionPerformed(ActionEvent e) {
	// // imageRendererThread = new Thread(new RenderAction());
	// // imageRendererThread.start();
	// }
	// }

	private class ShowPDFResolutionsPreviewListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			Thread prev = new Thread(new ExtractAction());
			prev.start();
		}
	}

}
