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
package jaseimov.client.maingui.sensorvisors;

import jaseimov.lib.remote.list.RemoteDeviceInfo;
import jaseimov.lib.remote.connect.ConnectException;
import jaseimov.client.utils.Utils;
import jaseimov.lib.devices.DevicePosition;
import jaseimov.lib.devices.DeviceType;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.tree.MutableTreeNode;
import org.jgraph.JGraph;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;

/**
 * Shows a graphical representation of ASEIMOV car using JGraph 5 library.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class JGraphLayer
{
    private JLayeredPane layeredPanel;

    private JGraph mCanvasGraph;
    private GraphModel mCanvasModel;
    private GraphLayoutCache mCanvasView;

    private DefaultGraphCell rootCell;
    private DefaultGraphCell backgroundCell;
    // Location of root background cell
    private int x0 = 355;
    private int y0 = 10;

    // Width and Heigh of root background cell
    private int width;
    private int height;

    // Mouse listener for entire graph
    private class JGraphMouseListener extends MouseAdapter
    {
        @Override
        public void mouseClicked(final MouseEvent e)
        {            
            DeviceCell cell = cellAtPoint(e.getPoint());
            if (cell != null)
            {
                addVisor(e.getPoint(),cell);
            }
        }        
    }

    // InternalFrame move listener to move background cell
    class IFMoveListener extends ComponentAdapter
    {
        DefaultGraphCell cell;

        public IFMoveListener(DefaultGraphCell newCell)
        {
            cell = newCell;
        }

        @Override
        public void componentMoved(ComponentEvent e)
        {
            JInternalFrame c = (JInternalFrame)e.getComponent();
            Rectangle2D newRect = c.getBounds();

            Map nested = new Hashtable();
            Map am = cell.getAttributes();

            GraphConstants.setBounds(am, newRect);

            nested.put(cell, am);

            mCanvasGraph.getGraphLayoutCache().edit(nested);

            mCanvasGraph.invalidate();
            c.moveToFront();
        }
    }

    // Cell class for Devices
    private static class DeviceCell extends DefaultGraphCell
    {
        private RemoteDeviceInfo deviceInfo;
        private SensorVisor visor;

        public DeviceCell(RemoteDeviceInfo dev)
        {
            deviceInfo = dev;            
        }

        public RemoteDeviceInfo getDeviceInfo()
        {
            return deviceInfo;
        }

        public SensorVisor getSensorVisor()
        {            
            return visor;
        }

        public void setSentorVisor(SensorVisor sv)
        {
            visor = sv;
        }
    }

    // Internal Frame close listener to remove background cell
    class IFCloseListener extends InternalFrameAdapter
    {
        DefaultGraphCell cell;

        public IFCloseListener(DefaultGraphCell closeCell)
        {
            cell = closeCell;
        }

        @Override
        public void internalFrameClosing(InternalFrameEvent e)
        {
            removeBackgroundCell(cell);
        }
    }

    /**
     * Creates a JGraphLayer
     * @param layers
     */
    public JGraphLayer(JLayeredPane layers)
    {        
        layeredPanel = layers;

        mCanvasGraph = new JGraph();
        mCanvasGraph.addMouseListener(new JGraphMouseListener());        

        // Properties of the graph
        mCanvasGraph.setAntiAliased(true);
        mCanvasGraph.setEditable(false);
        mCanvasGraph.setDropEnabled(true);
        mCanvasGraph.setGridVisible(true);
        mCanvasGraph.setGridColor(new Color(235, 235, 255));
        mCanvasGraph.setGridSize(20);
        mCanvasGraph.setGridMode(JGraph.LINE_GRID_MODE);
        mCanvasGraph.setBounds(0, 0, 2000, 1200);        

        mCanvasModel = new DefaultGraphModel();
        mCanvasView = new GraphLayoutCache(mCanvasModel, new DefaultCellViewFactory());

        mCanvasGraph.setModel(mCanvasModel);
        mCanvasGraph.setGraphLayoutCache(mCanvasView);

        // Background cell with logo
        DefaultGraphCell background = new DefaultGraphCell();
        GraphConstants.setBounds(background.getAttributes(), new Rectangle2D.Double(10,10,250,450));
        GraphConstants.setOpaque(background.getAttributes(), false);
        GraphConstants.setIcon(background.getAttributes(), new ImageIcon(getClass().getResource("/jaseimov/client/images/logo.png")));
        GraphConstants.setAutoSize(background.getAttributes(), true);
        GraphConstants.setSelectable(background.getAttributes(), false);
        mCanvasGraph.getGraphLayoutCache().insert(background);

        // Root cell creation
        rootCell = new DefaultGraphCell();
        GraphConstants.setChildrenSelectable(rootCell.getAttributes(), false);
        GraphConstants.setSizeable(rootCell.getAttributes(), false);

        // Background of root cell creation        
        backgroundCell = new DefaultGraphCell();
        
        GraphConstants.setBounds(backgroundCell.getAttributes(), new Rectangle2D.Double(x0,y0,250,450));        
        GraphConstants.setOpaque(backgroundCell.getAttributes(), false);
        GraphConstants.setBorder(backgroundCell.getAttributes(), BorderFactory.createLineBorder(Color.lightGray, 1));
        GraphConstants.setSizeable(backgroundCell.getAttributes(), false);
        ImageIcon icon = new ImageIcon(getClass().getResource("/jaseimov/client/images/car_background.png"));
        width = icon.getIconWidth();
        height = icon.getIconHeight();
        GraphConstants.setIcon(backgroundCell.getAttributes(), icon);
        GraphConstants.setAutoSize(backgroundCell.getAttributes(), true);
       
        // Add background to root cell
        rootCell.add((MutableTreeNode)backgroundCell);

        // Insert root cell in the graph with their children (background)
        mCanvasGraph.getGraphLayoutCache().insertGroup(rootCell, rootCell.getChildren().toArray());        
    }

    /**
     * Looks for a DeviCell in a point of the graph.
     * @param p
     * @return
     */
    private DeviceCell cellAtPoint(final Point2D p)
    {
        Vector<DefaultGraphCell> children = (Vector<DefaultGraphCell>) rootCell.getChildren();
        Rectangle2D childLocation;

        for (DefaultGraphCell cell : children)
        {
            if (cell instanceof DeviceCell)
            {
                childLocation = GraphConstants.getBounds(cell.getAttributes());

                // Has to be a valid size cell and has to contain the point,
                if (childLocation != null && childLocation.contains(p))
                        return (DeviceCell)cell;
            }
        }
        return null;
    }

    /**
     * Return the view of the graph.
     * @return View of the graph.
     */
    public JGraph getJGraph()
    {
        return mCanvasGraph;
    }

    /**
     * Adds a new device to the graph.
     * @param dev Device to be added.
     */
    public void addDeviceCell(RemoteDeviceInfo dev)
    {
        // Create Cell
        DeviceCell cell = new DeviceCell(dev);
        // Get type and position of device
        DeviceType type;
        DevicePosition position;
        try
        {
            type = dev.getDevice().getDeviceType();
            position = dev.getDevice().getDevicePosition();
        }
        catch(ConnectException ex)
        {
            ex.printStackTrace();
            return;
        }
        catch(RemoteException ex)
        {
            ex.printStackTrace();
            return;
        }

        // Cell image in graph
        ImageIcon icon = this.getDeviceImage(type, position);
        GraphConstants.setIcon(cell.getAttributes(), icon);

        // Update parent cell position
        Rectangle2D rootBounds = GraphConstants.getBounds(backgroundCell.getAttributes());
        x0 = (int)rootBounds.getMinX();
        y0 = (int)rootBounds.getMinY();

        // Cell location in graph
        if(position != DevicePosition.NOT_DEFINED)
        {
            int x = x0 + position.getX();
            int y = y0 + position.getY();
            int w = icon.getIconWidth();
            int h = icon.getIconHeight();
            Rectangle2D.Double bounds = new Rectangle2D.Double(x-w/2,y-h/2,w,h);
            GraphConstants.setBounds(cell.getAttributes(), bounds);
        }
        else
        {
            Rectangle2D.Double bounds = new Rectangle2D.Double(x0-40,y0+10,30,20);
            y0 += 30;
            GraphConstants.setBounds(cell.getAttributes(), bounds);
        }                

        // Cell color and border
        Color color = Utils.getColorByDeviceType(type);
        GraphConstants.setBackground(cell.getAttributes(), color);
        GraphConstants.setBorder(cell.getAttributes(), BorderFactory.createLineBorder(color, 2));

        // Cell properties        
        GraphConstants.setSizeable(cell.getAttributes(), false);
        GraphConstants.setAutoSize(cell.getAttributes(), true);

        // Cell port for conect with other cells
        cell.add(new DefaultPort());
        // Add cell to root cell
        rootCell.add((MutableTreeNode)cell);
        mCanvasGraph.getGraphLayoutCache().insertGroup(rootCell, rootCell.getChildren().toArray());        
    }

    private void addVisor(Point p, DeviceCell cell)
    {        
        // Creates sensor visor with device info
        SensorVisor visor = cell.getSensorVisor();
        if(visor == null || visor.isClosed())
        {
            visor = VisorFactory.getSensorVisor(cell.getDeviceInfo());
            cell.setSentorVisor(visor);
            // Visor has a cell as background
            DefaultGraphCell backCell = addBackgroundCell(cell);

            // When internal frame of visor moves, background cell should move too
            visor.addComponentListener(new IFMoveListener(backCell));

            // When internal frame closes, background cell should be removed from graph
            visor.addInternalFrameListener(new IFCloseListener(backCell));

            // Add frame to palette layer and show it
            layeredPanel.add(visor, JLayeredPane.PALETTE_LAYER);
        }
        // Move visor to a good position
        Rectangle2D cellBounds = GraphConstants.getBounds(cell.getAttributes());
        int midX = x0+(width/2);
        int cellCenterX = (int)cellBounds.getCenterX();

        if(Math.abs(midX - cellCenterX) < 40)
        {
            visor.setLocation(cellCenterX, (int)cellBounds.getCenterY());
        }
        else if(midX > cellCenterX)
        {
            visor.setLocation((int)cellBounds.getMinX()-350, (int)cellBounds.getMinY()-12);
        }
        else
        {
            visor.setLocation((int)cellBounds.getMaxX()+20, (int)cellBounds.getMinY()-12);
        }
        // Show visor
        visor.setVisible(true);
        layeredPanel.moveToFront(visor);
    }

    private DefaultGraphCell addBackgroundCell(DefaultGraphCell sourceCell)
    {
        DefaultGraphCell newCell = new DefaultGraphCell();

        GraphConstants.setSizeable(newCell.getAttributes(), false);
        GraphConstants.setMoveable(newCell.getAttributes(), false);
        GraphConstants.setEditable(newCell.getAttributes(), false);        
        GraphConstants.setOpaque(newCell.getAttributes(), true);        

        DefaultPort port0 = new DefaultPort();
        newCell.add(port0);

        DefaultEdge connectionToQwerk = new DefaultEdge();
        connectionToQwerk.setSource(port0);
        connectionToQwerk.setTarget(sourceCell.getChildAt(0));

        GraphConstants.setDisconnectable(connectionToQwerk.getAttributes(), false);
        GraphConstants.setEditable(connectionToQwerk.getAttributes(), false);
        GraphConstants.setSelectable(connectionToQwerk.getAttributes(), false);
        GraphConstants.setLineWidth(connectionToQwerk.getAttributes(), 2);
        Color cellColor = GraphConstants.getBackground(sourceCell.getAttributes());
        GraphConstants.setLineColor(connectionToQwerk.getAttributes(), cellColor);

        mCanvasGraph.getGraphLayoutCache().insert(newCell);
        mCanvasGraph.getGraphLayoutCache().insert(connectionToQwerk);
        mCanvasGraph.setSelectionCell(newCell);

        return newCell;
    }

    private void removeBackgroundCell(DefaultGraphCell cell)
    {
        Object[] cells = {cell};
        mCanvasGraph.getGraphLayoutCache().remove(cells, true, true);
        
    }

    // Returns the image of the device and rotate it if neccesary
    private ImageIcon getDeviceImage(DeviceType type, DevicePosition position)
    {
        ImageIcon icon;

        // Load image
        switch(type)
        {
            case ACCELEROMETER_SENSOR:
            case SPATIAL_SENSOR:
                icon = new ImageIcon(getClass().getResource("/jaseimov/client/images/accelerometer.png"));
                break;                            
            case SONAR_SENSOR:
                icon = new ImageIcon(getClass().getResource("/jaseimov/client/images/sonar.png"));
                break;
            case IR_SENSOR:
                icon = new ImageIcon(getClass().getResource("/jaseimov/client/images/ir.png"));
                break;
            case MOUSE_ENCODER_SENSOR:
            case PHIDGET_ENCODER_SENSOR:
                icon = new ImageIcon(getClass().getResource("/jaseimov/client/images/encoder.png"));
                break;
            case CAMERA_SENSOR:
                icon = new ImageIcon(getClass().getResource("/jaseimov/client/images/webcam.png"));
                break;
            default:
                icon = new ImageIcon(getClass().getResource("/jaseimov/client/images/pencil.png"));
                break;
        }        

        int angle;

        // Rotate image
        switch(position)
        {
            case LEFT_FRONT:
            case LEFT_CENTER:
            case LEFT_BACK:
                angle = 270;
                break;
            case RIGHT_FRONT:
            case RIGHT_CENTER:
            case RIGHT_BACK:
                angle = 90;
                break;
            case BACK_LEFT:
            case BACK_CENTER:
            case BACK_RIGHT:
                angle = 180;
                break;            
            case FRONT_LEFT_CORNER:
                angle = 0;
                icon = new ImageIcon(getClass().getResource("/jaseimov/client/images/sonar_corner.png"));
                break;
            case FRONT_RIGHT_CORNER:                
            case BACK_LEFT_CORNER:                
            case BACK_RIGHT_CORNER:
                angle = 45;
                icon = new ImageIcon(getClass().getResource("/jaseimov/client/images/sonar_corner.png"));
                break;
            case FRONT_LEFT:
            case FRONT_CENTER_TOP:
            case FRONT_CENTER_BOTTOM:
            case FRONT_RIGHT:            
            default:
                angle = 0;
                break;
        }

        if(angle != 0)
        {
            int w = icon.getIconWidth();
            int h = icon.getIconHeight();
            BufferedImage image;
            double x;
            double y;
            // Left/right area
            if(angle == 270 || angle == 90)
            {
                image = new BufferedImage(h, w, BufferedImage.TYPE_INT_ARGB);
                x = (h-w)/2.0;
                y = (w-h)/2.0;
            }
            // Back area
            else if(angle == 180)
            {
                image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                x = (w-h)/80.0;
                y = (h-w)/80.0;
            }
            // Corner area
            else
            {
                image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                x = 0;
                y = 0;
                switch(position)
                {                    
                    case FRONT_RIGHT_CORNER:
                        angle = 90;
                        break;
                    case BACK_RIGHT_CORNER:
                        angle = 180;
                        break;
                    case BACK_LEFT_CORNER:
                        angle = 270;
                        break;                    
                }
            }

            AffineTransform at = AffineTransform.getTranslateInstance(x, y);
            at.rotate(Math.toRadians(angle), w/2.0, h/2.0);
            Graphics2D g2d = image.createGraphics();            
            g2d.drawImage(icon.getImage(),at,null);
            icon = new ImageIcon(image);
            g2d.dispose();
        }

        return icon;
    }
}
