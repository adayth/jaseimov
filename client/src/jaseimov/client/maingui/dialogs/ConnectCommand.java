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
package jaseimov.client.maingui.dialogs;

import jaseimov.client.ClientApp;
import jaseimov.client.utils.Command;

/**
 * Command to connect JASEIMOV client application to a server.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
class ConnectCommand extends Command
{
    private static final String name = "connect";

    private ConnectDialog dialog;

    public ConnectCommand(ConnectDialog connectDialog)
    {
        super(name);
        dialog = connectDialog;
    }

    public void execute()
    {
        ClientApp.setIP(dialog.getIP());
        ClientApp.setPort(dialog.getPort());
        ClientApp.connectServer();
    }    
}