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
package server.devicetest;

import device.AbstractDevice;
import device.DeviceException;
import device.DeviceType;
import device.MotorControl;
import java.rmi.RemoteException;

/**
 * Virtual MotorControl for debugging purposes.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */

public class MotorControlTest extends AbstractDevice implements MotorControl
{
    double velocity = 0.;
    double minVelocity = -100.;
    double maxVelocity = 100.;

    double aceleration = 0.1;
    double minAceleration = 0.1;
    double maxAceleration = 100.;

    public MotorControlTest()
    {
        super("motor-test",DeviceType.MOTOR_CONTROL);
    }

    public void printStatus()
    {
        System.out.println("name: " + deviceName + " id: " + deviceID + " type: " + deviceType);        
        System.out.println("velocity: " + velocity);
        System.out.println("aceleration: " + aceleration);
    }

    public double getVelocity() throws RemoteException, DeviceException
    {
        return velocity;
    }

    public void setVelocity(double v) throws RemoteException, DeviceException
    {        
        velocity = v;        
    }

    public double getAcceleration() throws RemoteException, DeviceException
    {
        return aceleration;
    }

    public void setAcceleration(double a) throws RemoteException, DeviceException
    {
        if(a == 0)
        {
            a = minAceleration;
        }
        aceleration = a;        
    }

    public double getMinVelocity() throws RemoteException, DeviceException
    {
        return minVelocity;
    }

    public double getMaxVelocity() throws RemoteException, DeviceException
    {
        return maxVelocity;
    }

    public double getMinAcceleration() throws RemoteException, DeviceException
    {
        return minAceleration;
    }

    public double getMaxAcceleration() throws RemoteException, DeviceException
    {
        return maxAceleration;
    }

    public void stopMotor() throws RemoteException, DeviceException
    {
        setAcceleration(maxAceleration);
        setVelocity(0);
    }

    @Override
    public void closeDevice() throws DeviceException
    {
    }
}
