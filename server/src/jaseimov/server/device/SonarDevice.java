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

import jaseimov.lib.devices.AbstractDevice;
import jaseimov.lib.devices.Device;
import jaseimov.lib.devices.DeviceException;
import jaseimov.lib.devices.DeviceType;
import jaseimov.lib.devices.InterfaceKit;
import jaseimov.lib.devices.Sonar;
import java.rmi.RemoteException;

/**
 * Implements a Sonar device conected to a Phidgets InterfaceKit board. Uses phidgets java library.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class SonarDevice extends AbstractDevice implements Sonar
{
  private InterfaceKit ikit;
  private int sensorPort;
  private int outputPort;

  /**
   * Creates a new SonarDevice.
   * @param name Name for the device.
   * @param ikit InterfaceKit where sonar is connected.
   * @param sensorPort Analog input of the InterfaceKit where sonar is connected.
   * @param outputPort Digital output of the InterfaceKit where sonar can be turned on/off.
   * @throws DeviceException If the InterfaceKit has an error.
   */
  public SonarDevice(String name, Device ikit, int sensorPort, int outputPort) throws DeviceException
  {
    super(name, DeviceType.SONAR_SENSOR);

    this.ikit = (InterfaceKit) ikit;
    this.sensorPort = sensorPort;
    this.outputPort = outputPort;

    try
    {
      this.setEnabled(true);
    }
    catch (RemoteException ex)
    {
      throw new DeviceException(ex.getMessage());
    }
  }

  /**
   * This device doesn't need to be closed.
   * InterfaceKit will disable outputs on close
   * @throws DeviceException
   */
  @Override
  public void closeDevice() throws DeviceException
  {
  }

  public boolean getEnabled() throws RemoteException, DeviceException
  {
    return ikit.getOutput(outputPort);
  }

  public void setEnabled(boolean value) throws RemoteException, DeviceException
  {
    ikit.setOutput(outputPort, value);
  }

  /**
   * Obtain the sonar measured distance.
   * @return Measured distance in centimeters. If the sonar isn't enabled (is turned off) returns -1.
   * @throws RemoteException
   * @throws DeviceException
   */
  public double getDistance() throws RemoteException, DeviceException
  {
    if (getEnabled())
    {
      int value = ikit.getSensorValue(sensorPort);
      return value * 1.296;
    }
    else
    {
      return -1;
    }
  }

  public Object update() throws RemoteException, DeviceException
  {
    return getDistance();
  }
}
