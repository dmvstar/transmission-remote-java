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

import net.sf.dvstar.transmission.protocol.transmission.ProtocolConstants;

/**
 *
 * @author dstarzhynskyi
 */
public interface TorrentFieldsInterface {
    //--------------- Table Column and fields description ----------------------

    public interface TorrentFieldInterface extends ProtocolConstants, TorrentConstants {

        public static String COLUMN_RES_DESC = "tblTorrentList.columnModel.column.titles";
        public static String names[] = {
            "№", "Name",
            "Size","Progress",
            "Status",
            "Seed","Leech",
            "Dn speep","Up speed",
            "Upload"
        };
        public static final String[] fields = {
            FIELD_ID, FIELD_NAME,
            FIELD_TOTALSIZE, FIELD_DONEPROGRESS,
            FIELD_STATUS,
            FIELD_SEEDERS, FIELD_LEECHERS,
            FIELD_RATEDOWNLOAD_VIS, FIELD_RATEUPLOAD_VIS,
            FIELD_UPLOADEDEVER_VIS
        };
    }

    public interface FilesFieldInterface extends ProtocolConstants {

        public static String COLUMN_RES_DESC = "tblTorrentFiles.columnModel.titles";
        public static String names[] = {
            "Path",
            "Type",
            "Size",
            "Complete",
            "Done",
            "Wait",
            "Priority"
        };

        public static String fields[] = {
            FIELD_NAME,
            FIELD_TYPE,
            FIELD_LENGTH_VIS, // VISUAL
            FIELD_BYTESCOMPLETED_VIS,// VISUAL
            FIELD_FILE_PROGRESS // VISUAL
            ,FILED_STATS_WANTED
            ,FILED_STATS_PRIORITY
        };

        public static String files[] = {
            FIELD_NAME,
            FIELD_TYPE,
            FIELD_LENGTH_VIS, // VISUAL
            FIELD_BYTESCOMPLETED_VIS,// VISUAL
            FIELD_FILE_PROGRESS // VISUAL
        };

        public static String stats[] = {
//            FILED_STATS_COMPLETED
            FILED_STATS_WANTED
            ,FILED_STATS_PRIORITY
        };
    }

    public interface FilesStatsInterface extends ProtocolConstants {

        public static String COLUMN_RES_DESC = "tblTorrentFiles.columnModel.titles";
        public static String names[] = {
            "Complete"
            ,"Wanted"
            ,"Priority"
        };
        public static String fields[] = {
            FILED_STATS_COMPLETED
            ,FILED_STATS_WANTED
            ,FILED_STATS_PRIORITY
        };
    }


    public interface PeersFieldInterface extends ProtocolConstants {

        public static String COLUMN_RES_DESC = "tblTorrentPeers.columnModel.titles";
        public static String names[] = {
            "Address",
            "Country",
            "Flags",
            "Client",
            "Port",
            "Progress",
            "Down Rate",
            "UP Rate"
        };
        public static String fields[] = {
            FIELD_ADDRESS,
            FIELD_COUNTRY,
            FIELD_FLAGSTR,
            FIELD_CLIENTNAME,
            FIELD_PORT,
            FIELD_PROGRESS_VIS, // VISUAL
            FIELD_RATETOCLIENT_VIS, // VISUAL
            FIELD_RATETOPEER_VIS // VISUAL
        //            "clientIsChoked",
        //            "clientIsInterested",
        //            "isDownloadingFrom",
        //            "isEncrypted",
        //            "isIncoming",
        //            "isUploadingTo",
        //            "peerIsChoked",
        //            "peerIsInterested",
        };
    }

}
