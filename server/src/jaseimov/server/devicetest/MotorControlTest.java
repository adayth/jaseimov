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
package jaseimov.server.devicetest;

import jaseimov.lib.devices.AbstractDevice;
import jaseimov.lib.devices.Accelerometer;
import jaseimov.lib.devices.DeviceException;
import jaseimov.lib.devices.DeviceType;
import jaseimov.lib.devices.MotorControl;
import jaseimov.server.autocontrol.AutomaticControl;
import jaseimov.server.autocontrol.filters.EmptyFilter;
import jaseimov.server.autocontrol.filters.Filter;
import java.rmi.RemoteException;

/**
 * Virtual MotorControl for debugging purposes.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class MotorControlTest extends AbstractDevice implements MotorControl
{
  double velocity = 0.;
  double minVelocity = -100.;
  double maxVelocity = 100.;
  double aceleration = 0.1;
  double minAceleration = 0.1;
  double maxAceleration = 100.;

  public MotorControlTest()
  {
    super("motor-test", DeviceType.MOTOR_CONTROL);
  }

  public void printStatus()
  {
    System.out.println("name: " + deviceName + " id: " + deviceID + " type: " + deviceType);
    System.out.println("velocity: " + velocity);
    System.out.println("aceleration: " + aceleration);
  }

  public double getVelocity() throws RemoteException, DeviceException
  {
    return velocity;
  }

  public void setVelocity(double v) throws RemoteException, DeviceException
  {
    velocity = v;
  }

  public double getAcceleration() throws RemoteException, DeviceException
  {
    return aceleration;
  }

  public void setAcceleration(double a) throws RemoteException, DeviceException
  {
    if (a == 0)
    {
      a = minAceleration;
    }
    aceleration = a;
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

  @Override
  public void closeDevice() throws DeviceException
  {
  }

  ///////////////////////////////
  // New Methods for Auto Velocity Control
  ///////////////////////////////

  private AutomaticControl autoControl;

  public void configAutoControl(Accelerometer accelerometer)
  {
    Filter filter = new EmptyFilter();
    autoControl = new AutomaticControl(this, accelerometer, filter, 2000);
  }

  public void setAutoControlled(boolean enabled) throws RemoteException, DeviceException
  {
    autoControl.setEnabled(enabled);
  }

  public boolean isAutoControlled() throws RemoteException, DeviceException
  {
    return autoControl.isEnabled();
  }

  public void setAutoControlVelocity(double v) throws RemoteException, DeviceException
  {
    autoControl.setAutoControlVelocity(v);
  }

  public double getAutoControlVelocity() throws RemoteException, DeviceException
  {
    return autoControl.getAutoControlVelocity();
  }

  public double getAutoControlVelocityCalc() throws RemoteException, DeviceException
  {
    return autoControl.getVelocityFromAccel();
  }

}
