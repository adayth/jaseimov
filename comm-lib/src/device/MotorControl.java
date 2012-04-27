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

package device;

import java.rmi.RemoteException;

/**
 * RMI interface for a motor controller
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public interface MotorControl extends ControlDevice
{
    /**
     * Returns the current velocity of the controller.
     * @return Current velocity of the controller.
     * @throws RemoteException
     * @throws DeviceException
     */
    double getVelocity() throws RemoteException, DeviceException;

    /**
     * Changes the velocity of the controller.
     * @param v New velocity between getMinVelocity() and getMaxVelocity().
     * @throws RemoteException
     * @throws DeviceException
     */
    void setVelocity(double v) throws RemoteException, DeviceException;

    /**
     * Returns the current acceleration of the controller.
     * @return Current acceleration of the controller.
     * @throws RemoteException
     * @throws DeviceException
     */
    double getAcceleration() throws RemoteException, DeviceException;

    /**
     * Changes the acceleration of the controller.
     * @param a New acceleration between getMinAcceleration() and getMaxAcceleration().
     * @throws RemoteException
     * @throws DeviceException
     */
    void setAcceleration(double a) throws RemoteException, DeviceException;

    /**
     * Returns minimum avalaible velocity of the controller.
     * @return Minimum velocity.
     * @throws RemoteException
     * @throws DeviceException
     */
    double getMinVelocity() throws RemoteException, DeviceException;

    /**
     * Returns maximum avalaible velocity of the controller.
     * @return Maximum velocity.
     * @throws RemoteException
     * @throws DeviceException
     */
    double getMaxVelocity() throws RemoteException, DeviceException;

    /**
     * Returns minimum avalaible acceleration of the controller.
     * @return Minimum acceleration.
     * @throws RemoteException
     * @throws DeviceException
     */
    double getMinAcceleration() throws RemoteException, DeviceException;

    /**
     * Returns maximum avalaible acceleration of the controller.
     * @return Maximum acceleration.
     * @throws RemoteException
     * @throws DeviceException
     */
    double getMaxAcceleration() throws RemoteException, DeviceException;

    /**
     * Stops the motor.
     * @throws RemoteException
     * @throws DeviceException
     */
    void stopMotor() throws RemoteException, DeviceException;
}