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

import net.sf.dvstar.transmission.utils.TimeSpan;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import net.sf.dvstar.transmission.protocol.TorrentInterface;
import net.sf.dvstar.transmission.protocol.transmission.JSONMapModel.Files;
import net.sf.dvstar.transmission.protocol.transmission.JSONMapModel.Peers;
import net.sf.dvstar.transmission.utils.Tools;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.jdesktop.application.ResourceMap;

/**
 * Torrent class with implementation of transmission torrent structure 
 * @author dstarzhynskyi
 */
public class Torrent implements TorrentInterface, ProtocolConstants {

    private JSONObject jsonTorrent = null;
    //private Map mapTorrent = null;
    private byte[] piecesArray;
    private byte[] fileStatsArray;
    private byte[] fileWantedArray;
    private byte[] filePriorityArray;
    private int piecesCount;
    private int statusCode = 0;
    private int id = 0;
    private boolean hasError = false;
    private String errorString;
    private ResourceMap resourceMap = null;
    private String torrentName;
    private String torrentDir;
    private List<Files> filesList = null;
    private List<Peers> peersList = null;
    private List<TorrentFileDescInterface> filesDescList = null;
    private List<TorrentPeerDescInterface> peersDescList = null;

    public Torrent(JSONObject jsonTorrent, ResourceMap resourceMap) {
        this.jsonTorrent = jsonTorrent;
        this.setResourceBundle(resourceMap);
        parseResult();
    }

    /**
     * parse result and fill torrent data
     */
    private void parseResult() {

        String val;

        JSONArray filesStatsJsonArray = null;
        JSONArray filesJsonArray = null;

        val = jsonTorrent.getString(FIELD_ID);
        id = Integer.parseInt(val);

        val = jsonTorrent.getString(FIELD_NAME);
        torrentName = val;
        torrentDir = "";

        statusCode = jsonTorrent.getInt(FIELD_STATUS);

        val = jsonTorrent.getString(FIELD_PIECECOUNT);
        piecesCount = Integer.parseInt(val);

        val = jsonTorrent.getString(FIELD_ERRORSTRING);
        errorString = val;
        if (errorString.length() > 0) {
            hasError = true;
        }

        val = safeJsonGetString(jsonTorrent, FIELD_PIECES);
        if (val != null) {
            piecesArray = Base64.decodeBase64(val.getBytes());
        }
        /*
        //val = jsonTorrent.getString(ProtocolConstants.FIELD_FILES);
        val = safeJsonGetString(jsonTorrent, FILED_STATS_PRIORITY);
        if (val != null) {
        fileStatsArray = Base64.decodeBase64( val.getBytes() );
        System.out.println(val+"["+fileStatsArray.length+"]"+Tools.printArray(fileStatsArray));
        }
        val = safeJsonGetString(jsonTorrent, FILED_STATS_WANTED);
        if (val != null) {
        fileStatsArray = Base64.decodeBase64( val.getBytes() );
        System.out.println(val+"["+fileStatsArray.length+"]"+Tools.printArray(fileStatsArray));
        }
         */
        val = safeJsonGetString(jsonTorrent, FILED_STATS);
        if (val != null) {
            fileStatsArray = Base64.decodeBase64(val.getBytes());
            System.out.println(val + "[" + fileStatsArray.length + "]" + Tools.printArray(fileStatsArray));
            filesStatsJsonArray = jsonTorrent.getJSONArray(FILED_STATS);
        }

        val = safeJsonGetString(jsonTorrent, FIELD_FILES);
        if (val != null) {
            filesList = new ArrayList<Files>();
            filesDescList = new ArrayList<TorrentFileDescInterface>();
            filesJsonArray = jsonTorrent.getJSONArray(FIELD_FILES);
            for (int i = 0; i < filesJsonArray.size(); i++) {
                JSONObject objFiles = filesJsonArray.getJSONObject(i);
                Files files = new Files(objFiles);

                String key = "";
                String fld = "";
                for (int j = 0; j < Files.files.length; j++) {
                    key = Files.files[j];
                    /**
                     * @todo change to clean method
                     */
                    fld = getValueFrom(objFiles, key).toString();
                    //Object ofd = obj.get(key);
                    //String fld = ofd != null ? ofd.toString() : "";
                    files.setValue(key, fld);
                }

                if (filesStatsJsonArray != null) {
                    JSONObject objStats = filesStatsJsonArray.getJSONObject(i);

                    for (int j = 0; j < Files.stats.length; j++) {
                        key = Files.stats[j];
                        fld = getValueFrom(objStats, key).toString();
                        files.setValue(key, fld);
                    }
                }

                filesList.add(files);
                filesDescList.add(files);
            }
        }

        //val = jsonTorrent.getString(ProtocolConstants.FIELD_PEERS);
        val = safeJsonGetString(jsonTorrent, FIELD_PEERS);
        if (val != null) {
            peersList = new ArrayList<Peers>();
            peersDescList = new ArrayList<TorrentPeerDescInterface>();

            filesJsonArray = jsonTorrent.getJSONArray(FIELD_PEERS);

            for (int i = 0; i < filesJsonArray.size(); i++) {
                JSONObject obj = filesJsonArray.getJSONObject(i);
                Peers peers = new Peers(obj);

                String key = "";
                String fld = "";
                try {
                    for (int j = 0; j < Peers.fields.length; j++) {
                        key = Peers.fields[j];
                        /**
                         * @todo change to clean method
                         */
                        fld = getValueFrom(obj, key).toString();
                        peers.setValue(key, fld);
                    }
                } catch (Exception ex) {
                    System.err.println("fail to get " + key);
                    ex.printStackTrace();
                }

                peersList.add(peers);
                peersDescList.add(peers);
            }
        }


    }

