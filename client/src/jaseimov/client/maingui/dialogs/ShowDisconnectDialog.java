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

import jaseimov.client.utils.Command;
import javax.swing.JFrame;

/**
 * Command that creates and shows a {@link DisconnectDialog}.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class ShowDisconnectDialog extends Command
{
    private static final String name = "show disconnect dialog";

    private JFrame parent;    

    public ShowDisconnectDialog(JFrame parentFrame)
    {
        super(name);
        parent = parentFrame;
    }

    public void execute()
    {
        new DisconnectDialog(parent).setVisible(true);
    }
    
}