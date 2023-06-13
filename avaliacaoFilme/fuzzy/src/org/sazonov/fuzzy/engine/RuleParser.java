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

import java.io.*;


/**
 * Parses rules. The heart of this code was originally bundled as part of 
 * Sazonov's FuzzyEngine class.
 *
 * @version 2000 Original, v0.1, Edward S. Sazonov (esazonov@usa.com)
 * @version 12/19/2003 Modifications, v0.2,  Nazario Irizarry (naz-irizarry@excite.com)
 */
class RuleParser implements Constants {

    protected Map controlHash = null;
    //Nesting stack
    protected Stack nestingStack = null;
    
    protected StringBuffer out;

    /** Creates a new instance of RuleParser */
    protected RuleParser() {
        
        //Create nesting stack
        nestingStack = new Stack();
        out = new StringBuffer(100);
        
        initKeywords();
    }
    
    protected void initKeywords() {
        //Init. control controlHash hashtable
        controlHash = new HashMap();
        
        controlHash.put("if", new Integer(IF));
        controlHash.put("then", new Integer(THEN));
        controlHash.put("is", new Integer(IS));
        controlHash.put("and", new Integer(AND));
        controlHash.put("or", new Integer(OR));
        controlHash.put("(",  new Integer(LEFTP));
        controlHash.put(")", new Integer(RIGHTP));
        controlHash.put(" ", new Integer(NOP));
        controlHash.put("rule", new Integer(RULE));
        controlHash.put("weight", new Integer(WEIGHT));
        controlHash.put("set", new Integer(SET));
    }
   
    /**
     * Parse the reader sequence to populate the block of rules
     */
    protected void parseBlock(Reader reader, RuleBlock block) throws RulesParsingException {
        StreamTokenizer st = new StreamTokenizer( reader );
        st.resetSyntax();
        st.wordChars('a', 'z');
        st.wordChars('A', 'Z');
        st.wordChars(128 + 32, 255);
        st.whitespaceChars(0, ' ');
        st.commentChar('/');
        st.quoteChar('"');
        st.quoteChar('\'');
//        st.parseNumbers();
        st.ordinaryChar('(');
        st.ordinaryChar(')');
        st.wordChars('0', '9');
        st.wordChars('_','_');
        st.eolIsSignificant(true);
        
        block.clearRules();
        
        do {
            block.addRule(parseRule(st, block.getFuzzyState()));
        }
        while ( st.ttype != StreamTokenizer.TT_EOF);
        
    }
    
