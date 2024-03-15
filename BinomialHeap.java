
/**
 * BinomialHeap
 *
 * An implementation of binomial heap over non-negative integers. Based on
 * exercise from previous semester.
 */

public class BinomialHeap {
	public int size;
	public HeapNode last;
	public HeapNode min;
	public int nLinks;
	public int delRankSum;

	public BinomialHeap() {
		this.last = null;
		this.min = null;
		this.size = 0;
		this.nLinks = 0;
		this.delRankSum = 0;
	}

	public BinomialHeap(BinomialHeap.HeapNode newNode) {
		this.last = newNode;
		this.min = newNode;
		this.size = 1;
	}

	/**
	 * 
	 * pre: key > 0
	 *
	 * Insert (key,info) into the heap and return the newly generated HeapItem.
	 *
	 */
	public HeapItem insert(int key, String info) {
		HeapItem newItem = new HeapItem();
		newItem.key = key;
		newItem.info = info;
		HeapNode newNode = new HeapNode(newItem);
		newNode.next = newNode;
		newItem.node = newNode;
		if (this.size == 0) {
			this.last = newNode;
			this.min = newNode;
			this.size = 1;
		} else {
			this.meld(new BinomialHeap(newNode));
		}
		return newItem;
	}

	/**
	 * 
	 * returns the HeapNode node2 such that node2.next == node
	 *
	 */
	public HeapNode getOlderBrother(HeapNode node) {
		HeapNode curr = this.last.next;
		while (curr.next != node) {
			curr = curr.next;
		}

		return curr;

	}

	/**
	 * 
	 * Fixes the min attribute of the BinomialHeap.
	 * 
	 */

	public void fixMin() {
		this.min = this.last;
		HeapNode curr = this.last;
		do {
			if (curr.item.key < this.min.item.key) {
				this.min = curr;
			}
			curr = curr.next;
		} while (curr != this.last);
	}

	/**
	 * 
	 * Delete the minimal item
	 *
	 */
	public void deleteMin() {
		this.delRankSum += this.min.rank;
		if (this.size <= 2) {
			this.last = this.min.child;
			this.min = this.last;
			this.size--;
		} else if (this.min.rank == 0) {
			this.last.next = this.last.next.next;
			this.size--;
			fixMin();
		} else if (this.last == this.last.next) {
			this.size--;
			this.last = this.min.child;
			fixMin();
		} else {
			HeapNode brother = getOlderBrother(this.min);
			if (this.min == this.last) {
				this.last = brother;
			} 
			brother.next = brother.next.next;
			this.size = this.size - (int) (Math.pow(2, this.min.rank));
			this.meld(childrenHeap(this.min));
		}
	}
	
	/**
	 * 
	 * Creates a new Binomial Heap containing top's children
	 * 
	 */
	public BinomialHeap childrenHeap(HeapNode top) {
		BinomialHeap result = new BinomialHeap();
		result.last = top.child;
		result.fixMin();
		result.size = (int) (Math.pow(2, top.rank) - 1);
		return result;
	}

	/**
	 * 
	 * Return the minimal HeapItem
	 *
	 */
	public HeapItem findMin() {
		return this.min.item;
	}

	/**
	 * 
	 * Heapify an item up the Heap to fix its properties.
	 * 
	 */

	public void heapifyUp(HeapNode node) {
		while (node.parent != null && node.item.key < node.parent.item.key) {
			HeapNode.swapItems(node, node.parent);
			node = node.parent;
		}

	}

	/**
	 * 
	 * @pre: 0 < diff < item.key
	 * 
	 *       Decrease the key of item by diff and fix the heap.
	 * 
	 */
	public void decreaseKey(HeapItem item, int diff) {
		item.key -= diff;
		heapifyUp(item.node);
		if (item.key < this.min.item.key) {
			this.min = item.node;
		}
	}

	/**
	 * 
	 * Delete the item from the heap.
	 *
	 */
	public void delete(HeapItem item) {
		int smallerThanMin = item.key - this.min.item.key + 1;
		decreaseKey(item, smallerThanMin);
		deleteMin();
	}

	/**
	 * 
	 * Meld the heap with heap2
	 * 
	 * @pre node1.rank == node2.rank
	 */
	public HeapNode joinNodes(HeapNode node1, HeapNode node2) {
		this.nLinks++;
		HeapNode min = node1, max = node2;
		if (min.item.key > max.item.key) {
			min = node2;
			max = node1;
		}
		if (min.child == null) {
			max.next = max;
		} else {
			HeapNode child = min.child, first = child.next;
			max.next = first;
			child.next = max;
		}
		min.child = max;
		max.parent = min;
		min.rank = min.rank + 1;
		min.next = null;
		return min;
	}

