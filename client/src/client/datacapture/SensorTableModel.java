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
package client.datacapture;

import client.ClientApp;
import client.update.SensorCapturer;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import javax.swing.table.AbstractTableModel;

/**
 * Customized AbstractTableModel for JTable in CaptureFrame.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
class SensorTableModel extends AbstractTableModel implements Observer
{
    public final String[] columnNames =
    {
        ClientApp.getBundleString("DEVICE NAME"),
        ClientApp.getBundleString("DEVICE ID"),
        ClientApp.getBundleString("DEVICE TIME"),
        ClientApp.getBundleString("DEVICE DATA")
    };

    private List<SensorDataRow> sensorData = new ArrayList();

    public void addRow(SensorDataRow data)
    {
        sensorData.add(data);
        this.fireTableRowsInserted(sensorData.size() - 1, sensorData.size() - 1);
    }

    public void clearTable()
    {
        sensorData.clear();
        this.fireTableDataChanged();
    }

    public Object[][] getTable()
    {
        Object[][] table = new Object[sensorData.size()][columnNames.length];
        SensorDataRow[] data = sensorData.toArray(new SensorDataRow[0]);
        for (int i = 0; i < data.length; i++)
        {
            table[i][0] = data[i].getName();
            table[i][1] = data[i].getID();
            table[i][2] = data[i].getTime();
            table[i][3] = data[i].getData();
        }
        return table;
    }

    @Override
    public String getColumnName(int column)
    {
        if (column < columnNames.length)
        {
            return columnNames[column];
        } else
        {
            return new String();
        }
    }

    public int getRowCount()
    {
        return sensorData.size();
    }

    public int getColumnCount()
    {
        return columnNames.length;
    }

    public Object getValueAt(int rowIndex, int columnIndex)
    {
        SensorDataRow data = sensorData.get(rowIndex);
        switch (columnIndex)
        {
            case 0:
                return data.getName();
            case 1:
                return data.getID();
            case 2:
                return data.getTime();
            case 3:
                return data.getData();
            default:
                return null;
        }
    }

    public void update(Observable o, Object arg)
    {
        SensorCapturer capture = (SensorCapturer) o;
        Integer index = (Integer) arg;
        this.addRow(new SensorDataRow(capture.getName(), capture.getID(), capture.getTime(index), capture.getData(index)));
    }
}
