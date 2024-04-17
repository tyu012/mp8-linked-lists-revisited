import java.io.PrintWriter;
import java.util.ConcurrentModificationException;
import java.util.ListIterator;
import java.util.Random;
import java.util.function.Predicate;
import org.junit.jupiter.api.DisplayNameGenerator.Simple;

/**
 * Some simple experiments with SimpleLists
 */
public class SimpleListExpt {
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  static Random rand = new Random();

  // +-----------+---------------------------------------------------
  // | Utilities |
  // +-----------+

  /**
   * Add an element using an iterator.
   */
  static void add(PrintWriter pen, ListIterator<String> it, String val)
      throws Exception {
    pen.println("Add \"" + val + "\" at position " + it.nextIndex());
    it.add(val);
  } // add(PrintWriter)

  /**
   * Print a list.
   */
  static void printList(PrintWriter pen, SimpleList<String> lst) {
    int i = 0;
    for (String val : lst) {
      pen.print(i++ + ":" + val + "\t");
    } // for
    pen.println();
  } // printList(PrintWriter, SimpleList<String>)

  /**
   * Add a variety of elements, describing what happens.
   */
  static void addExpt(PrintWriter pen, SimpleList<String> lst, 
      String[] strings) throws Exception {
    ListIterator<String> lit = lst.listIterator();

    for (String str : strings) {
      add(pen, lit, str);
      printList(pen, lst);
      pen.println();
    } // for
  } // addExpt(PrintWriter, SimpleList<String>, String[])

  /**
   * Add a variety of elements, without describing what happens
   */
  static void addStrings(PrintWriter pen, SimpleList<String> lst,
      String[] strings) throws Exception {
    ListIterator<String> lit = lst.listIterator();

    for (String str : strings) {
      lit.add(str);
    } // for
    printList(pen, lst);
    pen.println();
  } // addStrings

  /**
   * Remove a variety of elements, moving forward.
   */
  static void removeForwardExpt(PrintWriter pen, SimpleList<String> lst,
      Predicate<String> pred) throws Exception {
    ListIterator<String> lit = lst.listIterator();

    while (lit.hasNext()) {
      String str = lit.next();
      if (pred.test(str)) {
        pen.println("Remove " + str);
        lit.remove();
        printList(pen, lst);
        pen.println();
      } // if
    } // while
  } // removeForwardExpt(PrintWriter, SimpleList<String>, Predicate<String>)

  /**
   * Remove a variety of elements, moving backward.
   */
  static void removeBackwardExpt(PrintWriter pen, SimpleList<String> lst,
      Predicate<String> pred) throws Exception {
    ListIterator<String> lit = lst.listIterator();

    // Advance to the end of the list
    while (lit.hasNext()) {
      lit.next();
    } // while

    // And then back up
    while (lit.hasPrevious()) {
      String str = lit.previous();
      if (pred.test(str)) {
        pen.println("Remove " + str);
        lit.remove();
        printList(pen, lst);
        pen.println();
      } // if
    } // while
  } // removeBackwardExpt(PrintWriter, SimpleList<String>, Predicate<String>)

  /**
   * Randomly remove n elements, moving forward and backward.
   *
   * @pre n
   */
  static void randomWalkRemove(PrintWriter pen, SimpleList<String> lst,
      int n) {
    ListIterator<String> lit = lst.listIterator();

    for (int i = 0; i < n; i++) {
      String val = "";

      // Random walk
      for (int j = 0; j < 5; j++) {
        if (!lit.hasNext() || (lit.hasPrevious() && rand.nextInt(2) == 0)) {
          pen.println("Backward to " + lit.previousIndex());
          val = lit.previous();
        } else {
          pen.println("Forward to " + lit.nextIndex());
          val = lit.next();
        } // if/else
      } // for j
      pen.println("Removing " + val);
      lit.remove();
      printList(pen, lst);
    } // for i
  } // randomWalkRemove(n)

