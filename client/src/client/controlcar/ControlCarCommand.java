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
package client.controlcar;

import client.ClientApp;
import client.Command;
import client.servercomm.ConnectException;
import client.devicelist.DeviceInfo;
import device.DeviceType;
import device.MotorControl;
import device.ServoControl;

/**
 * Command that creates and shows a {@link ControlCarFrame}.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class ControlCarCommand extends Command
{
    private static final String name = "control car";

    private ControlCarFrame control;

    public ControlCarCommand()
    {
        super(name);
    }

    public void execute()
    {
        DeviceInfo[] motors;
        DeviceInfo[] servos;
        try
        {
            motors = ClientApp.getDeviceList().getDeviceInfoArray(DeviceType.MOTOR_CONTROL);
            servos = ClientApp.getDeviceList().getDeviceInfoArray(DeviceType.SERVO_CONTROL);
            if(motors.length > 0 && servos.length > 0)
            {
                if(control == null || !control.isVisible())
                {
                    control = new ControlCarFrame((MotorControl)motors[0].getDevice(),(ServoControl)servos[0].getDevice());
                }
                ClientApp.registerFrame(control);
                control.setVisible(false);
                control.setVisible(true);
            }
            else
                throw new RuntimeException(ClientApp.getBundleString("NO MOTOR SERVO EXCEPTION"));
        }
        catch(ConnectException ex)
        {
            ex.printStackTrace();            
        }         
    }

}