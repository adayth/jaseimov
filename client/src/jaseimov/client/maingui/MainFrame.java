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
package jaseimov.client.maingui;

import jaseimov.client.camera.ViewCamerasCommand;
import jaseimov.client.chart.MakeChartCommand;
import jaseimov.client.datacapture.CaptureDataCommand;
import jaseimov.client.maingui.dialogs.ShowConnectDialog;
import jaseimov.client.maingui.dialogs.ShowDisconnectDialog;
import jaseimov.client.ClientApp;
import jaseimov.client.utils.Command;
import jaseimov.lib.remote.list.RemoteDeviceInfo;
import jaseimov.client.dialogs.ShowTimeDialog;
import jaseimov.client.maingui.dialogs.ShowCloseDialog;
import jaseimov.client.maingui.dialogs.ShowLanguageDialog;
import jaseimov.client.maingui.sensorvisors.JGraphLayer;
import jaseimov.lib.remote.connect.ConnectException;
import jaseimov.lib.remote.utils.SensorUpdater;
import jaseimov.lib.devices.Axis;
import jaseimov.lib.devices.Device;
import jaseimov.lib.devices.SensorDevice;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLayeredPane;

/**
 * JFrame with main GUI of JASEIMOV client applicaction.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class MainFrame extends JFrame implements ActionListener
{
    // Secondary frames and dialogs
    private JGraphLayer graphLayer;

    // List of commands
    private List<Command> commandList;    

    /**
     * Creates a MainFrame.
     */
    public MainFrame()
    {
        // Init Frame
        initComponents();        
        
        // MainFrame commands
        commandList = new ArrayList<Command>();

        Command showConnect = new ShowConnectDialog(this);
        commandList.add(showConnect);
        connectButton.setActionCommand(showConnect.getName());
        connectButton.addActionListener(this);

        Command showDisconnect = new ShowDisconnectDialog(this);
        commandList.add(showDisconnect);
        disconnectButton.setActionCommand(showDisconnect.getName());
        disconnectButton.addActionListener(this);

        Command controlCar = new jaseimov.client.controlcar.ControlCarCommand();
        commandList.add(controlCar);
        controlButton.setActionCommand(controlCar.getName());
        controlButton.addActionListener(this);

        Command controlCarB = new jaseimov.client.controlcarB.ControlCarCommand();
        commandList.add(controlCarB);
        controlButtonB.setActionCommand(controlCarB.getName());
        controlButtonB.addActionListener(this);

        Command close = new ShowCloseDialog(this);
        commandList.add(close);
        closeButton.setActionCommand(close.getName());
        closeButton.addActionListener(this);

        Command viewCameras = new ViewCamerasCommand();
        commandList.add(viewCameras);
        cameraButton.setActionCommand(viewCameras.getName());
        cameraButton.addActionListener(this);

        Command captureData = new CaptureDataCommand();
        commandList.add(captureData);
        captureButton.setActionCommand(captureData.getName());
        captureButton.addActionListener(this);

        Command makeChart = new MakeChartCommand();
        commandList.add(makeChart);
        chartButton.setActionCommand(makeChart.getName());
        chartButton.addActionListener(this);

        Command showLenguage = new ShowLanguageDialog(this);
        commandList.add(showLenguage);
        lenguageButton.setActionCommand(showLenguage.getName());
        lenguageButton.addActionListener(this);

        // Set global update time need to get all updater of sensors in DeviceList
        timeButton.addActionListener(
                new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        List<SensorUpdater> updaterList = new ArrayList<SensorUpdater>();
                        for(RemoteDeviceInfo devInfo : ClientApp.getDeviceList().getRemoteDeviceInfoArray())
                        {
                            SensorUpdater updater = devInfo.getSensorUpdater();
                            if(updater != null)
                            {
                                updaterList.add(updater);
                            }                            
                        }
                        new ShowTimeDialog(MainFrame.this,updaterList.toArray(new SensorUpdater[0])).execute();
                    }
                }
                );

        // If someone close the frame with the X button we should execute
        // ShowCloseDialog for advice the user
        this.addWindowListener(
                new WindowAdapter()
                {
                    @Override
                    public void windowClosing(WindowEvent evt)
                    {                        
                        closeButton.doClick();
                    }
                }
                );

        // Init GUI controls state
        updateUI(ClientApp.isConected(), null);        
    }

    public void actionPerformed(ActionEvent e)
    {        
        String cmd = e.getActionCommand();
        for(Command c : commandList)
        {
            if(cmd.equals(c.getName()))
            {
                c.execute();
                return;
            }
        }        
    }

    /**
     * Enable or disable buttons of the UI based in connect state and devices
     * avalible in current server.
     * @param connect If true, client is connected. If false, client is disconnected.
     * @param infoArray An array of RemoteDeviceInfo with all the devices avalaible in current server.
     */
    public void updateUI(boolean connect, RemoteDeviceInfo[] infoArray)
    {        
        if(connect)
        {
            connectButton.setEnabled(false);
            disconnectButton.setEnabled(true);

            try
            {
                boolean motor = false;
                boolean servo = false;
                boolean camera = false;
                boolean sensor = false;
                boolean accelerometer = false;

                for(RemoteDeviceInfo devInfo : infoArray)
                {
                    Device dev = devInfo.getDevice();
                    if (dev instanceof SensorDevice)
                    {
                        if(dev instanceof Axis)
                        {
                          continue;
                        }
                        else
                        {
                          if(dev instanceof jaseimov.lib.devices.Camera)
                          {
                            camera = true;
                          }
                          else if(dev instanceof jaseimov.lib.devices.Accelerometer)
                          {
                            accelerometer = true;
                          }
                          else
                          {
                            sensor = true;
                          }
                          graphLayer.addDeviceCell(devInfo);
                        }                        
                    }
                    else if(dev instanceof jaseimov.lib.devices.MotorControl)
                    {
                        motor = true;
                    }
                    else if(dev instanceof jaseimov.lib.devices.ServoControl)
                    {
                        servo = true;
                    }
                }

                controlButton.setEnabled(motor && servo);
                controlButtonB.setEnabled(motor && servo && accelerometer);
                cameraButton.setEnabled(camera);
                captureButton.setEnabled(sensor);
                chartButton.setEnabled(sensor);
                timeButton.setEnabled(sensor);
            }            
            catch (ConnectException ex)
            {
                ClientApp.showErrorDialog(this, ex);
                return;
            }
        }
        else
        {
            connectButton.setEnabled(true);
            disconnectButton.setEnabled(false);

            controlButton.setEnabled(false);
            controlButtonB.setEnabled(false);
            cameraButton.setEnabled(false);
            captureButton.setEnabled(false);
            chartButton.setEnabled(false);
            timeButton.setEnabled(false);

            layeredPanel.removeAll();
            graphLayer = new JGraphLayer(layeredPanel);
            layeredPanel.add(graphLayer.getJGraph(), JLayeredPane.DEFAULT_LAYER);
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

    layeredPanel = new javax.swing.JLayeredPane();
    javax.swing.JToolBar toolBar = new javax.swing.JToolBar();
    connectButton = new javax.swing.JButton();
    disconnectButton = new javax.swing.JButton();
    closeButton = new javax.swing.JButton();
    javax.swing.JToolBar.Separator jSeparator2 = new javax.swing.JToolBar.Separator();
    controlButton = new javax.swing.JButton();
    controlButtonB = new javax.swing.JButton();
    cameraButton = new javax.swing.JButton();
    captureButton = new javax.swing.JButton();
    chartButton = new javax.swing.JButton();
    javax.swing.JToolBar.Separator jSeparator1 = new javax.swing.JToolBar.Separator();
    lenguageButton = new javax.swing.JButton();
    timeButton = new javax.swing.JButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
    java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("jaseimov/client/client"); // NOI18N
    setTitle(bundle.getString("MAIN TITLE")); // NOI18N
    setMinimumSize(new java.awt.Dimension(0, 650));
    getContentPane().add(layeredPanel, java.awt.BorderLayout.CENTER);

    toolBar.setFloatable(false);
    toolBar.setRollover(true);

    connectButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jaseimov/client/images/connect.png"))); // NOI18N
    connectButton.setText(bundle.getString("CONNECT")); // NOI18N
    connectButton.setFocusable(false);
    connectButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    connectButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(connectButton);

    disconnectButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jaseimov/client/images/disconnect.png"))); // NOI18N
    disconnectButton.setText(bundle.getString("DISCONNECT")); // NOI18N
    disconnectButton.setFocusable(false);
    disconnectButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    disconnectButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(disconnectButton);

    closeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jaseimov/client/images/close.png"))); // NOI18N
    closeButton.setText(bundle.getString("CLOSE ALL")); // NOI18N
    closeButton.setFocusable(false);
    closeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    closeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(closeButton);
    toolBar.add(jSeparator2);

    controlButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jaseimov/client/images/controller.png"))); // NOI18N
    controlButton.setText(bundle.getString("CONTROL CAR")); // NOI18N
    controlButton.setFocusable(false);
    controlButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    controlButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(controlButton);

    controlButtonB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jaseimov/client/images/arrow_refresh.png"))); // NOI18N
    controlButtonB.setText(bundle.getString("CONTROL CAR")); // NOI18N
    controlButtonB.setFocusable(false);
    controlButtonB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    controlButtonB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(controlButtonB);

    cameraButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jaseimov/client/images/webcam.png"))); // NOI18N
    cameraButton.setText(bundle.getString("VIEW CAMERA")); // NOI18N
    cameraButton.setFocusable(false);
    cameraButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    cameraButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(cameraButton);

    captureButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jaseimov/client/images/table_save.png"))); // NOI18N
    captureButton.setText(bundle.getString("CAPTURE DATA")); // NOI18N
    captureButton.setFocusable(false);
    captureButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    captureButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(captureButton);

    chartButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jaseimov/client/images/chart_curve.png"))); // NOI18N
    chartButton.setText(bundle.getString("CREATE CHART")); // NOI18N
    chartButton.setFocusable(false);
    chartButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    chartButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(chartButton);
    toolBar.add(jSeparator1);

    lenguageButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jaseimov/client/images/language.png"))); // NOI18N
    lenguageButton.setText(bundle.getString("SET LANGUAGE")); // NOI18N
    lenguageButton.setFocusable(false);
    lenguageButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    lenguageButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(lenguageButton);

    timeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jaseimov/client/images/clock.png"))); // NOI18N
    timeButton.setText(bundle.getString("SET UPDATE TIME")); // NOI18N
    timeButton.setFocusable(false);
    timeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    timeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    toolBar.add(timeButton);

    getContentPane().add(toolBar, java.awt.BorderLayout.PAGE_START);

    pack();
  }// </editor-fold>//GEN-END:initComponents


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton cameraButton;
  private javax.swing.JButton captureButton;
  private javax.swing.JButton chartButton;
  private javax.swing.JButton closeButton;
  private javax.swing.JButton connectButton;
  private javax.swing.JButton controlButton;
  private javax.swing.JButton controlButtonB;
  private javax.swing.JButton disconnectButton;
  private javax.swing.JLayeredPane layeredPanel;
  private javax.swing.JButton lenguageButton;
  private javax.swing.JButton timeButton;
  // End of variables declaration//GEN-END:variables

}
