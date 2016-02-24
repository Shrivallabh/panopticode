// Example 109 from page 83 of Java Precisely second edition (The MIT Press 2005)
// Author: Peter Sestoft (sestoft@itu.dk)

import java.io.PrintWriter;

class PrintableMyLinkedList<T extends Printable>
  extends MyLinkedList<T> implements Printable
{
  public void print(PrintWriter fs) {
    boolean firstElement = true;
    for (T x : this) {
      x.print(fs);
      if (firstElement)
        firstElement = false;
      else
        fs.print(", ");
    }
  }
}
