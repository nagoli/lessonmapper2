package util;

/**
 * An interface for ordered objects.
 * A class that is to be used as a key in an ordered data structure
 * (such as a treap) must implement this interface.
 *
 * @see Treap
 * @version 1.0, 22 April 1997
 */
public interface Ordered
{
   /**
    * Compares two ordered objects.
    * This function should implement a total order relation.
    *
    * @param anotherOrderedObject the ordered object to be compared
    * @return the value 0 if this object equals the argument;
    * a value less that 0 if this object is less than the argument;
    * and a value larger than 0 if this object is greater than
    * the argument.
    */
   int compareTo(Ordered anotherOrderedObject);
}
