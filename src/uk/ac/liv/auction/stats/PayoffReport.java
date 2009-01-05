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

import java.io.Serializable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import uk.ac.liv.auction.agent.AbstractTradingAgent;

import uk.ac.liv.util.SummaryStats;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public abstract class PayoffReport extends DynamicSurplusReport implements
    Serializable {

	/**
	 * Maps keys representing groups onto the PayoffStats for that group.
	 * 
	 * @uml.property name="table"
	 * @uml.associationEnd qualifier="key:java.lang.Object
	 *                     uk.ac.liv.auction.stats.PayoffStats"
	 */
	private HashMap table = new HashMap();

	static Logger logger = Logger.getLogger(PayoffReport.class);

	public void setup(ParameterDatabase parameters, Parameter base) {
		super.setup(parameters, base);
	}

	public void calculate() {
		super.calculate();
		int numAgents = auction.getNumberOfRegisteredTraders();
		double averageSurplus = calculateTotalEquilibriumSurplus() / numAgents;
		table.clear();
		Iterator i = auction.getTraderIterator();
		while (i.hasNext()) {
			AbstractTradingAgent agent = (AbstractTradingAgent) i.next();
			double profits = agent.getProfits();
			double payoff = 1;
			if (averageSurplus != 0) {
				payoff = profits / averageSurplus;
			}
			Object key = getKey(agent);
			PayoffStats stats = (PayoffStats) table.get(key);
			if (stats == null) {
				stats = new PayoffStats(1, profits);
				table.put(key, stats);
			} else {
				stats.profits += profits;
				stats.numAgents++;
			}
			stats.recordPayoff(payoff);
		}
	}

	public double getProfits(Object key) {
		PayoffStats stats = (PayoffStats) table.get(key);
		if (stats != null) {
			return stats.profits;
		} else {
			return 0;
		}
	}

	public double getMeanPayoff(Object key) {
		PayoffStats stats = (PayoffStats) table.get(key);
		if (stats != null) {
			return stats.getPayoffDistribution().getMean();
		} else {
			return 0;
		}
	}

	public int getNumberOfAgents(Object key) {
		PayoffStats stats = (PayoffStats) table.get(key);
		if (stats != null) {
			return stats.numAgents;
		} else {
			return 0;
		}

	}

	public SummaryStats getPayoffDistribution(Object key) {
		PayoffStats stats = (PayoffStats) table.get(key);
		if (stats == null) {
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
		while (i.hasNext()) {
			Object key = (Object) i.next();
			PayoffStats stats = (PayoffStats) table.get(key);
			logger.info(stats.numAgents + " " + getReportText() + " " + key
			    + "\n\ttotal profits: " + stats.profits + "\n\tmean payoff: "
			    + stats.getPayoffDistribution().getMean() + "\n");
		}
		super.produceUserOutput();
	}

	public Map getVariables() {
		HashMap vars = new HashMap();
		vars.putAll(super.getVariables());
		Iterator i = table.keySet().iterator();
		while (i.hasNext()) {
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
		table.clear();
	}

	/**
	 * Map an agent onto a group.
	 * 
	 * @param agent
	 *          The agent to map from.
	 * @return An object representing the grouping of this agent.
	 */
	public abstract Object getKey(AbstractTradingAgent agent);

	/**
	 * Return user-friendly description of the space of groups.
	 * 
	 * @return A string describing the grouping of this report.
	 * @uml.property name="keyName"
	 */
	public abstract String getKeyName();

	/**
	 * @uml.property name="reportText"
	 */
	public abstract String getReportText();
}

class PayoffStats {

	/**
	 * @uml.property name="profits"
	 */
	public double profits = 0;

	/**
	 * @uml.property name="numAgents"
	 */
	public int numAgents = 0;

	/**
	 * @uml.property name="payoffDistribution"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	protected SummaryStats payoffDistribution = new SummaryStats();

	public PayoffStats(int numAgents, double profits) {
		this.numAgents = numAgents;
		this.profits = profits;
	}

	public void recordPayoff(double payoff) {
		payoffDistribution.newData(payoff);
	}

	/**
	 * @uml.property name="payoffDistribution"
	 */
	public SummaryStats getPayoffDistribution() {
		return payoffDistribution;
	}

}
