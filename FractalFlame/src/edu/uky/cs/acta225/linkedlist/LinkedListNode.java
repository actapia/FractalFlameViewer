package edu.uky.cs.acta225.linkedlist;

public class LinkedListNode<T> {
	private LinkedListNode<T> prev;
	private LinkedListNode<T> next;
	private T value;
	
	public LinkedListNode(LinkedListNode<T> p, LinkedListNode<T> n, T val) {
		prev = p;
		next = n;
		value = val;
	}
	
	public LinkedListNode<T> getPrev() {
		return prev;
	}

	public LinkedListNode<T> getNext() {
		return next;
	}

	public T getValue() {
		return value;
	}

	public LinkedListNode(LinkedListNode<T> p, T val) {
		this(p, null, val);
	}
	
	public LinkedListNode(T val) {
		this(null, null, val);
	}
	
	public LinkedListNode() {
		this(null, null, null);
	}

	public void setValue(T value) {
		this.value = value;
	}

	public void setPrev(LinkedListNode<T> prev) {
		this.prev = prev;
	}

	public void setNext(LinkedListNode<T> next) {
		this.next = next;
	}
	
	void linkAfter(LinkedListNode<T> after) {
		setNext(after);
		after.setPrev(this);
	}
	
	void linkBefore(LinkedListNode<T> before) {
		setPrev(before);
		before.setNext(this);
	}
	
}