    /**
     * @deprecated not clean     *
     */
    public Object getValueFrom(JSONObject jobj, String key) {
        Object ret = "";

        if (key.equals(FIELD_ID)) {
            ret = new Integer(jobj.getString(key));
        } else if (key.equals(FIELD_DONEPROGRESS)) {
            ret = Tools.calcPercentage(getHaveTotal(), getSize()) + " %";
            if (getStatusCode() == STATUS_CHECK) {
                ret = getRecheckPercentage() + " %";
            }
        } else if (key.equals(FIELD_STATUS)) {
            ret = getStatusDesc(jobj.getInt(key));
        } else if (key.equals(FIELD_TOTALSIZE)) {
            ret = Tools.getFileSize((float) jobj.getDouble(key));
        } else if (key.equals(FIELD_SEEDERS)) {
            int i = jobj.getInt(key);
            ret = i >= 0 ? i : 0;
        } else if (key.equals(FIELD_LEECHERS)) {
            int i = jobj.getInt(key);
            ret = i >= 0 ? i : 0;
        } else if (key.equals(FIELD_HAVEVALID_VIS)) {
            ret = Tools.getFileSize((float) jobj.getDouble(FIELD_HAVEVALID));
        } else if (key.equals(FIELD_UPLOADEDEVER)) {
            ret = Tools.getFileSize((float) jobj.getDouble(key));
        } else if (key.equals(FIELD_LENGTH_VIS)) {
            ret = Tools.getFileSize((float) jobj.getDouble(FIELD_LENGTH));
        } else if (key.equals(FIELD_BYTESCOMPLETED_VIS)) {
            ret = Tools.getFileSize((float) jobj.getDouble(FIELD_BYTESCOMPLETED));
        } else if (key.equals(FIELD_UPLOAD_SPEED_VIS)) {
            ret = Tools.getVisibleSpeed(jobj.getInt(FIELD_UPLOAD_SPEED));
        } else if (key.equals(FIELD_DOWNLOAD_SPEED_VIS)) {
System.out.println("[FIELD_DOWNLOAD_SPEED]="+jobj.getInt(FIELD_DOWNLOAD_SPEED));
            ret = Tools.getVisibleSpeed(jobj.getInt(FIELD_DOWNLOAD_SPEED));
        } else if (key.equals(FIELD_RATEDOWNLOAD_VIS)) {
System.out.println("[FIELD_RATEDOWNLOAD]="+jobj.getInt(FIELD_RATEDOWNLOAD));
            ret = Tools.getVisibleSpeed(jobj.getInt(FIELD_RATEDOWNLOAD));
        } else if (key.equals(FIELD_RATEUPLOAD_VIS)) {
            ret = Tools.getVisibleSpeed(jobj.getInt(FIELD_RATEUPLOAD));
        } else if (key.equals(FIELD_RATETOCLIENT_VIS)) {
            ret = Tools.getVisibleSpeed(jobj.getInt(FIELD_RATETOCLIENT));
        } else if (key.equals(FIELD_RATETOPEER_VIS)) {
            ret = Tools.getVisibleSpeed(jobj.getInt(FIELD_RATETOPEER));
        } else if (key.equals(FIELD_UPLOADEDEVER_VIS)) {
            ret = Tools.getFileSize((float) jobj.getDouble(FIELD_UPLOADEDEVER));
        } else if (key.equals(FIELD_COUNTRY)) {
            ret = getCountryByIP(jobj.getString(FIELD_ADDRESS));
        } else if (key.equals(FIELD_TYPE)) {
            ret = getFileType(jobj.getString(FIELD_NAME));
        } else if (key.equals(FIELD_RATETOCLIENT_VIS)) {
            ret = getCountryByIP(jobj.getString(FIELD_RATETOCLIENT));
        } else if (key.equals(FIELD_RATETOPEER_VIS)) {
            ret = getCountryByIP(jobj.getString(FIELD_RATETOPEER));
        } else if (key.equals(FIELD_PROGRESS_VIS)) {
            ret = Tools.formatDouble(jobj.getDouble(FIELD_PROGRESS) * 100.0, 2) + " %";
        } else if (key.equals(FIELD_FILE_PROGRESS)) {
            ret = Tools.calcPercentage(jobj.getLong(FIELD_BYTESCOMPLETED), jobj.getLong(FIELD_LENGTH)) + " %";
        } else {
            ret = jobj.getString(key);
        }

        return ret;
    }

