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

	public ArrayList<BinomialHeap.HeapItem> heapToItemsArray(BinomialHeap heap) {
		ArrayList<BinomialHeap.HeapItem> items = new ArrayList<>();

		BinomialHeap.HeapNode curr = heap.first;
		while (curr != null) {
			items.addAll(treeToKeysArray(curr));
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
		if (node == null)
			return 0;
		int size = 0;
		BinomialHeap.HeapNode kids = node.child;
		while (kids != null) {
			size += calcTreeSize(kids);
			kids = kids.next;
		}
		return size + 1;
	}

	public int calcHeapSize(BinomialHeap.HeapNode node) {
		if (node == null)
			return 0;
		int size = 0;
		while (node != null) {
			size += calcTreeSize(node);
			node = node.next;
		}
		return size;
	}

	public boolean isTreeSizeValid(BinomialHeap.HeapNode node) {
		return Math.abs(Math.pow(2, node.rank) - calcTreeSize(node)) < 0.5;
	}

	public boolean isTreeHeapValid(BinomialHeap.HeapNode node) {
		boolean isValidHeap = true;
		BinomialHeap.HeapNode curr = node;
		BinomialHeap.HeapNode brother;
		while (curr.child != null && isValidHeap) {
			if (curr.parent != null && curr.item.key < curr.parent.item.key) {
				isValidHeap = false;
			}
			brother = curr;
			while (brother.next != null && isValidHeap) {
				brother = brother.next;
				if (brother.parent != null && brother.item.key < brother.parent.item.key) {
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
		while (curr != null) {
			if (!isTreeSizeValid(curr)) {
				stats.brokenHeaps.add(heap);
				return;
			}
			if (!isTreeHeapValid(curr)) {
				stats.invalidHeaps.add(heap);
				return;
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
		BinomialHeap.HeapNode min = heap.first;
		BinomialHeap.HeapNode curr = heap.first;
		while (curr != null) {
			if (curr.item.key < min.item.key) {
				min = curr;
			}
			curr = curr.next;
		}
		return heap.min == min;
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