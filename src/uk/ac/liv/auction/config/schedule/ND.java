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
package uk.ac.liv.auction.config.schedule;


import uk.ac.liv.auction.config.Case;
import ec.util.ParameterDatabase;
import ec.util.Parameter;

/**
 * Normal demand curve, i.e. from upper left to lower right.
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 */
public class ND implements Case {
	
	private static final String ClassName = "uk.ac.liv.auction.agent.BuyerIntervalValuer";
		
	public String toString() {
		return "ND";
	}
	
	public void apply(ParameterDatabase pdb, Parameter base) {
		pdb.set(base.push("minvalue"), String.valueOf(20));
		pdb.set(base.push("step"), String.valueOf(10));	
	}
}
