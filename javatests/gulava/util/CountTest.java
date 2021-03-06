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
package gulava.util;

import static gulava.Goals.same;

import gulava.Var;
import gulava.testing.LogicAsserter;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class CountTest {
  private static final Var X = new Var();

  @Test
  public void testToString() {
    Assert.assertEquals("Count{1}", Count.of(null).toString());
    Assert.assertEquals("Count{2}", Count.of(Count.of(null)).toString());
    Assert.assertEquals("Count{3+foo}", Count.of(Count.of(Count.of("foo"))).toString());
  }

  @Test
  public void testStaticToString() {
    Assert.assertEquals("zero?{0}", Count.toString("zero?", null));
    Assert.assertEquals("lbl{1}", Count.toString("lbl", Count.of(null)));
    Assert.assertEquals("bar{2}", Count.toString("bar", Count.of(Count.of(null))));
    Assert.assertEquals("baz{3+foo}", Count.toString("baz", Count.of(Count.of(Count.of("foo")))));
  }

  @Test
  public void testUnify() {
    new LogicAsserter()
        .stream(
            same(
                Count.of(Count.of(X)),
                Count.of(Count.of(Count.of(null)))))
        .workUnits(1)
        .startSubst()
        .put(X, Count.of(null))
        .addRequestedVar(X)
        .test();
  }
}
