
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
import java.io.Reader;

/**
 * Contains the state of the fuzzy engine.  State consists of
 * the linguistic variables, the hedges, and the label weights that
 * are applied to entire rules.
 *
 * Note: This class is not thread-safe.
 *
 * @version 2000 Original, v0.1, Edward S. Sazonov (esazonov@usa.com)
 * @version 12/19/2003 Modifications, v0.2,  Nazario Irizarry (naz-irizarry@excite.com)
 */
public class FuzzyState implements Constants {
    
    
    //Hashtables
    protected HashMap lvHash = null;
    protected HashMap hedgesHash = null;
    protected HashMap labelWeights = null;


    
    //Global engine state
    private int engineState = UNDEFINED;
    
    
    boolean flagRuleFired = false;
        
    /**
     * Add a hedge (derived from engine.Hedge) to the engine
     * @param hedge Implementation of a hedge
     */
    public void addHedge(Hedge hedge) {
        hedgesHash.put(hedge.getName(),hedge);
    }
         
    /**
     * Returns "true" if a rule has fired during the last call to FuzzyEngine.evaluateRule(String).
     * If a block of rules has been evaluated, the function FuzzyBlockOfRules.isRuleFired()
     * should be called instead.
     * @return "True" if the last evaluated rules has fired, "false" if not.
     */
    public boolean isRuleFired() {
        return flagRuleFired;
    }

    
    /**
     * Register a linguistic variable with the engine.
     * Any lingustic variable participating in fuzzy evaluations should be registered with the engine.
     * The same lingustic variable cannot be registered with different engines if labels are used.
     * @param function fuzzyEngine.LinguisticVariable to be registered with the engine.
     */
    public void register(LinguisticVariable lv) {
        //Store the LV itself
        lvHash.put(lv.getName(), lv);
        
        //Provide access from LV to labels' weights hash
        //lv.setLabelWeightsHash(labelWeights);
        lv.setContext(this);
    }
    
    /**
     * Reset all previously fired rules.
     * Call to this function clears all rules and resets the engine to its initial state.
     */
    public void reset() {
        //Reset fired rules
        flagRuleFired = false;
        for (Iterator en = lvHash.values().iterator() ; en.hasNext() ;) {
            ((LinguisticVariable)en.next()).reset();
        }
        //Reset labels' weights changes
        for (Iterator en = labelWeights.values().iterator() ; en.hasNext() ;) {
            ((LinguisticVariable)en.next()).reset();
        }
    }
    /**
     * Constructor.
     * Engine initialization is performed here.
     */
    protected FuzzyState() {
        
        //Create LV hashtable
        lvHash = new HashMap();
                
        //Initialize hedges hash
        hedgesHash = new HashMap();
        Factory factory = Factory.getFactory();
        addHedge(factory.makeHedgeNot());
        addHedge(factory.makeHedgeVery());
        addHedge(factory.makeHedgeSomewhat());
        
        //Create labels weights hash
        labelWeights = new HashMap();
        
       
    }
    
    /**
     * Determine the weight of the label.  The label can apply
     * to one or more rules.
     *
     * @param label
     * @return
     */
    double computeLabelWeight(String label) {
        double weight = 1.0;
        if(label != null) {
            //Look-up label weight in hash
            LinguisticVariable temp = (LinguisticVariable)labelWeights.get(label);
            
            //Catch exceptions if no weight change happened
            try{
                if(temp!=null)	
                    weight = temp.defuzzify();
            }
            catch(NoRulesFiredException e) {
                weight=1.0;
            }
        }
        

        return weight;
    }
    
    /**
     * @return true if the label is known
     */
    boolean containsLabel(String label) {
        return labelWeights.containsKey(label);
    }
    
    /**
     * Add a label
     */
    void addLabel(String label, LinguisticVariable lv) {
        labelWeights.put(label,lv);
    }
    
    /**
     * @return the specfied label
     */
    LinguisticVariable getLabel(String label) {
        return (LinguisticVariable) labelWeights.get(label);
    }

    /**
     * The reader sequence is read and parsed into rules. 
     * The name of this method follows JSR 94 although this class
     * does not yet fit the JSR 94 model.
     *
     * @param reader input sequence
     */
    public RuleBlock createRuleExecutionSet(Reader reader) throws RulesParsingException {
        RuleBlock rules = Factory.getFactory().makeRuleBlock(this);
        Factory.getFactory().makeRuleParser().parseBlock(reader, rules);
        return rules;
    }
 
}
