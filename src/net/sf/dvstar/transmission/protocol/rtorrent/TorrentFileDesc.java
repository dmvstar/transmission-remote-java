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


package net.sf.dvstar.transmission.protocol.rtorrent;

import java.io.Serializable;
import net.sf.dvstar.transmission.protocol.TorrentConstants.TorrentFileColumnsDesc;
import net.sf.dvstar.transmission.protocol.TorrentInterface.TorrentFileDescInterface;
import net.sf.dvstar.transmission.protocol.transmission.ProtocolConstants;

public class TorrentFileDesc implements Serializable, ProtocolConstants, TorrentFileDescInterface {

    private String path;
    private Long size;
    private Double downPercent;

    public TorrentFileDesc() {
    }

    public Double getDownPercent() {
        return downPercent;
    }

    public void setDownPercent(Double downPercent) {
        this.downPercent = downPercent;
    }

    @Override
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

//    @Override
//    public Long getSize() {
//        return size;
//    }

    @Override
    public String getSize() {
        return ""+size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    @Override
    public String getFileType() {
        String ret = "-";
        return ret;
    }

    @Override
    public String toString() {
        return path + " " + size + " " + downPercent;
    }

    @Override
    public Object getObjectValue(TorrentFileColumnsDesc column) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getCompleted() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getProgress() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Boolean getWanted() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getPriority() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setWanted(boolean wanted) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getDir() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
