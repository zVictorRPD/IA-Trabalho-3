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
 * This can be started as either an applet or a main.
 * It was modified from Edward's original code to make it easier to 
 * understand what was going on.
 *
 * @version 2000 Original, v0.1, Edward S. Sazonov (esazonov@usa.com)
 * @version 12/19/2003 Modifications, v0.2,  Nazario Irizarry (naz-irizarry@excite.com)
 */
import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;

import java.beans.Beans;
import java.net.URL;

public class LoadSway extends Applet  {
    private static LoadSwayPanel loadSwayPanel;
    
    public String getAppletInfo() {
        return "LoadSway\n\nInsert the type's description here.\nCreation date: (04/17/00 %r)\n@author: \n";
    }
    
    
    private static void handleException(Throwable throwable) {
        System.err.println("--------- UNCAUGHT EXCEPTION ---------");
        throwable.printStackTrace(System.out);
    }
    
    /**
     * This init is called if we are launched as an applet
     */
    public void init() {
        try {
            setLayout(new BorderLayout());
            loadSwayPanel = new LoadSwayPanel();
            add("Center", loadSwayPanel);
            loadSwayPanel.init();
            repaint();
            
        } catch (Throwable throwable) {
            handleException(throwable);
        }
    }
    
    /**
     * main is called if we are started from the command line
     */
    public static void main(String[] strings) {
        try {
            Frame frame = new Frame();
            frame.setSize(600, 382);
            loadSwayPanel = new LoadSwayPanel();
            frame.add("Center", loadSwayPanel);
            frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent windowevent) {
                    System.exit(0);
                }
            });
            frame.setVisible(true);
            loadSwayPanel.init();
            loadSwayPanel.repaint();
        } catch (Throwable throwable) {
            handleException(throwable);
        }
    }
    
    
    public void update(Graphics graphics) {
        paint(graphics);
    }
    
}
