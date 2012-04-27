package jaseimov.client.controlcarB.filter;

import jaseimov.client.controlcarB.CurrentDevices;
import jaseimov.lib.devices.DeviceException;
import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.traces.Trace2DLtd;
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.table.TableModel;

public class Sequencer
{
  //timer vars
  Timer timer = new Timer();
  private MyTask tarea = new MyTask(); // timer generic task, iside we put the real task
  private boolean frozen; // indicates if the sequencer timer is paused or not
  public boolean scheduled = false; //indicates if the timer has a task already
  // global vars
  private double t0 = System.currentTimeMillis();//initial time
  private double t = 0.0; //current time
  private double tact = 0.0; // currect relative time (relative to t0)
  private double vact = 0.0; //actual speed
  private double vord = 0.0; //ordered speed
  private double vact_real = 0.0; //actual speed (filtred speed)
  private double vord_real = 0.0; //ordered speed
  private double vcorr_real = 0.0; // corrected filtered speed
  private double breakval = 0.0; //motor-break value
  private int breaksign = 0;
  private int cu; //curve used
  private double breakcoef = 0.0; //breaking coefficient
  double[][] acfw; //acceleration curve going forward
  double[][] dcfw; //decceleration curve going forward
  double[][] acbw; //acceleration curve going backwards
  double[][] dcbw; //decceleration curve going backward
  private double wheelratio = 0.0325; // in meters
  private double maxspeed = 100.0; //maximum speed for the car in m/s
  private double dt = 0.0; //sampling time in ms.
  private int valroll = 0; //maximum number of values to show at a time in the chart
  private int is_counter = 0; //intelistop counter
  private double error = 0.0; //defined as speed that the program sends minus speed that the program reads from the instruments
  // main tracers (let's have them public...)
  public ITrace2D[] trace = new Trace2DLtd[10];
  //curr data
  private double[] currdata = new double[11];
  private String currdatas;
  //Array attached to the tracers for data export
  ArrayList<String> arr = new ArrayList<String>();
  int stepCount = 0;
  //enables
  boolean enableFiltering = false;
  boolean enableAccelerationSpeed = true;
  boolean intelistop = false;
  boolean errorcontrol = true;
  boolean SequencerAvailable = false;
  boolean breakAvailable = false;
  double intelistoptime = 2.0; // 2000 ms, 2 seconds by default
  //car readings
  double[] acel =
  {
    0, 0, 0
  };  //aceleración del acelerometro
  double[] vacel = new double[3]; //velocidad del acelerometro
  double venc = 0.0;
  //Script
    /*The script format will be:
  column 0: time
  column 1: action {set acceleration, set speed, set position}
  column 2: value
   */
  TableModel script;
  boolean followScript = false;
  int ScriptIndex = 0;
  //reference curves module (is better to have it public)
  public Curves curves;
  // next step module
  private NextStep nxst;
  //convertions module
  private Convertions cnv = new Convertions();
  //charts
  private Chart2D chart1;
  private Chart2D chart2;

