//Copyright (C) 2004, Nazario Irizarry (naz-irizarry@excite.com)

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

package org.sazonov.fuzzy.engine.defuzzify;
import org.sazonov.fuzzy.engine.Defuzzifier;

/**
 * Implements defuzzification by determining the value which bisects the
 * area under the function. This tries to correct the quantization
 * problem of standard bisection.
 *
 * @author  Nazario Irizarry
 */
final public class AreaBisection2 implements Defuzzifier{
    
    
    public AreaBisection2() {
    }
    
    /**
     * Implements defuzzification by determining the value which bisects the
     * area under the function. This eliminates the quantization of the final
     * value by approximating a centroid function in the final step.
     */
    final public double defuzzify(double min, double step, double[] f) {
        int left= 0;
        int right= f.length-1;
        double rightArea = 0.0;
        double leftArea = 0.0;
        
        while (right > left+1) {
            if (leftArea >= rightArea) {
                rightArea += f[right--];
            }
            else {
                leftArea += f[left++];
            }
        }
        double index = (right*rightArea + left*leftArea)/(leftArea+rightArea) ;
        double x = min + index*step;
        return x;
        
    }
    
}
