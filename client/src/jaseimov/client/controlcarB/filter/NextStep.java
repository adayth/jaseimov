package jaseimov.client.controlcarB.filter;

public class NextStep
{

  /* MUY IMPORTANTE

   * REVISE la consistencia de unidades, especialmente las de dt con las curvas de referencia
   * deberian estar en milisegundos ambos.

   */
  private static double[][] acfw; //acceleration curve going forward
  private static double[][] dcfw; //decceleration curve going forward
  private static double[][] acbw; //acceleration curve going backwards
  private static double[][] dcbw; //decceleration curve going backwards
  private static double dt = 0;
  private int lastindex = 0;
  private int curveused = 0;

  //constructor
  public NextStep(double[][] accelerationReffw, double[][] brakingReffw, double[][] accelerationRefbw, double[][] brakingRefbw, double samplingstep)
  {
    acfw = accelerationReffw;
    dcfw = brakingReffw;
    acbw = accelerationRefbw;
    dcbw = brakingRefbw;
    dt = samplingstep; // in seconds, just like sequencer
    System.out.println("[NextStep]: dt: " + dt);
  }

  public double next_step(double vact, double vord)
  {


    if (vord > vact && vact >= 0.0)
    {// ACFW
      if (curveused != 1)
      {
        lastindex = 0;
      }
      curveused = 1;
      vact = up_step_speed(vact, acfw);
      if (vact > vord)
      {
        vact = vord;
        System.out.println("[NextStep]:Rectificated to vord:" + vord);
      }
      //System.out.println("ACFW vord:"+vord+" vact:"+vact+ " lastindex:"+lastindex);
    }


    if (vord < vact && vact > 0.0)
    {//DCFW
      if (curveused != 2)
      {
        lastindex = 0;
      }
      curveused = 2;
      vact = down_step_speed(vact, dcfw);
      if (vact < vord)
      {
        vact = vord;
        System.out.println("[NextStep]:Rectificated to vord:" + vord);
      }
      //System.out.println("DCFW vord:"+vord+" vact:"+vact+ " lastindex:"+lastindex);
    }

    if (vord < vact && vact <= 0.0 && vord <= 0.0)
    {// ACBW
      if (curveused != 3)
      {
        lastindex = 0;
      }
      curveused = 3;
      vact = down_step_speed(vact, acbw);
      if (vact < vord)
      {
        vact = vord;
        System.out.println("[NextStep]:Rectificated to vord:" + vord);
      }
      //System.out.println("ACBW vord:"+vord+" vact:"+vact+ " lastindex:"+lastindex);
    }

    if (vord > vact && vact < 0.0)
    {//DCBW
      if (curveused != 4)
      {
        lastindex = 0;
      }
      curveused = 4;
      vact = up_step_speed(vact, dcbw);
      if (vact > vord)
      {
        vact = vord;
        System.out.println("[NextStep]:Rectificated to vord:" + vord);
      }
      //System.out.println("DCBW vord:"+vord+" vact:"+vact+ " lastindex:"+lastindex);
    }



    return vact;
  }

  public void setacfw(double[][] val)
  {
    acfw = val;
  }

  public void setdcfw(double[][] val)
  {
    dcfw = val;
  }

  public void setacbw(double[][] val)
  {
    acbw = val;
  }

  public void setdcbw(double[][] val)
  {
    dcbw = val;
  }

  public void setdt(double val)
  {
    dt = val;
  }

  public int GetCurveUsed()
  {
    return curveused;
  }

