package uk.ac.liv.auction.ec.gp.func;

import ec.gp.*;
import ec.*;

import uk.ac.liv.auction.core.QuoteProvider;
import uk.ac.liv.auction.core.MarketQuote;

import uk.ac.liv.ec.gp.func.*;

import uk.ac.liv.util.GenericDouble;


public class QuoteAskPrice extends GPNode {

  public void eval( EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual, Problem problem ) {
    ((GPGenericData) input).data = new GenericDouble( new Double(((QuoteProvider) individual).getQuote().getAsk()) );
  }

  public String toString() {
    return "QuoteAskPrice";
  }
}