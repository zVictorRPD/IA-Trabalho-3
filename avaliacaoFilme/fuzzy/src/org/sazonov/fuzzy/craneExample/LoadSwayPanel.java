//Example of using using the fuzzy engine for Java 0.1a
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
 * This panel contains the Trolley model.
 * This panel can be embedded in a Frame or an Applet.
 *
 * @version 2000 Original, Edward S. Sazonov (esazonov@usa.com)
 * @version 12/19/2003 Modifications,  Nazario Irizarry (naz-irizarry@excite.com)
 */
import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;

import java.beans.Beans;
import java.net.URL;

final class LoadSwayPanel extends Panel implements KeyListener {
    private Trolley trolley;
    private FuzzyController fuzzyControllerThread;
    private double trolleyPosition = 270;
    private double magnetX = 270;
    private double magnetY = 0;
    private Pendulum pendulum = null;
    private double trolleyVelocity = 0.0;
    private Image craneBackground = null;
    private Image paintBuffer = null;
    private Image paintBuffer1 = null;
    private Image paintBuffer2 = null;
    private int loadPosition = 12;
    private int firstTargetPosition = 12;
    private int initialCranePosition = 270;
    private int targetPosition = 12;
    private int secondTargetPosition = 285;
    private ImageCanvas ivjImageCanvas = null;
    private Button ivjFuzzyControlButton = null;
    private Button ivjManualControlButton = null;
    private IvjEventHandler ivjEventHandler;
    private boolean appletStopped = true;
    private boolean appletManual = false;
    private boolean appletFuzzy = false;
    private Button ivjStopButton = null;
    private double currentTheta;
    private TextArea ivjFuzzyRules = null;
    private boolean loadLocked = false;
    private boolean stopTheCrane = false;
    private Label ivjAngle = null;
    private TextField ivjAngleOutput = null;
    private Label ivjDistance = null;
    private TextField ivjDistanceOutput = null;
    private Label ivjLoadLock = null;
    private Label ivjPower = null;
    private TextField ivjPowerOutput = null;
    private Label ivjRopeLength = null;
    private TextField ivjRopeLengthOutput = null;
    private TextField ivjLoadLockOutput = null;
    private Choice ivjFinalLoadPosition = null;
    private Choice ivjInitialCranePosition = null;
    private Choice ivjInitialLoadPosition = null;
    private Label ivjLabel1 = null;
    private Label ivjLabel11 = null;
    private Label ivjLabel12 = null;
    private Component refComponent = null;
    
    class IvjEventHandler implements ActionListener, ItemListener {
        
        public void actionPerformed(ActionEvent actionevent) {
            if (actionevent.getSource() == getManualControlButton()) {
                connEtoC1();
            }
            else if (actionevent.getSource() == getFuzzyControlButton()) {
                connEtoC2();
            }
            else if (actionevent.getSource() == getStopButton())
                connEtoC3();
        }
        
        public void itemStateChanged(ItemEvent itemevent) {
            if (itemevent.getSource() == getInitialCranePosition()) {
                connEtoC4(itemevent);
            }
            else if (itemevent.getSource() == getInitialLoadPosition()) {
                connEtoC5(itemevent);
            }
            else if (itemevent.getSource() == getFinalLoadPosition())
                connEtoC6(itemevent);
        }
    }
    
    public void activateFuzzyControl() {
        stopControl();
        appletFuzzy = true;
        fuzzyControllerThread = new FuzzyController(this);
        startTrolley();
    }
    
    public void activateManualControl() {
        stopControl();
        appletStopped = false;
        appletManual = true;
        getManualControlButton().setForeground(Color.green);
        getImageCanvas().requestFocus();
        startTrolley();
    }
    
    private void connEtoC1() {
        try {
            activateManualControl();
        } catch (Throwable throwable) {
            handleException(throwable);
        }
    }
    
    private void connEtoC2() {
        try {
            activateFuzzyControl();
        } catch (Throwable throwable) {
            handleException(throwable);
        }
    }
    
