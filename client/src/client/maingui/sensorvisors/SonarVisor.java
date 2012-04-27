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
package client.maingui.sensorvisors;

import client.ClientApp;
import client.devicelist.DeviceInfo;
import java.util.Observable;

/**
 * SensorVisor associated to a Sonar device.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class SonarVisor extends SensorVisor
{
    SensorPanel distancePanel;

    public SonarVisor(DeviceInfo info)
    {
        super(info);

        // Add panel for view sensor variables
        distancePanel = new SensorPanel(ClientApp.getBundleString("DISTANCE"), ClientApp.getBundleString("CM"));

        addPanel(distancePanel);
    }

    public void update(Observable o, Object arg)
    {
        Double distance = (Double) arg;
        distancePanel.setVariable(String.format(NUMBER_FORMAT, distance));
    }
}
