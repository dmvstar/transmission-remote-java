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

import net.sf.dvstar.transmission.protocol.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import net.sf.dvstar.transmission.protocol.TorrentConstants.TorrentFileColumnsDesc;
import net.sf.dvstar.transmission.protocol.TorrentConstants.TorrentPeerColumnsDesc;
import net.sf.dvstar.transmission.protocol.TorrentFieldsInterface.FilesFieldInterface;
import net.sf.dvstar.transmission.protocol.TorrentFieldsInterface.PeersFieldInterface;
import net.sf.dvstar.transmission.protocol.TorrentInterface.TorrentFileDescInterface;
import net.sf.dvstar.transmission.protocol.TorrentInterface.TorrentPeerDescInterface;
import net.sf.dvstar.transmission.utils.LocalSettiingsConstants;
import net.sf.dvstar.transmission.utils.Tools;
import net.sf.json.JSONObject;
import org.jdesktop.application.ResourceMap;

/**
 *
 * @author dstarzhynskyi
 */
public abstract class JSONMapModel extends DefaultTableModel implements TableModel, ProtocolConstants, LocalSettiingsConstants, TorrentConstants {

    protected Torrent torrent;
    protected final TransmissionManager manager;
    protected final ColumnsDescriptor columnsDescriptor;
    protected final ResourceMap globalResourceMap;
    List<TorrentFileDescInterface> filesDescList;
    List<TorrentPeerDescInterface> peersDescList;

    public ColumnsDescriptor getColumnsDescriptor() {
        return columnsDescriptor;
    }

    public JSONMapModel(TransmissionManager manager, Torrent torrent, ColumnsDescriptor columnsDescriptor) {
        this.manager = manager;
        this.torrent = torrent;
        this.columnsDescriptor = columnsDescriptor;
        this.globalResourceMap = manager.getGlobalResourceMap();
    }

    @Override
    public int getColumnCount() {
        return columnsDescriptor.getColumnCount();
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnsDescriptor.getColumn(columnIndex).columnName;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
    }

    public static class ColumnDescriptor {

        int columnIndex;
        Class<?> columnType;
        String columnName;
        String columnField;
        boolean editable = false;

        public ColumnDescriptor(
                int columnIndex,
                Class<?> columnType,
                String columnName,
                String columnIdent,
                boolean editable) {
            this.columnIndex = columnIndex;
            this.columnType = columnType;
            this.columnField = columnIdent;
            this.columnName = columnName;
            this.editable = editable;
        }

        public ColumnDescriptor(
                int columnIndex,
                Class<?> columnType,
                String columnName,
                String columnIdent) {
            this(columnIndex, columnType,
                    columnName, columnIdent,
                    false);
        }
    }

    public static class ColumnsDescriptor {

        List<ColumnDescriptor> columns = new ArrayList();

        public ColumnsDescriptor(ColumnDescriptor columnDescriptor[]) {
            columns = new ArrayList<ColumnDescriptor>();
            if (columnDescriptor != null) {
                for (int i = 0; i < columnDescriptor.length; i++) {
                    addColumn(columnDescriptor[i]);
                }
            }
        }

        public void addColumn(ColumnDescriptor column) {
            columns.add(column);
        }

        public ColumnDescriptor getColumn(int index) {
            return columns.get(index);
        }

        public int getColumnCount() {
            return columns.size();
        }

        public ColumnDescriptor getColumn(String key) throws TorrentsCommonException {
            ColumnDescriptor ret = null;
            int index = findIndex(key);
            if (index >= 0) {
                ret = columns.get(index);
            } else {
                throw new TorrentsCommonException("Key [" + key + "]not found !");
            }
            return ret;
        }

        private int findIndex(String key) {
            return -1;
        }
    }

    //--------------------------------------------------------------------------
    public static class JSONMapModelFiles extends JSONMapModel {

        //List<Files> filesList;
        public JSONMapModelFiles(TransmissionManager manager, Torrent torrent, ColumnsDescriptor columnsDescriptor) {
            super(manager, torrent, columnsDescriptor);
//            if(torrent!=null) {
//                filesList = this.torrent.getFilesList();
//            }
        }

        public void setTorrent(Torrent torrent) {
            this.torrent = torrent;
            if (this.torrent != null) {
                this.filesDescList = torrent.getFilesDescList();
            }
//            filesList = torrent.getFilesList();
            this.fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            int ret = 0;
            if (filesDescList != null) {
                ret = filesDescList.size();
            }
            return ret;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            Class<?> ret = columnsDescriptor.getColumn(columnIndex).columnType;// String.class;
            return ret;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            boolean ret = false;
            ret = columnsDescriptor.getColumn(columnIndex).editable;
            return ret;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            TorrentFileDescInterface tfiles = torrent.getFilesDescList().get(rowIndex);
            if(aValue.getClass() == Boolean.class) {
                tfiles.setWanted( !tfiles.getWanted() );
            }
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            TorrentFileDescInterface tfiles = torrent.getFilesDescList().get(rowIndex);
            Object ret = tfiles.getObjectValue(columnFilesIndexDescs[columnIndex]);

            //Files files = filesList.get(rowIndex);
            //ret = files.getValue( getColumnsDescriptor().getColumn(columnIndex).columnField );

            return ret;
        }
    }

    //--------------------------------------------------------------------------
    public static class JSONMapModelPeers extends JSONMapModel {
        //List<Peers> peersList;

        public JSONMapModelPeers(TransmissionManager manager, Torrent torrent, ColumnsDescriptor columnsDescriptor) {
            super(manager, torrent, columnsDescriptor);
            //if(torrent!=null) {
            //    peersList = this.torrent.getPeersList();
            //}
        }

