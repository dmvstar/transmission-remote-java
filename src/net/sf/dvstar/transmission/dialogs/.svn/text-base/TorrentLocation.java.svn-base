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
 * TorrentLocation.java
 * Dialog for determine torrent location
 * Created on 28 серп 2009, 14:40:01
 */
package net.sf.dvstar.transmission.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Logger;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;
import net.sf.dvstar.transmission.protocol.transmission.ProtocolConstants;
import net.sf.dvstar.transmission.protocol.transmission.Torrent;
import net.sf.dvstar.transmission.protocol.TransmissionManager;
import net.sf.dvstar.transmission.utils.ConfigStorage;
import net.sf.dvstar.transmission.utils.LocalSettiingsConstants;
import net.sf.dvstar.transmission.utils.Tools;
import org.klomp.snark.MetaInfo;

/**
 *
 * @author dstarzhynskyi
 */
public class TorrentLocation extends javax.swing.JDialog implements ProtocolConstants, LocalSettiingsConstants {

    public static final int MODE_LOCA = 1;
    public static final int MODE_MOVE = 2;
    public static final int MODE_ADDT = 3;
    private ConfigStorage config;
    private String srcDir;
    private String dstDirs;
    private String dstDir;
    private int mode = MODE_LOCA;
    private MetaInfo fileMeta = null;
    private Logger loggerProvider;
    private Torrent torrent;
    private boolean pausedTorrent = true;
    private boolean moveData = false;
    private Vector tableTitle = null;
    private Vector tableData = null;
    private List torrentFileWantedList = null;
    private List torrentFileUnWantedList = null;
    private List torrentFilePriority = null;

    /** Creates new form TorrentMoveData */
    public TorrentLocation(java.awt.Frame parent,
            boolean modal,
            int mode,
            Torrent torrent,
            MetaInfo fileMeta,
            TransmissionManager manager) {
        super(parent, modal);
        this.loggerProvider = manager.getGlobalLogger();
        this.config = manager.getGlobalConfigStorage();
        this.torrent = torrent;
        this.mode = mode;
        this.fileMeta = fileMeta;
        initComponents();
        this.getRootPane().setDefaultButton(btOk);
        
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(net.sf.dvstar.transmission.TransmissionApp.class).getContext().getResourceMap(TorrentLocation.class);
        String title = "...";

        switch (mode) {
            case MODE_ADDT: {
                chMove.setEnabled(false);
                chMove.setVisible(false);
                title = resourceMap.getString("TorrentLocation.title.loca.text");
                if (fileMeta != null) {
                    taTorrentName.setText(fileMeta.getName());
                }
            }
            break;
            case MODE_LOCA: {
                chMove.setEnabled(false);
                chMove.setVisible(false);
                title = resourceMap.getString("TorrentLocation.title.loca.text");
                if (fileMeta != null) {
                    taTorrentName.setText(fileMeta.getName());
                }
            }
            break;
            case MODE_MOVE: {
                chMove.setEnabled(true);
                chPaused.setEnabled(false);
                chPaused.setVisible(false);
                title = resourceMap.getString("TorrentLocation.title.move.text");
            }
            break;
        }

        this.setTitle(title);

        cbDestination.addActionListener(new MyActionListener());

        //List values = new Vector();

        if (torrent != null) {
            srcDir = torrent.getDownloadDir();
            taTorrentName.setText(torrent.getTorrentName());
            tfSource.setText(srcDir);
        } else {
            tfSource.setVisible(false);
            lbSource.setVisible(false);
        }

        String dlist = config.getProperty(CONF_CLI_USED_DIRS);
        if (dlist != null) {
            dstDirs = dlist;
            cbDestination.removeAllItems();

            StringTokenizer stoken = new StringTokenizer(dlist, ",");
            if (stoken.countTokens() > 0) {
                ArrayList dirList = new ArrayList();
                while (stoken.hasMoreElements()) {
                    //values.add(stoken.nextToken());
                    String itemList = stoken.nextToken();
                    if (!dirList.contains(itemList) && itemList.startsWith("/")) {
                        dirList.add(itemList);
                    }
                }
                Collections.sort( dirList );
                for(int i=0;i<dirList.size();i++){
                    cbDestination.addItem(dirList.get(i));
                }
            } else {
                //values.add(dlist);
                cbDestination.addItem(dlist);
            }

        }

        String lastd = config.getProperty(CONF_CLI_LAST_USED_DIR, ".", true);
        cbDestination.setSelectedItem(lastd);

        if (fileMeta != null) {
            List fileList = Tools.makeMetaInfoFiles(fileMeta);
            tableData = new Vector();
            List item;
            for (int i = 0; i < fileList.size(); i++) {
                if (i == 0) {
                    tfPath.setText(fileList.get(i).toString());
                } else {
                    item = new Vector();
                    item.add(fileList.get(i));
                    item.add(new Boolean(true));
                    tableData.add(item);
                }
            }

            tableTitle = new Vector();
            tableTitle.add("File");
            tableTitle.add("Use");

            tableFileList.setModel(new FileListTableModel(tableTitle));
            /*
            {

            Class[] types = new Class[]{
            java.lang.String.class, java.lang.Boolean.class
            };

            boolean[] canEdit = new boolean[]{
            false, true
            };

            @Override
            public Class getColumnClass(int columnIndex) {
            return types[columnIndex];
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit[columnIndex];
            }
            });
             */

        }


        addWindowListener(new java.awt.event.WindowAdapter() {

            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                dstDir = null;
            }
        });


//        MyComboBoxModel model = new MyComboBoxModel(values);
//        cbDestination.setModel(model);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        lbSource = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        tfSource = new javax.swing.JTextField();
        cbDestination = new javax.swing.JComboBox();
        chMove = new javax.swing.JCheckBox();
        stFileList = new javax.swing.JScrollPane();
        tableFileList = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        tfPath = new javax.swing.JTextField();
        chPaused = new javax.swing.JCheckBox();
        btDeselect = new javax.swing.JButton();
        btSelect = new javax.swing.JButton();
        btInvert = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        btOk = new javax.swing.JButton();
        btClose = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        taTorrentName = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLocationByPlatform(true);
        setMinimumSize(new java.awt.Dimension(730, 520));
        setModal(true);
        setName("Form"); // NOI18N

