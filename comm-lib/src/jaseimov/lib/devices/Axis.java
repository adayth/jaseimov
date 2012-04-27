package jaseimov.lib.devices;

import java.rmi.RemoteException;

/**
 * RMI interface for a sensor value for an axis.
 */
public interface Axis extends SensorDevice
{
  // Axis X,Y,Z index
  final public static int X_AXIS = 0;
  final public static int Y_AXIS = 1;
  final public static int Z_AXIS = 2;

  // Axis x,y,z names
  final public static String[] AXIS_NAMES = {"X", "Y", "Z"};

  /**
   * Returns the sensor value for this axis.
   * @return Value.
   * @throws RemoteException
   * @throws DeviceException
   */
  double getAxisValue() throws RemoteException, DeviceException;
}
