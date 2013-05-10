/**
 * Alni Common - Common utilities to be used with Android development
 * Copyright (C) 2011-2013  Alexander Nilsen
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
 * ::1::F3::4E::78::90::FF
 */

package alni.android.common;

public class StringUtils {
    public static boolean validateIPAddress(String ip) {
	return ip
		.matches("\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b");
    }

    public static boolean validatePort(String port) {
	boolean isNumber = port.matches("[0-9]+");
	return (isNumber && Integer.parseInt(port) < 65536);
    }
}
