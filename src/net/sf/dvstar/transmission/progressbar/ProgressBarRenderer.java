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


package net.sf.dvstar.transmission.progressbar;

import java.awt.Color;
import java.awt.Component;
import java.util.Properties;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import javax.swing.table.TableCellRenderer;
import net.sf.dvstar.transmission.protocol.transmission.ProtocolConstants;
import net.sf.dvstar.transmission.protocol.transmission.Torrent;
import net.sf.dvstar.transmission.protocol.transmission.TorrentsTableModel;
import net.sf.dvstar.transmission.utils.ConfigStorage;
import net.sf.dvstar.transmission.utils.LocalSettiingsConstants;
import net.sf.dvstar.transmission.utils.Tools;

/**
 *
 * @author sdv
 */
public class ProgressBarRenderer extends JProgressBar implements TableCellRenderer, ProtocolConstants, LocalSettiingsConstants {

    Color colCONF_PB_025_B = Color.PINK;
    Color colCONF_PB_050_B = Color.ORANGE;
    Color colCONF_PB_075_B = Color.CYAN;
    Color colCONF_PB_100_B = Color.BLUE;
    Color colCONF_PB_100_A = Color.GREEN;

    private Torrent torrent = null;

    public ProgressBarRenderer() {
        super();
        prepareData();
    }

    @Override
    public String getString(){
        String ret = super.getString();
        if(torrent != null){
            ret += "["+Tools.getFileSize( torrent.getDoneProgress().floatValue() )+"]";
        }
        return ret;
    }

    /**
     * Prepare color and other attrs for progresbar
     */
    private void prepareData() {

        ConfigStorage configStorage = new ConfigStorage();
        configStorage.loadConfig();
        Properties prop = configStorage.getStoredConfig();

        colCONF_PB_025_B = configStorage.propertiesManager(CONF_CLI_COL_PB_025_B, prop, colCONF_PB_025_B, DIR_SET);
        colCONF_PB_050_B = configStorage.propertiesManager(CONF_CLI_COL_PB_050_B, prop, colCONF_PB_050_B, DIR_SET);
        colCONF_PB_075_B = configStorage.propertiesManager(CONF_CLI_COL_PB_075_B, prop, colCONF_PB_075_B, DIR_SET);
        colCONF_PB_100_B = configStorage.propertiesManager(CONF_CLI_COL_PB_100_B, prop, colCONF_PB_100_B, DIR_SET);
        colCONF_PB_100_A = configStorage.propertiesManager(CONF_CLI_COL_PB_100_A, prop, colCONF_PB_100_A, DIR_SET);

    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component comp = new JProgressBar(0, 100);
        ((JProgressBar) comp).setStringPainted(true);
        //((JProgressBar)comp).setBorder( new EmptyBorder(0,0,0,0) );
        ((JProgressBar) comp).setBorder(new BevelBorder(BevelBorder.RAISED));
        TorrentsTableModel tmodel = ((TorrentsTableModel) table.getModel());
        torrent = tmodel.getTableDataTorrents().get(table.convertRowIndexToModel(row));
        int pvalue = (int) torrent.getDoneProgress().doubleValue();
        ((JProgressBar) comp).setValue(pvalue);
        if (pvalue < 25) {
            ((JProgressBar) comp).setForeground(colCONF_PB_025_B);
        } else if (pvalue < 50) {
            ((JProgressBar) comp).setForeground(colCONF_PB_050_B);
        } else if (pvalue < 75) {
            ((JProgressBar) comp).setForeground(colCONF_PB_075_B);
        } else if (pvalue < 100) {
            ((JProgressBar) comp).setForeground(colCONF_PB_100_B);
        } else {
            ((JProgressBar) comp).setForeground(colCONF_PB_100_A);
        }
        return comp;
    }

    private void setTorrent(Torrent torrent) {
        this.torrent = torrent;
    }
}
