/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2003 Steve Phelps
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

package uk.ac.liv.auction.agent.jade;

import uk.ac.liv.auction.agent.*;

import uk.ac.liv.auction.core.*;

import jade.core.*;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;

import jade.content.*;
import jade.content.onto.OntologyException;
import jade.content.lang.Codec;

import jade.proto.*;

import jade.wrapper.PlatformController;
import jade.wrapper.AgentController;

import jade.lang.acl.*;

import jade.content.lang.sl.*;

import jade.domain.FIPAAgentManagement.*;
import jade.domain.*;


public abstract class JADEAbstractAuctionAgent extends jade.core.Agent {


  /**
   * Setup the agent.  Registers with the DF, and adds a behaviour to
   * process incoming messages.
   */
  protected void setup() {
    try {
      System.out.println( getLocalName() + " setting up");

      // Create the agent descrption of itself
      DFAgentDescription dfd = new DFAgentDescription();
      dfd.setName(getAID());
      ServiceDescription sd = new ServiceDescription();
      sd.setName(getServiceName());
      sd.setType(getServiceName());
      dfd.addServices(sd);
      DFService.register(this, dfd);

      // Register the codec for the SL0 language
      getContentManager().registerLanguage(new SLCodec(), FIPANames.ContentLanguage.FIPA_SL0);

      // Register the ontology used by this application
      getContentManager().registerOntology(AuctionOntology.getInstance());

      addBehaviours();

    } catch ( Exception e ) {
      e.printStackTrace();
    }

  }

  public static void sendMessage( Agent agent, ACLMessage msg,
                                  ContentElement content )
       throws OntologyException, Codec.CodecException {
    msg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
    msg.setOntology(AuctionOntology.NAME);
    agent.getContentManager().fillContent(msg, content);
    System.out.println("Sending: " + msg);
    agent.send(msg);
  }

  public AID findAuctioneer() throws FIPAException, InterruptedException  {
    AID auctioneerAID = null;
    DFAgentDescription dfd = new DFAgentDescription();
    ServiceDescription sd = new ServiceDescription();
    sd.setType(JADEAuctionAdaptor.SERVICE_AUCTIONEER);
    dfd.addServices(sd);
    while (true) {
      System.out.println(getLocalName()+ " waiting for a JASAAuctioneer registering with the DF");
      SearchConstraints c = new SearchConstraints();
      c.setMaxDepth(new Long(3));
      DFAgentDescription[] result = DFService.search(this,dfd,c);
      if ((result != null) && (result.length > 0)) {
        dfd = result[0];
        auctioneerAID = dfd.getName();
        break;
      }
      Thread.sleep(10000);
    }
    System.out.println("found auctioneer with aid = " + auctioneerAID);
    return auctioneerAID;
  }

  public abstract void addBehaviours();

  public abstract String getServiceName();


}
