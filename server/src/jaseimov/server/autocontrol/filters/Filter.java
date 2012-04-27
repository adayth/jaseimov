package jaseimov.server.autocontrol.filters;

/**
 *
 * @author Aday Talavera <aday.talavera at gmail.com>
 */
public interface Filter
{
  double filter(double val);
  void reset();
}
