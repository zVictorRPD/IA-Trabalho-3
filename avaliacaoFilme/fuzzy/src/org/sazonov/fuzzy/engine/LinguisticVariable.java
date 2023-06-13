
//Copyright (C) 2000  Edward S. Sazonov (esazonov@usa.com)

//This program is free software; you can redistribute it and/or
//modify it under the terms of the GNU General Public License
//as published by the Free Software Foundation; either version 2
//of the License, or (at your option) any later version.

//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.

//You should have received a copy of the GNU General Public License
//along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

package org.sazonov.fuzzy.engine;

import java.util.*;

/**
 * Class for linguistic variables.
 *
 * @version 2000 Original, v0.1, Edward S. Sazonov (esazonov@usa.com)
 * @version 12/19/2003 Modifications, v0.2,  Nazario Irizarry (naz-irizarry@excite.com)
 */
public class LinguisticVariable {
    
    private String name;
    protected Map storage;
    protected double input_value;
    
    //Min and max support values for defuzzification
    protected double minSupport;
    protected double maxSupport;
    boolean minMaxSet;
    
    //Need to refer to the context to lookup label weights
    protected FuzzyState context;
    
    protected Defuzzifier defuzzifier;
    
    /**
     * Constructor
     * @param name Name of this linguistic variable
     */
    protected LinguisticVariable(String name, Defuzzifier defuzzifier) {
        this.name = name;
        this.defuzzifier = defuzzifier;
        storage = new HashMap();
    }
    
    /**
     * Add a membership function. This happens at "compile" time
     * when the linguistic variables and their member functions are defined.
     *
     * @param mFunction MembershipFunction to be added.
     */
    public void add(MemberFunction mFunction) {
        storage.put(mFunction.getName(),mFunction);
        
        //Check if there should be a change in min and max support values
        if (minMaxSet) {
            double v = mFunction.getMin();
            if(v<minSupport)	
                minSupport = v;
            v = mFunction.getMax();
            if(v>maxSupport)	
                maxSupport = v;
        }
        else {
            minSupport = mFunction.getMin();
            maxSupport = mFunction.getMax();
            minMaxSet = true;
        }
    }

    /**
     * Return an copy of itself with a different name.
     * The copy is exact except that rules fired since the last reset()
     * will not influence defuzzification result.
     *
     * @param name the name of the copy
     * @return engine.LinguisticVariable
     */
    public LinguisticVariable copy(String name) {
        //Create new LV
        LinguisticVariable tempLV = Factory.getFactory().makeLinguisticVariable(name);
        
        //Copy membership functions
        for(Iterator e = storage.values().iterator(); e.hasNext(); ) {
            tempLV.add((MemberFunction)e.next());
        }
        //Copy input value
        tempLV.setInputValue(input_value);
        
        return tempLV;
    }
    
    /**
     * Defuzzify using centroid.
     * Memebership functions are scaled by product and combined by summation.
     * Ultimately we want to have additional modes of summation.
     *
     * @return double Result of defuzzification
     * @exception fuzzyEngine.NoRulesFiredException This exception is thrown if no rules have
     * fired for this Linguistic Variable.
     */
    public double defuzzify() throws NoRulesFiredException {
        
        double step = (maxSupport-minSupport) * 0.01;
        double [] sum = new double [100];

        //Find sum of scaled functions
        buildCompositeFunc(step, sum);
        
        return defuzzifier.defuzzify(minSupport, step, sum);

    }

    /**
     * Builds a composite fuzzy function which is a combination of
     * the membership functions that have fired with their weights
     * appropriately combined.
     *
     * @param step
     * @param sum
     * @throws NoRulesFiredException
     */
    private void buildCompositeFunc(final double step, final double[] sum)
            throws NoRulesFiredException {

        int fired = 0;
        Iterator iterator = storage.values().iterator();        
        while(iterator.hasNext()) {
            MemberFunction mf = (MemberFunction) iterator.next();
            if (mf.hasWeight()) {
                fired++;
                        
                //Scale and change weight if needed
                double weight = mf.getCombinedWeights();
        //                System.out.println(this+" weights "+mf+" --> "+weight);


                /*
                 * We want to limit the amount of math
                 * so we want to home in on the non-zero part of this
                 * member function
                 */

                int jMin = (int)((mf.getMin() - minSupport)/step);
                if (jMin < 0)
                    jMin = 0;
                int jMax = 1 + (int)((mf.getMax() - minSupport)/step);
                if (jMax > sum.length)
                    jMax = sum.length;

                //Add to the sum
                double x = minSupport + jMin*step;
                for(int j=jMin; j<jMax; j++) {
                    //double x = minSupport + j*step;
                    sum[j] += weight * mf.fuzzify(x);
                    x += step;
                }
            }
        }
        if(fired == 0) {
            throw new NoRulesFiredException("No rules was fired for "+name);
        }
        
    }


    /**
     * Return the name of this linguistic variable
     * @return java.lang.String
     */
    public String getName() {
        return name;
    }
    /**
     * Return a MembershipFunction belonging to this
     * LinguisticVariable by its name.
     * @param name java.lang.String Name of the membership function.
     * @return MemebershipFunction
     */
    public MemberFunction getMemberFunctionByName(String name) {
        return (MemberFunction)storage.get(name);
    }
    /**
     * Perform fuzzification for an input value.
     * @param name Name of the membership function
     * @return double Returns result of fuzzification or -1 if fuzzification is impossible.
     */
    protected double is(String name) {
        MemberFunction m = getMemberFunctionByName(name);
        
        if(m!=null)		
            return m.fuzzify(input_value);
        else			
            return -1;
    }
    /**
     * Reset all rules that have previously fired for this LinguisticVariable.
     */
    public void reset() {
        Iterator iterator = storage.values().iterator();        
        while(iterator.hasNext()) {
            ((MemberFunction) iterator.next()).clearInferenceWeights();
        }
        
    }
    /**
     * Set input value for this lingustic variable.
     * @param value double
     */
    public void setInputValue(double value) {
        input_value = value;
    }

    /**
     * Used when a liguistic variable is registered.  Allows
     * the LV to reach back and lookup label weights if needed
     * during defuzzification.
     */
    protected void setContext(FuzzyState context) {
        this.context = context;
    }
    
    /**
     * Store result of a fired rule.
     * This method is invoked if a rule fired. It stores fired rule's label,
     * name of the membership function for which the rule fired and membership value.
     * Stored information is used when calling defuzzify().
     *
     * @param label Label of the fired rule, null if rule has no label.
     * @param name Name of the membership function
     * @param value Membership value.
     */
    protected void set(String label, String name, double value) throws RulesParsingException {
        MemberFunction mf = getMemberFunctionByName(name);
        if (mf == null) {
            throw new RulesParsingException("Wrong name of a membership function: "+name);
        }
        
        double labelWeight = context.computeLabelWeight(label);
        mf.addInferenceWeight(labelWeight * value);
    }
    
    public String toString() {
        return "LV "+getName();
    }
     
}


