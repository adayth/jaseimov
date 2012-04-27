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
import jaseimov.lib.devices.Encoder;
import java.rmi.RemoteException;

/**
 * Virtual Encoder for debugging purposes.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class MouseEncoderTest extends AbstractDevice implements Encoder
{
  public MouseEncoderTest()
  {
    super("test-encoder", DeviceType.MOUSE_ENCODER_SENSOR);
  }

  @Override
  public void closeDevice() throws DeviceException
  {
  }

  public int getTics() throws RemoteException
  {
    return (int) (Math.random() * 10);
  }

  public double getCmPerTic() throws RemoteException
  {
    return 0.01983130362578557;
  }



  public double getRadPerTic() throws RemoteException, DeviceException
  {
    return Math.PI;
  }

  public Object update() throws RemoteException, DeviceException
  {
    return getTics();
  }
}
