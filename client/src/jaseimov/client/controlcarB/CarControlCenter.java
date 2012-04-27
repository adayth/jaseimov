package jaseimov.client.controlcarB;

import jaseimov.client.joystick.JoystickControl;
import jaseimov.client.controlcarB.filter.Sequencer;
import jaseimov.client.utils.Command;
import jaseimov.client.utils.FileFunctions;
import jaseimov.lib.devices.Accelerometer;
import jaseimov.lib.devices.MotorControl;
import jaseimov.lib.devices.ServoControl;
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author santi
 */
public class CarControlCenter extends javax.swing.JFrame
{
  // ControlFrame commands
  Command setVelocity;
  Command setAcceleration;
  Command setPosition;
  Command stopVehicle;
  //limits
  double maxs; //max speed
  double mins; //min speed
  double maxa; //max acceleration
  double mina; //min acceleration
  double maxp; //max position
  double minp; //min position
  double centerp; // central position
  boolean rec = false; //for recording the steps in the program
  //initialize sequencer
  Sequencer sequencer = new Sequencer();
  //joystick control  
  JoystickControl jstk; // will be initialized separately
  //table data
  static DefaultTableModel data = new DefaultTableModel();
  // Filefunctions
  FileFunctions fl = new FileFunctions();

  public void setSpeed(double val)
  {
    sequencer.Start();
    sequencer.setVord(val);
  }

  /** Creates new form CarControlCenter */
  public CarControlCenter(MotorControl motorControl, ServoControl servoControl, Accelerometer accelerometer)
  {
    initComponents();

    CurrentDevices.setMotorControl(motorControl);
    CurrentDevices.setServoControl(servoControl);
    CurrentDevices.setAccelerometer(accelerometer);

    sequencer.setAvailable(false);
    maxs = CurrentDevices.maxSpeed;
    mins = CurrentDevices.minSpeed;
    maxa = CurrentDevices.maxAcceleration;
    mina = CurrentDevices.minAcceleration;
    maxp = CurrentDevices.maxPosition;
    minp = CurrentDevices.minPosition;
    centerp = (maxp + minp) / 2;

    speedSlider.setMaximum((int) maxs);
    speedSlider.setMinimum((int) mins);
    accelerationSlider.setMaximum((int) maxa);
    accelerationSlider.setMinimum((int) mina);
    accelerationSlider.setValue((int) maxa);
    positionSlider.setMaximum((int) maxp);
    positionSlider.setMinimum((int) minp);
    speedSlider.setValue(0);
    positionSlider.setValue(0);

    // Show reference curves
    sequencer.curves.showcurves(RefChart);

    // assign the tracers to the charts
    sequencer.setChart(chart1, chart2);

    // sequencer parameters
    sequencer.setInteliStopEnable(false);
    IntelistopCheck.setSelected(false);
    sequencer.setFilterEnable(true);
    FilterCheck.setSelected(true);

    // joystick timer    
    jstk = new JoystickControl(speedSlider, positionSlider, accelerationSlider);
    jstk.start();
    jstk.SetInfoLabel(jstklabel);    

    // intelistop slider
    IntelistopSlider.setMaximum(5);
    IntelistopSlider.setMinimum((int) sequencer.getSamplingTime());
    IntelistopSlider.setValue((int) sequencer.getIntelistopTime());
    IntelistopSlider.setToolTipText("Intelistop time");
    IntelistopLabel.setText(IntelistopSlider.getValue() + " ms.");
    IntelistopLabel.setToolTipText("Intelistop time");


    // settings checks by default
    OrderCheck.setSelected(true);
    FilterCheck.setSelected(true);
    FilterOrderCheck.setSelected(true);
    EncCheck.setSelected(true);
    AccYCheck.setSelected(true);
    SpeedYCheck.setSelected(true);
    ErrorCheck.setSelected(false);
    CheckTracers();

    //table
    data.addColumn("time (miliseconds)");
    data.addColumn("action");
    data.addColumn("value (%)");
    Table.setModel(data);
    sequencer.setScript(data);

    //combobox
    ComboBox.removeAllItems();
    ComboBox.addItem("0 Set Acceleration");
    ComboBox.addItem("1 Set Speed");
    ComboBox.addItem("2 Set Position");

    sequencer.setAvailable(true);

    //open settings file
    fillCombobox();
    OpenSettings();

  }

  public void AddRow(Object[] row)
  {
    data.addRow(row);
  }

  public void fillCombobox()
  {
    final File f = new File(fl.ProgramJoystickPresetsDir());
    File[] files = f.listFiles();
    if (files != null)
    {
      JoystickPresetComboBox.removeAllItems();
      for (int i = 0; i < files.length; i++)
      {
        if (files[i].getName().toLowerCase().endsWith(".joy"))
        {
          JoystickPresetComboBox.addItem(files[i].getName());
        }
      }
    }
  }

  private void CheckTracers()
  {
    sequencer.trace[0].setVisible(OrderCheck.isSelected());
    sequencer.trace[1].setVisible(FilterOrderCheck.isSelected());
    sequencer.trace[2].setVisible(ErrorCheck.isSelected());

    sequencer.trace[3].setVisible(AccXCheck.isSelected());
    sequencer.trace[4].setVisible(AccYCheck.isSelected());
    sequencer.trace[5].setVisible(AccZCheck.isSelected());

    sequencer.trace[6].setVisible(SpeedXCheck.isSelected());
    sequencer.trace[7].setVisible(SpeedYCheck.isSelected());
    sequencer.trace[8].setVisible(SpeedZCheck.isSelected());

    sequencer.trace[9].setVisible(EncCheck.isSelected());
  }

  public static double round(double d, int decimalPlace)
  {
    BigDecimal bd = new BigDecimal(Double.toString(d));
    bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
    return bd.doubleValue();
  }

  public void ClearData()
  {
    for (int i = data.getRowCount() - 1; i >= 0; i--)
    {
      data.removeRow(i);
    }
  }

