import java.util.Random;

public class Test {
	public static Random rand = new Random();
	public static int nHeaps = 10000;
	public static int maxHeapSize = rand.nextInt(nHeaps);
	public static void main(String[] args) {
		HeapTester tester = new HeapTester(nHeaps, maxHeapSize); 
		tester.test();
	}
}