	/**
	 * 
	 * Meld the heap with heap2
	 *
	 */
	public void meld(BinomialHeap heap2) {
		this.size += heap2.size;
		HeapNode smallLast = this.last, bigLast = heap2.last;
		HeapNode smaller = this.last.next, bigger = heap2.last.next, carry = null;
		HeapNode current = new HeapNode(), newFirst = current, tmp1, tmp2;
		smallLast.next = newFirst;
		bigLast.next = newFirst;
		if (smaller.rank > bigger.rank) {
			tmp1 = smaller;
			smaller = bigger;
			bigger = tmp1;
			tmp2 = bigLast;
			bigLast = smallLast;
			smallLast = tmp2;
		}
		boolean flag = false;
		if (smaller.rank < bigger.rank) {
			do {
				tmp1 = smaller.next;
				current.next = smaller;
				current = current.next;
				smaller = tmp1;
				flag = true;
			} while (smaller != newFirst && smaller.rank < bigger.rank);
		}
		flag = false;
		while ((smaller != newFirst && bigger != newFirst) || !flag) {
			flag = true;
			if (carry != null && carry.rank < Math.min(smaller.rank, bigger.rank)) {
				current.next = carry;
				current = current.next;
				carry = null;
			} else if (smaller.rank == bigger.rank) {
				if (carry != null) {
					current.next = carry;
					current = current.next;
				}
				tmp1 = smaller.next;
				tmp2 = bigger.next;
				carry = joinNodes(smaller, bigger);
				smaller = tmp1;
				bigger = tmp2;
			} else {
				HeapNode min = bigger;
				if (smaller.rank < bigger.rank) {
					min = smaller;
					smaller = smaller.next;
				} else {
					bigger = bigger.next;
				}
				if (carry != null) {
					carry = joinNodes(min, carry);
				} else {
					current.next = min;
					current = current.next;
				}
			}
		}
		if (bigger == newFirst) {
			tmp1 = bigger;
			bigger = smaller;
			smaller = tmp1;
			tmp2 = bigLast;
			bigLast = smallLast;
			smallLast = tmp2;
		}
		while (carry != null && bigger != newFirst && carry.rank == bigger.rank) {
			tmp1 = bigger.next;
			carry = joinNodes(bigger, carry);
			bigger = tmp1;
		}
		if (carry != null) {
			current.next = carry;
			current = current.next;
		}
		if (bigger != newFirst) {
			current.next = bigger;
			current = bigLast;
		}
		this.last = current;
		this.last.next = newFirst.next;
		fixMin();
	}

	/**
	 * 
	 * Return the number of elements in the heap
	 * 
	 */
	public int size() {
		return this.size;
	}

	/**
	 * 
	 * The method returns true if and only if the heap is empty.
	 * 
	 */
	public boolean empty() {
		return this.size == 0; // should be replaced by student code
	}

	/**
	 * 
	 * Return the number of trees in the heap.
	 * 
	 */
	public int numTrees() {
		String bin = Integer.toBinaryString(this.size);
		int c = 0;
		for (int i = 0; i < bin.length(); i++) {
			if (bin.charAt(i) == '1')
				c++;
		}
		return c;
	}

	/**
	 * Class implementing a node in a Binomial Heap.
	 * 
	 */
	public class HeapNode {
		public HeapNode(BinomialHeap.HeapItem newItem) {
			this.child = null;
			this.parent = null;
			this.next = null;
			this.rank = 0;
			this.item = newItem;
		}

		public static void swapItems(BinomialHeap.HeapNode node1, BinomialHeap.HeapNode node2) {
			HeapItem item1 = node2.item, item2 = node1.item;
			item1.node = node1;
			item2.node = node2;
			node2.item = item2;
			node1.item = item1;

		}

		public HeapNode() {
			this(null);
		}

		public HeapItem item;
		public HeapNode child;
		public HeapNode next;
		public HeapNode parent;
		public int rank;

		public int getRank() {
			return rank;
		}

		public BinomialHeap.HeapNode getNext() {
			return next;
		}

		public BinomialHeap.HeapNode getChild() {
			return child;
		}

		public void setChild(BinomialHeap.HeapNode child) {
			this.child = child;
		}

		public void setNext(BinomialHeap.HeapNode next) {
			this.next = next;
		}

		public void setRank(int rank) {
			this.rank = rank;
		}

		public HeapItem getItem() {
			return item;
		}
	}

	/**
	 * Class implementing an item in a Binomial Heap.
	 * 
	 */
	public class HeapItem {
		public HeapNode node;
		public int key;
		public String info;
	}

	public void displayHeap() {
		System.out.print("\nHeap : ");
		displayHeap(this.last.next, this.last);
		System.out.println("\n");
	}

	private void displayHeap(BinomialHeap.HeapNode r, BinomialHeap.HeapNode l) {
		if (r.child != null && r.child.rank == 0)
			System.out.print(r.child.item.key + " ");
		else if(r.child != null && r.child.next != null)
			displayHeap(r.child.next, r.child);
		System.out.print(r.item.key + " ");
		if (r != l && r != null) {
			displayHeap(r.next, l);
		}
	}
}
