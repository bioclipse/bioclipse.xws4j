package net.bioclipse.xws4j.views.servicediscovery;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import net.bioclipse.xws.client.IXmppItem;
import net.bioclipse.xws.client.adhoc.IFunction;
import net.bioclipse.xws.client.adhoc.IService;
import net.bioclipse.xws.client.disco.DiscoStatus;
import net.bioclipse.xws.client.disco.Functions;
import net.bioclipse.xws.client.disco.Items;
import net.bioclipse.xws.client.listeners.IDiscoListener;
import net.bioclipse.xws.exceptions.XwsException;
import net.bioclipse.xws4j.PluginLogger;
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
public class TreeObject implements TreeViewerContentProvider.ITreeObject, IDiscoListener {
	
	private IXmppItem xmppitem;
	private Object parent;
	private ServiceDiscoveryView discoview;
	private List<Object> children = new ArrayList<Object>();
	private boolean discoverchildren = false, discovering = false;
	
	public TreeObject(IXmppItem xmppitem, TreeObject parent,
						ServiceDiscoveryView discoview,
						boolean discover,
						boolean discoverchildren) {
		this.parent = parent;
		this.discoview = discoview;
		this.xmppitem = xmppitem;
		
		updateXmppItem(xmppitem);

		// discover xmppitem !?
		if (discover)
			discover(discoverchildren);
	}
	
	protected void discover(boolean discoverchildren) {
		if (discovering)
			return;
		this.discoverchildren = discoverchildren;
		if (xmppitem.getDiscoStatus() == DiscoStatus.NOT_DISCOVERED) {
			discovering = true;
			try {
				xmppitem.discoverAsync(this);
			} catch (Exception e) {
				discoview.setErrorMessage("Could not discover: " + e);
				XwsConsole.writeToConsoleBlueT("Could not discover: " + e);
			}
		} else if (discoverchildren == true && children.size() > 0){
			Iterator<Object> it = children.iterator();
			while (it.hasNext() == true) {
				Object object = it.next();
				if (object instanceof TreeObject)
					((TreeObject)object).discover(false);
			}
		}
	}
	
	private String getName() {
		if (xmppitem instanceof IFunction)
			return xmppitem.getNode();
		return xmppitem.getJid();
	}
	
	private void updateXmppItem(IXmppItem xmppitem) {
		children.clear();
		this.xmppitem = xmppitem;
		
		if (!(xmppitem instanceof IFunction) &&
				xmppitem.getDiscoStatus() == DiscoStatus.NOT_DISCOVERED) {
			children.add(new TempTreeObject(this));
		} else {
			Items items = null;
			try {
				items = xmppitem.getItems();
			} catch (XwsException e) {
				PluginLogger.log(e.getMessage());
			}
			if (items != null) {
				List<IXmppItem> xitems = items.getList();
				if (xitems != null) {
					Iterator<IXmppItem> it = xitems.iterator();
					
					while (it.hasNext() == true) {
						IXmppItem xitem_next = it.next();
						if (!xitem_next.getNode().equals("http://jabber.org/protocol/commands"))
							children.add(new TreeObject(xitem_next, this, discoview, discoverchildren, false));
					}
				}
			}
			
			if (xmppitem instanceof IService) {
				Functions functions = null;
				try {
					functions = ((IService)xmppitem).getFunctions();
				} catch (XwsException e) {
					PluginLogger.log(e.getMessage());
				}
				
				if (functions != null) {
					List<IFunction> xfunctions = functions.getList();
					if (xfunctions != null) {
						Iterator<IFunction> it = xfunctions.iterator();
						while (it.hasNext() == true) {
							children.add(new TreeObject(it.next(), this, discoview, discoverchildren, false));
						}
					}
				}
			}
		}

		discoview.refresh(this);
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
		if (children != null && children.size() > 0)
			return true;
		return false;
	}
	
	public String toString() {
		return getName();
	}

	public void onDiscovered(IXmppItem i, DiscoStatus disco_status) {
		updateXmppItem(i);
		discovering = false;
	}
}