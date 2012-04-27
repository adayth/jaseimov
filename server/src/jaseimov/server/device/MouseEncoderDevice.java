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
import jaseimov.lib.devices.DeviceException;
import jaseimov.lib.devices.DeviceType;
import jaseimov.lib.devices.Encoder;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import mouselib.MouseCapturer;

/**
 * Implements an encoder based in a ball mouse. Uses mouselib.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class MouseEncoderDevice extends AbstractDevice implements Encoder
{
  /**
   * Define the axis avalible in a ball mouse.
   */
  public enum MouseAxis
  {
    POSITIVE_X_AXIS,
    NEGATIVE_X_AXIS,
    POSITIVE_Y_AXIS,
    NEGATIVE_Y_AXIS;
  }

  private class MouseReadTask implements Runnable
  {
    public boolean runThread = true;

    public void run()
    {
      while (runThread)
      {
        try
        {
          byte[] packet = mouse.readMovementPacket();
          if (packet != null)
          {
            int inc = 0;
            // X = packet[1], Y = packet[2]
            switch (axis)
            {
              case POSITIVE_X_AXIS:
                inc = (int) packet[1];
                break;
              case NEGATIVE_X_AXIS:
                inc = -((int) packet[1]);
                break;
              case POSITIVE_Y_AXIS:
                inc = (int) packet[2];
                break;
              case NEGATIVE_Y_AXIS:
                inc = -((int) packet[2]);
                break;
              default:
                break;
            }
            tics += inc;
          }
        }
        catch (IOException ex)
        {
          ex.printStackTrace();
        }
      }
    }
  }
  private MouseCapturer mouse;
  private double cmPerTic;
  private MouseAxis axis;
  // This variable acomulates tics of the encoder and can be used by more of one Thread
  private volatile int tics;
  private MouseReadTask task = new MouseReadTask();
  private Thread thread;

  /**
   * Creates a new MouseEncoderDevice. Start a new thread that reads mouse device file using mouselib.
   * @param name Name of the device.
   * @param file Mouse device file, normally allocated in /dev/input/mouse#.
   * @param wheelRadius Radius of the wheel mouse for calculate cm/tic value.
   * @param ticsPerTurn Tics in a complete turn of the wheel for calculate cm/tic value.
   * @param mouseAxis The axis of the mouse with the encoder.
   * @throws DeviceException If the file is not found.
   */
  public MouseEncoderDevice(String name, String file, double wheelRadius, int ticsPerTurn, MouseAxis mouseAxis) throws DeviceException
  {
    super(name, DeviceType.ENCODER_SENSOR);

    mouse = new MouseCapturer(file);

    try
    {
      mouse.open();
      thread = new Thread(task);
      thread.start();
    }
    catch (FileNotFoundException ex)
    {
      throw new DeviceException(ex.getMessage());
    }

    // tics * cmPerTic = distance
    cmPerTic = wheelRadius * (360. / (double) ticsPerTurn) * ((2 * Math.PI) / 360.);
    // Mouse axis
    axis = mouseAxis;
  }

  public int getTics() throws RemoteException
  {
    // Reset counter and return value
    int saveTics = tics;
    tics = 0;
    return saveTics;
  }

  public double getCmPerTic() throws RemoteException
  {
    return cmPerTic;
  }

  public Object update() throws RemoteException, DeviceException
  {
    return getTics();
  }

  /**
   * Stops the Thread that read the mouse device file and closes it.
   * @throws DeviceException
   */
  @Override
  public void closeDevice() throws DeviceException
  {
    task.runThread = false;
    try
    {
      mouse.close();
    }
    catch (IOException ex)
    {
      throw new DeviceException(ex.getMessage());
    }
  }
}
