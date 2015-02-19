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

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.Rectangle;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import net.sf.dvstar.transmission.utils.ConfigStorage;
import net.sf.dvstar.transmission.utils.LocalSettiingsConstants;

/**
 *
 * @author dstarzhynskyi
 */
public class TorrentsTableCustomRenderer extends DefaultTableCellRenderer implements ProtocolConstants, LocalSettiingsConstants {

    DateFormat dateFormatter = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM);
    NumberFormat numberFormatter = NumberFormat.getInstance();
    Color colorOdd = new Color(0xe1e1e1);
    private final TorrentsTableModel loader;
    private boolean colorStatus = true;

    /**
     *
     * @param loader
     * @param noColor no color for rows by status
     */
    public TorrentsTableCustomRenderer(TorrentsTableModel loader) {
        this(loader, true);
    }

    public TorrentsTableCustomRenderer(TorrentsTableModel loader, boolean colorStatus) {
        this.loader = loader;
        this.colorStatus = colorStatus;
        prepareData();
    }

    public static Rectangle getColumnBounds(JTable table, int column) {
        //checkColumn(table, column);

        Rectangle result = table.getCellRect(-1, column, true);
        Insets i = table.getInsets();

        result.y = i.top;
        result.height = table.getVisibleRect().height;//   table.getHeight() - i.top - i.bottom;

        return result;
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {

        super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);

        Component comp = this;

        //  Code for data formatting
        Border selected = new LineBorder(Color.blue);


        setHorizontalAlignment(SwingConstants.LEFT);


        if (table.getColumnClass(column).getName().equals(JProgressBar.class.getName())) {
        }

        if (table.getColumnClass(column) == Boolean.class) {
        }

        /*
        if ( column == 3 && table.getModel() instanceof TorrentsTableModel)   {
        comp = new JProgressBar(0,100);
        ((JProgressBar)comp).setStringPainted(true);
        ((JProgressBar)comp).setForeground(Color.green);
        //((JProgressBar)comp).setBorder( new EmptyBorder(0,0,0,0) );
        ((JProgressBar)comp).setBorder( new BevelBorder( BevelBorder.RAISED ) );
        TorrentsTableModel model=((TorrentsTableModel)table.getModel());
        Torrent torrent = model.getTableDataTorrents().get( table.convertRowIndexToModel(row) );
        ((JProgressBar)comp).setValue((int) torrent.getDoneProgress().doubleValue() );
        }
         */

        /*
        if (value instanceof Date) {
        setText(dateFormatter.format((Date) value));
        }

        if (value instanceof Number) {
        setHorizontalAlignment(SwingConstants.RIGHT);

        if (value instanceof Double) {
        setText(numberFormatter.format(((Number) value).floatValue()));
        }
        }
         */
        //  Code for highlighting

        if (!isSelected) {
            //    String type = (String) table.getModel().getValueAt(row, 0);
            setBackground(row % 2 == 0 ? null : colorOdd);
        }

        /*
        System.out.println("TR_STATUS_CHECK_WAIT="+TR_STATUS_CHECK_WAIT);
        System.out.println("TR_STATUS_CHECK="+TR_STATUS_CHECK);
        System.out.println("TR_STATUS_DOWNLOAD="+TR_STATUS_DOWNLOAD);
        System.out.println("TR_STATUS_SEED="+TR_STATUS_SEED);
        System.out.println("TR_STATUS_STOPPED="+TR_STATUS_STOPPED);
         */

        if (colorStatus) {

            //int mrow = table.getRowSorter().convertRowIndexToModel(row);
            int mrow = table.convertRowIndexToModel(row);
            //if(table.getRowSorter()!=null) {
            //    mrow = table.getRowSorter().convertRowIndexToModel(row);
            //}
            if (mrow < 0) {
                return comp;
            }

            int statusCode = loader.getTableDataTorrents().get(mrow).getStatusCode();
            boolean hasError = loader.getTableDataTorrents().get(mrow).isHasError();

            if (statusCode == TR_STATUS_PAUSED) {
                setForeground(colCONF_CLI_COL_STATUS_STOPPED);
            } else if (statusCode == TR_STATUS_DOWNLOAD) {
                setForeground(colCONF_CLI_COL_STATUS_DOWNLOAD);
            } else if (statusCode == TR_STATUS_CHECK_WAIT) {
                setForeground(colCONF_CLI_COL_STATUS_CHECK_WAIT);
            } else if (statusCode == TR_STATUS_CHECK) {
                setForeground(colCONF_CLI_COL_STATUS_CHECK);
            } else if (statusCode == TR_STATUS_SEED) {
                setForeground(colCONF_CLI_COL_STATUS_SEED);
            } else {
                setForeground(Color.black);
            }

            if (hasError) {
                setForeground(colCONF_CLI_COL_STATUS_ERROR);
            }

        }

        /*
        if (loader.isCellChanged(row, column)) {
        setForeground(Color.red);
        } else {
        setForeground(Color.black);
        }
         */

        /*
        if(table.isColumnSelected(column)){
        setBackground(colorOdd);
        //System.out.println("----getColumnBounds(table, column)"+getColumnBounds(table, column));
        table.repaint(   getColumnBounds(table, column)   );
        }
         */

        if (table.isRowSelected(row) //&& table.isColumnSelected(column)
                ) {
            setBorder(selected);
        }

        return comp;
    }
    Color colCONF_CLI_COL_STATUS_CHECK_WAIT = Color.darkGray;
    Color colCONF_CLI_COL_STATUS_CHECK = Color.GRAY;
    Color colCONF_CLI_COL_STATUS_DOWNLOAD = Color.GREEN;
    Color colCONF_CLI_COL_STATUS_SEED = Color.BLUE;
    Color colCONF_CLI_COL_STATUS_STOPPED = Color.ORANGE;
    Color colCONF_CLI_COL_STATUS_ERROR = Color.RED;

    private void prepareData() {

        ConfigStorage configStorage = new ConfigStorage();
        configStorage.loadConfig();
        Properties prop = configStorage.getStoredConfig();

        colCONF_CLI_COL_STATUS_CHECK_WAIT = configStorage.propertiesManager(CONF_CLI_COL_STATUS_CHECK_WAIT, prop, colCONF_CLI_COL_STATUS_CHECK_WAIT, DIR_SET);
        colCONF_CLI_COL_STATUS_CHECK = configStorage.propertiesManager(CONF_CLI_COL_STATUS_CHECK, prop, colCONF_CLI_COL_STATUS_CHECK, DIR_SET);
        colCONF_CLI_COL_STATUS_DOWNLOAD = configStorage.propertiesManager(CONF_CLI_COL_STATUS_DOWNLOAD, prop, colCONF_CLI_COL_STATUS_DOWNLOAD, DIR_SET);
        colCONF_CLI_COL_STATUS_SEED = configStorage.propertiesManager(CONF_CLI_COL_STATUS_SEED, prop, colCONF_CLI_COL_STATUS_SEED, DIR_SET);
        colCONF_CLI_COL_STATUS_STOPPED = configStorage.propertiesManager(CONF_CLI_COL_STATUS_STOPPED, prop, colCONF_CLI_COL_STATUS_STOPPED, DIR_SET);

    }
}
