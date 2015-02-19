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

import net.sf.json.JSONObject;

/**
 *
 * @author dstarzhynskyi
 */
public class TransmissionDescriptor implements ProtocolConstants {

    private String version = "1.41";
    private int rpcVersion = -1;
    private String downloadDir = "";

    private JSONObject sessionData;
    private final JSONObject response;

    public TransmissionDescriptor(JSONObject response){
        this.response = response;
        parseResponse();
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the rpcVersion
     */
    public int getRpcVersion() {
        return rpcVersion;
    }

    /**
     * @param rpcVersion the rpcVersion to set
     */
    public void setRpcVersion(int rpcVersion) {
        this.rpcVersion = rpcVersion;
    }

    /**
     * @return the sessionData
     */
    public JSONObject getSessionData() {
        return sessionData;
    }

    /**
     * @param sessionData the sessionData to set
     */
    public void setSessionData(JSONObject sessionData) {
        this.sessionData = sessionData;
    }


    private void parseResponse() {
        if(response!=null){
            sessionData = response.getJSONObject(KEY_ARGUMENTS);
            downloadDir = sessionData.getString(FIELD_DOWNLOAD_DIR);
            version = sessionData.getString(FIELD_VERSION);
            rpcVersion = sessionData.getInt(FIELD_RPC_VERSION);
        }
    }

    /**
     * @return the downloadDir
     */
    public String getDownloadDir() {
        return downloadDir;
    }

    /**
     * @param downloadDir the downloadDir to set
     */
    public void setDownloadDir(String downloadDir) {
        this.downloadDir = downloadDir;
    }

}
