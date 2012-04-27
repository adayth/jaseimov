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

import client.ClientApp;
import client.devicelist.DeviceInfo;
import client.dialogs.SaveDialog;
import client.dialogs.ShowTimeDialog;
import client.servercomm.ConnectException;
import client.update.SensorCapturer;
import client.update.Updater;
import device.Camera;
import device.Device;
import device.DeviceType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Observable;
import java.util.Observer;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A JPanel that shows a Camera device in it.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class CameraPanel 
        extends javax.swing.JPanel
        implements ActionListener, ChangeListener, Observer
{
    private JFrame parent;

    private Camera camera;
    private int width;
    private int height;

    protected SensorCapturer capturer;
    private Updater updater;

    private PlayVideo player;

    private class FrameCloseListener extends WindowAdapter
    {
        @Override
        public void windowClosing(WindowEvent e)
        {
            updater.deleteObserver(CameraPanel.this);                        
        }
    }

    /**
     * Creates a new CameraPanel.
     * @param parentFrame The JFrame that will contain this panel.
     * @param info A {@link DeviceInfo} that contains a Camera device.
     */
    public CameraPanel(JFrame parentFrame, DeviceInfo info)
    {
        initComponents();

        //Set parent frame
        parent = parentFrame;
        parent.addWindowListener(new FrameCloseListener());

        // Init camera
        try
        {
            Device device = info.getDevice();       
            infoLabel.setText(device.getName() + " ID: " + device.getID());
            if(device.getDeviceType() == DeviceType.CAMERA_SENSOR)
            {
                camera = (Camera) device;
                width = camera.getImageWidth();
                height = camera.getImageHeigth();

                capturer = new SensorCapturer(info);
                player = new PlayVideo(this);
                updater = info.getUpdater();
            }
            else
                throw new IllegalArgumentException();
        }
        catch(ConnectException ex)
        {
            ex.printStackTrace();
        }
        catch(RemoteException ex)
        {
            ex.printStackTrace();
        }

        // Init image container
        imageLabel.setPreferredSize(new java.awt.Dimension(width,height));
        imageLabel.setDoubleBuffered(true);        

        // Set resolution label info
        resolutionLabel.setText(width + " x " + height + " pix");

        // Init GUI Button listeners
        viewButton.addActionListener(this);
        captureButton.addActionListener(this);
        playButton.addActionListener(this);
        deleteButton.addActionListener(this);
        startButton.addActionListener(this);
        endButton.addActionListener(this);
        saveImageButton.addActionListener(this);
        saveVideoButton.addActionListener(this);
        setTimeButton.addActionListener(this);

        // Slider listener
        positionSlider.addChangeListener(this);

        // Init record GUI visibility
        this.showRecordGUI(false);
    }

    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();

        if(source.equals(viewButton))
        {
            // Selected -> view camera
            if(viewButton.isSelected())
            {                
                updater.addObserver(this);                
                updater.autoUpdate(true);
            }
            // Unselected -> stop viewing camera
            else
            {                                            
                updater.deleteObserver(this);
            }
        }
        else if(source.equals(captureButton))
        {
            // Selected -> capturing
            if(captureButton.isSelected())
            {
                capturer.startCapture();
                this.showRecordGUI(false);
                //capturing = true;
                if(!viewButton.isSelected())
                {
                    viewButton.doClick();
                }
            }
            else
            {
                capturer.stopCapture();
                //capturing = false;
                if(capturer.getSize() > 0)
                {
                    this.showRecordGUI(true);
                    if(viewButton.isSelected())
                    {
                        viewButton.doClick();
                    }
                }
            }
        }
        else if(source.equals(playButton))
        {
            // Selected -> play
            if(playButton.isSelected())
            {                                
                player.play();
            }
            // Unselected -> stop
            else
            {
                player.stop();
            }
        }
        else if(source.equals(deleteButton))
        {
            // Clear captured frames and update GUI
            if(player.isPlaying())
            {
                player.stop();                
            }
            player.setPlayPosition(0);
            playButton.setSelected(false);
            capturer.clearCapture();
            this.updatePosition();
            this.showRecordGUI(false);                        
        }
        else if(source.equals(startButton))
        {
            // Set position to start position
            player.setPlayPosition(0);
            this.updatePosition();
        }
        else if(source.equals(endButton))
        {
            // Set position to end position
            if(capturer.getSize() > 0)
            {
                player.setPlayPosition(capturer.getSize()-1);
                this.updatePosition();
            }
        }
        else if(source.equals(setTimeButton))
        {            
            new ShowTimeDialog(this, updater).execute();
        }
        else if(source.equals(saveImageButton))
        {
            if(lastImage != null)
            {
                File file = SaveDialog.showFileDialog(this, SaveDialog.JPEG_IMAGE_FILTER);
                if(file != null)
                {
                    try
                    {
                        FileOutputStream out = new FileOutputStream(file);
                        out.write(lastImage);
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
        else if(source.equals(saveVideoButton))
        {
            if(capturer.getSize() > 0)
            {
                File directory = SaveDialog.showDirectoryDialog(this);
                if(directory != null)
                {
                    try
                    {
                        for(int i=0; i<capturer.getSize(); i++)
                        {
                            FileOutputStream out = new FileOutputStream(directory.getPath() + File.separator + i + ".jpg");
                            out.write((byte[])capturer.getData(i));
                            out.close();
                        }
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

    public void stateChanged(ChangeEvent e)
    {
        int value = positionSlider.getValue();
        if(player.getPlayPosition() != value)
        {
            player.setPlayPosition(value);
            updateImage((byte[])capturer.getData(value));
            updateImage((byte[])capturer.getData(value));
        }
    }

    public void update(Observable o, Object arg)
    {
        byte[] buffer = (byte[])arg;
        this.updateImage(buffer);
    }

    private int frames = 1;
    private long t0 = System.currentTimeMillis();
    private byte[] lastImage;

    public void updateImage(byte[] image)
    {
        lastImage = image;
        imageLabel.setIcon(new ImageIcon(image));

        long t = System.currentTimeMillis();

        if(t-t0 >= 1000)
        {
            fpsLabel.setText(String.format("%1$.2f FPS", frames / ((t-t0) / 1000.)));
            frames = 1;
            t0 = t;
        }
        else
        {
            frames++;
        }
        updatePosition();
    }               

    private void showRecordGUI(boolean value)
    {        
        startButton.setEnabled(value);
        endButton.setEnabled(value);
        positionSlider.setEnabled(value);
        playButton.setEnabled(value);        
        deleteButton.setEnabled(value);

        updatePosition();
    }

    public void updatePosition()
    {
        //Adjust max position value
        int maxPosition = capturer.getSize();
        if(maxPosition > 0)
        {
            maxPosition--;
        }
        // Update position slider
        positionSlider.setValue(player.getPlayPosition());
        positionSlider.setMaximum(maxPosition);
        // Update position label
        positionLabel.setText(player.getPlayPosition() + "/" + maxPosition);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        imageLabel = new javax.swing.JLabel();
        javax.swing.JToolBar jToolBar2 = new javax.swing.JToolBar();
        infoLabel = new javax.swing.JLabel();
        javax.swing.JToolBar.Separator jSeparator3 = new javax.swing.JToolBar.Separator();
        resolutionLabel = new javax.swing.JLabel();
        javax.swing.JToolBar.Separator jSeparator4 = new javax.swing.JToolBar.Separator();
        fpsLabel = new javax.swing.JLabel();
        javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
        javax.swing.JToolBar jToolBar1 = new javax.swing.JToolBar();
        viewButton = new javax.swing.JToggleButton();
        javax.swing.JToolBar.Separator jSeparator5 = new javax.swing.JToolBar.Separator();
        captureButton = new javax.swing.JToggleButton();
        playButton = new javax.swing.JToggleButton();
        deleteButton = new javax.swing.JButton();
        javax.swing.JToolBar.Separator jSeparator1 = new javax.swing.JToolBar.Separator();
        saveImageButton = new javax.swing.JButton();
        saveVideoButton = new javax.swing.JButton();
        javax.swing.JToolBar.Separator jSeparator2 = new javax.swing.JToolBar.Separator();
        setTimeButton = new javax.swing.JButton();
        recordToolBar = new javax.swing.JToolBar();
        startButton = new javax.swing.JButton();
        positionSlider = new javax.swing.JSlider();
        endButton = new javax.swing.JButton();
        javax.swing.JToolBar.Separator jSeparator6 = new javax.swing.JToolBar.Separator();
        positionLabel = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        setLayout(new java.awt.BorderLayout());

        imageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        imageLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/client/images/webcam.png"))); // NOI18N
        add(imageLabel, java.awt.BorderLayout.CENTER);

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);

        infoLabel.setText("camera 4 - ID: 25");
        jToolBar2.add(infoLabel);
        jToolBar2.add(jSeparator3);

        resolutionLabel.setText("352x288");
        jToolBar2.add(resolutionLabel);
        jToolBar2.add(jSeparator4);

        fpsLabel.setText("00.00 FPS");
        jToolBar2.add(fpsLabel);

        add(jToolBar2, java.awt.BorderLayout.PAGE_START);

        jPanel1.setLayout(new java.awt.BorderLayout());

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        viewButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/client/images/eye.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("client/client"); // NOI18N
        viewButton.setToolTipText(bundle.getString("VIEW")); // NOI18N
        viewButton.setFocusable(false);
        viewButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        viewButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(viewButton);
        jToolBar1.add(jSeparator5);

        captureButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/client/images/control_record.png"))); // NOI18N
        captureButton.setToolTipText(bundle.getString("CAPTURE VIDEO")); // NOI18N
        captureButton.setFocusable(false);
        captureButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        captureButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(captureButton);

        playButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/client/images/control_play.png"))); // NOI18N
        playButton.setToolTipText(bundle.getString("PLAY CAPTURE")); // NOI18N
        playButton.setFocusable(false);
        playButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        playButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/client/images/control_pause.png"))); // NOI18N
        playButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(playButton);

        deleteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/client/images/close.png"))); // NOI18N
        deleteButton.setToolTipText(bundle.getString("DELETE VIDEO")); // NOI18N
        deleteButton.setFocusable(false);
        deleteButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        deleteButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(deleteButton);
        jToolBar1.add(jSeparator1);

        saveImageButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/client/images/picture_save.png"))); // NOI18N
        saveImageButton.setToolTipText(bundle.getString("SAVE IMAGE")); // NOI18N
        saveImageButton.setFocusable(false);
        saveImageButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveImageButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(saveImageButton);

        saveVideoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/client/images/film_save.png"))); // NOI18N
        saveVideoButton.setToolTipText(bundle.getString("SAVE VIDEO")); // NOI18N
        saveVideoButton.setFocusable(false);
        saveVideoButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveVideoButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(saveVideoButton);
        jToolBar1.add(jSeparator2);

        setTimeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/client/images/clock.png"))); // NOI18N
        setTimeButton.setToolTipText(bundle.getString("SET UPDATE TIME")); // NOI18N
        setTimeButton.setFocusable(false);
        setTimeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        setTimeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(setTimeButton);

        jPanel1.add(jToolBar1, java.awt.BorderLayout.PAGE_END);

        recordToolBar.setFloatable(false);
        recordToolBar.setRollover(true);

        startButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/client/images/control_start.png"))); // NOI18N
        startButton.setToolTipText(bundle.getString("GO TO START FRAME")); // NOI18N
        startButton.setFocusable(false);
        startButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        startButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        recordToolBar.add(startButton);

        positionSlider.setValue(0);
        recordToolBar.add(positionSlider);

        endButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/client/images/control_end.png"))); // NOI18N
        endButton.setToolTipText(bundle.getString("GO TO END FRAME")); // NOI18N
        endButton.setFocusable(false);
        endButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        endButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        recordToolBar.add(endButton);
        recordToolBar.add(jSeparator6);

        positionLabel.setText("100/100");
        recordToolBar.add(positionLabel);

        jPanel1.add(recordToolBar, java.awt.BorderLayout.CENTER);

        add(jPanel1, java.awt.BorderLayout.PAGE_END);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JToggleButton captureButton;
    javax.swing.JButton deleteButton;
    javax.swing.JButton endButton;
    javax.swing.JLabel fpsLabel;
    javax.swing.JLabel imageLabel;
    javax.swing.JLabel infoLabel;
    javax.swing.JToggleButton playButton;
    javax.swing.JLabel positionLabel;
    javax.swing.JSlider positionSlider;
    javax.swing.JToolBar recordToolBar;
    javax.swing.JLabel resolutionLabel;
    javax.swing.JButton saveImageButton;
    javax.swing.JButton saveVideoButton;
    javax.swing.JButton setTimeButton;
    javax.swing.JButton startButton;
    javax.swing.JToggleButton viewButton;
    // End of variables declaration//GEN-END:variables


}
