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

import com.phidgets.PhidgetException;
import jaseimov.lib.devices.DeviceType;
import jaseimov.lib.devices.ServoControl;
import java.rmi.RemoteException;
import com.phidgets.ServoPhidget;
import jaseimov.lib.devices.AbstractDevice;
import jaseimov.lib.devices.DeviceException;

/**
 * Implements a ServoControl device based in Phidgets Servo Controller. Uses phidgets java library.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class ServoControlDevice extends AbstractDevice implements ServoControl
{
  private int phidgetSerial;
  private ServoPhidget servo;
  private int index;
  final private static int servoType = ServoPhidget.PHIDGET_SERVO_HITEC_HS5245MG;
  private int minPosition;
  private int maxPosition;
  private int startPosition;

  /**
   * Creates a new ServoControlDevice.
   * @param name Name of the device.
   * @param serial Phidgets serial number of the Phidgets Servo Controller.
   * @param index Index of the controller where the servomotor is connected.
   * @param min Minimun position of the servomotor allowed.
   * @param max Maximum position of the servomotor allowed.
   * @param start Start and safe position of the servomotor. Used as initial position in the constructor.
   * @throws DeviceException If the device is not found.
   */
  public ServoControlDevice(String name, int serial, int index, int min, int max, int start) throws DeviceException
  {
    super(name, DeviceType.SERVO_CONTROL);
    phidgetSerial = serial;
    this.index = index;
    minPosition = min;
    maxPosition = max;
    startPosition = start;

    try
    {
      servo = new ServoPhidget();
      servo.open(phidgetSerial);
      servo.waitForAttachment(DeviceConstants.PHIDGET_WAIT);
      servo.setServoType(index, servoType);
      servo.setEngaged(index, true);
      servo.setPosition(index, startPosition);
    }
    catch (PhidgetException ex)
    {
      throw new DeviceException(ex.getDescription());
    }
  }

  public double getPosition() throws RemoteException, DeviceException
  {
    try
    {
      return servo.getPosition(index);
    }
    catch (PhidgetException ex)
    {
      throw new DeviceException(ex.getDescription());
    }
  }

  public void setPosition(double p) throws RemoteException, DeviceException
  {
    try
    {
      servo.setPosition(index, p);
    }
    catch (PhidgetException ex)
    {
      throw new DeviceException(ex.getDescription());
    }
  }

  public boolean getEngaged() throws RemoteException, DeviceException
  {
    try
    {
      return servo.getEngaged(index);
    }
    catch (PhidgetException ex)
    {
      throw new DeviceException(ex.getDescription());
    }
  }

  public void setEngaged(boolean b) throws RemoteException, DeviceException
  {
    try
    {
      servo.setEngaged(index, b);
    }
    catch (PhidgetException ex)
    {
      throw new DeviceException(ex.getDescription());
    }
  }

  public double getMinPosition() throws RemoteException, DeviceException
  {
    return minPosition;
  }

  public double getMaxPosition() throws RemoteException, DeviceException
  {
    return maxPosition;
  }

  public double getStartPosition() throws RemoteException, DeviceException
  {
    return startPosition;
  }

  public void resetPosition() throws RemoteException, DeviceException
  {
    setPosition(startPosition);
  }

  /**
   * Sets position to start position and disengages the servomotor.
   * Then closes the Phidget device.
   * @throws DeviceException
   */
  @Override
  public void closeDevice() throws DeviceException
  {
    try
    {
      servo.setPosition(index, startPosition);
      servo.setEngaged(index, false);
      servo.close();
    }
    catch (PhidgetException ex)
    {
      throw new DeviceException(ex.getDescription());
    }
  }
}
