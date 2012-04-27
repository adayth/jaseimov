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
 * RMI interface for a camera.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public interface Camera extends SensorDevice
{
    /**
     * Returns the width in pixels of the images captured by this camera.
     * @return Width in pixels.
     * @throws RemoteException
     */
    int getImageWidth() throws RemoteException;

    /**
     * Returns the height in pixels of the images captured by this camera.
     * @return Height in pixels.
     * @throws RemoteException
     */
    int getImageHeigth() throws RemoteException;

    /**
     * Set the compression value used by the camera.
     * @param value Compressión value between 0 and 100.
     * @throws RemoteException
     * @throws DeviceException
     */
    void setCompression(int value)  throws RemoteException, DeviceException;

    /**
     * Returns an image captured by the camera when its called.
     * @return An array of bytes with the image.
     * @throws RemoteException
     * @throws DeviceException
     */
    byte[] getImage() throws RemoteException, DeviceException;
}
