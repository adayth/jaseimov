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
package client.camera;

import client.devicelist.DeviceInfo;
import device.Device;
import device.DevicePosition;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;

/**
 * A JFrame for view cameras organized in two types: front cameras and back cameras. Every camera is contained in a {@link CameraPanel}.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class CameraFrame
        extends javax.swing.JFrame
        implements ActionListener
{    
    /**
     * Creates a new CameraFrame.
     * @param deviceArray An array of {@link DeviceInfo} that contains Cameras devices.
     */
    public CameraFrame(DeviceInfo[] deviceArray)
    {
        initComponents();
        // Init GUI        
        viewAllButton.addActionListener(this);        
        viewFrontButton.addActionListener(this);        
        viewBackButton.addActionListener(this);        

        // Setup cameras
        for(DeviceInfo info : deviceArray)
        {
            try
            {
                Device device = info.getDevice();                               
                addCamera(info,device.getDevicePosition());
            }
            // Catch ConnectException / RemoteException
            catch(Exception ex)
            {
                ex.printStackTrace();
            }            
        }
        viewFrontButton.doClick();
    }

    private void addCamera(DeviceInfo camera, DevicePosition position)
    {
        JPanel panel;        
        String layoutPosition;
        
        switch(position)
        {
            case FRONT_LEFT_CAMERA:
                panel = frontPanel;
                layoutPosition = java.awt.BorderLayout.BEFORE_LINE_BEGINS;
                break;
            case FRONT_CENTER_CAMERA:
                panel = frontPanel;
                layoutPosition = java.awt.BorderLayout.CENTER;
                break;
            case FRONT_RIGHT_CAMERA:
                panel = frontPanel;
                layoutPosition = java.awt.BorderLayout.AFTER_LINE_ENDS;
                break;
            case BACK_LEFT_CAMERA:
                panel = backPanel;
                layoutPosition = java.awt.BorderLayout.BEFORE_LINE_BEGINS;
                break;
            case BACK_CENTER_CAMERA:
                panel = backPanel;
                layoutPosition = java.awt.BorderLayout.CENTER;
                break;
            case BACK_RIGHT_CAMERA:
                panel = backPanel;
                layoutPosition = java.awt.BorderLayout.AFTER_LINE_ENDS;
                break;
            default:
                throw new IllegalArgumentException();                
        }

        panel.add(new CameraPanel(this, camera), layoutPosition);
    }

    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();        
        if(source.equals(viewAllButton))
        {
            frontPanel.setVisible(true);
            backPanel.setVisible(true);
        }
        else if(source.equals(viewFrontButton))
        {
            frontPanel.setVisible(true);
            backPanel.setVisible(false);
        }
        else if(source.equals(viewBackButton))
        {
            frontPanel.setVisible(false);
            backPanel.setVisible(true);
        }
        if(frontPanel.getComponentCount() == 0)
        {
            frontPanel.setVisible(false);
        }
        if(backPanel.getComponentCount() == 0)
        {
            backPanel.setVisible(false);
        }
        pack();        
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
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        javax.swing.JToolBar.Separator jSeparator1 = new javax.swing.JToolBar.Separator();
        viewAllButton = new javax.swing.JButton();
        viewFrontButton = new javax.swing.JButton();
        viewBackButton = new javax.swing.JButton();
        javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        javax.swing.JPanel jPanel3 = new javax.swing.JPanel();
        frontPanel = new javax.swing.JPanel();
        backPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("client/client"); // NOI18N
        setTitle(bundle.getString("CAMERA FRAME TITLE")); // NOI18N
        setResizable(false);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        jLabel1.setText(bundle.getString("VIEW")); // NOI18N
        jToolBar1.add(jLabel1);
        jToolBar1.add(jSeparator1);

        viewAllButton.setText(bundle.getString("ALL")); // NOI18N
        viewAllButton.setFocusable(false);
        viewAllButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        viewAllButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(viewAllButton);

        viewFrontButton.setText(bundle.getString("FRONT")); // NOI18N
        viewFrontButton.setFocusable(false);
        viewFrontButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        viewFrontButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(viewFrontButton);

        viewBackButton.setText(bundle.getString("BACK")); // NOI18N
        viewBackButton.setFocusable(false);
        viewBackButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        viewBackButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(viewBackButton);

        getContentPane().add(jToolBar1, java.awt.BorderLayout.PAGE_START);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.Y_AXIS));

        frontPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("FRONT CAMERAS"))); // NOI18N
        frontPanel.setLayout(new java.awt.BorderLayout());
        jPanel3.add(frontPanel);

        backPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BACK CAMERAS"))); // NOI18N
        backPanel.setLayout(new java.awt.BorderLayout());
        jPanel3.add(backPanel);

        jScrollPane1.setViewportView(jPanel3);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JPanel backPanel;
    javax.swing.JPanel frontPanel;
    javax.swing.JButton viewAllButton;
    javax.swing.JButton viewBackButton;
    javax.swing.JButton viewFrontButton;
    // End of variables declaration//GEN-END:variables

}