  // constructor
  public Sequencer()
  {

    //Initialize curves
    try
    {
      curves = new Curves();
    }
    catch (UnsupportedEncodingException ex)
    {
      Logger.getLogger(Sequencer.class.getName()).log(Level.SEVERE, null, ex);
    }


    // is better to use the recomended sampling time(dt) from the curves
    dt = curves.getRecomendedCurveStep();


    //set the valroll according to the dt
    valroll = 10000;
    // load the curves
    acfw = curves.getacfw();
    dcfw = curves.getdcfw();
    acbw = curves.getacbw();
    dcbw = curves.getdcbw();

    //initialize nextstep module
    nxst = new NextStep(acfw, dcfw, acbw, dcbw, dt);

    //initialize the traces
    trace[0] = new Trace2DLtd(valroll, "Master order (m/s)");
    trace[1] = new Trace2DLtd(valroll, "Filtered order (m/s)");
    trace[2] = new Trace2DLtd(valroll, "Corrected speed (m/s)");

    trace[3] = new Trace2DLtd(valroll, "Car Acceleration(X) (m/s^2)");
    trace[4] = new Trace2DLtd(valroll, "Car Acceleration(Y) (m/s^2)");
    trace[5] = new Trace2DLtd(valroll, "Car Acceleration(Z) (m/s^2)");

    trace[6] = new Trace2DLtd(valroll, "Car Speed(X) (m/s)");
    trace[7] = new Trace2DLtd(valroll, "Car Speed(Y) (m/s)");
    trace[8] = new Trace2DLtd(valroll, "Car Speed(Z) (m/s)");

    trace[9] = new Trace2DLtd(valroll, "Encoder speed (m/s)");

    // set an arbitrary maximum real speed
    maxspeed = 100;

    //trace colours
    trace[0].setColor(Color.BLUE);
    trace[1].setColor(Color.ORANGE);
    trace[2].setColor(Color.red);

    trace[3].setColor(Color.CYAN);
    trace[4].setColor(Color.black);
    trace[5].setColor(Color.ORANGE);

    trace[6].setColor(Color.GREEN);
    trace[7].setColor(Color.blue);
    trace[8].setColor(Color.pink);

    trace[9].setColor(Color.white);

    //add the header of the array to save data
    String s = "time;";
    for (int i = 0; i < trace.length; i++)
    {
      s = s + trace[i].getLabel() + ";";
    }
    arr.add(s);

  } // constructor

  public void Execute_Step() throws DeviceException
  {
    t = System.currentTimeMillis();//set current time to compare it with t0
    tact = (t - t0) / 1000.0; // in seconds

    /*Check the script*/
    if (followScript == true)
    {
      double t1 = tact - dt / 2.0;
      double t2 = tact + dt / 2.0;
      //System.out.println("scriptindex:"+ScriptIndex+"/"+script.getRowCount()+" t1:"+t1+" t2:"+t2);
      double ts = Double.parseDouble(script.getValueAt(ScriptIndex, 0).toString());
      int action = Integer.parseInt(script.getValueAt(ScriptIndex, 1).toString());
      double value = Double.parseDouble(script.getValueAt(ScriptIndex, 2).toString());

      if (ts >= t1 && ts < t2)
      {
        System.out.println("[Sequencer]:Script Step:" + ScriptIndex + " " + ts
                + "(t=" + (t - t0) + "): " + action + " : " + value);

        if (ScriptIndex < script.getRowCount() - 1)
        {
          ScriptIndex++;
        }
        else
        {
          ScriptIndex = 0;
        }

        if (action == 0)
        {//set acceleration
          CurrentDevices.setMotorControlAcceleration(value);
          System.out.println("[Sequencer]:Script order: set acceleration: "
                  + value + " order:" + ScriptIndex);
        }
        if (action == 1)
        {//set speed
          setVord(value);
          System.out.println("[Sequencer]:Script: set speed: "
                  + value + " order:" + ScriptIndex);
        }
        if (action == 2)
        {//set position
          CurrentDevices.setServoPosition((int) value);
          System.out.println("[Sequencer]:Script: set position: "
                  + value + " order:" + ScriptIndex);
        }
        if (action == 4)
        {//begin
          ScriptIndex = 0;
          reset();
          System.out.println("[Sequencer]:Script: begin,"
                  + " order:" + ScriptIndex);
        }
        if (action == 5)
        {//end
          followScript = false;
          System.out.println("[Sequencer]:Script: end,"
                  + " order:" + ScriptIndex);
        }

      }
    }
    //end of script part


    /*main filtering process*/
    //vord is a value from -100 to 100, it is modified later inside setMotorControlSpeed
    if (enableFiltering == true)
    {
      vord_real = vord * maxspeed / 100.0;
      vact_real = nxst.next_step(vact_real, vord_real); // in m/s
      vact = vact_real / maxspeed * 100.0; //in %
    }
    else
    { //if not filtering, send the raw signal
      vact = vord;
      vact_real = vord_real;
    }
    // end of filtering process


    /* acceleration process */
    if (enableAccelerationSpeed == true)
    { // get the acceleration, and from it calculate the speed
      acel = CurrentDevices.getAccelerometerValues();
      vacel = cnv.a2v(acel[0], acel[1], acel[2]); //we have an absolute speed vector in m/s
    }


    /* get Encoder speed */
    venc = CurrentDevices.getEncoderSpeed() * wheelratio;
    //we have an absolute speed in m/s (coincident in direction with vacel[1])



    /* InteliStop */
    if (intelistop == true)
    {
      if (vact == vord)
      {
        is_counter++;
      }
      //if its 'intelistoptime' miliseconds in the same position, then stop the clock
      if (is_counter > intelistoptime / dt)
      {
        Pause();
        is_counter = 0;
      }
    }

    //compute the error
    error = absolute(vact_real - vacel[1]);
    if (errorcontrol == true)
    {
      vcorr_real = vact_real - error; // in m/s
    }

    // convert the speed (m/s) desired to % of motor control


    // add points to the tracers
    trace[0].addPoint(tact, vord_real);
    trace[1].addPoint(tact, vact_real);
    trace[2].addPoint(tact, vcorr_real);

    trace[3].addPoint(tact, acel[0]);
    trace[4].addPoint(tact, acel[1]);
    trace[5].addPoint(tact, acel[2]);

    trace[6].addPoint(tact, vacel[0]);
    trace[7].addPoint(tact, vacel[1]);
    trace[8].addPoint(tact, vacel[2]);

    trace[9].addPoint(tact, venc);

    //save data in the array
    currdata[0] = tact;
    currdata[1] = vord_real;
    currdata[2] = vact_real;
    currdata[3] = vcorr_real;
    currdata[4] = acel[0];
    currdata[5] = acel[1];
    currdata[6] = acel[2];
    currdata[7] = vacel[0];
    currdata[8] = vacel[1];
    currdata[9] = vacel[2];
    currdata[10] = venc;
    currdatas = "";
    for (int i = 0; i < currdata.length; i++)
    {
      currdatas = currdatas + String.valueOf(currdata[i]) + ";";
    }
    arr.add(currdatas);
    stepCount++;

  }
  // internal class that represents a task
  class MyTask extends TimerTask
  {
    public void run()
    {

      if (frozen == false)
      { //code to do at intervals
        try
        {
          //code to do at intervals
          Execute_Step();
        }
        catch (DeviceException ex)
        {
          Logger.getLogger(Sequencer.class.getName()).log(Level.SEVERE, null, ex);
        }
      }

    }// end run()
  }