    /**
     * Return string for visual table
     * @param column descriptor
     * @return value
     */
    @Override
    public Object getObjectValue(TorrentColumnsDesc column) {
        Object ret = "";

        switch (column) {
            case Id: {
                ret = getId();
            }
            break;
            case DoneProgress: {
                ret = Tools.getPercentage(getDoneProgress());
            }
            break;
            case Leechs: {
                ret = getLeechs();
            }
            break;
            case Name: {
                ret = getTorrentName();
            }
            break;
            case RateDn: {
                ret = Tools.getVisibleSpeed(getRateDn().floatValue());
            }
            break;
            case RateUp: {
                ret = Tools.getVisibleSpeed(getRateUp().floatValue());
            }
            break;
            case Seeds: {
                ret = getSeeds();
            }
            break;
            case Size: {
                ret = Tools.getFileSize(getHaveValid().floatValue());
                ret = Tools.getFileSize(getSize().floatValue());
            }
            break;
            case Status: {
                ret = getStatusDesc();
            }
            break;
            case Upload: {
                ret = Tools.getFileSize(getUpload().floatValue());
            }
            break;
        }

        return ret;
    }

    /**
     * @return the piecesArray
     */
    @Override
    public byte[] getPiecesArray() {
        return piecesArray;
    }

    /**
     * @return the pieceCount
     */
    @Override
    public Integer getPiecesCount() {
        return piecesCount;
    }

