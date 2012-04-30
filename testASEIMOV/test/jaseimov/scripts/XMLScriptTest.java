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

import org.junit.After;
import org.junit.Before;
import static org.junit.Assert.*;
import jaseimov.scripts.xml.ScriptXMLUtil;
import jaseimov.scripts.xml.bindings.Script;

/**
 *
 * @author Aday Talavera <aday.talavera at gmail.com>
 */
public class XMLScriptTest
{
  public XMLScriptTest()
  {
  }

  @org.junit.BeforeClass
  public static void setUpClass() throws Exception
  {
  }

  @org.junit.AfterClass
  public static void tearDownClass() throws Exception
  {
  }

  @org.junit.Before
  public void setUp() throws Exception
  {
  }

  @org.junit.After
  public void tearDown() throws Exception
  {
  }

  @org.junit.Test
  public void testWellFormedXMLFiles()
  {
    String testfiles[] = {"testfiles/test_file1.xml", "testfiles/test_file2.xml"};
    for(String filename : testfiles) {
      try {
        parse(filename);
        validate(filename);
      }
      catch(Exception ex) {
        // Should never happen
        fail();
      }
    }
  }

  @org.junit.Test
  public void testBadFormedXMLFiles()
  {
    String testfiles[] = {"testfiles/test_file3.xml", "testfiles/test_file4.xml"};
    for(String filename : testfiles) {
      try {
        parse(filename);
        validate(filename);
        // Should never happen
        fail();
      }
      catch(Exception ignore) {
      }
    }
  }

  /*
   * Util functions for tests
   */

  public static void parse(String file) throws Exception
  {
    System.out.println("Parsing file " + file);

    Script script = ScriptXMLUtil.parseXMLScript(file);

    System.out.println("<captures>");
    if (script.getCaptures() == null)
    {
      System.out.println("NULL");
    }
    else
    {
      System.out.println("\t<capture>");
      for (Script.Captures.Capture capture : script.getCaptures().getCapture())
      {
        System.out.println("\t\t<delay>: " + capture.getDelay());
        if (capture.getDevices() == null)
        {
          System.out.println("\t\tNULL");
        }
        else
        {
          for (Script.Captures.Capture.Devices.Device device : capture.getDevices().getDevice())
          {
            System.out.println("\t\t\t<device>: " + device.getId());
          }
        }
      }
    }

    System.out.println("<orders>");
    for (Script.Orders.Order order : script.getOrders().getOrder())
    {
      System.out.println("\t<order>: " + order.getDuration() + ", " + order.getVelocity() + ", " + order.getDirection());
    }
  }

  public static void validate(String file) throws Exception
  {
    System.out.println("Validating file " + file);

    ScriptXMLUtil.validateXMLScript(file);
  }
}
