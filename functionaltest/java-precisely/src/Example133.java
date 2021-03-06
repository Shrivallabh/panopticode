// Example 133 from page 105 of Java Precisely second edition (The MIT Press 2005)
// Author: Peter Sestoft (sestoft@itu.dk)

import java.util.*;

class Example133 {
  public static void main(String[] args) {
    SortedMap<Time,String> datebook = new TreeMap<Time,String>();
    datebook.put(new Time(12, 30), "Lunch");
    datebook.put(new Time(15, 30), "Afternoon coffee break");
    datebook.put(new Time( 9,  0), "Lecture");
    datebook.put(new Time(13, 15), "Board meeting");
    SortedMap<Time,String> pm = datebook.tailMap(new Time(12, 0));
    for (Map.Entry<Time,String> entry : pm.entrySet()) 
      System.out.println(entry.getKey() + " " + entry.getValue());
  }
}

