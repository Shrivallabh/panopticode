// Example 26 from page 21 of Java Precisely second edition (The MIT Press 2005)
// Author: Peter Sestoft (sestoft@itu.dk)


class Example026 {
  public static void main(String[] args) {
    TLC.SMC sio = new TLC.SMC();
    TLC.sf = 10;
    TLC oo = new TLC();
    oo.nf = 5;
    TLC.NMC io1 = oo.new NMC();
    System.out.println("io1.nnf1 = " + io1.nnf1);
    oo.nf = 7;
    TLC.NMC io2 = oo.new NMC();
    System.out.println("io1.nnf1 = " + io1.nnf1);
    System.out.println("io2.nnf1 = " + io2.nnf1);
  }
}

