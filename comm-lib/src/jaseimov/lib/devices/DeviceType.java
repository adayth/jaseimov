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
package jaseimov.lib.devices;

/**
 * Defines devices types present in JASEIMOV. 
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public enum DeviceType
{  
  MOTOR_CONTROL,  
  SERVO_CONTROL,
  ADV_SERVO_CONTROL,
  ACCELEROMETER_SENSOR,  
  AXIS_SENSOR,
  MOUSE_ENCODER_SENSOR,
  PHIDGET_ENCODER_SENSOR,
  INTERFACE_KIT,  
  SONAR_SENSOR,  
  IR_SENSOR,  
  CAMERA_SENSOR,
  SPATIAL_SENSOR;    
}
