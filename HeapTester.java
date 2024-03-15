import java.util.*;

public class HeapTester {

	public class FunctionStats {
		public String name;
		public ArrayList<String> failedInputs;
		public ArrayList<BinomialHeap> failedHeaps;
		public ArrayList<String> exceptionsMsg;
		public ArrayList<BinomialHeap> brokenHeaps;
		public ArrayList<BinomialHeap> invalidHeaps;
		public HashMap<String, ArrayList<BinomialHeap>> invalidFields;

		public FunctionStats(String name) {
			this.name = name;
			this.failedInputs = new ArrayList<>();
			this.failedHeaps = new ArrayList<>();
			this.exceptionsMsg = new ArrayList<>();
			this.brokenHeaps = new ArrayList<>();
			this.brokenHeaps = new ArrayList<>();
			this.invalidHeaps = new ArrayList<>();
			this.invalidFields = new HashMap<>();
		}

		public HashMap<String, Integer> getExceptionsHist() {
			HashMap<String, Integer> hist = new HashMap<>();
			for (String msg : this.exceptionsMsg) {
				if (hist.get(msg) == null) {
					hist.put(msg, 1);
				} else {
					hist.put(msg, hist.get(msg) + 1);
				}
			}
			return hist;
		}

		public void printStats() {
			System.out.println("---- Stats for " + this.name);
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
	public static final String[] FUNCS = { "insert", "delete" };

	public HeapTester(int nHeaps, int maxHeapSize) {
		this.nHeaps = nHeaps;
		this.maxHeapSize = maxHeapSize;
		this.stats = new HashMap<String, FunctionStats>();
		for (String funcName : FUNCS) {
			this.stats.put(funcName, new FunctionStats(funcName));
		}
	}

	public void test() {
		System.out.println("Building Heaps ...");
		ArrayList<BinomialHeap> randHeaps = this.buildRandHeaps();
		System.out.println("Checking Heaps Validity for Insert ...");
		areHeapsValid(randHeaps, "insert");
		areHeapsMinValid(randHeaps, "insert");

		System.out.println("Deleting Random Nodes from Heaps ...");
		deleteRandNodes(randHeaps);
		System.out.println("Checking Heaps Validity for Delete ...");
		areHeapsValid(randHeaps, "delete");
		areHeapsMinValid(randHeaps, "delete");

		System.out.println("Done Running Tests. \n");
		printStats();
	}

	public FunctionStats getFuncStats(String name) {
		return this.stats.get(name);
	}

	public ArrayList<BinomialHeap> buildRandHeaps() {
		Random rand = new Random();
		int size;
		int key;

		ArrayList<BinomialHeap> heaps = new ArrayList<>();

		for (int i = 1; i <= nHeaps; i++) {
			size = rand.nextInt(maxHeapSize);
			BinomialHeap heap = new BinomialHeap();
			for (int j = 1; j <= size; j++) {
				key = rand.nextInt(size);
				try {
					heap.insert(key, "");
				} catch (Exception e) {
					FunctionStats stats = getFuncStats("insert");
					stats.exceptionsMsg.add(e.getMessage());
					stats.failedHeaps.add(heap);
					stats.failedInputs.add(Integer.toString(key));
				}
			}
			heaps.add(heap);
		}
		return heaps;
	}

	public void treeToKeysArray(BinomialHeap.HeapNode node, ArrayList<BinomialHeap.HeapItem> items) {
		items.add(node.item);
		if (node.child == null) {return;}
		else {
			BinomialHeap.HeapNode curr = node.child;
			for (int i = 0; i < node.rank; i++) {
				treeToKeysArray(curr, items);
				curr = curr.next;
			}
		}
	}

	public ArrayList<BinomialHeap.HeapItem> heapToItemsArray(BinomialHeap heap) {
		ArrayList<BinomialHeap.HeapItem> items = new ArrayList<>();

		BinomialHeap.HeapNode curr = heap.last;
		for (int i = 0; i < heap.numTrees(); i++) {
			treeToKeysArray(curr, items);
			curr = curr.next;
		}
		return items;
	}

	public void deleteRandNode(BinomialHeap heap) {
		Random rand = new Random();
		if (heap.size == 0) {
			return;
		}
		ArrayList<BinomialHeap.HeapItem> items = heapToItemsArray(heap);
		BinomialHeap.HeapItem toDelete = items.get(rand.nextInt(items.size()));
		try {
			heap.delete(toDelete);
		} catch (Exception e) {
			FunctionStats stats = getFuncStats("delete");
			stats.exceptionsMsg.add(e.getMessage());
			stats.failedHeaps.add(heap);
			stats.failedInputs.add(Integer.toString(toDelete.key));
		}
	}

	public void deleteRandNodes(ArrayList<BinomialHeap> heaps) {
		for (BinomialHeap heap : heaps) {
			deleteRandNode(heap);
		}
	}

	public int calcTreeSize(BinomialHeap.HeapNode node) {
		int size = 0;
		BinomialHeap.HeapNode curr = node.child;
		for (int i = 0; i < node.rank; i++) {
			size += calcTreeSize(curr);
			curr = curr.next;
		}
		return size + 1;
	}

	public boolean isTreeSizeValid(BinomialHeap.HeapNode node) {
		return Math.abs(Math.pow(2, node.rank) - calcTreeSize(node)) < 0.5;
	}

	public boolean isTreeHeapValid(BinomialHeap.HeapNode node) {
		if (node.child == null) {return true;}

		BinomialHeap.HeapNode curr = node.child;
		for (int i = 0; i < node.rank; i++) {
			if (curr.item.key < node.item.key) {return false;}
			else if (!isTreeHeapValid(curr)) {return false;}
			curr = curr.next;
		}
		return true;
	}

	public void areTreesValid(BinomialHeap heap, String funcName) {
		FunctionStats stats = getFuncStats(funcName);

		BinomialHeap.HeapNode curr = heap.last;
		for (int i = 0; i < heap.numTrees(); i++) {
			if (!isTreeHeapValid(curr)) {
				stats.invalidHeaps.add(heap);
				break;
			}
			if (!isTreeSizeValid(curr)) {
				stats.brokenHeaps.add(heap);
				break;
			}
			curr = curr.next;
		}
	}

	public void areHeapsValid(ArrayList<BinomialHeap> heaps, String funcName) {
		for (BinomialHeap heap : heaps) {
			areTreesValid(heap, funcName);
		}
	}

	public boolean isMinValid(BinomialHeap heap) {
		ArrayList<BinomialHeap.HeapItem> items = heapToItemsArray(heap);
		if (heap.empty()) {return heap.min == null;}
		else {
			BinomialHeap.HeapItem min = heap.last.item;
			for (BinomialHeap.HeapItem item: items) {
				if (item.key < min.key) {min = item;}
			}
			return heap.min == min.node;
		}
		
	}

	public void areHeapsMinValid(ArrayList<BinomialHeap> heaps, String funcName) {
		FunctionStats stats = getFuncStats(funcName);
		stats.invalidFields.put("min", new ArrayList<BinomialHeap>());

		for (BinomialHeap heap : heaps) {
			if (!isMinValid(heap)) {
				stats.invalidFields.get("min").add(heap);
			}
		}
	}

	public void printStats() {
		for (String funcName : FUNCS) {
			getFuncStats(funcName).printStats();
		}
	}
}