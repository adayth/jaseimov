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

/**
 *
 * @author Aday Talavera <aday.talavera at gmail.com>
 */
public class CaptureScriptTest
{
  public CaptureScriptTest()
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
  public void testMain()
  {    
    String[] args = {"127.0.0.1", "script.txt", "capture.txt"};
    CaptureScript.main(args);    
  }
}
