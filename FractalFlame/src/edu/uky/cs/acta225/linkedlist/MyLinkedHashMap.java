package edu.uky.cs.acta225.linkedlist;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class MyLinkedHashMap<K, V> implements Map<K, V> {
	private DoublyLinkedList<K> keyList;
	private HashMap<K, LinkedListNode<K>> nodeMap;
	private HashMap<K, V> valueMap;
	
	public MyLinkedHashMap() {
		clear();
	}

	@Override
	public int size() {
		return nodeMap.size();
	}

	@Override
	public boolean isEmpty() {
		return nodeMap.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return valueMap.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return keyList.contains(value);
	}

	@Override
	public V get(Object key) {
		return valueMap.get(key);
	}

	@Override
	public V put(K key, V value) {
//		var node = entryMap.get(key);
//		if (node == null) {
//			node = entryList.add();
//			entryMap.put(key, node);
//		}
//		V res = node.getValue().getValue();
//		node.setValue(Map.entry(key, value));
//		return res;	
		var node = nodeMap.get(key);
		if (node == null) {
			node = keyList.add(key);
			nodeMap.put(key, node);
		}
		return valueMap.put(key, value);
	}
	
	public void replaceKey(K oldKey, K newKey) {
		var node = nodeMap.get(oldKey);
		node.setValue(newKey);
		nodeMap.remove(oldKey);
		nodeMap.put(newKey, node);
		var value = valueMap.remove(oldKey);
		valueMap.put(newKey, value);
	}

	@Override
	public V remove(Object key) {
		var node = nodeMap.get(key);
		if (node != null) {
			keyList.remove(node);
		}
		nodeMap.remove(key);
		return valueMap.remove(key);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for (var entry: m.entrySet()) {
			this.put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void clear() {
		keyList = new DoublyLinkedList<K>();
		nodeMap = new HashMap<K, LinkedListNode<K>>();
		valueMap = new HashMap<K, V>();
	}
	
	private abstract class KeyListIterator<T> implements Iterator<T> {
		Iterator<K> keyListIterator;
		T last;
		
		public KeyListIterator() {
			keyListIterator = keyList.iterator();
			last = null;
		}

		@Override
		public boolean hasNext() {
			return keyListIterator.hasNext();
		}
		
		protected abstract T keyToValue(K key);

		@Override
		public T next() {
			last = keyToValue(keyListIterator.next());
			return last;
		}
		
	}
	
	private class KeySet implements Set<K> {
		public KeySet() {}
		
		private class KeySetIterator extends KeyListIterator<K> {
			public KeySetIterator() {
				super();
			}
			
			@Override
			public void remove() {
				KeySet.this.remove(last);
			}

			@Override
			protected K keyToValue(K key) {
				return key;
			}
		}
		
		@Override
		public int size() {
			return nodeMap.size();
		}

		@Override
		public boolean isEmpty() {
			return nodeMap.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			return nodeMap.containsKey(o);
		}

		@Override
		public Iterator<K> iterator() {
			return new KeySetIterator();
		}

		@Override
		public Object[] toArray() {
			Object[] arr = new Object[this.size()];
			int i = 0;
			for (K k: this) {
				arr[i++] = k;
			}
			return arr;
		}

		@Override
		public <T> T[] toArray(T[] a) {
			int i = 0;
			for (K k: this) {
				a[i++] = (T) k;
			}
			return a;
		}

		@Override
		public boolean add(K e) {
			boolean contained = nodeMap.containsKey(e);
			return !contained;
		}

		@Override
		public boolean remove(Object o) {
			boolean contained = nodeMap.containsKey(o);
			MyLinkedHashMap.this.remove(o);
			return contained;
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			for (Object o: c) {
				if (!contains(o)) {
					return false;
				}
			}
			return true;
		}

		@Override
		public boolean addAll(Collection<? extends K> c) {
			boolean contained = false;
			for (K o: c) {
				contained |= !contains(o);
				add(o);
			}
			return contained;
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			boolean changed = false;
			Iterator<K> it = this.iterator();
			while (it.hasNext()) {
				K key = it.next();
				if (!c.contains(key)) {
					changed = true;
					it.remove();
				}
			}
			return changed;
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			boolean changed = false;
			Iterator<K> it = this.iterator();
			while (it.hasNext()) {
				K key = it.next();
				if (c.contains(key)) {
					it.remove();
					changed =  true;
				}
			}
			return changed;
		}

		@Override
		public void clear() {
			MyLinkedHashMap.this.clear();
		}
	}

	@Override
	public Set<K> keySet() {
		return new KeySet();
	}
	
	private class ValuesCollection implements Collection<V> {
		@Override
		public int size() {
			return nodeMap.size();
		}

		@Override
		public boolean isEmpty() {
			return nodeMap.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			return valueMap.containsValue(o);
		}
		
		private class ValuesCollectionIterator extends KeyListIterator<V> {
			public ValuesCollectionIterator() {
				super();
			}

			@Override
			protected V keyToValue(K key) {
				return valueMap.get(key);
			}
		}

		@Override
		public Iterator<V> iterator() {
			return new ValuesCollectionIterator();
		}

		@Override
		public Object[] toArray() {
			Object[] arr = new Object[size()];
			int i = 0;
			for (V value: this) {
				arr[i++] = value;
			}
			return arr;
		}

		@Override
		public <T> T[] toArray(T[] a) {
			int i = 0;
			for (V value: this) {
				a[i++] = (T)value;
			}
			return a;
		}

		@Override
		public boolean add(V e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Object o) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			for (Object obj: c) {
				if (!contains(c)) {
					return false;
				}
			}
			return true;
		}

		@Override
		public boolean addAll(Collection<? extends V> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			MyLinkedHashMap.this.clear();
		}
		
	}

	@Override
	public Collection<V> values() {
		return new ValuesCollection();
	}
	
	private class EntrySet implements Set<Entry<K, V>> {

		@Override
		public int size() {
			return nodeMap.size();
		}

		@Override
		public boolean isEmpty() {
			return nodeMap.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			if (!(o instanceof Entry<?, ?>)) {
				return false;
			}
			Entry<Object, Object> entry = (Entry<Object, Object>)o;
			return valueMap.containsKey(entry.getKey()) && (valueMap.get(entry.getKey()) == entry.getValue());
		}
		
		private class EntrySetIterator extends KeyListIterator<Entry<K, V>> {
			public EntrySetIterator() {
				super();
			}
			
			@Override
			protected Entry<K, V> keyToValue(K key) {
				return Map.entry(key, valueMap.get(key));
			}
			
			@Override
			public void remove() {
				EntrySet.this.remove(last);
			}
		}

		@Override
		public Iterator<Entry<K, V>> iterator() {
			return new EntrySetIterator();
		}

		@Override
		public Object[] toArray() {
			Object[] arr = new Object[size()];
			int i = 0;
			for (Entry<K, V> entry: this) {
				arr[i++] = entry;
			}
			return arr;
		}

		@Override
		public <T> T[] toArray(T[] a) {
			int i = 0;
			for (Entry<K, V> entry: this) {
				a[i++] = (T)entry;
			}
			return a;
		}

		@Override
		public boolean add(Entry<K, V> e) {
			// Add is unsupported because we could end up with a set mapping a key to multiple values.
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Object o) {
			boolean contained = contains(o);
			if (contained) {
				MyLinkedHashMap.this.remove(o);
			}
			return contained;
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			for (Object obj: c) {
				if (!contains(obj)) {
					return false;
				}
			}
			return true;
		}

		@Override
		public boolean addAll(Collection<? extends Entry<K, V>> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			boolean changed = false;
			Iterator<Entry<K, V>> it = this.iterator();
			while (it.hasNext()) {
				if (!c.contains(it.next())) {
					it.remove();
					changed = true;
				}
			}
			return changed;
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			boolean changed = false;
			Iterator<Entry<K, V>> it = this.iterator();
			while (it.hasNext()) {
				if (c.contains(it.next())) {
					it.remove();
					changed = true;
				}
			}
			return changed;
		}

		@Override
		public void clear() {
			MyLinkedHashMap.this.clear();
		}
		
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return new EntrySet();
	}

}
