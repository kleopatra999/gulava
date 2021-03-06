/*
 *  Copyright (c) 2015 The Gulava Authors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package gulava;

import static gulava.Goals.conj;
import static gulava.Goals.same;

import gulava.util.Count;
import gulava.util.InsertionSort;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;

/**
 * The code in this console application demonstrates how to run a query and print its results. This
 * code can also be used as a starting point to write a tool that prints the results of a one-off
 * logic computation.
 */
public class Demo {
  static void print(Stream s, int maxSteps, boolean dump, Var... requestedVars) throws IOException {
    Dumper dumper = new Dumper(0, new OutputStreamWriter(System.out));
    int totalSteps = 0;
    while (totalSteps < maxSteps) {
      if (s == Streams.EMPTY) {
        System.out.println("()");
        break;
      }
      if (dump) {
        System.out.println("\n--------------------------------------------------------------------------------");
        dumper.dump(s);
        dumper.flush();
      }
      Subst subst = s.subst();

      if (subst != null) {
        System.out.println(new View.Builder()
            .setSubst(subst)
            .addRequestedVar(requestedVars)
            .build());
      }
      s = s.rest();
      totalSteps++;
    }
    System.out.println("total steps: " + totalSteps);
  }

  public static void main(String... args) throws Exception {
    Var a = new Var();
    Var b = new Var();
    Var x = new Var();
    Var y = new Var();
    boolean dump = false;
    if (args.length > 0 && args[0].equals("--dump")) {
      dump = true;
    }

    System.out.println("\nexample 1: unification");
    Goal g = conj(
        same(x, y),
        same(x, Cons.list(Arrays.asList(2, 3, 4))),
        same(a, Cons.of(42, x)),
        same(b, Cons.of(43, y)));
    print(g.run(Subst.EMPTY), 10, dump, a, b);

    System.out.println("\nexample 2: undo insertion");
    Goal uninsert = InsertionSort.O.insert(
        Count.fromInt(10),
        a,
        Cons.s(
            Count.fromInt(1),
            Count.fromInt(10),
            Count.fromInt(20)));
    print(uninsert.run(Subst.EMPTY), 100, dump, a);

    System.out.println("\nexample 3: insertion sort");
    Goal sort = InsertionSort.O.sorted(
        Cons.s(
                Count.fromInt(5),
                Count.fromInt(4),
                Count.fromInt(3),
                Count.fromInt(2),
                Count.fromInt(10),
                Count.fromInt(12),
                Count.fromInt(9)),
        a);
    print(sort.run(Subst.EMPTY), 100, dump, a);

    System.out.println("\nTry append");
    Goal append = Cons.O.append(a, b, Cons.s(1, 2, 3, 4, 5, 6, 7));
    print(append.run(Subst.EMPTY), 1000, dump, a, b);
  }
}
