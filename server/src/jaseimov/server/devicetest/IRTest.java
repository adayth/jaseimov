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
import jaseimov.lib.devices.IR;
import java.rmi.RemoteException;

/**
 * Virtual IR for debugging purposes.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class IRTest extends AbstractDevice implements IR
{
  public IRTest()
  {
    super("ir-test", DeviceType.IR_SENSOR);
  }

  @Override
  public void closeDevice() throws DeviceException
  {
  }

  public double getDistance() throws RemoteException, DeviceException
  {
    int val = (int) (Math.random() * 1000);
    return 4800. / (val - 20.);
  }

  public Object update() throws RemoteException, DeviceException
  {
    return getDistance();
  }
}
