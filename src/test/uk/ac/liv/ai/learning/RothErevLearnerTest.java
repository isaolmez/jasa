package test.uk.ac.liv.ai.learning;

import junit.framework.*;

import uk.ac.liv.ai.learning.*;

import uk.ac.liv.util.CummulativeStatCounter;

public class RothErevLearnerTest extends TestCase {

  static final int CORRECT_ACTION = 2;

  NPTRothErevLearner learner1;

  public RothErevLearnerTest( String name ) {
    super(name);
  }

  public void setUp() {
    learner1 = new NPTRothErevLearner(10, 0.2, 0.2, 100.0);
  }

  public void testBasic() {
    learner1.setExperimentation(0.99);
    System.out.println("testBasic()");
    CummulativeStatCounter stats = new CummulativeStatCounter("action");
    int correctActions = 0;
    for( int i=0; i<100; i++ ) {
      int action = learner1.act();
      stats.newData(action);
      if ( action == CORRECT_ACTION ) {
        learner1.reward(1.0);
        correctActions++;
      } else {
        learner1.reward(0);
      }
    }
    System.out.println("final state of learner1 = " + learner1);
    System.out.println("learner1 score = " + correctActions + "%");
    System.out.println("learner1 peaks = " + learner1.countPeaks());
    System.out.println(stats);
  }

  public void testPeaks() {
    System.out.println("\ntestPeaks()");
    double q[] = { 12, 15, 12, 10, 16, 17, 0, 0, 0, 0 };
    learner1.setPropensities(q);
    int peaks = learner1.countPeaks();
    System.out.println(learner1);
    System.out.println("Number of peaks = " + peaks);
    assertTrue(peaks == 2);
  }

  public void testDistribution() {
    System.out.println("\ntestDistribution()");
    double q[] = { 55, 5, 5, 5, 5, 5, 5, 5, 5, 5 };
    CummulativeStatCounter action1Data = new CummulativeStatCounter("action1");
    for( int r=0; r<1000; r++ ) {
      learner1 = new NPTRothErevLearner(10, 0.2, 0.2, 1, System.currentTimeMillis());
      learner1.setPropensities(q);
      CummulativeStatCounter choiceData = new CummulativeStatCounter("choice");
      int action1Chosen = 0;
      for( int i=0; i<100; i++ ) {
        int choice = learner1.act();
        choiceData.newData(choice);
        if ( choice == 0 ) {
          action1Chosen++;
        }
      }
      action1Data.newData(action1Chosen);
    }
    System.out.println(action1Data);
    assertTrue( action1Data.getMean() <= 56 && action1Data.getMean() >= 54 );
  }


  public static void main( String[] args ) {
    junit.textui.TestRunner.run(suite());
  }

  public static Test suite() {
    return new TestSuite(RothErevLearnerTest.class);
  }
}