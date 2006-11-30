/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2005 Steve Phelps
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
import uk.ac.liv.util.io.DataWriter;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

import java.io.Serializable;

/**
 * A learner that chooses the same specified action on every iteration.
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class DumbLearner extends AbstractLearner implements DiscreteLearner,
    Parameterizable, Serializable {

  /**
   * @uml.property name="action"
   */
  protected int action;

  static final String P_ACTION = "action";

  public DumbLearner() {
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
    super.setup(parameters, base);
    action = parameters.getInt(base.push(P_ACTION), null, 0);
  }

  /**
   * @uml.property name="action"
   */
  public void setAction( int action ) {
    this.action = action;
  }

  /**
   * @uml.property name="action"
   */
  public int getAction() {
    return action;
  }

  public int act() {
    return action;
  }

  public double getLearningDelta() {
    return 0.0;
  }

  public void dumpState( DataWriter out ) {
    // TODO
  }

  public int getNumberOfActions() {
    return 1;
  }

}