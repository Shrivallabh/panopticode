// Author: Peter Sestoft (sestoft@itu.dk)
package common;

public interface Mapper<A,R> {
  R call(A x);
}
