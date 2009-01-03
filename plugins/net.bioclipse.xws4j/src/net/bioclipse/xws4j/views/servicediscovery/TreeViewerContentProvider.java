package net.bioclipse.xws4j.views.servicediscovery;

import java.util.List;
import java.util.ArrayList;

import net.bioclipse.xws4j.XwsConsole;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.part.ViewPart;

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
public class TreeViewerContentProvider implements IStructuredContentProvider, ITreeContentProvider {
	
	private ViewPart viewpart;
	private InvisibleRootObject invisibleroot;
	
	public interface ITreeObject {
		public Object getParent();
		public Object[] getChildren();
		public boolean hasChildren();
		public String toString();
	}
	
	private class InvisibleRootObject implements ITreeObject {
		private List<ITreeObject> children = new ArrayList<ITreeObject>();
		public void addChild(ITreeObject child) {
			children.add(child);
		}
		public Object getParent() {
			return null;
		}
		public Object[] getChildren() {
			return children.toArray();
		}
		public boolean hasChildren() {
			return !children.isEmpty();
		}
		public String toString() {
			return "invisible root";
		}
	}

	public TreeViewerContentProvider(ViewPart viewpart) {
		this.viewpart = viewpart;
		invisibleroot = new InvisibleRootObject();
	}
	
	public void reset() {
		invisibleroot = new InvisibleRootObject();
	}
	
	public void addFirstLevelObject(ITreeObject object) {
		invisibleroot.addChild(object);
	}
	
	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}
	public void dispose() {
	}
	public Object[] getElements(Object parent) {
		if (parent != null && invisibleroot != null) {
			if (parent.equals(viewpart.getViewSite()))
				return invisibleroot.getChildren();
			if (parent instanceof ITreeObject)
				return ((ITreeObject) parent).getChildren();
		}
		return new Object[0];
	}
	public Object getParent(Object child) {
		if (child != null && child instanceof ITreeObject)
			return ((ITreeObject)child).getParent();
		return null;
	}
	public Object [] getChildren(Object parent) {
		if (parent != null && parent instanceof ITreeObject)
			return ((ITreeObject)parent).getChildren();
		return new Object[0];
	}
	public boolean hasChildren(Object parent) {
		if (parent != null && parent instanceof ITreeObject) {
			return ((ITreeObject)parent).hasChildren();
		}
		return false;
	}
}