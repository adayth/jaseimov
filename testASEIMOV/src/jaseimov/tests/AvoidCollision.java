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

package jaseimov.tests;

import jaseimov.lib.remote.list.RemoteDeviceInfo;
import jaseimov.lib.remote.list.RemoteDeviceList;
import jaseimov.lib.remote.connect.ConnectException;
import jaseimov.lib.devices.Device;
import jaseimov.lib.devices.DeviceType;
import jaseimov.lib.devices.MotorControl;
import jaseimov.lib.devices.SensorDevice;

public class AvoidCollision
{
    static String ip = "127.0.0.1";
    static int port = java.rmi.registry.Registry.REGISTRY_PORT;

    static RemoteDeviceList list;

    public static void initList()
    {
        list = new RemoteDeviceList(ip,port);
        try
        {
            list.connectToServer();
        }
        catch(ConnectException ex)
        {
            ex.printStackTrace();
            System.exit(0);
        }
    }

    static MotorControl motor;    
    static double initAcceleration = 50.;

    public static void initMotor()
    {
        try
        {
            RemoteDeviceInfo[] motors = list.getRemoteDeviceInfoArray(DeviceType.MOTOR_CONTROL);
            if(motors.length > 0)
            {
                motor = (MotorControl) motors[0].getDevice();
                try
                {
                    motor.setVelocity(0);
                    motor.setAcceleration(initAcceleration);
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
            }
            else
            {
                System.err.println("No motors where found in this server");
                System.exit(0);
            }
        }
        catch(ConnectException ex)
        {
            ex.printStackTrace();
        }
    }

    static SensorDevice frontSensor;
    static int sensorID = 0;

    public static void initSensor()
    {
        try
        {
            Device dev = list.getRemoteDeviceInfo(sensorID).getDevice();
            if(dev != null && dev instanceof SensorDevice)
            {
                frontSensor = (SensorDevice)dev;
            }
            else
            {
                System.err.println("No sensors whith ID " + sensorID + " where found in this server");
                System.exit(0);
            }
        }
        catch(ConnectException ex)
        {
            ex.printStackTrace();
        }
        
        if(frontSensor == null)
        {
            System.err.println("No sensors whith ID " + sensorID + " where found in this server");
            System.exit(0);
        }
    }

    static double velocity = 40.;    
    static double stopDistance = 30.;

    public static void avoidCollision()
    {
        try
        {
            motor.setVelocity(velocity);
            while(true)
            {
                Double distance = (Double)frontSensor.update();
                if(distance > 0)
                {
                    System.out.println("Obstacle detected at " + distance + " cm");
                    if(distance <= stopDistance)
                    {
                        motor.stopMotor();
                        System.out.println("Stopping vehicle");
                        Thread.sleep(250);
                        return;
                    }
                }
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return;
        }
    }

    public static void close()
    {
        try
        {
            motor.stopMotor();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    public static void main(String[] args)
    {
        if(args.length == 4)
        {
            ip = args[0];            
            sensorID = Integer.valueOf(args[1]);
            velocity = Integer.valueOf(args[2]);
            stopDistance = Integer.valueOf(args[3]);
        }
        else
        {
            System.out.println("Use: $IP $SENSOR_ID $VELOCITY $DISTANCE");
            System.exit(1);
        }
        initList();
        initMotor();
        initSensor();
        avoidCollision();
        close();
    }

}