        public void setTorrent(Torrent torrent) {
            this.torrent = torrent;
            if (this.torrent != null) {
                this.peersDescList = torrent.getPeersDescList();
            }
            //peersList = torrent.getPeersList();
            this.fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            int ret = 0;
            if (peersDescList != null) {
                ret = peersDescList.size();
            }
            return ret;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            //Peers files = peersList.get(rowIndex);
            //Object ret = files.getValue(columns[columnIndex]);
            //Object ret = files.getValue( getColumnsDescriptor().getColumn(columnIndex).columnField );
            TorrentPeerDescInterface tpeers = torrent.getPeersDescList().get(rowIndex);
            Object ret = tpeers.getObjectValue(columnPeersIndexDescs[columnIndex]);

            return ret;
        }
    }

    //--------------------------------------------------------------------------
    public static class MapContainer {

        protected Map values;
        protected JSONObject jsonObj;

        public void setValue(String key, String val) {
            values.put(key, val);
        }

        public String getValue(String key) {
            String ret = "";
            ret = (String) values.get(key);
            return ret;
        }

        public void parseJSONObj() {
        }
    }

    //--------------------------------------------------------------------------
    public static class Files extends MapContainer implements FilesFieldInterface, TorrentFileDescInterface {

        public Files(JSONObject jsonObj) {
            super();
            super.values = new HashMap();
            super.jsonObj = jsonObj;
        }

        @Override
        public String getPath() {
            return jsonObj.getString(FIELD_NAME);
        }

        @Override
        public String getFileType() {
            return ("-");
        }

        @Override
        public String getSize() {
            Long fsize = jsonObj.getLong(FIELD_LENGTH);
            String ret = Tools.getFileSize(fsize.floatValue());
            return ret;
        }

        @Override
        public String getCompleted() {
            Long fsize = jsonObj.getLong(FIELD_BYTESCOMPLETED);
            String ret = Tools.getFileSize(fsize.floatValue());
            return ret;
        }

        @Override
        public String getProgress() {
            String ret = Tools.calcPercentage(jsonObj.getLong(FIELD_BYTESCOMPLETED), jsonObj.getLong(FIELD_LENGTH)) + " %";
            return ret;
        }

        @Override
        public Boolean getWanted() {
            Boolean ret = Boolean.parseBoolean((String) values.get(FILED_STATS_WANTED));
            return ret;
        }

        @Override
        public String getPriority() {
            String ret = (String) values.get(FILED_STATS_PRIORITY);
            ret = Tools.formatPriority( Integer.parseInt(ret) );
            return ret;
        }

        @Override
        public Object getObjectValue(TorrentFileColumnsDesc column) {
            Object ret = "";
            switch (column) {
                case Path: {
                    ret = getPath();
                }
                break;
                case Type: {
                    ret = getFileType();
                }
                break;
                case Size: {
                    ret = getSize();
                }
                break;
                case Progress: {
                    ret = getProgress();
                }
                break;
                case Complete: {
                    ret = getCompleted();
                }
                break;
                case Wanted: {
                    ret = getWanted();
                }
                break;
                case Priority: {
                    ret = getPriority();
                }
                break;

            }
            return ret;
        }

        @Override
        public void setWanted(boolean wanted) {
            values.put(FILED_STATS_WANTED, ""+wanted);
        }

        @Override
        public String getDir() {
            String ret = "";
            int indexSlash = getPath().indexOf('/');
            if( indexSlash > 0 ){
                ret = getPath().substring(0, indexSlash+1);
            }
            return ret;
        }
    }

    //--------------------------------------------------------------------------
    public static class Peers extends MapContainer implements PeersFieldInterface, TorrentPeerDescInterface {

        public Peers(JSONObject jsonObj) {
            super();
            super.values = new HashMap();
            super.jsonObj = jsonObj;
        }

        @Override
        public String getAddress() {
            return jsonObj.getString(FIELD_ADDRESS);
        }

        @Override
        public String getCountry() {
            return Tools.getCountryByAddress(jsonObj.getString(FIELD_ADDRESS));
        }

        @Override
        public String getFlags() {
            return jsonObj.getString(FIELD_FLAGSTR);
        }

        @Override
        public String getClient() {
            return jsonObj.getString(FIELD_CLIENTNAME);
        }

        @Override
        public Integer getPort() {
            return jsonObj.getInt(FIELD_PORT);
        }

        @Override
        public String getProgress() {
            return Tools.formatDouble(jsonObj.getDouble(FIELD_PROGRESS) * 100.0, 2) + " %";
        }

        @Override
        public String getRateDn() {
            return Tools.getVisibleSpeed(jsonObj.getLong(FIELD_RATETOCLIENT));
        }

        @Override
        public String getRateUp() {
            return Tools.getVisibleSpeed(jsonObj.getLong(FIELD_RATETOPEER));
        }

        @Override
        public Object getObjectValue(TorrentPeerColumnsDesc column) {
            Object ret = "";
            switch (column) {
                case Address: {
                    ret = getAddress();
                }
                break;
                case Country: {
                    ret = getCountry();
                }
                break;
                case Flags: {
                    ret = getFlags();
                }
                break;
                case Client: {
                    ret = getClient();
                }
                break;
                case Port: {
                    ret = getPort();
                }
                break;
                case Progress: {
                    ret = getProgress();
                }
                break;
                case RateDn: {
                    ret = getRateDn();
                }
                break;
                case RateUp: {
                    ret = getRateUp();
                }
                break;
            }
            return ret;
        }
    }
    //--------------------------------------------------------------------------
}
