package jaseimov.server.autocontrol.filters;

/**
 *
 * @author Aday Talavera <aday.talavera at gmail.com>
 */
public final class HypergeometricFilter implements Filter
{
  private int k;
  private double[] samples;
  private boolean full2;
  private double mult;
  private double valh0 = 0.0;
  private int i;

  public HypergeometricFilter(int k)
  {
    this.k = k;
    reset();
  }

  public double filter(double val)
  {
    double sig = 1.0;

    mult = mult * val;

    i++;
    if (i > k - 1)
    {
      i = 0;
      full2 = true;
    }

    if (full2 == true)
    {
      mult = mult / samples[i];
    }


    if (val > 0.0)
    {
      sig = 1.0;
    }
    else
    {
      sig = -1.0;
    }

    samples[i] = val;
    valh0 = val;

    // trick
    if ((valh0 > 0 && val <= 0) || (valh0 < 0 && val >= 0))
    {
      reset();
    }
    //end trick

    return sig * Math.pow(Math.abs(mult), (1.0 / k));
  }

  public void reset()
  {    
    samples = new double[k];
    full2 = false;
    mult = 1.0;
    i = -1;
  }
}
