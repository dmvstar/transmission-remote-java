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


package net.sf.dvstar.transmission.comps;

import java.util.HashMap;
import java.util.Map;
import javax.swing.RowFilter;
import javax.swing.RowFilter.Entry;
import net.sf.dvstar.transmission.protocol.TorrentConstants.TorrentFilterFieldDesc;
import net.sf.dvstar.transmission.protocol.TorrentInterface;
import net.sf.dvstar.transmission.protocol.transmission.TorrentsTableModel;

/**
 *
 * @author sdv
 */
public class TorrentFilter extends RowFilter<TorrentsTableModel, Object> {

    private TorrentFilterDesc torrentFilterDesc = null;

    public TorrentFilter(TorrentFilterDesc torrentFilterDesc) {
        super();
        this.torrentFilterDesc = torrentFilterDesc;
    }

    /**
     * @todo - ! optimise It !
     * @param value
     * @return
     */
//    public boolean include(Entry<? extends Object, ? extends Object> entry) {
    @Override
    public boolean include(Entry<? extends TorrentsTableModel, ? extends Object> entry) {
        boolean ret = true;
        if (torrentFilterDesc != null) {
            if (torrentFilterDesc.filtersMap.size() > 0) {
                ret = false;
                Object model = entry.getModel();
                if (model instanceof TorrentsTableModel) {
                    Object vvvo = entry.getValue(-1);
                    if (vvvo instanceof TorrentInterface) {
                        String vvvv = ((TorrentInterface) vvvo).getDownloadDir();
                        //System.out.println("getDownloadDir()=" + vvvv);

                        for (int i = 0; i < torrentFilterDesc.filtersMap.size(); i++) {
                            String filter = torrentFilterDesc.filtersMap.get(TorrentFilterFieldDesc.FIELD_LOCATION);
                            if (filter != null) {
                                if (vvvv.indexOf(filter) >= 0) {
                                    ret = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return ret;
    }

    public static class TorrentFilterDesc {

        Map<TorrentFilterFieldDesc, String> filtersMap = new HashMap();

        public void addFilterDescriptor(TorrentFilterFieldDesc field, String value) {
            filtersMap.put(field, value);
        }

        public int getCountItems(){ return filtersMap.size(); }

    }
}
