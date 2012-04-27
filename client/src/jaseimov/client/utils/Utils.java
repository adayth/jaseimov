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
package jaseimov.client.utils;

import jaseimov.lib.devices.DeviceType;
import java.awt.Color;

/**
 *
 * @author Aday Talavera <aday.talavera at gmail.com>
 */
public class Utils
{
  public static Color getColorByDeviceType(DeviceType type)
  {
    switch(type)
    {
      case MOTOR_CONTROL:
        return Color.decode("0x86ff7b");
      case SERVO_CONTROL:
        return Color.decode("0x86ff7b");
      case ACCELEROMETER_SENSOR:
      case AXIS_SENSOR:
      case SPATIAL_SENSOR:
        return Color.decode("0x1b67ff");
      case ENCODER_SENSOR:
        return Color.decode("0xffb655");
      case INTERFACE_KIT:
        return Color.decode("0x86ff7b");
      case SONAR_SENSOR:
        return Color.decode("0x00cfff");
      case IR_SENSOR:
        return Color.decode("0xff0000");
      case CAMERA_SENSOR:
        return Color.decode("0x4ffb4f");
      default:
        return Color.BLACK;
    }  
  }
}
