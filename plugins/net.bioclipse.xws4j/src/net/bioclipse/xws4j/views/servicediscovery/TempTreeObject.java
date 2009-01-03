package net.bioclipse.xws4j.views.servicediscovery;

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
public class TempTreeObject implements TreeViewerContentProvider.ITreeObject {
	
	private static final String TEMP_NAME = "discovering...";
	private Object parent;
	
	public TempTreeObject(TreeViewerContentProvider.ITreeObject parent) {
		this.parent = parent;
	}
	
	public Object getParent() {
		return parent;
	}
	
	public Object[] getChildren() {
		return new TreeObject[0];
	}
	
	public boolean hasChildren() {
		return false;
	}
	
	public String toString() {
		return TEMP_NAME;
	}
}