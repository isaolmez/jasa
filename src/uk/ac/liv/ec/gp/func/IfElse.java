package uk.ac.liv.ec.gp.func;

import ec.gp.*;
import ec.*;

public class IfElse extends GPNode {

  public void eval( EvolutionState state, int thread, GPData input,
                      ADFStack stack, GPIndividual individual, Problem problem ) {

    children[0].eval(state,thread,input,stack,individual,problem);

    if ( ((Boolean) ((GPGenericData) input).data).booleanValue() ) {
      children[1].eval(state,thread,input,stack,individual,problem);
    } else {
      children[2].eval(state,thread,input,stack,individual,problem);
    }
  }

  public String toString() {
    return "IfElse";
  }
}