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
package jaseimov.client.dialogs;

import jaseimov.client.utils.Command;
import jaseimov.lib.remote.utils.SensorUpdater;
import java.awt.Component;

/**
 * Command that creates and show a {@link TimeDialog}.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class ShowTimeDialog extends Command
{
    private static final String name = "show-time-dialog";

    private Component parent;
    private SensorUpdater[] updaterList;

    /**
     * Creates a ShowTimeDialog.
     * @param c Position of the dialog will be relative to this component.
     * @param list An array of SensorUpdater objects to be used by TimeDialog.
     */
    public ShowTimeDialog(Component c, SensorUpdater[] list)
    {
        super(name);
        parent = c;
        updaterList = list;
    }

    /**
     * Creates a ShowTimeDialog.
     * @param c Position of the dialog will be relative to this component.
     * @param updater The SensorUpdater object to be used by TimeDialog.
     */
    public ShowTimeDialog(Component c, SensorUpdater updater)
    {
        this(c, new SensorUpdater[]{updater});
    }    

    @Override
    public void execute()
    {                
        TimeDialog timeDialog = new TimeDialog(null, updaterList);
        timeDialog.setLocationRelativeTo(parent);
        timeDialog.setVisible(true);        
    }

}
