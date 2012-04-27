/*
 * Copyright (C) 2010 Aday Talavera Hierro <aday.talavera@gmail.com>
 *
 * This file is part of JASEIMOV.
 *
 * JASEIMOV is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JASEIMOV is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JASEIMOV.  If not, see <http://www.gnu.org/licenses/>.
 */
package jaseimov.lib.concurrent;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PeriodicTaskRunner
{
  private Runnable task;
  private long frequency;
  private boolean started = false;
  private ScheduledThreadPoolExecutor threadPool = new ScheduledThreadPoolExecutor(1);
  private ScheduledFuture taskFuture;

  public PeriodicTaskRunner(Runnable task, long frequency)
  {
    this.task = task;
    this.frequency = frequency;
  }

  public void start()
  {
    if (!started)
    {
      taskFuture = threadPool.scheduleAtFixedRate(task, 0, frequency, TimeUnit.MILLISECONDS);
      started = true;
    }
  }

  public void stop()
  {
    if (started)
    {
      taskFuture.cancel(false);
      started = false;
    }
  }

  public boolean istarted()
  {
    return started;
  }  
}
