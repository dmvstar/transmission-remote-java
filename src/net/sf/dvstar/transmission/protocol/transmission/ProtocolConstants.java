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


package net.sf.dvstar.transmission.protocol.transmission;

/**
 *
 * @author dstarzhynskyi
 */
public interface ProtocolConstants {

    public static int DIR_GET = 1;
    public static int DIR_SET = 2;

    public static int LOG_MODE_LOG = 2;
    public static int LOG_MODE_PRO = 4;
    public static int LOG_MODE_ALL = LOG_MODE_LOG | LOG_MODE_PRO;

    public static short STATUS_CHECK_WAIT = 1;
    public static short STATUS_CHECK   = 2;
    public static short STATUS_DOWNLOAD = 4;
    public static short STATUS_SEED = 8;
    public static short STATUS_PAUSED = 16;

    /*
        All
        Downloading
        Paused
        Seeding
        Checking
        Waiting
        Error
    */
    
    public static String STATUS_ALL_DESC = "torrent.status.All";
    public static String STATUS_DOWNLOAD_DESC = "torrent.status.Dowloading";
    public static String STATUS_PAUSED_DESC = "torrent.status.Paused";
    public static String STATUS_SEED_DESC = "torrent.status.Seeding";
    public static String STATUS_CHECK_WAIT_DESC = "torrent.status.Waiting";
    public static String STATUS_CHECK_DESC   = "torrent.status.Checking";
    public static String STATUS_ERROR_DESC = "torrent.status.Error";

    public static int TR_STATUS_CHECK_WAIT = (1 << 0);//,  Waiting in queue to check files
    public static int TR_STATUS_CHECK = (1 << 1);//',  Checking files
    public static int TR_STATUS_DOWNLOAD = (1 << 2);//,  Downloading
    public static int TR_STATUS_SEED = (1 << 3);//,  Seeding
    public static int TR_STATUS_PAUSED = (1 << 4);//   Torrent is stopped

    public static int BANDWIDTH_LOW = -1;
    public static int BANDWIDTH_NORMAL = 0;
    public static int BANDWIDTH_HIGH = 1;

    public static String BANDWIDTH_LOW_DESC = "torrent.file.band.low";
    public static String BANDWIDTH_NORMAL_DESC = "torrent.file.band.normal";
    public static String BANDWIDTH_HIGH_DESC = "torrent.file.band.high";

    public static int PB_25_B = 1;
    public static int PB_50_B = 2;
    public static int PB_75_B = 3;
    public static int PB_100_B = 4;
    public static int PB_100_A = 5;

    public static String TRS_STATUS_CHECK_WAIT = "Checking";
    public static String TRS_STATUS_CHECK = "Checking";
    public static String TRS_STATUS_DOWNLOAD = "Dowloading";
    public static String TRS_STATUS_SEED = "Seeding";
    public static String TRS_STATUS_PAUSED = "Paused";


    public static String KEY_TAG = "tag";
    public static String KEY_METHOD = "method";
    public static String KEY_IDS = "ids";
    public static String KEY_ARGUMENTS = "arguments";
    public static String KEY_FIELDS = "fields";
    public static String KEY_TORRENTS = "torrents";
    public static String METHOD_TORRENTGET = "torrent-get";
    public static int    METHOD_TORRENTGET_LIST = 1;
    public static int    METHOD_TORRENTGET_DTLS = 2;

    public static String METHOD_TORRENTSTART = "torrent-start";
    public static String METHOD_TORRENTSTOP = "torrent-stop";
    public static String METHOD_TORRENTSET = "torrent-set";
    public static String METHOD_SESSIONGET = "session-get";
    public static String METHOD_SESSIONSET = "session-set";
    public static String METHOD_TORRENTREMOVE = "torrent-remove";
    public static String METHOD_TORRENTVERIFY = "torrent-verify";
    public static String METHOD_TORRENTADD = "torrent-add";
    public static String METHOD_TORRENTREANNOUNCE = "torrent-reannounce";
    public static String METHOD_SESSIONSTATS = "session-stats";
    public static String METHOD_BLOCKLISTUPDATE = "blocklist-update";
    public static String METHOD_PORT_TEST = "port-test";
    public static String METHOD_TORRENT_SET_LOCATION = "torrent-set-location";
    public static String FIELD_CURRENT_STATS = "current-stats";

