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
import java.util.Observable;

/**
 * SensorVisor associated to an Spatial device.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class SpatialVisor extends SensorVisor
{
  SensorPanel xAccelPanel;
  SensorPanel yAccelPanel;
  SensorPanel zAccelPanel;
  SensorPanel xCompassPanel;
  SensorPanel yCompassPanel;
  SensorPanel zCompassPanel;
  SensorPanel xGyroPanel;
  SensorPanel yGyroPanel;
  SensorPanel zGyroPanel;

  public SpatialVisor(RemoteDeviceInfo info)
  {
    super(info);

    // Add panel for view sensor variables

    // Accelerometer
    xAccelPanel = new SensorPanel("AX:", ClientApp.getBundleString("ACCELEROMETER_UNIT"));
    yAccelPanel = new SensorPanel("AY:", ClientApp.getBundleString("ACCELEROMETER_UNIT"));
    zAccelPanel = new SensorPanel("AZ:", ClientApp.getBundleString("ACCELEROMETER_UNIT"));

    addPanel(xAccelPanel);
    addPanel(yAccelPanel);
    addPanel(zAccelPanel);

    // Compass
    xCompassPanel = new SensorPanel("BX:", "G");
    yCompassPanel = new SensorPanel("BY:", "G");
    zCompassPanel = new SensorPanel("BZ:", "G");

    addPanel(xCompassPanel);
    addPanel(yCompassPanel);
    addPanel(zCompassPanel);

    // Gyroscope
    xGyroPanel = new SensorPanel("CX:", "º/s");
    yGyroPanel = new SensorPanel("CY:", "º/s");
    zGyroPanel = new SensorPanel("CZ:", "º/s");

    addPanel(xGyroPanel);
    addPanel(yGyroPanel);
    addPanel(zGyroPanel);
  }

  public void update(Observable o, Object arg)
  {
    double v[][] = (double[][]) arg;
    if (v.length >= 3)
    {
      // Accelerometer
      if (v[0].length >= 3)
      {
        xAccelPanel.setVariable(String.format(NUMBER_FORMAT, v[0][0]));
        yAccelPanel.setVariable(String.format(NUMBER_FORMAT, v[0][1]));
        zAccelPanel.setVariable(String.format(NUMBER_FORMAT, v[0][2]));
      }

      // Compass
      if (v[1].length >= 3)
      {
        xCompassPanel.setVariable(String.format(NUMBER_FORMAT, v[1][0]));
        yCompassPanel.setVariable(String.format(NUMBER_FORMAT, v[1][1]));
        zCompassPanel.setVariable(String.format(NUMBER_FORMAT, v[1][2]));
      }

      // Gyroscope
      if (v[2].length >= 3)
      {
        xGyroPanel.setVariable(String.format(NUMBER_FORMAT, v[2][0]));
        yGyroPanel.setVariable(String.format(NUMBER_FORMAT, v[2][1]));
        zGyroPanel.setVariable(String.format(NUMBER_FORMAT, v[2][2]));
      }
    }
  }
}
