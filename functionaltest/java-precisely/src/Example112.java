// Example 112 from page 85 of Java Precisely second edition (The MIT Press 2005)
// Author: Peter Sestoft (sestoft@itu.dk)

import common.Mapper;

/*
   This generic interface represents a function from A to R; in C# one
   would use a generic delegate instead.

   In MyLinkedList method equals(Object), the cast to MyList<T> is
   unchecked and may fail at runtime.
*/

// A generic linked-list class with elements of type T

class Example112 {
  public static void main(String[] args) {
    MyLinkedList<Double> dLst 
      = new MyLinkedList<Double>(7.0, 9.0, 13.0, 0.0);
    for (double d : dLst)
      System.out.print(d + " ");
    System.out.println();
    MyList<Integer> iLst = 
      dLst.map(new Mapper<Double, Integer>() {
        public Integer call(Double d) { 
          return d < 0 ? -1 : d > 0 ? +1 : 0;
        }
      });
    for (int i : iLst)
      System.out.print(i + " ");
    System.out.println();
    MyList<String> sLst = 
      dLst.map(new Mapper<Double, String>() {
        public String call(Double d) { 
          return "s" + d; 
        }
      });
    for (String s : sLst)
      System.out.print(s + " ");
    System.out.println();
    // Testing SortedList<MyString>
    SortedList<MyString> sortedLst = new SortedList<MyString>();
    sortedLst.insert(new MyString("New York"));
    sortedLst.insert(new MyString("Rome"));
    sortedLst.insert(new MyString("Dublin"));
    sortedLst.insert(new MyString("Riyadh"));
    sortedLst.insert(new MyString("Tokyo"));
    for (MyString s : sortedLst)
      System.out.print(s.s + "   ");
    System.out.println();
  }
}


