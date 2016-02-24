// Example 109 from page 83 of Java Precisely second edition (The MIT Press 2005)
// Author: Peter Sestoft (sestoft@itu.dk)

class MyString implements Comparable<MyString> {
  public final String s;
  public MyString(String s) {
    if (s == null)
      throw new RuntimeException("null string");
    else
      this.s = s;
  }
  public int compareTo(MyString that) {
    return s.compareTo(that.s);
  }
}
