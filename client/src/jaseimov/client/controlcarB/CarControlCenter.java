/*
 *  Copyright (C) 2011 santi
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * CarControlCenter.java
 *
 * Created on 14-sep-2011, 18:43:37
 */
package jaseimov.client.controlcarB;

import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.traces.Trace2DLtd;
import jaseimov.client.controlcarB.adaptative.Curves;
import jaseimov.client.controlcarB.adaptative.NextStep;
import jaseimov.client.controlcarB.adaptative.Sequencer;
import jaseimov.client.dialogs.SaveDialog;
import jaseimov.client.joystick.JoystickControl;
import jaseimov.client.utils.FileFunctions;
import jaseimov.lib.devices.DeviceException;
import jaseimov.lib.devices.Encoder;
import jaseimov.lib.devices.MotorControl;
import jaseimov.lib.devices.ServoControl;
import jaseimov.lib.devices.Spatial;
import java.awt.Color;
import java.io.File;
import java.lang.String;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.ListModel;


public class CarControlCenter extends javax.swing.JFrame
{


  //modules
  Sequencer seq;
  Curves crv;
  NextStep nxst;
  JoystickControl jstk;
  FileFunctions fl;

  //global vars
  double SamplingTime = 0.0;
  int trace_num=4;
  public ITrace2D[] trace = new Trace2DLtd[trace_num];
  int[] motorcontrol_limits={0,0,0};
  int[] servo_limits={0,0,0};
  boolean recToScript=false;

  // list model
  DefaultListModel model = new DefaultListModel();
  // combobox model
  DefaultComboBoxModel modelcombo = new DefaultComboBoxModel();

  //enables
  boolean objectsEnable=false;

  /** Creates new form CarControlCenter */
  public CarControlCenter(MotorControl motorControl,
                          ServoControl servoControl,
                          Spatial spatial,
                          Encoder encoder)
  { 
    initComponents();
    System.out.println("PATH : "
      + System.getProperty("java.library.path"));
    // initialize curves
    crv = new Curves();
    SamplingTime=crv.getRecomendedCurveStep();
    System.out.println("[CarControlCenter]SamplingTime: "+SamplingTime);
    // initialize NextStep module
    nxst= new NextStep(crv.getacfw(),
                       crv.getdcfw(),
                       crv.getacbw(),
                       crv.getdcbw(),
                       SamplingTime);

    // initialize the traces
    initTraces(3000);

    // initialize the sequencer
    seq=new Sequencer(motorControl,
                      servoControl,
                      spatial,
                      encoder,
                      crv,
                      nxst,
                      trace,
                      model,
                      SamplingTime);
    
    // initialize filefunctions
    fl = new FileFunctions();
    
    // get limits
    getLimits(servoControl, motorControl);

    // scriptList
    scriptlist.setModel(model);


    // combobox for the script
    orderdropbox.setModel(modelcombo);
    modelcombo.addElement("v");
    modelcombo.addElement("p");
    modelcombo.addElement("start");
    modelcombo.addElement("stop");

    //initialize own controls
    StartUpControls();

    //start the joystick thread
    //initialize the joystick
    jstk=new JoystickControl(VelocitySlider, PositionSlider, seq);
    jstk.SetVideoGameMode(true);
    jstk.start();

    ACtrlCheckBox.setSelected(seq.isAutomaticControl());
    seq.setFilterEnable(true);
    FilterCheckBox.setSelected(seq.isFilterEnable());

    //haSpinner.setValue(seq.getHitAcceleration());

    // unloack the sequencer
    seq.setAvailable(true);
    seq.setAutomaticControl(true);
    objectsEnable=true;
  }

  private void getLimits(ServoControl servoControl, MotorControl motorControl){
    try
    {
      motorcontrol_limits[0] = (int) motorControl.getVelocity();
      motorcontrol_limits[1] = (int) motorControl.getMinVelocity();
      motorcontrol_limits[2] = (int) motorControl.getMaxVelocity();

      servo_limits[0]=(int) servoControl.getPosition();
      servo_limits[1]=(int) servoControl.getMinPosition();
      servo_limits[2]=(int) servoControl.getMaxPosition();

    }
    catch (RemoteException ex)
    {
      Logger.getLogger(CarControlCenter.class.getName()).log(Level.SEVERE, null, ex);
    }
    catch (DeviceException ex)
    {
      Logger.getLogger(CarControlCenter.class.getName()).log(Level.SEVERE, null, ex);
    }


  }

