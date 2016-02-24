// Example 109 from page 83 of Java Precisely second edition (The MIT Press 2005)
// Author: Peter Sestoft (sestoft@itu.dk)

class SortedList<T extends Comparable<T>> extends MyLinkedList<T> {
  // Sorted insertion
  public void insert(T x) {
    Node<T> node = first;
    while (node != null && x.compareTo(node.item) > 0)
      node = node.next;
    if (node == null)           // x > all elements; insert at end
      add(x);
    else {                      // x <= node.item; insert before node
      Node<T> newnode = new Node<T>(x);
      if (node.prev == null)    // insert as first element
        first = newnode;
      else
        node.prev.next = newnode;
      newnode.next = node;
      newnode.prev = node.prev;
      node.prev = newnode;
    }
  }
}
