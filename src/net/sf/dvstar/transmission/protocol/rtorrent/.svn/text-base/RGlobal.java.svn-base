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

import redstone.xmlrpc.XmlRpcArray;

public interface RGlobal {
	Object call_download();
	Object cat(Object args);
	Object close_low_diskspace();
	Object close_on_ratio();
	Object close_untied();
	Object create_link();
	Object delete_link();
	XmlRpcArray download_list();
	Object enable_trackers();
	Object encoding_list();
	Object encryption();
	Object execute();
	Object execute_log();
	Object execute_nothrow();
	Object execute_raw();
	Object execute_raw_nothrow();
	Object get_bind();
	Object get_check_hash();
	Object get_connection_leech();
	Object get_connection_seed();
	Object get_directory();
	Long get_download_rate();
	Object get_handshake_log();
	Object get_hash_interval();
	Object get_hash_max_tries();
	Object get_hash_read_ahead();
	Object get_http_cacert();
	Object get_http_capath();
	Object get_http_proxy();
	Object get_ip();
	Object get_key_layout();
	Object get_max_downloads_div();
	Object get_max_downloads_global();
	Object get_max_file_size();
	Object get_max_memory_usage();
	Object get_max_open_files();
	Object get_max_open_http();
	Object get_max_open_sockets();
	Object get_max_peers();
	Object get_max_peers_seed();
	Object get_max_uploads();
	Object get_max_uploads_div();
	Object get_max_uploads_global();
	Object get_memory_usage();
	Object get_min_peers();
	Object get_min_peers_seed();
	Object get_name();
	Object get_peer_exchange();
	Object get_port_open();
	Object get_port_random();
	Object get_port_range();
	Object get_preload_min_size();
	Object get_preload_required_rate();
	Object get_preload_type();
	Object get_proxy_address();
	Object get_receive_buffer_size();
	Object get_safe_free_diskspace();
	Object get_safe_sync();
	Object get_scgi_dont_route();
	Object get_send_buffer_size();
	Object get_session();
	Object get_session_lock();
	Object get_session_on_completion();
	Object get_split_file_size();
	Object get_split_suffix();
	Object get_stats_not_preloaded();
	Object get_stats_preloaded();
	Object get_timeout_safe_sync();
	Object get_timeout_sync();
	Object get_tracker_dump();
	Object get_tracker_numwant();
	Long get_upload_rate();
	Object get_use_udp_trackers();
	//reserved keywords.
	//Object if();
	//Object import();
	Object load(String url);
	Object load_raw(Object data);
	Object load_raw_start(Object data);
	Object load_raw_verbose(Object data);
	Object load_start(String url);
	Object load_start_verbose(String url);
	Object load_verbose(String url);
	Object on_close();
	Object on_erase();
	Object on_finished();
	Object on_hash_done();
	Object on_hash_queued();
	Object on_hash_removed();
	Object on_insert();
	Object on_open();
	Object on_start();
	Object on_stop();
	Object print();
	Object remove_untied();
	Object scgi_local();
	Object scgi_port();
	Object schedule();
	Object schedule_remove();
	Object session_save();
	Object set_bind();
	Object set_check_hash();
	Object set_connection_leech();
	Object set_connection_seed();
	Object set_directory();
	Object set_download_rate();
	Object set_handshake_log();
	Object set_hash_interval();
	Object set_hash_max_tries();
	Object set_hash_read_ahead();
	Object set_http_cacert();
	Object set_http_capath();
	Object set_http_proxy();
	Object set_ip();
	Object set_key_layout();
	Object set_max_downloads_div();
	Object set_max_downloads_global();
	Object set_max_file_size();
	Object set_max_memory_usage();
	Object set_max_open_files();
	Object set_max_open_http();
	Object set_max_open_sockets();
	Object set_max_peers();
	Object set_max_peers_seed();
	Object set_max_uploads();
	Object set_max_uploads_div();
	Object set_max_uploads_global();
	Object set_min_peers();
	Object set_min_peers_seed();
	Object set_name();
	Object set_peer_exchange();
	Object set_port_open();
	Object set_port_random();
	Object set_port_range();
	Object set_preload_min_size();
	Object set_preload_required_rate();
	Object set_preload_type();
	Object set_proxy_address();
	Object set_receive_buffer_size();
	Object set_safe_sync();
	Object set_scgi_dont_route();
	Object set_send_buffer_size();
	Object set_session();
	Object set_session_lock();
	Object set_session_on_completion();
	Object set_split_file_size();
	Object set_split_suffix();
	Object set_timeout_safe_sync();
	Object set_timeout_sync();
	Object set_tracker_dump();
	Object set_tracker_numwant();
	Object set_upload_rate(int i);
	Object set_use_udp_trackers();
	Object start_tied();
	Object stop_on_ratio();
	Object stop_untied();
	Object to_date();
	Object to_elapsed_time();
	Object to_kb();
	Object to_mb();
	Object to_time();
	Object to_xb();
	Object tos();
	Object try_import();
	Object view_add();
	Object view_filter();
	Object view_filter_on();
	Object view_list();
	Object view_sort();
	Object view_sort_current();
	Object view_sort_new();
	int xmlrpc_dialect(String string);
	void set_download_rate(int i);
}
