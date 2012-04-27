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

package mouselib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Class for capture PS/2 mouse packets in Linux /dev/input/mouse# devices.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class MouseCapturer
{
    private File device;
    private boolean opened = false;
    private FileInputStream file;

    /**
     *
     * @param deviceFile The mouse device file.
     *
     */
    public MouseCapturer(String deviceFile)
    {
        device = new File(deviceFile);
        if(!device.exists())
        {
            throw new IllegalArgumentException("File not found");
        }
    }

    /**
     * Open the mouse device file.
     * @throws FileNotFoundException If the file is not found.
     */
    public void open() throws FileNotFoundException
    {
        if(!opened)
        {
            file = new FileInputStream(device);
            opened = true;
        }
    }

    /**
     * Try to read a movement packet of three bytes from the mouse device file. This method blocks indefinitely until it read three bytes.
     * @return An array of three byte.
     * @throws IOException If there is an IO error.
     */
    public byte[] readMovementPacket() throws IOException
    {
        byte[] barray = new byte[3];
        file.read(barray);
        return barray;
    }

    /**
     * Close the mouse device file.
     * @throws IOException If there is an IO error.
     */
    public void close() throws IOException
    {
        if(opened)
        {
            file.close();
            opened = false;
        }
    }

    /**
     * Is possible to test this class with this main method. It opens the mouse device file and try to read and print in console 1000 mouse movement packets.
     * @param args Only take one argument, the mouse device file. If not defined is set to /dev/input/mouse0
     */
    public static void main(String[] args)
    {
        String device = "/dev/input/mouse0";
        if(args.length > 0)
        {
            device = args[0];
        }
        else
        {
            System.out.println("Device not especified, trying " + device);
        }

        MouseCapturer mouse = new MouseCapturer(device);

        try
        {
            mouse.open();
            System.out.println("Reading mouse packets on " + device + " press CTRL + C to exit");
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
            return;
        }

        for(int i=0; i<1000; i++)
        {
            try
            {
                byte[] packet = mouse.readMovementPacket();
                if(packet != null)
                {                    
                    int b7, b6, b5, b4, b3, b2, b1, b0;
                    b0 = (packet[0]     ) & 1;
                    b1 = (packet[0] >> 1) & 1;
                    b2 = (packet[0] >> 2) & 1;
                    b3 = (packet[0] >> 3) & 1;
                    b4 = (packet[0] >> 4) & 1;
                    b5 = (packet[0] >> 5) & 1;
                    b6 = (packet[0] >> 6) & 1;
                    b7 = (packet[0] >> 7) & 1;
                    
                    System.out.println("b7: " + b7 + " b6:" + b6 + " b5:" + b5 +  " b4:" + b4 + " b3:" + b3 + " b2:" + b2 + " b1:" + b1 + " b0:" + b0);
                    
                    int x, y;                    
                    x = packet[1];
                    y = packet[2];

                    System.out.println("X: " + x);
                    System.out.println("Y: " + y);
                }
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
                return;
            }
        }
        
        try
        {
            mouse.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            return;
        }
        System.exit(0);
    }
}
