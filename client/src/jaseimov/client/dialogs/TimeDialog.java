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
package jaseimov.client.dialogs;

import jaseimov.client.ClientApp;
import jaseimov.lib.remote.utils.SensorUpdater;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

/**
 * JDialog to change update time of an array of SensorUpdater.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class TimeDialog extends javax.swing.JDialog implements ActionListener
{    
    private SensorUpdater[] updaterList;

    /**
     * Creates a TimeDialog.
     * @param parent Position of the dialog will be relative to this component.
     * @param list Array of {@link SensorUpdater} objects.
     */
    public TimeDialog(java.awt.Frame parent, SensorUpdater[] list)
    {
        super(parent, true);
        initComponents();
        
        updaterList = list;

        cancelButton.addActionListener(this);
        okButton.addActionListener(this);
        resetButton.addActionListener(this);

        timeField.setPreferredSize(timeField.getSize());
        int time;
        if(updaterList.length > 1)
        {
            // Time will be the average between all members in list
            time = 0;
            for(SensorUpdater updater : list)
            {
                time += updater.getUpdateTime();
            }
            time = time / list.length;
        }
        else
        {
            time = updaterList[0].getUpdateTime();
        }
        timeField.setText(String.valueOf(time));        
    }

    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();
        if(source.equals(resetButton))
        {
            timeField.setText(String.valueOf(SensorUpdater.DEFAULT_UPDATE_TIME));
        }
        else if(source.equals(okButton))
        {
            int time = Integer.valueOf(timeField.getText());
            if(time > 0)
            {
                new SetTimeCommand(updaterList, time).execute();
                this.dispose();
            }
            else
            {
                JOptionPane.showMessageDialog(this, ClientApp.getBundleString("TIME ZERO ERROR"), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        else if(source.equals(cancelButton))
        {            
            this.dispose();
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

        javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        timeField = new javax.swing.JTextField();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        resetButton = new javax.swing.JButton();
        javax.swing.JPanel jPanel2 = new javax.swing.JPanel();
        cancelButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("jaseimov/client/client"); // NOI18N
        setTitle(bundle.getString("SET UPDATE TIME")); // NOI18N

        jLabel1.setText(bundle.getString("UPDATE TIME")); // NOI18N
        jPanel1.add(jLabel1);

        timeField.setText(bundle.getString("TIME FIELD VALUE")); // NOI18N
        jPanel1.add(timeField);

        jLabel2.setText(bundle.getString("MILLISECONDS")); // NOI18N
        jPanel1.add(jLabel2);

        resetButton.setText(bundle.getString("RESET TO DEFAULT")); // NOI18N
        resetButton.setToolTipText(bundle.getString("RESET TO DEFAULT TOOLTIP")); // NOI18N
        jPanel1.add(resetButton);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        cancelButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jaseimov/client/images/cancel.png"))); // NOI18N
        cancelButton.setText(bundle.getString("CANCEL")); // NOI18N
        jPanel2.add(cancelButton);

        okButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jaseimov/client/images/ok.png"))); // NOI18N
        okButton.setText(bundle.getString("OK")); // NOI18N
        jPanel2.add(okButton);

        getContentPane().add(jPanel2, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents
   

    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JButton cancelButton;
    javax.swing.JButton okButton;
    javax.swing.JButton resetButton;
    javax.swing.JTextField timeField;
    // End of variables declaration//GEN-END:variables
   
}
