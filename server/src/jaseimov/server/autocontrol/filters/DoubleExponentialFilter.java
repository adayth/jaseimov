package jaseimov.server.autocontrol.filters;

/**
 *
 * @author Aday Talavera <aday.talavera at gmail.com>
 */
public final class DoubleExponentialFilter implements Filter
{
  private double alfa;
  private double beta;
  private double dt;
  private double sn0;
  private double bn0;
  private double fn0;

  public DoubleExponentialFilter(double alfa, double beta, double dt)
  {
    this.alfa = alfa;
    this.beta = beta;
    this.dt = dt;
    reset();
  }

  public double filter(double val)
  {
    double sn = alfa * val + (1 - alfa) * fn0;
    double bn = beta * (sn - sn0) + (1 - beta) * bn0;
    double fn = sn + dt * bn;

    sn0 = sn;
    bn0 = bn;
    fn0 = fn;
    return fn;
  }

  public void reset()
  {
    sn0 = 0.0;
    bn0 = 0.0;
    fn0 = 0.0;
  }

}
