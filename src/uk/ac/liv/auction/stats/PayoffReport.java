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

package uk.ac.liv.auction.stats;

import uk.ac.liv.auction.agent.AbstractTradingAgent;

import uk.ac.liv.util.HeavyweightDistribution;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import java.io.Serializable;

import java.util.*;

import org.apache.log4j.Logger;

/**
 * An abstract report that keeps track of the ratio of actual
 * verses theoretical profits for a particular grouping of agents.
 * Concrete implementations define how agents are grouped together.
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public abstract class PayoffReport extends DynamicSurplusReport 
								implements Serializable {

  /**
   * Maps keys representing groups onto the PayoffStats for that group.
   * 
   * @seealso PayoffStats
   */
  private HashMap table = new HashMap();
  
  protected double totalProfits;

  static Logger logger = Logger.getLogger(PayoffReport.class);


  public void setup( ParameterDatabase parameters, Parameter base ) {
    super.setup(parameters, base);
  }


  public void calculate() {
    int numAgents = auction.getNumberOfRegisteredTraders();
    double averageSurplus = calculateTotalEquilibriumSurplus() / numAgents; 
    table.clear();
    totalProfits = 0;
    Iterator i = auction.getTraderIterator();
    while ( i.hasNext() ) {
      AbstractTradingAgent agent = (AbstractTradingAgent) i.next();
      double profits = agent.getProfits();
      double payoff = 1;
      if ( averageSurplus != 0 ) {
        payoff = profits / averageSurplus;
      }      
      Object key = getKey(agent);      
      PayoffStats stats = (PayoffStats) table.get(key);
      if ( stats == null ) {
        stats = new PayoffStats(1, profits);
        table.put(key, stats);
      } else {
        stats.profits += profits;
        stats.numAgents++;
      }
      stats.recordPayoff(payoff);
      totalProfits += profits;
    }   
  }




  public double getTotalProfits() {
    return totalProfits;
  }

  public double getProfits( Object key ) {
    PayoffStats stats =
        (PayoffStats) table.get(key);
    if ( stats != null ) {
      return stats.profits;
    } else {
      return 0;
    }
  }

  public double getMeanPayoff( Object key ) {
    PayoffStats stats =
       (PayoffStats) table.get(key);
    if ( stats != null ) {
      return stats.getPayoffDistribution().getMean();
    } else {
      return 0;
    }
  }

  public int getNumberOfAgents( Object key ) {
    PayoffStats stats =
        (PayoffStats) table.get(key);
    if ( stats != null ) {
      return stats.numAgents;
    } else {
      return 0;
    }

  }
  
  public HeavyweightDistribution getPayoffDistribution( Object key ) {
    PayoffStats stats = (PayoffStats) table.get(key);
    if ( stats == null ) {
      return null;
    } else {
      return stats.getPayoffDistribution();
    }
  }


  public void produceUserOutput() {
    calculate();
    logger.info("\nProfits per " + getKeyName());
    logger.info("-----------------------");
    logger.info("");
    Iterator i = table.keySet().iterator();
    while ( i.hasNext() ) {
      Object key = (Object) i.next();
      PayoffStats stats = (PayoffStats) table.get(key);
      logger.info(stats.numAgents + " " + getReportText() + " " +
                   key + "\n\ttotal profits: " + stats.profits +
                          "\n\tmean payoff: " + 
                               stats.getPayoffDistribution().getMean() + "\n");
    }
    super.produceUserOutput();
  }
     
  
  public Map getVariables() {    
    HashMap vars = new HashMap();
    vars.putAll(super.getVariables());
    Iterator i = table.keySet().iterator();
    while ( i.hasNext() ) {
      Object key = i.next();
      PayoffStats stats = (PayoffStats) table.get(key);
      String varName = "payoff." + key.toString();
      ReportVariable var = new ReportVariable(varName, "Payoff to " + key);
      vars.put(var, new Double(stats.getPayoffDistribution().getMean()));
    }
    return vars;
  }
  
  public void initialise() {
    super.initialise();
    totalProfits = 0;
    table.clear();
  }
  
  /**
   * Map an agent onto a group.
   * @param agent	The agent to map from.
   * @return An object representing the grouping of this agent.
   */
  public abstract Object getKey( AbstractTradingAgent agent );

  /**
   * Return user-friendly description of the space of groups.
   * @return A string describing the grouping of this report.
   */
  public abstract String getKeyName();
  
  public abstract String getReportText();
}


class PayoffStats {

  public double profits = 0;
  public int numAgents = 0;
  
  protected HeavyweightDistribution payoffDistribution = 
    new HeavyweightDistribution();

  public PayoffStats( int numAgents, double profits ) {
    this.numAgents = numAgents;
    this.profits = profits;
  }
  
  public void recordPayoff( double payoff ) {
    payoffDistribution.newData(payoff);    
  }
  
  public HeavyweightDistribution getPayoffDistribution() {
    return payoffDistribution;
  }

}