    public static String FIELD_RESULT = "result";
    public static String FIELD_RESULT_SUCCESS = "success";
    public static String FIELD_RESULT_DUPLICATE = "duplicate";

    public static String FIELD_ACTIVE_TORRENT_COUNT = "activeTorrentCount";
    public static String FIELD_DOWNLOAD_SPEED = "downloadSpeed";
    public static String FIELD_PAUSED_TORRENT_COUNT = "pausedTorrentCount";
    public static String FIELD_TORRENT_COUNT = "torrentCount";
    public static String FIELD_UPLOAD_SPEED = "uploadSpeed";
    public static String FIELD_DOWNLOAD_BYTES = "downloadedBytes";
    public static String FIELD_UPLOAD_BYTES = "uploadedBytes";

    public static String FIELD_DOWNLOAD_SPEED_VIS = "downloadSpeedVis";
    public static String FIELD_UPLOAD_SPEED_VIS = "uploadSpeedVis";

    public static String FIELD_TOTAL_STATS = "cumulative-stats";
    public static String FIELD_LOCATION = "location";
    public static String FIELD_MOVE = "move";
    public static String FIELD_PIECES = "pieces";
    public static String FIELD_PIECECOUNT = "pieceCount";
    public static String FIELD_PEERSSENDINGTOUS = "peersSendingToUs";
    public static String FIELD_PEERSGETTINGFROMUS = "peersGettingFromUs";
    public static String FIELD_FLAGSTR = "flagStr";
    public static String FIELD_COUNTRY = "country";
    public static String FIELD_HONORSSESSIONLIMITS = "honorsSessionLimits";
    public static String FIELD_METAINFO = "metainfo";
    public static String FIELD_CLIENTNAME = "clientName";
    public static String FIELD_HAVEUNCHECKED = "haveUnchecked";
    public static String FIELD_LEFTUNTILDONE = "leftUntilDone";
    public static String FIELD_COMMENT = "comment";
    public static String FIELD_NAME = "name";
    public static String FIELD_TYPE = "type";
    public static String FIELD_RATETOCLIENT = "rateToClient";
    public static String FIELD_RATETOCLIENT_VIS = "rateToClientV";
    public static String FIELD_BLOCKLISTSIZE = "blocklist-size";
    public static String FIELD_RATETOPEER = "rateToPeer";
    public static String FIELD_RATETOPEER_VIS = "rateToPeerV";
    public static String FIELD_ERRORSTRING = "errorString";
    public static String FIELD_PROGRESS = "progress";
    public static String FIELD_PROGRESS_VIS = "progressV";
    public static String FIELD_FILENAME = "filename";
    public static String FIELD_PAUSED = "paused";
    public static String FIELD_ANNOUNCEURL = "announceURL";
    public static String FIELD_ETA = "eta";
    public static String FIELD_STATUS = "status";
    public static String FIELD_BANDWIDTHPRIORITY = "bandwidthPriority";
    public static String FIELD_RATEDOWNLOAD = "rateDownload";
    public static String FIELD_RATEUPLOAD = "rateUpload";
    public static String FIELD_RATEDOWNLOAD_VIS = "rateDownloadV";
    public static String FIELD_RATEUPLOAD_VIS = "rateUploadV";
    public static String FIELD_TOTALSIZE = "totalSize";
    public static String FIELD_HAVEVALID = "haveValid";
    public static String FIELD_HAVEVALID_VIS = "haveValidV";
    public static String FIELD_UPLOADEDEVER = "uploadedEver";
    public static String FIELD_UPLOADEDEVER_VIS = "uploadedEverV";
    public static String FIELD_LEECHERS = "leechers";
    public static String FIELD_SEEDERS = "seeders";
    public static String FIELD_ADDEDDATE = "addedDate";
    public static String FIELD_ID = "id";
    public static String FIELD_FILES = "files";

    public static String FILED_STATS = "fileStats";
    public static String FILED_STATS_COMPLETED = "bytesCompleted";
    public static String FILED_STATS_WANTED = "wanted";
    public static String FILED_STATS_PRIORITY = "priority";

    public static String FIELD_PRIORITIES = "priorities";
    public static String FIELD_WANTED = "wanted";

