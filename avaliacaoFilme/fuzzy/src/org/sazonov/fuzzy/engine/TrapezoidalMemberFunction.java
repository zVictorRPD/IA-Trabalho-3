
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

/**
 * A trapezoidal fuzzy  function.
 *
 * @version 2/21/2004 Nazario Irizarry
 */
public class TrapezoidalMemberFunction extends MemberFunction {

    protected double leftTop;
    protected double rightTop;
    protected double dLeft, dRight;


    /**
     * @param name of the function
     * @param left is the lower bound
     * @param left_top is the x coordinate of the top left
     * @param right_top is the x coordinate of the top right
     * @param right is the upper bound
     */
    protected TrapezoidalMemberFunction(String name, double left, double left_top, double right_top, double right) {
        super(name, left, right);
        if (left_top < left)
            throw new IllegalArgumentException(" left bound ("+left+") must be less than left-top bound ("+left_top+")");
        if (right_top < left_top)
            throw new IllegalArgumentException(" left-top bound ("+left_top+") must be less than right-top bound ("+right_top+")");
        if (right < right_top)
            throw new IllegalArgumentException(" right-top bound ("+right_top+") must be less than right bound ("+right+")");
  
        leftTop = left_top;
        rightTop = right_top;
        
        dLeft = leftTop - left;
        dRight = right - rightTop;
    }
    /**
     * Fuzzify a value.
     * @param X Input value.
     * @return Result of fuzzification.
     */
    public double fuzzify(double X) {
 
        if(X < left)	
            return 0.0;
                
        if(X < leftTop)	
            return	(X-left)/(dLeft);
        
        if(X < rightTop)	
            return 1;
        
        if(X < right)	
            return (right-X)/(dRight);
        
        return 0;
    }
        
    /**
     * Compute the center of mass for this member function.
     */
    public double computeCenterOfMass() {
        double s = 0.5*(leftTop - left + right - rightTop);
        s += rightTop - leftTop;
        return s;
    }
    /**
     * Compute the area under the function.
     */
    public double computeArea() {
        double s = 0.5*(leftTop - left + right - rightTop);
        s += rightTop - leftTop;
        return s;
    }
    
}