    /**
     * Parse json result buffer
     * @param result object for parsing
     * @param id id of torrent
     * @throws net.sf.json.JSONException fail exception
     */
    public void parseResult(JSONObject result, int id) throws net.sf.json.JSONException {
        if (result == null) {
            return;
        }
        JSONObject arguments = result.getJSONObject("arguments");
        JSONArray jsonArray = arguments.getJSONArray("torrents");

        //System.out.println("[parseResult][size]=" + jsonArray.size());

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject torrentObject = jsonArray.getJSONObject(i);
            int nid = Integer.parseInt(torrentObject.get(FIELD_ID).toString());
            if (nid == id) {
                jsonTorrent = torrentObject;
                parseResult();
            }
        }
    }

    private String safeJsonGetString(JSONObject jsonTorrent, String fieldName) {
        String val = null;
        try {
            val = jsonTorrent.getString(fieldName);
        } catch (JSONException ex) {
            val = null;
        }
        return val;
    }

    private String getStatusDesc(int status) {
        String ret = "";
        switch (status) {
            case STATUS_CHECK: {
                ret = resourceMap != null ? resourceMap.getString(STATUS_CHECK_DESC) : STATUS_CHECK_DESC;
            }
            break;
            case STATUS_CHECK_WAIT: {
                ret = resourceMap != null ? resourceMap.getString(STATUS_CHECK_WAIT_DESC) : STATUS_CHECK_WAIT_DESC;
            }
            break;
            case STATUS_DOWNLOAD: {
                ret = resourceMap != null ? resourceMap.getString(STATUS_DOWNLOAD_DESC) : STATUS_DOWNLOAD_DESC;
            }
            break;
            case STATUS_PAUSED: {
                ret = resourceMap != null ? resourceMap.getString(STATUS_PAUSED_DESC) : STATUS_PAUSED_DESC;
            }
            break;
            case STATUS_SEED: {
                ret = resourceMap != null ? resourceMap.getString(STATUS_SEED_DESC) : STATUS_SEED_DESC;
            }
            break;

        }
        return ret;
    }

    //----------------------- Torrent getters ----------------------------------
    @Override
    public List<TorrentFileDescInterface> getFilesDescList() {
        return filesDescList;
    }

    @Override
    public List<TorrentPeerDescInterface> getPeersDescList() {
        return peersDescList;
    }

    /**
     * How time torrent activity
     * @return string via mm.dd hh:mm:ss format
     */
    @Override
    public String getElapsedTimeString() {
        String ret = jsonTorrent.getString(FIELD_ETA);
        //long addedDate = Long.parseLong(getValue(FIELD_ADDEDDATE).toString());
        //long curreDate = System.currentTimeMillis()/1000;
        //long diffeDate = curreDate - addedDate;
        TimeSpan ts = TimeSpan.fromSeconds(getElapsedTime());
        ret = TimeSpan.formatTimespanLong(ts);
        return ret;
    }

    @Override
    public Long getElapsedTime() {
        Long ret = new Long(0);
        long addedDate = getDateAdded();// Long.parseLong(getValue(FIELD_ADDEDDATE).toString());
        long curreDate = System.currentTimeMillis() / 1000;
        ret = curreDate - addedDate;
        return ret;
    }

    /**
     * How rest time
     * @return string via mm.dd hh:mm:ss format
     */
    @Override
    public String getRemainingTimeString() {
        if (this.isFinished()) {
            return "";
        } else {
            double eta = getRemainingTime();//jsonTorrent.getDouble(ProtocolConstants.FIELD_ETA);
            if (eta > 0) {
                TimeSpan ts = TimeSpan.fromSeconds(eta);
                return TimeSpan.formatTimespanLong(ts);
            } else {
                return "";
            }
        }
    }

    @Override
    public Long getRemainingTime() {
        Long ret = new Long(0);
        if (!this.isFinished()) {
            ret = jsonTorrent.getLong(ProtocolConstants.FIELD_ETA);
        }
        return ret;
    }

    @Override
    public String getDownloadDir() {
        return jsonTorrent.getString(FIELD_DOWNLOADDIR);
    }

    /**
     * @todo implement method !!
     * @return
     */
    @Override
    public Boolean isHasError() {
        return hasError;
    }

    /**
     * @return the id
     */
    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public Integer getStatusCode() {
        return statusCode;
    }

    @Override
    public String getTorrentName() {
        return torrentName;
    }

    public double getRecheckPercentage() {
        double ret = jsonTorrent.getDouble(FIELD_RECHECKPROGRESS);
        ret = Tools.calcPercentage(ret, 0);
        return ret;
    }

    @Override
    public Double getDoneProgress() {
        double ret = 0.00;
        //ret = Tools.calcPercentage(jsonTorrent.getLong(FIELD_BYTESCOMPLETED ), jsonTorrent.getLong(FIELD_LENGTH));
        ret = Tools.calcPercentage(getHaveTotal(), getSize());
        if (getStatusCode() == STATUS_CHECK) {
            ret = getRecheckPercentage();
        }
        return ret;
    }

    @Override
    public Long getHaveTotal() {
        long ret =
                Tools.toLong(jsonTorrent.getString(FIELD_HAVEVALID))
                + Tools.toLong(jsonTorrent.getString(FIELD_HAVEUNCHECKED));
        return ret;
    }

    @Override
    public Long getHaveValid() {
        return Tools.toLong(jsonTorrent.getString(FIELD_HAVEVALID));
    }

    @Override
    public Long getSize() {
        Long ret = Tools.toLong(jsonTorrent.getString(FIELD_SIZEWHENDONE));
        return ret;
    }

    /**
     * @return the errorString
     */
    @Override
    public String getErrorString() {
        return errorString;
    }

    @Override
    public String getStatusDesc() {
        return getStatusDesc(getStatusCode());
    }

    @Override
    public Long getUpload() {
        Long ret = Tools.toLong(jsonTorrent.getString(FIELD_UPLOADEDEVER));
        return ret;
    }

    @Override
    public Double getRateDn() {
        Double ret = Tools.toDouble(jsonTorrent.getString(FIELD_RATEDOWNLOAD));
        return ret;
    }

    @Override
    public Double getRateUp() {
        Double ret = Tools.toDouble(jsonTorrent.getString(FIELD_RATEUPLOAD));
        return ret;
    }

    @Override
    public Long getLeechs() {
        Long ret = new Long(0);
//        ret = jsonTorrent.getLong(FIELD_LEECHERS);
        ret = jsonTorrent.getLong(FIELD_PEERSGETTINGFROMUS);
        return ret;
    }

    @Override
    public Long getSeeds() {
        Long ret = new Long(0);
//        ret = jsonTorrent.getLong(FIELD_SEEDERS);
        ret = jsonTorrent.getLong(FIELD_PEERSSENDINGTOUS);
        return ret;
    }

    @Override
    public Boolean isFinished() {
        return this.leftUntilDone() <= 0;
    }

    @Override
    public String getHashString() {
        return jsonTorrent.getString(ProtocolConstants.FIELD_HASHSTRING);
    }

    @Override
    public String getComment() {
        return jsonTorrent.getString(FIELD_COMMENT);
    }

    @Override
    public String getCreator() {
        return jsonTorrent.getString(FIELD_CREATOR);
    }

    @Override
    public Long getDateCreated() {
        return jsonTorrent.getLong(FIELD_DATECREATED);
    }

    @Override
    public Long getDateAdded() {
        return jsonTorrent.getLong(FIELD_ADDEDDATE);
    }

    //----------------------- Torrent getters ----------------------------------
    private String getFileType(String file) {
        String ret = "-";
        return ret;
    }

    private String getCountryByIP(String address) {
        String ret = address;

        return ret;
    }

    public long leftUntilDone() {
        return jsonTorrent.getLong(FIELD_LEFTUNTILDONE);
    }
    //----------------------- Torrent getters ----------------------------------
    @Override
    public String toString() {
        String ret = "Torrent: ";
        ret += getTorrentName()+" "+getSize();
        return ret;
    }

    @Override
    public void setResourceBundle(ResourceMap resourceMap) {
        this.resourceMap = resourceMap;
    }

} //~

