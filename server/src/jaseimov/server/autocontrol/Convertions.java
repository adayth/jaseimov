package jaseimov.server.autocontrol;

public class Convertions
{
  /*Las siguientes variables se declaran como vector
  por su necesidad de ser estáticas y para permitir la simultaneidad
  de ejecución de las rutinas a las que pertenecen*/
  
  //variables estáticas de a2v_3
  private double vx0 = 0.0; // velocidad x anterior
  private double vy0 = 0.0; // velocidad y anterior
  private double vz0 = 0.0; // velocidad z anterior

  //variables estáticas de v2e_3
  private double px0 = 0.0; //posicion x inicial
  private double py0 = 0.0; //posicion y inicial
  private double pz0 = 0.0; //posicion z inicial

  // variables estativas de a2v
  private double v0 = 0.0;

  // variables estativas de v2e
  private double e0 = 0.0;
  
  //variable acumulativa de "tiempo anterior" para calculo de incrementos de tiempo
  private double t0 = System.currentTimeMillis(); //tiempo anterior

  public void setv0(double val)
  {
    v0 = val;
  }

  public void setp0(double val)
  {
    e0 = val;
  }

  public void setv0_3(double vx, double vy, double vz)
  {
    vx0 = vx;
    vy0 = vy;
    vz0 = vz;
  }

  public void setp0_3(double px, double py, double pz)
  {
    px0 = px;
    py0 = py;
    pz0 = pz;
  }

  public double a2v(double a)
  { //DINAMICA

    // a: aceleración actual a(t)
    // it: incremento de tiempo

    //la aceleracón viene dada en G (1G = 9.81 m/s2) en el dispositivo
    // el tiempo viene en milisegundos

    double v = 0.0;
    double t = System.currentTimeMillis();
    double it = (t - t0) / 1000.0; // convertir el incremento de tiempo de ms a s


    //velocidad x
    v = Math.sqrt((v0 * v0) + (2 * a * v0 * it) + (0.5 * a * a * it * it));


    //actualizar valores
    v0 = v;

    //System.out.println("ax:" + ax + ", ay:" + ay + ", az:" + az);
    //System.out.println("vx:" + v[0] + ", vy:" + v[1] + ", vz:" + v[2]);
    //System.out.println("t:" + t + "- t0:" + t0 + "= it:" + it);

    //actualizar tiempo
    t0 = t;
    return v; //v se devuelve en m/s

  }

  public double[] a2v_3(double ax, double ay, double az)
  { //DINAMICA

    // ax,ay,az: aceleración actual a(t)
    // it: incremento de tiempo
    // layer: capa de ejecución de la funcion

    //la aceleracón viene dada en G (1G = 9.81 m/s2) en el dispositivo
    // el tiempo viene en milisegundos

    double[] v =
    {
      0, 0, 0
    };
    double t = System.currentTimeMillis();
    double it = (t - t0) / 1000.0; // convertir el incremento de tiempo de ms a s


    //velocidad x
    v[0] = Math.sqrt((vx0 * vx0) + (2 * ax * vx0 * it) + (0.5 * ax * ax * it * it));

    //velocidad y
    v[1] = Math.sqrt((vy0 * vy0) + (2 * ay * vy0 * it) + (0.5 * ay * ay * it * it));

    //velocidad x
    v[2] = Math.sqrt((vz0 * vz0) + (2 * az * vz0 * it) + (0.5 * az * az * it * it));

    //actualizar valores
    vx0 = v[0];
    vy0 = v[1];
    vz0 = v[2];

    //System.out.println("ax:" + ax + ", ay:" + ay + ", az:" + az);
    //System.out.println("vx:" + v[0] + ", vy:" + v[1] + ", vz:" + v[2]);
    //System.out.println("t:" + t + "- t0:" + t0 + "= it:" + it);

    //actualizar tiempo
    t0 = t;
    return v; //v se devuelve en m/s

  }

  public double v2e(double vpos)
  {

    // vx,vy,vz: velocidad actual v(t)

    double e = 0.0;
    double t = System.currentTimeMillis();
    double it = (t - t0) / 1000.0; // convertir el incremento de tiempo de ms a s

    //x=x0+v+it;

    //posicion en x
    e = px0 + vpos * it;

    //actualizar posicion
    e0 = e;

    //actualizar tiempo
    t0 = t;

    return e; // x se devuelve en m
  }

  public double[] v2e_3(double vx, double vy, double vz)
  {

    // vx,vy,vz: velocidad actual v(t)

    double[] x = {0, 0, 0};
    double t = System.currentTimeMillis();
    double it = (t - t0) / 1000.0; // convertir el incremento de tiempo de ms a s


    //x=x0+v+it;

    //posicion en x
    x[0] = px0 + vx * it;

    //posicion en y
    x[1] = py0 + vy * it;

    //posicion en z
    x[2] = pz0 + vz * it;



    //actualizar posicion
    px0 = x[0];
    py0 = x[1];
    pz0 = x[2];

    //actualizar tiempo
    t0 = t;

    return x; // x se devuelve en m
  }
}
