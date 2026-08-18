package edu.uky.cs.acta225.linkedlist;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class DoublyLinkedList<T> implements Iterable<T> {
	private LinkedListNode<T> first;
	
	public DoublyLinkedList() {
		first = null;
	}
	
	public LinkedListNode<T> add() {
		LinkedListNode<T> newNode = new LinkedListNode<T>();
		if (first == null) {
			first = newNode;
			first.linkAfter(first);
		}
		else {
			first.getPrev().linkAfter(newNode);
			first.linkBefore(newNode);
		}
		return newNode;
	}
	
	public LinkedListNode<T> add(T value) {
		var newNode = add();
		newNode.setValue(value);
		return newNode;
	}
	
	public LinkedListNode<T> getFirst() {
		return first;
	}
	
	public void remove(LinkedListNode<T> node) {
		node.getPrev().linkAfter(node.getNext());
		if (node == first) {
			first = first.getNext();
		}
		if (node == first) {
			first = null;
		}
	}
	
	public boolean contains(Object value) {
		LinkedListNode<T> node = first;
		do {
			if (value.equals(node.getValue())) {
				return true;
			}
			node = node.getNext();
		} while (node != first);
		return false;
	}
	
	private class LinkedListIterator implements Iterator<T> {
		private LinkedListNode<T> curr;
		private boolean gave;
		
		public LinkedListIterator() {
			curr = first;
			gave = false;
		}

		@Override
		public boolean hasNext() {
			return (curr != null) && (!gave || (curr != first));
		}

		@Override
		public T next() {
			if ((curr == null) || (gave && (curr == first))) {
				throw new NoSuchElementException();
			}
			var res = curr;
			curr = curr.getNext();
			gave = true;
			return res.getValue();
		}
	}
	
	public Iterator<T> iterator() {
		return new LinkedListIterator();
	}
}
