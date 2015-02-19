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


/*
 * SessionStatistic.java
 *
 * Created on 21.07.2009, 15:52:40
 */
package net.sf.dvstar.transmission.dialogs;

import net.sf.dvstar.transmission.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import net.sf.dvstar.transmission.protocol.transmission.TransmissionWebClient;
import net.sf.dvstar.transmission.protocol.transmission.Requests;
import java.io.IOException;
import java.net.UnknownHostException;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import net.sf.dvstar.transmission.protocol.TorrentsCommonException;
import net.sf.dvstar.transmission.utils.TimeSpan;
import net.sf.dvstar.transmission.utils.Tools;
import net.sf.json.JSONObject;
import org.apache.http.HttpException;
import org.jdesktop.application.Action;
import org.jdesktop.application.Task;
import org.openide.util.Exceptions;

/**
 *
 * @author dstarzhynskyi
 */
public class SessionStatistic extends javax.swing.JDialog {

    TransmissionWebClient webClient = null;
    Timer updaterTimer;
    TransmissionView parent;

    /** Creates new form StatsDialog */
    public SessionStatistic(TransmissionView parent, TransmissionWebClient webClient, boolean modal) {
        super(parent.getFrame(), modal);
        this.webClient = webClient;
        this.parent = parent;
        initComponents();
        int messageTimeout = 5000;
        this.getRootPane().setDefaultButton(btClose);

        new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                fillFormData();
                return null;
            }
        }.execute();

        updaterTimer = new Timer(messageTimeout, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                new SwingWorker() {

                    @Override
                    protected Object doInBackground() throws Exception {
                        updaterTimer.stop();
                        fillFormData();
                        updaterTimer.start();
                        return null;
                    }
                }.execute();
            }
        });

        updaterTimer.setRepeats(true);
        updaterTimer.start();
    }

    private void fillFormData() {

        btRefresh.setEnabled(false);

        Requests req = new Requests(parent.getGlobalLogger());
        //\\ret = req.produceConnect();
        if (webClient != null) {
            try {
                JSONObject jobjSessionStats = req.sessionStats();
                JSONObject jobjSessionGet = req.sessionGet();
                parent.tracePrint(true, jobjSessionStats.toString());
                parent.tracePrint(true, jobjSessionGet.toString());

                webClient.processWebRequest(jobjSessionGet, "StatsDialog");
                if (webClient.getResponseData() != null) {
                    String sTemp;
                    JSONObject jresult = JSONObject.fromObject( webClient.getResponseData() );
                    JSONObject args = jresult.getJSONObject("arguments");
                    parent.tracePrint(true, args.toString());
                    sTemp = args.getString("version");
                    tfServerVersion.setText(sTemp);
                }

                webClient.processWebRequest(jobjSessionStats, "StatsDialog");
                if (webClient.getResponseData() != null) {
                    JSONObject jresult = JSONObject.fromObject( webClient.getResponseData() );
                    /*
                    {"arguments":{"activeTorrentCount":94,
                    "cumulative-stats":{"downloadedBytes":171339066334,"filesAdded":8637,"secondsActive":1183718,"sessionCount":69,"uploadedBytes":398813146658},
                    "current-stats":{"downloadedBytes":1382975,"filesAdded":0,"secondsActive":233,"sessionCount":1,"uploadedBytes":31733810},"downloadSpeed":13857,"pausedTorrentCount":0,"torrentCount":94,"uploadSpeed":140722},"result":"success"}
                     */

                    JSONObject args = jresult.getJSONObject("arguments");
                    JSONObject sessionStat = args.getJSONObject("current-stats");
                    JSONObject totatlStat = args.getJSONObject("cumulative-stats");

                    System.out.println("[StatsDialog][jresult]" + jresult.toString());
                    System.out.println("[StatsDialog][sessionStat]" + sessionStat.toString());
                    System.out.println("[StatsDialog][totatlStat]" + totatlStat.toString());

                    String sTemp;
                    sTemp = args.getString("activeTorrentCount");
                    lbActiveTorrentCount.setText(sTemp);

                    sTemp = sessionStat.getString("secondsActive");
                    lbActivityC.setText(TimeSpan.formatTimespanLong(TimeSpan.fromSeconds(sTemp)));
                    sTemp = sessionStat.getString("downloadedBytes");
                    lbDowloadC.setText(Tools.getFileSize(Float.parseFloat(sTemp)));
                    sTemp = sessionStat.getString("uploadedBytes");
                    lbUploadC.setText(Tools.getFileSize(Float.parseFloat(sTemp)));
                    sTemp = sessionStat.getString("filesAdded");
                    lbAddedFileC.setText(sTemp);
                    sTemp = sessionStat.getString("sessionCount");
                    lbSessionC.setText(sTemp);

                    sTemp = totatlStat.getString("secondsActive");
                    lbActivityT.setText(TimeSpan.formatTimespanLong(TimeSpan.fromSeconds(sTemp)));
                    sTemp = totatlStat.getString("downloadedBytes");
                    lbDowloadT.setText(Tools.getFileSize(Float.parseFloat(sTemp)));
                    sTemp = totatlStat.getString("uploadedBytes");
                    lbUploadT.setText(Tools.getFileSize(Float.parseFloat(sTemp)));
                    sTemp = totatlStat.getString("filesAdded");
                    lbAddedFileT.setText(sTemp);
                    sTemp = totatlStat.getString("sessionCount");
                    lbSessionT.setText(sTemp);
                }
            } catch (TorrentsCommonException ex) {
                Exceptions.printStackTrace(ex);
            } catch (UnknownHostException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } catch (HttpException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        btRefresh.setEnabled(true);

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        plTop = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        lbActiveTorrentCount = new javax.swing.JLabel();
        tfServerVersion = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        plInfo = new javax.swing.JPanel();
        plCurrent = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lbDowloadC = new javax.swing.JLabel();
        lbUploadC = new javax.swing.JLabel();
        lbAddedFileC = new javax.swing.JLabel();
        lbSessionC = new javax.swing.JLabel();
        lbActivityC = new javax.swing.JLabel();
        plTotal = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lbDowloadT = new javax.swing.JLabel();
        lbUploadT = new javax.swing.JLabel();
        lbAddedFileT = new javax.swing.JLabel();
        lbSessionT = new javax.swing.JLabel();
        lbActivityT = new javax.swing.JLabel();
        plButtons = new javax.swing.JPanel();
        btRefresh = new javax.swing.JButton();
        btClose = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(net.sf.dvstar.transmission.TransmissionApp.class).getContext().getResourceMap(SessionStatistic.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setMinimumSize(new java.awt.Dimension(730, 220));
        setModal(true);
        setName("Form"); // NOI18N

        plTop.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("plTop.border.title"))); // NOI18N
        plTop.setName("plTop"); // NOI18N
        plTop.setPreferredSize(new java.awt.Dimension(730, 68));

        jLabel11.setText(resourceMap.getString("lbActiveCount.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N

        lbActiveTorrentCount.setText(resourceMap.getString("lbActiveTorrentCount.text")); // NOI18N
        lbActiveTorrentCount.setName("lbActiveTorrentCount"); // NOI18N

        tfServerVersion.setEditable(false);
        tfServerVersion.setText(resourceMap.getString("tfServerVersion.text")); // NOI18N
        tfServerVersion.setToolTipText(resourceMap.getString("tfServerVersion.toolTipText")); // NOI18N
        tfServerVersion.setBorder(null);
        tfServerVersion.setFocusable(false);
        tfServerVersion.setName("tfServerVersion"); // NOI18N

        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N

        javax.swing.GroupLayout plTopLayout = new javax.swing.GroupLayout(plTop);
        plTop.setLayout(plTopLayout);
        plTopLayout.setHorizontalGroup(
            plTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plTopLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(plTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12))
                .addGap(25, 25, 25)
                .addGroup(plTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbActiveTorrentCount, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tfServerVersion, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                .addContainerGap(116, Short.MAX_VALUE))
        );
        plTopLayout.setVerticalGroup(
            plTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plTopLayout.createSequentialGroup()
                .addGroup(plTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbActiveTorrentCount)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(plTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(tfServerVersion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(plTop, java.awt.BorderLayout.PAGE_START);

        plInfo.setName("plInfo"); // NOI18N
        plInfo.setLayout(new java.awt.GridLayout(1, 2));

        plCurrent.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("plCurrent.border.title"))); // NOI18N
        plCurrent.setName("plCurrent"); // NOI18N

        jLabel1.setText(resourceMap.getString("lbDownload.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText(resourceMap.getString("lbUpload.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel3.setText(resourceMap.getString("lbAddedFiles.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        jLabel4.setText(resourceMap.getString("lbSesCount.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jLabel5.setText(resourceMap.getString("lbActiv.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        lbDowloadC.setText(resourceMap.getString("lbDowloadC.text")); // NOI18N
        lbDowloadC.setMinimumSize(new java.awt.Dimension(60, 14));
        lbDowloadC.setName("lbDowloadC"); // NOI18N
        lbDowloadC.setPreferredSize(new java.awt.Dimension(60, 15));

        lbUploadC.setText(resourceMap.getString("lbUploadC.text")); // NOI18N
        lbUploadC.setMinimumSize(new java.awt.Dimension(60, 14));
        lbUploadC.setName("lbUploadC"); // NOI18N
        lbUploadC.setPreferredSize(new java.awt.Dimension(60, 15));

        lbAddedFileC.setText(resourceMap.getString("lbAddedFileC.text")); // NOI18N
        lbAddedFileC.setMinimumSize(new java.awt.Dimension(60, 14));
        lbAddedFileC.setName("lbAddedFileC"); // NOI18N
        lbAddedFileC.setPreferredSize(new java.awt.Dimension(60, 15));

        lbSessionC.setText(resourceMap.getString("lbSessionC.text")); // NOI18N
        lbSessionC.setMinimumSize(new java.awt.Dimension(60, 14));
        lbSessionC.setName("lbSessionC"); // NOI18N
        lbSessionC.setPreferredSize(new java.awt.Dimension(60, 15));

        lbActivityC.setText(resourceMap.getString("lbActivityC.text")); // NOI18N
        lbActivityC.setMinimumSize(new java.awt.Dimension(60, 14));
        lbActivityC.setName("lbActivityC"); // NOI18N
        lbActivityC.setPreferredSize(new java.awt.Dimension(60, 15));

        javax.swing.GroupLayout plCurrentLayout = new javax.swing.GroupLayout(plCurrent);
        plCurrent.setLayout(plCurrentLayout);
        plCurrentLayout.setHorizontalGroup(
            plCurrentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plCurrentLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(plCurrentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(plCurrentLayout.createSequentialGroup()
                        .addGroup(plCurrentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(plCurrentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(plCurrentLayout.createSequentialGroup()
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                            .addGap(54, 54, 54))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, plCurrentLayout.createSequentialGroup()
                            .addGroup(plCurrentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))))
                .addGroup(plCurrentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(plCurrentLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(plCurrentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbUploadC, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
                            .addComponent(lbDowloadC, javax.swing.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)))
                    .addGroup(plCurrentLayout.createSequentialGroup()
                        .addComponent(lbAddedFileC, javax.swing.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, plCurrentLayout.createSequentialGroup()
                        .addGroup(plCurrentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lbActivityC, javax.swing.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE)
                            .addComponent(lbSessionC, javax.swing.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        plCurrentLayout.setVerticalGroup(
            plCurrentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plCurrentLayout.createSequentialGroup()
                .addGroup(plCurrentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(lbDowloadC, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(plCurrentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(lbUploadC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(plCurrentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(lbAddedFileC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(plCurrentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(lbSessionC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(plCurrentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(lbActivityC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(57, Short.MAX_VALUE))
        );

        plInfo.add(plCurrent);

        plTotal.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("plTotal.border.title"))); // NOI18N
        plTotal.setName("plTotal"); // NOI18N

        jLabel6.setText(resourceMap.getString("lbDownload.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        jLabel7.setText(resourceMap.getString("lbUpload.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        jLabel8.setText(resourceMap.getString("lbAddedFiles.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        jLabel9.setText(resourceMap.getString("lbSesCount.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N

        jLabel10.setText(resourceMap.getString("lbActiv.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N

        lbDowloadT.setText(resourceMap.getString("lbDowloadT.text")); // NOI18N
        lbDowloadT.setMinimumSize(new java.awt.Dimension(60, 14));
        lbDowloadT.setName("lbDowloadT"); // NOI18N
        lbDowloadT.setPreferredSize(new java.awt.Dimension(60, 15));

        lbUploadT.setText(resourceMap.getString("lbUploadT.text")); // NOI18N
        lbUploadT.setMinimumSize(new java.awt.Dimension(60, 14));
        lbUploadT.setName("lbUploadT"); // NOI18N
        lbUploadT.setPreferredSize(new java.awt.Dimension(60, 15));

        lbAddedFileT.setText(resourceMap.getString("lbAddedFileT.text")); // NOI18N
        lbAddedFileT.setMinimumSize(new java.awt.Dimension(60, 14));
        lbAddedFileT.setName("lbAddedFileT"); // NOI18N
        lbAddedFileT.setPreferredSize(new java.awt.Dimension(60, 15));

        lbSessionT.setText(resourceMap.getString("lbSessionT.text")); // NOI18N
        lbSessionT.setMinimumSize(new java.awt.Dimension(60, 14));
        lbSessionT.setName("lbSessionT"); // NOI18N
        lbSessionT.setPreferredSize(new java.awt.Dimension(60, 15));

        lbActivityT.setText(resourceMap.getString("lbActivityT.text")); // NOI18N
        lbActivityT.setMinimumSize(new java.awt.Dimension(60, 14));
        lbActivityT.setName("lbActivityT"); // NOI18N
        lbActivityT.setPreferredSize(new java.awt.Dimension(60, 15));

        javax.swing.GroupLayout plTotalLayout = new javax.swing.GroupLayout(plTotal);
        plTotal.setLayout(plTotalLayout);
        plTotalLayout.setHorizontalGroup(
            plTotalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plTotalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(plTotalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addGroup(plTotalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(plTotalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbDowloadT, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbUploadT, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbAddedFileT, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbSessionT, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbActivityT, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        plTotalLayout.setVerticalGroup(
            plTotalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plTotalLayout.createSequentialGroup()
                .addGroup(plTotalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(lbDowloadT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(plTotalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(lbUploadT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(plTotalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(lbAddedFileT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(plTotalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(lbSessionT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(plTotalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(lbActivityT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(57, Short.MAX_VALUE))
        );

        plInfo.add(plTotal);

        getContentPane().add(plInfo, java.awt.BorderLayout.CENTER);

        plButtons.setName("plButtons"); // NOI18N
        plButtons.setPreferredSize(new java.awt.Dimension(400, 48));

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(net.sf.dvstar.transmission.TransmissionApp.class).getContext().getActionMap(SessionStatistic.class, this);
        btRefresh.setAction(actionMap.get("doRefresh")); // NOI18N
        btRefresh.setIcon(resourceMap.getIcon("btRefresh.icon")); // NOI18N
        btRefresh.setText(resourceMap.getString("btRefresh.text")); // NOI18N
        btRefresh.setToolTipText(resourceMap.getString("btRefresh.toolTipText")); // NOI18N
        btRefresh.setDefaultCapable(false);
        btRefresh.setName("btRefresh"); // NOI18N
        btRefresh.setPreferredSize(new java.awt.Dimension(96, 26));

        btClose.setAction(actionMap.get("closeDialog")); // NOI18N
        btClose.setIcon(resourceMap.getIcon("btClose.icon")); // NOI18N
        btClose.setText(resourceMap.getString("btClose.text")); // NOI18N
        btClose.setName("btClose"); // NOI18N
        btClose.setPreferredSize(new java.awt.Dimension(96, 26));

        javax.swing.GroupLayout plButtonsLayout = new javax.swing.GroupLayout(plButtons);
        plButtons.setLayout(plButtonsLayout);
        plButtonsLayout.setHorizontalGroup(
            plButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plButtonsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 242, Short.MAX_VALUE)
                .addComponent(btClose, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        plButtonsLayout.setVerticalGroup(
            plButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, plButtonsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(plButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        getContentPane().add(plButtons, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    @Action
    public void closeDialog() {
        updaterTimer.stop();
        this.dispose();
    }

    @Action
    public Task doRefresh() {
        return new DoRefreshTask(org.jdesktop.application.Application.getInstance(net.sf.dvstar.transmission.TransmissionApp.class));
    }

    private class DoRefreshTask extends org.jdesktop.application.Task<Object, Void> {

        DoRefreshTask(org.jdesktop.application.Application app) {
            // Runs on the EDT.  Copy GUI state that
            // doInBackground() depends on from parameters
            // to DoRefreshTask fields, here.
            super(app);
            btRefresh.setEnabled(false);
            fillFormData();
            btRefresh.setEnabled(true);
        }

        @Override
        protected Object doInBackground() {
            // Your Task's code here.  This method runs
            // on a background thread, so don't reference
            // the Swing GUI from here.
            return null;  // return your result
        }

        @Override
        protected void succeeded(Object result) {
            // Runs on the EDT.  Update the GUI based on
            // the result computed by doInBackground().
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btClose;
    private javax.swing.JButton btRefresh;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel lbActiveTorrentCount;
    private javax.swing.JLabel lbActivityC;
    private javax.swing.JLabel lbActivityT;
    private javax.swing.JLabel lbAddedFileC;
    private javax.swing.JLabel lbAddedFileT;
    private javax.swing.JLabel lbDowloadC;
    private javax.swing.JLabel lbDowloadT;
    private javax.swing.JLabel lbSessionC;
    private javax.swing.JLabel lbSessionT;
    private javax.swing.JLabel lbUploadC;
    private javax.swing.JLabel lbUploadT;
    private javax.swing.JPanel plButtons;
    private javax.swing.JPanel plCurrent;
    private javax.swing.JPanel plInfo;
    private javax.swing.JPanel plTop;
    private javax.swing.JPanel plTotal;
    private javax.swing.JTextField tfServerVersion;
    // End of variables declaration//GEN-END:variables
}
