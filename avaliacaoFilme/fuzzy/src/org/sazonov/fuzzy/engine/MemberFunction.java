
//Copyright (C) 2004, Nazario Irizarry

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
import java.util.ArrayList;

/**
 * Base class for a fuzzy membership function.
 *
 * @version 2/21/2004  Nazario Irizarry
 */
public abstract class MemberFunction {
    protected String name;
    protected double left;
    protected double right;
    

    protected int numberOfWeights;
    protected double combinedWeight;
    
    /**
     * Constructor
     */
    protected MemberFunction(String name, double left, double right) {
        if (right < left)
            throw new IllegalArgumentException(" lower bound ("+left+") must be less than upper bound ("+right+")");
        this.left = left;
        this.right = right;
        this.name = name;
    }
    
    /**
     * Fuzzify a value.
     * @param X Input value.
     * @return Result of fuzzification.
     */
    abstract public double fuzzify(double X) ;
    
    /**
     * Return name of this membership function.
     * @return java.lang.String
     */
    final public String getName() {
        return name;
    }
    
    /**
     * Compute the center of mass for this member function.
     */
    abstract public double computeCenterOfMass() ;

    /**
     * @return the minimum of this function
     */
    final double getMin() {
        return left;
    }

    /**
     * @return the minimum of this function
     */
    final double getMax() {
        return right;
    }
     
    /**
     * clear the list of inferenced weights
     */
    protected void clearInferenceWeights() {
        numberOfWeights = 0;
        combinedWeight = 0;
    }
    
    /*
     * add a weight for future defuzzification
     */
    protected void addInferenceWeight(double weight) {
        combinedWeight += weight;
        numberOfWeights++;
    }
    
    /**
     * @return the number of weights
     */
    protected boolean hasWeight() {
        return numberOfWeights > 0;
    }
    
    /**
     * Combine all the weights that resulted from various rule firings by
     * taking the sum.
     *
     * @return the array of weights
     */
    protected double getCombinedWeights() {
        return combinedWeight/numberOfWeights;
    }    
    
    public String toString() {
        StringBuffer b = new StringBuffer(20);
        b.append("MF ");
        b.append(getName());
        if (hasWeight()) {
            b.append(" nWeights,weight = ");
            b.append(Integer.toString(numberOfWeights));
            b.append(",");
            b.append(Double.toString(getCombinedWeights()));
        }
        //return "MF "+getName()+inferencedWeights;
        return b.toString();
    }
}
