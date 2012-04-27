package jaseimov.server.autocontrol;

import jaseimov.lib.concurrent.PeriodicTaskRunner;
import jaseimov.lib.devices.Accelerometer;
import jaseimov.lib.devices.Axis;
import jaseimov.lib.devices.DeviceException;
import jaseimov.lib.devices.MotorControl;
import jaseimov.server.autocontrol.filters.Filter;
import java.rmi.RemoteException;

public class AutomaticControl
{
  // Axis to read Acceleration
  private final static int ACCELERATION_AXIS = Axis.Y_AXIS;

  private Convertions cnv = new Convertions();  

  private MotorControl motor;
  private Accelerometer accelerometer;
  private Filter filter;  

  private AutomaticControlTask task = new AutomaticControlTask();
  private PeriodicTaskRunner runner;  

  // Desired velocity in m/s
  private volatile double autoControlVelocity = 0.;

  // Last calculated velocity from acceleration in m/s
  private volatile double velocityFromAccel = 0.;  

  public AutomaticControl(MotorControl motor, Accelerometer accelerometer, Filter filter, long frequency)
  {
    this.motor = motor;
    this.accelerometer = accelerometer;
    this.filter = filter;
    runner = new PeriodicTaskRunner(task, frequency);
  }

  public boolean isEnabled()
  {
    return runner.istarted();
  }

  public void setEnabled(boolean enabled)
  {
    if(enabled)
    {
      filter.reset();
      runner.start();
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

  public void setAutoControlVelocity(double v)
  {
    autoControlVelocity = v;
  }

  public double getVelocityFromAccel()
  {
    return velocityFromAccel;
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

  /**   
   * Calculates how many percent must be increased the motor
   * @param accSpeed Speed coming from the accelerometer acceleration in m/s   
   * @return percent of motor control to use
   */
  private double calculateSpeed(double accSpeed, double ctrlSpeed)
  {    
    double error = 0.0;
    double relative_error = 0.0;
    double inc = 0.0;
    double sign;
    
    if(ctrlSpeed >= 0)
      sign = 1;
    else
      sign = -1;

    error = ctrlSpeed - accSpeed;
    relative_error = Math.abs(error / ctrlSpeed);
    
    if (relative_error > 0.0 && relative_error <= 0.3)
    {
      inc = 1.0;
    }
    else if(relative_error > 0.3 && relative_error <= 0.6)
    {
      inc = 2.0;
    }
    else if(relative_error > 0.6 && relative_error <= 0.8)
    {
      inc = 5.0;
    }
    else if(relative_error > 0.8 && relative_error <= 1.0)
    {
      inc = 10.0;
    }

    if (error >= 0.0)
    {
      inc = (sign * inc);
    }
    else
    {
      inc = -(sign * inc);
    }

    return inc;
  }

  private class AutomaticControlTask implements Runnable
  {
    public void run()
    {
      try
      {
        double autoControlVelocity = motor.getAutoControlVelocity();
        if(autoControlVelocity != 0)
        {
            // Acceleration from accelerometer
            double a = accelerometer.getAcceleration()[ACCELERATION_AXIS];
            // Velocity calc with acceleration (uses filter)
            velocityFromAccel = accel2Speed(a);

            // Calc increment with error between wanted speed and real speed readed from accelerometer
            double incrementSpeed = calculateSpeed(velocityFromAccel, autoControlVelocity);
            double currentMotorPercent = motor.getVelocity();

            // Adjust motor percents to max and min allowed velocity values
            double newMotorPercent = currentMotorPercent + incrementSpeed;
            if(newMotorPercent > motor.getMaxVelocity())
              newMotorPercent = motor.getMaxVelocity();
            else if(newMotorPercent < motor.getMinVelocity())
              newMotorPercent = motor.getMinVelocity();

            motor.setVelocity(newMotorPercent);
            System.out.println(
                    "a: " + a
                    + " a2speed " + velocityFromAccel
                    + " increment % " + incrementSpeed
                    + " current % " + currentMotorPercent
                    + " objectiveSpeed " + autoControlVelocity);
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
