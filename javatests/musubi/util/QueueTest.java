/*
 *  Copyright (c) 2015 Dmitry Neverov and Google
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
package musubi.util;

import static musubi.Goals.conj;
import static musubi.Goals.same;

import musubi.Cons;
import musubi.Var;
import musubi.testing.LogicAsserter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;

@RunWith(JUnit4.class)
public class QueueTest {
  private static final Var A = new Var();
  private static final Var B = new Var();
  private static final Var C = new Var();
  private static final Var D = new Var();
  private static final Var E = new Var();
  private static final Object EMPTY = Queue.empty();

  @Test
  public void enqueue() {
    Queue<Var, DiffList<?, ?>> finishQueue =
        new Queue<>(new Var(), new DiffList<>(new Var(), new Var()));
    new LogicAsserter()
        .stream(
            conj(
                QueueLast.o(42, EMPTY, A),
                QueueLast.o("1011", A, B),
                QueueLast.o(false, B, C),
                QueueLast.o('a', C, finishQueue),
                same(null, finishQueue.contents().hole()),
                same(D, finishQueue.contents().head()),
                same(E, finishQueue.size())))
        .addRequestedVar(D, E)
        .workUnits(2)
        .startSubst()
        .put(D, Cons.list(Arrays.asList(42, "1011", false, 'a')))
        .put(E, new Count<>(new Count<>(new Count<>(new Count<>(null)))))
        .test();
  }

  @Test
  public void backwardsEnqueue_inadequateCount() {
    new LogicAsserter()
        .stream(
            conj(
                QueueLast.o(42, B, new Queue<>(new Count<>(null), new Var())),
                QueueLast.o(42, A, B)))
        .workUnits(1)
        .test();
  }

  @Test
  public void backwardsEnqueue_success() {
    DiffList<Cons<Var, Cons<Var, Var>>, Var> startList = new DiffList<>(
        new Cons<>(new Var(), new Cons<>(new Var(), new Var())),
        new Var());

    new LogicAsserter()
        .stream(
            conj(
                QueueLast.o(10, A, new Queue<>(new Count<>(new Count<>(null)), startList)),
                QueueLast.o(20, EMPTY, A),
                same(startList.head().cdr().cdr(), null),
                same(C, startList.head())))
        .workUnits(2)
        .addRequestedVar(C)
        .startSubst()
        .put(C, Cons.list(Arrays.asList(20, 10)))
        .test();
  }
}
