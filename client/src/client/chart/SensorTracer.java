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

import client.update.SensorCapturer;
import info.monitorenter.gui.chart.ITrace2D;
import java.awt.BasicStroke;
import java.util.Observable;
import java.util.Observer;

/**
 * Observs a SensorCapturer to capturer sensor data and plot it in a ITrace2D.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
class SensorTracer implements Observer
{

    private ITrace2D trace;

    public SensorTracer(ITrace2D newTrace)
    {
        trace = newTrace;
        trace.setStroke(new BasicStroke(2.f));
    }

    public ITrace2D getTracer()
    {
        return trace;
    }

    public void update(Observable o, Object arg)
    {
        SensorCapturer capture = (SensorCapturer) o;
        Integer index = (Integer) arg;
        Object data = capture.getData(index);
        if (data instanceof Number)
        {
            trace.addPoint(capture.getTime(index), ((Number) data).doubleValue());
        }
    }
}
