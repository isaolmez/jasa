package ec.app.multiplexerslow.func;
import ec.*;
import ec.app.multiplexerslow.*;
import ec.gp.*;
import ec.util.*;

/* 
 * D7.java
 * 
 * Created: Wed Nov  3 18:26:37 1999
 * By: Sean Luke
 */

/**
 * @author Sean Luke
 * @version 1.0 
 */

public class D7 extends GPNode
    {
    public String toString() { return "d7"; }

    public void checkConstraints(final EvolutionState state,
                                 final int tree,
                                 final GPIndividual typicalIndividual,
                                 final Parameter individualBase)
        {
        super.checkConstraints(state,tree,typicalIndividual,individualBase);
        if (children.length!=0)
            state.output.error("Incorrect number of children for node " + 
                               toStringForError() + " at " +
                               individualBase);
        }

    public void eval(final EvolutionState state,
                     final int thread,
                     final GPData input,
                     final ADFStack stack,
                     final GPIndividual individual,
                     final Problem problem)
        {
        ((MultiplexerData)input).x = 
            ((((Multiplexer)problem).data >>> 7 ) & 1);
        }
    }


