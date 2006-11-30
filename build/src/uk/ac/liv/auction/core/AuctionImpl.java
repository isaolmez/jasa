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

package uk.ac.liv.auction.core;

import uk.ac.liv.auction.event.AuctionEventListener;
import uk.ac.liv.auction.event.AuctionEvent;
import uk.ac.liv.auction.event.AgentPolledEvent;
import uk.ac.liv.auction.event.AuctionClosedEvent;
import uk.ac.liv.auction.event.AuctionOpenEvent;
import uk.ac.liv.auction.event.EndOfDayEvent;
import uk.ac.liv.auction.event.RoundClosedEvent;
import uk.ac.liv.auction.event.RoundClosingEvent;
import uk.ac.liv.auction.event.ShoutPlacedEvent;
import uk.ac.liv.auction.event.ShoutReceivedEvent;
import uk.ac.liv.auction.event.TransactionExecutedEvent;

import uk.ac.liv.auction.stats.AuctionReport;
import uk.ac.liv.auction.stats.CombiAuctionReport;

import uk.ac.liv.util.IdAllocator;
import uk.ac.liv.util.Resetable;

import java.io.Serializable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.LinkedList;
import java.util.Iterator;

/**
 * An abstract implementation of Auction that provides basic logging facilities.
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public abstract class AuctionImpl extends Observable implements Auction,
    Serializable, Resetable {

  /**
   * The name of this auction.
   * 
   * @uml.property name="name"
   */
  protected String name;

  /**
   * Used to assign unique ids to each mySingletonInstance.
   */
  static IdAllocator idAllocator = new IdAllocator();

  /**
   * A unique id for this auction. It's main use is in debugging.
   * 
   * @uml.property name="id"
   */
  protected long id;

  /**
   * Flag indicating whether the auction is currently closed.
   * 
   * @uml.property name="closed"
   */
  protected boolean closed;

  /**
   * The plugable auction rules to use for this auction, e.g.
   * AscendingAuctioneer.
   * 
   * @uml.property name="auctioneer"
   * @uml.associationEnd
   */
  protected Auctioneer auctioneer = null;

  /**
   * The optional MarketDataLogger to log data to.
   * 
   * @uml.property name="report"
   * @uml.associationEnd
   */
  protected AuctionReport report = null;

  /**
   * @uml.property name="eventListeners"
   * @uml.associationEnd multiplicity="(0 -1)" ordering="true"
   *                     elementType="uk.ac.liv.auction.event.AuctionEventListener"
   *                     qualifier="eventClass:java.lang.Class java.util.List"
   */
  protected HashMap eventListeners = new HashMap();

  private static final Class[] allEvents = { RoundClosedEvent.class,
			RoundClosingEvent.class, AuctionOpenEvent.class,
			AuctionClosedEvent.class, EndOfDayEvent.class,
			TransactionExecutedEvent.class, ShoutPlacedEvent.class,
			AgentPolledEvent.class, ShoutReceivedEvent.class };

  public AuctionImpl( String name ) {
    id = idAllocator.nextId();
    if ( name != null ) {
      this.name = name;
    } else {
      this.name = "Auction " + id;
    }
    // initialise();
  }

  public AuctionImpl() {
    this(null);
  }

  protected void initialise() {
    closed = false;
  }

  public void reset() {
    initialise();
    eventListeners.clear();
  }

  /**
   * @uml.property name="auctioneer"
   */
  public void setAuctioneer( Auctioneer auctioneer ) {
    this.auctioneer = auctioneer;
    auctioneer.setAuction(this);
  }

  /**
   * @uml.property name="auctioneer"
   */
  public Auctioneer getAuctioneer() {
    return auctioneer;
  }

  public boolean closed() {
    return closed;
  }

  /**
   * Close the auction.
   */
  public void close() {
    closed = true;
  }

  /**
   * @uml.property name="lastShout"
   */
  public Shout getLastShout() throws ShoutsNotVisibleException {
//    if ( !auctioneer.shoutsVisible() ) {
//      throw new ShoutsNotVisibleException();
//    }
//    return lastShout;
    return auctioneer.getLastShout();
  }

  /**
   * Assign a data logger
   * 
   * @uml.property name="report"
   */
  public void setReport( AuctionReport logger ) {
    this.report = logger;
    removeAuctionEventListener(logger);
    addAuctionEventListener(logger);
  }

  /**
   * Get the current data logger
   * 
   * @uml.property name="report"
   */
  public AuctionReport getReport() {
    return report;
  }

  /**
   * Change the name of this auction.
   * 
   * @param name
   *          The new name of the auction.
   * @uml.property name="name"
   */
  public void setName( String name ) {
    this.name = name;
  }

  public MarketQuote getQuote() {
    return auctioneer.getQuote();
  }

  /**
   * @uml.property name="name"
   */
  public String getName() {
    return name;
  }

  /**
   * @uml.property name="id"
   */
  public long getId() {
    return id;
  }

  public void removeShout( Shout shout ) {
    // Remove this shout and all of its children.
    for ( Shout s = shout; s != null; s = s.getChild() ) {
      auctioneer.removeShout(s);
      // if ( s != shout ) {
      // ShoutPool.release(s);
      // }
    }
    shout.makeChildless();
  }

  /**
   * Handle a new shout in the auction.
   * 
   * @param shout
   *          The new shout in the auction.
   */
  public void newShout( Shout shout ) throws AuctionException {
    if ( closed() ) {
      throw new AuctionClosedException("Auction " + name + " is closed.");
    }
    if ( shout == null ) {
      throw new IllegalShoutException("null shout");
    }

    auctioneer.newShout(shout);

//    notifyObservers();
  }

  public void printState() {
    auctioneer.printState();
  }

  /**
   * Add a new market data logger.
   * 
   * @param newReport
   *          The new logger to add.
   */
  public void addReport( AuctionReport newReport ) {
    AuctionReport oldReport = report;
    if ( !(oldReport instanceof CombiAuctionReport) ) {
      setReport(new CombiAuctionReport());
      if ( oldReport != null ) {
        ((CombiAuctionReport) report).addReport(oldReport);
      }
    }
    ((CombiAuctionReport) report).addReport(newReport);
  }

  public void addListener( LinkedList listeners, AuctionEventListener listener ) {
    assert listener != null;

    if ( !listeners.contains(listener) ) {
      listeners.add(listener);
    }
  }

  public void addAuctionEventListener( AuctionEventListener listener ) {
    for ( int i = 0; i < allEvents.length; i++ ) {
      addAuctionEventListener(allEvents[i], listener);
    }
  }

  public void removeAuctionEventListener( AuctionEventListener listener ) {
    for ( int i = 0; i < allEvents.length; i++ ) {
      removeAuctionEventListener(allEvents[i], listener);
    }
  }

  public void addAuctionEventListener( Class eventClass,
      AuctionEventListener listener ) {
    LinkedList listenerList = (LinkedList) eventListeners.get(eventClass);
    if ( listenerList == null ) {
      listenerList = new LinkedList();
      eventListeners.put(eventClass, listenerList);
    }
    listenerList.add(listener);
  }

  public void removeAuctionEventListener( Class eventClass,
      AuctionEventListener listener ) {
    LinkedList listenerList = (LinkedList) eventListeners.get(eventClass);
    if ( listenerList != null ) {
      listenerList.remove(listener);
    }
  }

  protected void fireEvent( AuctionEvent event ) {
    List listeners = (List) eventListeners.get(event.getClass());
    if ( listeners != null ) {
      Iterator i = listeners.iterator();
      while ( i.hasNext() ) {
        AuctionEventListener listener = (AuctionEventListener) i.next();
        listener.eventOccurred(event);
      }
    }
  }

  public void informAuctionClosed() {
    fireEvent(new AuctionClosedEvent(this, getRound()));
  }

  public void informEndOfDay() {
    fireEvent(new EndOfDayEvent(this, getRound()));
  }

  public void informAuctionOpen() {
    fireEvent(new AuctionOpenEvent(this, getRound()));
  }

  /**
   * Return a Map of all of the variables in all of the reports configured for
   * this auction. The Map maps report variables, represented as objects of type
   * ReportVariable onto their values.
   * 
   * @see uk.ac.liv.auction.stats.ReportVariable
   */
  public Map getResults() {
    if ( report != null ) {
      return report.getVariables();
    } else {
      return new HashMap();
    }
  }

  public AuctionReport getReport( Class reportClass ) {
    if ( report != null ) {
      if ( report.getClass().equals(reportClass) ) {
        return report;
      } else if ( report instanceof CombiAuctionReport ) {
        Iterator i = ((CombiAuctionReport) report).reportIterator();
        while ( i.hasNext() ) {
          AuctionReport report = (AuctionReport) i.next();
          if ( report.getClass().equals(reportClass) ) {
            return report;
          }
        }
      }
    }
    return null;
  }

  public String toString() {
    return "(Auction id:" + id + ")";
  }

}