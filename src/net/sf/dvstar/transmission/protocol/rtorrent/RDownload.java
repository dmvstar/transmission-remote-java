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

public interface RDownload {
	Object check_hash(String string);
	Object close(String hash);
	Object create_link();
	Object delete_link();
	Object delete_tied();
	Object erase(String string);
	String get_base_filename(String hash);
	String get_base_path(String hash);
	Object get_bytes_done();
	long get_chunk_size(String hash);
	long get_chunks_hashed(String hash);
	long get_complete(String hash);
	long get_completed_bytes(String hash);
	long get_completed_chunks(String hash);
	String get_connection_current(String hash);
	String get_connection_leech(String hash);
	String get_connection_seed(String hash);
	long get_creation_date(String hash);
	String get_custom1(String hash);
	String get_custom2(String hash);
	String get_custom3(String hash);
	String get_custom4(String hash);
	String get_custom5(String hash);
	String get_directory(String hash);
	//String get_directory_base(String hash); not defined?
	long get_down_rate(String hash);
	long get_down_total(String hash);
	long get_free_diskspace(String hash);
	String get_hash(String hash);
	long get_hashing(String hash);
	long get_ignore_commands(String hash);
	long get_left_bytes(String hash);
	String get_local_id(String hash);
	String get_local_id_html(String hash);
	long get_max_file_size(String hash);
	long get_max_size_pex(String hash);
	String get_message(String hash);
	Object get_mode(String hash); //Object operator [mode] could not find element
	String get_name(String hash);
	long get_peer_exchange(String hash);
	long get_peers_accounted(String hash);
	long get_peers_complete(String hash);
	long get_peers_connected(String hash);
	long get_peers_max(String hash);
	long get_peers_min(String hash);
	long get_peers_not_connected(String hash);
	long get_priority(String hash);
	String get_priority_str(String hash);
	long get_ratio(String hash);
	long get_size_bytes(String hash);
	long get_size_chunks(String hash);
	long get_size_files(String hash);
	long get_size_pex(String hash);
	long get_skip_rate(String hash);
	long get_skip_total(String hash);
	long get_state(String hash);
	long get_state_changed(String hash);
	String get_tied_to_file(String hash);
	Object get_tracker_focus(String hash);
	Object get_tracker_numwant(String hash);
	Object get_tracker_size(String hash);
	Object get_up_rate(String hash);
	Object get_up_total(String hash);
	Object get_uploads_max(String hash);
	Object is_active(String hash);
	Object is_hash_checked(String hash);
	Object is_hash_checking(String hash);
	Object is_multi_file(String hash);
	long is_open(String hash);
	Object is_pex_active(String hash);
	Object is_private(String hash);
	//XmlRpcArray multicall(XmlRpcArray args); doesnt work, return wrong object type.
	Object open(String hash);
	Object set_connection_current();
	Object set_connection_leech();
	Object set_connection_seed();
	Object set_custom1(String hash, String label);
	Object set_custom2();
	Object set_custom3();
	Object set_custom4();
	Object set_custom5();
	Object set_directory();
	Object set_ignore_commands();
	Object set_max_file_size();
	Object set_max_peers();
	Object set_max_uploads();
	Object set_message();
	Object set_min_peers();
	Object set_peer_exchange();
	Object set_peers_max();
	Object set_peers_min();
	Object set_priority(String hash, int i);
	Object set_tracker_numwant();
	Object set_uploads_max();
	Object start(String string);
	Object stop(String string);
	Object update_priorities(String hash);
}
