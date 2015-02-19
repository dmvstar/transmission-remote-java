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

/**
 *
 * @author sdv
 */
public interface TorrentConstants {

    public enum TorrentFilterFieldDesc {
        FIELD_ID,
        FIELD_LOCATION,
        FIELD_COUNTRY,
    	FIELD_METAINFO,
        FIELD_CLIENTNAME,
        FIELD_HAVEUNCHECKED,
        FIELD_LEFTUNTILDONE,
        FIELD_COMMENT,
        FIELD_NAME,
        FIELD_TYPE,
        FIELD_ERRORSTRING,
        FIELD_PROGRESS,
        FIELD_FILENAME,
        FIELD_ANNOUNCEURL,
        FIELD_STATUS,
        FIELD_BANDWIDTHPRIORITY,
        FIELD_RATEDOWNLOAD,
        FIELD_RATEUPLOAD,
        FIELD_TOTALSIZE,
        FIELD_HAVEVALID,
        FIELD_UPLOADEDEVER,
        FIELD_LEECHERS,
        FIELD_SEEDERS,
        FIELD_ADDEDDATE
    }

    public enum TorrentColumnsDesc {
        Id, Name, Size, DoneProgress,
        Status, Seeds, Leechs,
        RateDn, RateUp, Upload
    }

    public static final TorrentColumnsDesc[] columnIndexDescs = {
        TorrentColumnsDesc.Id,
        TorrentColumnsDesc.Name,
        TorrentColumnsDesc.Size,
        TorrentColumnsDesc.DoneProgress,
        TorrentColumnsDesc.Status,
        TorrentColumnsDesc.Seeds,
        TorrentColumnsDesc.Leechs,
        TorrentColumnsDesc.RateDn,
        TorrentColumnsDesc.RateUp,
        TorrentColumnsDesc.Upload
    };

    public enum TorrentFileColumnsDesc {
        Path, Type, Size, Complete, Progress, Wanted, Priority
    }

    public static final TorrentFileColumnsDesc[] columnFilesIndexDescs = {
        TorrentFileColumnsDesc.Path,
        TorrentFileColumnsDesc.Type,
        TorrentFileColumnsDesc.Size,
        TorrentFileColumnsDesc.Complete,
        TorrentFileColumnsDesc.Progress,
        TorrentFileColumnsDesc.Wanted,
        TorrentFileColumnsDesc.Priority
    };

    public enum TorrentPeerColumnsDesc {
        Address, Country, Flags, Client,
        Port, Progress, RateDn, RateUp
    }

    public static final TorrentPeerColumnsDesc[] columnPeersIndexDescs = {
        TorrentPeerColumnsDesc.Address, // FIELD_ADDRESS,
        TorrentPeerColumnsDesc.Country, // FIELD_COUNTRY,
        TorrentPeerColumnsDesc.Flags,   // FIELD_FLAGSTR,
        TorrentPeerColumnsDesc.Client,  // FIELD_CLIENTNAME,
        TorrentPeerColumnsDesc.Port,    // FIELD_PORT,
        TorrentPeerColumnsDesc.Progress,// FIELD_PROGRESS_VIS, // VISUAL
        TorrentPeerColumnsDesc.RateDn,  // FIELD_RATETOCLIENT_VIS, // VISUAL
        TorrentPeerColumnsDesc.RateUp   // FIELD_RATETOPEER_VIS
    };

    public static class DestTorrent {
        public String  destPath = null;
        public boolean pausedTorrent = true;
        public List   fileWantedList = null;
        public List   fileUnWantedList = null;
        public List   filePriority = null;
    }


}
