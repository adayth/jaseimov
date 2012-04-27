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
import jaseimov.lib.devices.DeviceException;
import jaseimov.lib.devices.DeviceType;
import jaseimov.lib.devices.ServoControl;
import java.rmi.RemoteException;

/**
 * Virtual ServoControl for debugging purposes.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class ServoControlTest extends AbstractDevice implements ServoControl
{
  double position = 70.;
  double minPosition = 30.;
  double maxPosition = 110.;
  double startPosition = 70.;
  boolean engaged = false;

  public ServoControlTest()
  {
    super("servo-test", DeviceType.SERVO_CONTROL);
    engaged = true;
  }

  public void printStatus()
  {
    System.out.println("name: " + deviceName + " id: " + deviceID + " type: " + deviceType);
    System.out.println("position: " + position);
  }

  public double getPosition() throws RemoteException, DeviceException
  {
    return position - startPosition;
  }

  public void setPosition(double p) throws RemoteException, DeviceException
  {    
    position = p + startPosition;
  }

  public boolean getEngaged() throws RemoteException, DeviceException
  {
    return engaged;
  }

  public void setEngaged(boolean b) throws RemoteException, DeviceException
  {
    engaged = b;
  }

  public double getMinPosition() throws RemoteException, DeviceException
  {
    return minPosition - startPosition;
  }

  public double getMaxPosition() throws RemoteException, DeviceException
  {
    return maxPosition - startPosition;
  }

  public double getStartPosition() throws RemoteException, DeviceException
  {
    return 0;
  }

  public void resetPosition() throws RemoteException, DeviceException
  {
    setPosition(startPosition);
  }

  @Override
  public void closeDevice() throws DeviceException
  {
  }
}
