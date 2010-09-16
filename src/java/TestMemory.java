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

		System.out.println("Xmx : " + maxMemory / mb + " MB - used : " + (allocatedMemory - freeMemory) /mb  + " MB free: " + freeMemory / mb + " MB - reserved: " + allocatedMemory / mb + " MB - total unused: " + (freeMemory + (maxMemory - allocatedMemory)) / mb + " MB");
	}

	public static void main(String[] args) {
		printMemoryInfo();
	}

}
