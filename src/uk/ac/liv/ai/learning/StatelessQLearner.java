/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2003 Steve Phelps
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */


package uk.ac.liv.ai.learning;

import uk.ac.liv.util.Parameterizable;
import uk.ac.liv.util.Resetable;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

/**
 * A memory-less version of the Q-Learning algorithm.
 *
 * This class implements StimuliResponseLearner instead of
 * MDPLearner, and so can be used in place of, e.g. a RothErevLearner.
 *
 * We use the standard MDP QLearner class, but fool it with this
 * wrapper into thinking that there is only one state.
 *
 * @author Steve Phelps
 */

public class StatelessQLearner
    implements StimuliResponseLearner, Parameterizable, Resetable {

  QLearner qLearner;


  public StatelessQLearner( int numActions, double epsilon,
                              double learningRate, double discountRate  ) {

    qLearner = new QLearner(1, numActions, epsilon, learningRate,
                            discountRate);
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
    qLearner.setup(parameters, base);
  }

  public int act() {
    return qLearner.act();
  }

  public void reward(double reward) {
    qLearner.newState(reward, 0);
  }

  public void reset() {
    qLearner.reset();
  }

}