    private void connEtoC3() {
        try {
            stopControl();
        } catch (Throwable throwable) {
            handleException(throwable);
        }
    }
    
    private void connEtoC4(ItemEvent itemevent) {
        try {
            initialCranePositionChanged();
        } catch (Throwable throwable) {
            handleException(throwable);
        }
    }
    
    private void connEtoC5(ItemEvent itemevent) {
        try {
            initialLoadPositionChanged();
        } catch (Throwable throwable) {
            handleException(throwable);
        }
    }
    
    private void connEtoC6(ItemEvent itemevent) {
        try {
            finalLoadPositionChanged();
        } catch (Throwable throwable) {
            handleException(throwable);
        }
    }
    
    public void finalLoadPositionChanged() {
        switch (getFinalLoadPosition().getSelectedIndex()) {
            case 0:
                secondTargetPosition = 12;
                break;
            case 1:
                secondTargetPosition = 285;
                break;
            case 2:
                secondTargetPosition = (int) (300.0 * Math.random());
                break;
        }
    }
    
    Pendulum getPendulum() {
        return pendulum;
    }
    
    private Label getAngle() {
        if (ivjAngle == null) {
            try {
                ivjAngle = new Label();
                ivjAngle.setName("Angle");
                ivjAngle.setAlignment(1);
                ivjAngle.setFont(new Font("dialog", 1, 10));
                ivjAngle.setText("Angle");
                ivjAngle.setBounds(438, 238, 54, 9);
                ivjAngle.setForeground(Color.cyan);
            } catch (Throwable throwable) {
                handleException(throwable);
            }
        }
        return ivjAngle;
    }
    
    private TextField getAngleOutput() {
        if (ivjAngleOutput == null) {
            try {
                ivjAngleOutput = new TextField();
                ivjAngleOutput.setName("AngleOutput");
                ivjAngleOutput.setText("0");
                ivjAngleOutput.setBackground(Color.darkGray);
                ivjAngleOutput.setForeground(Color.red);
                ivjAngleOutput.setFont(new Font("monospaced", 1, 10));
                ivjAngleOutput.setBounds(438, 251, 48, 28);
                ivjAngleOutput.setEditable(false);
            } catch (Throwable throwable) {
                handleException(throwable);
            }
        }
        return ivjAngleOutput;
    }
    
    public String getAppletInfo() {
        return "LoadSway\n\nInsert the type's description here.\nCreation date: (04/17/00 %r)\n@author: \n";
    }
    
    private Label getDistance() {
        if (ivjDistance == null) {
            try {
                ivjDistance = new Label();
                ivjDistance.setName("Distance");
                ivjDistance.setAlignment(1);
                ivjDistance.setFont(new Font("dialog", 1, 10));
                ivjDistance.setText("Distance");
                ivjDistance.setBounds(415, 200, 54, 9);
                ivjDistance.setForeground(Color.cyan);
            } catch (Throwable throwable) {
                handleException(throwable);
            }
        }
        return ivjDistance;
    }
    
    private TextField getDistanceOutput() {
        if (ivjDistanceOutput == null) {
            try {
                ivjDistanceOutput = new TextField();
                ivjDistanceOutput.setName("DistanceOutput");
                ivjDistanceOutput.setText("  -");
                ivjDistanceOutput.setBackground(Color.darkGray);
                ivjDistanceOutput.setForeground(Color.red);
                ivjDistanceOutput.setFont(new Font("monospaced", 1, 10));
                ivjDistanceOutput.setBounds(415, 210, 48, 28);
                ivjDistanceOutput.setEditable(false);
            } catch (Throwable throwable) {
                handleException(throwable);
            }
        }
        return ivjDistanceOutput;
    }
    
