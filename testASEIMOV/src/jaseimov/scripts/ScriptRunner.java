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

import jaseimov.lib.devices.MotorControl;
import jaseimov.lib.devices.ServoControl;
import java.util.ArrayList;
import java.util.List;

public class ScriptRunner
{

  // Devices
  private MotorControl motor;
  private ServoControl servo;

  // List of script orders
  private List<ScriptOrder> orderList = new ArrayList<ScriptOrder>();
  // Current order index
  private int orderIndex;

  public void setMotor(MotorControl motor)
  {
    this.motor = motor;
  }

  public void setServo(ServoControl servo)
  {
    this.servo = servo;
  }

  public void setOrders(List<ScriptOrder> orders)
  {
    orderList = orders;
    resetScript();
  }  

  public boolean isScriptEnded()
  {
    return orderList.size() == orderIndex;
  }  

  public ScriptOrder getNextOrder()
  {    
    return orderList.get(orderIndex++);
  }

  public void resetScript()
  {
    orderIndex = 0;
  }

  public int getNumOrders()
  {
    return orderList.size();
  }  
}
