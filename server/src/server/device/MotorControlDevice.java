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
package server.device;

import device.DeviceType;
import device.MotorControl;
import java.rmi.RemoteException;
import com.phidgets.MotorControlPhidget;
import com.phidgets.PhidgetException;
import device.AbstractDevice;
import device.DeviceException;

/**
 * Implements a MotorControl based in Phidgets Motor Controller. Uses phidgets library.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class MotorControlDevice extends AbstractDevice implements MotorControl
{
    private int phidgetSerial;
    private MotorControlPhidget motor;
    private int index;
    
    private double minVelocity = -100;
    private double maxVelocity = 100;
    private double minAceleration;
    private double maxAceleration;

    /**
     * Creates a new MotorControlDevice.
     * @param name Name of the device.
     * @param serial Phidgets serial number of the PHidgets Motor Controller.
     * @param motorIndex Input of the controller where the motor is connected.
     * @throws DeviceException If the device is not found.
     */
    public MotorControlDevice(String name, int serial,int motorIndex) throws DeviceException
    {
        super(name,DeviceType.MOTOR_CONTROL);
        phidgetSerial = serial;
        index = motorIndex;

        // Connect to phidget device
        try
        {
            motor = new MotorControlPhidget();
            motor.open(phidgetSerial);
            motor.waitForAttachment(DeviceConstants.PHIDGET_WAIT);
            minAceleration = motor.getAccelerationMin(index);
            maxAceleration = motor.getAccelerationMax(index);
            motor.setVelocity(index,0);
            motor.setAcceleration(index, minAceleration);
        }        
        catch (PhidgetException ex)
        {            
            throw new DeviceException(ex.getDescription());
        }
    }    

    public double getVelocity() throws RemoteException, DeviceException
    {
        try
        {
            return motor.getVelocity(index);
        }
        catch (PhidgetException ex)
        {
            throw new DeviceException(ex.getDescription());
        }
    }

    public void setVelocity(double v) throws RemoteException, DeviceException
    {
        try
        {
            motor.setVelocity(index, v);
        }
        catch (PhidgetException ex)
        {
            throw new DeviceException(ex.getDescription());
        }
    }

    public double getAcceleration() throws RemoteException, DeviceException
    {
        try
        {
            return motor.getAcceleration(index);
        }
        catch (PhidgetException ex)
        {
            throw new DeviceException(ex.getDescription());
        }
    }
    
    public void setAcceleration(double a) throws RemoteException, DeviceException
    {
        // setAceleration(0) will throw a PhidgetException because mininimum aceleration is 0.1
        if(a == 0)
            a = minAceleration;
        try
        {
            motor.setAcceleration(index, a);
        }
        catch (PhidgetException ex)
        {
            throw new DeviceException(ex.getDescription());
        }
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

    /**
     * Stops the motor and closes the Phidget device.
     * @throws DeviceException
     */
    @Override
    public void closeDevice() throws DeviceException
    {
        try
        {
            motor.setAcceleration(index, maxAceleration);
            motor.setVelocity(index, 0);
            motor.close();
        }
        catch(PhidgetException ex)
        {            
            throw new DeviceException(ex.getDescription());
        }
    }
}