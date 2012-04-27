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
package jaseimov.server;

import jaseimov.lib.devices.Device;
import jaseimov.lib.devices.DevicePosition;
import jaseimov.lib.devices.AbstractDevice;
import jaseimov.lib.devices.Accelerometer;
import jaseimov.lib.devices.Axis;
import jaseimov.server.devicetest.CameraTest;
import jaseimov.server.devicetest.IRTest;
import jaseimov.server.devicetest.MotorControlTest;
import jaseimov.server.devicetest.MouseEncoderTest;
import jaseimov.server.devicetest.ServoControlTest;
import jaseimov.server.devicetest.SonarTest;
import jaseimov.server.devicetest.SpatialTest;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Main class of JASEIMOV Server.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public final class ServerApp
{
  /**
   * RMI port to be used in the RMI registry.
   */
  public static int rmiPort = java.rmi.registry.Registry.REGISTRY_PORT;
  private ServiceList list;
  private Registry rmiRegistry;

  /**
   * Creates a new Server.
   */
  public ServerApp()
  {
    list = new ServiceList(rmiPort);
  }

  /**
   * Configure the server with a configuration file.
   * @param configFile Configuration file.
   */
  public void configure(String configFile)
  {
    System.out.println("Reading configuration from " + configFile);

    try
    {
      AbstractDevice[] deviceList = Configurer.readDeviceConfigFile(configFile);
      for (AbstractDevice device : deviceList)
      {
        list.addService(new ServiceRMIDevice(device, rmiPort));
      }
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
    }
  }

  /**
   * Configure the server with a predefined list of virtual devices of server.devicetest package.
   * This is for debugging JASEIMOV client.
   */
  public void configureTest()
  {
    System.out.println("Configuring test devices");
    // Initialize a list of fake devices for testing
    List<AbstractDevice> testList = new ArrayList<AbstractDevice>();

    // Test devices    
    AbstractDevice device;

    MotorControlTest motorTest = new MotorControlTest();
    testList.add(motorTest);
    testList.add(new ServoControlTest());

    /*AccelerometerTest accel = new AccelerometerTest();
    accel.setDevicePosition(DevicePosition.CENTER);
    testList.add(accel);
    testList.add(accel.getAxisDevice(Axis.X_AXIS));
    testList.add(accel.getAxisDevice(Axis.Y_AXIS));
    testList.add(accel.getAxisDevice(Axis.Z_AXIS));*/

    SpatialTest spatial = new SpatialTest();
    spatial.setDevicePosition(DevicePosition.CENTER);
    testList.add(spatial);
    testList.add(spatial.getAccelAxisDevice(Axis.X_AXIS));
    testList.add(spatial.getAccelAxisDevice(Axis.Y_AXIS));
    testList.add(spatial.getAccelAxisDevice(Axis.Z_AXIS));
    motorTest.configAutoControl((Accelerometer)spatial);

    testList.add(spatial.getCompassAxisDevice(Axis.X_AXIS));
    testList.add(spatial.getCompassAxisDevice(Axis.Y_AXIS));
    testList.add(spatial.getCompassAxisDevice(Axis.Z_AXIS));

    testList.add(spatial.getGyroAxisDevice(Axis.X_AXIS));
    testList.add(spatial.getGyroAxisDevice(Axis.Y_AXIS));
    testList.add(spatial.getGyroAxisDevice(Axis.Z_AXIS));


    device = new MouseEncoderTest();
    device.setDevicePosition(DevicePosition.BACK_RIGHT_WHEEL);
    testList.add(device);
    device = new MouseEncoderTest();
    device.setDevicePosition(DevicePosition.BACK_LEFT_WHEEL);
    testList.add(device);
    device = new MouseEncoderTest();
    device.setDevicePosition(DevicePosition.FRONT_RIGHT_WHEEL);
    testList.add(device);
    device = new MouseEncoderTest();
    device.setDevicePosition(DevicePosition.FRONT_LEFT_WHEEL);
    testList.add(device);

    device = new SonarTest();
    device.setDevicePosition(DevicePosition.FRONT_LEFT_CORNER);
    testList.add(device);
    device = new SonarTest();
    device.setDevicePosition(DevicePosition.FRONT_RIGHT_CORNER);
    testList.add(device);
    device = new SonarTest();
    device.setDevicePosition(DevicePosition.FRONT_CENTER_TOP);
    testList.add(device);
    device = new IRTest();
    device.setDevicePosition(DevicePosition.FRONT_CENTER_BOTTOM);
    testList.add(device);
    device = new IRTest();
    device.setDevicePosition(DevicePosition.FRONT_LEFT);
    testList.add(device);
    device = new IRTest();
    device.setDevicePosition(DevicePosition.FRONT_RIGHT);
    testList.add(device);

    device = new IRTest();
    device.setDevicePosition(DevicePosition.LEFT_FRONT);
    testList.add(device);
    device = new SonarTest();
    device.setDevicePosition(DevicePosition.LEFT_CENTER);
    testList.add(device);
    device = new IRTest();
    device.setDevicePosition(DevicePosition.LEFT_BACK);
    testList.add(device);

    device = new IRTest();
    device.setDevicePosition(DevicePosition.RIGHT_FRONT);
    testList.add(device);
    device = new SonarTest();
    device.setDevicePosition(DevicePosition.RIGHT_CENTER);
    testList.add(device);
    device = new IRTest();
    device.setDevicePosition(DevicePosition.RIGHT_BACK);
    testList.add(device);

    device = new SonarTest();
    device.setDevicePosition(DevicePosition.BACK_LEFT_CORNER);
    testList.add(device);
    device = new SonarTest();
    device.setDevicePosition(DevicePosition.BACK_RIGHT_CORNER);
    testList.add(device);
    device = new IRTest();
    device.setDevicePosition(DevicePosition.BACK_LEFT);
    testList.add(device);
    device = new IRTest();
    device.setDevicePosition(DevicePosition.BACK_CENTER);
    testList.add(device);
    device = new IRTest();
    device.setDevicePosition(DevicePosition.BACK_RIGHT);
    testList.add(device);

    device = new CameraTest();
    device.setDevicePosition(DevicePosition.FRONT_LEFT_CAMERA);
    testList.add(device);

    device = new CameraTest();
    device.setDevicePosition(DevicePosition.FRONT_RIGHT_CAMERA);
    testList.add(device);

    device = new CameraTest();
    device.setDevicePosition(DevicePosition.BACK_LEFT_CAMERA);
    testList.add(device);

    device = new CameraTest();
    device.setDevicePosition(DevicePosition.BACK_RIGHT_CAMERA);
    testList.add(device);

    // Add to service list
    for (AbstractDevice dev : testList)
    {
      list.addService(new ServiceRMIDevice(dev, rmiPort));
    }

  }

  /**
   * Starts the server creating the RMI registry and all configured services.
   */
  public void startServer()
  {
    System.out.println("Starting server");
    try
    {
      rmiRegistry = LocateRegistry.createRegistry(rmiPort);
      list.startServices();
      list.bindList();

      // Hook to close devices when server is shutdown
      Runtime.getRuntime().addShutdownHook(
        new Thread()
        {
          @Override
          public void run()
          {
            stopServer();
          }
        }
        );
    }
    catch (Exception ex)
    {
      System.err.println("Error starting server");
      ex.printStackTrace();
      System.exit(1);
    }
  }

  /**
   * Stops the server deleting the RMI registry.
   */
  public void stopServer()
  {
    System.out.println("Stopping server");
    try
    {
      list.stopServices();
      list.unbindList();
      UnicastRemoteObject.unexportObject(rmiRegistry, true);
    }
    catch (Exception ex)
    {
      System.err.println("Error stopping server");
      ex.printStackTrace();
      System.exit(1);
    }
  }

  /**
   * Start server application.
   * @param args One of two: "-test"  or "-configure $configuration_file".

   */
  public static void main(String[] args)
  {
    System.out.println("JASEIMOV Server");
    ServerApp server = new ServerApp();

    if (args.length > 0 && args[0].equals("-test"))
    {
      server.configureTest();
    }
    else if (args.length > 1 && args[0].equals("-configure"))
    {
      server.configure(args[1]);
    }
    else
    {
      System.out.println("Not enough parameters. Valid options:\n\t-test\n\t-configure $configuration_file");
      System.exit(1);
    }

    server.startServer();

    try
    {
      System.out.println("List of devices avalaible:");
      System.out.println("-------------------------------------------------------------------------------------------");
      System.out.format("|%30s |%6s |%22s |%24s |%n", "Name", "ID", "Device type", "Device position");
      System.out.println("-------------------------------------------------------------------------------------------");
      ServiceDevice services[] = server.list.getServices();
      if (services.length > 0)
      {
        for (ServiceDevice service : services)
        {
          Device device = service.getDevice();
          System.out.format("|%30s |%6s |%22s |%24s |%n", device.getName(), device.getID(), device.getDeviceType(), device.getDevicePosition());
        }
        System.out.println("-------------------------------------------------------------------------------------------");
      }
      else
      {
        System.out.println("Empty service list");
      }

      java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
      System.out.println("\nEnter character, 'q' to quit");
      char c;
      while ((c = (char) br.read()) != 'q')
      {
        continue;
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }

    //server.stopServer();
    System.exit(0);
  }
}
