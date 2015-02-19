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

public interface RPeerConnection {
	Object get_address();
	Object get_client_version();
	Object get_completed_percent();
	Object get_down_rate();
	Object get_down_total();
	Object get_id();
	Object get_id_html();
	Object get_options_str();
	Object get_peer_rate();
	Object get_peer_total();
	Object get_port();
	Object get_up_rate();
	Object get_up_total();
	Object is_encrypted();
	Object is_incoming();
	Object is_obfuscated();
	Object is_snubbed();
}
