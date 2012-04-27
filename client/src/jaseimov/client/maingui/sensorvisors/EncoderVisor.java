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
package jaseimov.client.maingui.sensorvisors;

import jaseimov.client.ClientApp;
import jaseimov.lib.remote.list.RemoteDeviceInfo;
import jaseimov.lib.devices.Encoder;
import java.util.Observable;

/**
 * SensorVisor associated to an Encoder device.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class EncoderVisor extends SensorVisor
{
    private SensorPanel ticsPanel;
    private SensorPanel distancePanel;
    private SensorPanel velocityPanel;

    private double factor;

    public EncoderVisor(RemoteDeviceInfo info)
    {
        super(info);

        // Add panel for view sensor variables
        ticsPanel = new SensorPanel(ClientApp.getBundleString("ENCODER"), ClientApp.getBundleString("TICS"));
        addPanel(ticsPanel);
        
        // Get factor for calculate distance
        try
        {            
            factor = ((Encoder)info.getDevice()).getCmPerTic();
        }
        // Catch ConnectException / RemoteException
        catch(Exception ex)
        {
            ex.printStackTrace();
        }        

        distancePanel = new SensorPanel(ClientApp.getBundleString("DISTANCE"),ClientApp.getBundleString("CM"));
        addPanel(distancePanel);
        velocityPanel = new SensorPanel(ClientApp.getBundleString("VELOCITY"),ClientApp.getBundleString("CM/S"));
        addPanel(velocityPanel);
    }

    // Elapsed time beetwen updates, for calculate velocity
    private long t0 = System.currentTimeMillis();
    
    public void update(Observable o, Object arg)
    {
        // Tics
        Integer tics = (Integer) arg;
        ticsPanel.setVariable(tics.toString());
        // Distance
        Double distance = factor * tics.doubleValue();
        distancePanel.setVariable(String.format(NUMBER_FORMAT, distance));
        //Velocity
        long t1 = System.currentTimeMillis();
        Double velocity = 1000* (distance / (t1-t0));
        t0 = t1;
        velocityPanel.setVariable(String.format(NUMBER_FORMAT, velocity));
    }

}
