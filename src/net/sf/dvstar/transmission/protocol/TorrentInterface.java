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


package net.sf.dvstar.transmission.protocol;

import java.util.List;
import org.jdesktop.application.ResourceMap;

/**
 * Interface for manipulate torrents
 * @author dstarzhynskyi
 *
 * @todo make Torrent interface like as Bean for invoke methods
 */
public interface TorrentInterface extends TorrentConstants {

    public void setResourceBundle(ResourceMap resourceMap);

    public Integer getId();

    public String getHashString();

    public Integer getStatusCode();

    public String getStatusDesc();

    public String getTorrentName();

//    public String getTorrentDir();

    public String getComment();

    public String getCreator();

    public Double getDoneProgress();

    public Long getHaveTotal();

    public Long getHaveValid();

    public Long getSize();

    public Long getUpload();

    public String getErrorString();

    public Double getRateDn();

    public Double getRateUp();

    public Long getLeechs();

    public Long getSeeds();

    public byte[] getPiecesArray();

    public Integer getPiecesCount();

    public Boolean isHasError();

    public Boolean isFinished();

    public String getElapsedTimeString();

    public Long getElapsedTime();

    public Long getDateCreated();

    public Long getDateAdded();

    public String getRemainingTimeString();

    public Long getRemainingTime();

    public String getDownloadDir();

    public Object getObjectValue(TorrentColumnsDesc column);

    public List<TorrentFileDescInterface> getFilesDescList();

    public List<TorrentPeerDescInterface> getPeersDescList();

    public interface TorrentFileDescInterface {

        public String getPath();        // FIELD_NAME

        public String getDir();        // FIELD_NAME

        public String getFileType();    // FIELD_TYPE

        public String getSize();        // FIELD_LENGTH_VIS, // VISUAL

        public String getCompleted();   // FIELD_BYTESCOMPLETED_VIS,// VISUAL

        public String getProgress();    // FIELD_FILE_PROGRESS // VISUAL

        public Boolean getWanted();

        public String getPriority();

        public Object getObjectValue(TorrentFileColumnsDesc column);

        public void setWanted(boolean wanted);
    }

    public interface TorrentPeerDescInterface {

        public String getAddress();

        public String getCountry();

        public String getFlags();

        public String getClient();

        public Integer getPort();

        public String getProgress();

        public String getRateDn();

        public String getRateUp();

        public Object getObjectValue(TorrentPeerColumnsDesc column);
    }
}