  public void OpenSettings()
  {
    String filename = fl.ProgramHomeDir() + "/settings.info";
    File file = new File(filename);

    //Open Settings file
    if (file.exists() == true)
    {
      Object[] content = fl.OpenFile(filename).toArray();
      String line[];
      int vali = 0;
      boolean valb = false;
      String val;
      String id = "";
      Color valc = Color.BLACK;

      for (int i = 0; i <= content.length - 1; i++)
      {
        line = content[i].toString().split("=");
        if (line.length > 1)
        {

          id = line[0];
          val = line[1];

          try
          {
            vali = Integer.parseInt(line[1]);
          }
          catch (Exception ex)
          {
          }

          if (vali == 0)
          {
            valb = false;
          }
          else
          {
            valb = true;
          }

          if (val.contains("java.awt.Color") == true)
          {
            String[] comp = val.split(",");
            float hue, sat, bri;
            hue = Float.parseFloat(comp[0]);
            sat = Float.parseFloat(comp[1]);
            bri = Float.parseFloat(comp[2]);

            valc = Color.getHSBColor(hue, sat, bri);
          }

          //checkboxes state
          if (id.contentEquals("AccXCheck"))
          {
            AccXCheck.setSelected(valb);
          }
          if (id.contentEquals("AccYCheck"))
          {
            AccYCheck.setSelected(valb);
          }
          if (id.contentEquals("AccZCheck"))
          {
            AccZCheck.setSelected(valb);
          }

          if (id.contentEquals("SpeedXCheck"))
          {
            SpeedXCheck.setSelected(valb);
          }
          if (id.contentEquals("SpeedYCheck"))
          {
            SpeedYCheck.setSelected(valb);
          }
          if (id.contentEquals("SpeedZCheck"))
          {
            SpeedZCheck.setSelected(valb);
          }

          if (id.contentEquals("EncCheck"))
          {
            EncCheck.setSelected(valb);
          }

          if (id.contentEquals("OrderCheck"))
          {
            OrderCheck.setSelected(valb);
          }
          if (id.contentEquals("FilterOrderCheck"))
          {
            FilterOrderCheck.setSelected(valb);
          }
          if (id.contentEquals("ErrorCheck"))
          {
            ErrorCheck.setSelected(valb);
          }

          if (id.contentEquals("videogamemodeCheck"))
          {
            videogamemodeCheck.setSelected(valb);
            jstk.SetVideoGameMode(videogamemodeCheck.isSelected());
          }

          //colours

          if (id.contentEquals("chart_background_color"))
          {
            chart1.setBackground(valc);
          }
          if (id.contentEquals("chart_axis_color"))
          {
            chart1.setForeground(valc);
          }

          if (id.contentEquals("mandotrace_color"))
          {
            sequencer.trace[0].setColor(valc);
          }
          if (id.contentEquals("adapttrace_color"))
          {
            sequencer.trace[1].setColor(valc);
          }
          if (id.contentEquals("ErrTrace_color"))
          {
            sequencer.trace[2].setColor(valc);
          }

          if (id.contentEquals("AccTraceX_color"))
          {
            sequencer.trace[3].setColor(valc);
          }
          if (id.contentEquals("AccTraceY_color"))
          {
            sequencer.trace[4].setColor(valc);
          }
          if (id.contentEquals("AccTraceZ_color"))
          {
            sequencer.trace[5].setColor(valc);
          }

          if (id.contentEquals("SpeedTraceX_color"))
          {
            sequencer.trace[6].setColor(valc);
          }
          if (id.contentEquals("SpeedTraceY_color"))
          {
            sequencer.trace[7].setColor(valc);
          }
          if (id.contentEquals("SpeedTraceZ_color"))
          {
            sequencer.trace[8].setColor(valc);
          }

          if (id.contentEquals("EncTrace_color"))
          {
            sequencer.trace[9].setColor(valc);
          }

          //intelistop time slider
          if (id.contentEquals("IntelistopSlider"))
          {
            IntelistopSlider.setValue(vali);
          }

          //joystick last config file
          if (id.contentEquals("jstkFile"))
          {
            jstk.OpenConfigFile(val);
          }
        }

      }

    }


    //open acfw curve from program folder .jaseimov
    file = new File(fl.ProgramCurvesDir() + "/acfw.curve");
    if (file.exists() == true)
    {
      sequencer.curves.setacfw(sequencer.curves.OpenCurve(file));
    }

    //open dcfw curve from program folder .jaseimov
    file = new File(fl.ProgramCurvesDir() + "/dcfw.curve");
    if (file.exists() == true)
    {
      sequencer.curves.setdcfw(sequencer.curves.OpenCurve(file));
    }

    //open acbw curve from program folder .jaseimov
    file = new File(fl.ProgramCurvesDir() + "/acbw.curve");
    if (file.exists() == true)
    {
      sequencer.curves.setacbw(sequencer.curves.OpenCurve(file));
    }

    //open dcbw curve from program folder .jaseimov
    file = new File(fl.ProgramCurvesDir() + "/dcbw.curve");
    if (file.exists() == true)
    {
      sequencer.curves.setdcbw(sequencer.curves.OpenCurve(file));
    }


    sequencer.curves.showcurves(RefChart);

  }

  public void SaveSettings()
  {

    String filename = fl.ProgramHomeDir() + "/settings.info";
    File file = new File(filename);
    ArrayList<String> content = new ArrayList<String>();
    int val = 0;
    Color col;
    // checkboxes state
    if (AccXCheck.isSelected() == false)
    {
      val = 0;
    }
    else
    {
      val = 1;
    }
    content.add("AccXCheck=" + val);

    if (AccYCheck.isSelected() == false)
    {
      val = 0;
    }
    else
    {
      val = 1;
    }
    content.add("AccYCheck=" + val);

    if (AccZCheck.isSelected() == false)
    {
      val = 0;
    }
    else
    {
      val = 1;
    }
    content.add("AccZCheck=" + val);

    if (SpeedXCheck.isSelected() == false)
    {
      val = 0;
    }
    else
    {
      val = 1;
    }
    content.add("SpeedXCheck=" + val);

    if (SpeedYCheck.isSelected() == false)
    {
      val = 0;
    }
    else
    {
      val = 1;
    }
    content.add("SpeedYCheck=" + val);

    if (SpeedZCheck.isSelected() == false)
    {
      val = 0;
    }
    else
    {
      val = 1;
    }
    content.add("SpeedZCheck=" + val);

    if (EncCheck.isSelected() == false)
    {
      val = 0;
    }
    else
    {
      val = 1;
    }
    content.add("EncCheck=" + val);

    if (OrderCheck.isSelected() == false)
    {
      val = 0;
    }
    else
    {
      val = 1;
    }
    content.add("OrderCheck=" + val);

    if (FilterOrderCheck.isSelected() == false)
    {
      val = 0;
    }
    else
    {
      val = 1;
    }
    content.add("FilterOrderCheck=" + val);

    if (ErrorCheck.isSelected() == false)
    {
      val = 0;
    }
    else
    {
      val = 1;
    }
    content.add("ErrorCheck=" + val);

    if (videogamemodeCheck.isSelected() == false)
    {
      val = 0;
    }
    else
    {
      val = 1;
    }
    content.add("videogamemodeCheck=" + val);

    //colours
    /*
    col=chart1.getBackground();
    content.add("chart_background_color="+
    col.getAlpha()+","+
    col.getBlue()+","+
    col.getGreen()+","+
    col.getRed());

    content.add("chart_axis_color="+chart1.getForeground().getRGB());

    content.add("mandotrace_color="+sequencer.mandotrace.getColor().getRGB());
    content.add("adapttrace_color="+sequencer.adapttrace.getColor().getRGB());
    content.add("ErrTrace_color="+sequencer.ErrTrace.getColor().getRGB());

    content.add("AccTraceX_color="+sequencer.AccTraceX.getColor().getRGB());
    content.add("AccTraceY_color="+sequencer.AccTraceY.getColor().getRGB());
    content.add("AccTraceZ_color="+sequencer.AccTraceZ.getColor().getRGB());

    content.add("SpeedTraceX_color="+sequencer.SpeedTraceX.getColor().getRGB());
    content.add("SpeedTraceY_color="+sequencer.SpeedTraceY.getColor().getRGB());
    content.add("SpeedTraceZ_color="+sequencer.SpeedTraceZ.getColor().getRGB());

    content.add("EncTrace_color="+sequencer.EncTrace.getColor().getRGB());
     */

    // intelistop time slider
    content.add("IntelistopSlider=" + IntelistopSlider.getValue());

    // last joystick config file
    content.add("jstkFile=" + jstk.jstkFile.getAbsolutePath());


    System.out.println("[CarControlCenter]:size:" + content.size());
    fl.SaveFile(content, file);

  }

