
//Copyright (C) 2000  Edward S. Sazonov (esazonov@usa.com)
// Modifications 12/19/2003 by Nazario Irizarry (naz-irizarry@excite.com)

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

package org.sazonov.fuzzy.craneExample;
/**
 * Implements the fuzzy controller.  Defines 3 linguistic variables.
 * Angle and distance are input variables. Power is the output variable.
 * Power is used to drive velocity in the example.
 *
 * @version 2000 Original, v0.1, Edward S. Sazonov (esazonov@usa.com)
 * @version 12/19/2003 Modifications, v0.2,  Nazario Irizarry (naz-irizarry@excite.com)
 */

import java.awt.Color;

import org.sazonov.fuzzy.engine.RuleBlock;
import org.sazonov.fuzzy.engine.FuzzyState;
import org.sazonov.fuzzy.engine.LinguisticVariable;
import org.sazonov.fuzzy.engine.Factory;

class FuzzyController extends Thread {
    private LoadSwayPanel loadSwayPanel = null;
    private FuzzyState fuzzyState = null;
    private RuleBlock fuzzyRules;
    private LinguisticVariable angle = null;
    private LinguisticVariable power = null;
    private LinguisticVariable distance = null;
    private boolean running;
    
    public FuzzyController(LoadSwayPanel loadsway) {
        running = true;
        loadSwayPanel = loadsway;
        
        Factory factory = Factory.getFactory();

        fuzzyState = factory.makeFuzzyState();
        angle = factory.makeLinguisticVariable("angle");
        angle.add(factory.makeMemberFunction("negativeLarge", -4.0, -4.0, -0.314, -0.2616));
        angle.add(factory.makeMemberFunction("negativeSmall", -0.314, -0.2616, -0.0628, 0.0));
        angle.add(factory.makeMemberFunction("negative", -4.0, -4.0, -0.0628, 0.0));
        angle.add(factory.makeMemberFunction("zero", -0.0628, 0.0, 0.0, 0.0628));
        angle.add(factory.makeMemberFunction("positive", 0.0, 0.0628, 4.0, 4.0));
        angle.add(factory.makeMemberFunction("positiveSmall", 0.0, 0.0628, 0.2616, 0.314));
        angle.add(factory.makeMemberFunction("positiveLarge", 0.2616, 0.314, 4.0, 4.0));
        distance = factory.makeLinguisticVariable("distance");
        distance.add(factory.makeMemberFunction("positiveLarge", 75.0, 100.0, 400.0, 400.0));
        distance.add(factory.makeMemberFunction("positiveMedium", 35.0, 50.0, 75.0, 100.0));
        distance.add(factory.makeMemberFunction("positiveSmall", 0.0, 2.0, 35.0, 50.0));
        distance.add(factory.makeMemberFunction("zero", -2.0, 0.0, 0.0, 2.0));
        distance.add(factory.makeMemberFunction("negativeSmall", -50.0, -35.0, -2.0, 0.0));
        distance.add(factory.makeMemberFunction("negativeMedium", -100.0, -75.0, -50.0, -35.0));
        distance.add(factory.makeMemberFunction("negativeLarge", -400.0, -400.0, -100.0, -75.0));
        power = factory.makeLinguisticVariable("power");
        power.add(factory.makeMemberFunction("negativeLarge", -16.0, -15.0, -15.0, -14.0));
        power.add(factory.makeMemberFunction("negativeMedium", -6.0, -5.0, -5.0, -4.0));
        power.add(factory.makeMemberFunction("negativeSmall", -2.0, -1.0, -1.0, 0.0));
        power.add(factory.makeMemberFunction("zero", -1.0, 0.0, 0.0, 1.0));
        power.add(factory.makeMemberFunction("positiveSmall", 0.0, 1.0, 1.0, 2.0));
        power.add(factory.makeMemberFunction("positiveMedium", 4.0, 5.0, 5.0, 6.0));
        power.add(factory.makeMemberFunction("positiveLarge", 14.0, 15.0, 15.0, 16.0));

        fuzzyState.register(angle);
        fuzzyState.register(distance);
        fuzzyState.register(power);


        try {
            loadSwayPanel.getFuzzyControlButton().setForeground(Color.green);
            String ruleText = loadsway.getFuzzyRules().getText();
            fuzzyRules = fuzzyState.createRuleExecutionSet(new java.io.StringReader(ruleText));
        } catch (Exception exception) {
            loadSwayPanel.getFuzzyControlButton().setForeground(Color.red);
            handleException(exception);
        }
        start();
    }

    private void handleException(Throwable throwable) {
        System.out.println("--------- UNCAUGHT EXCEPTION in FuzzyController ---------");
        throwable.printStackTrace(System.out);
    }
    
    public void run() {
        while (running) {
            CraneState cranestate = loadSwayPanel.getState();
            fuzzyState.reset();
            angle.setInputValue(cranestate.currentTheta);
            distance.setInputValue(cranestate.distanceToTarget);
            try {
                fuzzyRules.executeRules();
            } catch (Exception exception) {
                loadSwayPanel.getFuzzyControlButton().setForeground(Color.red);
            }
            try {
                loadSwayPanel.setTrolleyVelocity(power.defuzzify());
            } catch (Exception exception) {
                System.out.println("Exception: " + exception.getMessage());
            }
            double d = loadSwayPanel.getPendulum().getRopeLength();
            if (d < 80.0)
                loadSwayPanel.getPendulum().setRopeLength(d + 2.0);
            
//            loadSwayPanel.getImageCanvas().repaint();
            try {
                Thread.sleep(200L);
            } catch (InterruptedException interruptedexception) {
                /* empty */
            }
        }
    }
    
    void abort() {
        running = false;
    }
}
