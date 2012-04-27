package jaseimov.client.controlcarB;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.traces.Trace2DLtd;
import java.awt.Color;
import java.util.ArrayList;

/**
 *
 * @author santi
 */
public class Interpolation
{
  public double[][] CubicSpline(Chart2D chart, double[][] data, int dt)
  {
    /*
    INPUT
     * chart: it is the graphic where to plot the data
     * data: input data
     * dt: step between the outpot points
     *
    OUTPUT
     * matrix of 2 columns
     *      column 0:time
     *      caolum 1: interpolated values
     */
    int valroll = 1000000;
    ITrace2D pts = new Trace2DLtd(valroll, "points");
    ITrace2D spl = new Trace2DLtd(valroll, "spline");

    pts.setColor(Color.blue);
    spl.setColor(Color.red);

    chart.removeAllTraces();


    int n = data.length;
    double t = 0.0;
    System.out.println("[interpolation]:size:" + n);
    double[] x = new double[n];
    double[] y = new double[n];
    double[] a = new double[n];
    double[] b = new double[n];
    double[] c = new double[n];
    double[] d = new double[n];
    double[] g = new double[n];
    double[] h = new double[n];
    double[] u = new double[n];
    double[] l = new double[n];
    double[] z = new double[n];


    //load x, y
    for (int i = 0; i < n; i++)
    {
      x[i] = data[i][0];
      y[i] = data[i][1];
      pts.addPoint(x[i], y[i]);
      System.out.println("[interpolation]:" + x[i] + "," + y[i]);
    }


    for (int i = 0; i < n - 1; i++)
    {
      h[i] = x[i + 1] - x[i];
    }

    for (int i = 1; i < n - 1; i++)
    {
      g[i] = (3 * (y[i + 1] - y[i]) / h[i]) - (3 * (y[i] - y[i - 1]) / h[i - 1]);
    }



    //Gausian elimination
    u[0] = 0; //mu
    l[0] = 1;
    z[0] = 0;


    for (int i = 2; i < n - 1; i++)
    {
      l[i] = 2 * (x[i + 1] - x[i - 1]) - (h[i - 1] * u[i - 1]);
      u[i] = h[i] / l[i];
      z[i] = (g[i] - (h[i - 1] * z[i - 1])) / l[i];
    }

    c[n - 1] = 0;
    l[n - 1] = 1;
    z[n - 1] = 0;

    //compute a,b,c & d
    for (int i = n - 2; i >= 0; i--)
    {
      a[i] = y[i];
      c[i] = z[i] - (u[i] * c[i + 1]);
    }

    for (int i = n - 2; i >= 0; i--)
    {
      b[i] = ((y[i + 1] - y[i]) / h[i]) - (h[i] * (c[i + 1] + 2 * c[i]) / 3);
      d[i] = (c[i + 1] - c[i]) / (3 * h[i]);
    }

    //now compute the spline set of points
    // t is the time incremented in dt in each step
    double sxp, syp;
    ArrayList<Double> sx = new ArrayList<Double>();
    ArrayList<Double> sy = new ArrayList<Double>();
    int np = 0;
    System.out.println("[interpolation]:Max time: x(" + n + "):" + x[n - 1]);

    for (int i = 0; i < n - 1; i++)
    {
      for (t = x[i]; t <= x[i + 1]; t += dt)
      {
        sxp = t;
        sx.add(sxp);
        syp = a[i] + (t - x[i]) * (b[i] + (t - x[i]) * (c[i] + (t - x[i]) * d[i]));
        sy.add(syp);
        spl.addPoint(sxp, syp);
        System.out.println("[interpolation]:" + sxp + "," + syp);
        np++;
      }
    }

    double[][] s = new double[np][2];

    for (int i = 0; i < np; i++)
    {
      s[i][0] = sx.get(i);
      s[i][1] = sy.get(i);
    }

    chart.addTrace(pts);
    chart.addTrace(spl);
    return s;
  }