    private Choice getFinalLoadPosition() {
        if (ivjFinalLoadPosition == null) {
            try {
                ivjFinalLoadPosition = new Choice();
                ivjFinalLoadPosition.setName("FinalLoadPosition");
                ivjFinalLoadPosition.setFont(new Font("dialog", 1, 10));
                ivjFinalLoadPosition.setBackground(Color.darkGray);
                ivjFinalLoadPosition.setBounds(283, 231, 119, 28);
                ivjFinalLoadPosition.setForeground(Color.yellow);
            } catch (Throwable throwable) {
                handleException(throwable);
            }
        }
        return ivjFinalLoadPosition;
    }
    
    public Button getFuzzyControlButton() {
        if (ivjFuzzyControlButton == null) {
            try {
                ivjFuzzyControlButton = new Button();
                ivjFuzzyControlButton.setName("FuzzyControlButton");
                ivjFuzzyControlButton.setFont(new Font("dialog", 1, 10));
                ivjFuzzyControlButton.setBackground(Color.gray);
                ivjFuzzyControlButton.setBounds(354, 284, 225, 21);
                ivjFuzzyControlButton.setForeground(Color.black);
                ivjFuzzyControlButton.setLabel("Fuzzy control");
            } catch (Throwable throwable) {
                handleException(throwable);
            }
        }
        return ivjFuzzyControlButton;
    }
    
    public TextArea getFuzzyRules() {
        if (ivjFuzzyRules == null) {
            try {
                ivjFuzzyRules = new TextArea("", 0, 0, 1);
                ivjFuzzyRules.setName("FuzzyRules");
                ivjFuzzyRules.setText
                ("if distance is negativeLarge then power is negativeLarge\nif distance is positiveLarge then power is positiveLarge\nif distance is negativeLarge and (angle is negativeLarge or angle is positiveLarge) then power is negativeMedium\nif distance is positiveLarge and (angle is negativeLarge or angle is positiveLarge) then power is positiveMedium\nif distance is negativeMedium then power is negativeMedium\nif distance is positiveMedium then power is positiveMedium\nif distance is negativeMedium and (angle is negativeLarge or angle is positiveLarge) then power is negativeSmall\nif distance is positiveMedium and (angle is negativeLarge or angle is positiveLarge) then power is positiveSmall\nif distance is negativeSmall then power is negativeSmall\nif distance is positiveSmall then power is positiveSmall\nif distance is zero and angle is zero then power is zero\nif (distance is zero or distance is positiveSmall or distance is negativeSmall) and angle is negative then power is positiveSmall\nif (distance is zero or distance is positiveSmall or distance is negativeSmall) and angle is positive then power is negativeSmall\n");
                ivjFuzzyRules.setBackground(Color.darkGray);
                ivjFuzzyRules.setBounds(5, 309, 589, 65);
            } catch (Throwable throwable) {
                handleException(throwable);
            }
        }
        return ivjFuzzyRules;
    }
    
    public ImageCanvas getImageCanvas() {
        if (ivjImageCanvas == null) {
            try {
                ivjImageCanvas = new ImageCanvas();
                ivjImageCanvas.setName("ImageCanvas");
                //               ivjImageCanvas.setLayout(null);
                ivjImageCanvas.setBounds(0, 0, 600, 200);
            } catch (Throwable throwable) {
                handleException(throwable);
            }
        }
        return ivjImageCanvas;
    }
    
    private Choice getInitialCranePosition() {
        if (ivjInitialCranePosition == null) {
            try {
                ivjInitialCranePosition = new Choice();
                ivjInitialCranePosition.setName("InitialCranePosition");
                ivjInitialCranePosition.setFont(new Font("dialog", 1, 10));
                ivjInitialCranePosition.setBackground(Color.darkGray);
                ivjInitialCranePosition.setBounds(26, 231, 119, 28);
                ivjInitialCranePosition.setForeground(Color.yellow);
            } catch (Throwable throwable) {
                handleException(throwable);
            }
        }
        return ivjInitialCranePosition;
    }
    
