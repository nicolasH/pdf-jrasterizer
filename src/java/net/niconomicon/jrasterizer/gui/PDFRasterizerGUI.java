package net.niconomicon.jrasterizer.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import net.niconomicon.jrasterizer.gui.RendererService.RASTERIZER_TYPE;

import com.sun.media.jai.widget.DisplayJAI;

/**
 * @author Nicolas Hoibian copyright August 2010
 * 
 */

public class PDFRasterizerGUI {
	JFrame frame;

	public static final String USER_HOME = "user.home";

	JPanel contentPane;

	DisplayJAI jaiPanel;
	Previewer previewer;

	// SavePanel savePanel;
	OpenPanel openPanel;
	SaveDialog saveDialog;
	RendererService service;
	BufferedImage img;

	JScrollPane previewSP;
	JScrollPane jaiSP;
	File currentFile;
	Executor exe;

	public PDFRasterizerGUI() {

		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		contentPane = new JPanel(new BorderLayout());

		openPanel = new OpenPanel(this);
		saveDialog = new SaveDialog(this);

		jaiPanel = new DisplayJAI();
		jaiSP = new JScrollPane(jaiPanel);

		previewer = new Previewer(this);
		previewSP = new JScrollPane(previewer);

		contentPane.add(openPanel.contentPane, BorderLayout.NORTH);
		contentPane.add(previewSP, BorderLayout.CENTER);
		contentPane.setPreferredSize(new Dimension(1024, 600));
		frame.setContentPane(contentPane);

		frame.pack();
		frame.setVisible(true);
		exe = Executors.newSingleThreadExecutor();

	}

	public void setPDFFile(File f) {
		currentFile = f;
		service = RendererService.createService(RASTERIZER_TYPE.PIXELS, f);
		// savePanel.setCurrentFile(f);
		saveDialog.setCurrentFile(f);
	}

	public File getCurrentFile() {
		return currentFile;
	}

	public void setImage(BufferedImage image) {
		this.img = image;

		jaiPanel.set(this.img);
		contentPane.remove(previewSP);
		previewSP.setVisible(false);
		jaiSP.setVisible(true);
		contentPane.add(jaiSP, BorderLayout.CENTER);
		jaiSP.revalidate();
		contentPane.revalidate();
	}

	public void showExtracts(File f) {
		contentPane.remove(jaiSP);
		jaiSP.setVisible(false);
		previewSP.setVisible(true);
		contentPane.add(previewSP, BorderLayout.CENTER);
		if (previewer.getPDFToPreview() == null || previewer.getPDFToPreview().compareTo(currentFile.getAbsolutePath()) != 0) {
			try {
				previewer.setPDFToPreview(f);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		previewSP.revalidate();
		contentPane.revalidate();
		previewSP.repaint();
	}

	public void addAction(Runnable action) {
		exe.execute(action);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		for (int i = 0; i < UIManager.getInstalledLookAndFeels().length; i++) {
			System.out.println("lnfs : " + UIManager.getInstalledLookAndFeels()[i]);
		}
		PDFRasterizerGUI rast = new PDFRasterizerGUI();
	}
}