  public void sendOrder(int val)
  {

    Object[] row = new Object[3];
    int pos = 0;


    if (val == 0)
    {//acceleration
      pos = accelerationSlider.getValue();
      CurrentDevices.setMotorControlAcceleration((double) pos);
      AccelerationLabel.setText("Acceleration: " + round(100 * pos / (maxa - mina), 0) + "%");
    }

    if (val == 1)
    {//speed
      pos = speedSlider.getValue();
      sequencer.setVord((double) pos);
      if (pos >= 0)
      {
        if (pos == 0)
        {
          SpeedLabel.setText("Speed: [Stopped]");
        }
        else
        {
          SpeedLabel.setText("Speed: " + pos + "% [FWD]");
        }
      }
      else
      {
        SpeedLabel.setText("Speed: " + -1 * pos + "% [BWD]");
      }
    }

    if (val == 2)
    {//position
      pos = positionSlider.getValue();
      CurrentDevices.setServoPosition(pos);
      if (pos >= centerp)
      {
        if (pos == centerp)
        {
          PositionLabel.setText("Direction: [Center]");
        }
        else
        {
          PositionLabel.setText("Direction: " + round(100 * (centerp - pos) / (maxp - centerp), 0) + "% [R]");
        }
      }
      else
      {
        PositionLabel.setText("Direction: " + round(100 * (pos - centerp) / (centerp - minp), 0) + "% [L]");
      }
    }

    if (rec == true)
    {
      row[0] = sequencer.getSequencertime();
      row[1] = ComboBox.getSelectedItem().toString().substring(0, 1);
      row[2] = pos;
      AddRow(row);
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

    MainTabbedPanel = new javax.swing.JTabbedPane();
    ControlValuesPanel = new javax.swing.JPanel();
    ControlPanel = new javax.swing.JPanel();
    ControlSubPanel1 = new javax.swing.JPanel();
    AccelerationLabel = new javax.swing.JLabel();
    accelerationSlider = new javax.swing.JSlider();
    SpeedLabel = new javax.swing.JLabel();
    speedSlider = new javax.swing.JSlider();
    PositionLabel = new javax.swing.JLabel();
    positionSlider = new javax.swing.JSlider();
    FilterCheck = new javax.swing.JCheckBox();
    IntelistopCheck = new javax.swing.JCheckBox();
    ControlSubPanel2 = new javax.swing.JPanel();
    SaveButton = new javax.swing.JButton();
    ClearButton = new javax.swing.JButton();
    jPanel9 = new javax.swing.JPanel();
    RunToggle = new javax.swing.JToggleButton();
    RecToggle = new javax.swing.JToggleButton();
    StopPanel = new javax.swing.JPanel();
    StopButton = new javax.swing.JButton();
    chart1 = new info.monitorenter.gui.chart.Chart2D();
    CarValuesPanel = new javax.swing.JPanel();
    chart2 = new info.monitorenter.gui.chart.Chart2D();
    ProgramPanel = new javax.swing.JPanel();
    jToolBar1 = new javax.swing.JToolBar();
    ExecuteButton = new javax.swing.JButton();
    OpenScriptButton = new javax.swing.JButton();
    SaveScriptButton = new javax.swing.JButton();
    ComboBox = new javax.swing.JComboBox();
    AddOrderButton = new javax.swing.JButton();
    QuitOrderButton = new javax.swing.JButton();
    ClearTableButton = new javax.swing.JButton();
    jScrollPane1 = new javax.swing.JScrollPane();
    Table = new javax.swing.JTable();
    SettingsPanel = new javax.swing.JPanel();
    SettingsSubPanel = new javax.swing.JTabbedPane();
    JoystickSettingsPanel = new javax.swing.JPanel();
    jLabel1 = new javax.swing.JLabel();
    JoystickPresetComboBox = new javax.swing.JComboBox();
    jPanel11 = new javax.swing.JPanel();
    jstklabel = new javax.swing.JLabel();
    videogamemodeCheck = new javax.swing.JCheckBox();
    ControlValuesSettingsPanel = new javax.swing.JPanel();
    OrderCheck = new javax.swing.JCheckBox();
    FilterOrderCheck = new javax.swing.JCheckBox();
    IntelistopSlider = new javax.swing.JSlider();
    IntelistopLabel = new javax.swing.JLabel();
    ErrorCheck = new javax.swing.JCheckBox();
    DrawCarValuesToogle = new javax.swing.JToggleButton();
    CarValuesSettingsPanel = new javax.swing.JPanel();
    AccXCheck = new javax.swing.JCheckBox();
    AccYCheck = new javax.swing.JCheckBox();
    AccZCheck = new javax.swing.JCheckBox();
    SpeedZCheck = new javax.swing.JCheckBox();
    SpeedYCheck = new javax.swing.JCheckBox();
    SpeedXCheck = new javax.swing.JCheckBox();
    EncCheck = new javax.swing.JCheckBox();
    ReferenceCurvesSettingsPanel = new javax.swing.JPanel();
    RefChart = new info.monitorenter.gui.chart.Chart2D();
    ShowReferencesButton = new javax.swing.JButton();
    LoadReferencesButton = new javax.swing.JButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(java.awt.event.WindowEvent evt) {
        formWindowClosing(evt);
      }
    });

    ControlValuesPanel.setLayout(new java.awt.BorderLayout());

    ControlPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Controls"));
    ControlPanel.setPreferredSize(new java.awt.Dimension(220, 282));
    ControlPanel.setLayout(new java.awt.GridLayout(0, 1));

    ControlSubPanel1.setLayout(new javax.swing.BoxLayout(ControlSubPanel1, javax.swing.BoxLayout.Y_AXIS));

    AccelerationLabel.setText("Acceleration");
    ControlSubPanel1.add(AccelerationLabel);

