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

package jaseimov.scripts;

public class ScriptOrder
{
  // Order line text
  private String orderText;
  // Duration of the order in ms
  private long duration;
  // Velocity of the motor in %
  private double motorVelocity;
  // Position of the servo
  private double servoPosition;

  public ScriptOrder(String orderText, long duration, double motorVelocity, double servoPosition)
  {
    this.orderText = orderText;
    this.duration = duration;
    this.motorVelocity = motorVelocity;
    this.servoPosition = servoPosition;
  }

  public long getDuration()
  {
    return duration;
  }

  public void setDuration(long duration)
  {
    this.duration = duration;
  }

  public double getMotorVelocity()
  {
    return motorVelocity;
  }

  public void setMotorVelocity(double motorVelocity)
  {
    this.motorVelocity = motorVelocity;
  }

  public double getServoPosition()
  {
    return servoPosition;
  }

  public void setServoPosition(double servoPosition)
  {
    this.servoPosition = servoPosition;
  }

  @Override
  public String toString()
  {
    return orderText;
  }
}
