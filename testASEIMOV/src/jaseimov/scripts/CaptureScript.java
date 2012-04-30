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

import jaseimov.lib.devices.MotorControl;
import jaseimov.lib.devices.SensorDevice;
import jaseimov.lib.devices.ServoControl;
import jaseimov.lib.remote.connect.ConnectException;
import jaseimov.lib.remote.list.RemoteDeviceInfo;
import jaseimov.lib.remote.list.RemoteDeviceList;
import jaseimov.scripts.xml.ScriptXMLUtil;
import jaseimov.scripts.xml.bindings.Script;
import jaseimov.scripts.xml.bindings.Script.Captures.Capture;
import jaseimov.scripts.xml.bindings.Script.Captures.Capture.Devices.Device;
import jaseimov.scripts.xml.bindings.Script.Orders.Order;
import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class CaptureScript
{
  static String IP;
  static int PORT = java.rmi.registry.Registry.REGISTRY_PORT;

  static String SCRIPT_PATH;
  static File SCRIPT_FILE;
  static Script SCRIPT;

  static String CAPTURE_PATH;
  static File CAPTURE_FILE;
  static long CAPTURE_TIME = 100;

  static MotorControl MOTOR = null;
  static ServoControl SERVO = null;

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
    // Read script file and load it
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
        ScriptXMLUtil.validateXMLScript(SCRIPT_FILE.getPath());
        SCRIPT = ScriptXMLUtil.parseXMLScript(SCRIPT_FILE.getPath());
        System.out.println("Readed " + SCRIPT.getOrders().getOrder().size() + " orders");
      }
      catch(Exception ex)
      {
        System.err.println("Error reading script file: " + ex);
        System.exit(1);
      }
    }

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
        System.out.println("Connected to motor");
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
        System.out.println("Connected to servo");
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


    // Verify we can connect to devices
    // Save in these lists capture information
    List<Integer> delaysList = new ArrayList<Integer>();
    List<List<SensorDevice>> capturesLists = new ArrayList<List<SensorDevice>>();

    if(SCRIPT.getCaptures() != null)
    {
      for(Capture capture : SCRIPT.getCaptures().getCapture())
      {
        // Delay
        delaysList.add(capture.getDelay());
        // Devices
        List<SensorDevice> sensorsList = new ArrayList<SensorDevice>();
        for(Device device : capture.getDevices().getDevice()) {
          try
          {
            SensorDevice sensor = (SensorDevice) remoteList.getRemoteDeviceInfo(device.getId()).getDevice();

            if (sensor != null)
            {
              System.out.println("Connected to device id " + device.getId());
              sensorsList.add(sensor);
            }
            else
            {
              throw new ConnectException();
            }
          }
          catch (Exception ex)
          {
            System.err.println("Unable to connect to device id " + device.getId() + ", error: " + ex);
            System.exit(1);
          }
        }
        capturesLists.add(sensorsList);
      }
    }

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
     long sleepTime;
     List<SensorDevice> sensors;

      volatile boolean run = true;
      List<List<Long>> timestamps = new ArrayList<List<Long>>();
      List<List<Object>> values = new ArrayList<List<Object>>();

      public CaptureThread(long sleepTime, List<SensorDevice> sensors)
      {
        this.sleepTime = sleepTime;
        this.sensors = sensors;

        for(int i=0; i<sensors.size(); i++)
        {
          timestamps.add(new ArrayList<Long>());
          values.add(new ArrayList<Object>());
        }
      }

      @Override
      public void run()
      {
        while(run)
        {
          // Get devices values
          try
          {
            for(int i=0; i<sensors.size(); i++)
            {
              long timestamp = System.currentTimeMillis();
              Object value = sensors.get(i).update();
              timestamps.get(i).add(timestamp);
              values.get(i).add(value);
            }
          }
          catch(Exception ex)
          {
            System.err.println("Unable to read/store device value, error: " + ex);
          }

          // Sleep capture time
          try
          {
            //long t0 = System.currentTimeMillis();
            sleep(sleepTime);
            //long t1 = System.currentTimeMillis();
            //debug("Elapsed time sleeping while capturing data: " + (t1-t0));
          }
          catch(InterruptedException ignore)
          {
          }
        }
      }
    }
    //CaptureThread captureThread = new CaptureThread(CAPTURE_TIME);
    List<CaptureThread> captureThreads = new ArrayList<CaptureThread>();
    if(!delaysList.isEmpty())
    {
      for(int i=0; i<delaysList.size(); i++)
      {
        long delay = delaysList.get(i);
        List<SensorDevice> sensors = capturesLists.get(i);
        captureThreads.add(new CaptureThread(delay, sensors));
      }
    }

    ///////////////////////////////////
    // Script thread
    ///////////////////////////////////

    class ScriptThread extends Thread
    {
      ScriptRunner scriptRunner = new ScriptRunner();

      public ScriptThread(List<Order> orders)
      {
        scriptRunner.setOrders(orders);
      }

      @Override
      public void run()
      {
        while(!scriptRunner.isScriptEnded())
        {
          // Get current order
          Order order = scriptRunner.getNextOrder();

          // Run order
          try
          {
            MOTOR.setVelocity(order.getVelocity());
            SERVO.setPosition(order.getDirection());
            System.out.printf("Current order: %d duration, %d velocity, %d direction\n", order.getDuration(), order.getVelocity(), order.getDirection());
          }
          catch(Exception ex)
          {
            System.err.println("Unable to run order, error: " + ex);
          }

          // Sleep order time
          try
          {
            long t0 = System.currentTimeMillis();
            sleep(order.getDuration());
            long t1 = System.currentTimeMillis();
            System.out.println("Elapsed time sleeping while executing an order: " + (t1-t0));
          }
          catch(InterruptedException ignore)
          {
          }
        }
        System.out.println("Script ended");
      }
    }
    ScriptThread scriptThread = new ScriptThread(SCRIPT.getOrders().getOrder());

    ///////////////////////////////////
    // Start threads
    ///////////////////////////////////

    for(CaptureThread captureThread : captureThreads)
    {
      captureThread.start();
    }
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

    // Early stop
    for(CaptureThread captureThread : captureThreads)
    {
      captureThread.run = false;
    }

    // Wait all to stop
    for(CaptureThread captureThread : captureThreads)
    {
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
    }

    ///////////////////////////////////
    // Dump captured info to capture file
    ///////////////////////////////////

    // dump lists
    for(CaptureThread captureThread : captureThreads)
    {
      for(int i=0; i<captureThread.sensors.size(); i++)
      {
        try
        {
          System.out.println("Sensor: " + captureThread.sensors.get(i).getName());
          System.out.println("Timestamps: " + captureThread.timestamps.get(i).size());
          System.out.println("Values: " + captureThread.values.get(i).size());
        }
        catch (RemoteException ex)
        {
          ex.printStackTrace();
        }
      }
    }

    /*System.out.println("Creating capture file: " + CAPTURE_FILE);
    try
    {
      // Open file
      CAPTURE_FILE = new File(CAPTURE_PATH);
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

      System.out.println("Capture file created");
    }
    catch(Exception ex)
    {
      System.err.println("Unable to write captured values, error: " + ex);
      System.exit(1);
    }*/

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
}