    public static String FIELD_TRACKERS = "trackers";
    public static String FIELD_PEERS = "peers";
    public static String FIELD_PEERLIMIT = "peer-limit";
    public static String FIELD_LENGTH = "length";
    public static String FIELD_BYTESCOMPLETED = "bytesCompleted";
    public static String FIELD_LENGTH_VIS = "lengthV";
    public static String FIELD_BYTESCOMPLETED_VIS = "bytesCompletedV";
    public static String FIELD_DELETELOCALDATA = "delete-local-data";
    public static String FIELD_MAXCONNECTEDPEERS = "maxConnectedPeers";
    public static String FIELD_CREATOR = "creator";
    public static String FIELD_SWARMSPEED = "swarmSpeed";
    public static String FIELD_DATECREATED = "dateCreated";
    public static String FIELD_HASHSTRING = "hashString";
    public static String FIELD_DOWNLOADDIR = "downloadDir";
    public static String FIELD_RECHECKPROGRESS = "recheckProgress";
    public static String FIELD_SEEDRATIOLIMIT = "seedRatioLimit";
    public static String FIELD_SEEDRATIOMODE = "seedRatioMode";
    public static String FIELD_SEEDRATIOLIMITED = "seedRatioLimited"; // session-set/get
    public static String VALUE_RECENTLY_ACTIVE = "recently-active";


    public static String FIELD_FILE_PROGRESS ="file-progress";
    public static String FIELD_FILE_PROGRESS_VIS ="file-progressV";
    public static String FIELD_FILE_DOWN = "key";


    public static String FIELD_DONEPROGRESS = "done-progress"; // not real field

    // BEGIN CONFUSION
    public static String FIELD_DOWNLOADLIMITMODE = "downloadLimitMode"; // DEPRECATED
    public static String FIELD_UPLOADLIMITMODE = "uploadLimitMode"; // DEPRECATED
    public static String FIELD_SPEEDLIMITDOWNENABLED = "speed-limit-down-enabled"; // ALSO DEPRECATED
    public static String FIELD_SPEEDLIMITUPENABLED = "speed-limit-up-enabled"; // ALSO DEPRECATED
    public static String FIELD_SPEEDLIMITDOWN = "speed-limit-down"; // ALSO DEPRECATED
    public static String FIELD_SPEEDLIMITUP = "speed-limit-up"; // ALSO DEPRECATED
    public static String FIELD_UPLOADLIMITED = "uploadLimited";
    public static String FIELD_DOWNLOADLIMITED = "downloadLimited";
    public static String FIELD_UPLOADLIMIT = "uploadLimit";
    public static String FIELD_DOWNLOADLIMIT = "downloadLimit";
    public static String FIELD_PORT_IS_OPEN = "port-is-open";

    // settings
    public static String FIELD_PORTFORWARDINGENABLED = "port-forwarding-enabled";
    public static String FIELD_ENCRYPTION = "encryption";
    public static String FIELD_ALTSPEEDTIMEENABLED = "alt-speed-time-enabled";
    public static String FIELD_ALTSPEEDTIMEBEGIN = "alt-speed-time-begin";
    public static String FIELD_ALTSPEEDTIMEEND = "alt-speed-time-end";
    public static String FIELD_BLOCKLISTENABLED = "blocklist-enabled";
    public static String FIELD_ALTSPEEDENABLED = "alt-speed-enabled";
    public static String FIELD_ALTSPEEDDOWN = "alt-speed-down";
    public static String FIELD_ALTSPEEDUP = "alt-speed-up";
    public static String FIELD_DHTENABLED = "dht-enabled";
    public static String FIELD_PEERLIMITGLOBAL = "peer-limit-global";
    public static String FIELD_PEXENABLED = "pex-enabled";
    public static String FIELD_PEXALLOWED = "pex-allowed";
    public static String FIELD_DOWNLOAD_DIR = "download-dir";
    public static String FIELD_VERSION = "version";
    public static String FIELD_RPC_VERSION = "rpc-version";

    public static String VALUE_PREFERRED = "preferred";
    public static String VALUE_TOLERATED = "tolerated";
    public static String VALUE_REQUIRED = "required";
    public static String FIELD_DONEDATE = "doneDate";
    public static String PRIORITY_HIGH = "priority-high";
    public static String PRIORITY_NORMAL = "priority-normal";
    public static String PRIORITY_LOW = "priority-low";
    public static String FILES_WANTED = "files-wanted";
    public static String FILES_UNWANTED = "files-unwanted";
    public static String FIELD_ADDRESS = "address";
    public static String FIELD_PORT = "port";
    public static String TIER = "tier";
    public static String ANNOUNCE = "announce";
    public static String SCRAPE = "scrape";
    public static String FIELD_SIZEWHENDONE = "sizeWhenDone";
    public static String FIELD_SIZEWHENDONE_VIS = "sizeWhenDoneV";
    public static String CONFIG_SERVER_URL = "config-server-url";


