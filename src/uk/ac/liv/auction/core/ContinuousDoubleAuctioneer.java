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

package uk.ac.liv.auction.core;

import java.util.Iterator;
import java.util.List;

import uk.ac.liv.util.Debug;
import uk.ac.liv.util.Parameterizable;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * @author Steve Phelps
 */

public class ContinuousDoubleAuctioneer extends KAuctioneer {

  public ContinuousDoubleAuctioneer() {
    this(null, 0);
  }

  public ContinuousDoubleAuctioneer( double k ) {
    this(null, k);
  }

  public ContinuousDoubleAuctioneer( Auction auction, double k ) {
    this(auction, new UniformPricingPolicy(k));
  }

  public ContinuousDoubleAuctioneer( Auction auction, KPricingPolicy policy ) {
    super(auction, policy);
  }

  public ContinuousDoubleAuctioneer( Auction auction ) {
    this(auction, 0);
  }

  public void generateQuote() {
    currentQuote = new MarketQuote(askQuote(), bidQuote());
  }

  public void endOfRoundProcessing() {
    clear();
    generateQuote();
  }

  public void endOfAuctionProcessing() {
    // do nothing
  }

  public boolean shoutsVisible() {
    return true;
  }

}