  public void Start()
  { //schedules and starts the timer, so the sequencer
    frozen = false;
    // we assign a task to the timer
    if (SequencerAvailable == true)
    {
      if (scheduled == false)
      {
        double period = dt * 1000.0; // the period must be in ms, but dt is in seconds...
        timer.schedule(tarea, 0, Math.round(period)); //we need to set the clock frequency to dt (period)
        nxst.setdt(dt);
        maxspeed = curves.getMaxVal(acfw);
        System.out.println("[Sequencer]:maxspeed:" + maxspeed + " m/s");
        System.out.println("[Sequencer]:Clock Started, frequency:" + Math.round(period) + " ms");
        scheduled = true;
      }
    }
  }// end Start

  public void Pause()
  { //pauses the timer
    System.out.println("[Sequencer]:Sequencer Clock Paused");
    frozen = true;
  }// end Stop

  public void TimerKill()
  {
    int purge = timer.purge();
    timer.cancel();
  }

  public void SetRecomended_dt()
  {
    dt = curves.getGlobalMinimumCurveStep();
    System.out.println("[Sequencer]:SetRecomended_dt: recomended dt: " + dt);
  }

  public void setAvailable(boolean val)
  {
    SequencerAvailable = val;
    System.out.println("[Sequencer]:Sequencer available:" + val);
  }

  public void setCurvesModule(Curves val)
  {
    curves = val;
    // load the curves
    acfw = curves.getacfw();
    dcfw = curves.getdcfw();
    acbw = curves.getacbw();
    dcbw = curves.getdcbw();
    nxst.setacfw(acfw);
    nxst.setdcfw(dcfw);
    nxst.setacbw(acbw);
    nxst.setdcbw(dcbw);
    nxst.setdt(curves.getRecomendedCurveStep());
  }

  public void setScript(TableModel val)
  {
    script = val;
  }

