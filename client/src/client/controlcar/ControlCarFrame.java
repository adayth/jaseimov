/*
 * Copyright (C) 2010 Aday Talavera Hierro <aday.talavera@gmail.com>
 *
 * This file is part of JASEIMOV.
 *
 * JASEIMOV is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JASEIMOV is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JASEIMOV.  If not, see <http://www.gnu.org/licenses/>.
 */
package client.controlcar;

import client.Command;
import client.servercomm.ConnectException;
import device.ServoControl;
import device.MotorControl;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A JFrame to control a MotorControl and a ServoControl device.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class ControlCarFrame extends JFrame implements ActionListener
{
    /**
     * Model for a MotorControl. Manages velocity and acceleration.
     */
    class MotorModel
    {
        MotorControl motor;

        int velocity;
        int minVelocity;
        int maxVelocity;
        

        int acceleration;
        int minAcceleration;
        int maxAcceleration;

        void changeMotor(MotorControl m)
        {
            motor = m;

            // Initialize velocity and aceleration values
            try
            {
                velocity = (int)motor.getVelocity();
                minVelocity = (int)motor.getMinVelocity();
                maxVelocity = (int)motor.getMaxVelocity();
                acceleration = (int)motor.getAcceleration();
                minAcceleration = (int)motor.getMinAcceleration();
                maxAcceleration = (int)motor.getMaxAcceleration();
            }
            // Catch DeviceException / RemoteException
            catch(Exception ex)
            {
                ex.printStackTrace();
            }                      

            // Initialize velocity controls limits and values
            velocitySlider.getModel().setRangeProperties(velocity, 0, minVelocity, maxVelocity, false);
            velocitySlider.addChangeListener(velocityListener);
            velocitySpinner.setModel(new SpinnerNumberModel(velocity, minVelocity, maxVelocity,1));
            velocitySpinner.addChangeListener(velocityListener);
            visualPanel.setVelocity(velocity);
            visualPanel.setVelocityListener(velocityListener);
            
            // Initialize aceleration controls limits and values
            acelerationSlider.getModel().setRangeProperties(acceleration, 0, minAcceleration, maxAcceleration, false);
            acelerationSlider.addChangeListener(accelerationListener);
            acelerationSpinner.setModel(new SpinnerNumberModel(acceleration, minAcceleration, maxAcceleration,1));
            acelerationSpinner.addChangeListener(accelerationListener);
        }        
        
        void setVelocity(int v)
        {            
            if(velocity != v)
            {
                // Update Model
                velocity = v;
                
                // Update Views
                velocitySlider.setValue(velocity);
                velocitySpinner.setValue(velocity);
                visualPanel.setVelocity(velocity);

                // Execute command on motor
                setVelocity.execute();
            }
        }        
        
        void setAcceleration(int a)
        {
            if( acceleration != a)
            {
                // Update Model
                acceleration = a;

                // Update Views
                acelerationSlider.setValue(acceleration);
                acelerationSpinner.setValue(acceleration);

                // Execute command on motor
                setAcceleration.execute();
            }
        }
    }

    /**
     * Velocity controller of MotorModel.
     */
    private class ChangeVelocityListener implements ChangeListener
    {
        public void stateChanged(ChangeEvent e)
        {
            int velocity;
            Object source = e.getSource();
            if(source.equals(velocitySlider))
            {
                velocity = velocitySlider.getValue();
            }
            else if(source.equals(velocitySpinner))
            {
                velocity = (Integer)velocitySpinner.getValue();
            }
            else if(source.equals(visualPanel))
            {
                velocity = visualPanel.getVelocity();
            }
            else
            {
                return;
            }

            motorModel.setVelocity(velocity);
        }
    }

    /**
     * Acceleration controller of MotorModel.
     */
    private class ChangeAccelerationListener implements ChangeListener
    {
        public void stateChanged(ChangeEvent e)
        {
            int acceleration;
            Object source = e.getSource();
            if(source.equals(acelerationSlider))
            {
                acceleration = acelerationSlider.getValue();
            }
            else if(source.equals(acelerationSpinner))
            {
                acceleration = (Integer)acelerationSpinner.getValue();
            }
            else
            {
                return;
            }

            motorModel.setAcceleration(acceleration);
        }
    }

    MotorModel motorModel;
    ChangeListener velocityListener = new ChangeVelocityListener();
    ChangeListener accelerationListener = new ChangeAccelerationListener();

    /**
     * Model for a ServoControl device. Manages position.
     */
    class ServoModel
    {
        ServoControl servo;

        int position;
        int minPosition;
        int maxPosition;
        int startPosition;        

        void changeServo(ServoControl s)
        {
            servo = s;

            // Get servo parameters
            try
            {
                position = (int)servo.getPosition();
                minPosition = (int)servo.getMinPosition();
                maxPosition = (int)servo.getMaxPosition();
                startPosition = (int)servo.getStartPosition();
            }
            // Catch DeviceException / RemoteException
            catch(Exception ex)
            {
                ex.printStackTrace();
            }

            // Update views
            positionSlider.getModel().setRangeProperties(position, 0, minPosition, maxPosition, false);
            positionSlider.addChangeListener(positionListener);
            positionSpinner.setModel(new SpinnerNumberModel(position, minPosition, maxPosition,1));
            positionSpinner.addChangeListener(positionListener);
            visualPanel.setPosition(position);
            visualPanel.setPositionListener(positionListener);
        }        

        public void setPosition(int p)
        {
            if(position != p)
            {
                // Update Model
                position = p;

                // Update Views
                positionSlider.setValue(position);
                positionSpinner.setValue(position);
                visualPanel.setPosition(position);
                
                // Execute command on servo
                setPosition.execute();
            }
        }
    }

    /**
     * Position controller of ServoModel.
     */
    private class ChangePositionListener implements ChangeListener
    {
        public void stateChanged(ChangeEvent e)
        {
            int position;
            Object source = e.getSource();
            if(source.equals(positionSlider))
            {
                position = positionSlider.getValue();
            }
            else if(source.equals(positionSpinner))
            {
                position = (Integer)positionSpinner.getValue();
            }
            else if(source.equals(visualPanel))
            {
                position = visualPanel.getPosition();
            }
            else
                return;

            servoModel.setPosition(position);
        }
    }
    
    ServoModel servoModel;
    ChangeListener positionListener = new ChangePositionListener();

    

    // ControlFrame commands
    Command setVelocity;
    Command setAcceleration;
    Command setPosition;
    Command stopVehicle;

    // Panel to control the vehicle moving the mouse
    VisualControlPanel visualPanel;

    /**
     * Creates a ControlCarFrame.
     * @param motor MotorControl to be controlled.
     * @param servo ServoControl to be controlled.
     * @throws ConnectException
     */
    public ControlCarFrame(MotorControl motor, ServoControl servo) throws ConnectException
    {
        initComponents();                

        // Init Motor/Servo Models
        motorModel = new MotorModel();
        servoModel = new ServoModel();

        // Create VisualControlPanel
        visualPanel = new VisualControlPanel(motorModel, servoModel);
        lastPanel.add(visualPanel, BorderLayout.CENTER);

        motorModel.changeMotor(motor);
        servoModel.changeServo(servo);

        // Init commands: set velocity, aceleration, position and stop vehicle
        setVelocity = new SetVelocityCommand(motorModel);

        setAcceleration = new SetAccelerationCommand(motorModel);

        setPosition = new SetPositionCommand(servoModel);

        stopVehicle = new StopVehicleCommand(motorModel);
        stopButton.setActionCommand(stopVehicle.getName());
        stopButton.addActionListener(this);

        pack();
    }

    public void actionPerformed(ActionEvent e)
    {
        String command = e.getActionCommand();

        // Stop vehicle command
        if(command.equals(stopVehicle.getName()))
        {
                stopVehicle.execute();
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JPanel controlPanel = new javax.swing.JPanel();
        javax.swing.JPanel motorPanel = new javax.swing.JPanel();
        javax.swing.JPanel acelerationPanel = new javax.swing.JPanel();
        javax.swing.JLabel acelerationLabel = new javax.swing.JLabel();
        acelerationSlider = new javax.swing.JSlider();
        acelerationSpinner = new javax.swing.JSpinner();
        javax.swing.JPanel velocityPanel = new javax.swing.JPanel();
        javax.swing.JLabel velocityLabel = new javax.swing.JLabel();
        velocitySlider = new javax.swing.JSlider();
        velocitySpinner = new javax.swing.JSpinner();
        javax.swing.JPanel servoPanel = new javax.swing.JPanel();
        javax.swing.JPanel positionPanel = new javax.swing.JPanel();
        positionSlider = new javax.swing.JSlider();
        positionSpinner = new javax.swing.JSpinner();
        javax.swing.JLabel positionLabel = new javax.swing.JLabel();
        lastPanel = new javax.swing.JPanel();
        javax.swing.JLabel forwardLabel = new javax.swing.JLabel();
        javax.swing.JLabel rightLabel = new javax.swing.JLabel();
        javax.swing.JLabel leftLabel = new javax.swing.JLabel();
        javax.swing.JLabel backwardLabel = new javax.swing.JLabel();
        javax.swing.JPanel centerPanel = new javax.swing.JPanel();
        stopButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("client/client"); // NOI18N
        setTitle(bundle.getString("CONTROL CAR")); // NOI18N
        setResizable(false);

        controlPanel.setLayout(new javax.swing.BoxLayout(controlPanel, javax.swing.BoxLayout.Y_AXIS));

        motorPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Motor"));
        motorPanel.setLayout(new javax.swing.BoxLayout(motorPanel, javax.swing.BoxLayout.Y_AXIS));

        acelerationLabel.setText(bundle.getString("ACELERATION")); // NOI18N

        acelerationSlider.setMajorTickSpacing(20);
        acelerationSlider.setMinorTickSpacing(10);
        acelerationSlider.setPaintLabels(true);
        acelerationSlider.setPaintTicks(true);

        javax.swing.GroupLayout acelerationPanelLayout = new javax.swing.GroupLayout(acelerationPanel);
        acelerationPanel.setLayout(acelerationPanelLayout);
        acelerationPanelLayout.setHorizontalGroup(
            acelerationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(acelerationPanelLayout.createSequentialGroup()
                .addGroup(acelerationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(acelerationLabel)
                    .addGroup(acelerationPanelLayout.createSequentialGroup()
                        .addComponent(acelerationSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(acelerationSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        acelerationPanelLayout.setVerticalGroup(
            acelerationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(acelerationPanelLayout.createSequentialGroup()
                .addComponent(acelerationLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(acelerationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(acelerationSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(acelerationSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        motorPanel.add(acelerationPanel);

        velocityLabel.setText(bundle.getString("VELOCITY")); // NOI18N

        velocitySlider.setMajorTickSpacing(50);
        velocitySlider.setMinimum(-100);
        velocitySlider.setMinorTickSpacing(25);
        velocitySlider.setPaintLabels(true);
        velocitySlider.setPaintTicks(true);
        velocitySlider.setPaintTrack(false);

        javax.swing.GroupLayout velocityPanelLayout = new javax.swing.GroupLayout(velocityPanel);
        velocityPanel.setLayout(velocityPanelLayout);
        velocityPanelLayout.setHorizontalGroup(
            velocityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(velocityPanelLayout.createSequentialGroup()
                .addGroup(velocityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(velocityLabel)
                    .addGroup(velocityPanelLayout.createSequentialGroup()
                        .addComponent(velocitySlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(velocitySpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        velocityPanelLayout.setVerticalGroup(
            velocityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(velocityPanelLayout.createSequentialGroup()
                .addComponent(velocityLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(velocityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(velocitySpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(velocitySlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        motorPanel.add(velocityPanel);

        controlPanel.add(motorPanel);

        servoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Servo"));

        positionSlider.setMajorTickSpacing(20);
        positionSlider.setMinorTickSpacing(5);
        positionSlider.setPaintLabels(true);
        positionSlider.setPaintTicks(true);
        positionSlider.setPaintTrack(false);

        positionLabel.setText(bundle.getString("POSITION")); // NOI18N

        javax.swing.GroupLayout positionPanelLayout = new javax.swing.GroupLayout(positionPanel);
        positionPanel.setLayout(positionPanelLayout);
        positionPanelLayout.setHorizontalGroup(
            positionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(positionPanelLayout.createSequentialGroup()
                .addGroup(positionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(positionPanelLayout.createSequentialGroup()
                        .addComponent(positionSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(positionSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(positionLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        positionPanelLayout.setVerticalGroup(
            positionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, positionPanelLayout.createSequentialGroup()
                .addComponent(positionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(positionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(positionSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(positionSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout servoPanelLayout = new javax.swing.GroupLayout(servoPanel);
        servoPanel.setLayout(servoPanelLayout);
        servoPanelLayout.setHorizontalGroup(
            servoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(positionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        servoPanelLayout.setVerticalGroup(
            servoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(positionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        controlPanel.add(servoPanel);

        getContentPane().add(controlPanel, java.awt.BorderLayout.LINE_START);

        lastPanel.setLayout(new java.awt.BorderLayout());

        forwardLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        forwardLabel.setText(bundle.getString("FORWARD")); // NOI18N
        lastPanel.add(forwardLabel, java.awt.BorderLayout.PAGE_START);

        rightLabel.setText(bundle.getString("RIGHT")); // NOI18N
        lastPanel.add(rightLabel, java.awt.BorderLayout.LINE_END);

        leftLabel.setText(bundle.getString("LEFT")); // NOI18N
        lastPanel.add(leftLabel, java.awt.BorderLayout.LINE_START);

        backwardLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        backwardLabel.setText(bundle.getString("BACKWARD")); // NOI18N
        lastPanel.add(backwardLabel, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(lastPanel, java.awt.BorderLayout.LINE_END);

        stopButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/client/images/stop.png"))); // NOI18N
        stopButton.setToolTipText(bundle.getString("STOP VEHICLE TOOLTIP")); // NOI18N
        stopButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        stopButton.setFocusable(false);
        stopButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        stopButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        centerPanel.add(stopButton);

        getContentPane().add(centerPanel, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JSlider acelerationSlider;
    javax.swing.JSpinner acelerationSpinner;
    javax.swing.JPanel lastPanel;
    javax.swing.JSlider positionSlider;
    javax.swing.JSpinner positionSpinner;
    javax.swing.JButton stopButton;
    javax.swing.JSlider velocitySlider;
    javax.swing.JSpinner velocitySpinner;
    // End of variables declaration//GEN-END:variables
  
}
