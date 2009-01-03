package net.bioclipse.xws4j.views.servicediscovery;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import net.bioclipse.xws.client.IXmppItem;
import net.bioclipse.xws.client.adhoc.IFunction;
import net.bioclipse.xws.client.adhoc.IService;
import net.bioclipse.xws.client.disco.DiscoStatus;
import net.bioclipse.xws.client.disco.Functions;
import net.bioclipse.xws.client.disco.Info;
import net.bioclipse.xws.client.disco.Items;
import net.bioclipse.xws4j.XwsConsole;

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
public class TreeObject implements TreeViewerContentProvider.ITreeObject {
	
	private IXmppItem xmppitem;
	private Object parent;
	private List<Object> children = new ArrayList<Object>();
	
	public TreeObject(IXmppItem xmppitem, TreeObject parent) {
		this.parent = parent;
		updateXmppItem(xmppitem);
	}
	
	public String getName() {
		if (xmppitem instanceof IFunction)
			return xmppitem.getNode();
		return xmppitem.getJid();
	}
	
	public void updateXmppItem(IXmppItem xmppitem) {
		children.clear();
		this.xmppitem = xmppitem;
		
XwsConsole.writeToConsoleBlueT("updateXmppItem");
		
		Items items = xmppitem.getItems();
		if (items != null) {
			List<IXmppItem> xitems = items.getList();
			Iterator<IXmppItem> it = xitems.iterator();
			while (it.hasNext() == true) {
				children.add(new TreeObject(it.next(), this));
XwsConsole.writeToConsoleBlueT("add item child" + items);
			}
		}
		
		if (xmppitem instanceof IService) {
			Functions functions = ((IService)xmppitem).getFunctions();
			if (functions != null) {
				List<IFunction> xfunctions = functions.getList();
				Iterator<IFunction> it = xfunctions.iterator();
				while (it.hasNext() == true) {
					children.add(new TreeObject(it.next(), this));
XwsConsole.writeToConsoleBlueT("add function child:" + functions);
				}
			}
		}
	}
	
	public IXmppItem getXmppItem() {
		return xmppitem;
	}
	
	public Object getParent() {
		return parent;
	}
	
	public Object[] getChildren() {
		if (children == null)
			return new TreeObject[0];
		return children.toArray();
	}
	
	public boolean hasChildren() {
		if (xmppitem.getDiscoStatus() == DiscoStatus.NOT_DISCOVERED)
			return true;
		if (children != null && children.size() > 0)
			return true;
		return false;
	}
	
	public String toString() {
		return getName();
	}
}