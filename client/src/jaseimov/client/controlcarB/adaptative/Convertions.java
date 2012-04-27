package jaseimov.client.controlcarB.adaptative;

public class Convertions
{

  /*Las siguientes variables se declaran como vector
  por su necesidad de ser estáticas y para permitir la simultaneidad
  de ejecución de las rutinas a las que pertenecen*/
  //variables estáticas de a2v
  private double vx0 = 0.0; // velocidad x anterior
  private double vy0 = 0.0; // velocidad y anterior
  private double vz0 = 0.0; // velocidad z anterior
  //variables estáticas de v2e
  private double px0 = 0.0; //posicion x inicial
  private double py0 = 0.0; //posicion y inicial
  private double pz0 = 0.0; //posicion z inicial
  //Variables de condición inicial que pueden ser ajustadas a un valor
  private double vxci;
  private double vyci;
  private double vzci;
  private double pxci;
  private double pyci;
  private double pzci;
  //variable acumulativa de "tiempo anterior" para calculo de incrementos de tiempo
  private double t0 = System.currentTimeMillis(); //tiempo anterior

  public void setv0(double vx, double vy, double vz)
  {
    vxci = vx;
    vyci = vy;
    vzci = vz;
  }

  public void setp0(double px, double py, double pz)
  {
    pxci = px;
    pyci = py;
    pzci = pz;
  }

  public double[] a2v(double ax, double ay, double az)
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


    //convertir el valor de "a" de G's a m/s2.
    ax = ax / 9.81;
    ay = ay / 9.81;
    az = az / 9.81;


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

  public double[] v2e(double vx, double vy, double vz)
  {

    // vx,vy,vz: velocidad actual v(t)
    // layer: capa de ejecución de la funcion

    double[] x =
    {
      0, 0, 0
    };
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