  public void setScriptEnable(Boolean val)
  {
    followScript = val;
    ScriptIndex = 0;
    System.out.println("[Sequencer]:Script: " + val);
  }

  public void setScriptIndex(int index)
  {
    ScriptIndex = index;
  }

  public double getSequencertime()
  {
    return System.currentTimeMillis() - t0;
  }

  public void setChart(Chart2D val1, Chart2D val2)
  {
    chart1 = val1;
    chart2 = val2;
    //asign traces
    chart1.addTrace(trace[0]);
    chart1.addTrace(trace[1]);
    chart1.addTrace(trace[2]);

    chart2.addTrace(trace[3]);
    chart2.addTrace(trace[4]);
    chart2.addTrace(trace[5]);

    chart2.addTrace(trace[6]);
    chart2.addTrace(trace[7]);
    chart2.addTrace(trace[8]);

    chart2.addTrace(trace[9]);
  }

  public void reset()
  { //delete all data, and reset time
    clearTraces();
    t0 = System.currentTimeMillis();
    arr.clear();
    ScriptIndex = 0;
  }

  public void clearTraces()
  {
    for (int i = 0; i < trace.length; i++)
    {
      trace[i].removeAllPoints();
    }
  }

  public void setVord(double val)
  {
    vord = val;
    System.out.println("[Sequencer]:New order to motor control: " + vord + "%");
    Start();
    t = System.currentTimeMillis();
  }

  public double absolute(double val)
  {
    if (val < 0)
    {
      val = val * -1;
    }
    return val;
  }

  public double getVact()
  {
    return vact;
  }

  public void set_t0(double val)
  {
    t0 = val;
  }

  public double get_t0()
  {
    return t0;
  }

  //public void setAccelerationCurve(double[][] accelcurv){ac=accelcurv;}
  //public void setDecelerationCurve(double[][] decelcurv){dc=decelcurv;}
  public double getMaxSpeed()
  {
    return maxspeed;
  }

  //public double[][] getACcurve(){return model.getac();}
  //public double[][] getDCcurve(){return model.getdc();}
  public Chart2D getChart1()
  {
    return chart1;
  }

  public Chart2D getChart2()
  {
    return chart2;
  }

  public Curves getCurvesModule()
  {
    return curves;
  }

  public void setFilterEnable(boolean val)
  {
    enableFiltering = val;
    System.out.println("[Sequencer]:Filter enabled:" + val);
  }

  public boolean getFilterEnable()
  {
    return enableFiltering;
  }

  public void setAccelerationProcessEnable(boolean val)
  {
    enableAccelerationSpeed = val;
    System.out.println("[Sequencer]:Acceleration process enabled:" + val);
  }

  public void SetBreakAvailable(boolean val)
  {
    breakAvailable = val;
  }

  public void setInteliStopEnable(boolean val)
  {
    intelistop = val;
    if (val == false)
    {
      Start();
    }
    else
    {
      is_counter = 0;
    }
    System.out.println("[Sequencer]:InteliStop enabled:" + val);
  }

  public void setIntelistopTime(int val)
  {
    intelistoptime = val;
  }

  public void setSamplingTime(int val)
  {
    dt = val;
  }

  public double getSamplingTime()
  {
    return dt;
  }

  public double getIntelistopTime()
  {
    return intelistoptime;
  }

  public void SaveData()
  {
    //Create a file chooser
    final JFileChooser fc = new JFileChooser();
    fc.showSaveDialog(chart1);
    fc.setName("Save tracers");
    File file = fc.getSelectedFile();

    System.out.println("[Sequencer]:Saving data in: " + file.getName() + "," + arr.lastIndexOf(arr));

    try
    {
      BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsolutePath()));

      //format time, Ordered speed, Filtered Speed, Received Speed from the car
      //bw.write("time,ordered speed,filtered speed,readed speed"); //file header
      for (int i = 0; i <= stepCount - 1; i++)
      {
        bw.write(arr.get(i).toString() + "\n");
        //System.out.println(arr.get(i));
      }

      bw.close();
    }
    catch (IOException ex)
    {
    }
    System.out.println("[Sequencer]:End of data save");
  }
}
