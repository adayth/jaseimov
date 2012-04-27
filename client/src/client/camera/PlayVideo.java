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
package client.camera;

import client.update.SensorCapturer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 * Play a video of a CameraPanel using a Timer.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
class PlayVideo implements ActionListener
{

    private CameraPanel cameraPanel;
    private SensorCapturer capturer;
    private Timer timer = new Timer(0, this);
    private int playPosition = 0;
    private boolean playing = false;

    public PlayVideo(CameraPanel cameraPanel)
    {
        this.cameraPanel = cameraPanel;
        capturer = cameraPanel.capturer;
    }

    public void play()
    {
        timer.setDelay(capturer.getUpdater().getUpdateTime());
        timer.start();
        playing = true;
    }

    public void stop()
    {
        timer.stop();
        playing = false;
    }

    public boolean isPlaying()
    {
        return playing;
    }

    public int getPlayPosition()
    {
        return playPosition;
    }

    public void setPlayPosition(int position)
    {
        playPosition = position;
    }

    public void actionPerformed(ActionEvent e)
    {
        if (playPosition < capturer.getSize())
        {
            byte[] image = (byte[]) capturer.getData(playPosition);
            cameraPanel.updateImage(image);
            cameraPanel.updatePosition();
            playPosition++;
        }
        else
        {
            cameraPanel.playButton.setSelected(false);
            playPosition = 0;
            stop();
        }
    }
}
