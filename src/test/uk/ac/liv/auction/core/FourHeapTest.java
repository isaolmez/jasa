package test.uk.ac.liv.auction.core;

import java.util.*;

import uk.ac.liv.auction.core.*;

import uk.ac.liv.util.BinaryHeap;

import test.uk.ac.liv.auction.agent.TestTrader;

import junit.framework.*;

/**
 * @author Steve Phelps
 */

public class FourHeapTest extends TestCase {

  TestShoutEngine shoutEngine;
  TestTrader testTrader;
  Random randGenerator;

  public FourHeapTest( String name ) {
    super(name);
  }

  public void setUp() {
    shoutEngine = new TestShoutEngine();
    testTrader = new TestTrader(this, 0, 0);
    randGenerator = new Random();
  }

  public Shout randomShout() {
    int quantity = randGenerator.nextInt(50);
    double price = randGenerator.nextDouble() * 100;
    boolean isBid = randGenerator.nextBoolean();
    return new Shout(testTrader, quantity, price, isBid);
  }

  public void testRandom() {

    int matches = 0;

    try {

      Shout testRemoveShout = null, testRemoveShout2 = null;

      for( int round=0; round<700; round++ ) {

        if ( testRemoveShout != null ) {
         shoutEngine.removeShout(testRemoveShout);
         shoutEngine.removeShout(testRemoveShout2);
        }

        for( int shout=0; shout<200; shout++ ) {
          shoutEngine.newShout(randomShout());
        }

        shoutEngine.newShout(testRemoveShout = randomShout());
        testRemoveShout2 = (Shout) testRemoveShout.clone();
        testRemoveShout2.setIsBid(! testRemoveShout2.isBid());
        shoutEngine.newShout(testRemoveShout2);

        if ( (round & 0x01) > 0 ) {
          continue;
        }

        List matched = shoutEngine.getMatchedShouts();
        Iterator i = matched.iterator();
        while ( i.hasNext() ) {
          matches++;
          Shout bid = (Shout) i.next();
          Shout ask = (Shout) i.next();
          assertTrue( bid.isBid() );
          assertTrue( ask.isAsk() );
          assertTrue( bid.getPrice() > ask.getPrice() );
          //System.out.print(bid + "/" + ask + " ");
        }
        //System.out.println("");
      }

    } catch ( Exception e ) {
      e.printStackTrace();
      fail();
    }

    System.out.println("Matches = " + matches);

  }


  public static void main( String[] args ) {
    junit.textui.TestRunner.run(suite());
  }

  public static Test suite() {
    return new TestSuite(FourHeapTest.class);
  }

}


class TestShoutEngine extends FourHeapShoutEngine {

  protected void checkBalanced() {

    int nS = countQty(sIn);
    int nB = countQty(bIn);
    if ( nS != nB ) {
      printState();
      throw new Error("shout heaps not balanced nS="+nS + " nB=" + nB);
    }

    Shout bInTop = (Shout) bIn.getFirst();
    Shout sInTop = (Shout) sIn.getFirst();
    Shout bOutTop = (Shout) bOut.getFirst();
    Shout sOutTop = (Shout) sOut.getFirst();

    checkBalanced(bInTop, bOutTop, "bIn >= bOut");
    checkBalanced(sOutTop, sInTop, "sOut >= sIn");
    checkBalanced(sOutTop, bOutTop, "sOut >= bOut");
    checkBalanced(bInTop, sInTop, "bIn >= sIn");
  }

  protected void checkBalanced( Shout s1, Shout s2, String condition ) {
    if ( !((s1 == null || s2 == null) || s1.getPrice() >= s2.getPrice()) ) {
      printState();
      System.out.println("shout1 = " + s1);
      System.out.println("shout2 = " + s2);
      throw new Error("Heaps not balanced! - " + condition);
    }
  }

  public static int countQty( BinaryHeap heap ) {
    Iterator i = heap.iterator();
    int qty = 0;
    while ( i.hasNext() ) {
      Shout s = (Shout) i.next();
      qty += s.getQuantity();
    }
    return qty;
  }

}