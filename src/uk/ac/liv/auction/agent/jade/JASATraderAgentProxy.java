package uk.ac.liv.auction.agent.jade;

import uk.ac.liv.auction.agent.RoundRobinTrader;
import uk.ac.liv.auction.core.Auction;
import uk.ac.liv.auction.core.Shout;

import jade.core.*;
import jade.lang.acl.*;

import jade.content.onto.OntologyException;

public class JASATraderAgentProxy extends JASAProxy implements RoundRobinTrader {

  AID auctioneerID;

  public JASATraderAgentProxy( AID auctioneerID, AID targetJadeID, Agent sender ) {
    super(targetJadeID, sender);
    this.auctioneerID = auctioneerID;
  }

  public void requestShout( Auction auction ) {
    try {
      ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
      msg.addReceiver(targetJadeID);
      RequestShoutAction content = new RequestShoutAction();
      JADEAbstractAuctionAgent.sendMessage(sender, msg, content);
    } catch ( Exception e ) {
      e.printStackTrace();
      throw new Error(e.getMessage());
    }
  }

  public void informOfSeller(Shout winningShout, RoundRobinTrader seller,
                             double price, int quantity) {
    try {
      ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
      BidSuccessfulPredicate content = new BidSuccessfulPredicate();
      content.setPrice(price);
      content.setQuantity(quantity);
      content.setSeller(((JASATraderAgentProxy) seller).getSenderAID().getName());
      JADEAbstractAuctionAgent.sendMessage(sender, msg, content);
    } catch ( Exception e ) {
      e.printStackTrace();
      throw new Error(e.getMessage());
    }
  }

  public AID getSenderAID() {
    return sender.getAID();
  }

  public int getId() {
    //TODO
    return -1;
  }

  public void reset() {
    //TODO
  }

}