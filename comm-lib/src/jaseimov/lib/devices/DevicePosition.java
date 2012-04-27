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
 * Defines valid positions and his coordinates in ASEIMOV car.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public enum DevicePosition
{

  /**
   * Position not defined and without defined coordinates.
   */
  NOT_DEFINED,
  // Front positions
  FRONT_LEFT_CORNER(23, 42),
  FRONT_LEFT(75, 25),
  FRONT_CENTER_TOP(137, 42),
  FRONT_CENTER_BOTTOM(137, 13),
  FRONT_RIGHT(199, 25),
  FRONT_RIGHT_CORNER(250, 42),
  // Left positions
  LEFT_FRONT(13, 212),
  LEFT_CENTER(13, 304),
  LEFT_BACK(13, 388),
  // Right positions
  RIGHT_FRONT(260, 212),
  RIGHT_CENTER(260, 304),
  RIGHT_BACK(260, 388),
  // Back positions
  BACK_LEFT_CORNER(30, 524),
  BACK_LEFT(79, 543),
  BACK_CENTER(137, 543),
  BACK_RIGHT(194, 543),
  BACK_RIGHT_CORNER(245, 524),
  // Camera positions
  FRONT_LEFT_CAMERA(90, 205),
  FRONT_CENTER_CAMERA(137, 205),
  FRONT_RIGHT_CAMERA(187, 205),
  BACK_LEFT_CAMERA(90, 400),
  BACK_CENTER_CAMERA(137, 400),
  BACK_RIGHT_CAMERA(187, 400),
  // Central position
  CENTER(137, 288),
  // Wheels encoders positions
  FRONT_LEFT_WHEEL(76, 128),
  FRONT_RIGHT_WHEEL(200, 128),
  BACK_LEFT_WHEEL(76, 455),
  BACK_RIGHT_WHEEL(200, 455);
  private final int x;
  private final int y;

  /**
   * Default constructor with x=0 and y=0.
   */
  DevicePosition()
  {
    x = 0;
    y = 0;
  }

  /**
   *
   * @param x X coordinate
   * @param y Y coordinate
   */
  DevicePosition(int x, int y)
  {
    this.x = x;
    this.y = y;
  }

  /**
   * Returns the X coordinate of this position.
   * @return X coordinate.
   */
  public int getX()
  {
    return x;
  }

  /**
   * Return the Y coordinate of this position.
   * @return Y coordinate.
   */
  public int getY()
  {
    return y;
  }
}
