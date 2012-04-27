package jaseimov.client.joystick;

import jaseimov.client.utils.FileFunctions;
import java.io.File;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JLabel;
import javax.swing.JSlider;

public class JoystickControl
{
  private static final int DELAY = 40;   // ms (polling interval) need to be fast
  private static int valinc = 0;
  private GamePadController gpController; // will be initialized in the constructor
  private Timer pollTimer = new Timer();   // timer which triggers the polling
  private MyTask tarea = new MyTask();
  private boolean scheduled = false;
  private boolean frozen = false;
  private boolean videogame_mode = false;
  public File jstkFile = null;
  // Filefunctions
  FileFunctions fl = new FileFunctions();
  //Sliders (this two sliders are going to be the ones in controlcarframe by initializing them)
  JSlider speedSlider = null;
  JSlider posSlider = null;
  JSlider accSlider = null;
  boolean SlidersAsigned = false;
  //information label control
  JLabel infolabel = null;
  private int maxs = 100; //max speed
  private int mins = -100; //min speed
  private int maxp = 140; //max position
  private int centerp = 0; //central position
  private int minp = 0; //min position
  private int maxa = 0; //max acceleration
  private int mina = 0; //min acceleration
  //Joystck compass directions
  int compassDir = 0;
  int stickdir = 0;
  int hatdir = 0;
  public static final int NUM_COMPASS_DIRS = 9;
  static final int NW = 0;
  static final int NORTH = 1;
  static final int NE = 2;
  static final int WEST = 3;
  static final int NONE = 4;   // default value
  static final int EAST = 5;
  static final int SW = 6;
  static final int SOUTH = 7;
  static final int SE = 8;
  //joystick Standard Buttons (initially a playstation(R) kind)
  // This values will represent the index of the "button pressed" vector
  int UP;
  int DOWN;
  int LEFT;
  int RIGHT;
  int L1;
  int L2;
  int L3;
  int R1;
  int R2;
  int R3;
  int A; //triangle
  int B; //circle
  int C; // X
  int D; // square
  int START;
  int SELECT;
  int ButtonCount = 0;

  public JoystickControl(JSlider Speed, JSlider Position, JSlider Acceleration)
  { //constructor
    System.out.println("[JoystickControl]:New JoystickControl Module loaded");

    gpController = new GamePadController(); //initialize the controller

    if (gpController.AnyGamePadFound == true)
    {
      ButtonCount = gpController.get_number_of_buttons(); //get the number of buttons
    }

    System.out.println("[JoystickControl]:Number of buttons found:" + ButtonCount);

    speedSlider = Speed;
    maxs = Speed.getMaximum();
    mins = Speed.getMinimum();
    posSlider = Position;
    maxp = Position.getMaximum();
    minp = Position.getMinimum();
    centerp = (maxp + minp) / 2;
    accSlider = Acceleration;
    maxa = Acceleration.getMaximum();
    mina = Acceleration.getMinimum();
    SlidersAsigned = true;

    if (ButtonCount == 12)
    {
      ApplyPS2SetUp(); //PS2 type setup
    }
  }
  // constructor

  /* Set up a timer which is activated every DELAY ms
  and polls the game pad and updates the GUI.
  Safe since the action handler is executed in the
  event-dispatching thread. */
  class MyTask extends TimerTask
  {
    public void run()
    {

      if (frozen == false && SlidersAsigned == true)
      {
        gpController.poll();

        // get button values
        boolean[] buttons = gpController.getButtons();
        AsignButtons(buttons);
        showInfo(buttons);

        // get POV hat compass direction
        //compassDir = gpController.getHatDir();
        //System.out.println("[Joystick]hat:"+compassDir);
        //setSliders(compassDir);

        // get compass direction for the two analog sticks
        //compassDir = gpController.getXYStickDir(); //left stick

        //get the stick values
        //if(stickdir != NONE){
        stickdir = gpController.getZRZStickDir(); //right stick
        //System.out.println("stick dir: "+stickdir);
        //R3 will act as shift
        if (buttons[R3] == false)
        {
          AsignMovement(stickdir);
        }
        else
        {//if R3 is pushed
          AsignMovementMax(stickdir);
        }
        //}else{
        //System.out.println("no stick");
        //}

        //get the hat values
        if (hatdir != NONE)
        {
          hatdir = gpController.getHatDir(); //right stick
          AsignMovement(hatdir);
          System.out.println("[JoystickControl]:Hatdir=" + hatdir);
        }

      }

    }// end run()
  }// end MyTask

  public void start()
  {
    frozen = false;

    if (scheduled == false && gpController.AnyGamePadFound == true)
    {
      pollTimer.schedule(tarea, 0, DELAY);
      System.out.println("[JoystickControl]:Joystick Clock Started");
      scheduled = true;
    }

  }

