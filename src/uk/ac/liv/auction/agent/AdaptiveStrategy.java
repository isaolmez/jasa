/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2002 Steve Phelps
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

package uk.ac.liv.auction.agent;

import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.auction.core.Auction;

import uk.ac.liv.util.Parameterizable;

import ec.util.Parameter;
import ec.util.ParameterDatabase;


public abstract class AdaptiveStrategy extends FixedQuantityStrategyImpl {

  boolean firstShout;

  static final String P_MARKUPSCALE = "markupscale";

  double markupScale = 1;

  public AdaptiveStrategy( AbstractTraderAgent agent ) {
    super(agent);
    initialise();
  }

  public AdaptiveStrategy() {
    super();
    initialise();
  }

  public void initialise() {
    firstShout = true;
    super.initialise();
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
    super.setup(parameters, base);
    markupScale = parameters.getDoubleWithDefault(base.push(P_MARKUPSCALE),
                                                   null,1);
  }

  public void modifyShout( Shout shout, Auction auction ) {

    super.modifyShout(shout, auction);

    if ( firstShout ) {
      firstShout = false;
    } else {
      calculateReward(auction);
    }

    // Generate an action from the learning algorithm
    int action = act();

    // Now turn the action into a price
    double price;
    if ( agent.isSeller() ) {
      price = agent.getPrivateValue() + action*markupScale;
    } else {
      price = agent.getPrivateValue() - action*markupScale;
    }
    /* TODO
    if ( price < funds ) {
      price = funds;
    } */
    if ( price < 0 ) {
      price = 0;
    }
    shout.setPrice(price);
    shout.setQuantity(quantity);
  }

  public double getMarkupScale() {
    return markupScale;
  }

  public void setMarkupScale( double markupScale ) {
    this.markupScale = markupScale;
  }

  public abstract int act();

  public abstract void calculateReward( Auction auction );

}