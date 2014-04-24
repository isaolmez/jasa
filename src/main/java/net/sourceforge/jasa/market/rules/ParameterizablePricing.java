/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2013 Steve Phelps
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

package net.sourceforge.jasa.market.rules;

/**
 * <p>
 * Auctioneer classes implementing this interface indicate that they support
 * parameterisable pricing rules, as per the k-double-market variants.
 * </p>
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public interface ParameterizablePricing {

	public void setK(double k);

	public double getK();

}