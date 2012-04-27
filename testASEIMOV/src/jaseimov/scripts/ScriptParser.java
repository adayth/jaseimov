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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ScriptParser
{
  public static final String LINE_SEPARATOR = ",";

  public static List<ScriptOrder> parseScriptFile(File script) throws IOException
  {
    // Script orders list to return
    List<ScriptOrder> orderList = new ArrayList<ScriptOrder>();

    // Read script lines
    BufferedReader in = new BufferedReader(new FileReader(script));
    if (in.ready())
    {
      String line;
      int lineCount = 0;
      while ((line = in.readLine()) != null)
      {
        lineCount++;
        String line_parts[] = line.split(LINE_SEPARATOR);
        if(line_parts.length >= 3)
        {
          long duration = Long.parseLong(line_parts[0]);
          double motorVelocity = Double.parseDouble(line_parts[1]);
          double servoPosition = Double.parseDouble(line_parts[2]);

          // Add order to the order list
          ScriptOrder order = new ScriptOrder(line, duration, motorVelocity, servoPosition);
          orderList.add(order);
        }
        else
        {
          throw new IOException("Error in number of params in line: " +  lineCount +  " contents: " + line);
        }
      }
    }    

    return orderList;
  }
}