    /**
     * Parse a single rule from a StreamTokenizer sequence
     *
     * @param st the StreamTokenizer sequence
     * @param engine the context
     */
    protected FuzzyRule parseRule(StreamTokenizer st, FuzzyState engine) throws RulesParsingException {
        //Output rule
        FuzzyRule rule = Factory.getFactory().makeFuzzyRule();
        
        try {
            parseRuleTokens(st, engine, rule);
        }
        catch(RulesParsingException e) {
            throw new RulesParsingException("\nError parsing rule on line "+st.lineno()+"\n"+e.getMessage());
        }
        catch(IOException e) {
            throw new RulesParsingException("\nError parsing rule on line "+st.lineno()+"\n"+e.getMessage());
        }
        
        return rule;
        
    }

    
    /**
     * Parse a rule like: if a is b and/or hedge, hedge c is d then e is f.
     * Recursive nesting for multilevel logical dependencies. Recursion is organized
     * by employing nesting Stack instead of recursive calls because
     * it allows easy storage of compiled rules (in block parsing mode). <p>
     *
     * Two modes of operation (rule evaluation or block parsing mode) are determined
     * by the value of the parameter "rule": if "rule" is equal to null, then the engine is in
     * rule evaluation mode and input values of all linguistic variables should be set
     * for the call to complete successfully. If "rule" points to an instance of FuzzyRule,
     * then only parsing is performed without performing actual evaluation of the rule.
     * Results of the parsing  are stored in the "rule".
     *
     * @param tokens tokenized Text representation of a fuzzy rule split into tokens.
     * @param engine reference to the context
     * @param rule FuzzyRule Container for the parsed rule (when operating in block parsing mode, rule != null)
     *
     * @return java.lang.String Textual results of parsing are returned for review
     */
    protected String parseRuleTokens(StreamTokenizer tokens, FuzzyState engine, FuzzyRule rule)
    throws RulesParsingException, IOException {
        
        // reset display buffer
        out.setLength(0);
        
        //Conrol Word
        int controlWord = NOP;
        
        //Current state of the engine
        int engineState = LABEL;
        
        //Current subState
        int engineSubState = READY;
        
        //Current linguistic variable
        LinguisticVariable lVariable = null;
        
        //Current hedge
        Hedge hVariable = null;
        
        //Current membersip function by name
        String mVariable = null;
        
        //Storage variables
        double accumulatedResult = 0.0;
        String currentToken = null;
        
        //Flags
        boolean flagAND = false;
        boolean flagOR = false;
        
        //Nesting up counter - for block parsing mode
        int nestingUp = 0;
        
        //Hedges buffer
        Stack hedgesBuffer = new Stack();
        
        //Labels stuff
        String thisRuleLabel = null;
        String changeRuleLabel = null;
        
        //Process a rule
        while (true) {
            tokens.nextToken();
    
            
            if(tokens.ttype == StreamTokenizer.TT_EOL || tokens.ttype == StreamTokenizer.TT_EOF) {
                break;
            }
            
            //Reset control word
            controlWord = UNDEFINED;
            
            //Read next token
            currentToken = tokens.sval;
            if (tokens.ttype == '(')
                currentToken = "(";
            else if (tokens.ttype == ')')
                currentToken = ")";

            
            //Add to the output string
            if (out != null) {
                out.append(currentToken);
                out.append(' ');
            }
            
            //Check control words hastable
            Object temp = controlHash.get(currentToken);
            if(temp!=null)
                controlWord=((Integer)temp).intValue();
            else {
                //Check LV hashtable
                temp = engine.lvHash.get(currentToken);
                if(temp!=null) {
                    controlWord=LV;
                    lVariable = (LinguisticVariable)temp;
                }
                else {
                    //Check hedges
                    temp = engine.hedgesHash.get(currentToken);
                    if(temp!=null) {
                        controlWord=HEDGE;
                        hVariable = (Hedge)temp;
                    }
                }
            }
            
            //Switch according to the state/subState
            if(controlWord!=NOP) {
                switch(engineState) {
                    case LABEL:
                        switch(engineSubState) {
                            //LABEL:READY --------------------------------------------------------------------------------------------------
                            case READY:
                                switch(controlWord) {
                                    case LV:
                                    case HEDGE:
                                        throw new RulesParsingException("- A label cannot be the same as an LV or a hedge: "+out);
                                    case IF:
                                        engineState = EVALUATION;
                                        break;
                                    case SET:
                                        accumulatedResult = 1.0;
                                        engineState = EXECUTION;
                                        break;
                                    case UNDEFINED:
                                        thisRuleLabel = currentToken;
                                        engineSubState = STORE_LABEL;
                                        
                                        //Block parsing mode
                                        if(rule!=null)
                                            rule.setName(currentToken);
                                        
                                        break;
                                    default:
                                        throw new RulesParsingException(" - A rule should start with a label of an 'if': "+out);
                                }
                                break;
                                //LABEL:STORE_LABEL --------------------------------------------------------------------------------------------
                            case STORE_LABEL:
                                switch(controlWord) {
                                    case IF:
                                        //Store label in the label's hash and create a copy of WEIGHT LV for that label
                                        if(!engine.containsLabel(thisRuleLabel)) {
                                            //retrive WEIGHT LV
                                            LinguisticVariable tempLV = (LinguisticVariable)engine.lvHash.get("weight");
                                            if(tempLV==null)
                                                throw new RulesParsingException(" - WEIGHT LV is not registered but required for LABELS: "+out);
                                            
                                            tempLV = tempLV.copy(thisRuleLabel);
                                            engine.addLabel(thisRuleLabel,tempLV);
                                        }
                                        engineState = EVALUATION;
                                        engineSubState = READY;
                                        break;
                                    case SET:
                                        //Store label in the label's hash and create a copy of WEIGHT LV for that label
                                        if(!engine.containsLabel(thisRuleLabel)) {
                                            //retrive WEIGHT LV
                                            LinguisticVariable tempLV = (LinguisticVariable)engine.lvHash.get("weight");
                                            if(tempLV==null)	
                                                throw new RulesParsingException(" - WEIGHT LV is not registered but required for LABELS: "+out);
                                            
                                            tempLV = tempLV.copy(thisRuleLabel);
                                            engine.addLabel(thisRuleLabel,tempLV);
                                        }
                                        accumulatedResult = 1.0;
                                        engineState = EXECUTION;
                                        engineSubState = READY;
                                        break;
                                    default:
                                        throw new RulesParsingException(" - Incorrect LABEL: "+out);
                                }
                                break;
                        }
                        break;
                    case EVALUATION:
                        switch(engineSubState) {
                            //EVALUATION:READY ---------------------------------------------------------------------------------------------
                            case READY:
                                switch(controlWord) {
                                    case LV:
                                        engineSubState = LV_READ;
                                        break;
                                    case AND:
                                        if(!flagAND && !flagOR)
                                            flagAND = true;
                                        else
                                            throw new RulesParsingException(" - Incorrect AND/OR operation"+out);
                                        break;
                                    case OR:
                                        if(!flagAND && !flagOR)
                                            flagOR = true;
                                        else
                                            throw new RulesParsingException(" - Incorrect AND/OR operation"+out);
                                        break;
                                    case LEFTP:
                                        //Nesting
                                        //Block parsing mode
                                        if(rule!=null)	nestingUp++;
                                        //Rule evaluation mode
                                        else {
                                            nestingStack.push(new StackElement(accumulatedResult,flagAND,flagOR));
                                            accumulatedResult = 0.0;
                                            flagAND = false;
                                            flagOR = false;
                                        }
                                        break;
                                    case RIGHTP:
                                        //Block parsing mode
                                        if(rule!=null) {
                                            List list = rule.getLeftPartExpressions();
                                            ((FuzzyExpression)list.get(list.size()-1)).nestingDown++;
                                        }
                                        //Rule evaluation mode
                                        else {
                                            //Check for hanging AND/OR operations
                                            if(flagAND || flagOR)
                                                throw new RulesParsingException(" - Unmatched AND/OR operation: "+out);
                                            
                                            //Return from nesting
                                            StackElement tempSE = (StackElement) nestingStack.pop();
                                            flagAND = tempSE.flagAND;
                                            flagOR = tempSE.flagOR;
                                            
                                            //Add to display string
                                            appendValue(out, accumulatedResult);
                                            
                                            //A hedge cannot appear at this place
                                            //If both flagAND and flahOR are not set, keep the accumulatedResult
                                            if(flagAND) {
                                                flagAND = false;
                                                accumulatedResult = accumulatedResult > tempSE.accumulatedResult ?
                                                tempSE.accumulatedResult : accumulatedResult;
                                            }
                                            if(flagOR) {
                                                flagOR = false;
                                                accumulatedResult = accumulatedResult < tempSE.accumulatedResult ?
                                                tempSE.accumulatedResult : accumulatedResult;
                                            }
                                        }
                                        break;
                                    case THEN:
                                        engineState = EXECUTION;
                                        break;
                                    default:
                                        throw new RulesParsingException(" - Incorrect operation: "+out);
                                }
                                break;
                                //EVALUATION:LV_READ -------------------------------------------------------------------------------------------
                            case LV_READ:
                                //the next item should be 'is' - everything else is an exception
                                switch(controlWord) {
                                    case IS:
                                        engineSubState = IS_READ;
                                        break;
                                    default:
                                        throw new RulesParsingException(" - only IS may be present at this place: "+out);
                                }
                                break;
                                //EVALUATION:IS_READ -------------------------------------------------------------------------------------------
                            case IS_READ:
                                //the next item may be a hedge or a membership function
                                switch(controlWord) {
                                    case HEDGE:
                                        hedgesBuffer.push(hVariable);
                                        engineSubState = HEDGE_READ;
                                        break;
                                    case UNDEFINED:
                                        engineSubState = COMPLETE_EVALUATION;
                                        mVariable = currentToken;
                                        break;
                                    default:
                                        throw new RulesParsingException(" - Incorrect operation after IS: "+out);
                                        
                                }
                                break;
                                //EVALUATION:HEDGE_READ ----------------------------------------------------------------------------------------
                            case HEDGE_READ:
                                //the next item can only be a membership function or another hedge
                                switch(controlWord) {
                                    case UNDEFINED:
                                        engineSubState = COMPLETE_EVALUATION;
                                        mVariable = currentToken;
                                        break;
                                    case HEDGE:
                                        hedgesBuffer.push(hVariable);
                                        engineSubState = HEDGE_READ;
                                        break;
                                    default:
                                        throw new RulesParsingException(" - Incorrect operation after HEDGE: "+out);
                                }
                                break;
                                //EVALUATION:EXCEPTION -----------------------------------------------------------------------------------------
                            default:
                                throw new RulesParsingException(" - Error in EVALUATION state: "+out);
                        }
                        //EVALUATION:COMPLETE_EVALUATION -------------------------------------------------------------------------------
                        if(engineSubState==COMPLETE_EVALUATION) {
                            //Block parsing mode
                            if(rule!=null) {
                                //Store hedges if needed
                                ArrayList hVector = new ArrayList();
                                while(!hedgesBuffer.empty())
                                    hVector.add((Hedge)hedgesBuffer.pop());
                                
                                FuzzyExpression tempExpression = Factory.getFactory().makeFuzzyExpression(lVariable,mVariable,hVector,out.toString());
                                //Store flags
                                tempExpression.flagAND=flagAND;
                                tempExpression.flagOR=flagOR;
                                flagOR=flagAND=false;
                                tempExpression.nestingUp = nestingUp;
                                nestingUp=0;
                                
                                //Reset text represenation
                                if (out != null)
                                    out.setLength(0);
                                //Store the expression
                                rule.getLeftPartExpressions().add(tempExpression);
                                
                            }
                            else
                                //Rule evaluation mode
                            {
                                double tempResult = 0.0;
                                
                                //Complete evaluation
                                tempResult = lVariable.is(mVariable);
                                lVariable = null;
                                
                                if(tempResult == -1)
                                    throw new RulesParsingException(" - Unable to perform fuzzy evaluation: "+out);
                                
                                //Apply hedge if needed
                                while(!hedgesBuffer.empty())
                                    tempResult = ((Hedge)hedgesBuffer.pop()).hedgeIt(tempResult);
                                
                                //Check if doing AND or OR ---> !(AND||OR) = STORE
                                if(!flagAND && !flagOR) {
                                    accumulatedResult = tempResult;
                                }
                                else {
                                    if(flagAND && flagOR)
                                        throw new RulesParsingException(" - Incorrect AND/OR operation: "+out);
                                    if(flagAND) {
                                        flagAND = false;
                                        accumulatedResult = accumulatedResult > tempResult ? tempResult : accumulatedResult;
                                    }
                                    if(flagOR) {
                                        flagOR = false;
                                        accumulatedResult = accumulatedResult < tempResult ? tempResult : accumulatedResult;
                                    }
                                }
                                //Add to display string
                                appendValue(out, tempResult);           

                            }//end of mode switch
                            
                            //Switch subState
                            engineSubState = READY;
                        }
                        break;
                    case EXECUTION:
                        switch(engineSubState) {
                            
                            //EXECUTION: READY --------------------------------------------------------------------------------------------
                            case READY:
                                switch(controlWord) {
                                    case LV:
                                        engineSubState = LV_READ;
                                        break;
                                        //Nothing to do if an 'and' is found
                                    case AND:
                                        break;
                                        //Change a rule's weight
                                    case RULE:
                                        engineSubState = RULE_READ;
                                        break;
                                        //Everything else generates exception
                                    default:
                                        throw new RulesParsingException(" - Incorrect operation after THEN: "+out);
                                }
                                break;
                                //EXECUTION: RULE_READ ----------------------------------------------------------------------------------------
                            case RULE_READ:
                                //The next item should be a label
                                switch(controlWord) {
                                    case UNDEFINED:
                                        changeRuleLabel = currentToken;
                                        engineSubState = LABEL_READ;
                                        break;
                                    default:
                                        throw new RulesParsingException(" - A LABEL should follow RULE: "+out);
                                }
                                break;
                                //EXECUTION: LABEL_READ ---------------------------------------------------------------------------------------
                            case LABEL_READ:
                                //The next item should be WEIGHT
                                switch(controlWord) {
                                    case WEIGHT:
                                        engineSubState = LV_READ;
                                        break;
                                    default:
                                        throw new RulesParsingException(" - An error after RULE LABEL (was 'weight' LV registered?): "+out);
                                }
                                break;
                                //EXECUTION: LV_READ ------------------------------------------------------------------------------------------
                            case LV_READ:
                                //The next item must be IS - anything else generates an exception
                                switch(controlWord) {
                                    case IS:
                                        engineSubState = IS_READ;
                                        break;
                                    default:
                                        throw new RulesParsingException(" - Only IS can be present at this place: "+out);
                                }
                                break;
                                //EXECUTION: IS_READ ------------------------------------------------------------------------------------------
                            case IS_READ:
                                //The next item can be a HEDGE or a membership function
                                switch(controlWord) {
                                    case HEDGE:
                                        hedgesBuffer.push(hVariable);
                                        engineSubState = HEDGE_READ;
                                        break;
                                    case UNDEFINED:
                                        mVariable = currentToken;
                                        engineSubState = COMPLETE_EXECUTION;
                                        break;
                                    default:
                                        throw new RulesParsingException(" - Incorrect sequence after IS: "+out);
                                }
                                break;
                                //EXECUTION: HEDGE_READ ----------------------------------------------------------------------------------------
                            case HEDGE_READ:
                                //The next item can be a membership function or another hedge
                                switch(controlWord) {
                                    case HEDGE:
                                        hedgesBuffer.push(hVariable);
                                        engineSubState = HEDGE_READ;
                                        break;
                                    case UNDEFINED:
                                        mVariable = currentToken;
                                        engineSubState = COMPLETE_EXECUTION;
                                        break;
                                    default:
                                        throw new RulesParsingException(" - An error in EXECUTION stage: "+out);
                                        
                                }
                                break;
                        }//end of switch (EXECUTION)
                        
                        //EXECUTION: COMPLETE_EXECUTION -------------------------------------------------------------------------------
                        if(engineSubState ==COMPLETE_EXECUTION) {
                            
                            //Block parsing mode
                            if(rule!=null) {
                                //Store hedges if needed
                                ArrayList hVector = new ArrayList();
                                while(!hedgesBuffer.empty())
                                    hVector.add((Hedge)hedgesBuffer.pop());
                                
                                //Check if this is a weight change operation
                                if(changeRuleLabel!=null) {
                                    //Ensure that thisRuleLabel is not equal to changeRuleLabel
                                    if(thisRuleLabel!=null && changeRuleLabel!=null && thisRuleLabel.equals(changeRuleLabel))
                                        throw new RulesParsingException(" - A LABEL cannot be assigned to a RULE that changes that label's WEIGHT: "+out);
                                    //Fetch the label WEIGHT LV from the hash
                                    lVariable = engine.getLabel(changeRuleLabel);
                                    if(lVariable==null)
                                        throw new RulesParsingException(" - Unable to change WEIGHT for LABEL "+changeRuleLabel+" the LABEL hasn't yet been encountered: "+out);
                                }
                                
                                FuzzyExpression tempExpression = Factory.getFactory().makeFuzzyExpression(lVariable,mVariable,hVector,out.toString());
                                //Reset text represenation
                                if (out != null)
                                    out.setLength(0);
                                //Store the expression
                                rule.getRightPartExpressions().add(tempExpression);
                            }
                            else
                                //Rule evaluation mode
                            {
                                //A temporary variable to store results of hedging / negating
                                double tempResult = accumulatedResult;
                                
                                
                                
                                //If something fired
                                if(accumulatedResult > 0.0) {
                                    //If this is a weight change operation
                                    if(changeRuleLabel!=null) {
                                        //Ensure that thisRuleLabel is not equal to changeRuleLabel
                                        if(thisRuleLabel!=null && changeRuleLabel!=null && thisRuleLabel.equals(changeRuleLabel))
                                            throw new RulesParsingException(" - A LABEL cannot be assigned to a RULE that changes that label's WEIGHT: "+out);
                                        //Fetch the label WEIGHT LV from the hash
                                        lVariable = engine.getLabel(changeRuleLabel);
                                        if(lVariable==null)
                                            throw new RulesParsingException(" - Unable to change WEIGHT for LABEL "+changeRuleLabel+" the LABEL hasn't yet been encountered: "+out);
                                    }
                                    
                                    //Apply hedge if needed
                                    while(!hedgesBuffer.empty())
                                        tempResult = ((Hedge)hedgesBuffer.pop()).hedgeIt(tempResult);
                                    
                                    //Store result
                                    lVariable.set(thisRuleLabel, currentToken, tempResult);
                                    
                                    //Set fired flag
                                    engine.flagRuleFired = true;
                                    
                                }
                                
                                //Add to display string
                                appendValue(out, tempResult);

                            }//end of mode switching
                            
                            //Switch engineSubState
                            engineSubState = READY;
                            
                        }
                        
                        break;
                } //end if switch(engineState)
            } //end of if(controlWord!=NOP)
            
        }

        if (out == null)
            return "";
        return out.toString();
    }    

    /**
     * Utility method to echo the value to the buffer
     * @param out
     * @param accumulatedResult
     */
    protected void appendValue(final StringBuffer out, final double value) {
        if (out == null)
            return;
        String s = String.valueOf(value);
        out.append("(");
        out.append(s.substring(0,s.length() > 3 ? 4 : 3));
        out.append(") ");
        
    }
}
