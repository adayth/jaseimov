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
package client.chart;

import client.devicelist.DeviceInfo;
import client.update.SensorCapturer;
import info.monitorenter.gui.chart.traces.Trace2DSimple;

/**
 * Contains a SensorCapturer and a SensorTracer associated to it.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
class SensorChartInfo
{

    private SensorCapturer capturer;
    private SensorTracer tracer;

    public SensorChartInfo(DeviceInfo info)
    {
        capturer = new SensorCapturer(info);
        tracer = new SensorTracer(new Trace2DSimple(capturer.toString()));
        try
        {
            tracer.getTracer().setColor(info.getDevice().getDeviceType().getColor());
        } 
        // Catch ConnectException / RemoteException
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public SensorCapturer getSensorCapturer()
    {
        return capturer;
    }

    public SensorTracer getSensorTracer()
    {
        return tracer;
    }

    @Override
    public String toString()
    {
        return capturer.toString();
    }
}
