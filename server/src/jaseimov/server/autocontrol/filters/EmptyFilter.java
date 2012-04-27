package jaseimov.server.autocontrol.filters;

/**
 *
 * @author Aday Talavera <aday.talavera at gmail.com>
 */
public class EmptyFilter implements Filter
{
  public double filter(double val)
  {
    return val;
  }

  public void reset()
  {    
  }
}
