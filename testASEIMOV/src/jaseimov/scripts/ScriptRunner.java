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

import jaseimov.scripts.xml.bindings.Script.Orders.Order;
import java.util.ArrayList;
import java.util.List;

public class ScriptRunner
{
  // List of script orders
  private List<Order> orderList = new ArrayList<Order>();
  // Current order index
  private int orderIndex;

  public void setOrders(List<Order> orders)
  {
    orderList = orders;
    resetScript();
  }

  public boolean isScriptEnded()
  {
    return orderList.size() == orderIndex;
  }

  public Order getNextOrder()
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
