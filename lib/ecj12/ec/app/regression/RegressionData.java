package ec.app.regression;
import ec.util.*;
import ec.*;
import ec.gp.*;

/* 
 * RegressionData.java
 * 
 * Created: Wed Nov  3 18:32:13 1999
 * By: Sean Luke
 */

/**
 * @author Sean Luke
 * @version 1.0 
 */

public class RegressionData extends GPData
    {
    // return value
    public double x;

    public GPData copyTo(final GPData gpd) 
        { ((RegressionData)gpd).x = x; return gpd; }
    }