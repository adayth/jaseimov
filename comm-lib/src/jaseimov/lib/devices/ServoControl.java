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
package jaseimov.lib.devices;

import java.rmi.RemoteException;

/**
 * RMI Interface for a servomotor controller.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public interface ServoControl extends ControlDevice
{

  /**
   * Returns current position of the servomotor.
   * @return A position between getMinPosition() and getMaxPosition().
   * @throws RemoteException
   * @throws DeviceException
   */
  double getPosition() throws RemoteException, DeviceException;

  /**
   * Changes the position of the servomotor.
   * @param p The new position between getMinPosition() and getMaxPosition().
   * @throws RemoteException
   * @throws DeviceException
   */
  void setPosition(double p) throws RemoteException, DeviceException;

  /**
   * Returns if the servomotor is engaged or not.
   * If the servomotor isn't engaged, setPosition() don't changes servo position.
   * @return True if the servomotor is engaged, false if it isn't.
   * @throws RemoteException
   * @throws DeviceException
   */
  boolean getEngaged() throws RemoteException, DeviceException;

  /**
   * Engages or disengages the servomotor.
   * @param b True to engage the servomotor, false to disengage it.
   * @throws RemoteException
   * @throws DeviceException
   */
  void setEngaged(boolean b) throws RemoteException, DeviceException;

  /**
   * Returns minimum avalaible position of controller.
   * @return Minimum position.
   * @throws RemoteException
   * @throws DeviceException
   */
  double getMinPosition() throws RemoteException, DeviceException;

  /**
   * Returns maximum avalaible position of controller.
   * @return Maximum position.
   * @throws RemoteException
   * @throws DeviceException
   */
  double getMaxPosition() throws RemoteException, DeviceException;

  /**
   * Returns a initial safe position for the servomotor.
   * @return A initial position.
   * @throws RemoteException
   * @throws DeviceException
   */
  double getStartPosition() throws RemoteException, DeviceException;

  /**
   * Set the servomotor to startPosition.
   * Equals to setPosition(getStartPosition());
   * @throws RemoteException
   * @throws DeviceException
   */
  void resetPosition() throws RemoteException, DeviceException;
}
