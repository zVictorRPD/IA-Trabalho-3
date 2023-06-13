
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
 * Contains single fuzzy expression in the form "LV is hedge, hedge, ... MF"
 *
 * @version 2000 Original, v0.1, Edward S. Sazonov (esazonov@usa.com)
 * @version 12/19/2003 Modifications, v0.2,  Nazario Irizarry (naz-irizarry@excite.com)
 */
class FuzzyExpression {
    //Linguistic variable
    protected LinguisticVariable LV = null;
    
    //Member function
    protected String MF = null;
    
    //Vecot of hedges - the sequence of appearance is equivalent to Stack.pop() operations
    protected ArrayList hedges = null;
    
    
    //Text representation of this expression
    protected String textExpression;
    
    //Public flags for structuring a rule - have no accessors
    protected boolean flagAND = false;
    protected boolean flagOR = false;
    
    //Nesting counters - have no accessors
    protected int nestingUp = 0;
    protected int nestingDown = 0;
    /**
     * Evaluate this fuzzy expression, compute and store result.
     * @return Result of evaluation
     * @exception engine.RulesParsingException This exception is thrown
     * if an error occurs during evaluation
     */
    protected double evaluateExpression() throws EvaluationException {
        double result = 0.0;
        
        //Perform evaluation
        if(LV!=null && MF!=null)
            result = LV.is(MF);
        else
            throw new EvaluationException(" - Missing linguistic variable or membership function: ");
        
        if(result == -1)
            throw new EvaluationException(" - Unable to perform fuzzy evaluation: ");
        
        //Apply hedges if needed
        for (Iterator en = hedges.iterator() ; en.hasNext() ;) {
            result = ((Hedge)en.next()).hedgeIt(result);
        }
        
        return result;
    }
    /**
     * Execute this fuzzy expression with the given value and label.
     *
     * @param value result of the last fired expression.
     * @param label String label of this rule.
     * @exception engine.RulesParsingException This exception is thrown
     * if an error occurs during evaluation.
     */
    protected double  executeExpression(double value, String label) throws EvaluationException {
        double temp = value;
        
        //Apply hedges if needed
        for (Iterator en = hedges.iterator() ; en.hasNext() ;) {
            temp = ((Hedge)en.next()).hedgeIt(temp);
        }
        
        //Perform evaluation
        if(LV!=null && MF!=null) {
            if(temp>0.0) {
                try	{
                    LV.set(label, MF, temp);
                }
                catch(Exception e)
                {throw new EvaluationException(e.getMessage());}
            }
        }
        else {
            throw new EvaluationException(" - Missing linguistic variable or membership function: ");
        }
        
        return temp;
        
    }
    /**
     * Return text representation of this expression.
     * @return java.lang.String
     */
    protected String getTextExpression() {
        return textExpression;
    }
    /**
     * Constructor. Create an fuzzy expression "something is hedge, hedge, ... something".
     * For example, if expression is "temperature is not very high" then
     * lVariable = temperature,
     * mVariable = normal,
     * hVector = not, very,
     * textRepresentation = temperature is not very high.
     * This class is a service class for FuzzyRule and should not be used otherwise.
     * @param lv engine.LinguisticVariable Lingustic variable
     * @param mf java.lang.String Membership function
     * @param hVector java.util.Vector Vector containing all the hedges
     * @param textRepresentation java.lang.String Text representation of this expression
     */
    protected FuzzyExpression(LinguisticVariable lVariable, String mVariable, ArrayList hVector, String textRepresentation) {
        LV = lVariable;
        MF = mVariable;
        hedges = hVector;
        textExpression = textRepresentation;
    }
}
