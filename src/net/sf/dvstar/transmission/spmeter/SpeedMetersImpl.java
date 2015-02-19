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


package net.sf.dvstar.transmission.spmeter;

/**
 *
 * @author dstarzhynskyi
 */
public class SpeedMetersImpl implements SpeedMetersInterface {

    private int maxValue = 0;
    private float ptsData[];
    /**
     * index of array & current len
     */
    
    private float freeMemory, totalMemory;
    private Runtime r = Runtime.getRuntime();
    /**
     * len of filled data
     */
    private int length = 0;

    public SpeedMetersImpl() {
    }

    public void setMaxValue(int graphW) {
        this.maxValue = graphW;
        
        if (ptsData == null) {
            ptsData = new float[graphW];
            length = 0;
        } else if (ptsData.length != graphW) {
            float tmp[] = null;
            // must be scale to new size !!
            if (length < graphW) {
                tmp = new float[length];
                System.arraycopy(ptsData, 0, tmp, 0, tmp.length);
            } else {
                tmp = new float[graphW];
                System.arraycopy(ptsData, ptsData.length - tmp.length, tmp, 0, tmp.length);
                length = tmp.length - 1;
            }
            ptsData = new float[graphW];
            System.arraycopy(tmp, 0, ptsData, 0, tmp.length);
        }
    }

    public int getMaxValue() {
        return maxValue;
    }

    public int getLength() {
        return length;
    }

    public float getCurValue(int pos){
        if (pos <= length) {
            return ptsData[pos];
        } else {
            return 0;
            //throw new SpeedMetersException("Fail get data for [" + pos + "]");
        }
    }
    //01234567890123456789
    //....................
    // add value to array until index < max
    public void addValue() {
        freeMemory = (float) r.freeMemory();
        totalMemory = (float) r.totalMemory();

        if (length + 1 == maxValue) {
            // shift data left
            System.arraycopy(ptsData, 1, ptsData, 0, length-1);
            /**
            for (int j = 1; j < length; j++) {
                ptsData[j - 1] = ptsData[j];
            }
            */
            length--;
        }
        ptsData[length] = (float) ((freeMemory / totalMemory) * Math.random());
//System.out.println("["+ptsNum+"] "+(freeMemory / totalMemory)+" "+  ptsData[ptsNum]);
        if (length + 1 < maxValue) {
            length++;
        }
    }

    public String printArray() {
        String ret = "[" + maxValue + "][" + length + "]=";

        ret += "[" + (float) ptsData[0] + "]"+"[" + (float) ptsData[length-1] + "]"+"[" + (float) ptsData[length] + "]";

 /*
        for (int i = 0; i < ptsNum; i++) {
            ret += "[" + (float) ptsData[i] + "]";
        }
*/
        return ret;
    }

    public void clearValue() {
        ptsData = null;
    }
}
