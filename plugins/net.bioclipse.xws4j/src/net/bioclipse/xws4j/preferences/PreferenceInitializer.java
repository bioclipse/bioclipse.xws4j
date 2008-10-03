package net.bioclipse.xws4j.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import net.bioclipse.xws4j.Activator;

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
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault()
				.getPreferenceStore();
		store.setDefault(PreferenceConstants.P_STRING_SERVER, "");
		store.setDefault(PreferenceConstants.P_STRING_USERNAME, "");
		store.setDefault(PreferenceConstants.P_STRING_PASSWORD, "");
		store.setDefault(PreferenceConstants.P_STRING_RESOURCE, "Bioclipse");
		store.setDefault(PreferenceConstants.P_STRING_SERVERPORT, "5222");
		store.setDefault(PreferenceConstants.P_BOOLEAN_LOGDEFAULT, false);
	}

}
