package uk.ac.liv.auction.ec.gp.func;

import uk.ac.liv.auction.core.Shout;

import ec.gp.*;
import ec.*;


public class CurrentShout extends GPNode {

  public void eval(EvolutionState state, int thread, GPData input,
                    ADFStack stack, GPIndividual individual, Problem problem) {
    GPAuctioneer auctioneer = (GPAuctioneer) individual;
    Shout result = auctioneer.getCurrentShout();
    ((GPAuctionData) input).set(result);
  }

  public String toString() {
    return "CurrentShout";
  }

}