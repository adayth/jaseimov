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

package device;

import java.awt.Color;

/**
 * Defines devices types present in JASEIMOV.
 * Each device has an unique color associated to it.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public enum DeviceType
{
    /**
     * Type for {@link MotorControl}.
     */
    MOTOR_CONTROL (Color.decode("0x86ff7b")),

    /**
     * Type for {@link ServoControl}.
     */
    SERVO_CONTROL (Color.decode("0x86ff7b")),

    /**
     * Type for {@link Accelerometer}.
     */
    ACCELEROMETER_SENSOR (Color.decode("0x1b67ff")),

    /**
     * Type for {@link Accelerometer.Axis}.
     */
    ACCELEROMETER_AXIS (Color.decode("0x1b67ff")),

    /**
     * Type for {@link Encoder}.
     */
    ENCODER_SENSOR (Color.decode("0xffb655")),

    /**
     * Type for {@link InterfaceKit}.
     */
    INTERFACE_KIT (Color.decode("0x86ff7b")),

    /**
     * Type for {@link Sonar}.
     */
    SONAR_SENSOR (Color.decode("0x00cfff")),

    /**
     * Type for {@link IR}.
     */
    IR_SENSOR (Color.decode("0xff0000")),

    /**
     * Type for {@link Camera}.
     */
    CAMERA_SENSOR (Color.decode("0x4ffb4f"));

    private final Color color;

    /**
     * Creates a DeviceType with a color that defines it.
     * @param c
     */
    DeviceType(Color c)
    {
        color = c;
    }

    /**
     * Return device associated color.
     * @return Associated color.
     */
    public Color getColor()
    {
        return color;
    }    
}
