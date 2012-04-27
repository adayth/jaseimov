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

import device.Camera;
import device.DeviceException;
import device.SensorDevice;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.rmi.RemoteException;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Updates data of SensorDevice using observer design pattern. Updates can be
 * scheduled to be done periodically with a period in milliseconds defined as update time.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class Updater extends Observable
{
    /**
     * Default update time for an Updater.
     */
    public static int DEFAULT_UPDATE_TIME = 100;    

    final static private ScheduledThreadPoolExecutor sensorThreadPool = new ScheduledThreadPoolExecutor(1);
    final static private ScheduledThreadPoolExecutor cameraThreadPool = new ScheduledThreadPoolExecutor(1);

    private ScheduledThreadPoolExecutor timer;

    private int updateTime;    
    private SensorDevice sensor;
    private boolean autoUpdate;

    private class UpdateTask implements Runnable
    {        
        @Override
        public void run()
        {
            update();            
        }
    }

    private UpdateTask task = new UpdateTask();
    private ScheduledFuture taskControl;

    /**
     * Name of autoupdate property value.
     */
    final public static String AUTOUPDATE_VALUE = "autoupdate";
    /**
     * Name of update time property value.
     */
    final public static String UPDATE_TIME_VALUE = "udpate_time";
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /**
     * Creates an Updater.
     * @param sensor SensorDevice to be updated.
     */
    public Updater(SensorDevice sensor)
    {
        this.sensor = sensor;        
        updateTime = Updater.DEFAULT_UPDATE_TIME;
        autoUpdate = false;

        if(sensor instanceof Camera)
        {            
            timer = cameraThreadPool;
        }
        else
        {
            timer = sensorThreadPool;
        }
    }

    @Override
    public void addObserver(Observer observer)
    {
        super.addObserver(observer);
    }

    @Override
    public void	deleteObserver(Observer observer)
    {
        super.deleteObserver(observer);
        // If no more observer, stop autoupdate task
        if(countObservers() == 0)
        {
            autoUpdate(false);
        }
    }

    /**
     * Adds a new PropertyChangeListener to the Updater properties.
     * @param l PropertyChangeListener to be added.
     */
    public void addPropertyListener(PropertyChangeListener l)
    {
        pcs.addPropertyChangeListener(l);
    }

    /**
     * Removes a PropertyChangeListener from the Updater properties.
     * @param l PropertyChangeListener to be removed.
     */
    public void removePropertyListener(PropertyChangeListener l)
    {
        pcs.removePropertyChangeListener(l);
    }

    /**
     * Activates or deactivates auto updates of sensor data. Fires an autoupdate property change.
     * @param auto If true, activates auto update. If false, deactivates it.
     */
    public void autoUpdate(boolean auto)
    {
        if(auto)
        {
            if(!autoUpdate)
            {                
                autoUpdate = true;                
                taskControl = timer.scheduleAtFixedRate(task, 0, (long)updateTime, TimeUnit.MILLISECONDS);
                pcs.firePropertyChange(AUTOUPDATE_VALUE, false, true);
            }
        }
        else
        {
            if(autoUpdate)
            {
                autoUpdate = false;                
                taskControl.cancel(false);
                pcs.firePropertyChange(AUTOUPDATE_VALUE, true, false);
            }
        }
    }

    /**
     * Performs an update of sensor data. All observers will be notified with the
     * new data.
     * @return The data obtained from the sensor.
     */
    public Object update()
    {
        try
        {
            Object sensorData = sensor.update();
            this.setChanged();
            this.notifyObservers(sensorData);
            return sensorData;
        }
        catch(RemoteException ex)
        {
            ex.printStackTrace();
            autoUpdate(false);
        }
        catch(DeviceException ex)
        {
            ex.printStackTrace();
            autoUpdate(false);
        }
        return null;
    }

    /**
     * Returns if automatic update is enabled or not.
     * @return True if is enabled, false it isn't.
     */
    public boolean getAutoUpdating()
    {
        return autoUpdate;
    }

    /**
     * Returns the update time.
     * @return Update time.
     */
    public int getUpdateTime()
    {
        return updateTime;
    }

    /**
     * Changes the update time. Fires an update time property change.
     * @param time New update time.
     */
    public void setUpdateTime(int time)
    {
        if(updateTime != time)
        {
            if(time != 0)
            {
                pcs.firePropertyChange(UPDATE_TIME_VALUE, updateTime, time);

                updateTime = time;

                if(autoUpdate)
                {
                    taskControl.cancel(false);
                    taskControl = timer.scheduleAtFixedRate(task, 0, (long)updateTime, TimeUnit.MILLISECONDS);
                }
            }
            else
            {
                throw new IllegalArgumentException("Update time can not be zero");
            }
        }
    }    

}