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
package jaseimov.client.maingui.sensorvisors;

import jaseimov.client.ClientApp;
import jaseimov.lib.remote.list.RemoteDeviceInfo;
import jaseimov.client.dialogs.ShowTimeDialog;
import jaseimov.lib.remote.utils.SensorUpdater;
import jaseimov.client.utils.Utils;
import jaseimov.lib.devices.Device;
import jaseimov.lib.devices.DeviceType;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Observer;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

/**
 * A JInternalFrame associated to a SensorDevice that shows sensor data in it.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public abstract class SensorVisor
        extends JInternalFrame
        implements Observer, ActionListener, PropertyChangeListener
{
    final static String NUMBER_FORMAT = "%1$.4f";

    private RemoteDeviceInfo deviceInfo;
    private SensorUpdater updater;

    private boolean observing = false;

    private class IFCloseListener extends InternalFrameAdapter
    {
        @Override
        public void internalFrameClosing(InternalFrameEvent e)
        {
            if(observing)
            {
                updater.deleteObserver(SensorVisor.this);
            }
            updater.removePropertyListener(SensorVisor.this);
        }
    }

    /**
     * Creates a SensorVisor.
     * @param devInfo A {@link RemoteDeviceInfo} that contains a SensorDevice.
     */
    protected SensorVisor(RemoteDeviceInfo devInfo)
    {        
        initComponents();
        pack();

        deviceInfo = devInfo;
        updater = devInfo.getSensorUpdater();

        // Set title and border color of frame
        try
        {
            Device dev = deviceInfo.getDevice();

            this.setTitle(ClientApp.getBundleString("DEVICE ID") + " " + dev.getID()  + " " + dev.getName());

            Color color = Utils.getColorByDeviceType(dev.getDeviceType());
            this.setBorder(new javax.swing.border.LineBorder(color,2));
        }
        // Catch ConnectException / RemoteException
        catch(Exception ex)
        {
            ex.printStackTrace();
        }        

        // Get state of updater and put it in GUI        
        autoUpdateButton.setSelected(updater.getAutoUpdating());

        // GUI Listeners
        updateButton.addActionListener(this);        
        autoUpdateButton.addActionListener(this);
        setTimeButton.addActionListener(this);

        // Close listener
        addInternalFrameListener(new IFCloseListener());

        // Listen for update properties
        updater.addPropertyListener(this);

        // If no auto update is set, just call one manual update
        // This need to be executed after InternalFrame creation
        if(!updater.getAutoUpdating())
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                   updateButton.doClick();
                }
            });
        }
    }

    protected void addPanel(JPanel panel)
    {
        sensorContainer.add(panel);
        pack();
    }    

    // Manual Update command
    public void actionPerformed(ActionEvent e)
    {
        Object object = e.getSource();

        if(object.equals(updateButton))
        {
            if(!observing)
            {
                this.update(updater, updater.update());
            }
            else
            {
                updater.update();
            }
        }
        else if(object.equals(autoUpdateButton))
        {
            if(autoUpdateButton.isSelected())
            {
                if(!observing)
                {
                    updater.addObserver(this);
                    observing = true;
                }
                updater.autoUpdate(true);
            }
            else
            {
                updater.deleteObserver(this);
                observing = false;
            }
        }
        else if(object.equals(setTimeButton))
        {
            new ShowTimeDialog(this, updater).execute();
        }
    }

    public void propertyChange(PropertyChangeEvent evt)
    {
        String name = evt.getPropertyName();
        if(name.equals(SensorUpdater.AUTOUPDATE_VALUE))
        {
            Boolean value = (Boolean)evt.getNewValue();            
            if(value != autoUpdateButton.isSelected())
            {                
                autoUpdateButton.setSelected(value.booleanValue());
            }
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

        javax.swing.JToolBar jToolBar1 = new javax.swing.JToolBar();
        updateButton = new javax.swing.JButton();
        javax.swing.JToolBar.Separator jSeparator1 = new javax.swing.JToolBar.Separator();
        autoUpdateButton = new javax.swing.JToggleButton();
        javax.swing.JToolBar.Separator jSeparator2 = new javax.swing.JToolBar.Separator();
        setTimeButton = new javax.swing.JButton();
        sensorContainer = new javax.swing.JPanel();

        setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        setClosable(true);

        jToolBar1.setFloatable(false);

        updateButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jaseimov/client/images/arrow_refresh.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("jaseimov/client/client"); // NOI18N
        updateButton.setToolTipText(bundle.getString("UPDATE NOW")); // NOI18N
        updateButton.setFocusable(false);
        updateButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        updateButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(updateButton);
        jToolBar1.add(jSeparator1);

        autoUpdateButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jaseimov/client/images/control_play.png"))); // NOI18N
        autoUpdateButton.setToolTipText(bundle.getString("AUTO UPDATE")); // NOI18N
        autoUpdateButton.setFocusable(false);
        autoUpdateButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        autoUpdateButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(autoUpdateButton);
        jToolBar1.add(jSeparator2);

        setTimeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jaseimov/client/images/clock.png"))); // NOI18N
        setTimeButton.setToolTipText(bundle.getString("SET UPDATE TIME")); // NOI18N
        setTimeButton.setFocusable(false);
        setTimeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        setTimeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(setTimeButton);

        getContentPane().add(jToolBar1, java.awt.BorderLayout.PAGE_START);

        sensorContainer.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        sensorContainer.setLayout(new javax.swing.BoxLayout(sensorContainer, javax.swing.BoxLayout.Y_AXIS));
        getContentPane().add(sensorContainer, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JToggleButton autoUpdateButton;
    javax.swing.JPanel sensorContainer;
    javax.swing.JButton setTimeButton;
    javax.swing.JButton updateButton;
    // End of variables declaration//GEN-END:variables

}
