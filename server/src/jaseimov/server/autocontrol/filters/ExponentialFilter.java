package jaseimov.server.autocontrol.filters;

/**
 *
 * @author Aday Talavera <aday.talavera at gmail.com>
 */
public final class ExponentialFilter implements Filter
{
  private double alfa;
  private double a;
  private double a0;
  private double valExp0;

  public ExponentialFilter(double alfa)
  {
    this.alfa = alfa;
    reset();
  }

  public double filter(double val)
  {
    //alfa: noise level from 0 to 1
    a0 = a;
    valExp0 = alfa * a0 + (1 - alfa) * valExp0;
    a = val;
    return valExp0;
  }

  public void reset()
  {
    a = 0.0;
    a0 = 0.0;
    valExp0 = 0.0;
  }
}
