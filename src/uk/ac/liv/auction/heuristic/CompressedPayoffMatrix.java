/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2004 Steve Phelps
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
 
package uk.ac.liv.auction.heuristic;

import java.util.Vector;
import java.util.Iterator;

import java.io.PrintWriter;

import uk.ac.liv.util.Partitioner;
import uk.ac.liv.util.BaseNIterator;

import uk.ac.liv.util.io.DataWriter;

/** 
 * @author Steve Phelps
 * @version $Revision$
 */


public class CompressedPayoffMatrix {
  
  protected int numPlayers;
  
  protected int numStrategies;
  
  protected Vector matrix;
  
  public CompressedPayoffMatrix( int numPlayers, int numStrategies ) {
    this.numPlayers = numPlayers;
    this.numStrategies = numStrategies;    
    matrix = initialiseMatrix(numPlayers, numStrategies);       
  }
  
  protected Vector initialiseMatrix( int numPlayers, int s ) {
    Vector v = new Vector(numPlayers);
    if ( s == 1 ) {
      for( int i=0; i<=numPlayers; i++ ) {
        v.add( new double[numStrategies] );
      }
      return v;
    } else {
      for( int i=0; i<=numPlayers; i++ ) {
        v.add(initialiseMatrix(numPlayers, s-1));
      }
      return v;
    }
  }
  
  public double[] getCompressedOutcome( int[] compressedEntry ) {
    Vector v = matrix;
    int i;
    for( i=0; i<compressedEntry.length-1; i++ ) {
      v = (Vector) v.get(compressedEntry[i]);
    }
    return (double[]) v.get(compressedEntry[i]);
  }

  public void setCompressedOutcome( int[] compressedEntry, 
                                      double[] compressedOutcome ) {
    Vector v = matrix;
    int i;
    for( i=0; i<compressedEntry.length-1; i++ ) {
      v = (Vector) v.get(i);
    }
    v.set(compressedEntry[i], compressedOutcome);
  }
   
  public Iterator compressedEntryIterator() {  
    return new Partitioner(numPlayers, numStrategies);
  }
  
  public Iterator fullEntryIterator() {
    return new BaseNIterator(numStrategies, numPlayers);
  }
  
  public double[] getFullOutcome( int[] fullEntry ) {
    int[] compressedEntry = new int[numStrategies];
    for( int i=0; i<fullEntry.length; i++ ) {
      compressedEntry[fullEntry[i]]++;
    }
    double[] compressedOutcome = getCompressedOutcome(compressedEntry);
    double[] fullOutcome = new double[numPlayers];
    for( int i=0; i<numPlayers; i++ ) {
      fullOutcome[i] = compressedOutcome[fullEntry[i]];
    }
    return fullOutcome;
  }
  
  public void export( DataWriter out ) {
    Iterator entries = compressedEntryIterator();
    while ( entries.hasNext() ) {
      int[] entry = (int[]) entries.next();
      for( int i=0; i<entry.length; i++ ) {
        out.newData(entry[i]);
      }
      double[] outcome = getCompressedOutcome(entry);
      for( int i=0; i<outcome.length; i++ ) {
        out.newData(outcome[i]);      
      }
    }
  }
  
  public void exportToGambit( PrintWriter nfgOut ) {
    exportToGambit(nfgOut, "JASA NFG");
  }
  
  public void exportToGambit( PrintWriter nfgOut, String title ) {
    
    nfgOut.print("NFG 1 R \"" + title + "\" { ");
    for( int i=0; i<numPlayers; i++ ) {
      nfgOut.print("\"Player" + (i+1) + "\" ");
    }
    nfgOut.println("}");
    nfgOut.println();
    
    nfgOut.print("{ ");
    for( int i=0; i<numPlayers; i++ ) {
      nfgOut.print("{ ");
      for ( int j=0; j<numStrategies; j++ ) {
        nfgOut.print("\"Strategy" + j + "\" ");
      }
      nfgOut.println("}");
    }
    nfgOut.println("}");
    
    nfgOut.println("\"\"");
    nfgOut.println();
    
    nfgOut.println("{");    
    int numEntries = 0; 
    Iterator entries = fullEntryIterator();
    while ( entries.hasNext() ) {
      nfgOut.print("{ \"");
      int[] fullEntry = (int[]) entries.next();
      for( int i=fullEntry.length-1; i>=0; i-- ) {
        nfgOut.print(fullEntry[i]+1);
      }
      nfgOut.print("\" ");
      double[] outcome = getFullOutcome(fullEntry);
      for( int i=outcome.length-1; i>=0; i-- ) {
        nfgOut.print(outcome[i]);
        if ( i > 0 ) {
          nfgOut.print(",");
        }        
        nfgOut.print(" ");
      }      
      nfgOut.println("}");
      numEntries++;
    }
    nfgOut.println("}");
    for( int i=1; i<=numEntries; i++ ) {
      nfgOut.print(i);
      if ( i < numEntries ) {
        nfgOut.print(" ");
      }      
    }
    nfgOut.flush();
  }

}
