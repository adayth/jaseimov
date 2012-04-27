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
package jaseimov.client.chart;

import jaseimov.client.ClientApp;
import jaseimov.lib.remote.list.RemoteDeviceInfo;
import jaseimov.client.dialogs.SaveDialog;
import jaseimov.client.dialogs.SensorSelectDialog;
import jaseimov.client.dialogs.ShowTimeDialog;
import jaseimov.lib.remote.utils.SensorUpdater;
import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.IAxis.AxisTitle;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.views.ChartPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/**
 * A JFrame to make charts with sensor's data.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class ChartFrame
        extends javax.swing.JFrame
        implements ActionListener
{
    // Chart
    private Chart2D chart = new Chart2D();
    // List of sensor added to chart    
    private SensorSelectDialog sensorDialog;


    private class FrameCloseListener extends WindowAdapter
    {
        @Override
        public void windowClosing(WindowEvent e)
        {
            if(startButton.isSelected())
                    startButton.doClick();
        }
    }

    /**
     * Creates a new ChartFrame.
     * @param infoArray An array of {@link RemoteDeviceInfo} that contains SensorDevice.
     */
    public ChartFrame(RemoteDeviceInfo[] infoArray)
    {        
        initComponents();

        // Close listener
        this.addWindowListener(new FrameCloseListener());

        chart.getAxisX().setAxisTitle(new AxisTitle("time"));
        chart.getAxisY().setAxisTitle(new AxisTitle("magnitude"));        

        // Put chart on frame
        this.getContentPane().add(new ChartPanel(chart), java.awt.BorderLayout.CENTER);
        chart.setPreferredSize(new java.awt.Dimension(800,600));

        // Put Sensor Capture List in dialog
        List sensorList = new ArrayList();
        for(RemoteDeviceInfo info : infoArray)
        {
            sensorList.add( new SensorChartInfo(info));
        }
        sensorDialog = new SensorSelectDialog(this,sensorList.toArray());        

        // Init listeners        
        sensorDialogButton.addActionListener(this);
        startButton.addActionListener(this);
        clearButton.addActionListener(this);
        saveButton.addActionListener(this);
        timeButton.addActionListener(this);

        pack();
    }
    
    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();
        
        if(source.equals(sensorDialogButton))
        {            
            sensorDialog.setVisible(true);
        }
        else if(source.equals(startButton))
        {
            if(startButton.isSelected())
            {
                Object[] objectList = sensorDialog.getSelectedObjects();
                if(objectList.length > 0)
                {
                    sensorDialogButton.setEnabled(false);

                    // Clear chart
                    for(ITrace2D tracer : chart.getTraces())
                    {
                        tracer.removeAllPoints();
                    }
                    chart.removeAllTraces();
                    // Init drawing chart
                    for(Object obj : objectList)
                    {
                        SensorChartInfo chartInfo = (SensorChartInfo) obj;
                        chartInfo.getSensorCapturer().addObserver(chartInfo.getSensorTracer());
                        chartInfo.getSensorCapturer().startCapture();
                        chart.addTrace(chartInfo.getSensorTracer().getTracer());
                    }
                }
                else
                {
                    startButton.setSelected(false);
                }
            }
            else
            {
                sensorDialogButton.setEnabled(true);
                // Stop drawing chart
                for(Object obj : sensorDialog.getSelectedObjects())
                {
                    SensorChartInfo chartInfo = (SensorChartInfo) obj;
                    chartInfo.getSensorCapturer().deleteObserver(chartInfo.getSensorTracer());
                    chartInfo.getSensorCapturer().stopCapture();                    
                }
            }
        }
        else if(source.equals(clearButton))
        {            
            for(ITrace2D tracer : chart.getTraces())
            {
                tracer.removeAllPoints();
            }            
        }
        else if(source.equals(timeButton))
        {
            Object[] selectedObjects = sensorDialog.getSelectedObjects();
            if(selectedObjects.length > 0)
            {
                List<SensorUpdater> updaterList = new ArrayList<SensorUpdater>();
                for(Object obj : selectedObjects)
                {
                    SensorChartInfo chartInfo = (SensorChartInfo) obj;
                    updaterList.add(chartInfo.getSensorCapturer().getUpdater());
                }
                new ShowTimeDialog(this,updaterList.toArray(new SensorUpdater[0])).execute();
            }
        }
        else if(source.equals(saveButton))
        {
            File file = SaveDialog.showFileDialog(this, SaveDialog.PNG_IMAGE_FILTER);
            if(file != null)
            {
                try
                {
                    BufferedImage bi = chart.snapShot();                                        
                    ImageIO.write(bi, "png", file);
                    JOptionPane.showMessageDialog(this, ClientApp.getBundleString("FILE SAVED"));
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                }
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
        sensorDialogButton = new javax.swing.JButton();
        javax.swing.JToolBar.Separator jSeparator2 = new javax.swing.JToolBar.Separator();
        timeButton = new javax.swing.JButton();
        javax.swing.JToolBar.Separator jSeparator1 = new javax.swing.JToolBar.Separator();
        startButton = new javax.swing.JToggleButton();
        javax.swing.JToolBar.Separator jSeparator3 = new javax.swing.JToolBar.Separator();
        clearButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("jaseimov/client/client"); // NOI18N
        setTitle(bundle.getString("CHART TITLE")); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        sensorDialogButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jaseimov/client/images/select_marker.png"))); // NOI18N
        sensorDialogButton.setToolTipText(bundle.getString("SELECT SENSOR")); // NOI18N
        sensorDialogButton.setFocusable(false);
        sensorDialogButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        sensorDialogButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(sensorDialogButton);
        jToolBar1.add(jSeparator2);

        timeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jaseimov/client/images/clock.png"))); // NOI18N
        timeButton.setToolTipText(bundle.getString("SET UPDATE TIME")); // NOI18N
        timeButton.setFocusable(false);
        timeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        timeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(timeButton);
        jToolBar1.add(jSeparator1);

        startButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jaseimov/client/images/control_play.png"))); // NOI18N
        startButton.setToolTipText(bundle.getString("START STOP CAPTURE")); // NOI18N
        startButton.setFocusable(false);
        startButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        startButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/jaseimov/client/images/control_stop.png"))); // NOI18N
        startButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(startButton);
        jToolBar1.add(jSeparator3);

        clearButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jaseimov/client/images/page_white.png"))); // NOI18N
        clearButton.setToolTipText(bundle.getString("CLEAR")); // NOI18N
        clearButton.setFocusable(false);
        clearButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        clearButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(clearButton);

        saveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jaseimov/client/images/picture_save.png"))); // NOI18N
        saveButton.setToolTipText(bundle.getString("SAVE IMAGE")); // NOI18N
        saveButton.setFocusable(false);
        saveButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(saveButton);

        getContentPane().add(jToolBar1, java.awt.BorderLayout.PAGE_START);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JButton clearButton;
    javax.swing.JButton saveButton;
    javax.swing.JButton sensorDialogButton;
    javax.swing.JToggleButton startButton;
    javax.swing.JButton timeButton;
    // End of variables declaration//GEN-END:variables

}
