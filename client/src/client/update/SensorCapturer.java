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
package client.update;

import client.devicelist.DeviceInfo;
import client.servercomm.ConnectException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Captures sensor data from an {@link Updater}.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class SensorCapturer extends Observable implements Observer
{
    private Updater updater;

    private String deviceName;
    private int deviceID;

    private List<Long> time = new ArrayList();
    private List data = new ArrayList();
    
    private boolean capturing = false;

    private long startTime;   

    /**
     * Creates a SensorCapturer.
     * @param info Needed to obtain Updater and info about the device.
     */
    public SensorCapturer(DeviceInfo info)
    {
        try
        {
            deviceName = info.getDevice().getName();
            deviceID = info.getDevice().getID();            
        }        
        catch (ConnectException ex)
        {
            ex.printStackTrace();
        }
        catch (RemoteException ex)
        {
            ex.printStackTrace();
        }
        updater = info.getUpdater();
    }

    /**
     * Starts to capture sensor data.
     */
    public void startCapture()
    {
        if(!capturing)
        {
            updater.addObserver(this);
            if(!updater.getAutoUpdating())
            {
                updater.autoUpdate(true);
            }
            capturing = true;
            startTime = System.currentTimeMillis();
        }
    }

    /**
     * Stops to capture sensor data.
     */
    public void stopCapture()
    {
        if(capturing)
        {            
            updater.deleteObserver(this);
            capturing = false;
        }
    }

    /**
     * Returns if the capture is started or not.
     * @return True if capture is started, false if it isn't.
     */
    public boolean isCapturing()
    {
        return capturing;
    }

    public void update(Observable o, Object arg)
    {
        if(capturing)
        {
            time.add(System.currentTimeMillis() - startTime);
            data.add(arg);
            this.setChanged();
            this.notifyObservers(new Integer(time.size()-1));
        }
    }

    /**
     * Remove all captured data. This won't stop a started capture.
     */
    public void clearCapture()
    {
        time.clear();
        data.clear();
        this.setChanged();
        this.notifyObservers(new Integer(time.size()));
    }

    /**
     * Returns the name of the device of this capture.
     * @return Name of the device.
     */
    public String getName()
    {
        return deviceName;
    }

    /**
     * Returns the ID of the device of this capture.
     * @return ID of the device.
     */
    public int getID()
    {
        return deviceID;
    }

    /**
     * Returns the number of data captured.
     * @return Number of data captured.
     */
    public int getSize()
    {
        return data.size();
    }

    /**
     * Obtains an specific time of the capture.
     * @param index Index of the time between 0 and getSize().
     * @return Time at that index.
     */
    public Long getTime(int index)
    {
        return time.get(index);
    }

    /**
     * Returns an array of length getSize() with times of the capture.     
     * @return An array with times of the capture.
     */
    public Long[] getTimeArray()
    {
        return time.toArray(new Long[0]);
    }

    /**
     * Obtains an specific data of the capture.
     * @param index Index of the data between 0 and getSize().
     * @return Data at that index.
     */
    public Object getData(int index)
    {
        return data.get(index);
    }

    /**
     * Returns an array of length getSize() with data of the capture.
     * @return An array with data of the capture.
     */
    public Object[] getDataArray()
    {
        return data.toArray();
    }

    /**
     * Returns the Updater used in the capture.
     * @return Updater used in the capture.
     */
    public Updater getUpdater()
    {
        return updater;
    }

    @Override
    public String toString()
    {
        return getName() + " " + getID();
    }    
}
