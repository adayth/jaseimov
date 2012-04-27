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
package jaseimov.server.device;

import jaseimov.lib.devices.DeviceType;
import jaseimov.lib.devices.MotorControl;
import java.rmi.RemoteException;
import com.phidgets.MotorControlPhidget;
import com.phidgets.PhidgetException;
import jaseimov.lib.devices.AbstractDevice;
import jaseimov.lib.devices.Accelerometer;
import jaseimov.lib.devices.DeviceException;
import jaseimov.server.autocontrol.AutomaticControl;
import jaseimov.server.autocontrol.filters.EmptyFilter;
import jaseimov.server.autocontrol.filters.Filter;

/**
 * Implements a MotorControl based in Phidgets Motor Controller. Uses phidgets library.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class MotorControlDevice extends AbstractDevice implements MotorControl
{
  private int phidgetSerial;
  private MotorControlPhidget motor;
  private int index;
  private double minVelocity = -100;
  private double maxVelocity = 100;
  private double minAceleration;
  private double maxAceleration;
  private double lowerLimit;
  private double upperLimit;
  private double limitDiff;

  /**
   * Creates a new MotorControlDevice.
   * @param name Name of the device.
   * @param serial Phidgets serial number of the PHidgets Motor Controller.
   * @param motorIndex Input of the controller where the motor is connected.
   * @throws DeviceException If the device is not found.
   */

  /**
   * Creates a new MotorControlDevice.
   * @param name Name of the device.
   * @param serial Phidgets serial number of the PHidgets Motor Controller.
   * @param motorIndex Input of the controller where the motor is connected.
   * @param lowerLimit Minimum percent (0-100) needed by the controller to move the motor.
   * @param upperLimit Maximum percent (0-100) that can be used by the controller without exceeding electrical motor limits.
   * @throws DeviceException If the device is not found.
   */
  public MotorControlDevice(String name, int serial, int motorIndex, int lowerLimit, int upperLimit) throws DeviceException
  {
    super(name, DeviceType.MOTOR_CONTROL);
    phidgetSerial = serial;
    index = motorIndex;

    // Velocity is used as percent: from 0 to 100
    // But velocity must ve adjusted to motor limits: lower to upper
    this.lowerLimit = lowerLimit;
    this.upperLimit = upperLimit;
    limitDiff = (this.upperLimit-lowerLimit) / 100.;

    // Connect to phidget device
    try
    {
      motor = new MotorControlPhidget();
      motor.open(phidgetSerial);
      motor.waitForAttachment(DeviceConstants.PHIDGET_WAIT);
      minAceleration = motor.getAccelerationMin(index);
      maxAceleration = motor.getAccelerationMax(index);
      motor.setVelocity(index, 0);
      motor.setAcceleration(index, minAceleration);
    }
    catch (PhidgetException ex)
    {
      throw new DeviceException(ex.getDescription());
    }
  }

  public double getVelocity() throws RemoteException, DeviceException
  {
    try
    {
      double v = motor.getVelocity(index);

      // Velocity value is adjusted to motor limits
      // Return percent value (0 is still zero)
      if( v != 0)
      {
        if(v > 0)
          v = (v - lowerLimit) / limitDiff;
        else
          v = (v + lowerLimit) / limitDiff;
      }
      
      return v;
    }
    catch (PhidgetException ex)
    {
      throw new DeviceException(ex.getDescription());
    }
  }

  public void setVelocity(double v) throws RemoteException, DeviceException
  {    
    // Adjust value to motor limits
    // 0 isn't adjusted because is the stop value
    if(v != 0)
    {
      if(v > 0)
        v = (v * limitDiff) + lowerLimit;
      else
        v = (v * limitDiff) - lowerLimit;
    }
    
    // Set velocity value   
    try
    {
      motor.setVelocity(index, v);
    }
    catch (PhidgetException ex)
    {
      throw new DeviceException(ex.getDescription());
    }
  }

  public double getAcceleration() throws RemoteException, DeviceException
  {
    try
    {
      return motor.getAcceleration(index);
    }
    catch (PhidgetException ex)
    {
      throw new DeviceException(ex.getDescription());
    }
  }

  public void setAcceleration(double a) throws RemoteException, DeviceException
  {
    // setAceleration(0) will throw a PhidgetException because mininimum aceleration is 0.1
    if (a == 0)
    {
      a = minAceleration;
    }
    try
    {
      motor.setAcceleration(index, a);
    }
    catch (PhidgetException ex)
    {
      throw new DeviceException(ex.getDescription());
    }
  }

  public double getMinVelocity() throws RemoteException, DeviceException
  {
    return minVelocity;
  }

  public double getMaxVelocity() throws RemoteException, DeviceException
  {
    return maxVelocity;
  }

  public double getMinAcceleration() throws RemoteException, DeviceException
  {
    return minAceleration;
  }

  public double getMaxAcceleration() throws RemoteException, DeviceException
  {
    return maxAceleration;
  }

  public void stopMotor() throws RemoteException, DeviceException
  {
    setAcceleration(maxAceleration);
    setVelocity(0);
  }

  /**
   * Stops the motor and closes the Phidget device.
   * @throws DeviceException
   */
  @Override
  public void closeDevice() throws DeviceException
  {
    try
    {
      motor.setAcceleration(index, maxAceleration);
      motor.setVelocity(index, 0);
      motor.close();
    }
    catch (PhidgetException ex)
    {
      throw new DeviceException(ex.getDescription());
    }
  }

  ///////////////////////////////
  // New Methods for Auto Velocity Control
  ///////////////////////////////

  private AutomaticControl autoControl;

  // TODO Call this from Configurer
  public void configAutoControl(Accelerometer accelerometer)
  {
    Filter filter = new EmptyFilter();
    autoControl = new AutomaticControl(this, accelerometer, filter, 2000);
  }

  private void checkAutoControlConfigured() throws DeviceException
  {
    if(autoControl == null)
    {
      throw new DeviceException("Automatic control not avalaible, configure it first");
    }
  }

  public void setAutoControlled(boolean enabled) throws RemoteException, DeviceException
  {
    checkAutoControlConfigured();
    autoControl.setEnabled(enabled);
  }

  public boolean isAutoControlled() throws RemoteException, DeviceException
  {
    checkAutoControlConfigured();
    return autoControl.isEnabled();
  }

  public void setAutoControlVelocity(double v) throws RemoteException, DeviceException
  {
    checkAutoControlConfigured();
    autoControl.setAutoControlVelocity(v);
  }

  public double getAutoControlVelocity() throws RemoteException, DeviceException
  {
    checkAutoControlConfigured();
    return autoControl.getAutoControlVelocity();
  }

  public double getAutoControlVelocityCalc() throws RemoteException, DeviceException
  {
    checkAutoControlConfigured();
    return autoControl.getVelocityFromAccel();
  }

}
