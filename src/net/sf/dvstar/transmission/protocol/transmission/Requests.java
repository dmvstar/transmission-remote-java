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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.dvstar.transmission.protocol.TorrentConstants.DestTorrent;
import net.sf.dvstar.transmission.protocol.transmission.ProtocolConstants.ResponseTag;
import net.sf.dvstar.transmission.utils.ConfigStorage;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.openide.util.Exceptions;

/**
 *
 * @author dstarzhynskyi
 */
public class Requests implements ProtocolConstants {

    private boolean connected = false;
    private Logger loggerProvider;

    public Requests(Logger loggerProvider) {
        this.loggerProvider = loggerProvider;
    }

    public boolean produceConnect() {
        boolean ret = false;
        ConfigStorage config = new ConfigStorage();

        if (config.loadConfig()) {
            try {

                String serverURL = config.getProfileProperty(CONFIG_SERVER_URL);

                XmlRpcClientConfigImpl xmlRpcConfig = new XmlRpcClientConfigImpl();
                xmlRpcConfig.setServerURL(new URL(serverURL));
                XmlRpcClient client = new XmlRpcClient();
                client.setConfig(xmlRpcConfig);
                ret = connected = true;
            } catch (MalformedURLException ex) {
                Exceptions.printStackTrace(ex);
                Logger.getLogger(Requests.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        ret = true;
        return (ret);
    }

    public JSONObject generic(String method, JSONArray ids) {
        JSONObject request = createBasicObject(method);
        JSONObject args = request.getJSONObject(ProtocolConstants.KEY_ARGUMENTS);
        args.put(ProtocolConstants.KEY_IDS, ids);
        request.put(ProtocolConstants.KEY_ARGUMENTS, args);
        return request;
    }

    public JSONObject sessionGet() {
        return createBasicObject(ProtocolConstants.METHOD_SESSIONGET, ResponseTag.SessionGet);
    }

    public JSONObject sessionStats() {
        return createBasicObject(ProtocolConstants.METHOD_SESSIONSTATS, ResponseTag.SessionStats);
    }

    public JSONObject torrentGet() {
        return torrentGet(null, null, METHOD_TORRENTGET_LIST);
    }

    public JSONObject torrentGet(List<Integer> tblTorrentListModel, TorrentsTableModel modelTorrentsList, int modeRefresh) {
        JSONObject request = createBasicObject(
                ProtocolConstants.METHOD_TORRENTGET,
                ResponseTag.TorrentGet);
        JSONArray ids = null;

        if (tblTorrentListModel != null && modelTorrentsList != null) {
            //int rows[] = tblTorrentList.getSelectedRows();

            if (tblTorrentListModel.size() > 0) {
                ids = new JSONArray();
                {
                    for (int i = 0; i < tblTorrentListModel.size(); i++) {
                        int modelRow = (Integer)tblTorrentListModel.get(i);//
                        Torrent torrent = modelTorrentsList.getTableDataTorrents().get(modelRow);
                        ids.add(torrent.getId());
                    }
                }
            }

        }

        Object[] olist;
        olist = new Object[]{
                    ProtocolConstants.FIELD_ID,
                    ProtocolConstants.FIELD_NAME,
                    ProtocolConstants.FIELD_TOTALSIZE,
                    ProtocolConstants.FIELD_STATUS,
                    ProtocolConstants.FIELD_SEEDERS,
                    ProtocolConstants.FIELD_LEECHERS,
                    ProtocolConstants.FIELD_PROGRESS,
                    ProtocolConstants.FIELD_RECHECKPROGRESS,
                    ProtocolConstants.FIELD_ADDEDDATE,
                    ProtocolConstants.FIELD_HAVEVALID,
                    ProtocolConstants.FIELD_HAVEUNCHECKED,
                    ProtocolConstants.FIELD_ETA,
                    ProtocolConstants.FIELD_RATEDOWNLOAD,
                    ProtocolConstants.FIELD_RATEUPLOAD,
                    ProtocolConstants.FIELD_UPLOADEDEVER,
                    ProtocolConstants.FIELD_LEFTUNTILDONE,
                    ProtocolConstants.FIELD_ANNOUNCEURL,
                    ProtocolConstants.FIELD_DOWNLOADLIMIT,
                    ProtocolConstants.FIELD_DOWNLOADLIMITMODE,
                    ProtocolConstants.FIELD_UPLOADLIMIT,
                    ProtocolConstants.FIELD_UPLOADLIMITED,
                    ProtocolConstants.FIELD_UPLOADLIMITMODE,
                    ProtocolConstants.FIELD_SPEEDLIMITDOWN,
                    ProtocolConstants.FIELD_DOWNLOADLIMITED,
                    ProtocolConstants.FIELD_SPEEDLIMITDOWNENABLED,
                    ProtocolConstants.FIELD_SPEEDLIMITUP,
                    ProtocolConstants.FIELD_SPEEDLIMITUPENABLED,
                    ProtocolConstants.FIELD_ERRORSTRING,
                    ProtocolConstants.FIELD_PEERSGETTINGFROMUS,
                    ProtocolConstants.FIELD_PEERSSENDINGTOUS,
                    ProtocolConstants.FIELD_PIECECOUNT,
                    ProtocolConstants.FIELD_MAXCONNECTEDPEERS,
                    ProtocolConstants.FIELD_COMMENT,
                    ProtocolConstants.FIELD_SWARMSPEED,
                    ProtocolConstants.FIELD_DATECREATED,
                    ProtocolConstants.FIELD_CREATOR,
                    ProtocolConstants.FIELD_HASHSTRING,
                    ProtocolConstants.FIELD_DOWNLOADDIR,
                    ProtocolConstants.FIELD_SEEDRATIOLIMIT,
                    ProtocolConstants.FIELD_BANDWIDTHPRIORITY,
                    ProtocolConstants.FIELD_SEEDRATIOMODE,
                    ProtocolConstants.FIELD_HONORSSESSIONLIMITS,
                    ProtocolConstants.FIELD_DONEDATE,
                    ProtocolConstants.FIELD_SIZEWHENDONE
                };

        switch (modeRefresh) {

            case METHOD_TORRENTGET_LIST: {
                // use below
            }
            break;
            case METHOD_TORRENTGET_DTLS: {
                olist = new Object[]{
                            ProtocolConstants.FIELD_ID,
                            ProtocolConstants.FIELD_NAME,
                            ProtocolConstants.FIELD_TOTALSIZE,
                            ProtocolConstants.FIELD_STATUS,
                            ProtocolConstants.FIELD_SEEDERS,
                            ProtocolConstants.FIELD_LEECHERS,
                            ProtocolConstants.FIELD_PROGRESS,
                            ProtocolConstants.FIELD_RECHECKPROGRESS,
                            ProtocolConstants.FIELD_ADDEDDATE,
                            ProtocolConstants.FIELD_HAVEVALID,
                            ProtocolConstants.FIELD_HAVEUNCHECKED,
                            ProtocolConstants.FIELD_ETA,
                            ProtocolConstants.FIELD_RATEDOWNLOAD,
                            ProtocolConstants.FIELD_RATEUPLOAD,
                            ProtocolConstants.FIELD_UPLOADEDEVER,
                            ProtocolConstants.FIELD_LEFTUNTILDONE,
                            ProtocolConstants.FIELD_ANNOUNCEURL,
                            ProtocolConstants.FIELD_DOWNLOADLIMIT,
                            ProtocolConstants.FIELD_DOWNLOADLIMITMODE,
                            ProtocolConstants.FIELD_UPLOADLIMIT,
                            ProtocolConstants.FIELD_UPLOADLIMITED,
                            ProtocolConstants.FIELD_UPLOADLIMITMODE,
                            ProtocolConstants.FIELD_SPEEDLIMITDOWN,
                            ProtocolConstants.FIELD_DOWNLOADLIMITED,
                            ProtocolConstants.FIELD_SPEEDLIMITDOWNENABLED,
                            ProtocolConstants.FIELD_SPEEDLIMITUP,
                            ProtocolConstants.FIELD_SPEEDLIMITUPENABLED,
                            ProtocolConstants.FIELD_ERRORSTRING,
                            ProtocolConstants.FIELD_PEERSGETTINGFROMUS,
                            ProtocolConstants.FIELD_PEERSSENDINGTOUS,
                            ProtocolConstants.FIELD_PIECECOUNT,
                            ProtocolConstants.FIELD_PIECES,
                            ProtocolConstants.FIELD_MAXCONNECTEDPEERS,
                            ProtocolConstants.FIELD_COMMENT,
                            ProtocolConstants.FIELD_SWARMSPEED,
                            ProtocolConstants.FIELD_DATECREATED,
                            ProtocolConstants.FIELD_CREATOR,
                            ProtocolConstants.FIELD_TRACKERS,
                            ProtocolConstants.FIELD_HASHSTRING,
                            ProtocolConstants.FIELD_DOWNLOADDIR,
                            ProtocolConstants.FIELD_SEEDRATIOLIMIT,
                            ProtocolConstants.FIELD_BANDWIDTHPRIORITY,
                            ProtocolConstants.FIELD_SEEDRATIOMODE,
                            ProtocolConstants.FIELD_HONORSSESSIONLIMITS,
                            ProtocolConstants.FIELD_DONEDATE,
                            ProtocolConstants.FIELD_FILES,
                            ProtocolConstants.FIELD_STATUS,
                            ProtocolConstants.FILED_STATS,
                            ProtocolConstants.FIELD_WANTED,
                            ProtocolConstants.FIELD_PRIORITIES,
                            ProtocolConstants.FIELD_PEERS,
                            ProtocolConstants.FIELD_SIZEWHENDONE
                        };
            }
            break;
            default: {
                // use below
            }
            break;

        }


        JSONArray fields = JSONArray.fromObject(olist);
        JSONObject args = request.getJSONObject(ProtocolConstants.KEY_ARGUMENTS);
        args.put(ProtocolConstants.KEY_FIELDS, fields);

        if (ids != null) {
            args.put(ProtocolConstants.KEY_IDS, ids);
        }

        request.put(ProtocolConstants.KEY_ARGUMENTS, args);


        return request;
    }

    public JSONObject createBasicObject(String method, ResponseTag tag) {
        //http://json-lib.sourceforge.net/
        Map map = new HashMap();
        map.put(ProtocolConstants.KEY_TAG, tag);
        map.put(ProtocolConstants.KEY_METHOD, method);
        map.put(ProtocolConstants.KEY_ARGUMENTS, new JSONObject());

        JSONObject jsonObject = JSONObject.fromObject(map);

        return jsonObject;
    }

    public JSONObject createBasicObject(String method) {
        return createBasicObject(method, ResponseTag.DoNothing);
    }

    public JSONObject torrentSetLocation(JSONArray ids, String location, boolean move) {
        JSONObject request = createBasicObject(ProtocolConstants.METHOD_TORRENT_SET_LOCATION, ResponseTag.DoNothing);
        JSONObject args = request.getJSONObject(ProtocolConstants.KEY_ARGUMENTS);
        args.put(ProtocolConstants.KEY_IDS, ids);
        args.put(ProtocolConstants.FIELD_LOCATION, location);
        args.put(ProtocolConstants.FIELD_MOVE, move);
        request.put(ProtocolConstants.KEY_ARGUMENTS, args);
        return request;
    }

    public JSONObject torrentRemove( JSONArray ids, boolean deleteLocalData ) {
        JSONObject request = createBasicObject(ProtocolConstants.METHOD_TORRENTREMOVE, ResponseTag.DoNothing);
        JSONObject args = request.getJSONObject(ProtocolConstants.KEY_ARGUMENTS);
        args.put(ProtocolConstants.KEY_IDS, ids);
        args.put(ProtocolConstants.FIELD_DELETELOCALDATA, deleteLocalData);
        request.put(ProtocolConstants.KEY_ARGUMENTS, args);
        return request;
    }


    /**
     * Add torrent to work
     * @param file Torrent file
     * @param deleteAfter
     * @param pausedTorrent
     * @param destination
     * @param peerLimit
     * @return
     */
    public JSONObject torrentAddByFile(File file, 
            boolean deleteAfter,

            DestTorrent destTorrent,

            //List wanted,
            //JsonArray unwanted,
            //JsonArray high,
            //JsonArray normal,
            //JsonArray low,
            //boolean pausedTorrent,
            String destination, int peerLimit) {

        JSONObject request = null;
        JSONArray  jArray = null;
        BufferedInputStream bis = null;
        try {
            long length = file.length();
            if (length > Integer.MAX_VALUE) {
                // File is too large
            } else {
                loggerProvider.log(Level.INFO, "Opening: {0}", file.getName());
                // Create the byte array to hold the data
                byte[] bytes = new byte[(int) length];

                bis = new BufferedInputStream(new FileInputStream(file));
                bis.read(bytes);
                String baseEncoded = new String(Base64.encodeBase64(bytes));
                loggerProvider.log(Level.INFO, "Opening: {0}", baseEncoded);

                request = createBasicObject(ProtocolConstants.METHOD_TORRENTADD);
                JSONObject args = request.getJSONObject(ProtocolConstants.KEY_ARGUMENTS);

                //args.put(ProtocolConstants.FIELD_FILENAME, file.getName() );
                args.put(ProtocolConstants.FIELD_METAINFO, baseEncoded);

                if(destTorrent.pausedTorrent) {
                    args.put(ProtocolConstants.FIELD_PAUSED, true);
                }
                else {
                    args.put(ProtocolConstants.FIELD_PAUSED, false);
                }

                if (destTorrent.fileWantedList!=null){
                    jArray = JSONArray.fromObject(destTorrent.fileWantedList);
                    args.put(ProtocolConstants.FILES_WANTED, jArray);
                }

                if (destTorrent.fileUnWantedList!=null){
                    jArray = JSONArray.fromObject(destTorrent.fileUnWantedList);
                    args.put(ProtocolConstants.FILES_UNWANTED, jArray);
                }

                if (destTorrent.filePriority!=null){
                    jArray = JSONArray.fromObject(destTorrent.filePriority);
                    args.put(ProtocolConstants.PRIORITY_NORMAL, jArray);
                }

                /*
                if (high != null)
                arguments.Put(ProtocolConstants.PRIORITY_HIGH, high);
                if (normal != null)
                arguments.Put(ProtocolConstants.PRIORITY_NORMAL, normal);
                if (low != null)
                arguments.Put(ProtocolConstants.PRIORITY_LOW, low);
                if (wanted != null)
                arguments.Put(ProtocolConstants.FILES_WANTED, wanted);
                if (unwanted != null)
                arguments.Put(ProtocolConstants.FILES_UNWANTED, unwanted);
                 */

                if (destination != null) {
                    args.put(ProtocolConstants.FIELD_DOWNLOAD_DIR, destination);
                }
                if (peerLimit > 0) {
                    args.put(ProtocolConstants.FIELD_PEERLIMIT, peerLimit);
                }
                request.put(ProtocolConstants.KEY_ARGUMENTS, args);

                if (deleteAfter && file.exists()) {
                    try {
                        file.delete();
                    } catch (Exception ex) {
                    }
                }
            }

        } catch (FileNotFoundException ex) {
            loggerProvider.log(Level.WARNING, "", ex);
        } catch (IOException ex) {
            loggerProvider.log(Level.WARNING, "", ex);
        } finally {
            try {
                bis.close();
            } catch (IOException ex) {
                loggerProvider.log(Level.WARNING, "", ex);
            }
        }

        return request;
    }

    /**
     * @return the connected
     */
    public boolean isConnected() {
        return connected;
    }
}
