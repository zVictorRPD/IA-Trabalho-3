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

/**
 * Constants used by the engine and parser.
 * This code was originally bundled as part of the FuzzyEngine class.
 *
 * @version 2000 Original, v0.1, Edward S. Sazonov (esazonov@usa.com)
 * @version 12/19/2003 Modifications, v0.2,  Nazario Irizarry (naz-irizarry@excite.com)
 */
interface Constants {
    //Action constants
    public static final int IF = 1;
    public static final int THEN = 2;
    public static final int IS = 3;
    public static final int SET = 4;
    public static final int AND = 5;
    public static final int OR = 6;
    public static final int LEFTP = 7;
    public static final int RIGHTP = 8;
    public static final int NOP = 9;
    public static final int EXECUTE = 10;
    public static final int HEDGE = 11;
    public static final int RULE = 12;
    public static final int UNDEFINED = 14;
    public static final int WEIGHT = 15;
    public static final int LV = 16;
    
    //Common subStates of the engine
    public static final int READY = 100;
    public static final int LV_READ = 101;
    public static final int IS_READ = 102;
    public static final int NOT_READ = 103;
    public static final int HEDGE_READ = 104;
    public static final int EXCEPTION = 105;
    
    //subStates of LABEL state
    public static final int STORE_LABEL = 200;
    
    //subStates of EVALUATION state
    public static final int COMPLETE_EVALUATION = 300;
    
    //subStates of EXECUTION state
    public static final int RULE_READ = 400;
    public static final int LABEL_READ = 401;
    public static final int COMPLETE_EXECUTION = 402;
    
    //Fuzzy engine states
    public static final int LABEL = 500;
    public static final int EVALUATION = 501;
    public static final int EXECUTION = 502;
    
}
