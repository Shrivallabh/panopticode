// Example 24 from page 21 of Java Precisely second edition (The MIT Press 2005)
// Author: Peter Sestoft (sestoft@itu.dk)


class Point {
  int x, y;

  Point(int x, int y) { this.x = x; this.y = y; }

  Point(Point p) { this(p.x, p.y); }    // calls the above constructor

  void move(int dx, int dy) { x += dx; y += dy; }

  public String toString() { return "(" + x + ", " + y + ")"; }
}