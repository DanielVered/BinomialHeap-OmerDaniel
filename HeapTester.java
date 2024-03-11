import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class HeapTester
{
	
	public class FunctionStats
	{
		public String name;
		public ArrayList<String> failedInputs;
		public ArrayList<BinomialHeap> failedHeaps;
		public ArrayList<String> exceptionsMsg;
		public ArrayList<BinomialHeap> brokenHeaps;
		public ArrayList<BinomialHeap> invalidHeaps;
		public HashMap<String, ArrayList<BinomialHeap>> invalidFields;
		
		public FunctionStats(String name) {
			this.name = name;
		}
		
		public HashMap<String, Integer> getExceptionsHist() {
			return null;
		}
		
		public void printStats() {
			System.out.println("Number of Failures: " + failedHeaps.size());
			System.out.println("Exceptions Histogram: " + getExceptionsHist());
			System.out.println("Number of Broken Heaps: " + brokenHeaps.size());
			System.out.println("Number of Invalid Heaps: " + invalidHeaps.size());
			System.out.println("Number of Invalid Min Field Heaps: " + invalidFields.get("min").size());
		}
	}
	
	public HashMap<String, FunctionStats> stats;
	public int nHeaps;
	public int maxHeapSize;
	public static final String[] FUNCS = {"insert", "delete"};
	
	public HeapTester(int nHeaps, int maxHeapSize) {
		this.nHeaps = nHeaps;
		this.maxHeapSize = maxHeapSize;
		this.stats = new HashMap<String, FunctionStats>();
		for (String funcName: FUNCS) {
			this.stats.put(funcName, new FunctionStats(funcName));
		}
	}
	
	public void test() {
		ArrayList<BinomialHeap> randHeaps = this.buildRandHeaps();
		areHeapsValid(randHeaps, "insert");
		areHeapsMinValid(randHeaps, "insert");
		
		deleteRandNodes(randHeaps);
		areHeapsValid(randHeaps, "delete");
		areHeapsMinValid(randHeaps, "delete");
		
		
		printStats();
		
	}
	
	public FunctionStats getFuncStats(String name) {
		return this.stats.get(name);
	}
	
	public ArrayList<BinomialHeap> buildRandHeaps(){
		Random rand = new Random();
		int size; int key;
		
		ArrayList<BinomialHeap> heaps = new ArrayList<>();
		
		for (int i = 1; i <= nHeaps; i++) {
			size = rand.nextInt(maxHeapSize);
			BinomialHeap heap = new BinomialHeap();
			for (int j = 1; j<= size; j++) {
				key = rand.nextInt(size);
				try {
					heap.insert(key, "");
				}
				catch (Exception e) {
					String msg = e.getMessage();
					FunctionStats stats = getFuncStats("insert");
					stats.exceptionsMsg.add(msg);
					stats.failedHeaps.add(heap);
					stats.failedInputs.add(Integer.toString(key));
				}
			}
			heaps.add(heap);
		}
		return heaps;
	}
	
	public ArrayList<BinomialHeap.HeapItem> treeToKeysArray(BinomialHeap.HeapNode node) {
		ArrayList<BinomialHeap.HeapItem> items = new ArrayList<>();
		
		BinomialHeap.HeapNode curr = node, brother;
		items.add(node.item);
		while (curr.child != null) {
			curr = curr.child;
			items.add(curr.item);
			brother = curr;
			while (brother.next != null) {
				brother = brother.next;
				items.add(brother.item);
			}
		}
		return items;
	}
	
	public ArrayList<BinomialHeap.HeapItem> heapToKeysArray(BinomialHeap heap) {
		ArrayList<BinomialHeap.HeapItem> items = new ArrayList<>();
		
		BinomialHeap.HeapNode curr = heap.first;
		while (curr.next != null) {
			items.addAll(treeToKeysArray(curr));
		}
		return items;
	}
	
	
	public void deleteRandNode(BinomialHeap heap) {
		Random rand = new Random();
		ArrayList<BinomialHeap.HeapItem> items = heapToKeysArray(heap);
		BinomialHeap.HeapItem toDelete = items.get(rand.nextInt(items.size()));
		heap.delete(toDelete);
	}
	
	public void deleteRandNodes(ArrayList<BinomialHeap> heaps) {
		for (BinomialHeap heap: heaps) {
			deleteRandNode(heap);
		}
	}
	
	public int calcTreeSize(BinomialHeap.HeapNode node) {
		int counter = 1;
		BinomialHeap.HeapNode curr = node.child;
		BinomialHeap.HeapNode brother;
		while (curr.child != null) {
			brother = curr;
			while (brother.next != null) {
				brother = brother.next;
				counter++;
			}
			curr = curr.child;
			counter++;
		}
		
		return counter;
	}
	
	public boolean isTreeSizeValid(BinomialHeap.HeapNode node) {
		return (double)node.rank == Math.log(calcTreeSize(node));
	}
	
	public boolean isTreeHeapValid(BinomialHeap.HeapNode node) {
		boolean isValidHeap = true;
		BinomialHeap.HeapNode curr = node.child;
		BinomialHeap.HeapNode brother;
		while (curr.child != null && isValidHeap) {
			if (curr.item.key < curr.parent.item.key) {
				isValidHeap = false;
			}
			brother = curr;
			while (brother.next != null && isValidHeap) {
				brother = brother.next;
				if (brother.item.key < brother.parent.item.key) {
					isValidHeap = false;
				}
			}
			curr = curr.child;
		}
		return isValidHeap;
	}
	
	public void areTreesValid(BinomialHeap heap, String funcName) {
		FunctionStats stats = getFuncStats(funcName);
		
		BinomialHeap.HeapNode curr = heap.first;
		while (curr.next != null) {
			if (!isTreeSizeValid(curr)) {
				stats.brokenHeaps.add(heap);
			}
			if (!isTreeHeapValid(curr)) {
				stats.invalidHeaps.add(heap);
			}
		}
	}
	
	public void areHeapsValid(ArrayList<BinomialHeap> heaps, String funcName) {
		for (BinomialHeap heap: heaps) {
			areTreesValid(heap, funcName);
		}
	}
	
	public boolean isMinValid(BinomialHeap heap) {
		BinomialHeap.HeapNode min = heap.first;
		BinomialHeap.HeapNode curr = heap.first;
		while (curr.next != null) {
			if (curr.item.key < min.item.key) {
				min = curr;
			}
		}
		return heap.min == min;
	}
	
	public void areHeapsMinValid(ArrayList<BinomialHeap> heaps, String funcName) {
		FunctionStats stats = getFuncStats(funcName);
		stats.invalidFields.put("min", new ArrayList<BinomialHeap>());
		
		for (BinomialHeap heap: heaps) {
			if (!isMinValid(heap)) {
				stats.invalidFields.get("min").add(heap);
			}
		}
	}
	
	public void printStats() {
		for (String funcName: FUNCS) {
			getFuncStats(funcName).printStats();
		}
	}
}
