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
package jaseimov.client.datacapture;

/**
 * A row of sensor data to be stored in {@link SensorTableModel}.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
class SensorDataRow
{

    private String sensorName;
    private Integer sensorID;
    private Long dataTime;
    private Object sensorData;

    public SensorDataRow(String name, Integer ID, Long time, Object data)
    {
        sensorName = name;
        sensorID = ID;
        dataTime = time;
        sensorData = data;
    }

    public String getName()
    {
        return sensorName;
    }

    public Integer getID()
    {
        return sensorID;
    }

    public Long getTime()
    {
        return dataTime;
    }

    public Object getData()
    {
        if(sensorData instanceof Number)        
            if(!(sensorData instanceof Integer))
            {
                return String.format("%1$.4f", sensorData);
            }
        return sensorData;
    }
}
