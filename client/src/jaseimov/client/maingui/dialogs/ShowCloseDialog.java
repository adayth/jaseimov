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
import javax.swing.JFrame;

/**
 * Command that creates and shows a {@link CloseDialog}.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class ShowCloseDialog extends Command
{
    private static final String name = "show-close-dialog";

    private JFrame parent;

    public ShowCloseDialog(JFrame parentFrame)
    {
        super(name);
        parent = parentFrame;
    }

    @Override
    public void execute()
    {
        if(ClientApp.isConected())
        {
            new CloseDialog(parent).setVisible(true);
        }
        else
        {
            new CloseCommand().execute();
        }
    }

}
