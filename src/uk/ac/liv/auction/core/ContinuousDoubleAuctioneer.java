package uk.ac.liv.auction.core;

import java.util.Iterator;
import java.util.List;

import uk.ac.liv.util.Debug;

/**
 * An Auctioneer for a uniform-price, continuous k-double-auction,
 * in which clearing takes place after every round of
 * bidding, and both buyers and sellers can make offers.
 */

public class ContinuousDoubleAuctioneer extends AbstractAuctioneer
                                            implements ParameterizablePricing {

  /**
   * k is a parameter that determines the clearing price of currently matched shouts.
   */
  double k = 1;

  public ContinuousDoubleAuctioneer() {
    super();
  }

  public ContinuousDoubleAuctioneer( Auction auction ) {
    super(auction);
  }

  /**
   * @param auction The auction container for this auctioneer.
   * @param k The parameter k determines the price at which shouts are cleared.
   * The price for each clearing is p = ka + (1-k)b, where a is the current global ask price,
   * and b is the bid price.  Use k = 0 for a Vickrey auction and k = 1 for first price auction.
   */
  public ContinuousDoubleAuctioneer( Auction auction, double k ) {
    this(auction);
    this.k = k;
  }

  public ContinuousDoubleAuctioneer( double k ) {
    this();
    this.k = k;
  }

  public synchronized void clear() {
    double price = determineClearingPrice();
    List shouts = shoutEngine.getMatchedShouts();
    Iterator i = shouts.iterator();
    while ( i.hasNext() ) {
      Shout bid = (Shout) i.next();  Debug.assert( bid.isBid() );
      Shout ask = (Shout) i.next();  Debug.assert( ask.isAsk() );
      auction.clear(ask, bid.getAgent(), ask.getAgent(), price, ask.getQuantity());
    }
  }

  public void setK( double k ) {
    this.k = k;
  }

  public double getK() {
    return k;
  }

  protected double determineClearingPrice() {
    return (k * (double) bidQuote() + (1.0 - k) * (double) askQuote());
  }

  protected double bidQuote() {
    return Shout.maxPrice(shoutEngine.getHighestMatchedAsk(), shoutEngine.getHighestUnmatchedBid());
  }

  protected double askQuote() {
    return Shout.minPrice(shoutEngine.getLowestUnmatchedAsk(), shoutEngine.getLowestMatchedBid());
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
}