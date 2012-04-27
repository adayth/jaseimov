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
package client.maingui.dialogs;

import client.ClientApp;
import client.Command;
import javax.swing.JFrame;

/**
 * Command that creates and show a {@link ConnectDialog}.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class ShowConnectDialog extends Command
{
    private static final String name = "show connect dialog";

    private JFrame parent;

    public ShowConnectDialog(JFrame parentFrame)
    {
        super(name);
        parent = parentFrame;
    }

    public void execute()
    {
        new ConnectDialog(parent, ClientApp.getIP(), ClientApp.getPort()).setVisible(true);
    }

}
