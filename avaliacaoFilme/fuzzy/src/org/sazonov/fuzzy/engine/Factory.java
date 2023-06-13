
//Copyright (C) 2000  Nazario Irizarry

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
import org.sazonov.fuzzy.engine.defuzzify.*;
/**
 * This class was added to Ed's original code to facilitate extending the
 * package with new or changed funcitonality.
 *
 * @version 12/19/2003 Original, v0.2,  Nazario Irizarry (naz-irizarry@excite.com)
 */
public class Factory {
    
    static protected Factory singleton;
    Hedge hedgeNot;
    Hedge hedgeSomewhat;
    Hedge hedgeVery;
    Defuzzifier defuzzifier;
    
    static public Factory getFactory() {
        if (singleton == null)
            singleton = new Factory();
        return singleton;
    }
    
    /**
     * If you extend the package with subclasses of any objects then set the 
     * singleton to your own version of the factory.
     */
    protected Factory() {
//        defuzzifier = new Centroid();
        defuzzifier = new AreaBisection2();
    }
    
    public void setDefuzzifier(Defuzzifier defuzzifier) {
        this.defuzzifier = defuzzifier;
    }
    
    public LinguisticVariable makeLinguisticVariable(String name) {
        return new LinguisticVariable(name, defuzzifier);
    }
    
    public FuzzyState makeFuzzyState() {
        return new FuzzyState();
    }
    
    protected RuleBlock makeRuleBlock(FuzzyState engine) {
        return new RuleBlock(engine);
    }

    protected RuleParser makeRuleParser() {
        return new RuleParser();
    }
    
    protected FuzzyRule makeFuzzyRule() {
        return new FuzzyRule();
    }
 
    protected FuzzyRule makeFuzzyRule(String label) {
        return new FuzzyRule(label);
    }
    
    public MemberFunction makeMemberFunction(String name_in, double start, double left_top, double right_top, double finish) {
        return new TrapezoidalMemberFunction(name_in, start, left_top, right_top, finish);
    }

    protected Hedge makeHedgeNot() {
        // hedges have no state so we only need one instance
        if (hedgeNot == null)
            hedgeNot = new HedgeNot();
        return hedgeNot;
    }

    protected Hedge makeHedgeVery() {
        // hedges have no state so we only need one instance
        if (hedgeVery == null)
            hedgeVery = new HedgeVery();
        return hedgeVery;
    }
    
    protected Hedge makeHedgeSomewhat() {
        // hedges have no state so we only need one instance
        if (hedgeSomewhat == null)
            hedgeSomewhat = new HedgeSomewhat();
        return hedgeSomewhat;
    }

    protected FuzzyExpression makeFuzzyExpression(LinguisticVariable lVariable, String mVariable, ArrayList hVector, String textRepresentation) {
        return new FuzzyExpression(lVariable,  mVariable,  hVector,  textRepresentation);
    }

}
