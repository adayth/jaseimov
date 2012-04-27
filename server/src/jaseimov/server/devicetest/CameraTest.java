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
import jaseimov.lib.devices.Camera;
import jaseimov.lib.devices.DeviceException;
import jaseimov.lib.devices.DeviceType;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;

/**
 * Virtual Camera for debugging purposes.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class CameraTest extends AbstractDevice implements Camera
{
  private int width = 352;
  private int height = 288;
  private byte[] image;

  public CameraTest()
  {
    super("camera-test", DeviceType.CAMERA_SENSOR);

    byte[] buf = new byte[1024];
    try
    {
      InputStream fis = this.getClass().getResourceAsStream("video-test.jpg");
      ByteArrayOutputStream bos = new ByteArrayOutputStream();

      for (int readNum; (readNum = fis.read(buf)) != -1;)
      {
        bos.write(buf, 0, readNum);
      }

      image = bos.toByteArray();
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
    }
  }

  @Override
  public void closeDevice() throws DeviceException
  {
  }

  public int getImageWidth() throws RemoteException
  {
    return width;
  }

  public int getImageHeigth() throws RemoteException
  {
    return height;
  }

  public void setCompression(int value) throws RemoteException, DeviceException
  {
    System.out.println("Camera " + getName() + " ID " + getID() + " compression is set to " + value);
  }

  public byte[] getImage() throws RemoteException, DeviceException
  {
    return image;
  }

  public Object update() throws RemoteException, DeviceException
  {
    return getImage();
  }
}
