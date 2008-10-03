package net.bioclipse.xws4j;

import org.eclipse.jface.preference.IPreferenceStore;

import net.bioclipse.xws4j.preferences.PreferenceConstants;
import net.bioclipse.xws.ILogPipe;

/**
 * 
 * This file is part of the Bioclipse xws4j Plug-in.
 * 
 * Copyright (C) 2008 Johannes Wagener
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, see <http://www.gnu.org/licenses>.
 * 
 * @author Johannes Wagener
 */
public class XwsLogPipe implements ILogPipe {
	
	private static boolean debug_mode;
	
	public XwsLogPipe() {
		IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();
		debug_mode = preferences.getBoolean(PreferenceConstants.P_BOOLEAN_LOGDEFAULT);
	}
	
	public void write(String text) {
		if (debug_mode)
			XwsConsole.writeToConsoleRedT(text);
	}
	
	public static boolean isDebugMode() {
		return debug_mode;
	}
	
	public static void setDebugMode(boolean mode) {
		debug_mode = mode;
	}
}