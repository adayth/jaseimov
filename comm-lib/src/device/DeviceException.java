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

/**
 * Exception to capture all exceptions related to device hardware
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class DeviceException extends Exception
{
    /**
     * Creates a DeviceException without message.
     */
    public DeviceException()
    {
        super();
    }

    /**
     * Creates a DeviceException whith an error message.
     * @param msg Error message provided by another exception.
     */
    public DeviceException(String msg)
    {
        super(msg);
    }
}
