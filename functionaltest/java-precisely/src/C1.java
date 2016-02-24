// Example 34 from page 27 of Java Precisely second edition (The MIT Press 2005)
// Author: Peter Sestoft (sestoft@itu.dk)


class C1 {
  static void m1(double d) { System.out.println("11d"); }
  void m1(int i)      { System.out.println("11i"); }
  void m2(int i)      { System.out.println("12i"); }
}
