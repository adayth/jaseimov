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
package jaseimov.client.chart;

import jaseimov.lib.remote.list.RemoteDeviceInfo;
import jaseimov.lib.remote.utils.SensorCapturer;
import jaseimov.client.utils.Utils;
import info.monitorenter.gui.chart.traces.Trace2DSimple;
import java.awt.Color;

/**
 * Contains a SensorCapturer and a SensorTracer associated to it.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
class SensorChartInfo
{
  private SensorCapturer capturer;
  private SensorTracer tracer;

  public SensorChartInfo(RemoteDeviceInfo info)
  {
    capturer = new SensorCapturer(info);
    tracer = new SensorTracer(new Trace2DSimple(capturer.toString()));
    try
    {
      Color color = Utils.getColorByDeviceType(info.getDevice().getDeviceType());
      tracer.getTracer().setColor(color);
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
