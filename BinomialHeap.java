
/**
 * BinomialHeap
 *
 * An implementation of binomial heap over non-negative integers. Based on
 * exercise from previous semester.
 */

import java.util.*;

public class BinomialHeap {
	public int size;
	public HeapNode first, last;
	public HeapNode min;

	public BinomialHeap() {
		this.first = null;
		this.last = null;
		this.min = null;
		this.size = 0;
	}

	public BinomialHeap(BinomialHeap.HeapNode newNode) {
		this.first = newNode;
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
		this.size++;
		HeapItem newItem = new HeapItem();
		newItem.key = key;
		newItem.info = info;
		HeapNode newNode = new HeapNode(newItem);
		newItem.node = newNode;
		if (this.size == 1) {
			this.first = newNode;
			this.last = newNode;
			this.min = newNode;
		} else {
			this.meld(new BinomialHeap(newNode));
		}
		return newItem; // should be replaced by student code
	}

	/**
	 * 
	 * returns the HeapNode node2 such that node2.next == node
	 *
	 */
	public HeapNode getOlderBrother(HeapNode node) {

		HeapNode curr = this.first;

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
		this.min = this.first;
		HeapNode curr = this.first;
		while (curr.next != null) {
			if (curr.item.key < this.min.item.key) {
				this.min = curr;
			}
		}
	}

	/**
	 * 
	 * Delete the minimal item
	 *
	 */
	public void deleteMin() {
		BinomialHeap minSubHeap = new BinomialHeap();
		minSubHeap.first = this.min.child;
		while (minSubHeap.first.next != null) {
			minSubHeap.last = minSubHeap.first.next;
		}
		minSubHeap.fixMin();
		HeapNode minOlderBrother = this.getOlderBrother(this.min);
		minOlderBrother.next = this.min.next;
		this.meld(minSubHeap);
		this.fixMin();
		this.size -= 1;
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
			HeapItem tmp = node.item;
			node.item = node.parent.item;
			node.parent.item = tmp;
		}

	}

	/**
	 * 
	 * pre: 0 < diff < item.key
	 * 
	 * Decrease the key of item by diff and fix the heap.
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
		System.out.println("join " + node1.item.key + " and " + node2.item.key);
		HeapNode min = node1, max = node2;
		if (min.item.key > max.item.key) {
			min = node2;
			max = node1;
		}
		HeapNode joined = new HeapNode();
		joined.item = min.item;
		joined.parent = null;
		joined.rank = min.rank + 1;
		joined.child = max;
		max.next = min.child;
		max.parent = joined;
		return joined;
	}

	/**
	 * 
	 * Meld the heap with heap2
	 *
	 */
	public void meld(BinomialHeap heap2) {
		HeapNode thisNode = this.first, otherNode = heap2.first, carry = null;
		if (heap2.min.item.key < this.min.item.key)
			this.min = heap2.min;
		this.size += heap2.size;
		HeapNode current = new HeapNode(), newFirst = current, tmp1, tmp2;
		while (thisNode != null && otherNode != null) {
			if (carry != null && carry.rank < Math.min(thisNode.rank, otherNode.rank)) {
				current.next = carry;
				carry = null;
			} else if (thisNode.rank == otherNode.rank) {
				/*
				 * System.out.println("1+1"); System.out.println("thisNode is " +
				 * thisNode.item.key); System.out.println("thisNode.next is " + thisNode.next);
				 * System.out.println("otherNode is " + otherNode.item.key);
				 * System.out.println("carry is " + carry);
				 */
				if (carry != null) {
					current.next = carry;
				}
				tmp1 = thisNode.next;
				tmp2 = otherNode.next;
				carry = joinNodes(thisNode, otherNode);
				thisNode = tmp1;
				otherNode = tmp2;
				/*
				 * System.out.println("thisNode is " + thisNode);
				 * System.out.println("otherNode is " + otherNode);
				 * System.out.println("carry is " + carry);
				 */
			} else {
				// find min
				HeapNode min = otherNode;
				if (thisNode.rank < otherNode.rank) {
					min = thisNode;
				}
				// work with min
				if (carry != null) {
					carry = joinNodes(min, carry);
				} else {
					current.next = min;
				}
				if (min == thisNode) {
					thisNode = thisNode.next;
				} else {
					otherNode = otherNode.next;
				}
			}
			if (current.next != null)
				current = current.next;
		}
		HeapNode bigger = thisNode;
		if (thisNode == null)
			bigger = otherNode;
		/*
		 * if (carry != null && bigger != null) { System.out.println(carry.rank +
		 * " is rank of carry"); System.out.println(bigger.rank + " is rank of bigger");
		 * }
		 */
		while (bigger != null) {
			if (carry != null) {
				if (carry.rank == bigger.rank) {
					tmp1 = bigger.next;
					carry = joinNodes(bigger, carry);
					bigger = tmp1;
				} else {
					current.next = carry;
					carry = null;
				}
			} else {
				current.next = bigger;
				bigger = bigger.next;
			}
			if (current.next != null)
				current = current.next;
		}
		current.next = carry;
		if (current.next != null)
			current = current.next;
		this.first = newFirst.next;
		this.last = current;
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
			if (bin.charAt(i) == 1)
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

		public HeapNode() {
			this(null);
		}

		public HeapItem item;
		public HeapNode child;
		public HeapNode next;
		public HeapNode parent;
		public int rank;
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

	public static void main(String[] args) {
		BinomialHeap testHeap = new BinomialHeap();
		ArrayList<Integer> lst = new ArrayList<Integer>();
		for (int i = 0; i <= 63; i++) {
			lst.add(i);
		}
		Collections.shuffle(lst);
		for (Integer i : lst) {
			testHeap.insert(i, "Hi!");
		}
		HeapNode current = testHeap.first;
		while (current != null) {
			System.out.println(current.item.key);
			current = current.child;
		}
	}

}