  public double[][] Linear(Chart2D chart, double[][] data, int dt)
  {
    /*
    INPUT
     * chart: it is the graphic where to plot the data
     * data: input data
     * dt: step between the outpot points
     *
    OUTPUT
     * matrix of 2 columns
     *      column 0:time
     *      caolum 1: interpolated values
     */
    int valroll = 1000000;
    ITrace2D pts = new Trace2DLtd(valroll, "points");
    ITrace2D spl = new Trace2DLtd(valroll, "linear");

    pts.setColor(Color.blue);
    spl.setColor(Color.red);

    chart.removeAllTraces();

    int n = data.length;
    double t = 0.0;
    double[] x = new double[n];
    double[] y = new double[n];

    //load x, y and also compute h and b
    for (int i = 0; i < n; i++)
    {
      x[i] = data[i][0];
      y[i] = data[i][1];
      pts.addPoint(x[i], y[i]);
      System.out.println("[interpolation]:" + x[i] + "," + y[i]);
    }

    //now compute the spline set of points
    // t is the time incremented in dt in each step
    double sxp, syp;
    ArrayList<Double> sx = new ArrayList<Double>();
    ArrayList<Double> sy = new ArrayList<Double>();
    int np = 0;
    System.out.println("[interpolation]:Max time: x(" + n + "):" + x[n - 1]);
    for (int i = 0; i < n - 1; i++)
    {
      for (t = x[i]; t <= x[i + 1]; t += dt)
      {
        sxp = t;
        sx.add(sxp);
        syp = y[i] + ((y[i + 1] - y[i]) / (x[i + 1] - x[i])) * (t - x[i]);
        sy.add(syp);
        spl.addPoint(sxp, syp);
        System.out.println("[interpolation]:" + sxp + "," + syp);
        np++;
      }
    }

    double[][] s = new double[np][2];

    for (int i = 0; i < np; i++)
    {
      s[i][0] = sx.get(i);
      s[i][1] = sy.get(i);
    }

    chart.addTrace(pts);
    chart.addTrace(spl);
    return s;

  }

  public double[][] Quadratic(Chart2D chart, double[][] data, int dt)
  {
    /*
    INPUT
     * chart: it is the graphic where to plot the data
     * data: input data
     * dt: step between the outpot points
     *
    OUTPUT
     * matrix of 2 columns
     *      column 0:time
     *      caolum 1: interpolated values
     */
    int valroll = 1000000;
    ITrace2D pts = new Trace2DLtd(valroll, "points");
    ITrace2D spl = new Trace2DLtd(valroll, "quadratic");

    pts.setColor(Color.blue);
    spl.setColor(Color.red);

    chart.removeAllTraces();

    int n = data.length;
    double t = 0.0;
    double[] x = new double[n];
    double[] y = new double[n];
    double[] z = new double[n];

    //load x, y and also compute h and b
    for (int i = 0; i < n; i++)
    {
      x[i] = data[i][0];
      y[i] = data[i][1];
      pts.addPoint(x[i], y[i]);
      System.out.println("[interpolation]:" + x[i] + "," + y[i]);
    }

    //z coefficients
    z[0] = y[0];
    for (int i = 1; i < n; i++)
    {
      z[i] = -z[i - 1] + 2 * ((y[i - 1] - y[i]) / (x[i - 1] - x[i]));
    }

    //now compute the spline set of points
    // t is the time incremented in dt in each step
    double sxp, syp;
    ArrayList<Double> sx = new ArrayList<Double>();
    ArrayList<Double> sy = new ArrayList<Double>();
    int np = 0;
    System.out.println("[interpolation]:Max time: x(" + n + "):" + x[n - 1]);
    for (int i = 0; i < n - 1; i++)
    {
      for (t = x[i]; t <= x[i + 1]; t += dt)
      {
        sxp = t;
        sx.add(sxp);
        syp = y[i] + z[i] * (t - x[i]) + ((z[i + 1] - z[i]) / (2 * (x[i + 1] - x[i]))) * (t - x[i]) * (t - x[i]);
        sy.add(syp);
        spl.addPoint(sxp, syp);
        System.out.println("[interpolation]:" + sxp + "," + syp);
        np++;
      }
    }

    double[][] s = new double[np][2];

    for (int i = 0; i < np; i++)
    {
      s[i][0] = sx.get(i);
      s[i][1] = sy.get(i);
    }

    chart.addTrace(pts);
    chart.addTrace(spl);
    return s;

  }
}
