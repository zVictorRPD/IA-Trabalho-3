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
 * Implements defuzzification via centroid
 *
 * @author  Nazario Irizarry
 */
final public class Centroid implements Defuzzifier{
    

    public Centroid() {
    }
    
    final public double defuzzify(double min, double step, double[] f) {
        double numerator = 0.0;
        double denominator = 0.0;
        
        double x = min;
        for(int i=0; i<f.length; i++) {
            //double x = (min+step*i);
            numerator +=   x * f[i];
            denominator += f[i];
            x += step;
        }

        return numerator/denominator;
        
    }
    
}
