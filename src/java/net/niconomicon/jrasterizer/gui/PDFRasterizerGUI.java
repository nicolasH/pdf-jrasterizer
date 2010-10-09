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
import javax.swing.SwingUtilities;
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

	int currentPage = 0;

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
		this.frame.setTitle(f.getAbsolutePath());
		saveDialog.setCurrentFile(f);
	}

	public File getCurrentFile() {
		return currentFile;
	}

	public void setImage(BufferedImage image, int page) {
		this.img = image;
		jaiPanel.set(this.img);
		contentPane.remove(previewSP);
		previewSP.setVisible(false);
		jaiSP.setVisible(true);
		jaiPanel.revalidate();
		contentPane.add(jaiSP, BorderLayout.CENTER);
		contentPane.revalidate();
		jaiSP.revalidate();
		openPanel.switchToViewMode(true);
		this.currentPage = page;
	}

	public void showSaveImageDialog(int page, int maxPage, Dimension sides) {
		if (page == 0) {
			page = currentPage;
		}
		if (maxPage == 0) {
			maxPage = service.getPageCount();
		}
		if (null == sides) {
			if (null != img) {
				sides = new Dimension(img.getWidth(), img.getHeight());
			} else {
				throw new IllegalArgumentException("Someone tried to call the save method while not giving correct arguments : the desired dimensions are null and no image was displayed so it cannot guess it.");
			}
		}
		saveDialog.save(page, maxPage, sides);
	}

	public void showExtracts() {
		contentPane.remove(jaiSP);
		jaiSP.setVisible(false);
		previewSP.setVisible(true);
		contentPane.add(previewSP, BorderLayout.CENTER);
		if (previewer.getPDFToPreview() == null || previewer.getPDFToPreview().compareTo(currentFile.getAbsolutePath()) != 0) {
			Runnable r = new Runnable() {
				public void run() {
					try {
						previewer.setPDFToPreview(currentFile);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			};
			SwingUtilities.invokeLater(r);
		}
		openPanel.switchToViewMode(false);
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