  private double up_step_speed(double vin, double[][] ref)
  {
// Me da la siguiente velocidad maxima alcanzable para un incremento de tiempo
// en una curva de pendiente positiva

// ENTRADA
// vin: velocidad actual
// ref(0,j): curva de puntos de referencia
// ref(1,j): vector de tiepmos para el vector de referencia

// SALIDA
// vout: velocidad siguiente posible

// ATENCIÓN: para que funcione, dt debe ser <= que el paso de la curva
    double v1, v2, v3, vout = 0;
    double t1, t2, t3, tin, tout;
    int points = ref.length;
//parametros de control del loop
    int max = points - 2;
    int min = lastindex;
    int inc = 1;


    for (int i = min; i < max; i += inc)
    {
      //localizar entre que dos valores está 'vin'
      v1 = ref[i][1];
      v2 = ref[i + 1][1];
      v3 = ref[i + 2][1];
      t1 = ref[i][0];
      t2 = ref[i + 1][0];
      t3 = ref[i + 2][0];

      //System.out.println(ref[i][0] +","+ref[i][1]);
      //System.out.println("up i"+i +" t1:" + t1 + " t2:"+t2+" t3:"+t3 + " v1:" + v1 + " v2:"+v2+" v3:"+v3+ " vin:" +vin);

      if (vin >= v1 && vin < v3)
      {
        // una vez localizado 'donde estoy ahora'
        // guardo la posición para luego
        lastindex = i;
        //System.out.println(" max_step:found at:"+i + "vin:"+ vin + " dtmod:" + dtmod);
        //aclastindex=i;
        // debo saber donde estaré en t+dt
        tin = t1 + (t2 - t1) * (vin - v1) / (v2 - v1);
        tout = tin + dt;

        //System.out.println("up: i:"+i + " tin:" + tin + " tout:"+tout + " t1:" + t1 + " t2:"+t2+" t3:"+t3);
        //System.out.println(" v1:" + v1 + " v2:"+v2+" v3:"+v3+ " vin:" +vin);
        // tout cae en el intervalo actual (1-2)
        if (tout > t1 && tout <= t2)
        {
          vout = v1 + (tout - t1) * (v2 - v1) / (t2 - t1);
          break;
          //System.out.println(" intervalo 1-2, v1:"+v1+" v2:"+v2+" vout:"+vout);
        }

        //tout cae en el intervalo siguiente (2-3)
        if (tout > t2 && tout <= t3)
        {
          vout = v2 + (tout - t2) * (v3 - v2) / (t3 - t2);
          break;
          //System.out.println(" intervalo 2-3, v2:"+v2+" v3:"+v3+" vout:"+vout);
        }

        // en el caso dt=dtref
        if (tout == t2)
        {
          vout = v2;
          break;
          //System.out.println(" tout=t2, v2:"+v2+" v3:"+v3+" vout:"+vout);
        }

        if (tout >= t3)
        {
          vout = v3;
          break;
          //System.out.println(" tout=t3, v2:"+v2+" v3:"+v3+" vout:"+vout);
        }

        //System.out.println(" tout:"+tout+ " v2:"+v2+" v3:"+v3+" vout:"+vout);
        //System.out.println(" max_step:Velocidad calculada, vout:"+vout);
      }
    }

    if (vout == vin)
    { //no lo encontró
      System.out.println("[NextStep]:Down: Not found.");
    }
    return vout;
  } //max_step_speed

  private double down_step_speed(double vin, double[][] ref)
  {
// Me da la siguiente velocidad maxima alcanzable para un incremento de tiempo
// en una curva e pendiente negativa

// ENTRADA
// vin: velocidad actual
// ref(0,j): curva de puntos de referencia
// ref(1,j): vector de tiemos para el vector de referencia

// SALIDA
// vout: velocidad siguiente posible

// ATENCIÓN: para que funcione, dt debe ser <= que el paso de la curva
    double v1 = 0, v2 = 0, v3 = 0, vout = vin;
    double t1 = 0, t2 = 0, t3 = 0, tin = 0, tout = 0;
    int points = ref.length;
//parametros de control del loop
    int max = points - 2;
    int min = lastindex;
    int inc = 1;

//System.out.println("min_step");
// me sitúo en la curva de referencia
//System.out.println("down_step_speed: Vin: "+vin);



    for (int i = min; i < max; i += inc)
    {
      //localizar entre que dos valores está 'vin'
      v1 = ref[i][1];
      v2 = ref[i + 1][1];
      v3 = ref[i + 2][1];
      t1 = ref[i][0];
      t2 = ref[i + 1][0];
      t3 = ref[i + 2][0];

      //System.out.println(ref[i][0] +","+ref[i][1]);
      //System.out.println("down i"+i +" t1:" + t1 + " t2:"+t2+" t3:"+t3 + " v1:" + v1 + " v2:"+v2+" v3:"+v3+ " vin:" +vin);

      if (vin <= v1 && vin > v3)
      {
        // una vez localizado 'donde estoy ahora'
        // guardo la posición para luego
        lastindex = i;
        //System.out.println(" min_step:found at:"+i + " vin:"+ vin  + " dt:" + dt);
        //dclastindex=i;
        // debo saber donde estaré en t+dt
        tin = t1 + (t2 - t1) * (vin - v1) / (v2 - v1);
        tout = tin + dt;

        //System.out.println("Down: i:"+i + " tin:" + tin + " tout:"+tout + " t1:" + t1 + " t2:"+t2+" t3:"+t3);
        //System.out.println(" v1:" + v1 + " v2:"+v2+" v3:"+v3+ " vin:" +vin);

        // tout cae en el intervalo actual (1-2)
        if (tout > t1 && tout <= t2)
        {
          vout = v1 - (tout - t1) * (v1 - v2) / (t2 - t1);
          break;
          //System.out.println(" intervalo 1-2, v1:"+v1+" v2:"+v2+" vout:"+vout);
        }

        //tout cae en el intervalo siguiente (2-3)
        if (tout >= t2 && tout <= t3)
        {
          vout = v2 - (tout - t2) * (v2 - v3) / (t3 - t2);
          break;
          //System.out.println(" intervalo 2-3, v2:"+v2+" v3:"+v3+" vout:"+vout);
        }

        // en el caso dt=dtref
        if (tout == t2)
        {
          vout = v2;
          break;
          //System.out.println(" tout=t2, v2:"+v2+" v3:"+v3+" vout:"+vout);
        }

        if (tout >= t3)
        {
          vout = v3;
          break;
        }

        //System.out.println(" min_step:Velocidad calculada, vout:"+vout);
      }
    }

    if (vout == vin)
    { //no lo encontró
      System.out.println("[NextStep]:Down: Not found.");
    }

    return vout;

  } //min_step_speed
}
