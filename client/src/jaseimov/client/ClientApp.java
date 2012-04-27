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
package jaseimov.client;

import jaseimov.lib.remote.list.RemoteDeviceList;
import jaseimov.lib.remote.list.RemoteDeviceInfo;
import jaseimov.client.maingui.MainFrame;
import jaseimov.lib.remote.connect.ConnectException;
import com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
 * Main class of JASEIMOV client. Manages a connection to a server with an IP and a port
 * and a {@link RemoteDeviceList}. Also creates and shows the GUI in class {@link MainFrame}.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public final class ClientApp
{
    private static String IP = "127.0.0.1";
    private static int port = java.rmi.registry.Registry.REGISTRY_PORT;

    private static Locale locale = Locale.ENGLISH;

    private static MainFrame mainFrame = null;
    private static RemoteDeviceList deviceList = null;

    private static boolean conected = false;

    private static List<JFrame> frameList = new ArrayList<JFrame>();;

    /**
     * Starts the GUI creating a new {@link MainFrame}.
     */
    public static void startApp()
    {
        Locale.setDefault(locale);
        conected = false;
        deviceList = null;
        mainFrame = new MainFrame();
        mainFrame.setVisible(true);        
    }

    /**
     * Connects client to a server.
     */
    public static void connectServer()
    {
        if(!conected)
        {
            deviceList = new RemoteDeviceList(IP,port);
            try
            {
                deviceList.connectToServer();
            }
            catch(ConnectException ex)
            {
                showErrorDialog(mainFrame, ex);
                return;
            }
            conected = true;
            mainFrame.updateUI(conected, deviceList.getRemoteDeviceInfoArray());
        }
    }

    /**
     * Disconnects client from a server.
     */
    public static void disconnectServer()
    {
        if(conected)
        {
            RemoteDeviceInfo[] infoArray =  deviceList.getRemoteDeviceInfoArray();
            for(RemoteDeviceInfo info : infoArray)
            {
                try
                {
                    if(info.getDeviceConnection().isConnected())
                    {
                        info.getDeviceConnection().disconnect();
                    }
                }
                catch(ConnectException ex)
                {
                    ex.printStackTrace();
                }
            }
            conected = false;
            closeAllFrames();            
            deviceList = null;
            mainFrame.updateUI(conected, null);
        }
    }

    /**
     * Closes the application.
     */
    public static void quit()
    {
        if(conected)
        {
            disconnectServer();
        }
        System.exit(0);
    }

    /**
     * Returns if the client is connected or not.
     * @return True if the client is connected, false if it isn't.
     */
    public static boolean isConected()
    {
        return conected;
    }

    /**
     * Returns the {@link RemoteDeviceList}.
     * @return The current RemoteDeviceList of the client.
     */
    public static RemoteDeviceList getDeviceList()
    {
        return deviceList;
    }

    /**
     * Returns the IP configured in the client.
     * @return Current IP.
     */
    public static String getIP()
    {
        return IP;
    }

    /**
     * Changes the IP configured in the client.
     * @param newIP New IP to configure.
     */
    public static void setIP(String newIP)
    {
        IP = newIP;
    }

    /**
     * Returns the port configured in the client.
     * @return New port to configure.
     */
    public static int getPort()
    {
        return port;
    }

    /**
     * Changes the port configured in the client.
     * @param newPort New port to configure.
     */
    public static void setPort(int newPort)
    {
        port = newPort;
    }

    /**
     * Changes the lenguage used by the client application.
     * @param newLocale New lenguage in form of Locale.
     */
    public static void setLanguage(Locale newLocale)
    {
        locale = newLocale;
    }

    /**
     * Resets the client application.
     */
    public static void reset()
    {
        ClientApp.disconnectServer();
        mainFrame.dispose();
        ClientApp.startApp();
    }

    /**
     * Returns a localized String with current Lenguage.
     * @param property Name of the localized String.
     * @return Localized String.
     */
    public static String getBundleString(String property)
    {
        return java.util.ResourceBundle.getBundle("jaseimov/client/client", locale).getString(property);
    }

    /**
     * Shows an error dialog.
     * @param c The Dialog will be located relative to this Component.
     * @param msg Error message to show.
     */
    public static void showErrorDialog(Component c, Object msg)
    {
        JOptionPane.showMessageDialog(c, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Register a JFrame of the client application.
     * @param frame JFrame to be registered.
     */
    public static void registerFrame(JFrame frame)
    {
        frameList.add(frame);
    }

    /**
     * Close all JFrames resgistered with registerFrame().
     */
    public static void closeAllFrames()
    {
        for(JFrame frame : frameList.toArray(new JFrame[0]))
        {            
            frame.dispose();
            frame = null;
        }
        frameList.clear();
    }

    /**
     * Starts JASEIMOV client application
     * @param args Not used yet.
     */
    public static void main(String[] args)
    {
        // Read IP from command line if present
        if(args.length > 0)
        {            
            ClientApp.IP = args[0];
        }

        // Set Look & Feel to Metal (Croos Platform)
        try
        {            
            UIManager.setLookAndFeel(NimbusLookAndFeel.class.getName());
        }
        catch (Exception ex)
        {
          try
          {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
          }
          catch(Exception ex2)
          {
            ex.printStackTrace();
          }
        }

        // Start application
        ClientApp.startApp();
    }

}