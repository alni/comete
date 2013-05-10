/**
 * Comete for Comete - Control Comete with an Android device
 * Copyright (C) 2011  Alexander Nilsen
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
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * @author Alexander Nilsen
 *
 */

package info.alni.comete.android;

import org.flightgear.fgfsclient.FGFSConnection;

public class Common {
    public static final int NUM_OF_ENGINES = 12;
    
    public static final String URL_BASE = "file:///android_asset/www/";
    public static final String URL_HELP_MAIN = URL_BASE + "help/index.html";
    public static FGFSConnection fgfs;
    public static MSFSConnection msfs;
}
