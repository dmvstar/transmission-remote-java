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



package net.sf.dvstar.transmission.protocol;

/**
 *
 * TorrentsCommonException
 *
 * @author dstarzhynskyi
 */
public class TorrentsCommonException extends Exception {

    public TorrentsCommonException(){
        super();
    }

    public TorrentsCommonException(String mess){
        super(mess);
    }

    public TorrentsCommonException(String mess, String additionalInfo){
        super(mess);
        this.additionalInfo = additionalInfo;
    }

    String additionalInfo="";
    public String getAdditionalInfo(){ return additionalInfo; }
}