  public void Freeze(boolean val)
  {
    frozen = val;
  }

  public void showInfo(boolean[] btn)
  {
    for (int i = 0; i < btn.length; i++)
    {
      if (btn[i])
      {
        infolabel.setText("[" + i + "]: pressed");
      }
    }
  }

  private void ApplyPS2SetUp()
  {
    String line[];
    String id = null;
    String init;
    int val = 0;
    InputStream in = getClass().getResourceAsStream("PS2.joy");
    String[] content = fl.convertStreamToString(in);
    for (int i = 0; i < content.length; i++)
    {
      line = content[i].split("=");

      if (line.length > 1 & line[0].contains("#") == false)
      {
        //System.out.println(content[i].toString());
        id = line[0];
        val = Integer.parseInt(line[1]);
        if (id.contentEquals("valinc"))
        {
          valinc = val;
        }
        if (id.contentEquals("L1"))
        {
          L1 = val;
        }
        if (id.contentEquals("L2"))
        {
          L2 = val;
        }
        if (id.contentEquals("L3"))
        {
          L3 = val;
        }
        if (id.contentEquals("R1"))
        {
          R1 = val;
        }
        if (id.contentEquals("R2"))
        {
          R2 = val;
        }
        if (id.contentEquals("R3"))
        {
          R3 = val;
        }
        if (id.contentEquals("A"))
        {
          A = val;
        }
        if (id.contentEquals("B"))
        {
          B = val;
        }
        if (id.contentEquals("C"))
        {
          C = val;
        }
        if (id.contentEquals("D"))
        {
          D = val;
        }
        if (id.contentEquals("START"))
        {
          START = val;
        }
        if (id.contentEquals("SELECT"))
        {
          SELECT = val;
        }
        if (id.contentEquals("UP"))
        {
          UP = val;
        }
        if (id.contentEquals("DOWN"))
        {
          DOWN = val;
        }
        if (id.contentEquals("LEFT"))
        {
          LEFT = val;
        }
        if (id.contentEquals("RIGHT"))
        {
          RIGHT = val;
        }

      }
    }

  }

  public void OpenConfigFile(String filename)
  {
    Object[] content = fl.OpenFile(filename).toArray();
    jstkFile = new File(filename); //usefull to save the settings see carcontrolcenter.java
    String line[];
    String id = null;
    int val = 0;

    if (jstkFile.exists() == true)
    {
      //examinate the content
      for (int i = 0; i < content.length; i++)
      {
        line = content[i].toString().split("=");
        //System.out.println(content[i].toString());
        if (line.length > 1)
        {
          id = line[0];
          val = Integer.parseInt(line[1]);
          if (id.contentEquals("valinc"))
          {
            valinc = val;
          }
          if (id.contentEquals("L1"))
          {
            L1 = val;
          }
          if (id.contentEquals("L2"))
          {
            L2 = val;
          }
          if (id.contentEquals("L3"))
          {
            L3 = val;
          }
          if (id.contentEquals("R1"))
          {
            R1 = val;
          }
          if (id.contentEquals("R2"))
          {
            R2 = val;
          }
          if (id.contentEquals("R3"))
          {
            R3 = val;
          }
          if (id.contentEquals("A"))
          {
            A = val;
          }
          if (id.contentEquals("B"))
          {
            B = val;
          }
          if (id.contentEquals("C"))
          {
            C = val;
          }
          if (id.contentEquals("D"))
          {
            D = val;
          }
          if (id.contentEquals("START"))
          {
            START = val;
          }
          if (id.contentEquals("SELECT"))
          {
            SELECT = val;
          }
          if (id.contentEquals("UP"))
          {
            UP = val;
          }
          if (id.contentEquals("DOWN"))
          {
            DOWN = val;
          }
          if (id.contentEquals("LEFT"))
          {
            LEFT = val;
          }
          if (id.contentEquals("RIGHT"))
          {
            RIGHT = val;
          }
        }
      }
      System.out.println("[JoystickControl]:config_file=" + filename);
    }
  }

  public void SetSliders(JSlider Speed, JSlider Position, JSlider Acceleration)
  {
    speedSlider = Speed;
    maxs = Speed.getMaximum();
    mins = Speed.getMinimum();
    posSlider = Position;
    maxp = Position.getMaximum();
    minp = Position.getMinimum();
    centerp = (maxp + minp) / 2;
    accSlider = Acceleration;
    maxa = Acceleration.getMaximum();
    mina = Acceleration.getMinimum();
    SlidersAsigned = true;
  }

  public void SetInfoLabel(JLabel val)
  {
    infolabel = val;
  }

