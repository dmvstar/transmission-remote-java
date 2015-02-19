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

public interface RTracker {
	Object get_group();
	Object get_id();
	Object get_min_interval();
	Object get_normal_interval();
	Object get_scrape_complete();
	Object get_scrape_downloaded();
	Object get_scrape_incomplete();
	Object get_scrape_time_last();
	Object get_type();
	Object get_url();
	Object is_enabled();
	Object is_open();
	Object multicall();
	Object set_enabled(String hash, int i, int b);	
}
