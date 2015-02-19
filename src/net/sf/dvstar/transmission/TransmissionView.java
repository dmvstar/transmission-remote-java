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
 * TransmissionView.java
 * Main view control for project
 * Created on 28 серп 2009, 14:40:01
 */
package net.sf.dvstar.transmission;

import java.awt.event.ComponentEvent;
import javax.swing.JButton;
import net.sf.dvstar.transmission.dialogs.*;
import net.sf.dvstar.transmission.comps.PiecesGraph;
import net.sf.dvstar.transmission.protocol.transmission.TorrentListRowSorter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Desktop;
import net.sf.dvstar.transmission.dialogs.SessionStatistic;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import net.sf.dvstar.transmission.protocol.TorrentsCommonException;
import net.sf.dvstar.transmission.protocol.transmission.TransmissionWebClient;
import net.sf.dvstar.transmission.protocol.transmission.Requests;
import java.io.IOException;
import java.net.UnknownHostException;
import org.apache.http.HttpException;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import net.sf.dvstar.transmission.comps.TorrentFilter;
import net.sf.dvstar.transmission.comps.TorrentFilter.TorrentFilterDesc;
import net.sf.dvstar.transmission.dialogs.ExtFilterDialog;
import net.sf.dvstar.transmission.dialogs.TorrentMetaInfo;
import net.sf.dvstar.transmission.dialogs.TorrentLocation;
import net.sf.dvstar.transmission.dialogs.TorrentProperties;
import net.sf.dvstar.transmission.dialogs.URLMouseListener;
import net.sf.dvstar.transmission.progressbar.ProgressBarRenderer;
import net.sf.dvstar.transmission.protocol.TorrentConstants.DestTorrent;
import net.sf.dvstar.transmission.protocol.TorrentFieldsInterface.FilesFieldInterface;
import net.sf.dvstar.transmission.protocol.TorrentFieldsInterface.PeersFieldInterface;
import net.sf.dvstar.transmission.protocol.TorrentInterface.TorrentFileDescInterface;
import net.sf.dvstar.transmission.protocol.transmission.JSONMapModel.ColumnDescriptor;
import net.sf.dvstar.transmission.protocol.transmission.JSONMapModel.ColumnsDescriptor;
import net.sf.dvstar.transmission.protocol.transmission.JSONMapModel.JSONMapModelFiles;
import net.sf.dvstar.transmission.protocol.transmission.JSONMapModel.JSONMapModelPeers;
import net.sf.dvstar.transmission.protocol.transmission.TorrentsTableModel;
import net.sf.dvstar.transmission.protocol.transmission.ProtocolConstants;
import net.sf.dvstar.transmission.protocol.transmission.Torrent;
import net.sf.dvstar.transmission.protocol.transmission.TransmissionDescriptor;
import net.sf.dvstar.transmission.protocol.TransmissionManager;
import net.sf.dvstar.transmission.protocol.transmission.TorrentsTableBooleanRenderer;
import net.sf.dvstar.transmission.protocol.transmission.TorrentsTableCustomRenderer;
import net.sf.dvstar.transmission.utils.ConfigStorage;
import net.sf.dvstar.transmission.utils.Html2Text;
import net.sf.dvstar.transmission.utils.LocalSettiingsConstants;
import net.sf.dvstar.transmission.utils.TimeSpan;
import net.sf.dvstar.transmission.utils.Tools;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jvnet.flamingo.common.*;
import org.jvnet.flamingo.common.icon.*;
import org.jvnet.flamingo.common.popup.JPopupPanel;
import org.jvnet.flamingo.common.popup.PopupPanelCallback;
import org.jvnet.flamingo.common.popup.JCommandPopupMenu;
import org.jvnet.flamingo.common.JCommandButton.CommandButtonKind;
import org.jvnet.flamingo.common.ui.CommandButtonLayoutManagerTile;
import org.jvnet.flamingo.utils.ArrowResizableIcon;
import org.klomp.snark.MetaInfo;
import org.klomp.snark.bencode.BDecoder;
import org.openide.util.Exceptions;
//import org.openide.awt.DropDownButtonFactory;

/**
 * The application's main frame.
 */
public class TransmissionView extends FrameView implements ProtocolConstants, TransmissionManager, LocalSettiingsConstants, ComponentListener {

    private SingleFrameApplication singleFrameApplication = null;
    private boolean connectedServer = false;
    private TransmissionWebClient webClient = null;
    private JPopupMenu tblTorrentListPopupMenu = new JPopupMenu("Torrents Menu");
    private JPopupMenu tblFileListPopupMenu = new JPopupMenu("FileList Menu");
    private JPopupMenu mainToolBarPopupMenu = new JPopupMenu("ToolBar Menu");
    private final PiecesGraph piecesGraph;
    private TorrentsTableModel modelTorrentsList = null;
    private TorrentsSelectionListener torrentsSelectionListener = null;
    private boolean enableTraceOut = false;
    private boolean lockValueChanged = false;
    private int rowSelectionOld = -1, rowSelectionNew = -1;
    private TransmissionDescriptor transmissionDaemonDescriptor;
    private Locale defaultLocale = Locale.ENGLISH;
    private TransmissionView transmissionView = null;
    private ResourceMap globalResourceMap = null;
    private ConfigStorage globalConfigStorage;
    private Logger globalLogger;
    private URLMouseListener urlLMouseListener;
    private TorrentsListPopupCmd defaultToolBarIconSize;
    private TorrentFilterDesc torrentFilterDesc = null;

    /**
     * Main class for visual application
     * @param app Parent application framework
     */
    public TransmissionView(SingleFrameApplication app) {
        super(app);

        this.singleFrameApplication = app;
        this.transmissionView = this;

        initGlobals();

        initLogger();

        initComponents();

        initLocale();

        initTimers();

        initResources();

        ResourceMap resourceMap = getResourceMap();
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");

        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        piecesGraph = new PiecesGraph();
        plPieces.add(piecesGraph, BorderLayout.CENTER);

        MainToolBarPopupListener mainToolBarPopupListener = new MainToolBarPopupListener();
        mainToolBar.addMouseListener(mainToolBarPopupListener);

        modelTorrentsList = new TorrentsTableModel(this);

        TorrentsTableModel.setPreferredColumnWidths(tblTorrentList);

        setRefButtonsState(connectedServer);
        setAllButtonsState(connectedServer);

        tblTorrentList.setModel(modelTorrentsList);

        /**
         * Set sorter to table
         */
        //!!tblTorrentList.setAutoCreateRowSorter(true);
        TorrentListRowSorter rorrentListRowSorter = new TorrentListRowSorter((TorrentsTableModel) tblTorrentList.getModel());
        tblTorrentList.setRowSorter(rorrentListRowSorter);


        TorrentListPopupListener torrentListPopupListener = new TorrentListPopupListener();
        tblTorrentList.addMouseListener(torrentListPopupListener);
        tblTorrentList.getTableHeader().addMouseListener(torrentListPopupListener);
        tblTorrentList.setRowSelectionAllowed(true);
        tblTorrentList.tableChanged(new TableModelEvent(modelTorrentsList));

        tblTorrentList.getColumnModel().getColumn(3).setCellRenderer(new ProgressBarRenderer());

        tblTorrentFiles.setAutoCreateRowSorter(true);
        tblTorrentPeers.setAutoCreateRowSorter(true);

        FileListPopupListener fileListPopupListener = new FileListPopupListener();
        tblFileListPopupMenu.setEnabled(false);
        tblTorrentFiles.addMouseListener(fileListPopupListener);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            @Override
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });

        //setAdditionalButtons();

        //Whenever filterText changes, invoke setTorrentListFilter.
        tfFindItem.getDocument().addDocumentListener(
                new DocumentListener() {

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        setTorrentListFilter();
                    }

                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        setTorrentListFilter();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        setTorrentListFilter();
                    }
                });

        checkNavigator();
        updateInfoBox(-1);
        updateFilesAndPeersBox(-1);
        btConnect.grabFocus();

        if (globalConfigStorage.parseBooleanProperty(globalConfigStorage.getProperty(CONF_CLI_AUTOCONNECT, false))) {

            ApplicationContext ctx = getApplication().getContext();
            Task task = doConnect();
            ctx.getTaskService().execute(task);

        }

        btConnect.grabFocus();

        initSpeedoMeter();

    }

    /**
     * Update the row filter regular expression from the expression in
     * the text box and add advanced filter.
     */
    private void setTorrentListFilter() {
        RowFilter<TorrentsTableModel, Object> rowFilter = null;
        java.util.List<RowFilter<TorrentsTableModel, Object>> filters = new ArrayList<RowFilter<TorrentsTableModel, Object>>();
//        java.util.List<RowFilter> filters = new ArrayList<RowFilter<TorrentsTableModel, Object>>();

        //If current expression doesn't parse, don't update.
        try {
            String filterCause = tfFindItem.getText();
            if (!cbRegistry.isSelected()) {
                filterCause = makeRegexCaseString(tfFindItem.getText());
            }
            rowFilter = RowFilter.regexFilter(filterCause, 1);

            tfFindItem.setToolTipText(filterCause);

            filters.add(rowFilter);
        } catch (java.util.regex.PatternSyntaxException ex) {
            return;
        }

        // use combo box filter by status
        if (cbFilterStatus.getSelectedIndex() > 0) {

            /*
            0 All
            1 Download TR_STATUS_DOWNLOAD
            2 Stopped  TR_STATUS_STOPPED
            3 Seed     TR_STATUS_SEED
            4 Check    TR_STATUS_CHECK || TR_STATUS_CHECK_WAIT
            5 Check    TR_STATUS_CHECK || TR_STATUS_CHECK_WAIT
            6 Error
             */

            String filter = "";
            switch (cbFilterStatus.getSelectedIndex()) {
                case 1:
                    filter = "" + globalResourceMap.getString(STATUS_DOWNLOAD_DESC);//TRS_STATUS_DOWNLOAD;
                    break;
                case 2:
                    filter = "" + globalResourceMap.getString(STATUS_PAUSED_DESC);//TRS_STATUS_PAUSED;
                    break;
                case 3:
                    filter = "" + globalResourceMap.getString(STATUS_SEED_DESC);//TRS_STATUS_SEED;
                    break;
                case 4:
                    filter = "" + globalResourceMap.getString(STATUS_CHECK_DESC);//TRS_STATUS_CHECK;
                    break;
                case 5:
                    filter = "" + globalResourceMap.getString(STATUS_CHECK_WAIT_DESC);//TRS_STATUS_CHECK;
                    break;
                case 6:
                    break;
            }


            rowFilter = RowFilter.regexFilter(filter, 4);
            filters.add(rowFilter);

        }

        /**
         * add filter based on torrent meta information
         */
        //        TorrentFilter<TorrentsTableModel, Object> tfilter = new TorrentFilter( torrentFilterDesc );
        if (torrentFilterDesc != null && torrentFilterDesc.getCountItems() > 0) {
            TorrentFilter tfilter = new TorrentFilter(torrentFilterDesc);
            filters.add(tfilter);
        }

        TableRowSorter sorter = (TableRowSorter) tblTorrentList.getRowSorter();

        RowFilter mrowFilter = RowFilter.andFilter(filters);
        if (sorter != null) {
            /**
             * @todo change is possible old selection to new
             * @todo restore sort order to new selection - solved
             */
            //selectedTorrentRows = null;
            lockValueChanged = true;
//            List sortKeys = sorter.getSortKeys();
            sorter.setRowFilter(mrowFilter);
//            sorter.setSortKeys(sortKeys);
            lockValueChanged = false;
            int rcs = tblTorrentList.getRowSorter().getViewRowCount();
            int rca = tblTorrentList.getRowSorter().getModelRowCount();
            lbFindInfo.setText(String.format("%d of %d", rcs, rca));
            lbFindInfo.invalidate();
        }
    }

    private void initGlobals() {

        System.setProperty("java.net.useSystemProxies", "false");

        //globalActionMap = org.jdesktop.application.Application.getInstance(net.sf.dvstar.transmission.TransmissionApp.class).getContext().getActionMap(TransmissionView.class, this);

        globalConfigStorage = new ConfigStorage();
        if (!globalConfigStorage.loadConfig()) {
            ClientConfig configDialog = new ClientConfig(this, true, false);
            configDialog.setLocationRelativeTo(this.getFrame());
            TransmissionApp.getApplication().show(configDialog);
        }

        lastOpenDir = new File(globalConfigStorage.getProperty("last-open-dir"));

        Locale configLocale = Tools.parseConfigLocale(globalConfigStorage.getProperty(CONF_CLI_LOCALE, "en", true));
        setDefaultLocale(configLocale);
        tracePrint(true, "Locale is " + globalConfigStorage.getProperty(CONF_CLI_LOCALE, "en", true));

        globalResourceMap = Application.getInstance(net.sf.dvstar.transmission.TransmissionApp.class).getContext().getResourceMap(TransmissionView.class);

        this.getFrame().setIconImage(globalResourceMap.getImageIcon("MainFrame.icon").getImage());

        String[] names = null;

        String namesList = globalResourceMap.getString("tblTorrentList.miTorrentPopup.items");

        if (namesList != null) {
            names = Tools.getStringArray(namesList, ",");
        }

        String appTitle = globalResourceMap.getString("Application.title") + " "
                + globalResourceMap.getString("Application.version");
        getFrame().setTitle(appTitle);

        PopupCommandTorrentDescriptor mainToolBarCommandsPopup[] = {
            new PopupCommandTorrentDescriptor(
            TorrentsListPopupCmd.TLIST_POPUP_CMD_ICON_32x32,
            globalResourceMap.getIcon("miTorrentProperties.icon"),
            "Icon 32x32",
            BUNDLE_IDENT_MENU_TORRENT_START),
            new PopupCommandTorrentDescriptor(
            TorrentsListPopupCmd.TLIST_POPUP_CMD_ICON_24x24,
            globalResourceMap.getIcon("miTorrentProperties.icon"),
            "Icon 24x24",
            BUNDLE_IDENT_MENU_TORRENT_START),
            new PopupCommandTorrentDescriptor(
            TorrentsListPopupCmd.TLIST_POPUP_CMD_ICON_16x16,
            globalResourceMap.getIcon("miTorrentProperties.icon"),
            "Icon 16x16",//"Start torrent(s)",
            BUNDLE_IDENT_MENU_TORRENT_START),};

        for (int i = 0; i < mainToolBarCommandsPopup.length; i++) {
            JMenuItem item;
            if (mainToolBarCommandsPopup[i].icon != null) {
                item = new JMenuItem(mainToolBarCommandsPopup[i].nameText, mainToolBarCommandsPopup[i].icon);
            } else {
                item = new JMenuItem(mainToolBarCommandsPopup[i].nameText);
            }
            TorrentsMenuItemListener menuItemListener = new TorrentsMenuItemListener(mainToolBarCommandsPopup[i].cmd);
            item.setName(mainToolBarCommandsPopup[i].nameText);
            item.addActionListener(menuItemListener);
            mainToolBarPopupMenu.add(item);
        }

        PopupCommandTorrentDescriptor commandsPopupTorrentUp[] = {
            new PopupCommandTorrentDescriptor(TorrentsListPopupCmd.TLIST_POPUP_CMD_LIMIT_DOWN,
            null, "5 kb/s"),
            new PopupCommandTorrentDescriptor(TorrentsListPopupCmd.TLIST_POPUP_CMD_LIMIT_DOWN,
            null, "15 kb/s"),
            new PopupCommandTorrentDescriptor(TorrentsListPopupCmd.TLIST_POPUP_CMD_LIMIT_DOWN,
            null, "25 kb/s"),
            new PopupCommandTorrentDescriptor(TorrentsListPopupCmd.TLIST_POPUP_CMD_LIMIT_DOWN,
            null, "35 kb/s"),
            new PopupCommandTorrentDescriptor(TorrentsListPopupCmd.TLIST_POPUP_CMD_LIMIT_DOWN,
            null, "45 kb/s"),
            new PopupCommandTorrentDescriptor(TorrentsListPopupCmd.TLIST_POPUP_CMD_LIMIT_DOWN,
            null, "55 kb/s"),
            new PopupCommandTorrentDescriptor(TorrentsListPopupCmd.TLIST_POPUP_CMD_LIMIT_DOWN,
            null, "65 kb/s"),
            new PopupCommandTorrentDescriptor(TorrentsListPopupCmd.TLIST_POPUP_CMD_LIMIT_DOWN,
            null, "105 kb/s")
        };

        PopupCommandTorrentDescriptor commandsPopupTorrentDn[] = {
            new PopupCommandTorrentDescriptor(TorrentsListPopupCmd.TLIST_POPUP_CMD_LIMIT_DOWN,
            null, "5 kb/s"),
            new PopupCommandTorrentDescriptor(TorrentsListPopupCmd.TLIST_POPUP_CMD_LIMIT_DOWN,
            null, "15 kb/s"),
            new PopupCommandTorrentDescriptor(TorrentsListPopupCmd.TLIST_POPUP_CMD_LIMIT_DOWN,
            null, "25 kb/s"),
            new PopupCommandTorrentDescriptor(TorrentsListPopupCmd.TLIST_POPUP_CMD_LIMIT_DOWN,
            null, "35 kb/s"),
            new PopupCommandTorrentDescriptor(TorrentsListPopupCmd.TLIST_POPUP_CMD_LIMIT_DOWN,
            null, "45 kb/s"),
            new PopupCommandTorrentDescriptor(TorrentsListPopupCmd.TLIST_POPUP_CMD_LIMIT_DOWN,
            null, "55 kb/s"),
            new PopupCommandTorrentDescriptor(TorrentsListPopupCmd.TLIST_POPUP_CMD_LIMIT_DOWN,
            null, "65 kb/s"),
            new PopupCommandTorrentDescriptor(TorrentsListPopupCmd.TLIST_POPUP_CMD_LIMIT_DOWN,
            null, "105 kb/s")
        };

        /**
         * Commands for main torrent list popup menu
         */
        PopupCommandTorrentDescriptor commandsPopup[] = {
            new PopupCommandTorrentDescriptor(
            TorrentsListPopupCmd.TLIST_POPUP_CMD_START,
            globalResourceMap.getIcon("miTorrentStart.icon"),
            names[0],//"Start torrent(s)",
            BUNDLE_IDENT_MENU_TORRENT_START),
            new PopupCommandTorrentDescriptor(
            TorrentsListPopupCmd.TLIST_POPUP_CMD_STOP,
            globalResourceMap.getIcon("miTorrentStop.icon"),
            names[1],//"Start torrent(s)",
            BUNDLE_IDENT_MENU_TORRENT_START),
            new PopupCommandTorrentDescriptor(
            TorrentsListPopupCmd.TLIST_POPUP_CMD_REFRESH,
            globalResourceMap.getIcon("miTorrentRefresh.icon"),
            names[2],//"Start torrent(s)",
            BUNDLE_IDENT_MENU_TORRENT_START),
            new PopupCommandTorrentDescriptor(
            TorrentsListPopupCmd.TLIST_POPUP_CMD_CHECK,
            globalResourceMap.getIcon("miTorrentCheck.icon"),
            names[3],//"Start torrent(s)",
            BUNDLE_IDENT_MENU_TORRENT_START),
            new PopupCommandTorrentDescriptor(
            TorrentsListPopupCmd.TLIST_POPUP_CMD_PROP,
            globalResourceMap.getIcon("miTorrentProperties.icon"),
            names[4],//"Start torrent(s)",
            BUNDLE_IDENT_MENU_TORRENT_START),
            new PopupCommandTorrentDescriptor(
            TorrentsListPopupCmd.TLIST_POPUP_CMD_DEL,
            globalResourceMap.getIcon("miTorrentDelete.icon"),
            names[5],//"Start torrent(s)",
            BUNDLE_IDENT_MENU_TORRENT_START),
            new PopupCommandTorrentDescriptor(
            TorrentsListPopupCmd.TLIST_POPUP_CMD_DELALL,
            globalResourceMap.getIcon("miTorrentDeleteAll.icon"),
            names[6],//"Start torrent(s)",
            BUNDLE_IDENT_MENU_TORRENT_START),
            new PopupCommandTorrentDescriptor(
            TorrentsListPopupCmd.TLIST_POPUP_CMD_ANNOUNCE,
            globalResourceMap.getIcon("miTorrentAnnounce.icon"),
            names[7],//"Start torrent(s)",
            BUNDLE_IDENT_MENU_TORRENT_START),
            new PopupCommandTorrentDescriptor(
            TorrentsListPopupCmd.TLIST_POPUP_CMD_MOVE,
            globalResourceMap.getIcon("miTorrentMove.icon"),
            names[8],//"Start torrent(s)",
            BUNDLE_IDENT_MENU_TORRENT_START),
            new PopupCommandTorrentDescriptor(
            TorrentsListPopupCmd.TLIST_POPUP_CMD_SET_LOCATION,
            globalResourceMap.getIcon("miTorrentLocation.icon"),
            names[9],//"Set location",
            BUNDLE_IDENT_MENU_TORRENT_START),
            new PopupCommandTorrentDescriptor(
            TorrentsListPopupCmd.TLIST_POPUP_CMD_NONE,
            null,
            names[10],//"Start torrent(s)",
            BUNDLE_IDENT_MENU_TORRENT_START),
            new PopupCommandTorrentDescriptor(true,
            TorrentsListPopupCmd.TLIST_POPUP_CMD_LIMIT_DOWN,
            globalResourceMap.getIcon("miTorrentUp.icon"),
            names[11],//"Start torrent(s)",
            BUNDLE_IDENT_MENU_TORRENT_START, commandsPopupTorrentDn),
            new PopupCommandTorrentDescriptor(true,
            TorrentsListPopupCmd.TLIST_POPUP_CMD_LIMIT_UP,
            globalResourceMap.getIcon("miTorrentDn.icon"),
            names[12],//"Start torrent(s)",
            BUNDLE_IDENT_MENU_TORRENT_START, commandsPopupTorrentUp)
        };

        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            if (names[i].equals("SEPARATOR")) {
                JSeparator item = new JSeparator();
                tblTorrentListPopupMenu.add(item);
            } else {

                if (commandsPopup[i].isMenu) {
                    JMenu item = makeMenuCommandsPopup(commandsPopup[i].menu, name);
                    tblTorrentListPopupMenu.add(item);
                } else {

                    JMenuItem item;
                    if (commandsPopup[i].icon != null) {
                        item = new JMenuItem(name, commandsPopup[i].icon);
                    } else {
                        item = new JMenuItem(name);
                    }
                    TorrentsMenuItemListener menuItemListener = new TorrentsMenuItemListener(commandsPopup[i].cmd);
                    item.setName(name);
                    item.addActionListener(menuItemListener);
                    tblTorrentListPopupMenu.add(item);
                }
            }
        }


        namesList = globalResourceMap.getString("tblTorrentFiles.miFilesPopup.items");


        /**
         * Commands for file list popup menu
         */
        PopupCommandFilesDescriptor commandsFilesPopup[] = {
            new PopupCommandFilesDescriptor(
            FilesListPopupCmd.TLIST_POPUP_CMD_OPENFILE,
            globalResourceMap.getIcon("miTorrentStart.icon"),
            "Open Desination File",//"Start torrent(s)"
            BUNDLE_IDENT_MENU_TORRENT_START),
            new PopupCommandFilesDescriptor(
            FilesListPopupCmd.TLIST_POPUP_CMD_OPENDIR,
            globalResourceMap.getIcon("miOpenDestDir.icon"),
            "Open Desination Dir",//"Start torrent(s)"
            BUNDLE_IDENT_MENU_TORRENT_START)
        };

        for (int i = 0; i < commandsFilesPopup.length; i++) {
            String name = commandsFilesPopup[i].nameText;
            if (names[i].equals("SEPARATOR")) {
                JSeparator item = new JSeparator();
                tblTorrentListPopupMenu.add(item);
            } else {

                if (commandsFilesPopup[i].isMenu) {
//                    JMenu item = makeMenuCommandsPopup(commandsFilesPopup[i].menu, name);
//                    tblFileListPopupMenu.add(item);
                } else {

                    JMenuItem item;
                    if (commandsFilesPopup[i].icon != null) {
                        item = new JMenuItem(name, commandsFilesPopup[i].icon);
                    } else {
                        item = new JMenuItem(name);
                    }
                    FilesMenuItemListener menuItemListener = new FilesMenuItemListener(commandsFilesPopup[i].cmd);
                    item.setName(name);
                    item.addActionListener(menuItemListener);
                    tblFileListPopupMenu.add(item);
                }
            }
        }

        /**
         * !!!!!
         * @todo Implements call methods for popup FileListPopupMenu
         * @todo Move to resource
         */
        tblFileListPopupMenu.add(new JSeparator());
        tblFileListPopupMenu.add("High priority");
        tblFileListPopupMenu.add("Normal priority");
        tblFileListPopupMenu.add("Low priority");
        tblFileListPopupMenu.add(new JSeparator());
        tblFileListPopupMenu.add("Download");
        tblFileListPopupMenu.add("Skip");
        tblFileListPopupMenu.add(new JSeparator());
        tblFileListPopupMenu.add("Select All");
        tblFileListPopupMenu.add("DeSelect All");
        tblFileListPopupMenu.add("Reverse Select");

