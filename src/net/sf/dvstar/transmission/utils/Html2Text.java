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


package net.sf.dvstar.transmission.utils;

import java.io.*;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.*;

public class Html2Text extends HTMLEditorKit.ParserCallback {
 StringBuffer internalStringBuffer;

 public Html2Text() {}

 public void parse(String str) throws IOException {
     StringReader sReader = new StringReader(str);
     parse(sReader);
 }

 public void parse(String str, String pattern, String replace) throws IOException {
     StringReader sReader = new StringReader(str);
     parse(sReader);
     if(pattern!=null && replace!=null){
        int fromIndex = 0, findIndex=-1;
        int lenPattern = pattern.length();
        while((findIndex=internalStringBuffer.indexOf(pattern, fromIndex))>=0){
            internalStringBuffer.replace(findIndex, findIndex+lenPattern, replace);
            fromIndex = findIndex+1;
        }
     }
 }

 public void parse(Reader in) throws IOException {
   internalStringBuffer = new StringBuffer();
   ParserDelegator delegator = new ParserDelegator();
   // the third parameter is TRUE to ignore charset directive
   delegator.parse(in, this, Boolean.TRUE);
 }

    @Override
 public void handleText(char[] text, int pos) {
   internalStringBuffer.append(text);
 }

 public String getText() {
   return internalStringBuffer.toString();
 }

 public static void main (String[] args) {
   try {
     // the HTML to convert
     FileReader in = new FileReader(args[0]);
     Html2Text parser = new Html2Text();
     parser.parse(in);
     in.close();
     System.out.println(parser.getText());
   }
   catch (Exception e) {
     e.printStackTrace();
   }
 }
}
