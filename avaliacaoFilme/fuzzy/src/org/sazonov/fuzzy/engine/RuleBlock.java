
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
import java.io.Reader;
import java.io.StreamTokenizer;

/**
 * Class for a block of fuzzy rules
 * @author Edward Sazonov
 *
 * @version 2000 Original, v0.1, Edward S. Sazonov (esazonov@usa.com)
 * @version 12/19/2003 Modifications, v0.2,  Nazario Irizarry (naz-irizarry@excite.com)
 */
public class RuleBlock {
        
    //List of all rules within block
    protected ArrayList allRules;

    
    //Flag indicating a rule has fired
    protected boolean aRuleFired;
    
    // name is for informational use
    protected String name="FuzzyBlockOfRules";
    
    // description is for informational use
    protected String description="";
    
    private FuzzyState context;
    
    /**
     * Constructor
     */
    protected RuleBlock(FuzzyState context) { 
        this.context = context;
    }

    public FuzzyState getFuzzyState() {
        return context;
    }
    
    /**
     * Evaluates this block.  
     * 
     */
    public void executeRules() throws EvaluationException {
        executeRules(null);
    }
    
    /**
     * Evaluates this block.  If the parameter is not null
     * it is used to write evaluation restuls for every expression in the rule.
     * 
     * @param out is the writer to write a "list" of rule fireings, it can be null
     */
    public void executeRules(PrintWriter out) throws EvaluationException {
        
        //Reset the flag
        aRuleFired = false;
                
        //Perform evaluation and execution of every rule
        for (Iterator en = allRules.iterator() ; en.hasNext() ;) {
            FuzzyRule tempRule = (FuzzyRule)en.next();
            
            try {
                tempRule.evaluateRule(out);
                if(tempRule.isRuleFired()) {
                    aRuleFired = true;
                }
            }
            catch(Exception e) {
                //Add exception handling
                System.out.println("Exception: "+e.getMessage());
                throw new EvaluationException(e.getMessage());
            }
            
        }
        if (out != null) {
            out.flush();
        }
        
    }

    /**
     * Returns true if any rule in the block has fired during a call to evaluateBlock()
     * or evaluateBlockText().
     * @return boolean
     */
    public boolean hasRuleFired() {
        return aRuleFired;
    }
            
    /**
     * Add a rule to the list
     */
    protected void addRule(FuzzyRule rule) {
        if (allRules == null)
            allRules = new ArrayList(10);
        allRules.add(rule);
    }
    
    /**
     * Clear all the rules
     */
    protected void clearRules() {
        if (allRules == null)
            return;
        allRules.clear();
    }
    
    /**
     * This method is a start (albeit trivial) to move towards JSR94
     *
     * @return a List of the rules in this block
     */
    public List getRules() {
        if (allRules == null)
            allRules = new ArrayList(10);
        return allRules;
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
    
    /**
     * Set its name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Set description
     */
    public void setDescription(String description) {
        this.description = description;
    }

}
