package uk.ac.liv.auction.electricity;

import uk.ac.liv.auction.agent.Strategy;
import uk.ac.liv.auction.agent.RoundRobinTrader;

import uk.ac.liv.auction.core.Shout;

import uk.ac.liv.ai.learning.*;

import uk.ac.liv.util.Resetable;
import uk.ac.liv.util.Debug;

/**
 * An electricity trader which uses a reinforcement learning algorithm
 * as a strategy.
 *
 * @author Steve Phelps
 */

public class MREElectricityTrader extends ElectricityTrader {

  /**
   * The learning algorithm for this trader.
   * The default algorithm is NPT modified RothErov.
   */
  StimuliResponseLearner learner;

  double lastProfit;

  /**
   * The default RothErov learning parameters
   */
  static final double R = 0.10;    // Recency
  static final double E = 0.20;    // Experimentation
  static final int K = 100;         // No. of possible different actions
  static final double X = 50000;
  static final double S1 = 9.0;

  public MREElectricityTrader( int capacity, double privateValue,
                                double fixedCosts, boolean isSeller,
                                StimuliResponseLearner learner ) {

    super(capacity, privateValue, fixedCosts, isSeller);
    this.learner = learner;
    this.strategy = constructStrategy();
    initialise();
  }

  public MREElectricityTrader( int capacity, double privateValue,
                              double fixedCosts, boolean isSeller ) {
    this(capacity, privateValue, fixedCosts, isSeller,
          new NPTRothErevLearner(K, R, E, S1*X));
  }

  public MREElectricityTrader( int capacity, double privateValue,
                              double fixedCosts, boolean isSeller, long seed ) {
    this(capacity, privateValue, fixedCosts, isSeller,
          new NPTRothErevLearner(K, R, E, S1*X, seed));
  }

  protected Strategy constructStrategy() {
    return new Strategy() {

      public void modifyShout( Shout shout ) {

        // Reward the learner based on last earnings
        learner.reward(lastProfit);

        // Generate an action from the learning algorithm
        int action = learner.act();

        Debug.assert("action >= 0", action >= 0);
        // Now turn the action into a price
        double price;
        if ( isSeller() ) {
          price = getPrivateValue() + action;
        } else {
          price = getPrivateValue() - action;
        }
        if ( price < 0 ) {
          price = 0;
        }
        shout.setPrice(price);
        shout.setQuantity(capacity);
        lastProfit = 0;
      }
    };
  }

  public void initialise() {
    lastProfit = 0;
    super.initialise();
  }

  public void informOfSeller( Shout winningShout, RoundRobinTrader seller,
                               double price, int quantity) {

    // Reward the learning algorithm according to profits made
    lastProfit = quantity * (privateValue - price);

    super.informOfSeller(winningShout, seller, price, quantity);
  }

  public void informOfBuyer( double price, int quantity ) {

    // Reward the learning algorithm according to profits made.
    lastProfit = quantity * (price - privateValue);

    super.informOfBuyer(price, quantity);
  }

  public void reset() {
    ((Resetable) learner).reset();
    super.reset();
  }

  public StimuliResponseLearner getLearner() {
    return learner;
  }

  public void setLearner( StimuliResponseLearner learner ) {
    this.learner = learner;
  }

  public String toString() {
    return "(" + getClass() + " id:" + id + " capacity:" + capacity + " privateValue:" + privateValue + " fixedCosts:" + fixedCosts + " profits:" + profits + " isSeller:" + isSeller + " learner:" + learner + ")";
  }

}

/*
class REStrategy implements Strategy {

  MREElectricityTrader agent;

  public REStrategy( MREElectricityTrader agent ) {
    this.agent = agent;
  }

  public void modifyShout( Shout shout ) {

    // Reward the learner based on last earnings
    agent.learner.reward(agent.lastProfit);

    // Generate an action from the learning algorithm
    int action = agent.learner.act();

    Debug.assert("action >= 0", action >= 0);
    // Now turn the action into a price
    double price;
    if ( agent.isSeller() ) {
      price = agent.getPrivateValue() + action;
    } else {
      price = agent.getPrivateValue() - action;
    }
    if ( price < 0 ) {
      price = 0;
    }
    shout.setPrice(price);
    shout.setQuantity(agent.capacity);
    agent.lastProfit = 0;

  }
} */