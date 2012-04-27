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

import com.phidgets.InterfaceKitPhidget;
import com.phidgets.PhidgetException;
import jaseimov.lib.devices.AbstractDevice;
import jaseimov.lib.devices.DeviceException;
import jaseimov.lib.devices.DeviceType;
import jaseimov.lib.devices.InterfaceKit;
import java.rmi.RemoteException;

/**
 * Implements an InterfaceKit based in a Phidget InterfaceKit board. Uses phidgets library.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class InterfaceKitDevice extends AbstractDevice implements InterfaceKit
{
  private int phidgetSerial;
  private InterfaceKitPhidget ikit;

  /**
   * Creates a new InterfaceKitDevice.
   * @param name Name of the device.
   * @param serial Phidgets serial number of the Phidgets InterfaceKit board.
   * @throws DeviceException If the device is not found.
   */
  public InterfaceKitDevice(String name, int serial) throws DeviceException
  {
    super(name, DeviceType.INTERFACE_KIT);

    phidgetSerial = serial;

    // Connect to phidget device
    try
    {
      ikit = new InterfaceKitPhidget();
      ikit.open(phidgetSerial);
      ikit.waitForAttachment(DeviceConstants.PHIDGET_WAIT);
      ikit.setRatiometric(true);
    }
    catch (PhidgetException ex)
    {
      throw new DeviceException(ex.getDescription());
    }
  }

  public boolean getOutput(int index) throws RemoteException, DeviceException
  {
    try
    {
      return ikit.getOutputState(index);
    }
    catch (PhidgetException ex)
    {
      throw new DeviceException(ex.getDescription());
    }
  }

  public void setOutput(int index, boolean value) throws RemoteException, DeviceException
  {
    try
    {
      ikit.setOutputState(index, value);
    }
    catch (PhidgetException ex)
    {
      throw new DeviceException(ex.getDescription());
    }
  }

  public int getSensorValue(int index) throws RemoteException, DeviceException
  {
    try
    {
      return ikit.getSensorValue(index);
    }
    catch (PhidgetException ex)
    {
      throw new DeviceException(ex.getDescription());
    }
  }

  /**
   * Set all digital outputs to disabled and close the Phidgets device.
   * @throws DeviceException
   */
  @Override
  public void closeDevice() throws DeviceException
  {
    try
    {
      for (int i = 0; i < ikit.getOutputCount(); i++)
      {
        if (ikit.getOutputState(i))
        {
          ikit.setOutputState(i, false);
        }
      }
      ikit.close();
    }
    catch (PhidgetException ex)
    {
      throw new DeviceException(ex.getDescription());
    }
  }
}
