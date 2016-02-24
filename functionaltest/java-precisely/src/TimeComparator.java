// Example 128 from page 101 of Java Precisely second edition (The MIT Press 2005)
// Author: Peter Sestoft (sestoft@itu.dk)

import java.util.Comparator;

class TimeComparator implements Comparator<Time> {
  // Return neg if t1 before t2; return pos if t1 after t2; return zero if same
  public int compare(Time t1, Time t2) {
    return t1.hh != t2.hh ? t1.hh - t2.hh : t1.mm - t2.mm;
  }
}