//         tblFileListPopupMenu.addPopupMenuListener( new  TorrentFileListPopupListener() )
        this.getFrame().addComponentListener(this);

    }

    private void showTblTorrentListPopupMenu(MouseEvent e) {
        tblTorrentListPopupMenu.show(e.getComponent(), e.getX(), e.getY());
    }

    private void showTblFileListListPopupMenu(MouseEvent e) {
        tblFileListPopupMenu.show(e.getComponent(), e.getX(), e.getY());
    }

    private void showMainToolBarPopupMenu(MouseEvent e) {
        mainToolBarPopupMenu.show(e.getComponent(), e.getX(), e.getY());
    }

    private void setAdditionalButtons() {
        ResizableIcon ri = new ArrowResizableIcon(9, SwingConstants.SOUTH);
        JCommandButton dropDownButton = new JCommandButton("Text Button", ri);

        ImageIcon imco = globalResourceMap.getImageIcon("btExit.icon");
        Dimension idim = new Dimension(imco.getIconWidth(), imco.getIconHeight());
        idim = new Dimension(40, 40);
        Image im = imco.getImage();

        ImageWrapperResizableIcon iwri = ImageWrapperResizableIcon.getIcon(im, idim);

        JCommandButton taskbarButtonPaste = new JCommandButton("", iwri);

        taskbarButtonPaste.setHorizontalAlignment(SwingConstants.LEFT);
        taskbarButtonPaste.setCommandButtonKind(CommandButtonKind.ACTION_AND_POPUP_MAIN_POPUP);
        //taskbarButtonPaste.setPopupOrientationKind(CommandButtonPopupOrientationKind.DOWNWARD);
        taskbarButtonPaste.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        taskbarButtonPaste.setDisplayState(TILE_36); // !!!!
        //taskbarButtonPaste.setPreferredSize(new Dimension(80, 48));
        System.out.println("Pref " + taskbarButtonPaste.getPreferredSize());
        System.out.println("Maxi " + taskbarButtonPaste.getMaximumSize());
        System.out.println("Mini " + taskbarButtonPaste.getMinimumSize());
        System.out.println("Size " + taskbarButtonPaste.getSize());
        Dimension d = new Dimension(taskbarButtonPaste.getPreferredSize().width, 48);
        taskbarButtonPaste.setMaximumSize(d);


        taskbarButtonPaste.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Taskbar Paste activated");
            }
        });
        taskbarButtonPaste.setPopupCallback(new PopupPanelCallback() {

            @Override
            public JPopupPanel getPopupPanel(JCommandButton commandButton) {
                return new SamplePopupMenu();
            }
        });

        taskbarButtonPaste.setActionRichTooltip(new RichTooltip("Paste",
                "Paste the contents of the Clipboard"));
        taskbarButtonPaste.setPopupRichTooltip(new RichTooltip("Paste",
                "Click here for more options such as pasting only the values or formatting"));
        taskbarButtonPaste.setActionKeyTip("1");


        //maiToolBar.add( rb );

        /*
        JRibbonBand rb = new JRibbonBand("", new EmptyResizableIcon(1));
        rb.addCommandButton(dropDownButton, RibbonElementPriority.MEDIUM);
         *
        JButton dropDownButton = DropDownButtonFactory.createDropDownButton(
        resourceMap.getIcon("btConnect.icon"),
        popup);
         */
        //dropDownButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(2,2,2,2));
        //dropDownButton.setBorderPainted( false );

        //btConnect = taskbarButtonPaste;
        mainToolBar.add(taskbarButtonPaste);

    }
    public static final CommandButtonDisplayState TILE_36 = new CommandButtonDisplayState(
            "Tile", 48) {

        @Override
        public CommandButtonLayoutManager createLayoutManager(
                AbstractCommandButton arg0) {
            return new CommandButtonLayoutManagerTile();
        }

        @Override
        public CommandButtonSeparatorOrientation getSeparatorOrientation() {
            return CommandButtonSeparatorOrientation.VERTICAL;
        }
    };

    private void setRefButtonsState(boolean connectedServer) {
        btRefresh.setEnabled(connectedServer);

        miTorrentRefresh.setEnabled(connectedServer);
    }

    private void setAllButtonsState(boolean connectedServer) {
        btRefresh.setEnabled(connectedServer);

        btStatistic.setEnabled(connectedServer);

        btAdd.setEnabled(connectedServer);
        btAddUrl.setEnabled(connectedServer);
        miFileQuickAddFile.setEnabled(connectedServer);
        miFileExtAddFile.setEnabled(connectedServer);
        miFileAddURL.setEnabled(connectedServer);

        btStart.setEnabled(connectedServer);
        btStop.setEnabled(connectedServer);
        miTorrentStart.setEnabled(connectedServer);
        miTorrentStop.setEnabled(connectedServer);
        miTorrentRefresh.setEnabled(connectedServer);

        miTorrentDelete.setEnabled(connectedServer);
        miTorrentAnnounce.setEnabled(connectedServer);
        miTorrentCheck.setEnabled(connectedServer);
        miTorrentDeleteAll.setEnabled(connectedServer);
        miTorrentStartAll.setEnabled(connectedServer);
        miTorrentStopAll.setEnabled(connectedServer);
        miTorrentProperties.setEnabled(connectedServer);
        miTorrentMove.setEnabled(connectedServer);
        miTorrentLocation.setEnabled(connectedServer);
    }
    private Level logginLevel = Level.ALL;

    private void initLogger() {

        try {
            String logPath = ConfigStorage.getFlogFileName();
            logRotate(logPath);
            FileHandler logFile = new FileHandler(logPath, true);
            logFile.setFormatter(new SimpleFormatter());

            globalLogger = Logger.getAnonymousLogger();
            Handler h[] = getGlobalLogger().getHandlers();
            for (int i = 0; i < h.length; i++) {
                globalLogger.removeHandler(h[i]);
            }

            globalLogger.addHandler(logFile);

            globalLogger.setLevel(logginLevel);
            globalLogger.setUseParentHandlers(false);

            globalLogger.log(Level.INFO, "Started");
            logFile.flush();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    private void checkNavigator() {
        int tblSelRow = tblTorrentList.getSelectedRow();
        if (tblSelRow < 0) {
            btLast.setEnabled(false);
            btNext.setEnabled(false);
            btPrev.setEnabled(false);
            btFirst.setEnabled(false);
            tfCurrentRow.setText("");
        } else {
            if (tblSelRow > 0) {
                btFirst.setEnabled(true);
                btPrev.setEnabled(true);
            } else {
                btPrev.setEnabled(false);
                btFirst.setEnabled(false);
            }
            if (tblSelRow < tblTorrentList.getRowSorter().getViewRowCount() - 1) {
                btLast.setEnabled(true);
                btNext.setEnabled(true);
            } else {
                btLast.setEnabled(false);
                btNext.setEnabled(false);
            }
            tfCurrentRow.setText("" + (tblSelRow + 1));
        }
    }

    private void initMainToolBarIcons(TorrentsListPopupCmd command) {

        defaultToolBarIconSize = command;

        switch (command) {
            case TLIST_POPUP_CMD_ICON_32x32: {
                btConnect.setIcon(globalResourceMap.getIcon("btConnect.icon")); // NOI18N
                btInfo.setIcon(globalResourceMap.getIcon("btInfo.icon")); // NOI18N
                btAdd.setIcon(globalResourceMap.getIcon("btAdd.icon")); // NOI18N
                btAddUrl.setIcon(globalResourceMap.getIcon("btAddUrl.icon")); // NOI18N

                btStart.setIcon(globalResourceMap.getIcon("btStart.icon")); // NOI18N
                btStop.setIcon(globalResourceMap.getIcon("btStop.icon")); // NOI18N
                btRefresh.setIcon(globalResourceMap.getIcon("btRefresh.icon")); // NOI18N

                btStatistic.setIcon(globalResourceMap.getIcon("btStatistic.icon")); // NOI18N
                btConfigCli.setIcon(globalResourceMap.getIcon("btConfigCli.icon")); // NOI18N
                btExit.setIcon(globalResourceMap.getIcon("btExit.icon")); // NOI18N
                mainToolBar.setMaximumSize(new Dimension(mainToolBar.getPreferredSize().width, 45));
                mainToolBar.setPreferredSize(new Dimension(mainToolBar.getPreferredSize().width, 45));
            }
            break;
            case TLIST_POPUP_CMD_ICON_16x16: {
                btConnect.setIcon(globalResourceMap.getIcon("btConnect_16.icon")); // NOI18N
                btInfo.setIcon(globalResourceMap.getIcon("btInfo_16.icon")); // NOI18N
                btAdd.setIcon(globalResourceMap.getIcon("btAdd_16.icon")); // NOI18N
                btAddUrl.setIcon(globalResourceMap.getIcon("btAddUrl_16.icon")); // NOI18N

                btStart.setIcon(globalResourceMap.getIcon("btStart_16.icon")); // NOI18N
                btStop.setIcon(globalResourceMap.getIcon("btStop_16.icon")); // NOI18N
                btRefresh.setIcon(globalResourceMap.getIcon("btRefresh_16.icon")); // NOI18N

                btStatistic.setIcon(globalResourceMap.getIcon("btStatistic_16.icon")); // NOI18N
                btConfigCli.setIcon(globalResourceMap.getIcon("btConfigCli_16.icon")); // NOI18N
                btExit.setIcon(globalResourceMap.getIcon("btExit_16.icon")); // NOI18N
                mainToolBar.setMaximumSize(new Dimension(mainToolBar.getPreferredSize().width, 24));
                mainToolBar.setPreferredSize(new Dimension(mainToolBar.getPreferredSize().width, 24));
            }
            break;
            case TLIST_POPUP_CMD_ICON_24x24: {
                btConnect.setIcon(globalResourceMap.getIcon("btConnect_24.icon")); // NOI18N
                btInfo.setIcon(globalResourceMap.getIcon("btInfo_24.icon")); // NOI18N
                btAdd.setIcon(globalResourceMap.getIcon("btAdd_24.icon")); // NOI18N
                btAddUrl.setIcon(globalResourceMap.getIcon("btAddUrl_24.icon")); // NOI18N

                btStart.setIcon(globalResourceMap.getIcon("btStart_24.icon")); // NOI18N
                btStop.setIcon(globalResourceMap.getIcon("btStop_24.icon")); // NOI18N
                btRefresh.setIcon(globalResourceMap.getIcon("btRefresh_24.icon")); // NOI18N

                btStatistic.setIcon(globalResourceMap.getIcon("btStatistic_24.icon")); // NOI18N
                btConfigCli.setIcon(globalResourceMap.getIcon("btConfigCli_24.icon")); // NOI18N
                btExit.setIcon(globalResourceMap.getIcon("btExit_24.icon")); // NOI18N
                mainToolBar.setMaximumSize(new Dimension(mainToolBar.getPreferredSize().width, 32));
                mainToolBar.setPreferredSize(new Dimension(mainToolBar.getPreferredSize().width, 32));
            }
            break;
        }
    }

    private void initResources() {

        tpTorrentDetails.setIconAt(0, globalResourceMap.getIcon("tpInfo.icon0"));
        tpTorrentDetails.setIconAt(1, globalResourceMap.getIcon("tpInfo.icon1"));
        tpTorrentDetails.setIconAt(2, globalResourceMap.getIcon("tpInfo.icon2"));
        tpTorrentDetails.setIconAt(3, globalResourceMap.getIcon("tpInfo.icon3"));
        tpTorrentDetails.setIconAt(4, globalResourceMap.getIcon("tpInfo.icon4"));

        initMainToolBarIcons(TorrentsListPopupCmd.TLIST_POPUP_CMD_ICON_24x24);

        cbFilterStatus.removeAllItems();
        cbFilterStatus.addItem(globalResourceMap.getString(STATUS_ALL_DESC));
        cbFilterStatus.addItem(globalResourceMap.getString(STATUS_DOWNLOAD_DESC));
        cbFilterStatus.addItem(globalResourceMap.getString(STATUS_PAUSED_DESC));
        cbFilterStatus.addItem(globalResourceMap.getString(STATUS_SEED_DESC));
        cbFilterStatus.addItem(globalResourceMap.getString(STATUS_CHECK_DESC));
        cbFilterStatus.addItem(globalResourceMap.getString(STATUS_CHECK_WAIT_DESC));
        cbFilterStatus.addItem(globalResourceMap.getString(STATUS_ERROR_DESC));


    }

    private void initLocale() {


        List<Locale> listLocales = Tools.getListOfAvailLanguages();
        for (int i = 0; i < listLocales.size(); i++) {
            Locale locale = listLocales.get(i);

            String country = locale.getCountry().length() > 0 ? locale.getCountry() : "uk";
            String flag = "/net/sf/dvstar/transmission/resources/images/flags/flags_" + country.toLowerCase() + ".png";

            URL urlFlag = getClass().getResource(flag);

            ImageIcon iconFlag = new ImageIcon(urlFlag);
            JMenuItem miLocale;
            if (iconFlag != null) {
                miLocale = new JMenuItem(locale.getDisplayLanguage(Locale.ENGLISH), iconFlag);
            } else {
                miLocale = new JMenuItem(locale.getDisplayLanguage(Locale.ENGLISH));
            }

            miLocale.addActionListener(new LocaleActionListener(locale));

            mnConfigLocale.add(miLocale);
        }
    }

    /**
     * @return the defaultLocale
     */
    @Override
    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    /**
     * @param defaultLocale the defaultLocale to set
     */
    public void setDefaultLocale(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
        Locale.setDefault(defaultLocale);
    }

    @Override
    public Logger getGlobalLogger() {
        return globalLogger;
    }

    @Override
    public ConfigStorage getGlobalConfigStorage() {
        return globalConfigStorage;
    }

    @Override
    public ResourceMap getGlobalResourceMap() {
        return globalResourceMap;
    }

    private List<Integer> mergeListToOne(List selectedRowList, List visibleRowList) {
        List ret = new ArrayList(selectedRowList);
        List tst = new ArrayList(visibleRowList);
        int idx;
        for (int i = 0; i < selectedRowList.size(); i++) {
            if ((idx = tst.indexOf(selectedRowList.get(i))) >= 0) {
                tst.remove(idx);
            }
        }
        ret.addAll(tst);
        return ret;
    }

    private void initTimers() {

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //!!statusMessageLabel.setText("");!!
                //String spd = updateStatusLine();
                //deault info text
                //statusMessageLabel.setText(spd);
            }
        });
        messageTimer.setRepeats(false);

        refreshTimer = new Timer(10000, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ApplicationContext ctx = getApplication().getContext();
                Task task = new DoRefreshTask(getApplication(), false, false);
                ctx.getTaskService().execute(task);
            }
        });
        refreshTimer.setRepeats(true);
        refreshTimer.stop();


        updaterTimer = new Timer(10000, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                ApplicationContext ctx = getApplication().getContext();
                Task task = new DoUpdateStatusLine(getApplication());
                ctx.getTaskService().execute(task);
                progressBar.setVisible(false);
                progressBar.setValue(0);

                /** @todo Запускать обновление информации в случае если
                в течении 0,5 сек не менялся выбор строки таблицы
                 */
                if ((rowSelectionNew = tblTorrentList.getSelectedRow()) > 0 && rowSelectionNew != rowSelectionOld) {
                    rowSelectionOld = rowSelectionNew;
                }

                rowSelectionOld = tblTorrentList.getSelectedRow();
                rowSelectionNew = -1;

                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                }

                rowSelectionNew = tblTorrentList.getSelectedRow();
                if (rowSelectionNew == rowSelectionOld) {
                    if (tblTorrentList.getSelectedRow() > 0) {
                        System.out.println("!!! [actionPerformed] Selected = " + rowSelectionNew);
                        taskDoUpdateTorrentDetails = new DoUpdateTorrentDetails(getApplication(), rowSelectionNew);
                        ctx.getTaskService().execute(taskDoUpdateTorrentDetails);
                        progressBar.setVisible(false);
                        progressBar.setValue(0);
                    }
                }
            }
        });
        updaterTimer.setRepeats(true);
        updaterTimer.stop();


        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }

        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });

    }

    private void initSpeedoMeter() {

        speedoTimer = new Timer(10, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ApplicationContext ctx = getApplication().getContext();
                Task task = new DoSpeedoMeterTask(getApplication());
                ctx.getTaskService().execute(task);
            }
        });
        refreshTimer.setRepeats(true);
        refreshTimer.stop();

    }

    //------------------------- ComponentListener ------------------------------
    @Override
    public void componentResized(ComponentEvent e) {
        String sizeString = "[" + e.getComponent().getWidth() + "][" + e.getComponent().getHeight() + "]";
        statusMessageLabel.setText("Size is " + sizeString);
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }
    //------------------------- ComponentListener ------------------------------

    private boolean processConnect(boolean mode) throws UnknownHostException, IOException, HttpException, SocketTimeoutException, TorrentsCommonException, JSONException {
        boolean ret = true;

        //if(httpClientConnection != null && httpClientConnection.isOpen()) { httpClientConnection.close(); return false;}
        webClient = new TransmissionWebClient(true, globalLogger);

        if (mode) {

            JSONObject jobj;

            Requests req = new Requests(getGlobalLogger());
            webClient.prepareWebRequest();

            //\\ret = req.produceConnect();
            jobj = req.sessionGet();
            JSONObject response = webClient.processWebRequest(jobj, "processConnect");
            transmissionDaemonDescriptor = new TransmissionDescriptor(response);

            if (webClient.getResponseData() != null) {
                String sTemp;
                /**
                 * @todo
                 * make better exception parse
                 */
                JSONObject jresult = JSONObject.fromObject(webClient.getResponseData());
                tracePrint(true, jresult.toString());
                JSONObject args = jresult.getJSONObject("arguments");
                tracePrint(true, args.toString());
                sTemp = args.getString("version");
                globalConfigStorage.setProperty("server-version", sTemp);
                globalConfigStorage.saveConfig();
            }


            connectedServer = ret;

        } else {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            webClient.closeWebRequest();
            connectedServer = ret = false;
        }


        /*
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "UTF-8");
        HttpProtocolParams.setUserAgent(params, "HttpComponents/1.1");
        HttpProtocolParams.setUseExpectContinue(params, true);
        
        BasicHttpProcessor httpproc = new BasicHttpProcessor();
        // Required protocol interceptors
        httpproc.addInterceptor(new RequestContent());
        httpproc.addInterceptor(new RequestTargetHost());
        // Recommended protocol interceptors
        httpproc.addInterceptor(new RequestConnControl());
        httpproc.addInterceptor(new RequestUserAgent());
        httpproc.addInterceptor(new RequestExpectContinue());
        
        HttpRequestExecutor httpexecutor = new HttpRequestExecutor();
        
        HttpContext context = new BasicHttpContext(null);
        
        HttpHost host = new HttpHost(localSettiingsFactory.getHost(), Integer.parseInt(localSettiingsFactory.getPort()));
        httpClientConnection = new DefaultHttpClientConnection();
        
        context.setAttribute(ExecutionContext.HTTP_CONNECTION, httpClientConnection);
        context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, host);
        
        if (!httpClientConnection.isOpen()) {
        Socket socket = new Socket(host.getHostName(), host.getPort());
        ((DefaultHttpClientConnection)httpClientConnection).bind(socket, params);
        }
        
        HttpEntity requestBodies = new StringEntity( jobj.toString() );
        
        BasicHttpEntityEnclosingRequest request = new BasicHttpEntityEnclosingRequest("POST",
        "/servlets-examples/servlet/RequestInfoExample");
        request.setEntity(requestBodies);
        System.out.println(">> Request URI: " + request.getRequestLine().getUri());
        
        request.setParams(params);
        httpexecutor.preProcess(request, httpproc, context);
        HttpResponse response = httpexecutor.execute(request, httpClientConnection, context);
        response.setParams(params);
        httpexecutor.postProcess(response, httpproc, context);
        
        System.out.println("<< Response: " + response.getStatusLine());
        System.out.println(EntityUtils.toString(response.getEntity()));
        System.out.println("==============");
        
         */


        /*
        if (!connStrategy.keepAlive(response, context)) {
        conn.close();
        } else {
        System.out.println("Connection kept alive...");
        }
         */
        return ret;
    }

    /**
     * Update torrens list
     * @param fullRefresh - refresh full list
     * @throws UnknownHostException
     * @throws IOException
     * @throws HttpException
     * @todo - update visible list vs current selected
     */
    private void refreshTorrentsList(boolean fullRefresh, boolean singleRow) throws UnknownHostException, IOException, HttpException, TorrentsCommonException {

        Requests req = new Requests(getGlobalLogger());
        JSONObject jobj = null;
        //int srows[] = tblTorrentList.getSelectedRows();
        List<Integer> vrowsList = null, vsrowsList = null;

//System.out.println("!!! Selected = "+Tools.printArray(srows));

        ListSelectionModel model = tblTorrentList.getSelectionModel();

        if (!fullRefresh) {
            vsrowsList = buildSelectModelList();
            if (!singleRow) {
                vrowsList = buildViewModelList();
            }

            if (vrowsList != null) {
                vsrowsList = mergeListToOne(vsrowsList, vrowsList);
            }

            jobj = req.torrentGet(vsrowsList, modelTorrentsList, METHOD_TORRENTGET_LIST);
            //mrows = buildSelectModelArray();
        } else {
            updateInfoBox(-1);
            jobj = req.torrentGet();
        }

        if (enableTraceOut) {
            System.out.println("[Requests][refreshTorrentsList][torrentGet]\n" + jobj.toString());
        }

        tracePrint(true, Tools.whoCalledMe() + " [Requests][refreshTorrentsList][torrentGet]" + jobj.toString());

        JSONObject result = webClient.processWebRequest(jobj, "refreshTorrentsList");

        if (fullRefresh) {
            modelTorrentsList = new TorrentsTableModel(result, this);
        } else {
            int vsrowsList_size = vsrowsList.size();
            for (int i = 0; i < vsrowsList_size; i++) {
                Torrent tor = modelTorrentsList.getTableDataTorrents().get(vsrowsList.get(i));

                tor.parseResult(result, tor.getId());

                modelTorrentsList.getTableDataTorrents().set(vsrowsList.get(i), tor);
            }
            /*
            for (int i = 0; i < mrows.length; i++) {
            Torrent tor = modelTorrentsList.getTableDataTorrents().get(mrows[i]);

            tor.parseResult(result, tor.getId());

            modelTorrentsList.getTableDataTorrents().set(mrows[i], tor);
            }
             */
        }


        if (result != null) {

            lockValueChanged = true;

            List sortList = tblTorrentList.getRowSorter().getSortKeys();
            tblTorrentList.setModel(modelTorrentsList);
            //newFilter();

            /**
             * Restore extern sorter
             */
            TorrentListRowSorter torrentListRowSorter =
                    new TorrentListRowSorter((TorrentsTableModel) tblTorrentList.getModel());
            tblTorrentList.setRowSorter(torrentListRowSorter);

            //tblTorrentList.getColumnModel().getColumn(0).setResizable(false);
            TableCellRenderer custom = new TorrentsTableCustomRenderer((TorrentsTableModel) modelTorrentsList);
            tblTorrentList.setDefaultRenderer(Object.class, custom);
            tblTorrentList.setDefaultRenderer(Number.class, custom);
            TorrentsTableModel.setPreferredColumnWidths(tblTorrentList);

            tblTorrentList.getColumnModel().getColumn(3).setCellRenderer(new ProgressBarRenderer());

            //restoreRowsSelections(selectedTorrentRows);

            //!!!! tblTorrentList.tableChanged(new TableModelEvent(modelTorrentsList));


            tblTorrentList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            tblTorrentList.setCellSelectionEnabled(false);
            tblTorrentList.setRowSelectionAllowed(true);


            if (torrentsSelectionListener == null) {
                tblTorrentList.setSelectionModel(model);
                torrentsSelectionListener = new TorrentsSelectionListener((TorrentsTableModel) modelTorrentsList, tblTorrentList);
                tblTorrentList.getSelectionModel().addListSelectionListener(torrentsSelectionListener);
            }

            //restoreRowsSelections(selectedTorrentRows);
            setTorrentListFilter();
            lockValueChanged = false;
            tblTorrentList.getRowSorter().setSortKeys(sortList);
            restoreRowsSelections(selectedTorrentRows);

        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        mainPanel = new javax.swing.JPanel();
        spMain = new javax.swing.JSplitPane();
        plTorrentList = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        cbRegistry = new javax.swing.JCheckBox();
        lbFindInfo = new javax.swing.JLabel();
        cbFilterStatus = new javax.swing.JComboBox();
        lbClearSelection = new javax.swing.JLabel();
        tfFindItem = new javax.swing.JTextField();
        btFind = new javax.swing.JButton();
        jPanel11 = new javax.swing.JPanel();
        spTorrentList = new javax.swing.JScrollPane();
        tblTorrentList = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        cbTorrentGroup = new javax.swing.JComboBox();
        btSetTorrentGroup = new javax.swing.JButton();
        btConfigTorrentGroup = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        btFirst = new javax.swing.JButton();
        btPrev = new javax.swing.JButton();
        tfCurrentRow = new javax.swing.JTextField();
        btNext = new javax.swing.JButton();
        btLast = new javax.swing.JButton();
        plTorrentInfo = new javax.swing.JPanel();
        tpTorrentDetails = new javax.swing.JTabbedPane();
        plInfo = new javax.swing.JPanel();
        plInfoCommon = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        tfTimeAll = new javax.swing.JTextField();
        tfDownloaded = new javax.swing.JTextField();
        tfSpeedDn = new javax.swing.JTextField();
        tfState = new javax.swing.JTextField();
        tfComment = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        tfTimeAll1 = new javax.swing.JTextField();
        tfUploaded = new javax.swing.JTextField();
        tfRemaining = new javax.swing.JTextField();
        tfStartedAt = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        lbErrorInfo = new javax.swing.JLabel();
        tfSeeds = new javax.swing.JTextField();
        tfLeechers = new javax.swing.JTextField();
        tfRate = new javax.swing.JTextField();
        tfCreatedAt = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        tfStorePath = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        tfSpeedDn3 = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        tfSpeedUp = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        tfCreator = new javax.swing.JTextField();
        tfErrorInfo = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        plPieces = new javax.swing.JPanel();
        lbProgress = new javax.swing.JLabel();
        plFiles = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblTorrentFiles = new javax.swing.JTable();
        plPeers = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblTorrentPeers = new javax.swing.JTable();
        plTrackers = new javax.swing.JPanel();
        plSpeed = new javax.swing.JPanel();
        plLog = new javax.swing.JPanel();
        spLog = new javax.swing.JScrollPane();
        taLog = new javax.swing.JTextArea();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        miFileConnect = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        miFileQuickAddFile = new javax.swing.JMenuItem();
        miFileExtAddFile = new javax.swing.JMenuItem();
        miFileInfo = new javax.swing.JMenuItem();
        miFileAddURL = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        javax.swing.JMenuItem miFileExit = new javax.swing.JMenuItem();
        configMenu = new javax.swing.JMenu();
        miConfigClient = new javax.swing.JMenuItem();
        miConfigServer = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        mnConfigLocale = new javax.swing.JMenu();
        torrentMenu = new javax.swing.JMenu();
        miTorrentStart = new javax.swing.JMenuItem();
        miTorrentStop = new javax.swing.JMenuItem();
        miTorrentRefresh = new javax.swing.JMenuItem();
        miTorrentCheck = new javax.swing.JMenuItem();
        miTorrentProperties = new javax.swing.JMenuItem();
        jSeparator11 = new javax.swing.JSeparator();
        miTorrentDelete = new javax.swing.JMenuItem();
        miTorrentDeleteAll = new javax.swing.JMenuItem();
        jSeparator12 = new javax.swing.JSeparator();
        miTorrentAnnounce = new javax.swing.JMenuItem();
        miTorrentMove = new javax.swing.JMenuItem();
        miTorrentLocation = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        miTorrentStartAll = new javax.swing.JMenuItem();
        miTorrentStopAll = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem miHelpAbout = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        mainToolBar = new javax.swing.JToolBar();
        btConnect = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        btInfo = new javax.swing.JButton();
        jSeparator10 = new javax.swing.JToolBar.Separator();
        btAdd = new javax.swing.JButton();
        btAddUrl = new javax.swing.JButton();
        jSeparator8 = new javax.swing.JToolBar.Separator();
        btStart = new javax.swing.JButton();
        btStop = new javax.swing.JButton();
        btRefresh = new javax.swing.JButton();
        jSeparator9 = new javax.swing.JToolBar.Separator();
        btStatistic = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        btConfigCli = new javax.swing.JButton();
        jSeparator7 = new javax.swing.JToolBar.Separator();
        btExit = new javax.swing.JButton();

        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setLayout(new java.awt.BorderLayout());

        spMain.setDividerLocation(250);
        spMain.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        spMain.setResizeWeight(1.0);
        spMain.setName("spMain"); // NOI18N

        plTorrentList.setMinimumSize(new java.awt.Dimension(21, 200));
        plTorrentList.setName("plTorrentList"); // NOI18N
        plTorrentList.setLayout(new java.awt.BorderLayout());

        jPanel10.setName("jPanel10"); // NOI18N
        jPanel10.setPreferredSize(new java.awt.Dimension(680, 27));
        jPanel10.setLayout(new java.awt.GridBagLayout());

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(net.sf.dvstar.transmission.TransmissionApp.class).getContext().getResourceMap(TransmissionView.class);
        cbRegistry.setText(resourceMap.getString("cbRegistry.text")); // NOI18N
        cbRegistry.setToolTipText(resourceMap.getString("cbRegistry.toolTipText")); // NOI18N
        cbRegistry.setBorder(null);
        cbRegistry.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        cbRegistry.setName("cbRegistry"); // NOI18N
        cbRegistry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbRegistryActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(1, 2, 1, 2);
        jPanel10.add(cbRegistry, gridBagConstraints);

        lbFindInfo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbFindInfo.setText(resourceMap.getString("lbFindInfo.text")); // NOI18N
        lbFindInfo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lbFindInfo.setMaximumSize(new java.awt.Dimension(28, 20));
        lbFindInfo.setName("lbFindInfo"); // NOI18N
        lbFindInfo.setPreferredSize(new java.awt.Dimension(28, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 96;
        gridBagConstraints.ipady = 6;
        gridBagConstraints.insets = new java.awt.Insets(1, 2, 1, 2);
        jPanel10.add(lbFindInfo, gridBagConstraints);

        cbFilterStatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All", "Downloading", "Paused", "Seeding", "Checking", "Error" }));
        cbFilterStatus.setName("cbFilterStatus"); // NOI18N
        cbFilterStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbFilterStatusActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 8;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(1, 2, 1, 2);
        jPanel10.add(cbFilterStatus, gridBagConstraints);

        lbClearSelection.setIcon(resourceMap.getIcon("clear_left.icon")); // NOI18N
        lbClearSelection.setText(resourceMap.getString("lbClearSelection.text")); // NOI18N
        lbClearSelection.setToolTipText(resourceMap.getString("lbClearSelection.toolTipText")); // NOI18N
        lbClearSelection.setName("lbClearSelection"); // NOI18N
        lbClearSelection.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbClearSelectionMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(1, 2, 1, 2);
        jPanel10.add(lbClearSelection, gridBagConstraints);

        tfFindItem.setText(null);
        tfFindItem.setName("tfFindItem"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 387;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 2, 1, 2);
        jPanel10.add(tfFindItem, gridBagConstraints);

        btFind.setIcon(resourceMap.getIcon("btFind.icon")); // NOI18N
        btFind.setText(resourceMap.getString("btFind.text")); // NOI18N
        btFind.setName("btFind"); // NOI18N
        btFind.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btFindActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(1, 2, 1, 2);
        jPanel10.add(btFind, gridBagConstraints);

        plTorrentList.add(jPanel10, java.awt.BorderLayout.NORTH);

        jPanel11.setName("jPanel11"); // NOI18N
        jPanel11.setLayout(new java.awt.BorderLayout());

        spTorrentList.setName("spTorrentList"); // NOI18N
        spTorrentList.setPreferredSize(new java.awt.Dimension(454, 200));

        tblTorrentList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "№", "Name", "Size", "Progress", "Status", "Seed", "Leech", "Dn Speed", "Up Speed", "Upload"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblTorrentList.setColumnSelectionAllowed(true);
        tblTorrentList.setName("tblTorrentList"); // NOI18N
        tblTorrentList.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tblTorrentList.getTableHeader().setReorderingAllowed(false);
        spTorrentList.setViewportView(tblTorrentList);
        tblTorrentList.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblTorrentList.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblTorrentList.columnModel.title0")); // NOI18N
        tblTorrentList.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblTorrentList.columnModel.title1")); // NOI18N
        tblTorrentList.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblTorrentList.columnModel.title2")); // NOI18N
        tblTorrentList.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblTorrentList.columnModel.title3")); // NOI18N
        tblTorrentList.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblTorrentList.columnModel.title4")); // NOI18N
        tblTorrentList.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("tblTorrentList.columnModel.title5")); // NOI18N
        tblTorrentList.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("tblTorrentList.columnModel.title6")); // NOI18N
        tblTorrentList.getColumnModel().getColumn(7).setHeaderValue(resourceMap.getString("tblTorrentList.columnModel.title7")); // NOI18N
        tblTorrentList.getColumnModel().getColumn(8).setHeaderValue(resourceMap.getString("tblTorrentList.columnModel.title8")); // NOI18N
        tblTorrentList.getColumnModel().getColumn(9).setHeaderValue(resourceMap.getString("tblTorrentList.columnModel.title9")); // NOI18N

        jPanel11.add(spTorrentList, java.awt.BorderLayout.CENTER);

        plTorrentList.add(jPanel11, java.awt.BorderLayout.CENTER);

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setPreferredSize(new java.awt.Dimension(981, 28));
        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel1.setMinimumSize(new java.awt.Dimension(314, 31));
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(261, 31));
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 1));

        cbTorrentGroup.setToolTipText(resourceMap.getString("cbTorrentGroup.toolTipText")); // NOI18N
        cbTorrentGroup.setName("cbTorrentGroup"); // NOI18N
        cbTorrentGroup.setPreferredSize(new java.awt.Dimension(120, 22));
        jPanel1.add(cbTorrentGroup);

        btSetTorrentGroup.setText(resourceMap.getString("btSetTorrentGroup.text")); // NOI18N
        btSetTorrentGroup.setToolTipText(resourceMap.getString("btSetTorrentGroup.toolTipText")); // NOI18N
        btSetTorrentGroup.setName("btSetTorrentGroup"); // NOI18N
        btSetTorrentGroup.setPreferredSize(new java.awt.Dimension(95, 26));
        btSetTorrentGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSetTorrentGroupActionPerformed(evt);
            }
        });
        jPanel1.add(btSetTorrentGroup);

        btConfigTorrentGroup.setIcon(resourceMap.getIcon("btConfigTorrentGroup.icon")); // NOI18N
        btConfigTorrentGroup.setText(resourceMap.getString("btConfigTorrentGroup.text")); // NOI18N
        btConfigTorrentGroup.setToolTipText(resourceMap.getString("btConfigTorrentGroup.toolTipText")); // NOI18N
        btConfigTorrentGroup.setName("btConfigTorrentGroup"); // NOI18N
        btConfigTorrentGroup.setPreferredSize(new java.awt.Dimension(26, 26));
        btConfigTorrentGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btConfigTorrentGroupActionPerformed(evt);
            }
        });
        jPanel1.add(btConfigTorrentGroup);

        jPanel3.add(jPanel1, java.awt.BorderLayout.CENTER);

        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 2));

        btFirst.setIcon(resourceMap.getIcon("btFirst.icon")); // NOI18N
        btFirst.setText(resourceMap.getString("btFirst.text")); // NOI18N
        btFirst.setName("btFirst"); // NOI18N
        btFirst.setPreferredSize(new java.awt.Dimension(22, 22));
        btFirst.addActionListener( new NavigatorButtonActionListener( NavigatorButtonActionListener.NAV_BUTTON_FIRS ));
        jPanel2.add(btFirst);

        btPrev.setIcon(resourceMap.getIcon("btPrev.icon")); // NOI18N
        btPrev.setName("btPrev"); // NOI18N
        btPrev.setPreferredSize(new java.awt.Dimension(22, 22));
        btPrev.addActionListener( new NavigatorButtonActionListener( NavigatorButtonActionListener.NAV_BUTTON_PREV ));
        jPanel2.add(btPrev);

        tfCurrentRow.setColumns(6);
        tfCurrentRow.setText(resourceMap.getString("tfCurrentRow.text")); // NOI18N
        tfCurrentRow.setName("tfCurrentRow"); // NOI18N
        jPanel2.add(tfCurrentRow);

        btNext.setIcon(resourceMap.getIcon("btNext.icon")); // NOI18N
        btNext.setName("btNext"); // NOI18N
        btNext.setPreferredSize(new java.awt.Dimension(22, 22));
        btNext.addActionListener( new NavigatorButtonActionListener( NavigatorButtonActionListener.NAV_BUTTON_NEXT ));
        jPanel2.add(btNext);

        btLast.setIcon(resourceMap.getIcon("btLast.icon")); // NOI18N
        btLast.setName("btLast"); // NOI18N
        btLast.setPreferredSize(new java.awt.Dimension(22, 22));
        btLast.addActionListener( new NavigatorButtonActionListener( NavigatorButtonActionListener.NAV_BUTTON_LAST ));
        jPanel2.add(btLast);

        jPanel3.add(jPanel2, java.awt.BorderLayout.EAST);

        plTorrentList.add(jPanel3, java.awt.BorderLayout.SOUTH);

        spMain.setLeftComponent(plTorrentList);

        plTorrentInfo.setName("plTorrentInfo"); // NOI18N
        plTorrentInfo.setPreferredSize(new java.awt.Dimension(590, 80));
        plTorrentInfo.setLayout(new java.awt.BorderLayout());

        tpTorrentDetails.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        tpTorrentDetails.setName("tpTorrentDetails"); // NOI18N

        plInfo.setName("plInfo"); // NOI18N
        plInfo.setLayout(new java.awt.BorderLayout());

        plInfoCommon.setBorder(javax.swing.BorderFactory.createTitledBorder("..."));
        plInfoCommon.setMinimumSize(new java.awt.Dimension(661, 162));
        plInfoCommon.setName("plInfoCommon"); // NOI18N
        plInfoCommon.setPreferredSize(new java.awt.Dimension(661, 162));
        plInfoCommon.setLayout(new java.awt.GridBagLayout());

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        jLabel2.setPreferredSize(new java.awt.Dimension(72, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        plInfoCommon.add(jLabel2, gridBagConstraints);

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        jLabel3.setPreferredSize(new java.awt.Dimension(72, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        plInfoCommon.add(jLabel3, gridBagConstraints);

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        jLabel4.setPreferredSize(new java.awt.Dimension(72, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        plInfoCommon.add(jLabel4, gridBagConstraints);

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N
        jLabel5.setPreferredSize(new java.awt.Dimension(72, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        plInfoCommon.add(jLabel5, gridBagConstraints);

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(72, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        plInfoCommon.add(jLabel6, gridBagConstraints);

        tfTimeAll.setEditable(false);
        tfTimeAll.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("tfTimeAll.border.lineColor"))); // NOI18N
        tfTimeAll.setMinimumSize(new java.awt.Dimension(68, 16));
        tfTimeAll.setName("tfTimeAll"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 30;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        plInfoCommon.add(tfTimeAll, gridBagConstraints);

        tfDownloaded.setEditable(false);
        tfDownloaded.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("tfTimeAll.border.lineColor"))); // NOI18N
        tfDownloaded.setMinimumSize(new java.awt.Dimension(68, 16));
        tfDownloaded.setName("tfDownloaded"); // NOI18N
        tfDownloaded.setPreferredSize(new java.awt.Dimension(68, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 30;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        plInfoCommon.add(tfDownloaded, gridBagConstraints);

        tfSpeedDn.setEditable(false);
        tfSpeedDn.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("tfTimeAll.border.lineColor"))); // NOI18N
        tfSpeedDn.setMinimumSize(new java.awt.Dimension(68, 16));
        tfSpeedDn.setName("tfSpeedDn"); // NOI18N
        tfSpeedDn.setPreferredSize(new java.awt.Dimension(68, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 30;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        plInfoCommon.add(tfSpeedDn, gridBagConstraints);

        tfState.setEditable(false);
        tfState.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("tfTimeAll.border.lineColor"))); // NOI18N
        tfState.setMinimumSize(new java.awt.Dimension(68, 16));
        tfState.setName("tfState"); // NOI18N
        tfState.setPreferredSize(new java.awt.Dimension(68, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 30;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        plInfoCommon.add(tfState, gridBagConstraints);

        tfComment.setEditable(false);
        tfComment.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("tfTimeAll.border.lineColor"))); // NOI18N
        tfComment.setName("tfComment"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 3.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        plInfoCommon.add(tfComment, gridBagConstraints);

        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N
        jLabel10.setPreferredSize(new java.awt.Dimension(72, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        plInfoCommon.add(jLabel10, gridBagConstraints);

        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N
        jLabel8.setPreferredSize(new java.awt.Dimension(72, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        plInfoCommon.add(jLabel8, gridBagConstraints);

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(72, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        plInfoCommon.add(jLabel7, gridBagConstraints);

        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N
        jLabel9.setPreferredSize(new java.awt.Dimension(72, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        plInfoCommon.add(jLabel9, gridBagConstraints);

        tfTimeAll1.setEditable(false);
        tfTimeAll1.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("tfTimeAll.border.lineColor"))); // NOI18N
        tfTimeAll1.setMaximumSize(new java.awt.Dimension(68, 16));
        tfTimeAll1.setMinimumSize(new java.awt.Dimension(68, 16));
        tfTimeAll1.setName("tfTimeAll1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 30;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        plInfoCommon.add(tfTimeAll1, gridBagConstraints);

        tfUploaded.setEditable(false);
        tfUploaded.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("tfTimeAll.border.lineColor"))); // NOI18N
        tfUploaded.setMinimumSize(new java.awt.Dimension(68, 16));
        tfUploaded.setName("tfUploaded"); // NOI18N
        tfUploaded.setPreferredSize(new java.awt.Dimension(68, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 30;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        plInfoCommon.add(tfUploaded, gridBagConstraints);

        tfRemaining.setEditable(false);
        tfRemaining.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("tfTimeAll.border.lineColor"))); // NOI18N
        tfRemaining.setMinimumSize(new java.awt.Dimension(68, 16));
        tfRemaining.setName("tfRemaining"); // NOI18N
        tfRemaining.setPreferredSize(new java.awt.Dimension(68, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 30;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        plInfoCommon.add(tfRemaining, gridBagConstraints);

        tfStartedAt.setEditable(false);
        tfStartedAt.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("tfTimeAll.border.lineColor"))); // NOI18N
        tfStartedAt.setMinimumSize(new java.awt.Dimension(68, 16));
        tfStartedAt.setName("tfStartedAt"); // NOI18N
        tfStartedAt.setPreferredSize(new java.awt.Dimension(68, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 30;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        plInfoCommon.add(tfStartedAt, gridBagConstraints);

        jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N
        jLabel14.setPreferredSize(new java.awt.Dimension(72, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        plInfoCommon.add(jLabel14, gridBagConstraints);

        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N
        jLabel12.setPreferredSize(new java.awt.Dimension(72, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        plInfoCommon.add(jLabel12, gridBagConstraints);

        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N
        jLabel11.setPreferredSize(new java.awt.Dimension(72, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        plInfoCommon.add(jLabel11, gridBagConstraints);

        lbErrorInfo.setForeground(resourceMap.getColor("tfErrorInfo.foreground")); // NOI18N
        lbErrorInfo.setText(resourceMap.getString("lbErrorInfo.text")); // NOI18N
        lbErrorInfo.setName("lbErrorInfo"); // NOI18N
        lbErrorInfo.setPreferredSize(new java.awt.Dimension(72, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        plInfoCommon.add(lbErrorInfo, gridBagConstraints);

        tfSeeds.setEditable(false);
        tfSeeds.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("tfTimeAll.border.lineColor"))); // NOI18N
        tfSeeds.setMinimumSize(new java.awt.Dimension(68, 16));
        tfSeeds.setName("tfSeeds"); // NOI18N
        tfSeeds.setPreferredSize(new java.awt.Dimension(68, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 30;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        plInfoCommon.add(tfSeeds, gridBagConstraints);

        tfLeechers.setEditable(false);
        tfLeechers.setText(resourceMap.getString("tfLeechers.text")); // NOI18N
        tfLeechers.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("tfTimeAll.border.lineColor"))); // NOI18N
        tfLeechers.setMinimumSize(new java.awt.Dimension(68, 16));
        tfLeechers.setName("tfLeechers"); // NOI18N
        tfLeechers.setPreferredSize(new java.awt.Dimension(68, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 30;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        plInfoCommon.add(tfLeechers, gridBagConstraints);

        tfRate.setEditable(false);
        tfRate.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("tfTimeAll.border.lineColor"))); // NOI18N
        tfRate.setMinimumSize(new java.awt.Dimension(68, 16));
        tfRate.setName("tfRate"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 30;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        plInfoCommon.add(tfRate, gridBagConstraints);

        tfCreatedAt.setEditable(false);
        tfCreatedAt.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("tfTimeAll.border.lineColor"))); // NOI18N
        tfCreatedAt.setMinimumSize(new java.awt.Dimension(68, 16));
        tfCreatedAt.setName("tfCreatedAt"); // NOI18N
        tfCreatedAt.setPreferredSize(new java.awt.Dimension(68, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 30;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        plInfoCommon.add(tfCreatedAt, gridBagConstraints);

        jLabel16.setText(resourceMap.getString("jLabel16.text")); // NOI18N
        jLabel16.setName("jLabel16"); // NOI18N
        jLabel16.setPreferredSize(new java.awt.Dimension(72, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        plInfoCommon.add(jLabel16, gridBagConstraints);

        tfStorePath.setEditable(false);
        tfStorePath.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("tfStorePath.border.lineColor"))); // NOI18N
        tfStorePath.setName("tfStorePath"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 3.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        plInfoCommon.add(tfStorePath, gridBagConstraints);

        jLabel17.setText(resourceMap.getString("jLabel17.text")); // NOI18N
        jLabel17.setName("jLabel17"); // NOI18N
        jLabel17.setPreferredSize(new java.awt.Dimension(72, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        plInfoCommon.add(jLabel17, gridBagConstraints);

        tfSpeedDn3.setEditable(false);
        tfSpeedDn3.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("tfSpeedDn3.border.lineColor"))); // NOI18N
        tfSpeedDn3.setMinimumSize(new java.awt.Dimension(68, 16));
        tfSpeedDn3.setName("tfSpeedDn3"); // NOI18N
        tfSpeedDn3.setPreferredSize(new java.awt.Dimension(68, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 30;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        plInfoCommon.add(tfSpeedDn3, gridBagConstraints);

        jLabel18.setText(resourceMap.getString("jLabel18.text")); // NOI18N
        jLabel18.setName("jLabel18"); // NOI18N
        jLabel18.setPreferredSize(new java.awt.Dimension(72, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        plInfoCommon.add(jLabel18, gridBagConstraints);

        tfSpeedUp.setEditable(false);
        tfSpeedUp.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("tfSpeedUp.border.lineColor"))); // NOI18N
        tfSpeedUp.setMaximumSize(new java.awt.Dimension(68, 16));
        tfSpeedUp.setMinimumSize(new java.awt.Dimension(68, 16));
        tfSpeedUp.setName("tfSpeedUp"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 30;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        plInfoCommon.add(tfSpeedUp, gridBagConstraints);

        jLabel19.setText(resourceMap.getString("jLabel19.text")); // NOI18N
        jLabel19.setName("jLabel19"); // NOI18N
        jLabel19.setPreferredSize(new java.awt.Dimension(72, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        plInfoCommon.add(jLabel19, gridBagConstraints);

        tfCreator.setEditable(false);
        tfCreator.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("tfCreator.border.lineColor"))); // NOI18N
        tfCreator.setMinimumSize(new java.awt.Dimension(68, 16));
        tfCreator.setName("tfCreator"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 30;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        plInfoCommon.add(tfCreator, gridBagConstraints);

        tfErrorInfo.setEditable(false);
        tfErrorInfo.setForeground(resourceMap.getColor("tfErrorInfo.foreground")); // NOI18N
        tfErrorInfo.setText(null);
        tfErrorInfo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tfErrorInfo.setName("tfErrorInfo"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        plInfoCommon.add(tfErrorInfo, gridBagConstraints);

        jLabel15.setText(resourceMap.getString("jLabel15.text")); // NOI18N
        jLabel15.setName("jLabel15"); // NOI18N
        jLabel15.setPreferredSize(new java.awt.Dimension(72, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        plInfoCommon.add(jLabel15, gridBagConstraints);

        plInfo.add(plInfoCommon, java.awt.BorderLayout.CENTER);

        jPanel9.setName("jPanel9"); // NOI18N
        jPanel9.setPreferredSize(new java.awt.Dimension(644, 56));

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        plPieces.setBackground(resourceMap.getColor("plPieces.background")); // NOI18N
        plPieces.setName("plPieces"); // NOI18N
        plPieces.setLayout(new java.awt.BorderLayout());

        lbProgress.setText(resourceMap.getString("lbProgress.text")); // NOI18N
        lbProgress.setName("lbProgress"); // NOI18N

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(plPieces, javax.swing.GroupLayout.DEFAULT_SIZE, 729, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(lbProgress)
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jLabel1))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(lbProgress))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(plPieces, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)))
                .addContainerGap())
        );

        lbProgress.getAccessibleContext().setAccessibleName(resourceMap.getString("lbProgress.AccessibleContext.accessibleName")); // NOI18N

        plInfo.add(jPanel9, java.awt.BorderLayout.NORTH);

        tpTorrentDetails.addTab(resourceMap.getString("plInfo.TabConstraints.tabTitle"), plInfo); // NOI18N

        plFiles.setName("plFiles"); // NOI18N
        plFiles.setLayout(new java.awt.BorderLayout());

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        tblTorrentFiles.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Path", "Type", "Complete", "Done", "Size"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblTorrentFiles.setName("tblTorrentFiles"); // NOI18N
        tblTorrentFiles.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(tblTorrentFiles);
        tblTorrentFiles.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblTorrentFiles.columnModel.title0")); // NOI18N
        tblTorrentFiles.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblTorrentFiles.columnModel.title1")); // NOI18N
        tblTorrentFiles.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblTorrentFiles.columnModel.title2")); // NOI18N
        tblTorrentFiles.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblTorrentFiles.columnModel.title3")); // NOI18N
        tblTorrentFiles.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblTorrentFiles.columnModel.title4")); // NOI18N

        plFiles.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        tpTorrentDetails.addTab(resourceMap.getString("plFiles.TabConstraints.tabTitle"), plFiles); // NOI18N

        plPeers.setName("plPeers"); // NOI18N
        plPeers.setLayout(new java.awt.BorderLayout());

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        tblTorrentPeers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "IP", "Country", "Flags", "Client", "Port", "Progress", "Dn rate", "Up rate"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblTorrentPeers.setName("tblTorrentPeers"); // NOI18N
        tblTorrentPeers.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tblTorrentPeers);
        tblTorrentPeers.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblTorrentPeers.columnModel.title0")); // NOI18N
        tblTorrentPeers.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblTorrentPeers.columnModel.title1")); // NOI18N
        tblTorrentPeers.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblTorrentPeers.columnModel.title2")); // NOI18N
        tblTorrentPeers.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblTorrentPeers.columnModel.title3")); // NOI18N
        tblTorrentPeers.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblTorrentPeers.columnModel.title4")); // NOI18N
        tblTorrentPeers.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("tblTorrentPeers.columnModel.title5")); // NOI18N
        tblTorrentPeers.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("tblTorrentPeers.columnModel.title6")); // NOI18N
        tblTorrentPeers.getColumnModel().getColumn(7).setHeaderValue(resourceMap.getString("tblTorrentPeers.columnModel.title7")); // NOI18N

        plPeers.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        tpTorrentDetails.addTab(resourceMap.getString("plPeers.TabConstraints.tabTitle"), plPeers); // NOI18N

        plTrackers.setName("plTrackers"); // NOI18N

        javax.swing.GroupLayout plTrackersLayout = new javax.swing.GroupLayout(plTrackers);
        plTrackers.setLayout(plTrackersLayout);
        plTrackersLayout.setHorizontalGroup(
            plTrackersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 884, Short.MAX_VALUE)
        );
        plTrackersLayout.setVerticalGroup(
            plTrackersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 233, Short.MAX_VALUE)
        );

        tpTorrentDetails.addTab(resourceMap.getString("plTrackers.TabConstraints.tabTitle"), plTrackers); // NOI18N

        plSpeed.setName("plSpeed"); // NOI18N
        plSpeed.setLayout(new java.awt.BorderLayout());
        tpTorrentDetails.addTab(resourceMap.getString("plSpeed.TabConstraints.tabTitle"), plSpeed); // NOI18N

        plLog.setName("plLog"); // NOI18N
        plLog.setLayout(new java.awt.BorderLayout());

        spLog.setName("spLog"); // NOI18N

        taLog.setColumns(20);
        taLog.setRows(5);
        taLog.setName("taLog"); // NOI18N
        spLog.setViewportView(taLog);

        plLog.add(spLog, java.awt.BorderLayout.CENTER);

        tpTorrentDetails.addTab(resourceMap.getString("plLog.TabConstraints.tabTitle"), plLog); // NOI18N

        plTorrentInfo.add(tpTorrentDetails, java.awt.BorderLayout.CENTER);

        spMain.setRightComponent(plTorrentInfo);

        mainPanel.add(spMain, java.awt.BorderLayout.CENTER);

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(net.sf.dvstar.transmission.TransmissionApp.class).getContext().getActionMap(TransmissionView.class, this);
        miFileConnect.setAction(actionMap.get("doConnect")); // NOI18N
        miFileConnect.setIcon(resourceMap.getIcon("miFileConnect.icon")); // NOI18N
        miFileConnect.setText(resourceMap.getString("miFileConnect.text")); // NOI18N
        miFileConnect.setName("miFileConnect"); // NOI18N
        fileMenu.add(miFileConnect);

        jSeparator3.setName("jSeparator3"); // NOI18N
        fileMenu.add(jSeparator3);

        miFileQuickAddFile.setAction(actionMap.get("doAddTorrentQuick")); // NOI18N
        miFileQuickAddFile.setIcon(resourceMap.getIcon("miFileQuickAddFile.icon")); // NOI18N
        miFileQuickAddFile.setText(resourceMap.getString("miFileQuickAddFile.text")); // NOI18N
        miFileQuickAddFile.setName("miFileQuickAddFile"); // NOI18N
        fileMenu.add(miFileQuickAddFile);

        miFileExtAddFile.setAction(actionMap.get("doAddTorrentExt")); // NOI18N
        miFileExtAddFile.setIcon(resourceMap.getIcon("miFileExtAddFile.icon")); // NOI18N
        miFileExtAddFile.setText(resourceMap.getString("miFileExtAddFile.text")); // NOI18N
        miFileExtAddFile.setName("miFileExtAddFile"); // NOI18N
        fileMenu.add(miFileExtAddFile);

        miFileInfo.setAction(actionMap.get("doTorrentInfo")); // NOI18N
        miFileInfo.setIcon(resourceMap.getIcon("miFileInfo.icon")); // NOI18N
        miFileInfo.setText(resourceMap.getString("miFileInfo.text")); // NOI18N
        miFileInfo.setName("miFileInfo"); // NOI18N
        fileMenu.add(miFileInfo);

        miFileAddURL.setIcon(resourceMap.getIcon("miFileAddURL.icon")); // NOI18N
        miFileAddURL.setText(resourceMap.getString("miFileAddURL.text")); // NOI18N
        miFileAddURL.setName("miFileAddURL"); // NOI18N
        fileMenu.add(miFileAddURL);

        jSeparator2.setName("jSeparator2"); // NOI18N
        fileMenu.add(jSeparator2);

        miFileExit.setAction(actionMap.get("doQuit")); // NOI18N
        miFileExit.setIcon(resourceMap.getIcon("miFileExit.icon")); // NOI18N
        miFileExit.setName("miFileExit"); // NOI18N
        fileMenu.add(miFileExit);

        menuBar.add(fileMenu);

        configMenu.setText(resourceMap.getString("configMenu.text")); // NOI18N
        configMenu.setName("configMenu"); // NOI18N

        miConfigClient.setAction(actionMap.get("doConfigClient")); // NOI18N
        miConfigClient.setIcon(resourceMap.getIcon("miConfigClient.icon")); // NOI18N
        miConfigClient.setText(resourceMap.getString("miConfigClient.text")); // NOI18N
        miConfigClient.setName("miConfigClient"); // NOI18N
        configMenu.add(miConfigClient);

        miConfigServer.setAction(actionMap.get("doConfigServer")); // NOI18N
        miConfigServer.setIcon(resourceMap.getIcon("miConfigServer.icon")); // NOI18N
        miConfigServer.setText(resourceMap.getString("miConfigServer.text")); // NOI18N
        miConfigServer.setName("miConfigServer"); // NOI18N
        configMenu.add(miConfigServer);

        jSeparator1.setName("jSeparator1"); // NOI18N
        configMenu.add(jSeparator1);

        mnConfigLocale.setIcon(resourceMap.getIcon("mnConfigLocale.icon")); // NOI18N
        mnConfigLocale.setText(resourceMap.getString("mnConfigLocale.text")); // NOI18N
        mnConfigLocale.setName("mnConfigLocale"); // NOI18N
        configMenu.add(mnConfigLocale);

        menuBar.add(configMenu);

        torrentMenu.setAction(actionMap.get("doMoveTorrent")); // NOI18N
        torrentMenu.setText(resourceMap.getString("torrentMenu.text")); // NOI18N
        torrentMenu.setName("torrentMenu"); // NOI18N

        miTorrentStart.setAction(actionMap.get("doStartTorrent")); // NOI18N
        miTorrentStart.setIcon(resourceMap.getIcon("miTorrentStart.icon")); // NOI18N
        miTorrentStart.setText(resourceMap.getString("miTorrentStart.text")); // NOI18N
        miTorrentStart.setName("miTorrentStart"); // NOI18N
        torrentMenu.add(miTorrentStart);

        miTorrentStop.setAction(actionMap.get("doPauseTorrent")); // NOI18N
        miTorrentStop.setIcon(resourceMap.getIcon("miTorrentStop.icon")); // NOI18N
        miTorrentStop.setText(resourceMap.getString("miTorrentStop.text")); // NOI18N
        miTorrentStop.setName("miTorrentStop"); // NOI18N
        torrentMenu.add(miTorrentStop);

        miTorrentRefresh.setAction(actionMap.get("doRefresh")); // NOI18N
        miTorrentRefresh.setIcon(resourceMap.getIcon("miTorrentRefresh.icon")); // NOI18N
        miTorrentRefresh.setText(resourceMap.getString("miTorrentRefresh.text")); // NOI18N
        miTorrentRefresh.setName("miTorrentRefresh"); // NOI18N
        torrentMenu.add(miTorrentRefresh);

        miTorrentCheck.setIcon(resourceMap.getIcon("miTorrentCheck.icon")); // NOI18N
        miTorrentCheck.setText(resourceMap.getString("miTorrentCheck.text")); // NOI18N
        miTorrentCheck.setName("miTorrentCheck"); // NOI18N
        torrentMenu.add(miTorrentCheck);

        miTorrentProperties.setAction(actionMap.get("doTorrentProperties")); // NOI18N
        miTorrentProperties.setIcon(resourceMap.getIcon("miTorrentProperties.icon")); // NOI18N
        miTorrentProperties.setText(resourceMap.getString("miTorrentProperties.text")); // NOI18N
        miTorrentProperties.setName("miTorrentProperties"); // NOI18N
        torrentMenu.add(miTorrentProperties);

        jSeparator11.setName("jSeparator11"); // NOI18N
        torrentMenu.add(jSeparator11);

        miTorrentDelete.setIcon(resourceMap.getIcon("miTorrentDelete.icon")); // NOI18N
        miTorrentDelete.setText(resourceMap.getString("miTorrentDelete.text")); // NOI18N
        miTorrentDelete.setName("miTorrentDelete"); // NOI18N
        torrentMenu.add(miTorrentDelete);

        miTorrentDeleteAll.setIcon(resourceMap.getIcon("miTorrentDeleteAll.icon")); // NOI18N
        miTorrentDeleteAll.setText(resourceMap.getString("miTorrentDeleteAll.text")); // NOI18N
        miTorrentDeleteAll.setName("miTorrentDeleteAll"); // NOI18N
        torrentMenu.add(miTorrentDeleteAll);

        jSeparator12.setName("jSeparator12"); // NOI18N
        torrentMenu.add(jSeparator12);

        miTorrentAnnounce.setIcon(resourceMap.getIcon("miTorrentAnnounce.icon")); // NOI18N
        miTorrentAnnounce.setText(resourceMap.getString("miTorrentAnnounce.text")); // NOI18N
        miTorrentAnnounce.setName("miTorrentAnnounce"); // NOI18N
        torrentMenu.add(miTorrentAnnounce);

        miTorrentMove.setAction(actionMap.get("doMoveTorrent")); // NOI18N
        miTorrentMove.setIcon(resourceMap.getIcon("miTorrentMove.icon")); // NOI18N
        miTorrentMove.setText(resourceMap.getString("miTorrentMove.text")); // NOI18N
        miTorrentMove.setName("miTorrentMove"); // NOI18N
        torrentMenu.add(miTorrentMove);

        miTorrentLocation.setIcon(resourceMap.getIcon("miTorrentLocation.icon")); // NOI18N
        miTorrentLocation.setText(resourceMap.getString("miTorrentLocation.text")); // NOI18N
        miTorrentLocation.setName("miTorrentLocation"); // NOI18N
        torrentMenu.add(miTorrentLocation);

        jSeparator4.setName("jSeparator4"); // NOI18N
        torrentMenu.add(jSeparator4);

        miTorrentStartAll.setIcon(resourceMap.getIcon("miTorrentStartAll.icon")); // NOI18N
        miTorrentStartAll.setText(resourceMap.getString("miTorrentStartAll.text")); // NOI18N
        miTorrentStartAll.setName("miTorrentStartAll"); // NOI18N
        torrentMenu.add(miTorrentStartAll);

        miTorrentStopAll.setIcon(resourceMap.getIcon("miTorrentStopAll.icon")); // NOI18N
        miTorrentStopAll.setText(resourceMap.getString("miTorrentStopAll.text")); // NOI18N
        miTorrentStopAll.setName("miTorrentStopAll"); // NOI18N
        torrentMenu.add(miTorrentStopAll);

        menuBar.add(torrentMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        miHelpAbout.setAction(actionMap.get("showAboutBox")); // NOI18N
        miHelpAbout.setIcon(resourceMap.getIcon("miHelpAbout.icon")); // NOI18N
        miHelpAbout.setName("miHelpAbout"); // NOI18N
        helpMenu.add(miHelpAbout);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 891, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 707, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        mainToolBar.setRollover(true);
        mainToolBar.setName("mainToolBar"); // NOI18N

        btConnect.setAction(actionMap.get("doConnect")); // NOI18N
        btConnect.setIcon(resourceMap.getIcon("btConnect.icon")); // NOI18N
        btConnect.setFocusable(false);
        btConnect.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btConnect.setMinimumSize(new java.awt.Dimension(0, 0));
        btConnect.setName("btConnect"); // NOI18N
        btConnect.setPreferredSize(new java.awt.Dimension(45, 43));
        btConnect.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolBar.add(btConnect);

        jSeparator5.setName("jSeparator5"); // NOI18N
        jSeparator5.setSeparatorSize(new java.awt.Dimension(5, 40));
        mainToolBar.add(jSeparator5);

        btInfo.setAction(actionMap.get("doTorrentInfo")); // NOI18N
        btInfo.setIcon(resourceMap.getIcon("btInfo.icon")); // NOI18N
        btInfo.setText(resourceMap.getString("btInfo.text")); // NOI18N
        btInfo.setToolTipText(resourceMap.getString("btInfo.toolTipText")); // NOI18N
        btInfo.setFocusable(false);
        btInfo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btInfo.setName("btInfo"); // NOI18N
        btInfo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolBar.add(btInfo);

        jSeparator10.setName("jSeparator10"); // NOI18N
        jSeparator10.setSeparatorSize(new java.awt.Dimension(5, 40));
        mainToolBar.add(jSeparator10);

        btAdd.setAction(actionMap.get("doAddTorrentExt")); // NOI18N
        btAdd.setIcon(resourceMap.getIcon("btAdd.icon")); // NOI18N
        btAdd.setToolTipText(resourceMap.getString("btAdd.toolTipText")); // NOI18N
        btAdd.setFocusable(false);
        btAdd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btAdd.setName("btAdd"); // NOI18N
        btAdd.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolBar.add(btAdd);

        btAddUrl.setAction(actionMap.get("addTorentURL")); // NOI18N
        btAddUrl.setIcon(resourceMap.getIcon("btAddUrl.icon")); // NOI18N
        btAddUrl.setToolTipText(resourceMap.getString("btAddUrl.toolTipText")); // NOI18N
        btAddUrl.setFocusable(false);
        btAddUrl.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btAddUrl.setName("btAddUrl"); // NOI18N
        btAddUrl.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolBar.add(btAddUrl);

        jSeparator8.setName("jSeparator8"); // NOI18N
        jSeparator8.setSeparatorSize(new java.awt.Dimension(5, 40));
        mainToolBar.add(jSeparator8);

        btStart.setAction(actionMap.get("doStartTorrent")); // NOI18N
        btStart.setIcon(resourceMap.getIcon("btStart.icon")); // NOI18N
        btStart.setToolTipText(resourceMap.getString("btStart.toolTipText")); // NOI18N
        btStart.setFocusable(false);
        btStart.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btStart.setName("btStart"); // NOI18N
        btStart.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolBar.add(btStart);

        btStop.setAction(actionMap.get("doStopTorrent")); // NOI18N
        btStop.setIcon(resourceMap.getIcon("btStop.icon")); // NOI18N
        btStop.setToolTipText(resourceMap.getString("btStop.toolTipText")); // NOI18N
        btStop.setFocusable(false);
        btStop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btStop.setName("btStop"); // NOI18N
        btStop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolBar.add(btStop);

        btRefresh.setAction(actionMap.get("doRefresh")); // NOI18N
        btRefresh.setIcon(resourceMap.getIcon("btRefresh.icon")); // NOI18N
        btRefresh.setFocusable(false);
        btRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btRefresh.setName("btRefresh"); // NOI18N
        btRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolBar.add(btRefresh);

        jSeparator9.setName("jSeparator9"); // NOI18N
        jSeparator9.setSeparatorSize(new java.awt.Dimension(5, 40));
        mainToolBar.add(jSeparator9);

        btStatistic.setAction(actionMap.get("doStatisticDialog")); // NOI18N
        btStatistic.setIcon(resourceMap.getIcon("btStatistic.icon")); // NOI18N
        btStatistic.setToolTipText(resourceMap.getString("btStatistic.toolTipText")); // NOI18N
        btStatistic.setFocusable(false);
        btStatistic.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btStatistic.setName("btStatistic"); // NOI18N
        btStatistic.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolBar.add(btStatistic);

        jSeparator6.setName("jSeparator6"); // NOI18N
        jSeparator6.setSeparatorSize(new java.awt.Dimension(5, 40));
        mainToolBar.add(jSeparator6);

        btConfigCli.setAction(actionMap.get("doConfigClient")); // NOI18N
        btConfigCli.setIcon(resourceMap.getIcon("btConfigCli.icon")); // NOI18N
        btConfigCli.setFocusable(false);
        btConfigCli.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btConfigCli.setName("btConfigCli"); // NOI18N
        btConfigCli.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolBar.add(btConfigCli);

        jSeparator7.setName("jSeparator7"); // NOI18N
        jSeparator7.setSeparatorSize(new java.awt.Dimension(5, 40));
        mainToolBar.add(jSeparator7);

        btExit.setAction(actionMap.get("doQuit")); // NOI18N
        btExit.setIcon(resourceMap.getIcon("btExit.icon")); // NOI18N
        btExit.setText(resourceMap.getString("btExit.text")); // NOI18N
        btExit.setFocusable(false);
        btExit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btExit.setName("btExit"); // NOI18N
        btExit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolBar.add(btExit);

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
        setToolBar(mainToolBar);
    }// </editor-fold>//GEN-END:initComponents

    private void cbFilterStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbFilterStatusActionPerformed
        setTorrentListFilter();
    }//GEN-LAST:event_cbFilterStatusActionPerformed

    private void cbRegistryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbRegistryActionPerformed
        setTorrentListFilter();
    }//GEN-LAST:event_cbRegistryActionPerformed

    private void lbClearSelectionMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbClearSelectionMouseClicked
        tfFindItem.setText("");
    }//GEN-LAST:event_lbClearSelectionMouseClicked

    private void btSetTorrentGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSetTorrentGroupActionPerformed

        String message = String.format(globalResourceMap.getString("message.common.notimplemented"), "btSetTorrentGroupActionPerformed");
        String title = globalResourceMap.getString("message.common.title.warning");
        JOptionPane.showMessageDialog(transmissionView.getFrame(), message, title, JOptionPane.INFORMATION_MESSAGE);

    }//GEN-LAST:event_btSetTorrentGroupActionPerformed

    private void btFindActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btFindActionPerformed
        ExtFilterDialog dialog = new ExtFilterDialog(this, true);
        dialog.setLocationRelativeTo(this.getFrame());
        TransmissionApp.getApplication().show(dialog);
        if (dialog.getTorrentFilterDesc() != null) {
            torrentFilterDesc = dialog.getTorrentFilterDesc();
            setTorrentListFilter();
        }
        /*
        String message = String.format(globalResourceMap.getString("message.common.notimplemented"), "btFindActionPerformed");
        String title = globalResourceMap.getString("message.common.title.warning");
        JOptionPane.showMessageDialog(transmissionView.getFrame(), message, title, JOptionPane.INFORMATION_MESSAGE);
         */
    }//GEN-LAST:event_btFindActionPerformed

    private void btConfigTorrentGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btConfigTorrentGroupActionPerformed
        String message = String.format(globalResourceMap.getString("message.common.notimplemented"), "btConfigTorrentGroupActionPerformed");
        String title = globalResourceMap.getString("message.common.title.warning");
        JOptionPane.showMessageDialog(transmissionView.getFrame(), message, title, JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btConfigTorrentGroupActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAdd;
    private javax.swing.JButton btAddUrl;
    private javax.swing.JButton btConfigCli;
    private javax.swing.JButton btConfigTorrentGroup;
    private javax.swing.JButton btConnect;
    private javax.swing.JButton btExit;
    private javax.swing.JButton btFind;
    private javax.swing.JButton btFirst;
    private javax.swing.JButton btInfo;
    private javax.swing.JButton btLast;
    private javax.swing.JButton btNext;
    private javax.swing.JButton btPrev;
    private javax.swing.JButton btRefresh;
    private javax.swing.JButton btSetTorrentGroup;
    private javax.swing.JButton btStart;
    private javax.swing.JButton btStatistic;
    private javax.swing.JButton btStop;
    private javax.swing.JComboBox cbFilterStatus;
    private javax.swing.JCheckBox cbRegistry;
    private javax.swing.JComboBox cbTorrentGroup;
    private javax.swing.JMenu configMenu;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator10;
    private javax.swing.JSeparator jSeparator11;
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JToolBar.Separator jSeparator7;
    private javax.swing.JToolBar.Separator jSeparator8;
    private javax.swing.JToolBar.Separator jSeparator9;
    private javax.swing.JLabel lbClearSelection;
    private javax.swing.JLabel lbErrorInfo;
    private javax.swing.JLabel lbFindInfo;
    private javax.swing.JLabel lbProgress;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JToolBar mainToolBar;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem miConfigClient;
    private javax.swing.JMenuItem miConfigServer;
    private javax.swing.JMenuItem miFileAddURL;
    private javax.swing.JMenuItem miFileConnect;
    private javax.swing.JMenuItem miFileExtAddFile;
    private javax.swing.JMenuItem miFileInfo;
    private javax.swing.JMenuItem miFileQuickAddFile;
    private javax.swing.JMenuItem miTorrentAnnounce;
    private javax.swing.JMenuItem miTorrentCheck;
    private javax.swing.JMenuItem miTorrentDelete;
    private javax.swing.JMenuItem miTorrentDeleteAll;
    private javax.swing.JMenuItem miTorrentLocation;
    private javax.swing.JMenuItem miTorrentMove;
    private javax.swing.JMenuItem miTorrentProperties;
    private javax.swing.JMenuItem miTorrentRefresh;
    private javax.swing.JMenuItem miTorrentStart;
    private javax.swing.JMenuItem miTorrentStartAll;
    private javax.swing.JMenuItem miTorrentStop;
    private javax.swing.JMenuItem miTorrentStopAll;
    private javax.swing.JMenu mnConfigLocale;
    private javax.swing.JPanel plFiles;
    private javax.swing.JPanel plInfo;
    private javax.swing.JPanel plInfoCommon;
    private javax.swing.JPanel plLog;
    private javax.swing.JPanel plPeers;
    private javax.swing.JPanel plPieces;
    private javax.swing.JPanel plSpeed;
    private javax.swing.JPanel plTorrentInfo;
    private javax.swing.JPanel plTorrentList;
    private javax.swing.JPanel plTrackers;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JScrollPane spLog;
    private javax.swing.JSplitPane spMain;
    private javax.swing.JScrollPane spTorrentList;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JTextArea taLog;
    private javax.swing.JTable tblTorrentFiles;
    private javax.swing.JTable tblTorrentList;
    private javax.swing.JTable tblTorrentPeers;
    private javax.swing.JTextField tfComment;
    private javax.swing.JTextField tfCreatedAt;
    private javax.swing.JTextField tfCreator;
    private javax.swing.JTextField tfCurrentRow;
    private javax.swing.JTextField tfDownloaded;
    private javax.swing.JTextField tfErrorInfo;
    private javax.swing.JTextField tfFindItem;
    private javax.swing.JTextField tfLeechers;
    private javax.swing.JTextField tfRate;
    private javax.swing.JTextField tfRemaining;
    private javax.swing.JTextField tfSeeds;
    private javax.swing.JTextField tfSpeedDn;
    private javax.swing.JTextField tfSpeedDn3;
    private javax.swing.JTextField tfSpeedUp;
    private javax.swing.JTextField tfStartedAt;
    private javax.swing.JTextField tfState;
    private javax.swing.JTextField tfStorePath;
    private javax.swing.JTextField tfTimeAll;
    private javax.swing.JTextField tfTimeAll1;
    private javax.swing.JTextField tfUploaded;
    private javax.swing.JMenu torrentMenu;
    private javax.swing.JTabbedPane tpTorrentDetails;
    // End of variables declaration//GEN-END:variables
    private Timer messageTimer;
    private Timer busyIconTimer;
    private Timer updaterTimer;
    private Timer refreshTimer;
    private Timer speedoTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private Task taskDoUpdateTorrentDetails = null;
    private int selectedTorrentRows[] = null;

    /**
     * Restore selected row after refreshing table data
     * @param srows
     */
    private void restoreRowsSelections(int srows[]) {
        lockValueChanged = true;

        /*
        int rcs = tblTorrentList.getRowSorter().getViewRowCount();
        int rca = tblTorrentList.getRowSorter().getModelRowCount();

        if(rcs != rca) return;

         */

        tracePrint(true, "[restoreRowsSelections] " + Tools.printArray(srows));
        //tracePrint(true, "[restoreRowsSelections] " + Tools.showCallStack());

        ListSelectionModel model = tblTorrentList.getSelectionModel();

        if (srows != null && srows.length > 0) {
            for (int i = 0; i < srows.length; i++) {
                int j = srows[i];
                //j = tblTorrentList.convertRowIndexToModel(srows[i]);
                //j = tblTorrentList.getRowSorter().convertRowIndexToModel(srows[i]);
                model.addSelectionInterval(j, j);
                {
                    //tblTorrentList.getSelectionModel().removeListSelectionListener(tblTorrentList);
                    //enableListSelectionEvent = false;
                    //tblTorrentList.setSelectionModel(model);
                    //enableListSelectionEvent = true;
                    //tblTorrentList.getSelectionModel().addListSelectionListener(torrentsSelectionListener);
                }
            }
            //tblTorrentList.setSelectionModel(model);
        }
        lockValueChanged = false;
    }

    /**
     * Update pieces graph for torrent parts
     * @param modelRow
     */
    private void updatePiecesGraph(final int modelRow) {

        Torrent torrent = modelTorrentsList.getTableDataTorrents().get(modelRow);

        if (torrent.getDoneProgress() >= 100.0) {
            piecesGraph.setFull(true);
            piecesGraph.repaint();
            return;
        } else {
            piecesGraph.clearBits();
            piecesGraph.setFull(false);
        }

        if (torrent.getPiecesArray() != null) {
            //piecesGraph.clearBits();
            piecesGraph.applyBits(torrent.getPiecesArray(), torrent.getPiecesCount());
            piecesGraph.repaint();
        } else {
            piecesGraph.clearBits();
            piecesGraph.setFull(false);
        }
    }
    private JSONMapModelFiles modelTorrentsFiles = null;
    private JSONMapModelPeers modelTorrentsPeers = null;

    /**
     * Update Files and Peeer table for torrent
     * @param modelRowIndex - row index in model
     */
    private void updateFilesAndPeersBox(final int modelRowIndex) {
        String names[] = null;
        ColumnDescriptor columnDescriptor[] = null;
        TableCellRenderer customRenderer;
        TableCellRenderer customBoolean;

        Torrent torrent = null;
        if (modelRowIndex >= 0) {
            torrent = modelTorrentsList.getTableDataTorrents().get(modelRowIndex);
        }

        names = FilesFieldInterface.names;
        String snames;
        if ((snames = globalResourceMap.getString("tblTorrentFiles.columnModel.titles")) != null) {
            names = Tools.getStringArray(snames, ","); // add param - count fields default
        }
        ColumnsDescriptor columnsDescriptor;
        if (modelTorrentsFiles == null) {
            columnDescriptor = new ColumnDescriptor[]{
                        new ColumnDescriptor(1, String.class, names[0], FilesFieldInterface.fields[0]), new ColumnDescriptor(2, String.class, names[1], FilesFieldInterface.fields[1]), new ColumnDescriptor(3, String.class, names[2], FilesFieldInterface.fields[2])// VISUAL
                        , new ColumnDescriptor(4, String.class, names[3], FilesFieldInterface.fields[3])// VISUAL
                        , new ColumnDescriptor(5, String.class, names[4], FilesFieldInterface.fields[4]) // VISUAL
                        , new ColumnDescriptor(6, Boolean.class, names[5], FilesFieldInterface.fields[5]) // VISUAL
                        , new ColumnDescriptor(6, String.class, names[6], FilesFieldInterface.fields[6]) // VISUAL
                    };

            columnsDescriptor = new ColumnsDescriptor(columnDescriptor);

            modelTorrentsFiles = new JSONMapModelFiles(this, torrent, columnsDescriptor);
            tblTorrentFiles.setModel(modelTorrentsFiles);
            customRenderer = new TorrentsTableCustomRenderer((TorrentsTableModel) modelTorrentsList, false);
            tblTorrentFiles.setDefaultRenderer(Object.class, customRenderer);
            tblTorrentFiles.setDefaultRenderer(Number.class, customRenderer);

            TableCellRenderer rend = tblTorrentFiles.getDefaultRenderer(Boolean.class);
            rend.toString();

            customBoolean = new TorrentsTableBooleanRenderer((TorrentsTableModel) modelTorrentsList, false);
            tblTorrentFiles.setDefaultRenderer(Boolean.class, customBoolean);

            //!!tblTorrentFiles.tableChanged(new TableModelEvent(modelTorrentsFiles));
            tblTorrentFiles.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            tblTorrentFiles.setCellSelectionEnabled(false);
            tblTorrentFiles.setRowSelectionAllowed(true);
        } else {
            modelTorrentsFiles.setTorrent(torrent);
        }
        if (torrent != null && torrent.getFilesDescList() != null && torrent.getFilesDescList().size() > 0) {
            tblFileListPopupMenu.setEnabled(true);
        } else {
            tblFileListPopupMenu.setEnabled(false);
        }

        tblTorrentFiles.revalidate();

        names = PeersFieldInterface.names;
        if ((snames = globalResourceMap.getString("tblTorrentPeers.columnModel.titles")) != null) {
            names = Tools.getStringArray(snames, ",");
        }

        if (modelTorrentsPeers == null) {
            columnDescriptor = new ColumnDescriptor[]{
                        new ColumnDescriptor(1, String.class, names[0], PeersFieldInterface.fields[0]),
                        new ColumnDescriptor(2, String.class, names[1], PeersFieldInterface.fields[1]),
                        new ColumnDescriptor(3, String.class, names[2], PeersFieldInterface.fields[2]),
                        new ColumnDescriptor(4, String.class, names[3], PeersFieldInterface.fields[3]), // VISUAL
                        new ColumnDescriptor(5, String.class, names[4], PeersFieldInterface.fields[4]), // VISUAL
                        new ColumnDescriptor(6, String.class, names[5], PeersFieldInterface.fields[5]), // VISUAL
                        new ColumnDescriptor(7, String.class, names[6], PeersFieldInterface.fields[6]), // VISUAL
                        new ColumnDescriptor(8, String.class, names[7], PeersFieldInterface.fields[7]) // VISUAL
                    };

            columnsDescriptor = new ColumnsDescriptor(columnDescriptor);

            modelTorrentsPeers =
                    new JSONMapModelPeers(this, torrent, columnsDescriptor);
            tblTorrentPeers.setModel(modelTorrentsPeers);
            customRenderer = new TorrentsTableCustomRenderer((TorrentsTableModel) modelTorrentsList, false);
            tblTorrentPeers.setDefaultRenderer(Object.class, customRenderer);
            tblTorrentPeers.setDefaultRenderer(Number.class, customRenderer);
            //!!tblTorrentPeers.tableChanged(new TableModelEvent(modelTorrentsPeers));
            tblTorrentPeers.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            tblTorrentPeers.setCellSelectionEnabled(false);
            tblTorrentPeers.setRowSelectionAllowed(true);
        } else {
            modelTorrentsPeers.setTorrent(torrent);
        }
        tblTorrentPeers.revalidate();

        tracePrint(true, "[updateFilesAndPeersBox][files]=" + modelTorrentsFiles.getRowCount() + " [peers]=" + modelTorrentsPeers.getRowCount());


    }

    private synchronized void updateInfoBox(final int modelRow) {
        // update pieces graph
        lbErrorInfo.setVisible(false);
        tfErrorInfo.setVisible(false);
        if (modelRow >= 0) {
            Torrent torrent = modelTorrentsList.getTableDataTorrents().get(modelRow);
            if (torrent != null) {
                ((TitledBorder) plInfoCommon.getBorder()).setTitle(torrent.getTorrentName());
                //piecesGraph.clearBits();

                tfTimeAll.setText(torrent.getElapsedTimeString()); //
                tfDownloaded.setText(Tools.getFileSize(torrent.getHaveValid().floatValue())); //Value(FIELD_HAVEVALID_VIS).toString());
                tfUploaded.setText(Tools.getFileSize(torrent.getUpload().floatValue()));     // torrent.getValue(FIELD_UPLOADEDEVER).toString());

                tfRemaining.setText(torrent.getRemainingTimeString());

                tfCreator.setText(torrent.getCreator());                                     //Value(FIELD_CREATOR).toString());
                tfCreatedAt.setText(TimeSpan.getDateFromEpochStr(torrent.getDateCreated()));      //Value(FIELD_DATECREATED)));
                tfStartedAt.setText(TimeSpan.getDateFromEpochStr(torrent.getDateAdded()));        //Value(FIELD_ADDEDDATE)));

//                tfSpeedDn.setText(Tools.getVisibleSpeed(torrent.getRateDn().floatValue()));// Value(FIELD_RATEDOWNLOAD_VIS).toString());
//                tfSpeedUp.setText(Tools.getVisibleSpeed(torrent.getRateUp().floatValue()));//Value(FIELD_RATEUPLOAD_VIS).toString());
                tfSpeedDn.setText(Tools.getVisibleSpeed(torrent.getRateDn().floatValue()));// Value(FIELD_RATEDOWNLOAD_VIS).toString());
                tfSpeedUp.setText(Tools.getVisibleSpeed(torrent.getRateUp().floatValue()));//Value(FIELD_RATEUPLOAD_VIS).toString());

                tfState.setText(torrent.getStatusDesc());                           //Value(FIELD_STATUS).toString());

                tfComment.setText(torrent.getComment());                            //Value(FIELD_COMMENT).toString());
                tfComment.removeMouseListener(urlLMouseListener);
                tfComment.setForeground(Color.BLACK);
                if (tfComment.getText().startsWith("http")) {
                    urlLMouseListener = new URLMouseListener(tfComment.getText());
                    tfComment.addMouseListener(urlLMouseListener);
                    tfComment.setForeground(Color.BLUE);
                }

                tfStorePath.setText(torrent.getDownloadDir());                      //Value(FIELD_DOWNLOADDIR).toString());
                lbProgress.setText(Tools.getPercentage(torrent.getDoneProgress()));//Value(FIELD_DONEPROGRESS).toString());
                tfErrorInfo.setText(torrent.getErrorString());                      //Value(FIELD_ERRORSTRING).toString());
                if (torrent.isHasError()) {
                    lbErrorInfo.setVisible(true);
                    tfErrorInfo.setVisible(true);
                }
                plInfoCommon.repaint();
            }
        } else {
            ((TitledBorder) plInfoCommon.getBorder()).setTitle("...");
            tfTimeAll.setText("");
            tfDownloaded.setText("");
            tfUploaded.setText("");
            tfSpeedDn.setText("");
            tfState.setText("");
            tfComment.setText("");
            tfStorePath.setText("");
            lbProgress.setText("");
            piecesGraph.clearBits();
            plInfoCommon.repaint();
        }
    }

    private Torrent updateTorrentDetailsFromServer(int modelRow) {

        Requests req = new Requests(getGlobalLogger());
        Torrent torrent = null;

        if (modelTorrentsList.getTableDataTorrents() == null) {
            return null;
        }

        updatePiecesGraph(modelRow);
        updateFilesAndPeersBox(modelRow);

        List vrowsList = buildSelectModelList();

        JSONObject objReq = req.torrentGet(vrowsList, modelTorrentsList, METHOD_TORRENTGET_DTLS);
        JSONObject objRes = null;
        try {
            objRes = webClient.processWebRequest(objReq, "updateTorrentDetailsFromServer");
            torrent = modelTorrentsList.getTableDataTorrents().get(modelRow);
            torrent.parseResult(objRes, torrent.getId());
            modelTorrentsList.getTableDataTorrents().set(modelRow, torrent);
        } catch (TorrentsCommonException ex) {
            Exceptions.printStackTrace(ex);
        } catch (UnknownHostException ex) {
            System.err.println("1---------------------------------------------");
            Exceptions.printStackTrace(ex);
            System.err.println("1---------------------------------------------");
        } catch (java.net.SocketTimeoutException ex) {
            System.err.println("5---------------------------------------------");
            Exceptions.printStackTrace(ex);
            System.err.println("5---------------------------------------------");
        } catch (InterruptedIOException ex) {
            System.err.println("2---------------------------------------------");
            Exceptions.printStackTrace(ex);
            System.err.println("2---------------------------------------------");
        } catch (IOException ex) {
            System.err.println("3---------------------------------------------");
            Exceptions.printStackTrace(ex);
            System.err.println("3---------------------------------------------");
        } catch (HttpException ex) {
            System.err.println("4---------------------------------------------");
            Exceptions.printStackTrace(ex);
            System.err.println("4---------------------------------------------");
        }
        if (objRes != null) {
            updatePiecesGraph(modelRow);
            updateFilesAndPeersBox(modelRow);
        }
        return torrent;
    }

    /**
     * Update status line info with speed and size of session
     * @return string with status line
     */
    String updateStatusLine() {
//            String fmts = "%d B/s down, %d B/s up, %d torrents, %d dowloading, %d seeding D %d/ U %d ";
        String fmts = "%s down, %s up, %d torrents, %d active, %d paused, down %s , up %s ";

        fmts = globalResourceMap.getString("statusLine.info.format");
        String mess = "";
        if (!connectedServer) {
            return mess;
        }

        long start = System.currentTimeMillis();
        Requests req = new Requests(getGlobalLogger());
        {
            JSONObject request = req.sessionStats();
            try {
                JSONObject answer = webClient.processWebRequest(request, "updateStatusLine");
                if (answer != null) {
                    JSONObject args = answer.getJSONObject(KEY_ARGUMENTS);
                    JSONObject sessionStat = args.getJSONObject(FIELD_CURRENT_STATS);
                    mess = String.format(fmts,
                            //Long.parseLong(args.getString(FIELD_DOWNLOAD_SPEED)),
                            //Long.parseLong(args.getString(FIELD_UPLOAD_SPEED)),
                            Tools.getVisibleSpeed(args.getLong(FIELD_DOWNLOAD_SPEED)),
                            Tools.getVisibleSpeed(args.getLong(FIELD_UPLOAD_SPEED)),
                            Long.parseLong(args.getString(FIELD_TORRENT_COUNT)),
                            Long.parseLong(args.getString(FIELD_ACTIVE_TORRENT_COUNT)),
                            Long.parseLong(args.getString(FIELD_PAUSED_TORRENT_COUNT)),
                            Tools.getFileSize(sessionStat.getLong(FIELD_DOWNLOAD_BYTES)),
                            Tools.getFileSize(sessionStat.getLong(FIELD_UPLOAD_BYTES)));
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
            //Thread.sleep(100);
        }
        long stop = System.currentTimeMillis();
        System.out.println("[mess][" + (stop - start) + "][" + mess + "]");
        return mess;
    }

    /**
     * Build Json array for all selected torrents
     * @return array
     */
    private JSONArray buildIdJSONArray() {
        return buildIdJSONArray(false);
    }

    /**
     * Build Json array for selected torrents
     * @param started flad to indicate get only activ torrents
     * @return array
     */
    private JSONArray buildIdJSONArray(boolean started) {
        JSONArray ids = new JSONArray();
        {
            int rows[] = tblTorrentList.getSelectedRows();

            for (int i = 0; i < rows.length; i++) {
                //int modelRow = tblTorrentList.getRowSorter().convertRowIndexToModel(rows[i]);
                int modelRow = tblTorrentList.convertRowIndexToModel(rows[i]);
                Torrent torrent = modelTorrentsList.getTableDataTorrents().get(modelRow);
                if (started) {
                    if (torrent.getStatusCode() != STATUS_PAUSED) {
                        ids.add(torrent.getId());
                    }
                } else {
                    ids.add(torrent.getId());
                }
            }

            return ids;
        }
    }

    private List<Integer> buildViewModelList() {
        ArrayList ret = new ArrayList();

        int firstRow = 0, lastRow = 0;
        JViewport viewport = (JViewport) spTorrentList.getViewport();
        Rectangle viewRect = viewport.getViewRect();
        firstRow = tblTorrentList.rowAtPoint(new Point(0, viewRect.y));
        lastRow = tblTorrentList.rowAtPoint(new Point(0, viewRect.y + viewRect.height - 1));
        if (lastRow < 0) {
            lastRow = firstRow + tblTorrentList.getRowSorter().getViewRowCount() - 1;
        }
        /**
         * @todo   if rows < height - not update last row
         */
        tracePrint(true, "=-=-=-=-=-=-= tblTorrentList first=" + firstRow + " last=" + lastRow);

        for (int i = firstRow; i <= lastRow; i++) {
            ret.add(new Integer(tblTorrentList.convertRowIndexToModel(i)/* realConvertRowIndexToModel(i)*/));
        }

        return ret;
    }

    private List<Integer> buildSelectModelList() {
        ArrayList ret = new ArrayList();
        int rows[] = buildSelectModelArray();
        if (rows != null) {
            for (int i = 0; i < rows.length; i++) {
                ret.add(new Integer(rows[i]));
            }
        }
        return ret;
    }

    private int[] buildSelectModelArray() {
        int[] ids = null;
        {
            int rows[] = tblTorrentList.getSelectedRows();
            if (rows != null) {
                ids = new int[rows.length];
                for (int i = 0; i < rows.length; i++) {
                    int modelRow = tblTorrentList.convertRowIndexToModel(rows[i]);
                    ids[i] = modelRow;
                }
            }
        }
        return ids;
    }

    /**
     * Show dialog for select torrent file
     * @return selected file
     */
    private File[] getTorrentFile() {
        File files[] = null;
        File lastLoadDir = new File(globalConfigStorage.getProperty("last-load-dir", ".", true));
        JFileChooser fcOpenFile = new JFileChooser(lastLoadDir);
        fcOpenFile.setFileFilter(new TorrentFileFilter());
        fcOpenFile.setMultiSelectionEnabled(true);
        Tools.switchJFileChooserDetails(fcOpenFile.getComponents());
        int returnVal = fcOpenFile.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            files = fcOpenFile.getSelectedFiles();
            lastLoadDir = fcOpenFile.getSelectedFile().getParentFile();
            globalConfigStorage.setProperty("last-load-dir", lastLoadDir.toString());
            globalConfigStorage.saveConfig();
        }
        return files;
    }

    /**
     * Load metainfo from torrent file
     * @return metainfo class
     */
    private MetaInfo getTorrentFileMeta(File file) {

        MetaInfo metaFile = null;

        try {
            InputStream ist = new FileInputStream(file);

            metaFile = new MetaInfo(new BDecoder(ist));

            tracePrint(true, metaFile.toString());


        } catch (InterruptedIOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (UnknownHostException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
        }

        return metaFile;
    }

    private int getRealRowCount(JTable table) {
        int ret = table.getRowCount();
        if (table.getRowSorter() != null) {
            ret = table.getRowSorter().getViewRowCount();
        }

        return ret;
    }

    /**
     * Trace print
     * @param string message
     */
    public void tracePrint(boolean force, String string) {
        tracePrint(LOG_MODE_PRO, false, string);
    }

    /**
     * Trace print
     * @param force enable stdout
     * @param string message
     */
    public void tracePrint(int mode, boolean force, String string) {
        if (enableTraceOut || force) {
            System.out.println(Tools.printDateStamp() + " " + string);
        }
        if (globalLogger != null) {
            globalLogger.log(Level.INFO, Tools.printDateStamp() + " " + string);
        }

        if ((mode & LOG_MODE_LOG) > 0) {
            logPrint(string);
        }
    }

    public void logPrint(String string) {
        taLog.append(Tools.printDateStamp() + " " + string.replace('\n', ' ') + "\n");
    }

    /**
     * Emable - disable button state
     * @param state show or hide
     * @param command for command
     */
    private void setButtonState(boolean state, TorrentsListPopupCmd command) {
        switch (command) {
            case TLIST_POPUP_CMD_CHECK: {
            }
            break;
            case TLIST_POPUP_CMD_MOVE: {
            }
            break;
            case TLIST_POPUP_CMD_STOP: {
                btStop.setEnabled(state);
            }
            break;
            case TLIST_POPUP_CMD_START: {
                btStart.setEnabled(state);
            }
            case TLIST_POPUP_CMD_ADD: {
                btAdd.setEnabled(state);
                miFileQuickAddFile.setEnabled(state);
                miFileExtAddFile.setEnabled(state);
            }
            break;
            case TLIST_POPUP_CMD_ADD_EXT: {
                btAdd.setEnabled(state);
                miFileQuickAddFile.setEnabled(state);
                miFileExtAddFile.setEnabled(state);
            }
            break;
            case TLIST_POPUP_CMD_CONN: {
                btConnect.setEnabled(true);
                // set icons
                if (connectedServer) {
                    mainToolBarSetIcon(btConnect, MainToolBarIcons.btDisConnect);
                    //btConnect.setIcon(globalResourceMap.getIcon("btDisConnect.icon")); // NOI18N
                    btConnect.setText(globalResourceMap.getString("btDisConnect.text")); // NOI18N
                } else {
                    mainToolBarSetIcon(btConnect, MainToolBarIcons.btConnect);
                    //btConnect.setIcon(globalResourceMap.getIcon("btConnect.icon")); // NOI18N
                    btConnect.setText(globalResourceMap.getString("btConnect.text")); // NOI18N
                }

                setRefButtonsState(connectedServer);
                setAllButtonsState(connectedServer);
                btConnect.setEnabled(true);
            }
            break;
        }
    }

    private void logRotate(String logPath) {
        File logFile = new File(logPath);
        int logCount = 10;
        String testPath = logPath + "." + logCount;
        File testFile = new File(testPath);
        String nextPath = logPath + "." + logCount + 1;
        File nextFile = new File(nextPath);
        long maxLen = (10 * 1024 * 1024);

        if (logFile.exists() && logFile.isFile() && logFile.length() > maxLen) {
            for (int i = logCount; i > 0; i--) {
                testPath = logPath + "." + i;
                testFile = new File(testPath);
                nextPath = logPath + "." + (i + 1);
                nextFile = new File(nextPath);
                if (testFile.exists()) {
                    if (i == logCount) {
                        // max exist
                        testFile.delete();
                        continue;
                    }
                    if (testFile.exists()) {
                        testFile.renameTo(nextFile);
                    }
                }
            }
            nextPath = logPath + "." + 1;
            nextFile = new File(nextPath);
            logFile.renameTo(nextFile);
        }
    }

    private JMenu makeMenuCommandsPopup(PopupCommandTorrentDescriptor[] pmenu, String name) {
        JMenu menu = new JMenu(name);
        for (int i = 0; i < pmenu.length; i++) {
            JMenuItem item = new JMenuItem(pmenu[i].nameText);
            menu.add(item);
        }
        return menu;
    }

    private void mainToolBarSetIcon(JButton btConnect, MainToolBarIcons icon) {
        String resIdent = getMainToolBarIconIdent(icon);
        switch (defaultToolBarIconSize) {
            case TLIST_POPUP_CMD_ICON_32x32: {
            }
            break;
            case TLIST_POPUP_CMD_ICON_24x24: {
                resIdent += "_24";
            }
            break;
            case TLIST_POPUP_CMD_ICON_16x16: {
                resIdent += "_16";
            }
            break;
        }
        resIdent += ".icon";
        btConnect.setIcon(globalResourceMap.getIcon(resIdent)); // NOI18N
    }

    private String getMainToolBarIconIdent(MainToolBarIcons mainToolBarIcons) {
        String ret = "btConnect";
        switch (mainToolBarIcons) {
            case btConnect:
                ret = "btConnect";
                break;
            case btDisConnect:
                ret = "btDisConnect";
                break;
            case btAdd:
                ret = "btAdd";
                break;
            case btAddUrl:
                ret = "btAddUrl";
                break;
            case btConfigCli:
                ret = "btConfigCli";
                break;
            case btExit:
                ret = "btExit";
                break;
            case btInfo:
                ret = "btInfo";
                break;
            case btRefresh:
                ret = "btRefresh";
                break;
            case btStart:
                ret = "btStart";
                break;
            case btStatistic:
                ret = "btStatistic";
                break;
            case btStop:
                ret = "btStop";
                break;
        }
        return ret;
    }

    /**
     * Get destination path for torrent data
     * @param fileMeta metainfo for torrent
     * @return path
     */
    private DestTorrent getDestPath(MetaInfo fileMeta) {
        DestTorrent destTorrent = new DestTorrent();
        TorrentLocation dialog = new TorrentLocation(
                transmissionView.getFrame(),
                true,
                TorrentLocation.MODE_ADDT,
                null,
                fileMeta,
                transmissionView);
        dialog.setLocationRelativeTo(transmissionView.getFrame());
        dialog.setName(null);
        TransmissionApp.getApplication().show(dialog);
        destTorrent.destPath = dialog.getDstDir();
        destTorrent.pausedTorrent = dialog.isPausedTorrent();
        destTorrent.fileWantedList = dialog.getFileWantedList();
        destTorrent.fileUnWantedList = dialog.getFileUnWantedList();
        destTorrent.filePriority = dialog.getFilePriority();
        dialog.dispose();
        return destTorrent;
    }

    //------------------------ Do Commands---------------------------------------
    private void cmdCheckTorrent() {
        int selRow = tblTorrentList.getSelectedRow();
        if (selRow >= 0) {
            setRefButtonsState(false);
            Requests req = new Requests(getGlobalLogger());
            JSONObject jobj = req.generic(METHOD_TORRENTVERIFY, buildIdJSONArray());
            System.out.println("[cmdCheckTorrent] " + jobj.toString());
            if (webClient != null) {
                try {
                    tracePrint(LOG_MODE_LOG, true, "[cmdCheckTorrent] [" + Tools.printArray(buildSelectModelArray()) + "]" + jobj.toString());
                    JSONObject result = webClient.processWebRequest(jobj, "cmdCheckTorrent");
                    System.out.println("[cmdCheckTorrent] " + result.toString());
                    getGlobalLogger().log(Level.FINE, "[cmdCheckTorrent] " + result.toString());
                    if (result != null) {
                        int[] rows = tblTorrentList.getSelectedRows();
                        refreshTorrentsList(false, false);
                        restoreRowsSelections(rows);
                    }
                } catch (TorrentsCommonException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (UnknownHostException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (InterruptedIOException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (HttpException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            setRefButtonsState(true);
        }
    }

    private void cmdConnect() {
    }

    /**
     * Ad torrent
     * @param isExt - with parameters
     */
    private void cmdAddTorrent(boolean isExt) {
        String message = "", title = "";
        try {
            // Your Task's code here.  This method runs
            // on a background thread, so don't reference
            // the Swing GUI from here.
            btAdd.setEnabled(false);
            miFileQuickAddFile.setEnabled(false);
            miFileExtAddFile.setEnabled(false);

            File[] files = getTorrentFile();

            if (files != null) {

                for (int index = 0; index < files.length; index++) {
                    File file = files[index];

                    MetaInfo fileMeta = getTorrentFileMeta(file);
                    String destPath = transmissionDaemonDescriptor.getDownloadDir();
                    DestTorrent destTorrent = null;
                    if (isExt) {
                        destTorrent = getDestPath(fileMeta);
                        destPath = destTorrent.destPath;
                    }

                    btAdd.setEnabled(true);
                    miFileQuickAddFile.setEnabled(true);
                    miFileExtAddFile.setEnabled(true);

                    if (destTorrent != null && destTorrent.destPath != null) {
                        Requests request = new Requests(getGlobalLogger());
                        /**
                         * @todo 03.10 add separate starting files in the torrent
                         */
                        JSONObject jobj = request.torrentAddByFile(file, false, destTorrent, destPath, 0);
                        tracePrint(LOG_MODE_LOG, true, "[cmdAddTorrent] prepare [" + file + "]" + "[" + jobj + "]");
                        tracePrint(true, fileMeta.toString());

                        /**
                         * @todo run in the separate threads
                         */
                        JSONObject robj = webClient.processWebRequest(jobj, "cmdAddTorrent");
                        tracePrint(true, robj.toString());
                        tracePrint(LOG_MODE_LOG, true, "[cmdAddTorrent] return  [" + file + "]" + "[" + robj + "]");

                        if (robj.getString(FIELD_RESULT).equals(FIELD_RESULT_SUCCESS)) {
                            setRefButtonsState(false);
                            refreshTorrentsList(true, false);
                        } else {
                            if (robj.getString(FIELD_RESULT).indexOf(FIELD_RESULT_DUPLICATE) >= 0) {
                                message = String.format(globalResourceMap.getString("dialog.addTorrent.duplicate"), fileMeta.getName());
                                title = globalResourceMap.getString("dialog.addTorrent.title");
                                JOptionPane.showMessageDialog(transmissionView.getFrame(), message, title, JOptionPane.ERROR_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(transmissionView.getFrame(), robj.toString(), "Add torrent file", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                }
            }
        } catch (TorrentsCommonException ex) {
            Exceptions.printStackTrace(ex);
        } catch (UnknownHostException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (HttpException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            btAdd.setEnabled(true);
            miFileQuickAddFile.setEnabled(true);
            miFileExtAddFile.setEnabled(true);
            tracePrint(true, "Fail to add torrent !!");
        }
    }

    private void cmdAnnounseTorrent() {
        int selRow = tblTorrentList.getSelectedRow();
        if (selRow >= 0) {
            setRefButtonsState(false);
            Requests req = new Requests(getGlobalLogger());
            String myName = Tools.getCalledMethod();
            JSONObject jobj = req.generic(METHOD_TORRENTREANNOUNCE, buildIdJSONArray());
            tracePrint(LOG_MODE_ALL, true, myName + " " + jobj.toString());
            if (webClient != null) {
                try {
                    tracePrint(LOG_MODE_LOG, true, "[" + myName + "] [" + Tools.printArray(buildSelectModelArray()) + "]" + jobj.toString());
                    JSONObject result = webClient.processWebRequest(jobj, myName);
                    tracePrint(true, "[" + myName + "] " + result.toString());
                    getGlobalLogger().log(Level.FINE, "[" + myName + "] " + result.toString());
                } catch (TorrentsCommonException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (UnknownHostException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (InterruptedIOException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (HttpException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            setRefButtonsState(true);
        }
    }

    private void cmdStartTorrent() {
        int selRow = tblTorrentList.getSelectedRow();
        if (selRow >= 0) {
            setRefButtonsState(false);
            Requests req = new Requests(getGlobalLogger());
            JSONObject jobj = req.generic(METHOD_TORRENTSTART, buildIdJSONArray());
            System.out.println("[doStartTorrent] " + jobj.toString());
            if (webClient != null) {
                try {
                    tracePrint(LOG_MODE_LOG, true, "[doStartTorrent] [" + Tools.printArray(buildSelectModelArray()) + "]" + jobj.toString());
                    JSONObject result = webClient.processWebRequest(jobj, "DoStartTorrentTask");
                    System.out.println("[doStartTorrent] " + result.toString());
                    tracePrint(LOG_MODE_LOG, true, "[doStartTorrent] [" + Tools.printArray(buildSelectModelArray()) + "]" + result.toString());
                    getGlobalLogger().log(Level.FINE, "[doStartTorrent] " + result.toString());
                    if (result != null) {
                        int[] rows = tblTorrentList.getSelectedRows();
                        refreshTorrentsList(false, false);
                        restoreRowsSelections(rows);
                    }
                } catch (TorrentsCommonException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (UnknownHostException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (InterruptedIOException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (HttpException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            setRefButtonsState(true);
        }
    }

    /**
     * Command for stop selected torrents
     */
    private void cmdStopTorrent() {
        int selRow = tblTorrentList.getSelectedRow();
        if (selRow >= 0) {
            setRefButtonsState(false);
            Requests req = new Requests(getGlobalLogger());
            JSONObject jobj = req.generic(METHOD_TORRENTSTOP, buildIdJSONArray());
            System.out.println("[doStopTorrent] " + jobj.toString());
            if (webClient != null) {
                try {
                    tracePrint(LOG_MODE_LOG, true, "[doStopTorrent] [" + Tools.printArray(buildSelectModelArray()) + "]" + jobj.toString());
                    JSONObject result = webClient.processWebRequest(jobj, "DoPauseTorrentTask");
                    System.out.println("[doStopTorrent] " + result.toString());
                    getGlobalLogger().log(Level.FINE, "[doStopTorrent] " + result.toString());
                    tracePrint(LOG_MODE_LOG, true, "[doStopTorrent] [" + Tools.printArray(buildSelectModelArray()) + "]" + result.toString());
                    if (result != null) {
                        int[] rows = tblTorrentList.getSelectedRows();
                        refreshTorrentsList(false, false);
                        restoreRowsSelections(rows);
                    }
                } catch (TorrentsCommonException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (UnknownHostException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (InterruptedIOException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (HttpException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            setRefButtonsState(true);
        }
    }

    /**
     * Command for delete selected torrents
     */
    private void cmdDelTorrent(boolean deleteLocalData) {
        int selRow = tblTorrentList.getSelectedRow();
        int modRow = selRow;
        JSONObject jobj = null;
        if (selRow >= 0) {
            modRow = tblTorrentList.convertRowIndexToModel(selRow);
            String fmts = globalResourceMap.getString("dialog.deleteTorrent.format");
            Torrent torrent = modelTorrentsList.getTableDataTorrents().get(modRow);

            String mess = String.format(fmts, modRow, torrent.getId(), torrent.getTorrentName());
            int ret = JOptionPane.showConfirmDialog(transmissionView.getFrame(), mess, globalResourceMap.getString("dialog.deleteTorrent.title"), JOptionPane.YES_NO_OPTION);

            if (ret == JOptionPane.OK_OPTION) {
                if (deleteLocalData) {
                    fmts = globalResourceMap.getString("dialog.deleteTorrentData.format");
                    mess = String.format(fmts, modRow, torrent.getId(), torrent.getTorrentName());
                    ret = JOptionPane.showConfirmDialog(transmissionView.getFrame(), mess, globalResourceMap.getString("dialog.deleteTorrentData.title"), JOptionPane.YES_NO_OPTION);
                }
            }

            if (ret == JOptionPane.OK_OPTION) {


                setRefButtonsState(false);
                Requests req = new Requests(getGlobalLogger());

                //jobj = req.generic(METHOD_TORRENTREMOVE, buildIdJSONArray());
                jobj = req.torrentRemove(buildIdJSONArray(), deleteLocalData);

                System.out.println("[cmdDelTorrent] " + jobj.toString());
                if (webClient != null) {
                    try {
                        JSONObject result = webClient.processWebRequest(jobj, "cmdDelTorrent");
                        tracePrint(true, "[cmdDelTorrent] " + result.toString());
                        getGlobalLogger().log(Level.FINE, "[cmdDelTorrent] " + result.toString());
                        if (result != null) {
                            refreshTorrentsList(true, false);
                        }
                    } catch (TorrentsCommonException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (UnknownHostException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (InterruptedIOException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (HttpException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
                fmts = globalResourceMap.getString("dialog.deleteTorrent.completed");
                logPrint(String.format(fmts, modRow, torrent.getId(), torrent.getTorrentName()));
                setRefButtonsState(true);
            } else {
                fmts = globalResourceMap.getString("dialog.deleteTorrent.canceled");
                logPrint(String.format(fmts, modRow, torrent.getId(), torrent.getTorrentName()));
            }

        }
    }

    /**
     * Move torrent with data
     */
    private void cmdMoveTorrent(int mode) {
        JSONObject jobj = null, jres = null;
        if (tblTorrentList.getSelectedRow() >= 0) {
            int selectedRow = -1;
            selectedRow = tblTorrentList.convertRowIndexToModel(tblTorrentList.getSelectedRow());
            Torrent torrent = modelTorrentsList.getTableDataTorrents().get( selectedRow );
            TorrentLocation dialog = new TorrentLocation(
                    transmissionView.getFrame(),
                    true,
                    mode,
                    //TorrentLocation.MODE_MOVE,
                    torrent,
                    null,
                    transmissionView);
            dialog.setLocationRelativeTo(transmissionView.getFrame());
            TransmissionApp.getApplication().show(dialog);
            String dstDir = dialog.getDstDir();
            boolean moveData = dialog.isMoveData();

            if (dstDir != null && webClient != null) {
                try {
                    Requests request = new Requests(globalLogger);
                    JSONArray movedArray = new JSONArray();
                    JSONArray startedArray = new JSONArray();
                    movedArray.addAll(buildIdJSONArray());

                    // Stop selected if started
                    startedArray = buildIdJSONArray(true);
                    jobj = request.generic(METHOD_TORRENTSTOP, startedArray);
                    tracePrint(LOG_MODE_LOG, true, "[doStopTorrent] [" + Tools.printArray(buildSelectModelArray()) + "]" + jobj.toString());
                    jres = webClient.processWebRequest(jobj, "DoPauseTorrentTask");
                    tracePrint(LOG_MODE_LOG, true, "[doStopTorrent] [" + Tools.printArray(buildSelectModelArray()) + "]" + jres.toString());
                    if (jres != null) {
                        tracePrint(true, jres.toString());
                    }

                    if (mode == TorrentLocation.MODE_LOCA) {
                        moveData = false;
                    }

                    // Move selected
                    jobj = request.torrentSetLocation(movedArray, dstDir, moveData);
                    tracePrint(LOG_MODE_LOG, true, "[doMoveTorrent] [" + Tools.printArray(buildSelectModelArray()) + "]" + jobj.toString());
                    jres = webClient.processWebRequest(jobj, "DoMoveTorrentTask");
                    tracePrint(LOG_MODE_LOG, true, "[doMoveTorrent] [" + Tools.printArray(buildSelectModelArray()) + "]" + jres.toString());
                    if (jres != null) {
                        tracePrint(true, jres.toString());
                    }


                    startCheckStartTorrentIsMoved( selectedRow, torrent );
                    

/*
                    if(torrentModed) {
                    // Start selected
                    jobj = request.generic(METHOD_TORRENTSTART, startedArray);
                    tracePrint(LOG_MODE_LOG, true, "[doStartTorrent] [" + Tools.printArray(buildSelectModelArray()) + "]" + jobj.toString());
                    jres = webClient.processWebRequest(jobj, "DoPauseTorrentTask");
                    tracePrint(LOG_MODE_LOG, true, "[doStartTorrent] [" + Tools.printArray(buildSelectModelArray()) + "]" + jres.toString());
                    if (jres != null) {
                        tracePrint(true, jres.toString());
                    }
                    }

 */

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
        }
    }
    //------------------------ Do Commands--------------------------------------
    //------------------------ Actions -----------------------------------------
    //------------------------ Actions -----------------------------------------

    @Action
    public Task doMoveTorrent() {
        return new DoCommonTorrentTask(getApplication(), TorrentsListPopupCmd.TLIST_POPUP_CMD_MOVE);
    }

    @Action
    public Task doSetLocationTorrent() {
        return new DoCommonTorrentTask(getApplication(), TorrentsListPopupCmd.TLIST_POPUP_CMD_SET_LOCATION);
    }

    @Action
    public Task doAnnounceTorrent() {
        return new DoCommonTorrentTask(getApplication(), TorrentsListPopupCmd.TLIST_POPUP_CMD_ANNOUNCE);
    }

    @Action
    public void doAddTorentURL() {
    }

    @Action
    public void doTorrentInfo() {

        JFileChooser fcOpenFile = new JFileChooser(lastOpenDir);
        fcOpenFile.setFileFilter(new TorrentFileFilter());

        int returnVal = fcOpenFile.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fcOpenFile.getSelectedFile();
            lastOpenDir = file.getParentFile();

            globalConfigStorage.setProperty("last-open-dir", lastOpenDir.toString());
            globalConfigStorage.saveConfig();

            TorrentMetaInfo dialog = new TorrentMetaInfo(this, true, file);
            dialog.setLocationRelativeTo(this.getFrame());
            TransmissionApp.getApplication().show(dialog);

        }

    }

    @Action
    public Task doCheckTorrent() {
        return new DoCommonTorrentTask(getApplication(), TorrentsListPopupCmd.TLIST_POPUP_CMD_CHECK);
    }

    @Action
    public Task doAddTorrentExt() {
        return new DoCommonTorrentTask(getApplication(), TorrentsListPopupCmd.TLIST_POPUP_CMD_ADD_EXT);
    }

    @Action
    public Task doAddTorrentQuick() {
        return new DoCommonTorrentTask(getApplication(), TorrentsListPopupCmd.TLIST_POPUP_CMD_ADD);
    }

//    @Action
//    public Task doAddTorrentQuick() {
//        return new DoAddTorrentQuickTask(getApplication());
//    }
    @Action
    public Task showAboutBox() {
        return new ShowAboutBoxTask(getApplication());
    }

    @Action
    public Task doConnect() {
        //return new DoCommonTorrentTask(getApplication(), TorrentsListPopupCmd.TLIST_POPUP_CMD_CONN);
        return new DoConnectTask(getApplication());
    }

    @Action
    public void doConfigClient() {
        ClientConfig configDialog = new ClientConfig(this, true);
        configDialog.setLocationRelativeTo(this.getFrame());
        TransmissionApp.getApplication().show(configDialog);
    }

    @Action
    public void doStatisticDialog() {
        SessionStatistic statsDialog = new SessionStatistic(this, webClient, true);
        statsDialog.setLocationRelativeTo(this.getFrame());
        statsDialog.setVisible(true);
    }

    @Action
    public void doQuit() {
        if (connectedServer) {
            doConnect();
        }
        singleFrameApplication.exit();
    }

    @Action
    public Task doRefresh() {
        return new DoRefreshTask(getApplication(), true, false);
    }

    @Action
    public Task doDeleteTorrent() {
        //return new DoStartTorrentTask(getApplication());
        return new DoCommonTorrentTask(getApplication(), TorrentsListPopupCmd.TLIST_POPUP_CMD_DEL);
    }

    @Action
    public Task doDeleteTorrentAll() {
        //return new DoStartTorrentTask(getApplication());
        return new DoCommonTorrentTask(getApplication(), TorrentsListPopupCmd.TLIST_POPUP_CMD_DELALL);
    }

    @Action
    public Task doTorrentProperties() {
        //return new DoStartTorrentTask(getApplication());
        return new DoCommonTorrentTask(getApplication(), TorrentsListPopupCmd.TLIST_POPUP_CMD_PROP);
    }

    /**
     * Chech if torrent is moved to new path and start it
     * @todo implement it
     */
    @Action
    private Task doCheckStartTorrentIsMoved(int torrentId, Torrent torrent) {
        return new DoCheckStartTorrentIsMoved(getApplication(), torrentId , torrent );
    }

    private void startCheckStartTorrentIsMoved(int selectedRow, Torrent torrent) {

        ApplicationContext ctx = getApplication().getContext();
        Task task = doCheckStartTorrentIsMoved(selectedRow, torrent);
        ctx.getTaskService().execute(task);
        
    }

    private class DoTorrentPropertiesTask extends org.jdesktop.application.Task<Object, Void> {

        DoTorrentPropertiesTask(org.jdesktop.application.Application app) {
            // Runs on the EDT.  Copy GUI state that
            // doInBackground() depends on from parameters
            // to DoTorrentPropertiesTask fields, here.
            super(app);
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

    /**
     * Comman for torrent properties dialog
     */
    public void cmdTorrentProperties() {
        int selRow = tblTorrentList.getSelectedRow();
        int modRow = selRow;
        if (selRow >= 0) {
            modRow = tblTorrentList.convertRowIndexToModel(selRow);
            TorrentProperties dialog = new TorrentProperties(this, true);
            dialog.setLocationRelativeTo(this.getFrame());
            TransmissionApp.getApplication().show(dialog);
        }
    }

    @Action
    public Task doStartTorrent() {
        //return new DoStartTorrentTask(getApplication());
        return new DoCommonTorrentTask(getApplication(), TorrentsListPopupCmd.TLIST_POPUP_CMD_START);
    }

    @Action
    public Task doStopTorrent() {
        //return new DoPauseTorrentTask(getApplication());
        return new DoCommonTorrentTask(getApplication(), TorrentsListPopupCmd.TLIST_POPUP_CMD_STOP);
    }
    //------------------------ Actions -----------------------------------------
    private File lastOpenDir = new File(".");

    private String makeRegexCaseString(String text) {
        String ret = "";
        if (text.length() > 0) {
            String upper = text.toUpperCase();
            String lower = text.toLowerCase();
            for (int i = 0; i < text.length(); i++) {
                ret += "[" + lower.charAt(i) + upper.charAt(i) + "]";
            }
        } else {
            ret = text;
        }
        return ret;
    }
    //------------------------ Inner classes -----------------------------------

    class TorrentFileFilter extends FileFilter {

        String extensions[] = new String[]{".torrent", ".torrnt"};

        public TorrentFileFilter() {
        }

        @Override
        public boolean accept(File pathname) {

            if (pathname.isDirectory()) {
                return true;
            }

            String name = pathname.getName().toLowerCase();
            for (int i = extensions.length - 1; i >= 0; i--) {
                if (name.endsWith(extensions[i])) {
                    return true;
                }
            }
            return false;

        }

        @Override
        public String getDescription() {
            return globalResourceMap.getString("TransmissionView.FileFilter.Description");
        }
    }

    class MainTooolBarPopupItemListener implements ActionListener {

        private TorrentsListPopupCmd command;

        public MainTooolBarPopupItemListener(TorrentsListPopupCmd command) {
            this.command = command;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ApplicationContext ctx = getApplication().getContext();


            switch (command) {
                case TLIST_POPUP_CMD_ICON_32x32: {
                    initMainToolBarIcons(TorrentsListPopupCmd.TLIST_POPUP_CMD_ICON_32x32);
                }
                break;
                case TLIST_POPUP_CMD_ICON_16x16: {
                    initMainToolBarIcons(TorrentsListPopupCmd.TLIST_POPUP_CMD_ICON_16x16);
                }
                break;
            }


        }
    }

    /**
     * Implements files popup menu
     */
    class FilesMenuItemListener implements ActionListener {

        private FilesListPopupCmd command;

        public FilesMenuItemListener(FilesListPopupCmd command) {
            this.command = command;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ApplicationContext ctx = getApplication().getContext();
            Task task;

            switch (command) {

                case TLIST_POPUP_CMD_OPENDIR: {
                    task = doFilesOpenTorrentDir();
                    ctx.getTaskService().execute(task);
                }
                break;

                case TLIST_POPUP_CMD_OPENFILE: {
                    task = doFilesOpenTorrentFile();
                    ctx.getTaskService().execute(task);
                }

                case TLIST_POPUP_CMD_SELECT_ALL: {
                    selectFileListCmd(FilesListPopupCmd.TLIST_POPUP_CMD_SELECT_ALL);
                }

                case TLIST_POPUP_CMD_SELECT_NON: {
                    selectFileListCmd(FilesListPopupCmd.TLIST_POPUP_CMD_SELECT_NON);
                }

                case TLIST_POPUP_CMD_SELECT_INV: {
                    selectFileListCmd(FilesListPopupCmd.TLIST_POPUP_CMD_SELECT_INV);
                }

                /*

                tblFileListPopupMenu.add("High priority");
                tblFileListPopupMenu.add("Normal priority");
                tblFileListPopupMenu.add("Low priority");

                tblFileListPopupMenu.add("Download");
                tblFileListPopupMenu.add("Skip");

                tblFileListPopupMenu.add("Select All");
                tblFileListPopupMenu.add("DeSelect All");
                 */


                break;

                default: {
                    JOptionPane.showMessageDialog(transmissionView.getFrame(), "Command [" + Tools.whoCalledMethod() + "][" + command + "] is not implemented yet !");
                }
                break;

            }
        }

        private void selectFileListCmd(FilesListPopupCmd filesListPopupCmd) {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    private Task doFilesOpenTorrentFile() {
        return new DoCommonFilesTask(getApplication(), FilesListPopupCmd.TLIST_POPUP_CMD_OPENFILE);
    }

    private Task doFilesOpenTorrentDir() {
        return new DoCommonFilesTask(getApplication(), FilesListPopupCmd.TLIST_POPUP_CMD_OPENDIR);
    }

    private void cmdFilesOpenTorrentDir() {
        int selRow = tblTorrentList.getSelectedRow();
        int selFil = tblTorrentFiles.getSelectedRow();
        if (selRow >= 0 && selFil >= 0) {
            int modelRow = tblTorrentList.convertRowIndexToModel(selRow);
            Torrent torrent = modelTorrentsList.getTableDataTorrents().get(modelRow);
            tracePrint(LOG_MODE_LOG, true, "[cmdFilesOpenTorrentFile] [" + torrent + "]");

            selRow = tblTorrentFiles.getSelectedRow();
            if (selRow >= 0) {
                try {
                    modelRow = tblTorrentFiles.convertRowIndexToModel(selRow);
                    TorrentFileDescInterface fdesk = torrent.getFilesDescList().get(modelRow);
                    String destPath = torrent.getDownloadDir() + "/" + fdesk.getDir();
                    File itemFile = new File(destPath);
                    if (itemFile.exists()) {
                        Desktop desktop = Desktop.getDesktop();
                        tracePrint(LOG_MODE_LOG, true, "[cmdFilesOpenTorrentFile] [" + torrent + "][" + destPath + "]");
                        desktop.open(itemFile);
                    } else {
                        String mess = "[cmdFilesOpenTorrentFile] Not exist file: [" + destPath + "]";
                        tracePrint(LOG_MODE_LOG, true, mess);
                        JOptionPane.showMessageDialog(transmissionView.getFrame(), mess, "Open torrent file", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }

        }
    }

    private void cmdFilesOpenTorrentFile() {
        int selRow = tblTorrentList.getSelectedRow();
        if (selRow >= 0) {
            int modelRow = tblTorrentList.convertRowIndexToModel(selRow);
            Torrent torrent = modelTorrentsList.getTableDataTorrents().get(modelRow);
            tracePrint(LOG_MODE_LOG, true, "[cmdFilesOpenTorrentFile] [" + torrent + "]");

            selRow = tblTorrentFiles.getSelectedRow();
            if (selRow >= 0) {
                try {
                    modelRow = tblTorrentFiles.convertRowIndexToModel(selRow);
                    TorrentFileDescInterface fdesk = torrent.getFilesDescList().get(modelRow);
                    String destPath = torrent.getDownloadDir() + "/" + fdesk.getPath();
                    File itemFile = new File(destPath);
                    if (itemFile.exists()) {
                        Desktop desktop = Desktop.getDesktop();
                        tracePrint(LOG_MODE_LOG, true, "[cmdFilesOpenTorrentFile] [" + torrent + "][" + destPath + "]");
                        desktop.open(itemFile);
                    } else {
                        String mess = "[cmdFilesOpenTorrentFile] Not exist file: [" + destPath + "]";
                        tracePrint(LOG_MODE_LOG, true, mess);
                        JOptionPane.showMessageDialog(transmissionView.getFrame(), mess, "Open torrent file", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }

        }
    }

    /**
     * Main torrents list (table) popup menu
     */
    class TorrentsMenuItemListener implements ActionListener {

        private TorrentsListPopupCmd command;

        public TorrentsMenuItemListener(TorrentsListPopupCmd command) {
            this.command = command;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ApplicationContext ctx = getApplication().getContext();
            Task task;
            switch (command) {
                case TLIST_POPUP_CMD_MOVE: {
                    task = doMoveTorrent();
                    ctx.getTaskService().execute(task);
                }
                break;
                case TLIST_POPUP_CMD_CHECK: {
                    task = doCheckTorrent();
                    ctx.getTaskService().execute(task);
                }
                break;
                case TLIST_POPUP_CMD_START: {
                    task = doStartTorrent();
                    ctx.getTaskService().execute(task);
                }
                break;
                case TLIST_POPUP_CMD_STOP: {
                    task = doStopTorrent();
                    ctx.getTaskService().execute(task);
                }
                break;
                case TLIST_POPUP_CMD_DEL: {
                    task = doDeleteTorrent();
                    ctx.getTaskService().execute(task);
                }
                break;
                case TLIST_POPUP_CMD_DELALL: {
                    task = doDeleteTorrentAll();
                    ctx.getTaskService().execute(task);
                }
                break;
                case TLIST_POPUP_CMD_ANNOUNCE: {
                    task = doAnnounceTorrent();
                    ctx.getTaskService().execute(task);
                }
                break;
                case TLIST_POPUP_CMD_ICON_32x32: {
                    initMainToolBarIcons(TorrentsListPopupCmd.TLIST_POPUP_CMD_ICON_32x32);
                }
                break;
                case TLIST_POPUP_CMD_ICON_16x16: {
                    initMainToolBarIcons(TorrentsListPopupCmd.TLIST_POPUP_CMD_ICON_16x16);
                }
                break;
                case TLIST_POPUP_CMD_ICON_24x24: {
                    initMainToolBarIcons(TorrentsListPopupCmd.TLIST_POPUP_CMD_ICON_24x24);
                }
                break;
                case TLIST_POPUP_CMD_PROP: {
                    task = doTorrentProperties();
                    ctx.getTaskService().execute(task);
                }
                break;
                case TLIST_POPUP_CMD_SET_LOCATION: {
                    task = doSetLocationTorrent();
                    ctx.getTaskService().execute(task);
                }
                break;
                default: {
                    JOptionPane.showMessageDialog(transmissionView.getFrame(), "Command [" + Tools.whoCalledMethod() + "][" + command + "] is not implemented yet !");
                }
                break;
            }
        }
    }

    private class ShowAboutBoxTask extends org.jdesktop.application.Task<Object, Void> {

        ShowAboutBoxTask(org.jdesktop.application.Application app) {
            // Runs on the EDT.  Copy GUI state that
            // doInBackground() depends on from parameters
            // to ShowAboutBoxTask fields, here.
            super(app);
            JFrame mainFrame = TransmissionApp.getApplication().getMainFrame();
            JDialog aboutBox = new TransmissionAboutBox(transmissionView);
            aboutBox.setLocationRelativeTo(mainFrame);
            TransmissionApp.getApplication().show(aboutBox);
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

    private class DoConnectTask extends org.jdesktop.application.Task<Object, Void> {

        DoConnectTask(org.jdesktop.application.Application app) {
            // Runs on the EDT.  Copy GUI state that
            // doInBackground() depends on from parameters
            // to DoConnectTask fields, here.
            super(app);
            setRefButtonsState(false);
            btConnect.setEnabled(false);
        }

        @Override
        protected Object doInBackground() {

            String message = "";

            try {
                setProgress(0);
                //setMessage("Rolling back the current changes...");

                try {
                    if (!connectedServer) {

                        message = globalResourceMap.getString("StatusBar.message.connect");

                        setMessage(String.format(message, globalConfigStorage.getProfileProperty(LocalSettiingsConstants.CONF_CLI_HOST))); //  "Connect to server"
                        tracePrint(LOG_MODE_LOG, false, String.format(message, globalConfigStorage.getProfileProperty(LocalSettiingsConstants.CONF_CLI_HOST)));

                        connectedServer = processConnect(true);
                        if (connectedServer) {
                            message = globalResourceMap.getString("StatusBar.message.refresh");
                            setMessage(message); //"Refresh torrents list"
                            refreshTorrentsList(true, false);

                            if (tblTorrentList.getModel().getRowCount() > 0) {
                                ListSelectionModel model = tblTorrentList.getSelectionModel();
                                model.setSelectionInterval(0, 0);
                                tblTorrentList.setSelectionModel(model);
                            }
                            updaterTimer.start();
                            refreshTimer.start();

                        }
                    } else {

                        modelTorrentsList = new TorrentsTableModel(null, transmissionView);
                        tblTorrentList.setModel(modelTorrentsList);
                        tblTorrentList.setRowSorter(null);

                        tblTorrentList.tableChanged(new TableModelEvent(modelTorrentsList));
                        updateInfoBox(-1);
                        connectedServer = processConnect(false);
                        updaterTimer.stop();
                    }
                } catch (org.apache.http.conn.HttpHostConnectException ex) {
                    Exceptions.printStackTrace(ex);
                    JOptionPane.showMessageDialog(TransmissionApp.getApplication().getMainFrame(), "Error connect to server " + webClient.getTargetHttpHost());
                    tracePrint(LOG_MODE_LOG | LOG_MODE_PRO, true, "Error connect to server " + webClient.getTargetHttpHost());
                } catch (UnknownHostException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (JSONException ex) {
                    message = globalResourceMap.getString("Error-JSONParse");
                    String addMessage = ex.getMessage();
                    Html2Text converter = new Html2Text();
                    try {
                        converter.parse(addMessage);
                        addMessage = converter.getText();
                    } catch (IOException ex1) {
                        Exceptions.printStackTrace(ex1);
                    }
                    tracePrint(LOG_MODE_LOG | LOG_MODE_PRO, true, String.format(message, webClient.getTargetHttpHost().toHostString()) + "\n" + addMessage);
                    JOptionPane.showMessageDialog(TransmissionApp.getApplication().getMainFrame(),
                            String.format(message, webClient.getTargetHttpHost().toHostString()));
                    Exceptions.printStackTrace(ex);
                } catch (TorrentsCommonException ex) {
                    message = globalResourceMap.getString("Error-HttpConnection");
                    String addMessage = ex.getMessage();
                    Html2Text converter = new Html2Text();
                    try {
                        converter.parse(addMessage);//, ".", ".\n");
                        addMessage = converter.getText();
                    } catch (IOException ex1) {
                        Exceptions.printStackTrace(ex1);
                    }
                    if (ex.getAdditionalInfo().length() > 0) {
                        addMessage = ex.getAdditionalInfo() + "\n" + addMessage;
                    }
                    tracePrint(LOG_MODE_LOG | LOG_MODE_PRO, true, String.format(message, webClient.getTargetHttpHost().toHostString()) + "\n" + addMessage);
                    JOptionPane.showMessageDialog(TransmissionApp.getApplication().getMainFrame(),
                            String.format(message, webClient.getTargetHttpHost().toHostString()) + "\n" + addMessage,
                            globalResourceMap.getString("Error-Auth.title"),
                            JOptionPane.ERROR_MESSAGE);
                    Exceptions.printStackTrace(ex);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (HttpException ex) {
                    Exceptions.printStackTrace(ex);
                }


                setProgress(1, 0, 4);
                Thread.sleep(1000L); // remove for real app
                setProgress(2, 0, 4);
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            }
            return null; // return your result
        }

        @Override
        protected void succeeded(Object result) {
            // Runs on the EDT.  Update the GUI based on
            // the result computed by doInBackground().
            btConnect.setEnabled(true);
            // set icons
            if (connectedServer) {
                //btConnect.setIcon(globalResourceMap.getIcon("btDisConnect.icon")); // NOI18N
                mainToolBarSetIcon(btConnect, MainToolBarIcons.btDisConnect);
                btConnect.setText(globalResourceMap.getString("btDisConnect.text")); // NOI18N
            } else {
                //btConnect.setIcon(globalResourceMap.getIcon("btConnect.icon")); // NOI18N
                mainToolBarSetIcon(btConnect, MainToolBarIcons.btConnect);
                btConnect.setText(globalResourceMap.getString("btConnect.text")); // NOI18N
            }

            setRefButtonsState(connectedServer);
            setAllButtonsState(connectedServer);
            btConnect.setEnabled(true);
        }
    }

    /**
     * Update speed panel from last result for update torrent details
     */
    private class DoSpeedoMeterTask extends org.jdesktop.application.Task<Object, Void> {

        DoSpeedoMeterTask(org.jdesktop.application.Application app) {
            super(app);

        }

        @Override
        protected Object doInBackground() {
            if (connectedServer) {
            }

            return null; // return your result
        }

        @Override
        protected void succeeded(Object result) {
            // Runs on the EDT.  Update the GUI based on
            // the result computed by doInBackground().
            setRefButtonsState(true);
        }
    }

    private class DoRefreshTask extends org.jdesktop.application.Task<Object, Void> {

        private boolean singleRows;
        private boolean fullRefresh;

        DoRefreshTask(org.jdesktop.application.Application app, boolean fullRefresh, boolean singleRows) {
            // Runs on the EDT.  Copy GUI state that
            // doInBackground() depends on from parameters
            // to DoRefreshTask fields, here.
            super(app);
            this.singleRows = singleRows;
            this.fullRefresh = fullRefresh;
            setRefButtonsState(false);
        }

        @Override
        protected Object doInBackground() {
            try {
                // Your Task's code here.  This method runs
                // on a background thread, so don't reference
                // the Swing GUI from here.
                setProgress(0);
                try {
                    if (connectedServer) {
                        //!! setMessage("Refresh torrents list");
                        refreshTorrentsList(fullRefresh, singleRows);
                    }
                } catch (TorrentsCommonException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (UnknownHostException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (HttpException ex) {
                    //Exceptions.printStackTrace(ex);
                    tracePrint(true, ex.getMessage());
                }
                setProgress(0);
                Thread.sleep(1L); // remove for real app
                //setProgress(0);
                return null; // return your result
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            }
            return null; // return your result
        }

        @Override
        protected void succeeded(Object result) {
            // Runs on the EDT.  Update the GUI based on
            // the result computed by doInBackground().
            setRefButtonsState(true);
        }
    }

    private class DoUpdateTorrentDetails extends org.jdesktop.application.Task<Object, Void> {

        private int tblSelRow = -1;
        private boolean startedUpdateTorrentDetails = false;
        private boolean nowExec = false;

        public DoUpdateTorrentDetails(org.jdesktop.application.Application app, int tblSelRow) {
            this(app, tblSelRow, false);
        }

        public DoUpdateTorrentDetails(org.jdesktop.application.Application app, int tblSelRow, boolean nowExec) {
            super(app);
            this.tblSelRow = tblSelRow;
            this.nowExec = nowExec;
            if (!nowExec) {
                updaterTimer.stop();
            }
        }

        @Override
        protected Object doInBackground() throws Exception {

            //int selRow = tblTorrentFiles.getSelectedRow();

            if (startedUpdateTorrentDetails) {
                return null;
            }

            try {
                startedUpdateTorrentDetails = true;
                rowSelectionOld = this.tblSelRow;
                rowSelectionNew = -1;

                Thread.sleep(500);
                rowSelectionNew = tblTorrentList.getSelectedRow();

                System.out.println("[DoUpdateTorrentDetails] check  [" + rowSelectionNew + "][" + rowSelectionOld + "]");

                if (rowSelectionNew != rowSelectionOld) {
                    return null;
                }

                if (tblSelRow >= 0) {

                    updaterTimer.stop();

                    int modelRow = tblTorrentList.convertRowIndexToModel(rowSelectionNew);//  realConvertRowIndexToModel(rowSelectionNew);

                    setRefButtonsState(false);
                    setProgress(0);
                    setMessage("Update torrent details ...");

                    updateInfoBox(modelRow);

                    tracePrint(true, "[DoUpdateTorrentDetails] Execute doInBackground .. 0 [" + tblSelRow + "][" + rowSelectionNew + "][" + modelRow + "] rows=" + Tools.printArray(selectedTorrentRows));

                    updateTorrentDetailsFromServer(modelRow);

                    tracePrint(true, "[DoUpdateTorrentDetails] Execute doInBackground .. 1 rows=" + Tools.printArray(selectedTorrentRows));

                    int srows[] = selectedTorrentRows;

                    lockValueChanged = true;
                    //!! tblTorrentList.tableChanged(new TableModelEvent(modelTorrentsList));
                    updateInfoBox(modelRow);
                    lockValueChanged = false;

                    selectedTorrentRows = srows;

                    tracePrint(true, "[DoUpdateTorrentDetails] Execute doInBackground .. 2 rows=" + Tools.printArray(selectedTorrentRows));

                    //!!(selectedTorrentRows);

                    restoreRowsSelections(srows);
                    //restoreRowsSelections(srows);
                    //refreshTorrentsList(false);

                    //updateFilesAndPeersBox(modelRow);
                    //updatePiecesGraph(modelRow);

                }
            } finally {
                startedUpdateTorrentDetails = false;
                updaterTimer.start();
                refreshTimer.start();
            }


            return null;
        }

        @Override
        protected void succeeded(Object result) {
            // Runs on the EDT.  Update the GUI based on
            // the result computed by doInBackground().
            setProgress(4, 0, 4);
            setRefButtonsState(true);
            if (!nowExec) {
                updaterTimer.start();
            }
        }
    }

    /**
     * Listener for main popup
     */
    class TorrentListPopupListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            showPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            showPopup(e);
        }

        private void showPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                showTblTorrentListPopupMenu(e);
            }
        }
    }

    /**
     * Listener for main popup
     */
    class FileListPopupListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            showPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            showPopup(e);
        }

        private void showPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                showTblFileListListPopupMenu(e);
            }
        }
    }

    /**
     * Listener for main popup
     */
    class MainToolBarPopupListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            showPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            showPopup(e);
        }

        private void showPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                showMainToolBarPopupMenu(e);
            }
        }
    }

    /**
     *
     * @author dstarzhynskyi
     */
    class TorrentsSelectionListener implements ListSelectionListener {

        private JTable table;

        public TorrentsSelectionListener(TorrentsTableModel model, JTable table) {
            this.table = table;
        }

        @Override
        public void valueChanged(ListSelectionEvent event) {
            if (event.getValueIsAdjusting()) {
                return;
            }

            if (lockValueChanged) {
                return;
            }

            refreshTimer.stop();
            updaterTimer.stop();


            checkNavigator();
            int selRow = table.getSelectedRow();
            selectedTorrentRows = table.getSelectedRows();

            tracePrint(true, "!!![TorrentsSelectionListener][valueChanged] [" + lockValueChanged + "]Selected = " + selRow + " selected rows=" + Tools.printArray(selectedTorrentRows));
            tracePrint(true, Tools.whoCalledMe());
            //tracePrint(true, "+++" + Tools.showCallStack());


            if (selRow >= 0) {

                if (tblTorrentList.getModel().getRowCount() <= selRow) {
                    updateInfoBox(tblTorrentList.convertRowIndexToModel(selRow));
                }
                /*
                if (lockValueChanged) {
                lockValueChanged = false;
                return;
                }
                 */
                taskDoUpdateTorrentDetails = new DoUpdateTorrentDetails(getApplication(), selRow, true);
                ApplicationContext ctx = getApplication().getContext();
                ctx.getTaskService().execute(taskDoUpdateTorrentDetails);

                //restoreRowsSelections(selectedTorrentRows);

            }
        }
    }

    private class DoUpdateStatusLine extends org.jdesktop.application.Task<Object, Void> {

        public DoUpdateStatusLine(org.jdesktop.application.Application app) {
            super(app);
            updaterTimer.stop();
        }

        @Override
        protected Object doInBackground() throws Exception {


            this.setProgress(100);

            System.out.println("[DoUpdateStatusLine] Execute doInBackground ..");
            //Thread.sleep(5000);

            String msg = updateStatusLine();

            setMessage(msg);
            updaterTimer.start();

            return null; // return your result
        }

        @Override
        protected void succeeded(Object result) {
            updaterTimer.start();
        }
    }

    class SamplePopupMenu extends JCommandPopupMenu {

        public SamplePopupMenu() {
            this.addMenuButton(new JCommandMenuButton("Test menu item 1",
                    new EmptyResizableIcon(16)));
            this.addMenuButton(new JCommandMenuButton("Test menu item 2",
                    new EmptyResizableIcon(16)));
            this.addMenuButton(new JCommandMenuButton("Test menu item 3",
                    new EmptyResizableIcon(16)));
            this.addMenuSeparator();
            this.addMenuButton(new JCommandMenuButton("Test menu item 4",
                    new EmptyResizableIcon(16)));
            this.addMenuButton(new JCommandMenuButton("Test menu item 5",
                    new EmptyResizableIcon(16)));
        }
    }

    private class NavigatorButtonActionListener implements ActionListener {

        public static final int NAV_BUTTON_PREV = 1;
        public static final int NAV_BUTTON_NEXT = 2;
        public static final int NAV_BUTTON_FIRS = 3;
        public static final int NAV_BUTTON_LAST = 4;
        private int buttonIndex = 0;

        public NavigatorButtonActionListener(int buttonIndex) {
            this.buttonIndex = buttonIndex;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ListSelectionModel model = tblTorrentList.getSelectionModel();
            int column = tblTorrentList.getSelectedColumn();
            int sel = -1, cur = tblTorrentList.getSelectedRow();
            model.clearSelection();
            switch (buttonIndex) {
                case NAV_BUTTON_FIRS: {
                    tracePrint(true, "NAV_BUTTON_FIRS");
                    sel = 0;
                    model.addSelectionInterval(sel, sel);
                }
                break;
                case NAV_BUTTON_PREV: {
                    tracePrint(true, "NAV_BUTTON_PREV");
                    if (cur > 0) {
                        sel = cur - 1;
                        model.addSelectionInterval(sel, sel);
                    }
                }
                break;
                case NAV_BUTTON_NEXT: {
                    tracePrint(true, "NAV_BUTTON_NEXT");
                    if (cur < getRealRowCount(tblTorrentList) - 1) {
                        sel = cur + 1;
                        model.addSelectionInterval(sel, sel);
                    }
                }
                break;
                case NAV_BUTTON_LAST: {
                    tracePrint(true, "NAV_BUTTON_LAST");
                    sel = getRealRowCount(tblTorrentList) - 1;
                    model.addSelectionInterval(sel, sel);
                }
                break;
            }
            //tblTorrentList.changeSelection(last, column, false, true);
            tblTorrentList.scrollRectToVisible(tblTorrentList.getCellRect(sel, column, false));
        }
    }

    private class DoCommonFilesTask extends org.jdesktop.application.Task<Object, Void> {

        FilesListPopupCmd command;

        DoCommonFilesTask(org.jdesktop.application.Application app, FilesListPopupCmd command) {
            // Runs on the EDT.  Copy GUI state that
            // doInBackground() depends on from parameters
            // to DoMoveTorrentTask fields, here.
            super(app);
            this.command = command;
        }

        @Override
        protected Object doInBackground() throws Exception {

            switch (command) {
                case TLIST_POPUP_CMD_OPENFILE: {
                    cmdFilesOpenTorrentFile();
                }
                break;
                case TLIST_POPUP_CMD_OPENDIR: {
                    cmdFilesOpenTorrentDir();
                }
                break;
            }

            return null;
        }
    }

    private class DoCheckStartTorrentIsMoved extends org.jdesktop.application.Task<Object, Void> {

        DoCheckStartTorrentIsMoved(org.jdesktop.application.Application app, Integer torrentId, Torrent torrent){
            super(app);
        }


        @Override
        protected Object doInBackground() throws Exception {

            return null;
        }

    }

    private class DoCommonTorrentTask extends org.jdesktop.application.Task<Object, Void> {

        TorrentsListPopupCmd command;

        DoCommonTorrentTask(org.jdesktop.application.Application app, TorrentsListPopupCmd command) {
            // Runs on the EDT.  Copy GUI state that
            // doInBackground() depends on from parameters
            // to DoMoveTorrentTask fields, here.
            super(app);
            this.command = command;
        }

        @Override
        protected Object doInBackground() throws Exception {
            setButtonState(false, command);
            switch (command) {
                case TLIST_POPUP_CMD_CHECK: {
                    cmdCheckTorrent();
                }
                break;
                case TLIST_POPUP_CMD_DEL: {
                    cmdDelTorrent(false);
                }
                break;
                case TLIST_POPUP_CMD_DELALL: {
                    cmdDelTorrent(true);
                }
                break;
                case TLIST_POPUP_CMD_MOVE: {
                    cmdMoveTorrent(TorrentLocation.MODE_MOVE);
                }
                break;
                case TLIST_POPUP_CMD_SET_LOCATION: {
                    cmdMoveTorrent(TorrentLocation.MODE_LOCA);
                }
                break;
                case TLIST_POPUP_CMD_STOP: {
                    cmdStopTorrent();
                }
                break;
                case TLIST_POPUP_CMD_START: {
                    cmdStartTorrent();
                }
                break;
                case TLIST_POPUP_CMD_ANNOUNCE: {
                    cmdAnnounseTorrent();
                }
                break;
                case TLIST_POPUP_CMD_ADD: {
                    cmdAddTorrent(false);
                }
                break;
                case TLIST_POPUP_CMD_ADD_EXT: {
                    cmdAddTorrent(true);
                }
                break;
                case TLIST_POPUP_CMD_CONN: {
                    cmdConnect();
                }
                break;
                case TLIST_POPUP_CMD_PROP: {
                    cmdTorrentProperties();
                }
                break;
            }
            return null;
        }

        @Override
        protected void succeeded(Object result) {
            // Runs on the EDT.  Update the GUI based on
            // the result computed by doInBackground().
            setButtonState(true, command);
        }
    }

    class PopupCommandDescriptor {

        public Icon icon;
        public String nameText;
        public String nameIdent;
        public boolean isMenu;
    }

    class PopupCommandTorrentDescriptor extends PopupCommandDescriptor {

        public TorrentsListPopupCmd cmd;
        public PopupCommandTorrentDescriptor[] menu;

        public PopupCommandTorrentDescriptor(TorrentsListPopupCmd cmd, Icon icon, String name) {
            this(cmd, icon, name, null);
        }

        public PopupCommandTorrentDescriptor(TorrentsListPopupCmd cmd, Icon icon, String name, String ident) {
            this(false, cmd, icon, name, ident);
        }

        public PopupCommandTorrentDescriptor(boolean isMenu, TorrentsListPopupCmd cmd, Icon icon, String name, String ident) {
            this(isMenu, cmd, icon, name, ident, null);
        }

        public PopupCommandTorrentDescriptor(boolean isMenu, TorrentsListPopupCmd cmd, Icon icon, String name, String ident, PopupCommandTorrentDescriptor[] menu) {
            this.isMenu = isMenu;
            this.cmd = cmd;
            this.icon = icon;
            this.nameText = name;
            this.menu = menu;
        }
    }

    class PopupCommandFilesDescriptor extends PopupCommandDescriptor {

        public FilesListPopupCmd cmd;
        public PopupCommandFilesDescriptor[] menu;

        public PopupCommandFilesDescriptor(FilesListPopupCmd cmd, Icon icon, String name) {
            this(cmd, icon, name, null);
        }

        public PopupCommandFilesDescriptor(FilesListPopupCmd cmd, Icon icon, String name, String ident) {
            this(false, cmd, icon, name, ident);
        }

        public PopupCommandFilesDescriptor(boolean isMenu, FilesListPopupCmd cmd, Icon icon, String name, String ident) {
            this(isMenu, cmd, icon, name, ident, null);
        }

        public PopupCommandFilesDescriptor(boolean isMenu, FilesListPopupCmd cmd, Icon icon, String name, String ident, PopupCommandFilesDescriptor[] submenu) {
            this.isMenu = isMenu;
            this.cmd = cmd;
            this.icon = icon;
            this.nameText = name;
            this.menu = submenu;
        }
    }

    private class LocaleActionListener implements ActionListener {

        private final Locale locale;

        public LocaleActionListener(Locale locale) {
            this.locale = locale;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Locale.setDefault(locale);
            setDefaultLocale(locale);
            globalConfigStorage.setProperty(CONF_CLI_LOCALE, locale.toString());
            globalConfigStorage.saveConfig();
        }
    }
    //------------------------ Inner classes -----------------------------------
}
