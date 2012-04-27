package jaseimov.server.autocontrol;

import jaseimov.lib.concurrent.PeriodicTaskRunner;
import jaseimov.lib.devices.Accelerometer;
import jaseimov.lib.devices.Axis;
import jaseimov.lib.devices.Device;
import jaseimov.lib.devices.DeviceException;
import jaseimov.lib.devices.Encoder;
import jaseimov.lib.devices.MotorControl;
import jaseimov.server.autocontrol.filters.Filter;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AutomaticControl
{
  // Axis to read Acceleration
  private final static int ACCELERATION_AXIS = Axis.Y_AXIS;
  private Convertions cnv = new Convertions();
  private MotorControl motor;
  private Encoder encoder;
  private Accelerometer accelerometer;
  private Filter filter;
  private AutomaticControlTask task = new AutomaticControlTask();
  private PeriodicTaskRunner runner;
  // Desired velocity in m/s
  private volatile double autoControlVelocity = 0.;
  // Last raw velocity
  private volatile double rawVelocity = 0.;
  // Last filtered velocity
  private volatile double filteredVelocity = 0.;
  //last time in ms
  private double lastTime = 0.0;
  // current time in ms
  private double t = 0.0;
  // time increment in seconds
  private double dt = 0.0;
  // Calculate speed globa vars
  double LastReceivedOrder = 0.0;
  double LastRegulationInc = 0.0;
  double encoderConvertion = 0.0;
  // motor limits
  double upperlimit = 0.0;
  double lowerlimit = 0.0;
  int maxpower = 0;
  int minpower = 0;
  // switchable inner limits
  double uplimit = 0.0;
  double downlimit = 0.0;
  // Acceleration security control
  double accel=0.0;
  int accindex=0;
  double accsum=0.0;
  double[] accvec=new double[4];
  // desired order
  double desiredOrder=0.0;
  // Emergergency state
  boolean emergencyState=false;
  boolean hitted=false;
  double acelhit=4.0;


  public AutomaticControl(MotorControl motor, Device encoder, Device accelerometer, Filter filter, long frequency)
  {
    try
    {
      this.motor = motor;
      this.encoder = (Encoder) encoder;
      this.accelerometer = (Accelerometer) accelerometer;
      this.filter = filter;
      runner = new PeriodicTaskRunner(task, frequency);
      encoderConvertion = this.encoder.getCmPerTic();
      upperlimit = motor.getUpperLimit();
      lowerlimit = motor.getLowerLimit();
      maxpower = (int) (motor.getMaxVelocity() * upperlimit / 100);
      minpower = (int) (motor.getMinVelocity() * upperlimit / 100);
      /*System.out.println("Pmin: " + lowerlimit + ", Pmax: " + upperlimit
              + ", lowerlimit:" + lowerlimit + "; upperlimit:" + upperlimit);*/
      
    }
    catch (RemoteException ex)
    {
      Logger.getLogger(AutomaticControl.class.getName()).log(Level.SEVERE, null, ex);
    }
    catch (DeviceException ex)
    {
      Logger.getLogger(AutomaticControl.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public boolean isEnabled()
  {
    return runner.istarted();
  }

  public void setEnabled(boolean enabled)
  {
    if (enabled)
    {
      filter.reset();
      runner.start();
      lastTime = System.currentTimeMillis();
    }
    else
    {
      runner.stop();
    }
  }

  public double getAutoControlVelocity()
  {
    return autoControlVelocity;
  }

  public void setHitAccelerationMaximum(double val){
    acelhit=val;
    //System.out.println("Hit:"+val);
  }

  public double getHitAccelerationMaximum(){
    return acelhit;
  }

  public void setAutoControlVelocity(double v)
  {
    autoControlVelocity = v;
  }

  public void setDesiredOrder(double v){
      desiredOrder=v;
  }

  public boolean getEnergencyState(){return emergencyState;}

  public double getVelocityRaw()
  {
    return rawVelocity;
  }

  public double getVelocityFiltered()
  {
    return filteredVelocity;
  }
  // for the zero condition of accel2Speed
  private int zeroCounter = 0;
  private final static double epsylon = 0.01; // m/s²

  /**
   * Converts acceleration coming from the accelerometer to a speed using a filter
   * @param acc Acceleration coming from the sensor   
   * @return speed, filtered or not depending on the case
   */
  private double accel2Speed(double acc)
  {
    /* aditionally, we need to check for the zero condition dinamically
    so we will count the times that acceleration is between a recovering of
    zero  (-epsylon < acc < epsylon) where epsylon is a value close to zero

    if the acceleration is in the epsylon interval, let's say 10 times,
    then we can say the car is stopped and we need to ensure the zero
    condition for the speed convertion.*/

    double spd = 0.0;

    // TODO Convert units from G to m/s² if needed
    // acc=acc/g; // g = 9.81 m/s²

    // zero condition counter
    if (acc >= (-1 * epsylon) && acc <= epsylon)
    {
      zeroCounter++;
    }
    else
    {
      zeroCounter = 0;
    }

    if (zeroCounter == 10)
    {
      //reset values
      filter.reset();
      cnv.setv0(0.0);
    }

    spd = filter.filter(cnv.a2v(acc));

    return spd;
  }

  public double accumulativeAverage(double newval){

    accsum-=accvec[accindex];
    accvec[accindex]=newval;

    accsum+=newval;

    accindex+=1;
    if (accindex>accvec.length){
      accindex=0;
    }
    return accsum/(double)accvec.length;
  }

  /**   
   * Calculates how many percent must be sent to the motor
   * @param InstrumentsReading Speed coming from the instruments in m/s
   * @param ReceivedOrder: Order from the system controller in m/s
   * @param motorPower: Percent of the motorcontrol power
   * @return percent of motor control to use
   */
  private double calculateSpeed(double instrumentsReading, double receivedOrder, double motorPower)
  {
    long finalControllerPower = 0;
    double controllerPower =0.0;
    double vmax=1.2178;
    double error=receivedOrder-instrumentsReading;

    double A=(100.0*receivedOrder/vmax);
    double B=(100.0*error/vmax);

   controllerPower = A;
    

    // out
    /*
    System.out.println("[calculateSpeed] Order: " + receivedOrder
            + ", encoder:" + instrumentsReading + " m/s"
            + ", maxinc:" + maxinc
            + ", Automatic Reg: Inc:" + RegulationInc + " %"
            + ", outPower: " + controllerPower + " %");

*/
    // convert the double to int for the controller.
    LastReceivedOrder = receivedOrder;
    finalControllerPower = Math.round(controllerPower);

    return finalControllerPower;
  }

  private class AutomaticControlTask implements Runnable
  {
    public void run()
    {
      try
      {
        double autoControlVelocity = motor.getAutoControlVelocity();
        double motorPower=motor.getVelocity();
        
        //time actualization
        t = System.currentTimeMillis();
        dt = (t - lastTime) / 1000.0;
        lastTime = t;
        //System.out.println("dt:"+dt);

        // Check hits to determine emergencystate
          double[] acc = accelerometer.getAcceleration();
          
          if((Math.abs(acc[1])>acelhit ||Math.abs(acc[1])>acelhit || Math.abs(acc[2]-9.81)>acelhit)){
            hitted=true;
          }else{
            hitted=false;
          }

          //System.out.println("Acel avg:"+acc[0]+", "+acc[1]+", "+(acc[2]-9.81));
          if (hitted==true && desiredOrder==0.0){
            emergencyState=true;
          }

          if (desiredOrder!=0.0){
            emergencyState=false;
          }
          // end of emergency check

        if (dt>0.0){
          // Encoder velocity
          rawVelocity = encoder.getTics() * encoderConvertion / dt;
          filteredVelocity = filter.filter(rawVelocity);
        }

        if (dt>0.0 && emergencyState==false){                 

          // get new regulation motor control power
          double MotorPercent = calculateSpeed(filteredVelocity, autoControlVelocity, motorPower);

          // limit speed to upperlimit (ej: 75%)
          if (MotorPercent > maxpower)
          {
            MotorPercent = maxpower;
          }
          else if (MotorPercent < minpower)
          {
            MotorPercent = minpower;
          }
          
          motor.setUseVelocityLimits(true);
          motor.setVelocity(MotorPercent);
          
        }

        if (emergencyState==true){
          //System.out.println("[EMERGENCY STATE]Sent to motor control: "+0.0+ "%");
          motor.setVelocity(0.0);
        }
        
      }
      catch (RemoteException ex)
      {
        ex.printStackTrace();
      }
      catch (DeviceException ex)
      {
        ex.printStackTrace();
      }
    }
  }
}
