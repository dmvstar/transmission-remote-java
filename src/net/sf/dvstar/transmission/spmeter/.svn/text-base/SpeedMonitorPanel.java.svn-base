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

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import javax.swing.*;

public class SpeedMonitorPanel extends JPanel implements Runnable {

    public Thread thread;
    public long sleepAmount = 1000;
    private int widthMeter, heightMeter;
    private BufferedImage buffImage;
    private Graphics2D canva2D;
    private Font font = new Font("Times New Roman", Font.PLAIN, 11);
    private Runtime r = Runtime.getRuntime();
    private int columnInc;
    private int ptsData[];
    private int ptNum;
    private int ascent, descent;
    private float freeMemory, totalMemory;
    private Rectangle graphOutlineRect = new Rectangle();
    private Rectangle2D mfRect = new Rectangle2D.Float();
    private Rectangle2D muRect = new Rectangle2D.Float();
    private Line2D graphLine = new Line2D.Float();
    private Color graphColor = new Color(46, 139, 87);
    private Color mfColor = new Color(0, 100, 0);
    private String usedStr;
    private double noise = 1.0;
    private final SpeedMonitorProviderInterface provider;

    
    public SpeedMonitorPanel(  SpeedMonitorProviderInterface provider ) {
        this.provider = provider;
        setBackground(Color.black);
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (thread == null) {
                    start();
                } else {
                    stop();
                }
            }
        });


        addMouseMotionListener( new MouseMotionAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                double x = e.getPoint().x;
                double y = e.getPoint().y;

                if(x>0 & y>0)
                    noise = x/y<0?x/y:y/x;
                else
                    noise = 1;
                noise = noise>1?1:noise;
//System.out.println(x+" "+y+" "+noise);
            }

        });

    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(320, 80);
    }

    @Override
    public void paint(Graphics g) {

        if (canva2D == null) {
            return;
        }

        canva2D.setBackground(getBackground());
        canva2D.clearRect(0, 0, widthMeter, heightMeter);

        freeMemory  = (float) r.freeMemory();
        totalMemory = (float) r.totalMemory();

        // .. Draw allocated and used strings ..
        canva2D.setColor(Color.green);
        canva2D.drawString(String.valueOf((int) totalMemory / 1024) + "K allocated", 4.0f, (float) ascent + 0.5f);
        usedStr = String.valueOf(((int) (totalMemory - freeMemory)) / 1024) + "K used";
        canva2D.drawString(usedStr, 4, heightMeter - descent);

        int rowsCount = 15;

        // Calculate remaining size
        float ssH = ascent + descent;
        float remainingHeight = (float) (heightMeter - (ssH * 2) - 0.5f);
        float blockHeight = remainingHeight / rowsCount;

        float blockWidth = 20.0f;

        // .. Memory Free ..
        canva2D.setColor(mfColor);
        int MemUsage = (int) ((freeMemory / totalMemory) * 10);
        int i = 0;
        for (; i < MemUsage; i++) {
            mfRect.setRect(5, (float) ssH + i * blockHeight,
                    blockWidth, (float) blockHeight - 1);
            canva2D.fill(mfRect);
        }

        // .. Memory Used ..
        canva2D.setColor(Color.green);
        for (; i < rowsCount; i++) {
            muRect.setRect(5, (float) ssH + i * blockHeight,
                    blockWidth, (float) blockHeight - 1);
            canva2D.fill(muRect);
        }

        // .. Draw History Graph ..
        canva2D.setColor( graphColor );
        int graphX = 30;
        int graphY = (int) ssH;
        int graphW = widthMeter - graphX - 5;
        int graphH = (int) remainingHeight;
        graphOutlineRect.setRect(graphX, graphY, graphW, graphH);
        canva2D.draw(graphOutlineRect);

        int graphRow = graphH / rowsCount;

        // .. Draw row ..
        for (int j = graphY; j <= graphH + graphY; j += graphRow) {
            graphLine.setLine(graphX, j, graphX + graphW, j);
            canva2D.draw(graphLine);
        }

        // .. Draw animated column movement ..
        int graphColumn = graphW / 15;

        if (columnInc == 0) {
            columnInc = graphColumn;
        }

        for (int j = graphX + columnInc; j < graphW + graphX; j += graphColumn) {
            graphLine.setLine(j, graphY, j, graphY + graphH);
            canva2D.draw(graphLine);
        }

        --columnInc;


        // @todo use data provider for paint

        provider.getMeters().get(0).setMaxValue(graphW);

        provider.getMeters().get(0).addValue();

        for (int j = graphX + graphW - ptNum, p = 0; p < provider.getMeters().get(0).getLength(); p++, j++) {
            //System.out.println(provider.getMeters().get(0).printArray());
            canva2D.setColor(Color.red);
                if (p != 0) {
                    int cv0 =  (int) ((3+graphY) + graphH  * provider.getMeters().get(0).getCurValue(p));
                    int cv1 =  (int) ((3+graphY) + graphH  * provider.getMeters().get(0).getCurValue(p - 1));
                    if ( cv0 != cv1 ) {
                        canva2D.drawLine(j - 1, cv1 , j, cv0);
                    } else {
                        canva2D.fillRect(j, cv0, 1, 1);
                    }
                }
        }

/*
        if (ptsData == null) {
            ptsData = new int[graphW];
            ptNum = 0;
        } else if (ptsData.length != graphW) {
            int tmp[] = null;
            if (ptNum < graphW) {
                tmp = new int[ptNum];
                System.arraycopy(ptsData, 0, tmp, 0, tmp.length);
            } else {
                tmp = new int[graphW];
                System.arraycopy(ptsData, ptsData.length - tmp.length, tmp, 0, tmp.length);
                ptNum = tmp.length - 1;
            }
            ptsData = new int[graphW];
            System.arraycopy(tmp, 0, ptsData, 0, tmp.length);
            //ptNum = graphW;
        } else {

            canva2D.setColor(Color.yellow);
            ptsData[ptNum] = (int) (graphY + graphH * (freeMemory / totalMemory)*1 );

            for (int j = graphX + graphW - ptNum, k = 0; k < ptNum; k++, j++) {
                if (k != 0) {
                    if (ptsData[k] != ptsData[k - 1]) {
                        canva2D.drawLine(j - 1, ptsData[k - 1], j, ptsData[k]);
                    } else {
                        canva2D.fillRect(j, ptsData[k], 1, 1);
                    }
                }
            }

            if (ptNum + 1 == ptsData.length) {
                // throw out oldest point
                for (int j = 1; j < ptNum ; j++) {
                    ptsData[j - 1] = ptsData[j];
                }
                ptsData[ptNum-1] = (int) (graphY + graphH * (freeMemory / totalMemory)*1);
                //--ptNum;
            } else {
            }


        }
*/

            if (ptNum < graphW) {
                ptNum++;
            } else {
            }



        g.drawImage(buffImage, 0, 0, this);
    }

    boolean started = false;

    public void start() {
        started = true;
        thread = new Thread(this);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.setName("MemoryMonitor");
        thread.start();
    }

    public synchronized void stop() {
        started = false;
        thread = null;
        notify();
    }

    public void run() {

        Thread me = Thread.currentThread();

        while (thread == me && !isShowing() || getSize().width == 0) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                return;
            }
        }

        while (thread == me && isShowing()) {
            Dimension d = getSize();
            if (d.width != widthMeter || d.height != heightMeter) {
                widthMeter = d.width;
                heightMeter = d.height;
                buffImage = (BufferedImage) createImage(widthMeter, heightMeter);
                canva2D = buffImage.createGraphics();
                canva2D.setFont(font);
                FontMetrics fm = canva2D.getFontMetrics(font);
                ascent = (int) fm.getAscent();
                descent = (int) fm.getDescent();
            }
            repaint();
            try {
                Thread.sleep(sleepAmount);
            } catch (InterruptedException e) {
                break;
            }
        }
        thread = null;
    }

    private void printArray(int[] pts) {
        for(int i=0;i<pts.length;i++)
            System.out.print("["+pts[i]+"]");
        System.out.println();
    }

    void clear() {
        ptsData = null;
        for(int i=0;i<provider.getMeters().size();i++)
            provider.getMeters().get(i).clearValue();
    }
}
