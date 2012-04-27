package jaseimov.server.autocontrol.filters;

/**
 *
 * @author Aday Talavera <aday.talavera at gmail.com>
 */
public final class SquareFilter implements Filter
{
  private int k;
  private double[] samples;
  private boolean full1;
  private double sum;
  private int i;

  public SquareFilter(int k)
  {
    this.k = k;
    reset();
  }

  public double filter(double val)
  {
    sum = sum + val;

    i++;
    if (i > k - 1)
    {
      i = 0;
      full1 = true;
    }

    if (full1 == true)
    {
      sum = sum - samples[i];
    }

    samples[i] = val;

    if (full1 == true)
    {
      return sum / k;
    }
    else
    {
      return sum / (i + 1);
    }
  }

  public void reset()
  {
    samples = new double[k];
    full1 = false;
    sum = 0.0;
    i = -1;
  }
}
