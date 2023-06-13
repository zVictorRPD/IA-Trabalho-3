
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
import java.io.PrintWriter;

/**
 * Container for a fuzzy rule.
 * This class contains vectors of left-part and right-part expressions
 * (before and after "then") for a fuzzy rule.
 *
 * @version 2000 Original, v0.1, Edward S. Sazonov (esazonov@usa.com)
 */
public class FuzzyRule {
    //Name of this rule
    protected String name;
    
    //Description of this rule
    protected String description;
    
    //Left and right sides
    protected ArrayList leftPartExpressions = null;
    protected ArrayList rightPartExpressions = null;
    
    //Flag to indicate that this rules has fired
    protected boolean fired = false;
    
    protected double evaluationResult;
    
    
    /**
     * Evaluate left part and execute right part of a pre-parsed rule.
     * Changes value of the private variable evaluationResult.
     * This call will set fired to true if any expression fires.
     * @param out is the PrintWriter to use to write a text representation of the rule
     * with all expressions evaluated. May be null.
     */
    protected void evaluateRule(PrintWriter out) throws EvaluationException {
        //Final result
        double accumulatedResult = 1.0;
        
        //Nesting Stack
        Stack nestingStack = new Stack();
        
        //Flags
        boolean flagAND = false;
        boolean flagOR = false;
        
        //Evaluate
        for (Iterator en = leftPartExpressions.iterator() ; en.hasNext() ;) {
            FuzzyExpression tempExpression = (FuzzyExpression)en.next();
            
            //Extract flags
            flagAND = tempExpression.flagAND;
            flagOR = tempExpression.flagOR;
            
            //If nesting up
            for(int i=0; i<tempExpression.nestingUp; i++) {
                //Nesting
                nestingStack.push(new StackElement(accumulatedResult,flagAND,flagOR));
                flagAND=flagOR=false;
                accumulatedResult = 0.0;
            }
            
            //Evaluate expression
            double tempResult = 0.0;
            try {
                tempResult = tempExpression.evaluateExpression();
                
                //Add to output string
                if (out != null) {
                    writeValue(out, tempExpression, tempResult);
                }
            }
            catch(Exception e) {
                //Add exception handling
                System.out.println("Exception: "+e.getMessage());
                throw new EvaluationException(e.getMessage());
            }
            
            
            //If AND / OR / STORE operations
            if(!flagAND && !flagOR) {
                accumulatedResult = tempResult;
            }
            if(flagAND && !flagOR) {
                accumulatedResult = accumulatedResult > tempResult ? tempResult : accumulatedResult;
            }
            if(!flagAND && flagOR) {
                accumulatedResult = accumulatedResult < tempResult ? tempResult : accumulatedResult;
            }
            
            //If nesting down
            for(int i=0; i<tempExpression.nestingDown; i++) {
                StackElement tempSE = (StackElement) nestingStack.pop();
                flagAND = tempSE.flagAND;
                flagOR = tempSE.flagOR;
                
                if(flagAND && !flagOR) {
                    accumulatedResult = accumulatedResult > tempSE.accumulatedResult ?
                    tempSE.accumulatedResult : accumulatedResult;
                }
                if(!flagAND && flagOR) {
                    accumulatedResult = accumulatedResult < tempSE.accumulatedResult ?
                    tempSE.accumulatedResult : accumulatedResult;
                }
            }
        }
        
        evaluationResult = accumulatedResult;
        
        //Reset the flag
        clearFired();
        
        //Execute assignments
        for (Iterator en = rightPartExpressions.iterator() ; en.hasNext() ;) {
            FuzzyExpression tempExpression = (FuzzyExpression)en.next();
            
            try {
                double temp = tempExpression.executeExpression(evaluationResult, getName());
                if(temp>0.0)
                    setFired();
                
                //Add to output string
                if (out != null) {
                    writeValue(out, tempExpression, temp);
                }
            }
            catch(Exception e) {
                //Add exception handling
                System.out.println("Exception: "+e.getMessage());
                throw new EvaluationException(e.getMessage());
            }
            
        }

    }

    /**
     * Build a display string.
     *
     * @param out
     * @param tempExpression
     * @param tempResult
     */
    protected void writeValue(final PrintWriter out, final FuzzyExpression tempExpression, final double tempResult) {
        out.print(tempExpression.getTextExpression());
        out.print("(");
        String s = String.valueOf(tempResult);
        out.print(s.substring(0,s.length() > 3 ? 4 : 3));
        out.print(") ");
    }
    /**
     * Evaluate left part and execute right part of a pre-parsed rule.
     * Changes value of the private variable evaluationResult.
     * This call will set fired to true if any expression fires.
     */
    protected void evaluateRule() throws EvaluationException {
        evaluateRule(null);
    }
    
    /**
     * Returns the result of the left part evaluation.
     * @return double
     */
    public double getEvaluationResult() {
        return evaluationResult;
    }
    
    
    /**
     * Returns the java.lang.Vector containing all expression for the left part of the rule.
     * @return java.util.Vector
     */
    protected List getLeftPartExpressions() {
        return leftPartExpressions;
    }
    /**
     * Returns the java.lang.Vector containing all expression for the right part of the rule.
     * @return java.util.Vector
     */
    protected List getRightPartExpressions() {
        return rightPartExpressions;
    }
    
    /**
     * Called everytime a right part expression is fired.
     */
    protected void setFired() {
        fired = true;
    }
    
    /**
     * Called just before the right-part expressions are evaluated.
     */
    protected void clearFired() {
        fired = false;
    }
    
    /**
     * Returns true if the rule fired (during executeRule call).
     * @return boolean
     */
    public boolean isRuleFired() {
        return fired;
    }
    /**
     * Constructor.
     * @param name This rule label.
     */
    public FuzzyRule(String name) {
        this();
        setName(name);
    }
    /**
     * Constructor.
     */
    protected FuzzyRule() {
        init();
    }
    
    protected void init() {
        leftPartExpressions = new ArrayList(3);
        rightPartExpressions = new ArrayList(2);
    }
    
    /**
     * Set label for this rule.
     *
     * @param newLabel java.lang.String
     */
    protected void setName(String name) {
        this.name = name;
    }
    
    /**
     * This method is a start (albeit trivial) to move towards JSR94
     *
     * @return the name of this block of rules
     */
    public String getName() {
        return name;
    }
    
    /**
     * This method is a start (albeit trivial) to move towards JSR94
     *
     * @return the description of this block of rules
     */
    public String getDescription() {
        return description;
    }
}
