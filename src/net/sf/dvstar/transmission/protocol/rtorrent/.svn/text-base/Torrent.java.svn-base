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

import java.util.List;
import net.sf.dvstar.transmission.protocol.TorrentInterface;
import org.jdesktop.application.ResourceMap;

public class Torrent implements TorrentInterface {

	private String hash;
	
	private String name;
	private Long size;
	private Double downPercent;
	private Long ratio; 
	private Long sizeUpload;
	private String state;
	private String error;
	private Long seeders;
	private Long leechers;
	private Long peers;
	private Long downRate;
	private Long upRate;
	private List<TorrentFileDescInterface> filesDescList;

	public void setHash(String hash) {
		this.hash = hash;
	}
	public void setName(String name) {
		this.name = name;
	}
        public void setSize(Long size) {
		this.size = size;
	}
	public void setDownPercent(Double downPercent) {
		this.downPercent = downPercent;
	}
	public void setRatio(Long ratio) {
		this.ratio = ratio;
	}
	public void setSizeUpload(Long sizeUpload) {
		this.sizeUpload = sizeUpload;
	}
	public void setState(String state) {
		this.state = state;
	}
	public void setError(String error) {
		this.error = error;
	}
	public void setSeeders(Long seeders) {
		this.seeders = seeders;
	}
	public void setLeechers(Long leechers) {
		this.leechers = leechers;
	}
	public void setPeers(Long peers) {
		this.peers = peers;
	}
	public void setDownRate(Long downRate) {
		this.downRate = downRate;
	}
	public void setUpRate(Long upRate) {
		this.upRate = upRate;
	}

        /**
         * @todo check usage for rtorrent
         */
        public Long getPeers() {
		return peers;
	}
	public Long getRatio() {
		return ratio;
	}

        @Override
        public String toString() {
            return this.name;
        }

        public void setFilesDescList(List<TorrentFileDescInterface> files) {
            this.filesDescList = files;
        }
        
    @Override
    public List<TorrentFileDescInterface> getFilesDescList() {
        return filesDescList;
    }

    @Override
    public String getHashString() {
    	return hash;
    }
    @Override
    public Long getSize() {
	return size;
    }

    @Override
    public Integer getId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Integer getStatusCode() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getStatusDesc() {
		return state;
    }

    @Override
    public String getTorrentName() {
		return name;
    }

    @Override
    public Double getDoneProgress() { // getDownPercent()
		return downPercent;
    }

    @Override
    public Long getHaveTotal() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Long getHaveValid() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Long getUpload() {
		return sizeUpload;
    }

    @Override
    public String getErrorString() {
		return error;
    }

    @Override
    public Double getRateDn() {
		return new Double( downRate );
    }

    @Override
    public Double getRateUp() {
		return new Double( upRate );
    }

    @Override
    public Long getLeechs() {
		return leechers;
    }

    @Override
    public Long getSeeds() {
		return seeders;
    }

    @Override
    public byte[] getPiecesArray() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Integer getPiecesCount() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Boolean isHasError() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Boolean isFinished() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getElapsedTimeString() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Long getElapsedTime() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getRemainingTimeString() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Long getRemainingTime() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getDownloadDir() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getComment() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getCreator() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Long getDateCreated() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Long getDateAdded() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object getObjectValue(TorrentColumnsDesc column) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<TorrentPeerDescInterface> getPeersDescList() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setResourceBundle(ResourceMap resourceMap) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
	
}
