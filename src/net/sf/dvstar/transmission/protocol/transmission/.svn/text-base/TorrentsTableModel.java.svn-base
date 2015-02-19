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
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import net.sf.dvstar.transmission.utils.LocalSettiingsConstants;
import net.sf.dvstar.transmission.utils.Tools;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 *
 * @author dstarzhynskyi
 */
public class TorrentsTableModel implements TableModel, ProtocolConstants, LocalSettiingsConstants, TorrentConstants {

    private List<Torrent> tableDataTorrents;
    JSONObject result;
    private org.jdesktop.application.ResourceMap resourceMap;

    public static final double[] prefWidths = new double[]{
        2, 48, 10, 10, 10, 5, 5, 5, 5, 5
    };

    private String[] columnNames = null;
    private boolean parsed = false;
    /*
    {
        "№", "Name", "Size", "Progress", "Status", "Seed", "Leech", "Down Speed", "Up Speed","Upload"
    };
    */

    public TorrentsTableModel(JSONObject result, TransmissionManager manager) {
        this.resourceMap = manager.getGlobalResourceMap();
        fillLocaleColumnNames();
        if (result != null) {
            this.result = result;
            tableDataTorrents = new ArrayList();
            parsed = false;
            parseResult();
            parsed = true;
        } else {
            tableDataTorrents = null;
        }
    }

    public TorrentsTableModel(TransmissionManager manager) {
        this(null, manager);
    }

    @Override
    public int getRowCount() {
        int ret = 0;
        if (getTableDataTorrents() != null && parsed) {
            ret = getTableDataTorrents().size();
        }
        return ret;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        String ret = columnNames[columnIndex];
        return ret;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0 || columnIndex == 7 || columnIndex == 8) {
            return Integer.class;
        }
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object ret = "";
        if (getTableDataTorrents() != null && rowIndex >=0) {
            if (rowIndex < getTableDataTorrents().size()) {

                Torrent torrent = getTableDataTorrents().get(rowIndex);
                                      //columnIndexDescs[columnIndex]
                //ret = torrent.getValue( columnIndexNames[columnIndex] );
                ret = torrent.getObjectValue( columnIndexDescs[columnIndex] );

            }
        }
        return ret;
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

    /**
     * @todo make Exceprions on error
     *
     */
    private void parseResult() {
        JSONObject arguments = result.getJSONObject("arguments");
        JSONArray jsonArray = arguments.getJSONArray("torrents");

//System.out.println("[parseResult][size]=" + jsonArray.size());

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject torrentObject = jsonArray.getJSONObject(i);

            Torrent torrentItem = new Torrent(torrentObject, resourceMap);
            getTableDataTorrents().add(torrentItem);

        }
//System.out.println("[parseResult][load]=" + getTableDataTorrents().size());
    }

    public static void setPreferredColumnWidths(JTable table) {
        setPreferredColumnWidths(table, prefWidths);
    }

    public static void setPreferredColumnWidths(JTable table, double[] percentages) {
        Dimension tableDim = table.getPreferredSize();

        double total = 0;
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            if (table.getColumnModel().getColumn(i).getResizable()) {
                total += percentages[i];
            }
        }
//System.out.print("[Table] ");
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            int width = (int) (tableDim.width * (percentages[i] / total));
            if (column.getResizable()) {
                column.setPreferredWidth(width);
            }
//System.out.print("[" + i + "][" + column.getResizable() + "][" + width + "]");
        }
//System.out.println();
    }

    boolean isCellChanged(int row, int column) {
        return false;
    }

    /**
     * @return the tableDataTorrents
     */
    public List<Torrent> getTableDataTorrents() {
        return tableDataTorrents;
    }

    private void fillLocaleColumnNames() {

        String namesList = resourceMap.getString("tblTorrentList.columnModel.column.titles");
        if(namesList!=null) {
            columnNames = Tools.getStringArray(namesList, ",");
        }
        
    }

}


