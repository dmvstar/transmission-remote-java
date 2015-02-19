/*
 * transmission-remote-java remote control for transmission daemon
 *
 * Copyright (C) 2009-2011 Dmytro Starzhynskyi (dvstar)
 * http://transmission-rj.sourceforge.net/
 * http://code.google.com/p/transmission-remote-java/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.sf.dvstar.transmission.comps;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

/**
 *
 * @author dstarzhynskyi
 */
public class PiecesGraph extends JPanel {

    private byte[] bits;
    private int len;
    private boolean full;

    public PiecesGraph() {
        super();
        setBackground(Color.GRAY);
        setForeground(Color.GREEN);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);    // paints background
        // do your drawing here
        if (len > 0) {
            int width = getWidth();
            double scale = (double)len / (double)width;
            for (int n = 0; n < getWidth(); n++) {
                int index = (int) (n * scale);
                if (bitGet(bits, len, index)) {
                    g.drawLine(n, 0, n, getHeight());
                }
            }
        } else {
            if( isFull() ) {
                g.fillRect(0, 0, getWidth(), getHeight());
            }
            else {
                g.clearRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    private  boolean bitGet(byte[] array, int len, int index) {
        if (index < 0 || index >= len) {
            return false;
        }
        if (isFull()){
            return true;
        } else {
            return (array[index >> 3] & (1 << ((7 - index) & 7))) != 0;
        }
    }

    public void applyBits(byte[] b, int len) {
        this.len = len;
        this.bits = b;
        invalidate();
    }

    public void clearBits() {
        this.len = 0;
        this.bits = new byte[0];
        invalidate();
    }

    public boolean isFull() {
        return full;
    }

    public void setFull( boolean full ) {
        this.full = full;
        repaint();
    }
}
