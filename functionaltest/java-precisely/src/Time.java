// Example 128 from page 101 of Java Precisely second edition (The MIT Press 2005)
// Author: Peter Sestoft (sestoft@itu.dk)

class Time implements Comparable<Time> {
  public final int hh, mm;           // 24-hour clock

  public Time(int hh, int mm) { this.hh = hh; this.mm = mm; }

  // Return neg if this before o; return pos if this after o; return zero if same
  public int compareTo(Time t) {
    return hh != t.hh ? hh - t.hh : mm - t.mm;
  }

  public boolean equals(Object o) {
    if (this == o)                                    // fast, frequent case
      return true;
    if (o == null || this.getClass() != o.getClass()) // null or not same type
      return false;
    Time t = (Time)o;                                 // here o instanceof Time
    return hh == t.hh && mm == t.mm;
  }

  public int hashCode() { return 60 * hh + mm; }

  public String toString()
  { return (hh < 10 ? "0"+hh : ""+hh) + ":" + (mm < 10 ? "0"+mm : ""+mm); }
}
