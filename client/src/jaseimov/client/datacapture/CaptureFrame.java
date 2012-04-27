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
package jaseimov.client.datacapture;

import jaseimov.client.ClientApp;
import jaseimov.lib.remote.utils.SensorCapturer;
import jaseimov.lib.remote.list.RemoteDeviceInfo;
import jaseimov.client.dialogs.SaveDialog;
import jaseimov.client.dialogs.SensorSelectDialog;
import jaseimov.client.dialogs.ShowTimeDialog;
import jaseimov.lib.remote.utils.SensorUpdater;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

/**
 * JFrame that allows to capture data from SensorDevice devices. Show captured data in a JTable.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class CaptureFrame extends javax.swing.JFrame implements ActionListener
{
    // Table
    private JTable table;

    private SensorTableModel tableModel;    
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
     * Creates a CaptureFrame.
     * @param infoArray An array of {@link RemoteDeviceInfo} that contains SensorDevice devices.
     */
    public CaptureFrame(RemoteDeviceInfo[] infoArray)
    {
        initComponents();

        // Close listener
        this.addWindowListener(new FrameCloseListener());

        // Fill dialog sensor list
        List sensorList = new ArrayList();
        for(RemoteDeviceInfo info : infoArray)
        {
            sensorList.add(new SensorCapturer(info));
        }
        sensorDialog = new SensorSelectDialog(this, sensorList.toArray());

        // Create table
        tableModel = new SensorTableModel();
        table = new JTable(tableModel);
        scrollPanel.setViewportView(table);        

        // Init listeners        
        sensorDialogButton.addActionListener(this);
        startButton.addActionListener(this);
        clearButton.addActionListener(this);
        saveButton.addActionListener(this);
        timeButton.addActionListener(this);        

        pack();
        tableModel.addTableModelListener(new TableModelListener()
            {
                public void tableChanged(TableModelEvent e)
                {
                    // Auto scroll content
                    table.scrollRectToVisible(table.getCellRect(table.getRowCount()-1, table.getColumnCount(), true));
                }
            }
        );
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
            // Start Capture
            if(startButton.isSelected())
            {
                Object[] objectList = sensorDialog.getSelectedObjects();
                if(objectList.length > 0)
                {
                    sensorDialogButton.setEnabled(false);

                    for(Object obj : objectList)
                    {
                        SensorCapturer capturer = (SensorCapturer) obj;
                        capturer.addObserver(tableModel);
                        capturer.startCapture();
                    }
                }
                else
                {
                    startButton.setSelected(false);
                }
            }
            // Stop Capture
            else
            {
                sensorDialogButton.setEnabled(true);

                for(Object obj : sensorDialog.getSelectedObjects())
                {
                    SensorCapturer capturer = (SensorCapturer) obj;
                    capturer.deleteObserver(tableModel);
                    capturer.stopCapture();
                }
            }
        }
        else if(source.equals(clearButton))
        {
            tableModel.clearTable();
        }
        else if(source.equals(timeButton))
        {
            Object[] selectedObjects = sensorDialog.getSelectedObjects();
            if(selectedObjects.length > 0)
            {
                List<SensorUpdater> updaterList = new ArrayList<SensorUpdater>();
                for(Object obj : selectedObjects)
                {
                    SensorCapturer capturer = (SensorCapturer) obj;
                    updaterList.add(capturer.getUpdater());
                }
                new ShowTimeDialog(this,updaterList.toArray(new SensorUpdater[0])).execute();
            }
        }
        else if(source.equals(saveButton))
        {
            if(tableModel.getRowCount() > 0)
            {
                File file = SaveDialog.showFileDialog(this, SaveDialog.CSV_FILTER);
                if(file != null)
                {
                    try
                    {
                        FileOutputStream out = new FileOutputStream(file.getPath() + ".csv");
                        OutputStreamWriter writer = new OutputStreamWriter(out);

                        // Write table column names
                        for(String name : tableModel.columnNames)
                        {
                            writer.write(name);
                            writer.write(",");
                        }
                        writer.write("\n");

                        // Write data rows
                        Object[][] data = tableModel.getTable();
                        if(data.length > 0)
                        {
                            for(Object[] row : data)
                            {
                                for(Object obj : row)
                                {
                                    writer.write(obj.toString());
                                    writer.write(",");
                                }
                                writer.write("\n");
                            }
                        }
                        writer.flush();
                        writer.close();
                        out.close();
                        JOptionPane.showMessageDialog(this, ClientApp.getBundleString("FILE SAVED"));
                    }
                    catch(FileNotFoundException ex)
                    {
                        ex.printStackTrace();
                    }
                    catch(IOException ex)
                    {
                        ex.printStackTrace();
                    }
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
        scrollPanel = new javax.swing.JScrollPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("jaseimov/client/client"); // NOI18N
        setTitle(bundle.getString("DATA CAPTURE TITLE")); // NOI18N

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
        clearButton.setToolTipText(bundle.getString("CLEAR CAPTURE")); // NOI18N
        clearButton.setFocusable(false);
        clearButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        clearButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(clearButton);

        saveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jaseimov/client/images/table_save.png"))); // NOI18N
        saveButton.setToolTipText(bundle.getString("SAVE CAPTURE")); // NOI18N
        saveButton.setFocusable(false);
        saveButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(saveButton);

        getContentPane().add(jToolBar1, java.awt.BorderLayout.PAGE_START);
        getContentPane().add(scrollPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JButton clearButton;
    javax.swing.JButton saveButton;
    javax.swing.JScrollPane scrollPanel;
    javax.swing.JButton sensorDialogButton;
    javax.swing.JToggleButton startButton;
    javax.swing.JButton timeButton;
    // End of variables declaration//GEN-END:variables

}