        jPanel2.setMinimumSize(new java.awt.Dimension(400, 180));
        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setPreferredSize(new java.awt.Dimension(320, 137));

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(net.sf.dvstar.transmission.TransmissionApp.class).getContext().getResourceMap(TorrentLocation.class);
        lbSource.setText(resourceMap.getString("lbSource.text")); // NOI18N
        lbSource.setName("lbSource"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        tfSource.setText(resourceMap.getString("tfSource.text")); // NOI18N
        tfSource.setName("tfSource"); // NOI18N

        cbDestination.setEditable(true);
        cbDestination.setName("cbDestination"); // NOI18N

        chMove.setSelected(true);
        chMove.setText(resourceMap.getString("chMove.text")); // NOI18N
        chMove.setName("chMove"); // NOI18N

        stFileList.setName("stFileList"); // NOI18N
        stFileList.setPreferredSize(new java.awt.Dimension(400, 220));

        tableFileList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "File", "Use"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableFileList.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        tableFileList.setName("tableFileList"); // NOI18N
        stFileList.setViewportView(tableFileList);
        tableFileList.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tableFileList.columnModel.title0")); // NOI18N
        tableFileList.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tableFileList.columnModel.title1")); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        tfPath.setColumns(10);
        tfPath.setEditable(false);
        tfPath.setText(resourceMap.getString("tfPath.text")); // NOI18N
        tfPath.setToolTipText(resourceMap.getString("tfPath.toolTipText")); // NOI18N
        tfPath.setBorder(null);
        tfPath.setMinimumSize(new java.awt.Dimension(30, 15));
        tfPath.setName("tfPath"); // NOI18N

        chPaused.setText(resourceMap.getString("chPaused.text")); // NOI18N
        chPaused.setName("chPaused"); // NOI18N

        btDeselect.setIcon(resourceMap.getIcon("btDeselect.icon")); // NOI18N
        btDeselect.setText(resourceMap.getString("btDeselect.text")); // NOI18N
        btDeselect.setToolTipText(resourceMap.getString("btDeselect.toolTipText")); // NOI18N
        btDeselect.setMaximumSize(new java.awt.Dimension(200, 200));
        btDeselect.setMinimumSize(new java.awt.Dimension(110, 25));
        btDeselect.setName("btDeselect"); // NOI18N
        btDeselect.setPreferredSize(new java.awt.Dimension(130, 26));
        btDeselect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btDeselectActionPerformed(evt);
            }
        });

        btSelect.setIcon(resourceMap.getIcon("btSelect.icon")); // NOI18N
        btSelect.setText(resourceMap.getString("btSelect.text")); // NOI18N
        btSelect.setToolTipText(resourceMap.getString("btSelect.toolTipText")); // NOI18N
        btSelect.setMaximumSize(new java.awt.Dimension(200, 200));
        btSelect.setMinimumSize(new java.awt.Dimension(110, 25));
        btSelect.setName("btSelect"); // NOI18N
        btSelect.setPreferredSize(new java.awt.Dimension(130, 26));
        btSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSelectActionPerformed(evt);
            }
        });

        btInvert.setIcon(resourceMap.getIcon("btInvert.icon")); // NOI18N
        btInvert.setText(resourceMap.getString("btInvert.text")); // NOI18N
        btInvert.setToolTipText(resourceMap.getString("btInvert.toolTipText")); // NOI18N
        btInvert.setMaximumSize(new java.awt.Dimension(200, 200));
        btInvert.setName("btInvert"); // NOI18N
        btInvert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btInvertActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tfPath, javax.swing.GroupLayout.DEFAULT_SIZE, 822, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chMove, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chPaused)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 226, Short.MAX_VALUE)
                                        .addComponent(btInvert, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btDeselect, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btSelect, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                        .addComponent(lbSource, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(1, 1, 1)
                                        .addComponent(tfSource, javax.swing.GroupLayout.DEFAULT_SIZE, 710, Short.MAX_VALUE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(2, 2, 2)
                                        .addComponent(cbDestination, 0, 710, Short.MAX_VALUE))
                                    .addComponent(stFileList, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 808, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGap(14, 14, 14))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(lbSource))
                    .addComponent(tfSource, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbDestination, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jLabel2)))
                .addGap(2, 2, 2)
                .addComponent(chMove)
                .addGap(9, 9, 9)
                .addComponent(chPaused)
                .addGap(9, 9, 9)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btDeselect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btInvert, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(stFileList, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(320, 48));

        btOk.setIcon(resourceMap.getIcon("btOk.icon")); // NOI18N
        btOk.setText(resourceMap.getString("btOk.text")); // NOI18N
        btOk.setToolTipText(resourceMap.getString("btOk.toolTipText")); // NOI18N
        btOk.setName("btOk"); // NOI18N
        btOk.setPreferredSize(new java.awt.Dimension(90, 26));
        btOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btOkActionPerformed(evt);
            }
        });

        btClose.setIcon(resourceMap.getIcon("btClose.icon")); // NOI18N
        btClose.setText(resourceMap.getString("btClose.text")); // NOI18N
        btClose.setName("btClose"); // NOI18N
        btClose.setPreferredSize(new java.awt.Dimension(90, 26));
        btClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCloseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btClose, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 546, Short.MAX_VALUE)
                .addComponent(btOk, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btClose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btOk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.SOUTH);

        jPanel3.setName("jPanel3"); // NOI18N

        jLabel1.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        taTorrentName.setColumns(20);
        taTorrentName.setFont(resourceMap.getFont("taTorrentName.font")); // NOI18N
        taTorrentName.setForeground(resourceMap.getColor("taTorrentName.foreground")); // NOI18N
        taTorrentName.setLineWrap(true);
        taTorrentName.setRows(5);
        taTorrentName.setText(resourceMap.getString("taTorrentName.text")); // NOI18N
        taTorrentName.setToolTipText(resourceMap.getString("taTorrentName.toolTipText")); // NOI18N
        taTorrentName.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("taTorrentName.border.lineColor"))); // NOI18N
        taTorrentName.setDisabledTextColor(resourceMap.getColor("taTorrentName.disabledTextColor")); // NOI18N
        taTorrentName.setEnabled(false);
        taTorrentName.setFocusable(false);
        taTorrentName.setName("taTorrentName"); // NOI18N
        taTorrentName.setOpaque(false);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 810, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(taTorrentName)
                    .addContainerGap()))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(73, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addGap(36, 36, 36)
                    .addComponent(taTorrentName, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(17, Short.MAX_VALUE)))
        );

        getContentPane().add(jPanel3, java.awt.BorderLayout.NORTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCloseActionPerformed
        dstDir = null;
        torrentFileWantedList = null;
        torrentFileUnWantedList = null;
        //System. out.println( this.getSize().toString() );
        this.dispose();
    }//GEN-LAST:event_btCloseActionPerformed

    private void btOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btOkActionPerformed
        if (cbDestination.getItemCount() > 0) {
            dstDirs = "";
            for (int i = 0; i < cbDestination.getItemCount(); i++) {
                String delim = ",";
                if (i == cbDestination.getItemCount() - 1) {
                    delim = "";
                }
                dstDirs += cbDestination.getItemAt(i) + delim;
            }
        }

        dstDir = cbDestination.getSelectedItem().toString();
        moveData = chMove.isSelected();
        pausedTorrent = chPaused.isSelected();

        boolean fullList = true;
        torrentFileWantedList = new ArrayList();
        torrentFileUnWantedList = new ArrayList();
        torrentFilePriority = new ArrayList();
        if (tableData != null) {
            for (int i = 0; i < tableData.size(); i++) {
                Vector item = (Vector) tableData.get(i);
                String fileName = (String) item.get(0);
                Boolean fileDown = (Boolean) item.get(1);
                if (fileDown.booleanValue()) {
                    torrentFileWantedList.add(i);///*fileName*/);
//                torrentFileWantedList.add(fileName);
//                torrentFileUnWantedList.add("0");//+(i+1)/*fileName*/);
                } else {
//                torrentFileWantedList.add("0");//+(i+1)/*fileName*/);
                    torrentFileUnWantedList.add(i);//+(i+1)/*fileName*/);
                    fullList = false;
                }
                torrentFilePriority.add(i);
            }
        }
        if (fullList) {
            torrentFileWantedList = null;
            torrentFileUnWantedList = null;
            torrentFilePriority = null;
        }

        config.setProperty(CONF_CLI_USED_DIRS, dstDirs);
        config.setProperty(CONF_CLI_LAST_USED_DIR, dstDir);
        config.saveConfig();
        this.dispose();
    }//GEN-LAST:event_btOkActionPerformed

    private void btSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSelectActionPerformed
        for (int i = 0; i < tableData.size(); i++) {
            Vector item = (Vector) tableData.get(i);
            item.set(1, new Boolean(true));
            tableData.set(i, item);
        }
        ((AbstractTableModel) tableFileList.getModel()).fireTableDataChanged();
    }//GEN-LAST:event_btSelectActionPerformed

    private void btDeselectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btDeselectActionPerformed
        for (int i = 0; i < tableData.size(); i++) {
            Vector item = (Vector) tableData.get(i);
            item.set(1, new Boolean(false));
            tableData.set(i, item);
        }
        ((AbstractTableModel) tableFileList.getModel()).fireTableDataChanged();
        //tableFileList.getModel().setValueAt(item, i, 1);
    }//GEN-LAST:event_btDeselectActionPerformed

    private void btInvertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btInvertActionPerformed
        for (int i = 0; i < tableData.size(); i++) {
            Vector item = (Vector) tableData.get(i);
            item.set(1, new Boolean(!((Boolean) item.get(1))).booleanValue());
        }
        ((AbstractTableModel) tableFileList.getModel()).fireTableDataChanged();
    }//GEN-LAST:event_btInvertActionPerformed

    public String getDstDir() {
        return dstDir;
    }

    public boolean isMoveData() {
        return chMove.isSelected();
    }

    public boolean isPausedTorrent() {
        return pausedTorrent;
    }

    public List getFileWantedList() {
        return torrentFileWantedList;
    }

    public List getFileUnWantedList() {
        return torrentFileUnWantedList;
    }

    public List getFilePriority() {
        return torrentFilePriority;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btClose;
    private javax.swing.JButton btDeselect;
    private javax.swing.JButton btInvert;
    private javax.swing.JButton btOk;
    private javax.swing.JButton btSelect;
    private javax.swing.JComboBox cbDestination;
    private javax.swing.JCheckBox chMove;
    private javax.swing.JCheckBox chPaused;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel lbSource;
    private javax.swing.JScrollPane stFileList;
    private javax.swing.JTextArea taTorrentName;
    private javax.swing.JTable tableFileList;
    private javax.swing.JTextField tfPath;
    private javax.swing.JTextField tfSource;
    // End of variables declaration//GEN-END:variables

    class MyComboBoxModel implements ComboBoxModel {

        private List values;
        private Object selected;

        public MyComboBoxModel(List values) {
            this.values = values;
        }

        public List getValues() {
            return values;
        }

        public void setValues(List values) {
            this.values = values;
        }

        @Override
        public void setSelectedItem(Object anItem) {
            selected = anItem;
        }

        @Override
        public Object getSelectedItem() {
            return selected;
        }

        @Override
        public int getSize() {
            return values.size();
        }

        @Override
        public Object getElementAt(int index) {
            return values.get(index);
        }

        @Override
        public void addListDataListener(ListDataListener l) {
        }

        @Override
        public void removeListDataListener(ListDataListener l) {
        }
    }

    class FileListTableModel extends AbstractTableModel {

        Class[] types = new Class[]{
            java.lang.String.class, java.lang.Boolean.class
        };
        boolean[] canEdit = new boolean[]{
            false, true
        };
        private List tableTitle;

        private FileListTableModel(List tableTitle) {
            this.tableTitle = tableTitle;
        }

        @Override
        public Class getColumnClass(int columnIndex) {
            return types[columnIndex];
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit[columnIndex];
        }

        @Override
        public String getColumnName(int column) {
            Object id = null;

            id = tableTitle.get(column);

            return (id == null) ? super.getColumnName(column)
                    : id.toString();
        }

        @Override
        public int getRowCount() {
            int ret = -1;
            if (tableData != null) {
                ret = tableData.size();
            }
            return ret;
        }

        @Override
        public int getColumnCount() {
            int ret = -1;
            if (tableTitle != null) {
                ret = tableTitle.size();
            }
            return ret;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Object ret = null;
            if (tableData != null) {
                if (rowIndex < getRowCount() && columnIndex < getColumnCount()) {
                    Vector item = (Vector) tableData.get(rowIndex);
                    ret = item.get(columnIndex);
                }
            }
            return ret;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (tableData != null) {
                if (rowIndex < getRowCount() && columnIndex < getColumnCount()) {
                    Vector item = (Vector) tableData.get(rowIndex);
                    item.set(columnIndex, aValue);
                }
            }
        }
    }

    class MyActionListener implements ActionListener, ItemListener {

        Object oldItem;

        @Override
        public void actionPerformed(ActionEvent evt) {
            JComboBox cb = (JComboBox) evt.getSource();
            Object newItem = cb.getSelectedItem();

            dstDir = cb.getSelectedItem().toString();


            JComboBox anCombo = (JComboBox) evt.getSource();
            DefaultComboBoxModel comboModel = (DefaultComboBoxModel) anCombo.getModel();
            int index = comboModel.getIndexOf(anCombo.getSelectedItem());
            if (index == -1) {
                comboModel.addElement(anCombo.getSelectedItem());
            }//else already added

            /*
            System.out.println("+++++++"+newItem+"="+oldItem);

            boolean same = newItem.equals(oldItem);
            oldItem = newItem;


            if (!same) {
            if(((MyComboBoxModel) cb.getModel()).getValues().indexOf(newItem)<0){
            ((MyComboBoxModel) cb.getModel()).getValues().add(newItem);
            System.out.println("======="+cb.getItemCount());

            }
            }

            if ("comboBoxEdited".equals(evt.getActionCommand())) {
            // User has typed in a string; only possible with an editable combobox
            System.out.println("======="+evt);
            } else if ("comboBoxChanged".equals(evt.getActionCommand())) {
            }
             */
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
        }
    }
}
