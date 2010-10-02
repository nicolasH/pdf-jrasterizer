package net.niconomicon.jrasterizer.utils;

/**
 * 
 */

/**
 * @author niko
 * 
 */
public class TestMemory {

	/**
	 * @param args
	 */
	public static void printMemoryInfo() {
		long mb = 1024 * 1024;
		Runtime runtime = Runtime.getRuntime();

		long maxMemory = runtime.maxMemory();
		long allocatedMemory = runtime.totalMemory();
		long freeMemory = runtime.freeMemory();

		System.out.println("Xmx : " + maxMemory / mb + " MB - used : " + (allocatedMemory - freeMemory) / mb + " MB free: " + freeMemory / mb + " MB - reserved: " + allocatedMemory / mb + " MB - total unused: " + (freeMemory + (maxMemory - allocatedMemory)) / mb + " MB");
	}

	/**
	 * 
	 * @return a number between 0 and 1 representing the amount of memory used versus the amount of memory potentially
	 *         usable.
	 */
	public static double getAvailableMemory() {
		long mb = 1024 * 1024;
		Runtime runtime = Runtime.getRuntime();

		long maxMemory = runtime.maxMemory();
		long allocatedMemory = runtime.totalMemory();
		long freeMemory = runtime.freeMemory();
		double percent = ((double) (freeMemory + maxMemory - allocatedMemory)) / (double) maxMemory;

		System.out.println("Available memory = " + percent + "%");
		return percent;
	}

	public static void main(String[] args) {
		printMemoryInfo();
	}

}
