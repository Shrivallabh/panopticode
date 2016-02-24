// Example 34 from page 27 of Java Precisely second edition (The MIT Press 2005)
// Author: Peter Sestoft (sestoft@itu.dk)

class C2 extends C1 {
  static void m1(double d) { System.out.println("21d"); }
  void m1(int i)      { System.out.println("21i"); }
  void m2(double d)   { System.out.println("22d"); }
  void m2(Integer ii) { System.out.println("22ii"); }
  void m3(int i)      { System.out.println("23i"); }
  void m4(Integer ii) { System.out.println("24ii"); }
}