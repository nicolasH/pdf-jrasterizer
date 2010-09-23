/**
 * 
 */
package net.niconomicon.jrasterizer.gui;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.niconomicon.jrasterizer.PDFFileFilter;

/**
 * @author Nicolas Hoibian copyright August 2010
 * 
 */
public class OpenPanel {

	JPanel contentPane;

	JFileChooser sourceChooser;

	PDFFileFilter pdfFilter;
	JTextField from;

	Thread imageRendererThread;
	PDFRasterizerGUI gui;

	File pdfFile;

	public OpenPanel(PDFRasterizerGUI gui) {
		this.gui = gui;
		init();
	}

	private void init() {
		// ren = new PDFToImageRendererDPI();
		contentPane = new JPanel(new GridBagLayout());

		from = new JTextField();
		from.addFocusListener(new InputActionListener());
		JButton choose = new JButton("Choose pdf to rasterize");
		choose.addActionListener(new InputActionListener());
		pdfFilter = new PDFFileFilter();

		sourceChooser = new JFileChooser();
		sourceChooser.setAcceptAllFileFilterUsed(false);
		sourceChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		sourceChooser.setFileFilter(pdfFilter);
		sourceChooser.setDialogTitle("Open image or pdf");
		sourceChooser.setCurrentDirectory(new File(System.getProperty(PDFRasterizerGUI.USER_HOME)));
		// later : choose destination, type, resolution. Later

		JButton preview = new JButton("Back to previews");
		preview.addActionListener(new ShowPDFResolutionsPreviewListener());

		// JButton view = new JButton("show");
		// view.addActionListener(new ShowPDFImageListener());

		// //////////////
		// layout
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;
		contentPane.add(choose, c);

		c = new GridBagConstraints();
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		// c.anchor = GridBagConstraints.LINE_END;
		contentPane.add(from, c);

		c = new GridBagConstraints();
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_END;
		contentPane.add(preview, c);

		// c = new GridBagConstraints();
		// c.gridy = 0;
		// c.anchor = GridBagConstraints.LINE_END;
		// contentPane.add(view, c);

		contentPane.setName("OpenPDF");
	}

	private class SetPDFAction implements Runnable {

		public void run() {
			try {
				gui.setPDFFile(pdfFile);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private class ExtractAction implements Runnable {

		public void run() {
			try {
				gui.showExtracts(pdfFile);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private class InputActionListener implements ActionListener, FocusListener, MouseListener {

		public void mouseClicked(java.awt.event.MouseEvent e) {};

		public void mouseEntered(java.awt.event.MouseEvent e) {};

		public void mouseExited(java.awt.event.MouseEvent e) {};

		public void mousePressed(java.awt.event.MouseEvent e) {};

		public void mouseReleased(java.awt.event.MouseEvent e) {};

		public void focusGained(FocusEvent e) {
			// if the focus is gained by clicking then act.
			// if the focus is lost because some popup closed, don't act.
			System.out.println("Something gained focus");
			Component c = e.getOppositeComponent();
			System.out.println("Class : " + c);
			if (c == null) { return; }
			while (c != null && !(c instanceof JFrame)) {
				System.out.println("Class : " + c);
				if (c instanceof Dialog) { return; }
				c = c.getParent();
			}
			act();
		}

		public void focusLost(FocusEvent e) {}

		public void act() {

			String s = " some file";
			int returnVal = sourceChooser.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				System.out.println("You chose to open this file: " + sourceChooser.getSelectedFile().getName());
				s = sourceChooser.getSelectedFile().getName();

				try {
					pdfFile = sourceChooser.getSelectedFile();
					from.setText(pdfFile.getCanonicalPath());
					Thread t = new Thread(new SetPDFAction());
					t.start();
					Thread.sleep(500);
					t = new Thread(new ExtractAction());
					t.start();
				} catch (Exception e) {
					from.setText("cannot Open File");
					e.printStackTrace();
				}
			}
		}

		public void actionPerformed(ActionEvent arg0) {
			act();
		}
	}

	private class ShowPDFImageListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			// imageRendererThread = new Thread(new RenderAction());
			// imageRendererThread.start();
		}
	}

	private class ShowPDFResolutionsPreviewListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			Thread prev = new Thread(new ExtractAction());
			prev.start();
		}
	}

}
