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

package jaseimov.scripts;

import jaseimov.lib.devices.Accelerometer;
import jaseimov.lib.devices.MotorControl;
import jaseimov.lib.devices.ServoControl;
import jaseimov.lib.remote.connect.ConnectException;
import jaseimov.lib.remote.list.RemoteDeviceInfo;
import jaseimov.lib.remote.list.RemoteDeviceList;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class CaptureScript
{
  static boolean DEBUG = true;

  static String IP = "127.0.0.1";
  static int PORT = java.rmi.registry.Registry.REGISTRY_PORT;

  static String SCRIPT_PATH = "script.txt";
  static File SCRIPT_FILE;
  static ScriptRunner scriptRunner = new ScriptRunner();
  
  static String CAPTURE_PATH = "capture.txt";  
  static File CAPTURE_FILE;
  static long CAPTURE_TIME = 100;

  static MotorControl MOTOR = null;
  static ServoControl SERVO = null;
  static Accelerometer ACCEL = null;

  static List<Long> TIMESTAMP_LIST = new ArrayList<Long>();
  static List<double[]> ACCELERATION_LIST = new ArrayList<double[]>();

  public static void main(String[] args)
  {
    ///////////////////////////////////
    // Read input parameters
    ///////////////////////////////////

    if (args.length >= 3)
    {
      IP = args[0];
      SCRIPT_PATH = args[1];
      CAPTURE_PATH = args[2];
    }
    else
    {
      System.out.println("Use: $IP $SCRIPT-FILE $CAPTURE-FILE");
      System.exit(1);
    }

    ///////////////////////////////////
    // TODO Read script file and load it
    ///////////////////////////////////

    SCRIPT_FILE = new File(SCRIPT_PATH);
    if(!SCRIPT_FILE.exists() || !SCRIPT_FILE.canRead())
    {
      System.err.println("Unable to read script file: " + SCRIPT_FILE);
    }
    else
    {
      System.out.println("Reading script file: " + SCRIPT_FILE);
      try
      {
        List<ScriptOrder> orders = ScriptParser.parseScriptFile(SCRIPT_FILE);
        scriptRunner.setOrders(orders);
        System.out.println("Readed " + orders.size() + " orders");
      }
      catch(IOException ex)
      {
        System.err.println("Error reading script file: " + ex);
        System.exit(1);
      }
    }

    ///////////////////////////////////
    // Verify that capture file can be written
    ///////////////////////////////////

    CAPTURE_FILE = new File(CAPTURE_PATH);
    /*if(CAPTURE_FILE.canWrite())
    {
      System.err.println("Unable to write to capture file: " + CAPTURE_FILE);
      System.exit(1);
    }    */

    ///////////////////////////////////
    // Obtain server device list
    ///////////////////////////////////
    
    RemoteDeviceList remoteList = new RemoteDeviceList(IP, PORT);
    try
    {
      remoteList.connectToServer();
    }
    catch(ConnectException ex)
    {
      System.err.println("Unable to connect to server device list");
      System.exit(1);
    }

    ///////////////////////////////////
    // Connect to remote devices
    ///////////////////////////////////
    
    RemoteDeviceInfo devInfoArray[];
        
    // Motor    
    try
    {      
      devInfoArray = remoteList.getRemoteDeviceInfoArray(MotorControl.class);
      if(devInfoArray.length > 0)
      {
        MOTOR = (MotorControl) devInfoArray[0].getDevice();
      }
      else
      {
        throw new ConnectException();
      }
    }
    catch(ConnectException ex)
    {
      System.err.println("Unable to connect to a motor");
      System.exit(1);
    }

    // Servo    
    try
    {
      devInfoArray = remoteList.getRemoteDeviceInfoArray(ServoControl.class);
      if(devInfoArray.length > 0)
      {
        SERVO = (ServoControl) devInfoArray[0].getDevice();
      }
      else
      {
        throw new ConnectException();
      }
    }
    catch(ConnectException ex)
    {
      System.err.println("Unable to connect to a servo");
      System.exit(1);
    }

    // Accelerometer sensor    
    try
    {
      devInfoArray = remoteList.getRemoteDeviceInfoArray(Accelerometer.class);
      if(devInfoArray.length > 0)
      {
        ACCEL = (Accelerometer) devInfoArray[0].getDevice();
      }
      else
      {
        throw new ConnectException();
      }
    }
    catch(ConnectException ex)
    {
      System.err.println("Unable to connect to an accelerometer");
      System.exit(1);
    }

    // TODO Other sensors

    ///////////////////////////////////
    // Initialize devices
    ///////////////////////////////////
    
    try
    {
      // Motor init values
      MOTOR.setVelocity(0);
      MOTOR.setAcceleration(MOTOR.getMaxAcceleration());
      MOTOR.setAutoControlled(false);

      // Servo init values
      SERVO.resetPosition();
    }
    catch(Exception ex)
    {
      System.err.println("Error initializing devices: " + ex);
      System.exit(1);
    }

    ///////////////////////////////////
    // Capture thread
    ///////////////////////////////////

    class CaptureThread extends Thread
    {
      volatile boolean run = true;

      @Override
      public void run()
      {
        while(run)
        {
          // Get acceleration values
          try
          {
            long timestamp = System.currentTimeMillis();
            double a[] = ACCEL.getAcceleration();

            TIMESTAMP_LIST.add(timestamp);
            ACCELERATION_LIST.add(a);
          }
          catch(Exception ex)
          {
            System.err.println("Unable to read or store acceleration value, error: " + ex);
          }

          // Sleep capture time
          try
          {
            //long t0 = System.currentTimeMillis();
            sleep(CAPTURE_TIME);
            //long t1 = System.currentTimeMillis();
            //debug("Elapsed time sleeping while capturing data: " + (t1-t0));
          }
          catch(InterruptedException ignore)
          {
          }
        }
      }
    }
    CaptureThread captureThread = new CaptureThread();

    ///////////////////////////////////
    // Script thread
    ///////////////////////////////////

    class ScriptThread extends Thread
    {
      @Override
      public void run()
      {
        while(!scriptRunner.isScriptEnded())
        {
          // Get current order
          ScriptOrder order = scriptRunner.getNextOrder();

          // Run order
          try
          {
            MOTOR.setVelocity(order.getMotorVelocity());
            SERVO.setPosition(order.getServoPosition());
            debug("Order runned: " + order.toString());
          }
          catch(Exception ex)
          {
            System.err.println("Unable to run order, error: " + ex);
          }
          
          // Sleep order time
          try
          {
            //long t0 = System.currentTimeMillis();
            sleep(order.getDuration());
            //long t1 = System.currentTimeMillis();
            //debug("Elapsed time sleeping while executing an order: " + (t1-t0));
          }
          catch(InterruptedException ignore)
          {
          }
        }
        System.out.println("Script ended");
      }
    }
    ScriptThread scriptThread = new ScriptThread();

    ///////////////////////////////////
    // Start threads
    ///////////////////////////////////

    captureThread.start();
    scriptThread.start();

    ///////////////////////////////////
    // Wait until script ends
    ///////////////////////////////////
    
    while(scriptThread.isAlive())
    {
      try
      {
        scriptThread.join();
      }
      catch(InterruptedException ignore)
      {
      }
    }

    ///////////////////////////////////
    // Stop capturing devices
    ///////////////////////////////////
    
    captureThread.run = false;    
    while(captureThread.isAlive())
    {
      try
      {
        captureThread.join();
      }
      catch(InterruptedException ignore)
      {
      }
    }

    ///////////////////////////////////
    // Dump captured info to capture file
    ///////////////////////////////////

    System.out.println("Creating capture file: " + CAPTURE_FILE);
    try
    {
      // Open file
      OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(CAPTURE_FILE));

      // Headers
      writer.write("Timestamp, ax, ay, az");
      writer.write("\n");

      // Captured values
      for(int i=0; i<TIMESTAMP_LIST.size(); i++)
      {
        long timestamp = TIMESTAMP_LIST.get(i);
        double a[] = ACCELERATION_LIST.get(i);
        double ax = a[0];
        double ay = a[1];
        double az = a[2];
        writer.write(timestamp + ", " + ax + ", " + ay + ", " + az);
        writer.write("\n");
      }

      writer.flush();
      writer.close();
    }
    catch(Exception ex)
    {
      System.err.println("Unable to write captured values, error: " + ex);
      System.exit(1);
    }
    System.out.println("Capture file created");

    ///////////////////////////////////
    // Close app
    ///////////////////////////////////
    
    try
    {
      MOTOR.stopMotor();
      SERVO.resetPosition();
    }
    catch(Exception ex)
    {
      System.err.println("Error while closing app: " + ex);
      System.exit(1);
    }    
  }

  // Print a message only if DEBUG flag is enabled
  static void debug(String msg)
  {
    if(DEBUG)
      System.out.println(msg);
  }
}
