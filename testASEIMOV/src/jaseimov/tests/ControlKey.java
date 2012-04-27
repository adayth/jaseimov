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
import jaseimov.lib.devices.DeviceException;
import jaseimov.lib.devices.DeviceType;
import jaseimov.lib.devices.MotorControl;
import jaseimov.lib.devices.ServoControl;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.rmi.RemoteException;
import javax.swing.JFrame;


public class ControlKey
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

    public static void printList()
    {
        RemoteDeviceInfo infoArray[] = list.getRemoteDeviceInfoArray();
        if(infoArray.length > 0)
        {
            System.out.println("Info of remote services:");
            for(RemoteDeviceInfo info : infoArray)
            {
                try
                {
                    Device dev = info.getDevice();
                    System.out.println(dev.getID() + " " + dev.getName() + " " + dev.getDeviceType());
                }
                catch(ConnectException ex)
                {
                    ex.printStackTrace();
                }
                catch(RemoteException ex)
                {
                    ex.printStackTrace();
                }
            }
        }
        else
        {
            System.err.println("Empty service list");
            System.exit(0);
        }
    }

    static MotorControl motor;
    static ServoControl servo;

    public static void initMotor()
    {
        try
        {
            RemoteDeviceInfo[] motors = list.getRemoteDeviceInfoArray(DeviceType.MOTOR_CONTROL);
            if(motors.length > 0)
            {
                motor = (MotorControl) motors[0].getDevice();
            }
            else
            {
                System.err.println("Empty motors list");
                System.exit(0);
            }
        }
        catch(ConnectException ex)
        {
            ex.printStackTrace();
        }
    }

    public static void initServo()
    {
        try
        {
            RemoteDeviceInfo[] servos = list.getRemoteDeviceInfoArray(DeviceType.SERVO_CONTROL);
            if(servos.length > 0)
            {
                servo = (ServoControl) servos[0].getDevice();
            }
            else
            {
                System.err.println("Empty servos list");
                System.exit(0);
            }
        }
        catch(ConnectException ex)
        {
            ex.printStackTrace();
        }
    }

    
    static double velocity;
    static double minVelocity;
    static double maxVelocity;
    static double incVelocity = 5;

    static double position;
    static double minPosition;
    static double maxPosition;
    static double startPosition;
    static double incPosition = 5;

    public static void controlVehicle()
    {
        class KeyControl extends KeyAdapter
        {
            public KeyControl()
            {
                try
                {
                    velocity = (int)motor.getVelocity();
                    minVelocity = motor.getMinVelocity();
                    maxVelocity = motor.getMaxVelocity();
                    motor.setAcceleration(motor.getMaxAcceleration());

                    position = (int)servo.getPosition();
                    minPosition = servo.getMinPosition();
                    maxPosition = servo.getMaxPosition();
                    startPosition = servo.getStartPosition();

                    System.out.println("\nMotor & servo characteristics:");

                    System.out.println("minVelocity = " + minVelocity);
                    System.out.println("maxVelocity = " + maxVelocity);

                    System.out.println("minPosition = " + minPosition);
                    System.out.println("maxPosition = " + maxPosition);
                    System.out.println("startPosition = " + startPosition);
                }
                catch(RemoteException ex)
                {
                    ex.printStackTrace();
                }
                catch(DeviceException ex)
                {
                    ex.printStackTrace();
                }
                printStatus();
            }
            
            @Override
            public void keyTyped(KeyEvent e)
            {
                System.out.println();
                char c = e.getKeyChar();                
                switch(c)
                {
                    case 'w':
                        velocity += incVelocity;
                        setVelocity(velocity);
                        break;
                    case 's':
                        velocity -= incVelocity;
                        setVelocity(velocity);
                        break;
                    case 'a':
                        position -= incPosition;
                        setPosition(position);
                        break;
                    case 'd':
                        position += incPosition;
                        setPosition(position);
                        break;
                    case 'e':
                        velocity = 0;
                        setVelocity(velocity);
                        setPosition(startPosition);
                        break;
                    case 'q':
                        System.out.println("Exiting");
                        exit();
                        System.exit(0);
                        break;
                    default:
                        return;                        
                }                
                printStatus();
            }

            public void setVelocity(double v)
            {
                System.out.println("Set motor velocity to " + v);
                try
                {
                    motor.setVelocity(v);
                }
                catch(RemoteException ex)
                {
                    ex.printStackTrace();
                }
                catch(DeviceException ex)
                {
                    ex.printStackTrace();
                }
            }

            public void setPosition(double p)
            {
                System.out.println("Set servo position to " + p);
                try
                {
                    servo.setPosition(p);
                }
                catch(RemoteException ex)
                {
                    ex.printStackTrace();
                }
                catch(DeviceException ex)
                {
                    ex.printStackTrace();
                }
            }

            public void exit()
            {
                try
                {
                    servo.resetPosition();
                    motor.stopMotor();
                }
                catch(RemoteException ex)
                {
                    ex.printStackTrace();
                }
                catch(DeviceException ex)
                {
                    ex.printStackTrace();
                }
            }

            public void printStatus()
            {
                try 
                {
                    Thread.sleep(100);
                }
                catch (InterruptedException ex)
                {
                    ex.printStackTrace();
                }
                try
                {
                    System.out.println("\nReal motor Velocity = " + motor.getVelocity());
                    System.out.println("Real servo Position = " + servo.getPosition());
                }
                catch (RemoteException ex)
                {
                    ex.printStackTrace();
                }
                catch (DeviceException ex)
                {
                    ex.printStackTrace();
                }
            }
        }
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addKeyListener(new KeyControl());
        frame.setSize(200, 200);
        frame.setVisible(true);
    }
    
    public static void main(String[] args)
    {
       initList();
       printList();

       initMotor();
       initServo();

       controlVehicle();
    }

}
