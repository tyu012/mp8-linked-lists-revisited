import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Simple circularly-linked, doubly-linked lists with a dummy node.
 * 
 * This list supports the Fail Fast policy.
 * 
 * @author Tim Yu
 * 
 * Based on lab work completed with Keely Miyamoto and Nye Tenerelli.
 */
public class SimpleCDLL<T> implements SimpleList<T> {
  // +--------+------------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * The front of the list
   */
  Node2<T> front;

  /**
   * The number of values in the list.
   */
  int size;

  //
  // The below fields are unique to SimpleCDLL.
  //

  /**
   * A dummy node precedes the front node and follows the back node.
   */
  Node2<T> dummy;

  /**
   * To support fail fast, we keep track of the number of changes that have been made to this list.
   */
  long numChanges;

  // +--------------+------------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create an empty list.
   */
  public SimpleCDLL() {
    this.front = null;
    this.size = 0;
    this.dummy = new Node2<T>(null);
    this.numChanges = 0;
  } // SimpleDLL

  // +-----------+---------------------------------------------------------
  // | Iterators |
  // +-----------+

  public Iterator<T> iterator() {
    return listIterator();
  } // iterator()

  public ListIterator<T> listIterator() {
    return new ListIterator<T>() {
      // +--------+--------------------------------------------------------
      // | Fields |
      // +--------+

      /**
       * The position in the list of the next value to be returned.
       * Included because ListIterators must provide nextIndex and
       * prevIndex.
       */
      int pos = 0;

      /**
       * The cursor is between neighboring values, so we start links
       * to the previous and next value.
       * 
       * Note the special case of the empty list: The next field has a null pointer.
       * This is the only special case of the SimpleCDLL.
       */
      Node2<T> prev = SimpleCDLL.this.dummy;
      Node2<T> next = SimpleCDLL.this.front;

      /**
       * The node to be updated by remove or set.  Has a value of
       * null when there is no such value.
       */
      Node2<T> update = null;

      /**
       * The number of changes of the SimpleCDLL at the time of this iterator object's creation,
       * or when this iterator last mutated the SimpleCDLL.
       * 
       * If this.numChanges != SimpleCDLL.this.numChanges, throw a ConcurrentModificationException
       * when modifying the SimpleCDLL.
       * 
       * This field is necessary to support fail fast.
       */
      long numChanges = SimpleCDLL.this.numChanges;

      // +---------+-------------------------------------------------------
      // | Methods |
      // +---------+

      public void add(T val) throws UnsupportedOperationException {
        // Check list has not been changed by other iterators
        if (this.numChanges != SimpleCDLL.this.numChanges) {
          throw new ConcurrentModificationException();
        }

        // Add a node
        // Edge case: list is empty
        if (this.prev == SimpleCDLL.this.dummy && this.next == null) {
          this.next = new Node2<T>(SimpleCDLL.this.dummy, val, SimpleCDLL.this.dummy);
        }
        // Normal case: list is not empty
        else {
          this.prev = this.prev.insertAfter(val);
        }

        // Note that we cannot update
        this.update = null;

        // Increase the size
        ++SimpleCDLL.this.size;

        // Update the position.  (See SimpleArrayList.java for more of
        // an explanation.)
        ++this.pos;

        // Update numChanges.
        ++this.numChanges;
        ++SimpleCDLL.this.numChanges;
      } // add(T)

      public boolean hasNext() {
        return (this.pos < SimpleCDLL.this.size);
      } // hasNext()

      public boolean hasPrevious() {
        return (this.pos > 0);
      } // hasPrevious()

      public T next() {
        if (!this.hasNext()) {
         throw new NoSuchElementException();
        } // if
        // Identify the node to update
        this.update = this.next;
        // Advance the cursor
        this.prev = this.next;
        this.next = this.next.next;
        // Note the movement
        ++this.pos;
        // And return the value
        return this.update.value;
      } // next()

      public int nextIndex() {
        return this.pos;
      } // nextIndex()

      public int previousIndex() {
        return this.pos - 1;
      } // prevIndex

      public T previous() throws NoSuchElementException {
        if (!this.hasPrevious()) {
          throw new NoSuchElementException();
        }

        // Identify the node to update
        this.update = this.prev;
        // Advance the cursor
        this.next = this.prev;
        this.prev = this.prev.prev;

        // Note the movement
        --this.pos;
        // And return the value
        return this.update.value;
      } // previous()

      public void remove() {
        // Sanity check
        if (this.update == null) {
          throw new IllegalStateException();
        } // if

        // Update the cursor
        if (this.next == this.update) {
          this.next = this.update.next;
        } // if
        if (this.prev == this.update) {
          this.prev = this.update.prev;
          --this.pos;
        } // if

        // Update the front
        if (SimpleCDLL.this.front == this.update) {
          SimpleCDLL.this.front = this.update.next;
        } // if

        // Do the real work
        this.update.remove();
        --SimpleCDLL.this.size;

        // Note that no more updates are possible
        this.update = null;
      } // remove()

      public void set(T val) {
        // Sanity check
        if (this.update == null) {
          throw new IllegalStateException();
        } // if
        // Do the real work
        this.update.value = val;
        // Note that no more updates are possible
        this.update = null;
      } // set(T)
    };
  } // listIterator()

} // class SimpleDLL<T>
