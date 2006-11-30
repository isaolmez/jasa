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

package uk.ac.liv.auction.agent;

import uk.ac.liv.auction.core.Auction;

import uk.ac.liv.auction.event.AuctionEvent;
import uk.ac.liv.auction.event.AuctionEventListener;

import uk.ac.liv.util.Resetable;
import uk.ac.liv.util.Parameterizable;

/**
 * A commodity valuation policy for RoundRobinTrader agents.
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public interface ValuationPolicy extends Resetable, Parameterizable,
    AuctionEventListener {

  /**
   * Determine the current valuation of commodity in the given auction.
   */
  public double determineValue( Auction auction );

  /**
   * Recalculate valuation after consumption of the commodity being traded in
   * the given auction.
   */
  public void consumeUnit( Auction auction );

  /**
   * Recalculate valuation(s) in response to an auction event.
   */
  public void eventOccurred( AuctionEvent event );
  
  public void setAgent( TradingAgent agent );

}