    private Choice getInitialLoadPosition() {
        if (ivjInitialLoadPosition == null) {
            try {
                ivjInitialLoadPosition = new Choice();
                ivjInitialLoadPosition.setName("InitialLoadPosition");
                ivjInitialLoadPosition.setFont(new Font("dialog", 1, 10));
                ivjInitialLoadPosition.setBackground(Color.darkGray);
                ivjInitialLoadPosition.setBounds(156, 231, 119, 28);
                ivjInitialLoadPosition.setForeground(Color.yellow);
            } catch (Throwable throwable) {
                handleException(throwable);
            }
        }
        return ivjInitialLoadPosition;
    }
    
    private Label getLabel11() {
        if (ivjLabel11 == null) {
            try {
                ivjLabel11 = new Label();
                ivjLabel11.setName("Label11");
                ivjLabel11.setFont(new Font("dialog", 1, 10));
                ivjLabel11.setAlignment(1);
                ivjLabel11.setText("Load's position");
                ivjLabel11.setBounds(156, 205, 111, 26);
                ivjLabel11.setForeground(Color.green);
            } catch (Throwable throwable) {
                handleException(throwable);
            }
        }
        return ivjLabel11;
    }
    
    private Label getLabel12() {
        if (ivjLabel12 == null) {
            try {
                ivjLabel12 = new Label();
                ivjLabel12.setName("Label12");
                ivjLabel12.setFont(new Font("dialog", 1, 10));
                ivjLabel12.setAlignment(1);
                ivjLabel12.setText("Destination");
                ivjLabel12.setBounds(285, 205, 111, 26);
                ivjLabel12.setForeground(Color.green);
            } catch (Throwable throwable) {
                handleException(throwable);
            }
        }
        return ivjLabel12;
    }
    
    private Label getLabel1() {
        if (ivjLabel1 == null) {
            try {
                ivjLabel1 = new Label();
                ivjLabel1.setName("Label1");
                ivjLabel1.setFont(new Font("dialog", 1, 10));
                ivjLabel1.setAlignment(1);
                ivjLabel1.setText("Crane's position");
                ivjLabel1.setBounds(26, 205, 111, 26);
                ivjLabel1.setForeground(Color.green);
            } catch (Throwable throwable) {
                handleException(throwable);
            }
        }
        return ivjLabel1;
    }
    
    private Label getLoadLock() {
        if (ivjLoadLock == null) {
            try {
                ivjLoadLock = new Label();
                ivjLoadLock.setName("LoadLock");
                ivjLoadLock.setAlignment(1);
                ivjLoadLock.setFont(new Font("dialog", 1, 10));
                ivjLoadLock.setText("Load Lock");
                ivjLoadLock.setBounds(532, 200, 63, 9);
                ivjLoadLock.setForeground(Color.cyan);
            } catch (Throwable throwable) {
                handleException(throwable);
            }
        }
        return ivjLoadLock;
    }
    
    private TextField getLoadLockOutput() {
        if (ivjLoadLockOutput == null) {
            try {
                ivjLoadLockOutput = new TextField();
                ivjLoadLockOutput.setName("LoadLockOutput");
                ivjLoadLockOutput.setText("  OFF");
                ivjLoadLockOutput.setBackground(Color.darkGray);
                ivjLoadLockOutput.setForeground(Color.red);
                ivjLoadLockOutput.setFont(new Font("monospaced", 1, 10));
                ivjLoadLockOutput.setBounds(532, 210, 57, 28);
                ivjLoadLockOutput.setEditable(false);
            } catch (Throwable throwable) {
                handleException(throwable);
            }
        }
        return ivjLoadLockOutput;
    }
    
    public Button getManualControlButton() {
        if (ivjManualControlButton == null) {
            try {
                ivjManualControlButton = new Button();
                ivjManualControlButton.setName("ManualControlButton");
                ivjManualControlButton.setFont(new Font("dialog", 1, 10));
                ivjManualControlButton.setBackground(Color.gray);
                ivjManualControlButton.setBounds(23, 284, 225, 21);
                ivjManualControlButton.setForeground(Color.black);
                ivjManualControlButton.setLabel("Manual control");
            } catch (Throwable throwable) {
                handleException(throwable);
            }
        }
        return ivjManualControlButton;
    }
    