  private void StartUpControls( )
  {
    //setup velocitySlider
    VelocitySlider.getModel().setRangeProperties(
              motorcontrol_limits[0],
              0,
              motorcontrol_limits[1],
              motorcontrol_limits[2],
              false);



    // setup position slider
    PositionSlider.getModel().setRangeProperties(
              servo_limits[0],
              0,
              servo_limits[1],
              servo_limits[2],
              false);
    PositionSlider.setValue(servo_limits[0]);

    VelocityLabel.setText("Vel: "+VelocitySlider.getValue());
    PositionLabel.setText("Pos: "+PositionSlider.getValue());

    Trace1CheckBox.setSelected(true);
    Trace2CheckBox.setSelected(true);
    Trace3CheckBox.setSelected(true);
  }

  private void initTraces(int valroll){

    //initialize the traces
    trace[0] = new Trace2DLtd(valroll, "Master order (m/s)");
    trace[1] = new Trace2DLtd(valroll, "Filtered order (m/s)");
    trace[2] = new Trace2DLtd(valroll, "Car Speed(encoder) (m/s)");
    trace[3] = new Trace2DLtd(valroll, "Car Speed filter(encoder) (m/s)");

        //trace colours
    trace[0].setColor(Color.BLUE);
    trace[1].setColor(Color.ORANGE);
    trace[2].setColor(Color.red);
    trace[3].setColor(Color.MAGENTA);

    Chart1.removeAllTraces();
    for(int i=0;i<trace.length;i++){
        Chart1.addTrace(trace[i]);
    }
  }

  private void traceChecks(){
    trace[0].setVisible(Trace1CheckBox.isSelected());
    trace[1].setVisible(Trace2CheckBox.isSelected());
    trace[2].setVisible(Trace3CheckBox.isSelected());
    trace[3].setVisible(Trace4CheckBox.isSelected());
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jTabbedPane1 = new javax.swing.JTabbedPane();
        ChartPanel = new javax.swing.JPanel();
        Chart1 = new info.monitorenter.gui.chart.Chart2D();
        jPanel3 = new javax.swing.JPanel();
        recScriptToggle = new javax.swing.JToggleButton();
        playScriptToggle = new javax.swing.JToggleButton();
        jPanel1 = new javax.swing.JPanel();
        RecToggle = new javax.swing.JToggleButton();
        SaveDataButton = new javax.swing.JButton();
        ClearDataButton = new javax.swing.JButton();
        TracersPanel = new javax.swing.JPanel();
        Trace1CheckBox = new javax.swing.JCheckBox();
        Trace2CheckBox = new javax.swing.JCheckBox();
        Trace3CheckBox = new javax.swing.JCheckBox();
        ClearTracesButton = new javax.swing.JButton();
        Trace4CheckBox = new javax.swing.JCheckBox();
        ScriptPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        scriptlist = new javax.swing.JList();
        orderdropbox = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        timetextbox = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        valuetextbox = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        addbutton = new javax.swing.JButton();
        deleteselectedbutton = new javax.swing.JButton();
        clearscriptButton = new javax.swing.JButton();
        loadScriptButton = new javax.swing.JButton();
        savescriptButton = new javax.swing.JButton();
        modifyButton = new javax.swing.JButton();
        ControlsPanel = new javax.swing.JPanel();
        PositionLabel = new javax.swing.JLabel();
        PositionSlider = new javax.swing.JSlider();
        VelocityLabel = new javax.swing.JLabel();
        VelocitySlider = new javax.swing.JSlider();
        IntelistopCheckBox = new javax.swing.JCheckBox();
        FilterCheckBox = new javax.swing.JCheckBox();
        ACtrlCheckBox = new javax.swing.JCheckBox();
        StopButton = new javax.swing.JButton();
        ResetButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        Chart1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        javax.swing.GroupLayout Chart1Layout = new javax.swing.GroupLayout(Chart1);
        Chart1.setLayout(Chart1Layout);
        Chart1Layout.setHorizontalGroup(
            Chart1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 585, Short.MAX_VALUE)
        );
        Chart1Layout.setVerticalGroup(
            Chart1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 415, Short.MAX_VALUE)
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Script"));

