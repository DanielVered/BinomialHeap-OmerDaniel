import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;

public class Exp {
	
	public static void main(String[] args) {
		ExpStats firstExp = new ExpStats("firstExp");
		ExpStats secondExp = new ExpStats("secondExp");
		ExpStats ThirdExp = new ExpStats("ThirdExp");
		
		long start, end;
		for (int i = 1; i <= 6; i++) {
			
			firstExp.setNewI(i);
			start = getCurrTime();
			BinomialHeap heap1 = buildHeap(false, true, i);
			end = getCurrTime();
			firstExp.logData(i, end - start, heap1);
			
			secondExp.setNewI(i);
			start = getCurrTime();
			BinomialHeap heap2 = buildHeap(true, true, i);
			deleteMinPoly(heap2);
			end = getCurrTime();
			secondExp.logData(i, end - start, heap2);
			
			ThirdExp.setNewI(i);
			start = getCurrTime();
			BinomialHeap heap3 = buildHeap(false, false, i);
			deleteMinLim(Math.pow(2, 5) - 1, heap3);
			end = getCurrTime();
			ThirdExp.logData(i, end - start, heap3);
			
		}
		
		firstExp.printData();
		secondExp.printData();
		ThirdExp.printData();
	}
	
	public static class ExpStats {
		public HashMap<Integer, HashMap<String, Long>> stats;
		public String name;
		
		public ExpStats(String name) {
			this.name = name;
			this.stats = new HashMap<>();
		}
		
		public void setNewI(int i) {
			this.stats.put(i, new HashMap<String, Long>());
		}
		
		public void setFieldI(int i, String name, long value) {
			this.stats.get(i).put(name, value);
		}
		
		public void logData(int i, long timeElapsed, BinomialHeap heap) {
			this.setFieldI(i, "timeElapsed", timeElapsed);
			this.setFieldI(i, "nTrees", heap.numTrees());
			this.setFieldI(i, "nLinks", heap.nLinks);
		}
		
		public void printData() {
			System.out.println("---- Stats for " + this.name);
			for (int i = 1; i <= 6; i++) {
				System.out.println("-- i = " + i + ":" + this.stats.get(i));
			}
		}
	}
	
	public static long getCurrTime() {
		return System.currentTimeMillis();
	}
	
	public static ArrayList<Integer> getKeys(boolean randomOrder, boolean ascending, int i) {
		int n = (int) (Math.pow(3, i + 5) - 1);
		ArrayList<Integer> keys = new ArrayList<>();
		for (int j = 1; j <= n; j++) {
			keys.add(j);
		}
		if (randomOrder) {
			Collections.shuffle(keys);
		}
		else if (!ascending) {
			Collections.reverse(keys);
		}
		
		return keys;
	}
	
	public static BinomialHeap buildHeap(boolean randomOrder, boolean ascending, int i) {
		BinomialHeap heap = new BinomialHeap();
		
		ArrayList<Integer> keys = getKeys(randomOrder, ascending, i);
		for (int key: keys) {
			heap.insert(key, "");
		}
		return heap;
	}
	
	public static void deleteMinPoly(BinomialHeap heap) {
		for (int j = 0; j < heap.size / 2; j++) {
			heap.deleteMin();
		}
	}
	
	public static void deleteMinLim(double limit, BinomialHeap heap) {
		while (heap.size() > limit) {
			heap.deleteMin();
		}
	}	
}