    private Label getPower() {
        if (ivjPower == null) {
            try {
                ivjPower = new Label();
                ivjPower.setName("Power");
                ivjPower.setAlignment(1);
                ivjPower.setFont(new Font("dialog", 1, 10));
                ivjPower.setText("Power");
                ivjPower.setBounds(464, 200, 70, 9);
                ivjPower.setForeground(Color.cyan);
            } catch (Throwable throwable) {
                handleException(throwable);
            }
        }
        return ivjPower;
    }
    
    private TextField getPowerOutput() {
        if (ivjPowerOutput == null) {
            try {
                ivjPowerOutput = new TextField();
                ivjPowerOutput.setName("PowerOutput");
                ivjPowerOutput.setText("  OFF  ");
                ivjPowerOutput.setBackground(Color.darkGray);
                ivjPowerOutput.setForeground(Color.red);
                ivjPowerOutput.setFont(new Font("monospaced", 1, 10));
                ivjPowerOutput.setBounds(465, 210, 64, 28);
                ivjPowerOutput.setEditable(false);
            } catch (Throwable throwable) {
                handleException(throwable);
            }
        }
        return ivjPowerOutput;
    }
    
    private Label getRopeLength() {
        if (ivjRopeLength == null) {
            try {
                ivjRopeLength = new Label();
                ivjRopeLength.setName("RopeLength");
                ivjRopeLength.setAlignment(1);
                ivjRopeLength.setFont(new Font("dialog", 1, 10));
                ivjRopeLength.setText("Rope Length");
                ivjRopeLength.setBounds(490, 238, 83, 9);
                ivjRopeLength.setForeground(Color.cyan);
            } catch (Throwable throwable) {
                handleException(throwable);
            }
        }
        return ivjRopeLength;
    }
    
    private TextField getRopeLengthOutput() {
        if (ivjRopeLengthOutput == null) {
            try {
                ivjRopeLengthOutput = new TextField();
                ivjRopeLengthOutput.setName("RopeLengthOutput");
                ivjRopeLengthOutput.setText("20");
                ivjRopeLengthOutput.setBackground(Color.darkGray);
                ivjRopeLengthOutput.setForeground(Color.red);
                ivjRopeLengthOutput.setFont(new Font("monospaced", 1, 10));
                ivjRopeLengthOutput.setBounds(490, 251, 77, 28);
                ivjRopeLengthOutput.setEditable(false);
            } catch (Throwable throwable) {
                handleException(throwable);
            }
        }
        return ivjRopeLengthOutput;
    }
    
    public CraneState getState() {
        return new CraneState(currentTheta,
        (double) (targetPosition - trolleyPosition));
    }
    
    private Button getStopButton() {
        if (ivjStopButton == null) {
            try {
                ivjStopButton = new Button();
                ivjStopButton.setName("StopButton");
                ivjStopButton.setFont(new Font("dialog", 1, 10));
                ivjStopButton.setBackground(Color.gray);
                ivjStopButton.setBounds(270, 284, 62, 21);
                ivjStopButton.setForeground(Color.black);
                ivjStopButton.setLabel("Stop");
            } catch (Throwable throwable) {
                handleException(throwable);
            }
        }
        return ivjStopButton;
    }
    
    private void handleException(Throwable throwable) {
        System.out.println("--------- UNCAUGHT EXCEPTION in LoadSwayPanel ---------");
        throwable.printStackTrace(System.out);
    }
    
    private void initConnections() throws Exception {
        getManualControlButton().addActionListener(ivjEventHandler);
        getFuzzyControlButton().addActionListener(ivjEventHandler);
        getStopButton().addActionListener(ivjEventHandler);
        getInitialCranePosition().addItemListener(ivjEventHandler);
        getInitialLoadPosition().addItemListener(ivjEventHandler);
        getFinalLoadPosition().addItemListener(ivjEventHandler);
    }
    
