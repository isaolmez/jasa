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
package uk.ac.liv.auction.config;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * Defines the relative ratio of the populations of different agents.
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 */
public class Ratio implements ParameterBasedCase {

  public static final String P_AGENTTYPE = "agenttype";

  public static final String P_NUMAGENTS = "numagents";

  private int s;

  private int b;

  public Ratio() {
  }

  public void setParameter(String param) {
    String values[] = param.split(":");

    s = Integer.parseInt(values[0]);
    b = Integer.parseInt(values[1]);
  }

  public String toString() {
    return s + "|" + b;
  }

  public void apply(ParameterDatabase pdb, Parameter base) {
    int groupsize = pdb.getInt(base.push(GroupSize.P_GROUPSIZE));
    int sellerNum = s * groupsize;
    int buyerNum = b * groupsize;

    pdb.set(base.push(P_AGENTTYPE + ".0." + P_NUMAGENTS), String
        .valueOf(sellerNum));
    pdb.set(base.push(P_AGENTTYPE + ".1." + P_NUMAGENTS), String
        .valueOf(buyerNum));
  }

}