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


package net.sf.dvstar.transmission.progressbar;

import java.awt.Color;


public class RGB extends Color {

    /** The Color represented */
    private Color color;

    public RGB(int r, int g, int b) {
        super(0);
        color = new Color(r, g, b);
    }

    public RGB(Color color) {
        super(0);
        this.color = color;
    }

    /**
     * Overrides superclass method
     */
    @Override
    public int getRGB() {
        return color.getRGB();
    }

    /**
     * Sets the new RGB value of the WebColor
     *
     * @param r
     * @param g
     * @param b
     */
    public void setRGB(int r, int g, int b) {
        color = new Color(r, g, b);
    }

    /**
     * Sets the internal Color to the parameter Color
     *
     * @param color
     */
    public void setColor(Color color) {
        this.color = color;
    }
}
		    				    			                    			            		