    public void initialCranePositionChanged() {
        switch (getInitialCranePosition().getSelectedIndex()) {
            case 0:
                initialCranePosition = 12;
                trolleyPosition = 12;
                magnetX = 12;
                break;
            case 1:
                initialCranePosition = 285;
                trolleyPosition = 285;
                magnetX = 285;
                break;
            case 2:
                trolleyPosition = (int) (300.0 * Math.random());
                magnetX = trolleyPosition;
                initialCranePosition = (int) trolleyPosition;
                break;
        }
    }
    
    public void initialLoadPositionChanged() {
        switch (getInitialLoadPosition().getSelectedIndex()) {
            case 0:
                loadPosition = 12;
                firstTargetPosition = 12;
                targetPosition = 12;
                break;
            case 1:
                loadPosition = 285;
                firstTargetPosition = 285;
                targetPosition = 285;
                break;
            case 2:
                loadPosition = (int) (300.0 * Math.random());
                firstTargetPosition = loadPosition;
                targetPosition = loadPosition;
                break;
        }
    }
    
    /**
     * Be sure to init() after the parent container is showing so that call to
     * createImage() works.
     */
    public void init() {
        
        try {
            ivjEventHandler = new IvjEventHandler();
            setName("LoadSway");
            setLayout(null);
            setForeground(Color.white);
            add(getImageCanvas(), getImageCanvas().getName());
            add(getManualControlButton(), getManualControlButton().getName());
            add(getFuzzyControlButton(), getFuzzyControlButton().getName());
            add(getStopButton(), getStopButton().getName());
            add(getFuzzyRules(), getFuzzyRules().getName());
            add(getDistance(), getDistance().getName());
            add(getDistanceOutput(), getDistanceOutput().getName());
            add(getAngle(), getAngle().getName());
            add(getAngleOutput(), getAngleOutput().getName());
            add(getRopeLength(), getRopeLength().getName());
            add(getRopeLengthOutput(), getRopeLengthOutput().getName());
            add(getPower(), getPower().getName());
            add(getPowerOutput(), getPowerOutput().getName());
            add(getLoadLock(), getLoadLock().getName());
            add(getLoadLockOutput(), getLoadLockOutput().getName());
            add(getInitialCranePosition(),getInitialCranePosition().getName());
            add(getInitialLoadPosition(),getInitialLoadPosition().getName());
            add(getFinalLoadPosition(), getFinalLoadPosition().getName());
            add(getLabel1(), getLabel1().getName());
            add(getLabel11(), getLabel11().getName());
            add(getLabel12(), getLabel12().getName());
            initConnections();
            pendulum = new Pendulum(trolleyPosition);
            getImageCanvas().addKeyListener(this);
            
            
            /*
             * set up for double buffering
             * createImage()  only works if the container is showing
             */
            paintBuffer1 = createImage(600, 200);
            paintBuffer2 = createImage(600, 200);
            paintBuffer = paintBuffer1;
            
            
            /*
             * Load our background image
             */
            craneBackground = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("org/sazonov/fuzzy/craneExample/dock.gif"));
            
            
            getInitialCranePosition().add("Ship");
            getInitialCranePosition().add("Ground platform");
            getInitialCranePosition().add("Random");
            getInitialLoadPosition().add("Ship");
            getInitialLoadPosition().add("Ground platform");
            getInitialLoadPosition().add("Random");
            getFinalLoadPosition().add("Ship");
            getFinalLoadPosition().add("Ground platform");
            getFinalLoadPosition().add("Random");
            
            /*
             * Wait for our background to load
             */
            MediaTracker imageTracker = new MediaTracker(this);
            imageTracker.addImage(craneBackground, 0);
            imageTracker.waitForID(0);
            
            getImageCanvas().preparePaint();
            getImageCanvas().repaint();
            
        } catch (Throwable throwable) {
            handleException(throwable);
        }
    }
    
    public void keyPressed(KeyEvent keyevent) {
        if (appletManual) {
            int i = keyevent.getKeyCode();
            double d = 0.0;
            switch (i) {
                
                // left arrow key imparts force on the trolley to the left
                case KeyEvent.VK_LEFT:
                    if (trolleyVelocity > -15.0)
                        trolleyVelocity--;
                    break;
                    
                // right arrow key imparts force on the trolley to the right
                case KeyEvent.VK_RIGHT:
                    if (trolleyVelocity < 15.0)
                        trolleyVelocity++;
                    break;
                    
                // space key brings trolley to dead stop
                case KeyEvent.VK_SPACE:
                    trolleyVelocity = 0.0;
                    break;
                
                // up arrow key raises the crane a little
                case KeyEvent.VK_UP:
                    d = pendulum.getRopeLength();
                    if (d - 5.0 > 0.0)
                        pendulum.setRopeLength(d - 5.0);
                    break;
                    
                // down arrow key drops the crane a little
                case KeyEvent.VK_DOWN:
                    d = pendulum.getRopeLength();
                    if (d + 5.0 < 90.0)
                        pendulum.setRopeLength(d + 5.0);
                    break;
                    
                // enter key toggles the "magnet" on the crane
                case KeyEvent.VK_ENTER:
                    if (magnetX > loadPosition - 5 && magnetX < loadPosition + 5
                    && magnetY > 78)
                        loadLocked = !loadLocked;
                    break;
            }
        }
    }
    
    public void keyReleased(KeyEvent keyevent) {
        /* empty */
    }
    
    public void keyTyped(KeyEvent keyevent) {
        /* empty */
    }
    
    
    class ImageCanvas extends Canvas {
        private static final boolean DOUBLE_BUFFER = true;
        
        public void update(Graphics g) {
            paint(g);
        }
        
        public void paint(Graphics graphics) {
            if (craneBackground != null ) {
                if (DOUBLE_BUFFER) {
                    if ( paintBuffer != null) {
                        graphics.drawImage(paintBuffer, 0, 0, null /*this*/);
                    }
                }
                else {
                    paintToGraphics(graphics);
                }
            }
        }
        
        public void preparePaint() {
            if (DOUBLE_BUFFER) {
                if (craneBackground != null && paintBuffer != null) {
                    Graphics g = paintBuffer1.getGraphics();

                    // draw to buffer not being used

                    if (paintBuffer == paintBuffer1) {
                        g = paintBuffer2.getGraphics();
                    }
                    else {
                        g = paintBuffer1.getGraphics();
                    }

                    paintToGraphics(g);

                    // swap active buffer
                    if (paintBuffer == paintBuffer1) {
                        paintBuffer = paintBuffer2;
                    }
                    else {
                        paintBuffer = paintBuffer1;
                    }
                }
            }
            
        }
        
        private void paintToGraphics(Graphics g) {
            g.drawImage(craneBackground, 0, 0, /*this*/ null);
            g.setColor(Color.yellow);
            int trolleyPositionInt = (int) trolleyPosition;
            g.fillRect(trolleyPositionInt + 125, 29, 10, 5);
            currentTheta = pendulum.getNextTheta(trolleyPosition, 1.0);
            magnetX = (int) ((double) trolleyPosition - pendulum.getRopeLength() * Math.sin(currentTheta));
            magnetY = (int) (pendulum.getRopeLength() * Math.cos(currentTheta));
            int magnetXInt = (int) magnetX;
            int magnetYInt = (int) magnetY;
            g.drawLine(trolleyPositionInt + 130, 34, magnetXInt + 130, magnetYInt + 34);
            g.fillRect(magnetXInt + 125, magnetYInt + 34, 10, 5);
            g.fillRect(magnetXInt + 120, magnetYInt + 39, 20, 5);
            g.setColor(Color.lightGray);
            if (loadLocked) {
                loadPosition = magnetXInt;
                g.fillRect(loadPosition + 120, magnetYInt + 44, 20, 20);
                g.setColor(Color.black);
                g.drawRect(loadPosition + 120, magnetYInt + 44, 20, 20);
                g.drawRect(loadPosition + 123, magnetYInt + 47, 14,
                14);
            } else {
                int i = 128;
                if (loadPosition > 30 && loadPosition < 270)
                    i = 137;
                g.fillRect(loadPosition + 120, i, 20, 20);
                g.setColor(Color.black);
                g.drawRect(loadPosition + 120, i, 20, 20);
                g.drawRect(loadPosition + 123, i + 3, 14, 14);
            }
            int i = 0;
            if (secondTargetPosition > 30 && secondTargetPosition < 270)
                i = 10;
            g.setColor(Color.black);
            g.drawLine(secondTargetPosition + 131, 138 + i,
            secondTargetPosition + 131, 148 + i);
            g.setColor(Color.red);
            g.fillRect(secondTargetPosition + 132, 138 + i, 10, 6);        }
    }
    
    public void setTrolleyVelocity(double d) {
        trolleyVelocity = d;
    }
    
    
    public void stopControl() {
        if (appletManual) {
            appletManual = false;
            getManualControlButton().setForeground(Color.black);
        }
        else if (appletFuzzy) {
            appletFuzzy = false;
            if (fuzzyControllerThread != null) {
                fuzzyControllerThread.abort();
                fuzzyControllerThread = null;
            }
            getFuzzyControlButton().setForeground(Color.black);
        }
        appletStopped = true;
        loadPosition = firstTargetPosition;
        loadLocked = false;
        targetPosition = firstTargetPosition;
        magnetX = initialCranePosition;
        magnetY = 0;
        trolleyPosition = initialCranePosition;
        trolleyVelocity = 0.0;
        pendulum = new Pendulum(trolleyPosition);
        stopTheCrane = false;
    }
    
    public void startTrolley() {
        if (trolley == null) {
            trolley = new Trolley();
            trolley.start();
        }
    }
    public void stopTrolley() {
        if (trolley != null) {
            trolley.abort();
            trolley = null;
        }
    }
    
    class Trolley extends Thread {
        private boolean running = true;
        
        public void run() {
            //        this.getGraphics();
            
            while (running) {
                trolleyPosition += trolleyVelocity;
                if (trolleyPosition < 0) {
                    trolleyPosition = 0;
                    trolleyVelocity = 0.0;
                }
                if (trolleyPosition > 300) {
                    trolleyPosition = 300;
                    trolleyVelocity = 0.0;
                }
                if (magnetX > targetPosition - 3 && magnetX < targetPosition + 3
                && pendulum.getRopeLength() >= 80.0
                && Math.abs(pendulum.getAngularVelocity()) < 0.01
                && !stopTheCrane && appletFuzzy) {
                    loadLocked = !loadLocked;
                    targetPosition = secondTargetPosition;
                    if (!loadLocked) {
                        fuzzyControllerThread.abort();
                        trolleyVelocity = 0.0;
                        stopTheCrane = true;
                    }
                }
                pendulum.setMass(loadLocked ? 5.0 : 1.0);
                getDistanceOutput().setText(String.valueOf(targetPosition - trolleyPosition));
                getRopeLengthOutput().setText(String.valueOf(pendulum.getRopeLength()));
                if (loadLocked)
                    getLoadLockOutput().setText("  ON");
                else
                    getLoadLockOutput().setText("  OFF");
                if (trolleyVelocity == 0.0)
                    getPowerOutput().setText("  OFF");
                else
                    getPowerOutput().setText(String.valueOf(trolleyVelocity));
                String string = new String(String.valueOf(currentTheta * 180.0 / 6.28));
                string = string.substring(0,
                string.length() >= 6 ? 6 : string.length());
                getAngleOutput().setText(string);
                getImageCanvas().preparePaint();
                getImageCanvas().repaint();
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException interruptedexception) {
                    /* empty */
                }
            }
        }
        
        void abort() {
            running = false;
        }
    }
}
