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
	public int nLinks;
	public int delRankSum;

	public BinomialHeap() {
		this.first = null;
		this.last = null;
		this.min = null;
		this.size = 0;
		this.nLinks = 0;
		this.delRankSum = 0;
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
		HeapItem newItem = new HeapItem();
		newItem.key = key;
		newItem.info = info;
		HeapNode newNode = new HeapNode(newItem);
		newItem.node = newNode;
		if (this.size == 0) {
			this.first = newNode;
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
		while (curr != null) {
			if (curr.item.key < this.min.item.key) {
				this.min = curr;
			}
			curr = curr.next;
		}
	}
	
	/**
	 * 
	 * Delete the minimal item
	 *
	 */
	public void deleteMin() {
		this.delRankSum += this.min.rank;
		
		if (this.size <= 2) {
			this.first = this.min.child;
			this.min = this.first;
			this.last = this.first;
			this.size--;
			return;
		} else if(this.min.rank == 0) {
			this.first = this.first.next;
			this.size--;
			fixMin();
			return;
		} else if (this.min == this.first) {
			this.first = this.first.next;
		} else {
			getOlderBrother(this.min).next = this.min.next;
		}
		this.size = this.size - (int) (Math.pow(2,this.min.rank));
		this.meld(childrenHeap(this.min));
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
		this.nLinks++;
		HeapNode min = node1, max = node2;
		if (min.item.key > max.item.key) {
			min = node2;
			max = node1;
		}
		max.next = min.child;
		min.child = max;
		max.parent = min;
		min.rank = min.rank + 1;
		min.next = null;
		return min;
	}

	/**
	 * 
	 * Creates a new Binomial Heap containing top's children
	 * 
	 */
	public BinomialHeap childrenHeap(HeapNode top) {
		Stack<HeapNode> children = new Stack<BinomialHeap.HeapNode>();
		HeapNode current = top.child;
		while(current != null) {
			children.add(current);
			current = current.next;
		}
		BinomialHeap result = new BinomialHeap();
		result.first = children.pop();
		current = result.first;
		while(!children.isEmpty()) {
			children.peek().next = null;
			current.next = children.pop();
			current = current.next;
		}
		result.last = current;
		result.fixMin();
		result.size = (int) (Math.pow(2, top.rank) - 1);
		return result;
	}

	/**
	 * 
	 * Meld the heap with heap2
	 *
	 */
	public void meld(BinomialHeap heap2) {
		HeapNode thisNode = this.first, otherNode = heap2.first, carry = null;
		this.size += heap2.size;
		HeapNode current = new HeapNode(), newFirst = current, tmp1, tmp2;
		while (thisNode != null && otherNode != null) {
			if (carry != null && carry.rank < Math.min(thisNode.rank, otherNode.rank)) {
				current.next = carry;
				current = current.next;
				carry = null;
			} else if (thisNode.rank == otherNode.rank) {
				if (carry != null) {
					current.next = carry;
					current = current.next;
				}
				tmp1 = thisNode.next;
				tmp2 = otherNode.next;
				carry = joinNodes(thisNode, otherNode);
				thisNode = tmp1;
				otherNode = tmp2;
			} else {
				HeapNode min = otherNode;
				if (thisNode.rank < otherNode.rank) {
					min = thisNode;
					thisNode = thisNode.next;
				} else {
					otherNode = otherNode.next;
				}
				if (carry != null) {
					carry = joinNodes(min, carry);
				} else {
					current.next = min;
					current = current.next;
				}
			}
		}
		HeapNode bigger = thisNode;
		if (thisNode == null) {
			bigger = otherNode;
		}
		/*while (bigger != null) {
			if (carry != null) {
				if (carry.rank == bigger.rank) {
					tmp1 = bigger.next;
					carry = joinNodes(bigger, carry);
					bigger = tmp1;
				} else {
					current.next = carry;
					carry = null;
					current = current.next;
				}
			} else {
				current.next = bigger;
				bigger = bigger.next;
				current = current.next;
			}
		}
		if (carry != null) {
			current.next = carry;
			current = current.next;
		}*/
		while(carry != null && bigger != null && carry.rank == bigger.rank) {
			tmp1 = bigger.next;
			carry = joinNodes(bigger, carry);
			bigger = tmp1;
		}
		if (carry != null) {
			current.next = carry;
			current = current.next;
		}
		if (bigger != null) {
			current.next = bigger;
			current = current.next;
		}
		
		this.first = newFirst.next;
		this.last = current;
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

		public int getKey() {
			return key;
		}
	}

	public void displayHeap() {
		System.out.print("\nHeap : ");
		displayHeap(first);
		System.out.println("\n");
	}

	private void displayHeap(HeapNode r) {
		if (r != null) {
			displayHeap(r.child);
			System.out.print(r.item.key + " ");
			displayHeap(r.next);
		}
	}
}