    public enum ResponseTag {
        SessionGet, SessionStats, TorrentGet, DoNothing, UpdateFiles, PortTest, UpdateBlocklist
    }

    public enum TorrentsSortBy {
        Alphanumeric, Status, DateDone, DateAdded
    }

    public static final String[] columnIndexNames = {
        FIELD_ID, FIELD_NAME,
        FIELD_TOTALSIZE, FIELD_DONEPROGRESS,
        FIELD_STATUS,
        FIELD_SEEDERS, FIELD_LEECHERS,
        FIELD_RATEDOWNLOAD_VIS,FIELD_RATEUPLOAD_VIS,
        FIELD_UPLOADEDEVER_VIS
    };

    public enum TorrentsListPopupCmd {
        TLIST_POPUP_CMD_CONN, TLIST_POPUP_CMD_NONE, TLIST_POPUP_CMD_ADD, TLIST_POPUP_CMD_ADD_EXT, TLIST_POPUP_CMD_START, TLIST_POPUP_CMD_STOP,
        TLIST_POPUP_CMD_DEL,  TLIST_POPUP_CMD_MOVE,TLIST_POPUP_CMD_CHECK, TLIST_POPUP_CMD_ANNOUNCE,
        TLIST_POPUP_CMD_SET_LOCATION, TLIST_POPUP_CMD_LIMIT_DOWN, TLIST_POPUP_CMD_LIMIT_UP, TLIST_POPUP_CMD_PROP, 
        TLIST_POPUP_CMD_REFRESH, TLIST_POPUP_CMD_DELALL,
        TLIST_POPUP_CMD_ICON_32x32, TLIST_POPUP_CMD_ICON_24x24, TLIST_POPUP_CMD_ICON_16x16
    }

    public enum FilesListPopupCmd {
        TLIST_POPUP_CMD_OPENDIR,
        TLIST_POPUP_CMD_OPENFILE,
        TLIST_POPUP_CMD_PRORITY_LOW,
        TLIST_POPUP_CMD_PRORITY_NORMAL,
        TLIST_POPUP_CMD_PRORITY_HIGH,
        TLIST_POPUP_CMD_SELECT_INV,
        TLIST_POPUP_CMD_SELECT_ALL,
        TLIST_POPUP_CMD_SELECT_NON
    }

    public enum MainToolBarIcons {
        btConnect,//.icon=images/default/24x24/tb_connect_go.png
        btDisConnect,//.icon=images/default/24x24/tb_connect_no.png
        btConfigCli,//.icon=images/default/24x24/tb_configure.png
        btStatistic,//.icon=images/default/24x24/tb_torrent_stat.png
        btExit,//.icon=images/default/24x24/tb_exit.png
        btRefresh,//.icon=images/default/24x24/tb_player_reload.png
        btAdd,//.icon=images/default/24x24/tb_add_ext.png
        btAddUrl,//.icon=images/default/24x24/tb_add_url.png
        btStart,//.icon=images/default/24x24/tb_player_play.png
        btStop,//.icon=images/default/24x24/tb_player_pause.png
        btInfo//.icon=images/default/24x24/tb_torrent_info.png
    }

    public static final String MainToolBarIconsIdent[]= {
        "btConnect",//.icon=images/default/24x24/tb_connect_go.png
        "btDisConnect",//.icon=images/default/24x24/tb_connect_no.png
        "btConfigCli",//.icon=images/default/24x24/tb_configure.png
        "btStatistic",//.icon=images/default/24x24/tb_torrent_stat.png
        "btExit",//.icon=images/default/24x24/tb_exit.png
        "btRefresh",//.icon=images/default/24x24/tb_player_reload.png
        "btAdd",//.icon=images/default/24x24/tb_add_ext.png
        "btAddUrl",//.icon=images/default/24x24/tb_add_url.png
        "btStart",//.icon=images/default/24x24/tb_player_play.png
        "btStop",//.icon=images/default/24x24/tb_player_pause.png
        "btInfo"//.icon=images/default/24x24/tb_torrent_info.png
    };

    public static final String BUNDLE_IDENT_MENU_TORRENT_START = "Menu.Torrent.Start.text";

}