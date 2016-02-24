// Example 34 from page 27 of Java Precisely second edition (The MIT Press 2005)
// Author: Peter Sestoft (sestoft@itu.dk)


class Example034 {
  public static void main(String[] args) {
    int i = 17;
    Integer ii = new Integer(i);
    double d = 17.0;
    C2 c2 = new C2();                           // Type C2, object class C2
    C1 c1 = c2;                                 // Type C1, object class C2
    c1.m1(i); c2.m1(i); c1.m1(d); c2.m1(d);     // Prints 21i 21i 11d 21d
    c1.m2(i);                                   // Prints 12i
    c2.m2(i);                                   // Prints 12i
    c2.m2(ii);                                  // Prints 22ii
    c2.m3(ii);                                  // Prints 23i,  with unboxing
    c2.m4(i);                                   // Prints 24ii, with boxing
  }
}

