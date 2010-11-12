package net.niconomicon.jrasterizer.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import net.niconomicon.jrasterizer.RendererService;
import net.niconomicon.jrasterizer.RendererService.RASTERIZER_TYPE;

//import com.sun.media.jai.widget.DisplayJAI;
import com.sun.pdfview.PDFParseException;

/**
 * 
 * The 'main' class, which is also the main frame class.
 * 
 * @author Nicolas Hoibian copyright August 2010
 * 
 */

public class PDFRasterizerGUI {
	JFrame frame;

	public static final String USER_HOME = "user.home";
	public static final String WELCOME_TEXT_STRING = "<html><body><center><b>Welcome.</b></center><p>This app is based on the pdf-renderer library from java.net " + "(https://pdf-renderer.dev.java.net/) . This app is under light development and is therefore limited in scope and functionnality. The pdf-renderer library is also under development, and is not perfect yet. There might be some rendering issues with PDFs" + " that embed fonts, advanced PDF function for gradients or odd formatting.</p></body></html>";

	JPanel contentPane;

	BackgroundPanel imagePanel;
	// DisplayJAI is a system dependent binary from https://jai-core.dev.java.net/
	// DisplayJAI jaiPanel;
	Previewer previewer;

	// SavePanel savePanel;
	OpenPanel openPanel;
	SaveDialog saveDialog;
	RendererService service;
	BufferedImage img;

	JScrollPane previewSP;
	JScrollPane imageSP;
	File currentFile;
	Executor exe;

	JLabel errorPanel;

	int currentPage = 0;

	boolean JAI_exists = false;

	public PDFRasterizerGUI() {

		errorPanel = new JLabel(WELCOME_TEXT_STRING);
		ErrorReporter.createErrorReporter(errorPanel);

		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		contentPane = new JPanel(new BorderLayout());

		openPanel = new OpenPanel(this);
		saveDialog = new SaveDialog(this);

		imagePanel = new BackgroundPanel();
		imageSP = new JScrollPane(imagePanel);

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
		try {
			service = RendererService.createService(RASTERIZER_TYPE.PIXELS, f);
		} catch (IOException ex) {
			if (ex instanceof PDFParseException) {
				ErrorReporter.displayError("Sorry. The PDF could not be parsed");
			} else {
				ErrorReporter.displayError("Sorry. The PDF could not be opened");
			}
		}
		this.frame.setTitle(f.getAbsolutePath());
		saveDialog.setCurrentFile(f);
	}

	public File getCurrentFile() {
		return currentFile;
	}

	public void setImage(BufferedImage image, int page) {
		this.img = image;
		imagePanel.setImage(image);
		imagePanel.setOpaque(false);
		imagePanel.invalidate();
		contentPane.remove(previewSP);
		previewSP.setVisible(false);

		contentPane.add(imageSP, BorderLayout.CENTER);
		imagePanel.setVisible(true);
		imageSP.setVisible(true);
		imagePanel.revalidate();
		imageSP.revalidate();
		openPanel.switchToViewMode(true);
		this.currentPage = page;
		ErrorReporter.displayError("");
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
		contentPane.remove(imageSP);
		imageSP.setVisible(false);
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
		ErrorReporter.displayError("");
	}

	public void addAction(Runnable action) {
		exe.execute(action);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String nimbus = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
		for (int i = 0; i < UIManager.getInstalledLookAndFeels().length; i++) {
			if (nimbus.compareTo(UIManager.getInstalledLookAndFeels()[i].getClassName()) == 0) {
				UIManager.setLookAndFeel(nimbus);
			}
		}
		PDFRasterizerGUI gui = new PDFRasterizerGUI();
	}
}
