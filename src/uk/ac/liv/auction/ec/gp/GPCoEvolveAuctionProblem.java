package uk.ac.liv.auction.ec.gp;


import java.util.*;

import java.io.*;

import ec.*;
import ec.gp.*;
import ec.gp.koza.*;
import ec.util.*;

import uk.ac.liv.ec.coevolve.*;

import uk.ac.liv.util.*;
import uk.ac.liv.util.io.*;

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.agent.*;
import uk.ac.liv.auction.electricity.*;
import uk.ac.liv.auction.stats.*;

import uk.ac.liv.auction.ec.gp.func.*;



/**
 * <p>Title: JASA</p>
 * <p> </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p> </p>
 * @author Steve Phelps
 *
 */

public class GPCoEvolveAuctionProblem extends GPProblem implements CoEvolutionaryProblem {

  static int NS = 2;
  static int NB = 3;

  static int CS = 10;
  static int CB = 10;

  static int MAX_ROUNDS = 1000;

  static final String P_TRADERS = "numtraders";
  static final String P_ROUNDS = "maxrounds";
  static final String P_ITERATIONS = "iterations";

  static final int buyerValues[] = { 36, 17, 12 };

  static final int sellerValues[] = { 35, 16, 11 };

  protected RoundRobinAuction auction;

  protected List buyers, sellers;

  protected CSVWriter statsOut;

  protected ArrayList allTraders;

  protected MarketDataLogger logger;


  public Object protoClone() throws CloneNotSupportedException {

    GPCoEvolveAuctionProblem myobj = (GPCoEvolveAuctionProblem) super.protoClone();
    //TODO?
    return myobj;
  }


  public void setup( EvolutionState state, Parameter base ) {

    super.setup(state,base);

    boolean uniPrice = state.parameters.getBoolean(base.push("uniprice"),null,false);

    NS = state.parameters.getIntWithDefault(base.push("ns"), null, 3);
    NB = state.parameters.getIntWithDefault(base.push("nb"), null, 3);

    CS = state.parameters.getIntWithDefault(base.push("cs"), null, 10);
    CB = state.parameters.getIntWithDefault(base.push("cb"), null, 10);

    String statsFileName = state.parameters.getStringWithDefault(base.push("statsfile"), "coevolve-electricity-stats.csv");

    auction = new RandomRobinAuction();
    auction.setMaximumRounds(MAX_ROUNDS);

    allTraders = new ArrayList(10);
    sellers = registerTraders(allTraders, auction, true, NS, CS, sellerValues);
    buyers = registerTraders(allTraders, auction, false, NB, CB, buyerValues);

    logger = new StatsMarketDataLogger();
    auction.setMarketDataLogger(logger);

    try {
      statsOut = new CSVWriter(new FileOutputStream(statsFileName), 7 + NS + NB);//13);
    } catch ( IOException e ) {
      e.printStackTrace();
    }

  }

  public void evaluate( EvolutionState state, Vector[] group, int thread ) {

    Iterator traders = allTraders.iterator();

    auction.reset();

    for( int i=0; traders.hasNext(); i++ ) {
      ElectricityTrader trader = (ElectricityTrader) traders.next();
      GPTradingStrategy strategy = (GPTradingStrategy) group[i+1].get(0);
      strategy.setGPContext(state, thread, stack, this);
      trader.setStrategy(strategy);
      strategy.setAgent(trader);
      trader.reset();
    }

    GPAuctioneer auctioneer = (GPAuctioneer) group[0].get(0);
    auctioneer.setGPContext(state, thread, stack, this);
    auctioneer.setAuction(auction);
    auction.setAuctioneer(auctioneer);

    auction.run();

    traders = allTraders.iterator();

    // Set the fitness for the strategy population according to profits made
    for( int i=0; traders.hasNext(); i++ ) {
      ElectricityTrader trader = (ElectricityTrader) traders.next();
      double profits = trader.getProfits();
      float fitness = Float.MAX_VALUE;
      if ( (!Double.isNaN(profits)) ) {
        fitness = 10000000f - (float) profits;
      }
      if ( fitness < 0 ) {
        fitness = 0;
      }
      System.out.println("Fitness for " + trader);
      System.out.println("profits = " + profits);
      System.out.println("fitness = " + fitness);
      GPTradingStrategy individual = (GPTradingStrategy) group[i+1].get(0);
      ((KozaFitness) individual.fitness).setStandardizedFitness(state, fitness);
      individual.evaluated = true;
    }

    ElectricityStats stats = new ElectricityStats(0, 200, auction);
    System.out.println("Market stats = " + stats);
    float relMarketPower = (float) (Math.abs(stats.mPB) + Math.abs(stats.mPS)) / 2.0f;
    float fitness = 1000000;
    if ( !Float.isInfinite(relMarketPower) && !Double.isNaN(stats.eA) ) {
      fitness = relMarketPower + 1-((float) stats.eA/100);  //TODO!
    }
    System.out.println("Auctioneer fitness = " + fitness);
    GPIndividual individual = (GPIndividual) group[0].get(0);
    ((KozaFitness) individual.fitness).setStandardizedFitness(state, fitness);
    individual.evaluated = true;


  }


  public static List registerTraders( ArrayList allTraders,
                                      RoundRobinAuction auction,
                                      boolean areSellers, int num, int capacity,
                                      int[] values ) {
    System.out.println("num = " + num);
    ArrayList result = new ArrayList();
    for( int i=0; i<num; i++ ) {

      // Construct a trader for this record
      ElectricityTrader trader =
        new ElectricityTrader(capacity, values[i % values.length], 0,
                            areSellers);

      result.add(trader);
      allTraders.add(trader);
      auction.register(trader);
      System.out.println("Registered " + trader);
    }

    return result;
  }

  public static void main( String[] args ) {
    ec.Evolve.main(new String[] { "-file", "ecj.params/coevolve-gpauctioneer.params"} );
  }

}