  public void SetVideoGameMode(boolean val)
  {
    videogame_mode = val;
  }

  private void AsignButtons(boolean[] btn)
  {
    int speed = speedSlider.getValue();
    int pos = posSlider.getValue();

    if (btn[D] == true || btn[R3] == true)
    {//square buton or  R3
      speedSlider.setValue(0);
      posSlider.setValue(centerp);
    }

    if (btn[A] == true)
    { //triangle on (max speed)
      speedSlider.setValue(maxs);
    }

    //L2 decrease speed, speed--
    if (btn[L2] == true || btn[DOWN] == true)
    {
      if (speed > mins)
      {
        speedSlider.setValue(speed - valinc);
      }
    }

    //R2 increase speed, speed++
    if (btn[R2] == true || btn[UP] == true)
    {
      if (speed < maxs)
      {
        speedSlider.setValue(speed + valinc);
      }
    }

    if (btn[L2] == false & btn[DOWN] == false & btn[R2] == false & btn[UP] == false)
    {
      if (videogame_mode == true)
      {
        speedSlider.setValue(0);
      }
    }

    if (btn[L1] == true || btn[LEFT] == true)
    {//L1 decrease position (to the left), pos--
      if (pos > minp)
      {
        posSlider.setValue(pos - valinc);
      }
    }

    if (btn[R1] == true || btn[RIGHT] == true)
    {//R1 increase position (to the right), pos++
      if (pos < maxp)
      {
        posSlider.setValue(pos + valinc);
      }
    }


  }

  private void AsignMovement(int val)
  {
    int speed = speedSlider.getValue();
    int pos = posSlider.getValue();

    if (val == NW)
    { //speed++, pos--
      if (speed < maxs)
      {
        speedSlider.setValue(speed + valinc);
      }
      if (pos > minp)
      {
        posSlider.setValue(pos - valinc);
      }
    }

    if (val == NORTH)
    { //speed++
      if (speed < maxs)
      {
        speedSlider.setValue(speed + valinc);
      }
    }

    if (val == NE)
    { //speed++, pos++
      if (speed < maxs)
      {
        speedSlider.setValue(speed + valinc);
      }
      if (pos < maxp)
      {
        posSlider.setValue(pos + valinc);
      }
    }

    if (val == WEST)
    { //pos--
      if (pos > minp)
      {
        posSlider.setValue(pos - valinc);
      }
    }

    /* by now center is not functional
    if (val==NONE){
    speedSlider.setValue(0);
    posSlider.setValue(maxp/2);
    //System.out.println("center");
    }
     */

    if (val == EAST)
    { //pos++
      if (pos < maxp)
      {
        posSlider.setValue(pos + valinc);
      }
    }

    if (val == SW)
    { //speed--, pos--
      if (speed > mins)
      {
        speedSlider.setValue(speed - valinc);
      }
      if (pos > minp)
      {
        posSlider.setValue(pos - valinc);
      }
    }

    if (val == SOUTH)
    { //speed--
      if (speed > mins)
      {
        speedSlider.setValue(speed - valinc);
      }
    }

    if (val == SE)
    { //speed--, pos++
      if (speed > mins)
      {
        speedSlider.setValue(speed - valinc);
      }
      if (pos < maxp)
      {
        posSlider.setValue(pos + valinc);
      }
    }
  }

  private void AsignMovementMax(int val)
  {


    if (val == NW)
    {
      speedSlider.setValue(maxs);
      posSlider.setValue(minp);
      //System.out.println("NW");
    }

    if (val == NORTH)
    {
      speedSlider.setValue(maxs);
      posSlider.setValue(centerp);
      //System.out.println("N");
    }

    if (val == NE)
    {
      speedSlider.setValue(maxs);
      posSlider.setValue(maxp);
      //System.out.println("NE");
    }

    if (val == WEST)
    {
      speedSlider.setValue(0);
      posSlider.setValue(minp);
      //System.out.println("W");
    }

    /* by now center is not functional
    if (val==NONE){
    speedSlider.setValue(0);
    posSlider.setValue(maxp/2);
    //System.out.println("center");
    }
     */

    if (val == EAST)
    {
      speedSlider.setValue(0);
      posSlider.setValue(maxp);
      //System.out.println("E");
    }

    if (val == SW)
    {
      speedSlider.setValue(mins);
      posSlider.setValue(minp);
      //System.out.println("SW");
    }

    if (val == SOUTH)
    {
      speedSlider.setValue(mins);
      posSlider.setValue(centerp);
      //System.out.println("S");
    }

    if (val == SE)
    {
      speedSlider.setValue(mins);
      posSlider.setValue(maxp);
      //System.out.println("SE");
    }


  }
}// end class

