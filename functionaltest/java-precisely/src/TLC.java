// Example 39 from page 31 of Java Precisely second edition (The MIT Press 2005)
// Author: Peter Sestoft (sestoft@itu.dk)

class TLC {                                   // top-level class
  static int sf;
  int nf;

  static class SMC {                          // static member class
    static int ssf = sf + TLC.sf;             // can have static members
    int snf = sf + TLC.sf;                    // cannot use non-static TLC members
  }

  class NMC {                                 // non-static member (inner) class
    int nnf1 = sf + nf;                       // can use non-static TLC members
    int nnf2 = TLC.sf + TLC.this.nf;          // cannot have static members
  }

  void nm() {                                 // non-static method in TLC
    class NLC {                               // local (inner) class in method
      int m(int p) { return sf+nf+p; }        // can use non-static TLC members
    }                                         // cannot have static members
  }

  // According to the Java Language Specification, 2nd edition,
  // section 8.1.2, every local class is an inner class, and thus
  // static members are disallowed in local classes, even inside
  // static methods:

  static void sm() {                          // static method in TLC
    class SLC {                               // local class
      // static int ssf = 78;                 // cannot have static members
    }                                         // cannot use non-static TLC members
  }
}
