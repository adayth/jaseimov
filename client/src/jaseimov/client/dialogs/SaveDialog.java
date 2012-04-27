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

import jaseimov.client.ClientApp;
import java.awt.Component;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * A factory for save file dialogs.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class SaveDialog
{
    /**
     * Filter for JPG images.
     */
    public static final
            FileNameExtensionFilter JPEG_IMAGE_FILTER = new FileNameExtensionFilter(ClientApp.getBundleString("JPEG FILENAME"), "jpg", "jpeg");
    /**
     * Filter for PNG images.
     */
    public static final
            FileNameExtensionFilter PNG_IMAGE_FILTER = new FileNameExtensionFilter(ClientApp.getBundleString("PNG FILENAME"), "png");
    /**
     * Filter for CSV files.
     */
    public static final
            FileNameExtensionFilter CSV_FILTER = new FileNameExtensionFilter(ClientApp.getBundleString("CSV FILENAME"), "csv");

    /**
     * Shows a save file dialog.
     * @param parent Position of the dialog will be relative to this component.
     * @param filter A filter of files to show.
     * @return File choosed by the user.
     */
    public static File showFileDialog(Component parent, FileNameExtensionFilter filter)
    {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(filter);
        int returnVal = fc.showSaveDialog(parent);
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {            
            return fc.getSelectedFile();
        }
        else
        {
            return null;
        }
    }

    /**
     * Shows a save directory dialog.
     * @param parent Position of the dialog will be relative to this component.
     * @return Directory choosed by the user.
     */
    public static File showDirectoryDialog(Component parent)
    {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fc.showSaveDialog(parent);
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            return fc.getSelectedFile();
        }
        else
        {
            return null;
        }
    }
}
