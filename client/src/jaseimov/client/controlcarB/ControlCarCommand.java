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
package jaseimov.client.controlcarB;

import jaseimov.client.ClientApp;
import jaseimov.lib.remote.connect.ConnectException;
import jaseimov.lib.remote.list.RemoteDeviceInfo;
import jaseimov.client.utils.Command;
import jaseimov.lib.devices.Accelerometer;
import jaseimov.lib.devices.MotorControl;
import jaseimov.lib.devices.ServoControl;

/**
 * Command that creates and shows a {@link CarControlCenter}.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class ControlCarCommand extends Command
{
  private static final String name = "control car B";
  private CarControlCenter control;

  public ControlCarCommand()
  {
    super(name);
  }

  public void execute()
  {
    RemoteDeviceInfo[] motors;
    RemoteDeviceInfo[] servos;
    RemoteDeviceInfo[] accelerometers;
    try
    {
      motors = ClientApp.getDeviceList().getRemoteDeviceInfoArray(MotorControl.class);
      servos = ClientApp.getDeviceList().getRemoteDeviceInfoArray(ServoControl.class);
      accelerometers = ClientApp.getDeviceList().getRemoteDeviceInfoArray(Accelerometer.class);

      if (motors.length > 0 && servos.length > 0 && accelerometers.length > 0)
      {
        if (control == null || !control.isVisible())
        {
          control = new CarControlCenter(
                  (MotorControl) motors[0].getDevice(),
                  (ServoControl) servos[0].getDevice(),
                  (Accelerometer) accelerometers[0].getDevice());
        }
        ClientApp.registerFrame(control);
        control.setTitle("car control");
        control.setVisible(false);
        control.setVisible(true);
      }
      else
      {
        throw new RuntimeException(ClientApp.getBundleString("NO MOTOR SERVO EXCEPTION"));
      }
    }
    catch (ConnectException ex)
    {
      ex.printStackTrace();
    }
  }
}
