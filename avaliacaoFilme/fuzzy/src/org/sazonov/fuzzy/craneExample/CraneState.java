
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
 * The state of the crane
 *
 * @version 2000 Original, v0.1, Edward S. Sazonov (esazonov@usa.com)
 * @version 12/19/2003 Modifications, v0.2,  Nazario Irizarry (naz-irizarry@excite.com)
 */

class CraneState {
    public double distanceToTarget = 0.0;
    public double currentTheta = 0.0;
    
    public CraneState(double theta, double distance) {
        currentTheta = theta;
        distanceToTarget = distance;
    }
}
