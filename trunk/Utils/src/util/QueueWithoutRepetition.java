package util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
/**
 * this class hold a queue and asegurate there is no repetition
 * to asegurate that when offered a element already in the queue should get back at the end 
 *  in this queue use offerAtEnd()
 * default queue implementation is LinkedList but other implementation can be 
 * given in parameter of the contructor
 * 
 * offer() and poll() should be used to access queue elements
 * @author omotelet
 *
 * @param <E>
 */
public class QueueWithoutRepetition<E> implements Queue<E>{

	HashSet<E> itsSet = new HashSet<E>();
	
	Queue<E> itsQueue = new LinkedList<E>();

	
	public QueueWithoutRepetition() {
	    
	}
	
	
	public QueueWithoutRepetition(Queue<E> aSpecialQueueImplementation) {
	    this();
		if (aSpecialQueueImplementation!=null) itsQueue = aSpecialQueueImplementation;
	}
	
	
	public E element() {
		return itsQueue.element();
	}

	/**
	 * offer an element unless aO is already contanined in the queue	 	
	 */
	public boolean offer(E aO) {
		if (aO!=null && !itsSet.contains(aO)){
			if ( itsQueue.offer(aO)) {
				itsSet.add(aO);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * offer aO if already contained put it at the end of the queue
	 * @param aO
	 * @return
	 */
	public boolean offerAtEnd(E aO) {
		if (aO ==null ) return false;
		if (itsSet.contains(aO)){
			itsQueue.remove(aO);
		}
		if ( itsQueue.offer(aO)) {
				itsSet.add(aO);
				return true;
		}
		return false;
	}
	

	public E peek() {
		return itsQueue.peek();
	}

	public E poll() {
		E theE = itsQueue.poll();
		if (theE!=null) itsSet.remove(theE);
		return theE;
	}

	public E remove() {
		E theE = itsQueue.remove();
		if (theE!=null) itsSet.remove(theE);
		return theE;
	}

	public boolean add(E aO) {
		System.out.println("not implemented");
		return false;
	}

	public boolean addAll(Collection<? extends E> aC) {
		System.out.println("not implemented");
		return false;
	}

	public void clear() {
		itsQueue.clear();
		itsSet.clear();
	}

	public boolean contains(Object aO) {
		return itsSet.contains(aO);
	}

	public boolean containsAll(Collection<?> aC) {
		return itsSet.containsAll(aC);
	}

	public boolean isEmpty() {
		return itsQueue.isEmpty();
	}

	public Iterator<E> iterator() {
		return itsQueue.iterator();
	}

	public boolean remove(Object aO) {
		if (itsSet.remove(aO))
			return itsQueue.remove(aO);
		return false; 
	}

	public boolean removeAll(Collection<?> aC) {
		System.out.println("not implemented");
		return false;
	}

	public boolean retainAll(Collection<?> aC) {
		System.out.println("not implemented");
		return false;
	}

	public int size() {
		return itsSet.size();
	}

	public Object[] toArray() {
		System.out.println("not implemented");
		return null;
	}

	public <T> T[] toArray(T[] a) {
		System.out.println("not implemented");
		return null;
	}
	
	@Override
	public String toString() {
		return itsQueue.toString();
	}
	
	
	
	
	
}
