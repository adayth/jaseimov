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
import jaseimov.lib.devices.Sonar;
import java.rmi.RemoteException;

/**
 * Virtual Sonar for debugging purposes.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class SonarTest extends AbstractDevice implements Sonar
{
  boolean enabled;

  public SonarTest()
  {
    super("sonar-test", DeviceType.SONAR_SENSOR);
    enabled = false;
  }

  @Override
  public void closeDevice() throws DeviceException
  {
  }

  public boolean getEnabled() throws RemoteException, DeviceException
  {
    return enabled;
  }

  public void setEnabled(boolean value) throws RemoteException, DeviceException
  {
    enabled = value;
  }

  public double getDistance() throws RemoteException, DeviceException
  {
    return (Math.random() * 1000) * 1.296;
  }

  public Object update() throws RemoteException, DeviceException
  {
    return getDistance();
  }
}