  /**
   * Tries to add two elements using two iterators, both created in the beginning of method call.
   * 
   * Second add operation should fail if the list fails fast.
   */
  static void failFastExpt(PrintWriter pen, SimpleList<String> lst) {
    ListIterator<String> lit1 = lst.listIterator();
    ListIterator<String> lit2 = lst.listIterator();
    
    pen.println("Adding first element using iterator 1... ");
    lit1.add("should work");
    printList(pen, lst);

    try {
      pen.println("Adding second element using iterator 2... ");
      lit2.add("should NOT work");
      printList(pen, lst);
      pen.println("List does not fail fast.");
    } catch (ConcurrentModificationException e) {
      pen.println("List fails fast.");
      pen.println(e);
    } catch (Exception e) {
      pen.println("List does not fail fast.");
      pen.println(e);
    }
  }

  // +-------------+-------------------------------------------------
  // | Experiments |
  // +-------------+

  static void expt1(PrintWriter pen, SimpleList<String> lst) throws Exception {
    pen.println("Experiment 1: Add a variety of elements.");
    addExpt(pen, lst, new String[] {"A", "B", "C"});
    addExpt(pen, lst, new String[] {"X", "Y", "Z"});
    pen.println();
  } // expt1(PrintWriter, SimpleList<String>)

  static void expt2(PrintWriter pen, SimpleList<String> lst) throws Exception {
    pen.println("Experiment 2: Remove alternating elements, moving forward.");
    final Counter counter = new Counter();
    addStrings(pen, lst, new String[] {"A", "B", "C", "D", "E", "F", "G"});
    removeForwardExpt(pen, lst, (str) -> (counter.get() % 2) == 0);
    pen.println();
  } // expt2(PrintWriter, SimpleList<String>)

  static void expt3(PrintWriter pen, SimpleList<String> lst) throws Exception {
    pen.println("Experiment 3: Remove random elements, moving forward.");
    addStrings(pen, lst, new String[] {"A", "B", "C", "D", "E", "F", "G"});
    removeForwardExpt(pen, lst, (str) -> rand.nextInt(2) == 0);
    pen.println();
  } // expt3(PrintWriter, SimpleList<String>

  static void expt4(PrintWriter pen, SimpleList<String> lst, int n) 
      throws Exception {
    pen.println("Experiment 4: Removing elements with a random walk.");
    addStrings(pen, lst, new String[] {"A", "B", "C", "D", "E", "F", "G"});
    try {
      randomWalkRemove(pen, lst, n);
    } catch (Exception e) {
      pen.println("Experiment ended early because " + e.toString());
    } // try/catch
    pen.println();
  } // expt4(PrintWriter, SimpleList<String>, int)

  static void expt5(PrintWriter pen, SimpleList<String> lst) throws Exception {
    pen.println("Experiment 5: Remove random elements, moving backwards.");
    addStrings(pen, lst, new String[] {"A", "B", "C", "D", "E", "F", "G"});
    removeBackwardExpt(pen, lst, (str) -> rand.nextInt(2) == 0);
    pen.println();
  } // expt5(PrintWriter, SimpleList<String>

  static void expt6(PrintWriter pen, SimpleList<String> lst) throws Exception {
    pen.println("Experiment 6: Remove alternating elements, moving backwards.");
    final Counter counter = new Counter();
    addStrings(pen, lst, new String[] {"A", "B", "C", "D", "E", "F", "G"});
    removeBackwardExpt(pen, lst, (str) -> (counter.get() % 2) == 0);
    pen.println();
  } // expt6(PrintWriter, SimpleList<String>)

  static void expt7(PrintWriter pen, SimpleList<String> lst) throws Exception {
    pen.println("Experiment 7: Fail fast test.");
    failFastExpt(pen, lst);
  }
} // class SimpleListExpt


/**
 * A simple counter.
 */
class Counter {
  int val = 0;
  int get() {
    return val++;
  } // get()
} // class Counter
