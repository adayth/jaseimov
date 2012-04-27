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
package client.servercomm;

import client.ClientApp;

/**
 * Defines the possible exceptions of ClientDevice.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class ConnectException extends Exception
{
    final static String ALREADY_CONNECTED = ClientApp.getBundleString("CONNECT EXCEPTION 1");
    final static String NOT_CONNECTED = ClientApp.getBundleString("CONNECT EXCEPTION 2");
    final static String ID_DONT_MATCH = ClientApp.getBundleString("CONNECT EXCEPTION 3");

    public ConnectException() 
    {        
    }
    
    public ConnectException(String msg)
    {
        super(msg);
    }

    public ConnectException(Exception ex)
    {
        super(ex.getMessage());
    }
}