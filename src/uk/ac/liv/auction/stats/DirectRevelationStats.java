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


package uk.ac.liv.auction.stats;

import uk.ac.liv.auction.agent.AbstractTraderAgent;
import uk.ac.liv.auction.core.*;

import uk.ac.liv.util.Debug;
import uk.ac.liv.util.Resetable;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

import huyd.poolit.*;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import uk.ac.liv.util.io.DataWriter;

import java.util.*;

import java.io.Serializable;

import org.apache.log4j.Logger;

/**
 * @author Steve Phelps
 */

public class DirectRevelationStats implements Resetable, Serializable {

  /**
   * The shout engine used to compute.
   */
  protected FourHeapShoutEngine shoutEngine = new FourHeapShoutEngine();

  /**
   * The auction we are computing stats for.
   */
  protected RoundRobinAuction auction;

  /**
   * The truthful shouts of all traders in the auction.
   */
  protected ArrayList shouts;

  protected ArrayList bids;

  protected ArrayList asks;


  static Logger logger = Logger.getLogger(DirectRevelationStats.class);

  public DirectRevelationStats( RoundRobinAuction auction ) {
    this();
    this.auction = auction;
  }

  public DirectRevelationStats() {
    shouts = new ArrayList();
    bids = new ArrayList();
    asks = new ArrayList();
  }

  public void setAuction( RoundRobinAuction auction ) {
    this.auction = auction;
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
  }

  public void calculate() {
    simulateDirectRevelation();
  }

  protected void simulateDirectRevelation() {
    try {
      Iterator traders = auction.getTraderIterator();
      while ( traders.hasNext() ) {
        AbstractTraderAgent trader = (AbstractTraderAgent) traders.next();
        int quantity = trader.determineQuantity(auction);
        double value = trader.getPrivateValue();
        boolean isBid = trader.isBuyer();
        Shout shout = ShoutPool.fetch(trader, quantity, value, isBid);
        shouts.add(shout);
        if ( isBid ) {
          shoutEngine.newBid(shout);
          bids.add(shout);
        } else {
          shoutEngine.newAsk(shout);
          asks.add(shout);
        }
      }
    } catch ( DuplicateShoutException e ) {
      e.printStackTrace();
      throw new Error(e.getMessage());
    }
  }


  protected void releaseShouts() {
    Iterator i = shouts.iterator();
    while ( i.hasNext() ) {
      Shout s = (Shout) i.next();
      ShoutPool.release(s);
    }
  }


  public void initialise() {
    shouts.clear();
    shoutEngine.reset();
    bids.clear();
    asks.clear();
  }


  public void reset() {
    initialise();
  }



}
