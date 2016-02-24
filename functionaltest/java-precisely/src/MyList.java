// Example 109 from page 83 of Java Precisely second edition (The MIT Press 2005)
// Author: Peter Sestoft (sestoft@itu.dk)

import common.Mapper;

interface MyList<T> extends Iterable<T> {
  int getCount();                       // Number of elements
  T get(int i);                         // Get element at index i
  void set(int i, T item);              // Set element at index i
  void add(T item);                     // Add element at end
  void insert(int i, T item);           // Insert element at index i
  void removeAt(int i);                 // Remove element at index i
  <U> MyList<U> map(Mapper<T,U> f);     // Map f over all elements
}