    accelerationSlider.setPaintLabels(true);
    accelerationSlider.setPaintTicks(true);
    accelerationSlider.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        accelerationSliderStateChanged(evt);
      }
    });
    ControlSubPanel1.add(accelerationSlider);

    SpeedLabel.setText("Speed");
    ControlSubPanel1.add(SpeedLabel);

    speedSlider.setPaintLabels(true);
    speedSlider.setPaintTicks(true);
    speedSlider.setValue(0);
    speedSlider.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        speedSliderStateChanged(evt);
      }
    });
    ControlSubPanel1.add(speedSlider);

    PositionLabel.setText("Position");
    ControlSubPanel1.add(PositionLabel);

    positionSlider.setPaintLabels(true);
    positionSlider.setPaintTicks(true);
    positionSlider.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        positionSliderStateChanged(evt);
      }
    });
    ControlSubPanel1.add(positionSlider);

    FilterCheck.setText("Filter");
    FilterCheck.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        FilterCheckActionPerformed(evt);
      }
    });
    ControlSubPanel1.add(FilterCheck);

    IntelistopCheck.setText("InteliStop");
    IntelistopCheck.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        IntelistopCheckActionPerformed(evt);
      }
    });
    ControlSubPanel1.add(IntelistopCheck);

    ControlPanel.add(ControlSubPanel1);

    SaveButton.setText("Save data");
    SaveButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        SaveButtonActionPerformed(evt);
      }
    });

    ClearButton.setText("Reset");
    ClearButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        ClearButtonActionPerformed(evt);
      }
    });

    jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder("Program"));

    RunToggle.setText("Run");
    RunToggle.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        RunToggleActionPerformed(evt);
      }
    });

    RecToggle.setText("Rec");
    RecToggle.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        RecToggleActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
    jPanel9.setLayout(jPanel9Layout);
    jPanel9Layout.setHorizontalGroup(
      jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
        .addComponent(RunToggle, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(RecToggle, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
    );
    jPanel9Layout.setVerticalGroup(
      jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel9Layout.createSequentialGroup()
        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(RunToggle)
          .addComponent(RecToggle))
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    javax.swing.GroupLayout ControlSubPanel2Layout = new javax.swing.GroupLayout(ControlSubPanel2);
    ControlSubPanel2.setLayout(ControlSubPanel2Layout);
    ControlSubPanel2Layout.setHorizontalGroup(
      ControlSubPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(SaveButton, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
      .addComponent(ClearButton, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
      .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );
    ControlSubPanel2Layout.setVerticalGroup(
      ControlSubPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(ControlSubPanel2Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(SaveButton)
        .addGap(7, 7, 7)
        .addComponent(ClearButton)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(86, Short.MAX_VALUE))
    );

    ControlPanel.add(ControlSubPanel2);

    ControlValuesPanel.add(ControlPanel, java.awt.BorderLayout.LINE_START);

    StopPanel.setLayout(new java.awt.CardLayout());
    ControlValuesPanel.add(StopPanel, java.awt.BorderLayout.PAGE_START);

    StopButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jaseimov/client/images/stop.png"))); // NOI18N
    StopButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        StopButtonActionPerformed(evt);
      }
    });
    ControlValuesPanel.add(StopButton, java.awt.BorderLayout.PAGE_END);

    chart1.setBackground(new java.awt.Color(102, 102, 102));
    chart1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
    chart1.setForeground(new java.awt.Color(255, 255, 255));
    chart1.setFont(chart1.getFont());
    chart1.setPreferredSize(new java.awt.Dimension(518, 300));
    chart1.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        chart1MouseClicked(evt);
      }
    });

    javax.swing.GroupLayout chart1Layout = new javax.swing.GroupLayout(chart1);
    chart1.setLayout(chart1Layout);
    chart1Layout.setHorizontalGroup(
      chart1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 393, Short.MAX_VALUE)
    );
    chart1Layout.setVerticalGroup(
      chart1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 504, Short.MAX_VALUE)
    );

    ControlValuesPanel.add(chart1, java.awt.BorderLayout.CENTER);

    MainTabbedPanel.addTab("Control values", ControlValuesPanel);

    CarValuesPanel.setLayout(new java.awt.BorderLayout());

    chart2.setBackground(new java.awt.Color(102, 102, 102));
    chart2.setForeground(new java.awt.Color(255, 255, 255));

    javax.swing.GroupLayout chart2Layout = new javax.swing.GroupLayout(chart2);
    chart2.setLayout(chart2Layout);
    chart2Layout.setHorizontalGroup(
      chart2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 641, Short.MAX_VALUE)
    );
    chart2Layout.setVerticalGroup(
      chart2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 574, Short.MAX_VALUE)
    );

    CarValuesPanel.add(chart2, java.awt.BorderLayout.CENTER);

    MainTabbedPanel.addTab("Car values", CarValuesPanel);

    ProgramPanel.setLayout(new java.awt.BorderLayout());

    jToolBar1.setFloatable(false);
    jToolBar1.setRollover(true);

    ExecuteButton.setText("Run");
    ExecuteButton.setFocusable(false);
    ExecuteButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    ExecuteButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    ExecuteButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        ExecuteButtonActionPerformed(evt);
      }
    });
    jToolBar1.add(ExecuteButton);

    OpenScriptButton.setText("Open");
    OpenScriptButton.setFocusable(false);
    OpenScriptButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    OpenScriptButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    OpenScriptButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        OpenScriptButtonActionPerformed(evt);
      }
    });
    jToolBar1.add(OpenScriptButton);

    SaveScriptButton.setText("Save");
    SaveScriptButton.setFocusable(false);
    SaveScriptButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    SaveScriptButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    SaveScriptButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        SaveScriptButtonActionPerformed(evt);
      }
    });
    jToolBar1.add(SaveScriptButton);

    ComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
    jToolBar1.add(ComboBox);

    AddOrderButton.setText("Add");
    AddOrderButton.setFocusable(false);
    AddOrderButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    AddOrderButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    AddOrderButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        AddOrderButtonActionPerformed(evt);
      }
    });
    jToolBar1.add(AddOrderButton);

    QuitOrderButton.setText("Quit");
    QuitOrderButton.setFocusable(false);
    QuitOrderButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    QuitOrderButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    QuitOrderButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        QuitOrderButtonActionPerformed(evt);
      }
    });
    jToolBar1.add(QuitOrderButton);

    ClearTableButton.setText("Clear");
    ClearTableButton.setFocusable(false);
    ClearTableButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    ClearTableButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    ClearTableButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        ClearTableButtonActionPerformed(evt);
      }
    });
    jToolBar1.add(ClearTableButton);

    ProgramPanel.add(jToolBar1, java.awt.BorderLayout.PAGE_START);

    Table.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {
        {null, null, null, null},
        {null, null, null, null},
        {null, null, null, null},
        {null, null, null, null}
      },
      new String [] {
        "Title 1", "Title 2", "Title 3", "Title 4"
      }
    ));
    jScrollPane1.setViewportView(Table);

    ProgramPanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

    MainTabbedPanel.addTab("Program", ProgramPanel);

    SettingsPanel.setLayout(new java.awt.BorderLayout());

    JoystickSettingsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Joystick"));

    jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jaseimov/client/images/mando.png"))); // NOI18N

    JoystickPresetComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
    JoystickPresetComboBox.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        JoystickPresetComboBoxActionPerformed(evt);
      }
    });

    jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder("Button pressed info"));
    jPanel11.setLayout(new java.awt.BorderLayout());

    jstklabel.setText("...");
    jPanel11.add(jstklabel, java.awt.BorderLayout.CENTER);

    videogamemodeCheck.setText("Videogame mode");
    videogamemodeCheck.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        videogamemodeCheckActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout JoystickSettingsPanelLayout = new javax.swing.GroupLayout(JoystickSettingsPanel);
    JoystickSettingsPanel.setLayout(JoystickSettingsPanelLayout);
    JoystickSettingsPanelLayout.setHorizontalGroup(
      JoystickSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(JoystickSettingsPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel1)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(JoystickSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
          .addComponent(JoystickPresetComboBox, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(videogamemodeCheck, javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jPanel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap(317, Short.MAX_VALUE))
    );
    JoystickSettingsPanelLayout.setVerticalGroup(
      JoystickSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(JoystickSettingsPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(JoystickSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(JoystickSettingsPanelLayout.createSequentialGroup()
            .addGap(7, 7, 7)
            .addComponent(JoystickPresetComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(videogamemodeCheck)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addComponent(jLabel1))
        .addContainerGap(366, Short.MAX_VALUE))
    );

    SettingsSubPanel.addTab("Joystick", JoystickSettingsPanel);

    ControlValuesSettingsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Control values"));

    OrderCheck.setText("Master order");
    OrderCheck.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        OrderCheckMouseClicked(evt);
      }
    });
    OrderCheck.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        OrderCheckActionPerformed(evt);
      }
    });

    FilterOrderCheck.setText("Filtered order");
    FilterOrderCheck.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        FilterOrderCheckMouseClicked(evt);
      }
    });
    FilterOrderCheck.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        FilterOrderCheckActionPerformed(evt);
      }
    });

    IntelistopSlider.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        IntelistopSliderStateChanged(evt);
      }
    });

    IntelistopLabel.setText("jLabel1");

    ErrorCheck.setText("Filtered signal - error");
    ErrorCheck.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        ErrorCheckMouseClicked(evt);
      }
    });
    ErrorCheck.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        ErrorCheckActionPerformed(evt);
      }
    });

    DrawCarValuesToogle.setText("Draw car values");
    DrawCarValuesToogle.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        DrawCarValuesToogleActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout ControlValuesSettingsPanelLayout = new javax.swing.GroupLayout(ControlValuesSettingsPanel);
    ControlValuesSettingsPanel.setLayout(ControlValuesSettingsPanelLayout);
    ControlValuesSettingsPanelLayout.setHorizontalGroup(
      ControlValuesSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(ControlValuesSettingsPanelLayout.createSequentialGroup()
        .addGap(14, 14, 14)
        .addGroup(ControlValuesSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(ErrorCheck)
          .addComponent(OrderCheck)
          .addComponent(FilterOrderCheck)
          .addComponent(IntelistopLabel)
          .addComponent(IntelistopSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(DrawCarValuesToogle, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)))
    );
    ControlValuesSettingsPanelLayout.setVerticalGroup(
      ControlValuesSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(ControlValuesSettingsPanelLayout.createSequentialGroup()
        .addGap(9, 9, 9)
        .addGroup(ControlValuesSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(ControlValuesSettingsPanelLayout.createSequentialGroup()
            .addGap(40, 40, 40)
            .addComponent(ErrorCheck))
          .addComponent(OrderCheck)
          .addGroup(ControlValuesSettingsPanelLayout.createSequentialGroup()
            .addGap(20, 20, 20)
            .addComponent(FilterOrderCheck))
          .addGroup(ControlValuesSettingsPanelLayout.createSequentialGroup()
            .addGap(60, 60, 60)
            .addComponent(IntelistopLabel)))
        .addGap(2, 2, 2)
        .addComponent(IntelistopSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(5, 5, 5)
        .addComponent(DrawCarValuesToogle))
    );

    SettingsSubPanel.addTab("Control Values", ControlValuesSettingsPanel);

    CarValuesSettingsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Car values"));

    AccXCheck.setText("Accelerometer (X)");
    AccXCheck.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        AccXCheckMouseClicked(evt);
      }
    });
    AccXCheck.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        AccXCheckActionPerformed(evt);
      }
    });

    AccYCheck.setText("Accelerometer (Y)");
    AccYCheck.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        AccYCheckMouseClicked(evt);
      }
    });
    AccYCheck.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        AccYCheckActionPerformed(evt);
      }
    });

    AccZCheck.setText("Accelerometer (Z)");
    AccZCheck.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        AccZCheckMouseClicked(evt);
      }
    });
    AccZCheck.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        AccZCheckActionPerformed(evt);
      }
    });

    SpeedZCheck.setText("Integrated speed (Z)");
    SpeedZCheck.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        SpeedZCheckMouseClicked(evt);
      }
    });
    SpeedZCheck.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        SpeedZCheckActionPerformed(evt);
      }
    });

    SpeedYCheck.setText("Integrated speed (Y)");
    SpeedYCheck.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        SpeedYCheckMouseClicked(evt);
      }
    });
    SpeedYCheck.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        SpeedYCheckActionPerformed(evt);
      }
    });

    SpeedXCheck.setText("Integrated speed (X)");
    SpeedXCheck.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        SpeedXCheckMouseClicked(evt);
      }
    });
    SpeedXCheck.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        SpeedXCheckActionPerformed(evt);
      }
    });

    EncCheck.setText("Encoder speed (Y)");
    EncCheck.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        EncCheckMouseClicked(evt);
      }
    });
    EncCheck.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        EncCheckActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout CarValuesSettingsPanelLayout = new javax.swing.GroupLayout(CarValuesSettingsPanel);
    CarValuesSettingsPanel.setLayout(CarValuesSettingsPanelLayout);
    CarValuesSettingsPanelLayout.setHorizontalGroup(
      CarValuesSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(CarValuesSettingsPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(CarValuesSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(AccXCheck)
          .addComponent(AccYCheck)
          .addComponent(AccZCheck))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addGroup(CarValuesSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(CarValuesSettingsPanelLayout.createSequentialGroup()
            .addComponent(SpeedXCheck)
            .addComponent(EncCheck))
          .addComponent(SpeedZCheck)
          .addComponent(SpeedYCheck))
        .addContainerGap(169, Short.MAX_VALUE))
    );
    CarValuesSettingsPanelLayout.setVerticalGroup(
      CarValuesSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(CarValuesSettingsPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(CarValuesSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(SpeedXCheck)
          .addComponent(EncCheck)
          .addGroup(CarValuesSettingsPanelLayout.createSequentialGroup()
            .addComponent(AccXCheck)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(CarValuesSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(AccYCheck)
              .addComponent(SpeedYCheck))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(CarValuesSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(AccZCheck)
              .addComponent(SpeedZCheck))))
        .addGap(211, 211, 211))
    );

    SettingsSubPanel.addTab("Car Values", CarValuesSettingsPanel);

    ReferenceCurvesSettingsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Reference curves"));
    ReferenceCurvesSettingsPanel.setLayout(new java.awt.BorderLayout());

    RefChart.setBackground(new java.awt.Color(238, 238, 238));
    RefChart.setAutoscrolls(true);

    javax.swing.GroupLayout RefChartLayout = new javax.swing.GroupLayout(RefChart);
    RefChart.setLayout(RefChartLayout);
    RefChartLayout.setHorizontalGroup(
      RefChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 613, Short.MAX_VALUE)
    );
    RefChartLayout.setVerticalGroup(
      RefChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 446, Short.MAX_VALUE)
    );

    ReferenceCurvesSettingsPanel.add(RefChart, java.awt.BorderLayout.CENTER);

    ShowReferencesButton.setText("References");
    ShowReferencesButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        ShowReferencesButtonActionPerformed(evt);
      }
    });
    ReferenceCurvesSettingsPanel.add(ShowReferencesButton, java.awt.BorderLayout.PAGE_START);

    LoadReferencesButton.setText("Load References");
    LoadReferencesButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        LoadReferencesButtonActionPerformed(evt);
      }
    });
    ReferenceCurvesSettingsPanel.add(LoadReferencesButton, java.awt.BorderLayout.PAGE_END);

    SettingsSubPanel.addTab("Reference curves", ReferenceCurvesSettingsPanel);

    SettingsPanel.add(SettingsSubPanel, java.awt.BorderLayout.CENTER);

    MainTabbedPanel.addTab("Settings", SettingsPanel);

    getContentPane().add(MainTabbedPanel, java.awt.BorderLayout.CENTER);

    pack();
  }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
      // Closing
      //SaveSettings();
      //inmediately stop
      sequencer.setFilterEnable(false);
      sequencer.setVord(0);
      sequencer.TimerKill();
    }//GEN-LAST:event_formWindowClosing

    private void StopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_StopButtonActionPerformed
      // Stop
      speedSlider.setValue(0);
      positionSlider.setValue(0);
      sequencer.setVord(0);
    }//GEN-LAST:event_StopButtonActionPerformed

    private void videogamemodeCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_videogamemodeCheckActionPerformed
      // videogame mode check
      jstk.SetVideoGameMode(videogamemodeCheck.isSelected());
    }//GEN-LAST:event_videogamemodeCheckActionPerformed

    private void JoystickPresetComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JoystickPresetComboBoxActionPerformed
      // set joystick preset
      if (JoystickPresetComboBox.getItemCount() > 0)
      {
        System.out.println("[CarControlCenter]:" + JoystickPresetComboBox.getSelectedItem().toString());
        jstk.OpenConfigFile(fl.ProgramJoystickPresetsDir() + "/" + JoystickPresetComboBox.getSelectedItem().toString());
      }
}//GEN-LAST:event_JoystickPresetComboBoxActionPerformed

    private void EncCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EncCheckActionPerformed
      // TODO add your handling code here:
      CheckTracers();
}//GEN-LAST:event_EncCheckActionPerformed

    private void EncCheckMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_EncCheckMouseClicked
      // EncCheck select colour
      if (evt.getButton() == 3)
      {
        Color col = JColorChooser.showDialog(EncCheck, "Colour", EncCheck.getBackground());
        EncCheck.setBackground(col);
        EncCheck.setToolTipText(AccZCheck.getText());
        sequencer.trace[9].setColor(col);
      }
}//GEN-LAST:event_EncCheckMouseClicked

    private void SpeedXCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SpeedXCheckActionPerformed
      // TODO add your handling code here:
      CheckTracers();
}//GEN-LAST:event_SpeedXCheckActionPerformed

    private void SpeedXCheckMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SpeedXCheckMouseClicked
      // SpeedXCheck select colour
      if (evt.getButton() == 3)
      {
        Color col = JColorChooser.showDialog(SpeedXCheck, "Colour", SpeedXCheck.getBackground());
        SpeedXCheck.setBackground(col);
        SpeedXCheck.setToolTipText(SpeedXCheck.getText());
        sequencer.trace[6].setColor(col);
      }
}//GEN-LAST:event_SpeedXCheckMouseClicked

    private void SpeedYCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SpeedYCheckActionPerformed
      // TODO add your handling code here:
      CheckTracers();
}//GEN-LAST:event_SpeedYCheckActionPerformed

    private void SpeedYCheckMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SpeedYCheckMouseClicked
      // SpeedYCheck select colour
      if (evt.getButton() == 3)
      {
        Color col = JColorChooser.showDialog(SpeedYCheck, "Colour", SpeedYCheck.getBackground());
        SpeedYCheck.setBackground(col);
        SpeedYCheck.setToolTipText(SpeedYCheck.getText());
        sequencer.trace[7].setColor(col);
      }
}//GEN-LAST:event_SpeedYCheckMouseClicked

    private void SpeedZCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SpeedZCheckActionPerformed
      // TODO add your handling code here:
      CheckTracers();
}//GEN-LAST:event_SpeedZCheckActionPerformed

    private void SpeedZCheckMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SpeedZCheckMouseClicked
      // SpeedZCheck select colour
      if (evt.getButton() == 3)
      {
        Color col = JColorChooser.showDialog(SpeedZCheck, "Colour", SpeedZCheck.getBackground());
        SpeedZCheck.setBackground(col);
        SpeedZCheck.setToolTipText(SpeedZCheck.getText());
        sequencer.trace[8].setColor(col);
      }
}//GEN-LAST:event_SpeedZCheckMouseClicked

    private void AccZCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AccZCheckActionPerformed
      // TODO add your handling code here:
      CheckTracers();
}//GEN-LAST:event_AccZCheckActionPerformed

    private void AccZCheckMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_AccZCheckMouseClicked
      // AccYCheck select colour
      if (evt.getButton() == 3)
      {
        Color col = JColorChooser.showDialog(AccZCheck, "Colour", AccZCheck.getBackground());
        AccZCheck.setBackground(col);
        AccZCheck.setToolTipText(AccZCheck.getText());
        sequencer.trace[5].setColor(col);
      }
}//GEN-LAST:event_AccZCheckMouseClicked

    private void AccYCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AccYCheckActionPerformed
      // TODO add your handling code here:
      CheckTracers();
}//GEN-LAST:event_AccYCheckActionPerformed

    private void AccYCheckMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_AccYCheckMouseClicked
      // AccYCheck select colour
      if (evt.getButton() == 3)
      {
        Color col = JColorChooser.showDialog(AccYCheck, "Colour", AccYCheck.getBackground());
        AccYCheck.setBackground(col);
        AccYCheck.setToolTipText(AccYCheck.getText());
        sequencer.trace[4].setColor(col);
      }
}//GEN-LAST:event_AccYCheckMouseClicked

    private void AccXCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AccXCheckActionPerformed
      // TODO add your handling code here:
      CheckTracers();
}//GEN-LAST:event_AccXCheckActionPerformed

    private void AccXCheckMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_AccXCheckMouseClicked
      // AccXCheck select colour
      if (evt.getButton() == 3)
      {
        Color col = JColorChooser.showDialog(AccXCheck, "Colour", AccXCheck.getBackground());
        AccXCheck.setBackground(col);
        AccXCheck.setToolTipText(AccXCheck.getText());
        sequencer.trace[3].setColor(col);
      }
}//GEN-LAST:event_AccXCheckMouseClicked

    private void LoadReferencesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoadReferencesButtonActionPerformed
      //Load references from file ac and dc

      File file = null;
      double[][] curve;

      file = fl.OpenDialog("Acceleration curve [FW]");
      if (file == null)
      {
      }
      else
      {
        curve = sequencer.curves.OpenCurve(file);
        sequencer.curves.setacfw(curve); //set acceleration curve from file
        //save the opened curve to the "last used" curve file
        file = new File(fl.ProgramCurvesDir() + "/acfw.curve");
        sequencer.curves.SaveCurve(file, curve);
      }


      file = fl.OpenDialog("Decceleration curve [FW]");
      if (file == null)
      {
      }
      else
      {
        curve = sequencer.curves.OpenCurve(file);
        sequencer.curves.setdcfw(curve); //set acceleration curve from file
        //save the opened curve to the "last used" curve file
        file = new File(fl.ProgramCurvesDir() + "/dcfw.curve");
        sequencer.curves.SaveCurve(file, curve);
      }


      file = fl.OpenDialog("Acceleration curve [BW]");
      if (file == null)
      {
      }
      else
      {
        curve = sequencer.curves.OpenCurve(file);
        sequencer.curves.setacbw(curve); //set acceleration curve from file
        //save the opened curve to the "last used" curve file
        file = new File(fl.ProgramCurvesDir() + "/acbw.curve");
        sequencer.curves.SaveCurve(file, curve);
      }


      file = fl.OpenDialog("Decceleration curve [FW]");
      if (file == null)
      {
      }
      else
      {
        curve = sequencer.curves.OpenCurve(file);
        sequencer.curves.setdcbw(curve); //set acceleration curve from file
        //save the opened curve to the "last used" curve file
        file = new File(fl.ProgramCurvesDir() + "/dcbw.curve");
        sequencer.curves.SaveCurve(file, curve);
      }

      sequencer.curves.showcurves(RefChart);
}//GEN-LAST:event_LoadReferencesButtonActionPerformed

    private void ShowReferencesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ShowReferencesButtonActionPerformed
      // Show reference curves
      sequencer.curves.showcurves(RefChart);
}//GEN-LAST:event_ShowReferencesButtonActionPerformed

    private void DrawCarValuesToogleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DrawCarValuesToogleActionPerformed
      //Draw car values on the main chart

      if (DrawCarValuesToogle.isSelected() == true)
      {
        chart1.addTrace(sequencer.trace[3]);
        chart1.addTrace(sequencer.trace[4]);
        chart1.addTrace(sequencer.trace[5]);

        chart1.addTrace(sequencer.trace[6]);
        chart1.addTrace(sequencer.trace[7]);
        chart1.addTrace(sequencer.trace[8]);

        chart1.addTrace(sequencer.trace[9]);
      }
      else
      {
        chart1.removeTrace(sequencer.trace[3]);
        chart1.removeTrace(sequencer.trace[4]);
        chart1.removeTrace(sequencer.trace[5]);

        chart1.removeTrace(sequencer.trace[6]);
        chart1.removeTrace(sequencer.trace[7]);
        chart1.removeTrace(sequencer.trace[8]);

        chart1.removeTrace(sequencer.trace[9]);
      }
}//GEN-LAST:event_DrawCarValuesToogleActionPerformed

    private void ErrorCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ErrorCheckActionPerformed
      // TODO add your handling code here:
      CheckTracers();
}//GEN-LAST:event_ErrorCheckActionPerformed

    private void ErrorCheckMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ErrorCheckMouseClicked
      // ErrorCheck select colour
      if (evt.getButton() == 3)
      {
        Color col = JColorChooser.showDialog(ErrorCheck, "Colour", ErrorCheck.getBackground());
        ErrorCheck.setBackground(col);
        ErrorCheck.setToolTipText(ErrorCheck.getText());
        sequencer.trace[2].setColor(col);
      }
}//GEN-LAST:event_ErrorCheckMouseClicked

    private void IntelistopSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_IntelistopSliderStateChanged
      // Intelistop time change
      JSlider slider = (JSlider) evt.getSource();
      int pos = slider.getValue();
      sequencer.setIntelistopTime(pos);
      IntelistopLabel.setText(pos + " s.");
}//GEN-LAST:event_IntelistopSliderStateChanged

    private void FilterOrderCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FilterOrderCheckActionPerformed
      // Filter order check
      CheckTracers();
}//GEN-LAST:event_FilterOrderCheckActionPerformed

    private void FilterOrderCheckMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_FilterOrderCheckMouseClicked
      // FilterOrderCheck select colour
      if (evt.getButton() == 3)
      {
        Color col = JColorChooser.showDialog(FilterOrderCheck, "Colour", FilterOrderCheck.getBackground());
        FilterOrderCheck.setBackground(col);
        FilterOrderCheck.setToolTipText(FilterOrderCheck.getText());
        sequencer.trace[1].setColor(col);
      }
}//GEN-LAST:event_FilterOrderCheckMouseClicked

    private void OrderCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OrderCheckActionPerformed
      // marter order check
      CheckTracers();
}//GEN-LAST:event_OrderCheckActionPerformed

    private void OrderCheckMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_OrderCheckMouseClicked
      // OrderCheck select colour
      if (evt.getButton() == 3)
      {
        Color col = JColorChooser.showDialog(OrderCheck, "Colour", OrderCheck.getBackground());
        OrderCheck.setBackground(col);
        OrderCheck.setToolTipText(OrderCheck.getText());
        sequencer.trace[0].setColor(col);
      }
}//GEN-LAST:event_OrderCheckMouseClicked

    private void ClearTableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClearTableButtonActionPerformed
      // clear program
      ClearData();
}//GEN-LAST:event_ClearTableButtonActionPerformed

    private void QuitOrderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_QuitOrderButtonActionPerformed
      // remove selected orders from the program
      int i = data.getRowCount() - 1;
      while (i >= 0)
      {
        if (Table.isRowSelected(i) == true)
        {
          data.removeRow(i);
        }
        else
        {
          i--;
        }
      }
    }//GEN-LAST:event_QuitOrderButtonActionPerformed

    private void AddOrderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddOrderButtonActionPerformed
      // Add row
      Object[] row = new Object[3];

      row[0] = 0.0;
      row[1] = ComboBox.getSelectedItem().toString().substring(0, 1);
      row[2] = 0.0;
      AddRow(row);
}//GEN-LAST:event_AddOrderButtonActionPerformed

    private void SaveScriptButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveScriptButtonActionPerformed
      // save program
      final JFileChooser fc = new JFileChooser();
      fc.showSaveDialog(chart1);
      fc.setName("Save tracers");
      File file = fc.getSelectedFile();

      try
      {
        BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsolutePath()));

        //format time, Ordered speed, Filtered Speed, Received Speed from the car
        for (int i = 0; i < data.getRowCount(); i++)
        {
          bw.write(data.getValueAt(i, 0) + "," + data.getValueAt(i, 1) + "," + data.getValueAt(i, 2) + "\n");
        }

        bw.close();
      }
      catch (IOException ex)
      {
      }
}//GEN-LAST:event_SaveScriptButtonActionPerformed

    private void OpenScriptButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OpenScriptButtonActionPerformed
      // open file to table
      final JFileChooser fc = new JFileChooser();
      fc.showOpenDialog(chart1);
      fc.setName("Open Program");
      File file = fc.getSelectedFile();
      Object[] inpt = fl.OpenFile(file.getAbsolutePath()).toArray();
      String[] vals;

      ClearData();
      for (int i = 0; i < inpt.length; i++)
      {
        vals = inpt[i].toString().split(",");
        data.addRow(vals);
      }
    }//GEN-LAST:event_OpenScriptButtonActionPerformed

    private void ExecuteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExecuteButtonActionPerformed
      // Enable script
      RunToggle.setSelected(true);
}//GEN-LAST:event_ExecuteButtonActionPerformed

    private void chart1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_chart1MouseClicked
      // chart1 select colour
      if (evt.getButton() == 3)
      {
        Color col = JColorChooser.showDialog(chart1, "Chart background colour", chart1.getBackground());
        chart1.setBackground(col);
        col = JColorChooser.showDialog(chart1, "Chart Axis colour", chart1.getForeground());
        chart1.setForeground(col);
      }
}//GEN-LAST:event_chart1MouseClicked

    private void RecToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RecToggleActionPerformed
      // Rec toggle
      JToggleButton tb = (JToggleButton) evt.getSource();
      rec = tb.isSelected();
}//GEN-LAST:event_RecToggleActionPerformed

    private void RunToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RunToggleActionPerformed
      // Run toggle
      JToggleButton tb = (JToggleButton) evt.getSource();

      if (tb.isSelected() == true)
      {
        IntelistopCheck.setSelected(false);
        sequencer.setInteliStopEnable(false);
        sequencer.setScriptEnable(true);
        sequencer.reset();
        sequencer.Start();
      }
      else
      {
        sequencer.setScriptEnable(false);
      }
}//GEN-LAST:event_RunToggleActionPerformed

    private void ClearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClearButtonActionPerformed
      // Reset
      speedSlider.setValue((int) mins);
      sequencer.reset();
}//GEN-LAST:event_ClearButtonActionPerformed

    private void SaveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveButtonActionPerformed
      // Save data
      sequencer.SaveData();
}//GEN-LAST:event_SaveButtonActionPerformed

    private void IntelistopCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_IntelistopCheckActionPerformed
      // set intelistop on/off
      JCheckBox cb = (JCheckBox) evt.getSource();
      sequencer.setInteliStopEnable(cb.isSelected());
}//GEN-LAST:event_IntelistopCheckActionPerformed

    private void FilterCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FilterCheckActionPerformed
      // Set speed filter on/off
      JCheckBox cb = (JCheckBox) evt.getSource();
      sequencer.setFilterEnable(cb.isSelected());
}//GEN-LAST:event_FilterCheckActionPerformed

    private void positionSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_positionSliderStateChanged
      // set position
      sendOrder(2);
}//GEN-LAST:event_positionSliderStateChanged

    private void speedSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_speedSliderStateChanged
      // set speed (send the speed order to the filter: sequencer)
      sendOrder(1);
}//GEN-LAST:event_speedSliderStateChanged

    private void accelerationSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_accelerationSliderStateChanged
      // set Acceleration
      sendOrder(0);
}//GEN-LAST:event_accelerationSliderStateChanged
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JCheckBox AccXCheck;
  private javax.swing.JCheckBox AccYCheck;
  private javax.swing.JCheckBox AccZCheck;
  private javax.swing.JLabel AccelerationLabel;
  private javax.swing.JButton AddOrderButton;
  private javax.swing.JPanel CarValuesPanel;
  private javax.swing.JPanel CarValuesSettingsPanel;
  private javax.swing.JButton ClearButton;
  private javax.swing.JButton ClearTableButton;
  private javax.swing.JComboBox ComboBox;
  private javax.swing.JPanel ControlPanel;
  private javax.swing.JPanel ControlSubPanel1;
  private javax.swing.JPanel ControlSubPanel2;
  private javax.swing.JPanel ControlValuesPanel;
  private javax.swing.JPanel ControlValuesSettingsPanel;
  private javax.swing.JToggleButton DrawCarValuesToogle;
  private javax.swing.JCheckBox EncCheck;
  private javax.swing.JCheckBox ErrorCheck;
  private javax.swing.JButton ExecuteButton;
  private javax.swing.JCheckBox FilterCheck;
  private javax.swing.JCheckBox FilterOrderCheck;
  private javax.swing.JCheckBox IntelistopCheck;
  private javax.swing.JLabel IntelistopLabel;
  private javax.swing.JSlider IntelistopSlider;
  private javax.swing.JComboBox JoystickPresetComboBox;
  private javax.swing.JPanel JoystickSettingsPanel;
  private javax.swing.JButton LoadReferencesButton;
  private javax.swing.JTabbedPane MainTabbedPanel;
  private javax.swing.JButton OpenScriptButton;
  private javax.swing.JCheckBox OrderCheck;
  private javax.swing.JLabel PositionLabel;
  private javax.swing.JPanel ProgramPanel;
  private javax.swing.JButton QuitOrderButton;
  private javax.swing.JToggleButton RecToggle;
  private info.monitorenter.gui.chart.Chart2D RefChart;
  private javax.swing.JPanel ReferenceCurvesSettingsPanel;
  private javax.swing.JToggleButton RunToggle;
  private javax.swing.JButton SaveButton;
  private javax.swing.JButton SaveScriptButton;
  private javax.swing.JPanel SettingsPanel;
  private javax.swing.JTabbedPane SettingsSubPanel;
  private javax.swing.JButton ShowReferencesButton;
  private javax.swing.JLabel SpeedLabel;
  private javax.swing.JCheckBox SpeedXCheck;
  private javax.swing.JCheckBox SpeedYCheck;
  private javax.swing.JCheckBox SpeedZCheck;
  private javax.swing.JButton StopButton;
  private javax.swing.JPanel StopPanel;
  private javax.swing.JTable Table;
  private javax.swing.JSlider accelerationSlider;
  private info.monitorenter.gui.chart.Chart2D chart1;
  private info.monitorenter.gui.chart.Chart2D chart2;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JPanel jPanel11;
  private javax.swing.JPanel jPanel9;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JToolBar jToolBar1;
  private javax.swing.JLabel jstklabel;
  private javax.swing.JSlider positionSlider;
  private javax.swing.JSlider speedSlider;
  private javax.swing.JCheckBox videogamemodeCheck;
  // End of variables declaration//GEN-END:variables
}
