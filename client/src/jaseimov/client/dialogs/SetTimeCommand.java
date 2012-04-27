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

/**
 * Command that changes update time of an array of SensorUpdater.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
class SetTimeCommand extends Command
{
    private static final String name = "set-update-time";

    private int newTime;
    private SensorUpdater[] updaterList;

    public SetTimeCommand(SensorUpdater[] list, int time)
    {
        super(name);        
        updaterList = list;
        newTime = time;
    }

    @Override
    public void execute()
    {
        for(SensorUpdater updater : updaterList)
        {
            updater.setUpdateTime(newTime);
        }
    }
    
}
