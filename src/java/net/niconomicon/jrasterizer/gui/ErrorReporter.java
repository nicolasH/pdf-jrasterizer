/**
 * 
 */
package net.niconomicon.jrasterizer.gui;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 * @author Nicolas Hoibian
 * 
 */
public class ErrorReporter {

	private static ErrorReporter reporter;
	JLabel errorDisplayer;

	private ErrorReporter(JLabel errorDisplayer) {
		this.errorDisplayer = errorDisplayer;
	}

	public static void createErrorReporter(JLabel errorDisplayer) {
		reporter = new ErrorReporter(errorDisplayer);
	}

	public static void displayError(String errorMessage) {
		updater setter = reporter.new updater(errorMessage, reporter.errorDisplayer);
		SwingUtilities.invokeLater(setter);

	}

	private class updater implements Runnable {
		String message;
		JLabel output;

		public updater(String message, JLabel output) {
			this.message = message;
			this.output = output;
		}

		public void run() {
			output.setText(message);
		}
	}
}
