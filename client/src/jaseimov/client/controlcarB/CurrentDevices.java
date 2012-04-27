package jaseimov.client.controlcarB;

import jaseimov.lib.devices.Accelerometer;
import jaseimov.lib.devices.DeviceException;
import jaseimov.lib.devices.Encoder;
import jaseimov.lib.devices.MotorControl;
import jaseimov.lib.devices.ServoControl;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CurrentDevices
{

  /*This class stores and distributes the loaded devices*/
  //motor control
  static MotorControl motor;
  //Servo control
  static ServoControl servo;
  //Acelerometer
  static Accelerometer accel;
  //Encoder
  static Encoder enc;
  //limits
  public static double maxSpeed;
  public static double minSpeed;
  public static double maxAcceleration;
  public static double minAcceleration;
  public static double maxPosition;
  public static double minPosition;
  //motor limits
    /*explanation (example):
  the electric engine may start with the 30% of the controller speed
  and burns if the controller speed is higher than 88%
  then we must set those limits according to our calculations*/
  public static int motor_upper_limit = 100;
  public static int motor_lower_limit = 14;
  //convertion vars
  public static double acv = 0.01; //to convert cm/s^2 to m/s^2
  public static int ticsinthewheel = 12; //according to the ASEIMOV I

  public static void setAccelerationConvertionValue(double val)
  {
    acv = val;
    //System.out.println("[CurrentDevices]:Acceleration convertion value:" +acv);
  }

  public static void setTicsinthewheel(int val)
  {
    ticsinthewheel = val;
    //System.out.println("[CurrentDevices]:Encoder wheel tics:" +ticsinthewheel);
  }

  public static void setMotorControl(MotorControl val)
  {
    motor = val;
    try
    {
      maxSpeed = motor.getMaxVelocity();
      minSpeed = motor.getMinVelocity();
      maxAcceleration = motor.getMaxAcceleration();
      minAcceleration = motor.getMinAcceleration();
    }
    catch (RemoteException ex)
    {
      Logger.getLogger(CurrentDevices.class.getName()).log(Level.SEVERE, null, ex);
    }
    catch (DeviceException ex)
    {
      Logger.getLogger(CurrentDevices.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public static void setServoControl(ServoControl val)
  {
    servo = val;

    try
    {
      maxPosition = servo.getMaxPosition();
      minPosition = servo.getMinPosition();
    }
    catch (RemoteException ex)
    {
      Logger.getLogger(CurrentDevices.class.getName()).log(Level.SEVERE, null, ex);
    }
    catch (DeviceException ex)
    {
      Logger.getLogger(CurrentDevices.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public static void setAccelerometer(Accelerometer val)
  {
    accel = val;
  }

  public static void setEncoder(Encoder val)
  {
    enc = val;
  }

  public static MotorControl getMotorControl()
  {
    return motor;
  }

  public static ServoControl getServoControl()
  {
    return servo;
  }

  public static Accelerometer getAccelerometer()
  {
    return accel;
  }

  public static Encoder getEncoder()
  {
    return enc;
  }

  public static void setMotorControlSpeed(double val)
  {
    //this line limits speed to the limits given
    if (val != 0.0)
    {
      if (val > 0)
      {
        val = (val * (motor_upper_limit - motor_lower_limit) / 100) + motor_lower_limit;
      }
      else
      {
        val = (val * (motor_upper_limit - motor_lower_limit) / 100) - motor_lower_limit;
      }
    }

    try
    {
      motor.setVelocity(val);
      //System.out.println("[CurrentDevices]:motor control speed: "+val);
    }
    catch (RemoteException ex)
    {
      Logger.getLogger(CurrentDevices.class.getName()).log(Level.SEVERE, null, ex);
    }
    catch (DeviceException ex)
    {
      Logger.getLogger(CurrentDevices.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public static void setMotorControlBreak(double val)
  {
    //this uses the magnetization that does not move the motor, to stop it
    if (val != 0.0)
    {
      if (val > 0)
      {
        val = (val * (motor_lower_limit) / 100);
      }
      else
      {
        val = (val * (motor_lower_limit) / 100);
      }
    }

    try
    {
      motor.setVelocity(val);
      System.out.println("[CurrentDevices]:motor control speed: " + val);
    }
    catch (RemoteException ex)
    {
      Logger.getLogger(CurrentDevices.class.getName()).log(Level.SEVERE, null, ex);
    }
    catch (DeviceException ex)
    {
      Logger.getLogger(CurrentDevices.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public static void setMotorControlAcceleration(double val)
  {
    try
    {
      motor.setAcceleration(val);
    }
    catch (RemoteException ex)
    {
      Logger.getLogger(CurrentDevices.class.getName()).log(Level.SEVERE, null, ex);
    }
    catch (DeviceException ex)
    {
      Logger.getLogger(CurrentDevices.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public static void setServoPosition(int val)
  {
    try
    {
      servo.setPosition(val);
    }
    catch (RemoteException ex)
    {
      Logger.getLogger(CurrentDevices.class.getName()).log(Level.SEVERE, null, ex);
    }
    catch (DeviceException ex)
    {
      Logger.getLogger(CurrentDevices.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public static double[] getAccelerometerValues()
  {
    double[] v =
    {
      0.0, 0.0, 0.0
    };
    //as we know that the device is returning the acceleration value in cm/s^2
    // we stablish a convertion value to return values in m/s^2
    // see the acv var in the global declaration
    try
    {
      //o[0]=accAxisX.update();
      //o[1]=accAxisY.update();
      //o[2]=accAxisZ.update();
      //System.out.println(o[0]+","+o[1]+","+o[2]);
      double a[] = accel.getAcceleration();
      v[0] = a[0] * acv;
      v[1] = a[1] * acv;
      v[2] = a[2] * acv;
      return v;
    }
    catch (RemoteException ex)
    {
      Logger.getLogger(CurrentDevices.class.getName()).log(Level.SEVERE, null, ex);
    }
    catch (DeviceException ex)
    {
      Logger.getLogger(CurrentDevices.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }

  public static double getEncoderSpeed()
  {
    // returns the encoder angular speed in rad/s

    try
    {
      return enc.getTics() * 2 * 3.14159265359 / ticsinthewheel;
    }
    catch (RemoteException ex)
    {
      Logger.getLogger(CurrentDevices.class.getName()).log(Level.SEVERE, null, ex);
    }
    return -1.0; //nonsense to know it's not returning the value
  }

  public static double getMotorControlSpeed()
  {
    try
    {
      return motor.getVelocity();
    }
    catch (RemoteException ex)
    {
      Logger.getLogger(CurrentDevices.class.getName()).log(Level.SEVERE, null, ex);
    }
    catch (DeviceException ex)
    {
      Logger.getLogger(CurrentDevices.class.getName()).log(Level.SEVERE, null, ex);
    }
    return -1.0; //nonsense to know it's not returning the value
  }

  public static double getServoPosition()
  {
    try
    {
      return servo.getPosition();
    }
    catch (RemoteException ex)
    {
      Logger.getLogger(CurrentDevices.class.getName()).log(Level.SEVERE, null, ex);
    }
    catch (DeviceException ex)
    {
      Logger.getLogger(CurrentDevices.class.getName()).log(Level.SEVERE, null, ex);
    }
    return -1.0; //nonsense to know it's not returning the value
  }
}
