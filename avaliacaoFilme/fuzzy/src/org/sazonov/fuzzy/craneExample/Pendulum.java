
// Copyright (C) 2000  Edward S. Sazonov (esazonov@usa.com)
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
 * This is the model for the pendulum.
 *
 * @version 2000 Original, v0.1, Edward S. Sazonov (esazonov@usa.com)
 * @version 12/19/2003 Modifications, v0.2,  Nazario Irizarry (naz-irizarry@excite.com)
 */

final class Pendulum {
    private double angularVelocity = 0.0;
    private double xVelocity = 0.0;
    private double xPosition = 0.0;
    private double previousTheta = 0.0;
    private double ropeLength = 20.0;
    private double mass = 1.0;
    private double damping = 0.1;
    
    public double getAngularVelocity() {
        return angularVelocity;
    }
    
    public double getDamping() {
        return damping;
    }
    
    public double getMass() {
        return mass;
    }
    
    public double getNextTheta(double d, double d_0_) {
        double d_1_ = d - xPosition;
        xPosition = d;
        double d_2_ = d_1_ / d_0_ - xVelocity;
        xVelocity = d_1_ / d_0_;
        double d_3_ = d_2_ / d_0_;
        double d_4_ = d_3_ * Math.cos(previousTheta) / (mass * ropeLength);
        double d_5_ = 9.8 / ropeLength * Math.sin(-previousTheta);
        double d_6_ = d_4_ + d_5_ - damping * angularVelocity;
        angularVelocity += d_6_ * d_0_;
        previousTheta += angularVelocity * d_0_;
        if (previousTheta > 1.57)
            previousTheta = 1.57;
        if (previousTheta < -1.57)
            previousTheta = -1.57;
        return previousTheta;
    }
    
    public double getRopeLength() {
        return ropeLength;
    }
    
    public Pendulum(double xPos) {
        xPosition =  xPos;
    }
    
    public void setDamping(double damping) {
        this.damping = damping;
    }
    
    public void setMass(double mass) {
        this.mass = mass;
    }
    
    public void setRopeLength(double ropeLength) {
        this.ropeLength = ropeLength;
    }
}