        recScriptToggle.setText("Rec to script");
        recScriptToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                recScriptToggleActionPerformed(evt);
            }
        });

        playScriptToggle.setText("Play script");
        playScriptToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playScriptToggleActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(recScriptToggle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(playScriptToggle)
                .addContainerGap(47, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(recScriptToggle)
                .addComponent(playScriptToggle))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Data"));

        RecToggle.setText("Rec");
        RecToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RecToggleActionPerformed(evt);
            }
        });

        SaveDataButton.setText("Save");
        SaveDataButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveDataButtonActionPerformed(evt);
            }
        });

        ClearDataButton.setText("clear");
        ClearDataButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ClearDataButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(RecToggle, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SaveDataButton, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(ClearDataButton)
                .addContainerGap(40, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(RecToggle)
                .addComponent(SaveDataButton)
                .addComponent(ClearDataButton))
        );

        javax.swing.GroupLayout ChartPanelLayout = new javax.swing.GroupLayout(ChartPanel);
        ChartPanel.setLayout(ChartPanelLayout);
        ChartPanelLayout.setHorizontalGroup(
            ChartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ChartPanelLayout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(Chart1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        ChartPanelLayout.setVerticalGroup(
            ChartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ChartPanelLayout.createSequentialGroup()
                .addComponent(Chart1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ChartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jTabbedPane1.addTab("Chart", ChartPanel);

        Trace1CheckBox.setText("Master order");
        Trace1CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Trace1CheckBoxActionPerformed(evt);
            }
        });

        Trace2CheckBox.setText("Filtered order");
        Trace2CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Trace2CheckBoxActionPerformed(evt);
            }
        });

        Trace3CheckBox.setText("Encoder velocity");
        Trace3CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Trace3CheckBoxActionPerformed(evt);
            }
        });

        ClearTracesButton.setText("Clear Traces");
        ClearTracesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ClearTracesButtonActionPerformed(evt);
            }
        });

        Trace4CheckBox.setText("Encoder velocity filter");
        Trace4CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Trace4CheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout TracersPanelLayout = new javax.swing.GroupLayout(TracersPanel);
        TracersPanel.setLayout(TracersPanelLayout);
        TracersPanelLayout.setHorizontalGroup(
            TracersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TracersPanelLayout.createSequentialGroup()
                .addGap(66, 66, 66)
                .addGroup(TracersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Trace4CheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, 513, Short.MAX_VALUE)
                    .addGroup(TracersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(Trace3CheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Trace2CheckBox)
                        .addComponent(Trace1CheckBox)
                        .addComponent(ClearTracesButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        TracersPanelLayout.setVerticalGroup(
            TracersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TracersPanelLayout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(Trace1CheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Trace2CheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Trace3CheckBox)
                .addGap(10, 10, 10)
                .addComponent(Trace4CheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ClearTracesButton)
                .addContainerGap(303, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Tracers", TracersPanel);

        jScrollPane1.setViewportView(scriptlist);

        orderdropbox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel1.setText("Order");

        timetextbox.setText("0.00");

        jLabel2.setText("Time (seconds)");

        valuetextbox.setText("0.00");

        jLabel3.setText("Value");

        addbutton.setText("Add");
        addbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addbuttonActionPerformed(evt);
            }
        });

        deleteselectedbutton.setText("Delete selected");
        deleteselectedbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteselectedbuttonActionPerformed(evt);
            }
        });

        clearscriptButton.setText("Clear");
        clearscriptButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearscriptButtonActionPerformed(evt);
            }
        });

        loadScriptButton.setText("Load");
        loadScriptButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadScriptButtonActionPerformed(evt);
            }
        });

        savescriptButton.setText("Save");
        savescriptButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                savescriptButtonActionPerformed(evt);
            }
        });

        modifyButton.setText("Modify");
        modifyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modifyButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ScriptPanelLayout = new javax.swing.GroupLayout(ScriptPanel);
        ScriptPanel.setLayout(ScriptPanelLayout);
        ScriptPanelLayout.setHorizontalGroup(
            ScriptPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ScriptPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ScriptPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ScriptPanelLayout.createSequentialGroup()
                        .addGroup(ScriptPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE)
                            .addGroup(ScriptPanelLayout.createSequentialGroup()
                                .addComponent(deleteselectedbutton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(clearscriptButton)
                                .addGap(18, 18, 18)
                                .addComponent(loadScriptButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(savescriptButton)))
                        .addContainerGap())
                    .addGroup(ScriptPanelLayout.createSequentialGroup()
                        .addGroup(ScriptPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(timetextbox, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(ScriptPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(orderdropbox, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(ScriptPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(ScriptPanelLayout.createSequentialGroup()
                                .addComponent(valuetextbox, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(modifyButton, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(addbutton, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(24, 24, 24))
                            .addGroup(ScriptPanelLayout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addContainerGap(271, Short.MAX_VALUE))))))
        );
        ScriptPanelLayout.setVerticalGroup(
            ScriptPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ScriptPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ScriptPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ScriptPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(timetextbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(orderdropbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(valuetextbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(modifyButton)
                    .addComponent(addbutton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ScriptPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(deleteselectedbutton)
                    .addComponent(clearscriptButton)
                    .addComponent(loadScriptButton)
                    .addComponent(savescriptButton))
                .addContainerGap(65, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Script", ScriptPanel);

        ControlsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Controls"));
        ControlsPanel.setLayout(new java.awt.GridBagLayout());

        PositionLabel.setText("jLabel2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        ControlsPanel.add(PositionLabel, gridBagConstraints);

        PositionSlider.setPaintLabels(true);
        PositionSlider.setValue(0);
        PositionSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                PositionSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        ControlsPanel.add(PositionSlider, gridBagConstraints);

        VelocityLabel.setText("jLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        ControlsPanel.add(VelocityLabel, gridBagConstraints);

        VelocitySlider.setPaintLabels(true);
        VelocitySlider.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                VelocitySliderMouseClicked(evt);
            }
        });
        VelocitySlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                VelocitySliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        ControlsPanel.add(VelocitySlider, gridBagConstraints);

        IntelistopCheckBox.setText("InteliStop");
        IntelistopCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                IntelistopCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(35, 0, 0, 0);
        ControlsPanel.add(IntelistopCheckBox, gridBagConstraints);

        FilterCheckBox.setText("Filter");
        FilterCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FilterCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        ControlsPanel.add(FilterCheckBox, gridBagConstraints);

        ACtrlCheckBox.setText("AutomaticCtrl");
        ACtrlCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ACtrlCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        ControlsPanel.add(ACtrlCheckBox, gridBagConstraints);

        StopButton.setText("Stop");
        StopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StopButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 3;
        gridBagConstraints.insets = new java.awt.Insets(64, 0, 1, 0);
        ControlsPanel.add(StopButton, gridBagConstraints);

        ResetButton.setText("Reset");
        ResetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ResetButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        ControlsPanel.add(ResetButton, gridBagConstraints);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(ControlsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 592, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ControlsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

  private void IntelistopCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_IntelistopCheckBoxActionPerformed
    // Intelistop check
    seq.setInteliStopEnable( IntelistopCheckBox.isSelected());
  }//GEN-LAST:event_IntelistopCheckBoxActionPerformed

  private void VelocitySliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_VelocitySliderStateChanged
    // set speed
    if (seq.isAvailable()==true){
      seq.setVord((double)VelocitySlider.getValue());
      seq.Start();
      
      VelocityLabel.setText("Vel:"+ String.format("%.2g%n",seq.getVord_real())+" m/s");
    }

    if (recToScript){
      String str=seq.getCurrentTime()+";v;"+VelocitySlider.getValue();
      int pos = scriptlist.getModel().getSize();
      model.add(pos, str);
    }
  }//GEN-LAST:event_VelocitySliderStateChanged

  private void PositionSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_PositionSliderStateChanged
    // Set servo position
    if (seq.isAvailable()==true){
      seq.setServoPosition((double)PositionSlider.getValue());
      PositionLabel.setText("Pos: "+PositionSlider.getValue());
    }

    if (recToScript){
      String str=seq.getCurrentTime()+";p;"+PositionSlider.getValue();
      int pos = scriptlist.getModel().getSize();
      model.add(pos, str);
    }
  }//GEN-LAST:event_PositionSliderStateChanged

  private void FilterCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FilterCheckBoxActionPerformed
    // Filtering on - off
    seq.setFilterEnable(FilterCheckBox.isSelected());
  }//GEN-LAST:event_FilterCheckBoxActionPerformed

  private void ACtrlCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ACtrlCheckBoxActionPerformed
    // Automatic control on - off
    seq.setAutomaticControl(ACtrlCheckBox.isSelected());
  }//GEN-LAST:event_ACtrlCheckBoxActionPerformed

  private void VelocitySliderMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_VelocitySliderMouseClicked
    // TODO add your handling code here:
  }//GEN-LAST:event_VelocitySliderMouseClicked

  private void StopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_StopButtonActionPerformed
    // Stop button
    boolean prevstate=FilterCheckBox.isSelected();
    FilterCheckBox.setSelected(false);
    VelocitySlider.setValue(motorcontrol_limits[0]);
    VelocitySlider.setValue(0);
    PositionSlider.setValue(servo_limits[0]);
    FilterCheckBox.setSelected(prevstate);
    seq.stopMotor();
    //seq.Pause();
  }//GEN-LAST:event_StopButtonActionPerformed

  private void SaveDataButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveDataButtonActionPerformed
    // Save data
    //Create a file chooser
    File file = SaveDialog.showFileDialog(CarControlCenter.this, SaveDialog.CSV_FILTER);
    if(file != null)
    {
      seq.SaveData(file);
    }
  }//GEN-LAST:event_SaveDataButtonActionPerformed

  private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    // on close: Stop car
    VelocitySlider.setValue(motorcontrol_limits[0]);
    PositionSlider.setValue(servo_limits[0]);
    FilterCheckBox.setSelected(false);
    seq.setVord(0.0);
    seq.setAutomaticControl(false);
    seq.SendOrderToMotorControl(0.0);
    seq.Pause();
    
  }//GEN-LAST:event_formWindowClosing

  private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
    //on close:  Stop car
    VelocitySlider.setValue(motorcontrol_limits[0]);
    PositionSlider.setValue(servo_limits[0]);
    FilterCheckBox.setSelected(false);
    seq.setVord(0.0);
    seq.setAutomaticControl(false);
    seq.SendOrderToMotorControl(0.0);
    seq.Pause();
    
  }//GEN-LAST:event_formWindowClosed

  private void RecToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RecToggleActionPerformed
    // Rec enable
    seq.setRecEnable(RecToggle.isSelected());
  }//GEN-LAST:event_RecToggleActionPerformed

  private void ResetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ResetButtonActionPerformed
    // Reset all
    FilterCheckBox.setSelected(false);
    seq.setVord(0.0);
    seq.SendOrderToMotorControl(0.0);
    seq.Pause();
    for (int i=0;i< trace.length;i++){
      trace[i].removeAllPoints();
    }
  }//GEN-LAST:event_ResetButtonActionPerformed

  private void Trace1CheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Trace1CheckBoxActionPerformed
    // TODO add your handling code here:
    traceChecks();
  }//GEN-LAST:event_Trace1CheckBoxActionPerformed

  private void Trace2CheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Trace2CheckBoxActionPerformed
    // TODO add your handling code here:
    traceChecks();
  }//GEN-LAST:event_Trace2CheckBoxActionPerformed

  private void Trace3CheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Trace3CheckBoxActionPerformed
    // TODO add your handling code here:
    traceChecks();
  }//GEN-LAST:event_Trace3CheckBoxActionPerformed

  private void ClearTracesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClearTracesButtonActionPerformed
    // ClearTraces
    for (int i=0;i< trace.length;i++){
      trace[i].removeAllPoints();
    }
  }//GEN-LAST:event_ClearTracesButtonActionPerformed

  private void addbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addbuttonActionPerformed
    // Add to list
    String str=timetextbox.getText() + ";" +
               orderdropbox.getSelectedItem().toString() + ";" +
               valuetextbox.getText();
    int pos = scriptlist.getModel().getSize();
    model.add(pos, str);
  }//GEN-LAST:event_addbuttonActionPerformed

  private void modifyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modifyButtonActionPerformed
    // modify script line
    String str=timetextbox.getText() + ";" +
               orderdropbox.getSelectedItem().toString() + ";" +
               valuetextbox.getText();
    int pos = scriptlist.getSelectedIndex();
    model.set(pos, str);
  }//GEN-LAST:event_modifyButtonActionPerformed

  private void deleteselectedbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteselectedbuttonActionPerformed
    // delete selected from script
    int[] idx = scriptlist.getSelectedIndices();
    for (int i=idx.length-1; i>=0 ;i--){
      model.remove(idx[i]);
    }
  }//GEN-LAST:event_deleteselectedbuttonActionPerformed

  private void clearscriptButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearscriptButtonActionPerformed
    // clear script
    model.removeAllElements();
  }//GEN-LAST:event_clearscriptButtonActionPerformed

  private void playScriptToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playScriptToggleActionPerformed
    // play script toggle button
    seq.setScriptEnable(playScriptToggle.isSelected());
  }//GEN-LAST:event_playScriptToggleActionPerformed

  private void Trace4CheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Trace4CheckBoxActionPerformed
    // TODO add your handling code here:
    traceChecks();
  }//GEN-LAST:event_Trace4CheckBoxActionPerformed

  private void recScriptToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_recScriptToggleActionPerformed
    // Rec to script toggle button
    recToScript=recScriptToggle.isSelected();
    if (recScriptToggle.isSelected()){
      seq.Start();
    }
  }//GEN-LAST:event_recScriptToggleActionPerformed

  private void savescriptButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_savescriptButtonActionPerformed
    // save scriptlist file
    File file = SaveDialog.showFileDialog(CarControlCenter.this, SaveDialog.CSV_FILTER);
    if(file != null)
    {
      ListModel ls=scriptlist.getModel();
      ArrayList<String> cont = new ArrayList<String>();
      for (int i=0; i<ls.getSize();i++ ){
        cont.add((String) ls.getElementAt(i));
      }

      fl.SaveFile(cont, file);
    }
  }//GEN-LAST:event_savescriptButtonActionPerformed

  private void loadScriptButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadScriptButtonActionPerformed
    // loas scriptlist file
    File file = fl.OpenDialog("Open script");
    if(file != null)
    {
      ListModel ls=scriptlist.getModel();
      ArrayList<String> cont = fl.OpenFile(file.getAbsolutePath());

      for (int i=0; i< cont.size();i++){
        System.out.println(cont.get(i));
        model.addElement(cont.get(i));
      }
    }
  }//GEN-LAST:event_loadScriptButtonActionPerformed

  private void ClearDataButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClearDataButtonActionPerformed
    // TODO add your handling code here:

      seq.clearRecData();

  }//GEN-LAST:event_ClearDataButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox ACtrlCheckBox;
    private info.monitorenter.gui.chart.Chart2D Chart1;
    private javax.swing.JPanel ChartPanel;
    private javax.swing.JButton ClearDataButton;
    private javax.swing.JButton ClearTracesButton;
    private javax.swing.JPanel ControlsPanel;
    private javax.swing.JCheckBox FilterCheckBox;
    private javax.swing.JCheckBox IntelistopCheckBox;
    private javax.swing.JLabel PositionLabel;
    private javax.swing.JSlider PositionSlider;
    private javax.swing.JToggleButton RecToggle;
    private javax.swing.JButton ResetButton;
    private javax.swing.JButton SaveDataButton;
    private javax.swing.JPanel ScriptPanel;
    private javax.swing.JButton StopButton;
    private javax.swing.JCheckBox Trace1CheckBox;
    private javax.swing.JCheckBox Trace2CheckBox;
    private javax.swing.JCheckBox Trace3CheckBox;
    private javax.swing.JCheckBox Trace4CheckBox;
    private javax.swing.JPanel TracersPanel;
    private javax.swing.JLabel VelocityLabel;
    private javax.swing.JSlider VelocitySlider;
    private javax.swing.JButton addbutton;
    private javax.swing.JButton clearscriptButton;
    private javax.swing.JButton deleteselectedbutton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton loadScriptButton;
    private javax.swing.JButton modifyButton;
    private javax.swing.JComboBox orderdropbox;
    private javax.swing.JToggleButton playScriptToggle;
    private javax.swing.JToggleButton recScriptToggle;
    private javax.swing.JButton savescriptButton;
    private javax.swing.JList scriptlist;
    private javax.swing.JFormattedTextField timetextbox;
    private javax.swing.JFormattedTextField valuetextbox;
    // End of variables declaration//GEN-END:variables
}
