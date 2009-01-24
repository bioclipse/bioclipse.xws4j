package net.bioclipse.xws4j.views.servicediscovery;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import net.bioclipse.xws.client.adhoc.IFunction;
import net.bioclipse.xws.client.adhoc.IService;
import net.bioclipse.xws.client.IXmppItem;
import net.bioclipse.xws.client.disco.DiscoStatus;
import net.bioclipse.xws.exceptions.XwsException;
import net.bioclipse.xws4j.Activator;
import net.bioclipse.xws4j.PluginLogger;

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
public class TreeViewerLabelProvider extends LabelProvider implements
ITableLabelProvider, ITableFontProvider, ITableColorProvider {
	
	private FontRegistry font_registry = new FontRegistry();
	
	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 0) {
			/*if (element instanceof TempTreeObject) {
				return null;//Activator.getDefault().getImageRegistry().get("lightbulb");
			}*/
			
			if (element instanceof TreeObject) {
				TreeObject treeobject = (TreeObject)element;
				IXmppItem xitem = treeobject.getXmppItem();
				if (xitem instanceof IFunction &&
						xitem.getDiscoStatus() == DiscoStatus.DISCOVERED) {
					try {
						if (((IFunction)xitem).isCompatibleFunction())
							return Activator.getDefault().getImageRegistry().get("cog");
					} catch (XwsException e) {
						PluginLogger.log(e.getMessage());
					}
				}
				if (xitem.getDiscoStatus() == DiscoStatus.DISCOVERED) {
					if (xitem instanceof IService)
						return Activator.getDefault().getImageRegistry().get("page_white_gear");

					return Activator.getDefault().getImageRegistry().get("lightbulb");
				}
					
				if (xitem.getDiscoStatus() == DiscoStatus.DISCOVERED_WITH_ERROR)
					return Activator.getDefault().getImageRegistry().get("error");
			}
			return Activator.getDefault().getImageRegistry().get("bullet_yellow");
		}
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		
		if (element instanceof TempTreeObject) {
			if (columnIndex == 0) {
				return ((TempTreeObject)element).toString();
			} else
				return null;
		}
		
		if (element instanceof TreeObject) {
			TreeObject treeobject = (TreeObject)element;
			if (columnIndex == 0) {
				String desc = treeobject.getXmppItem().getDescription();
				if (desc.equals(""))
					return treeobject.getXmppItem().getJid();
				return treeobject.getXmppItem().getDescription();
			} else if (columnIndex == 1) {
				return treeobject.getXmppItem().getJid();
			} else if (columnIndex == 2) {
				return treeobject.getXmppItem().getNode();
			}
		}
		return null;
	}

	public Font getFont(Object element, int columnIndex) {
		if (element instanceof TempTreeObject) {
			return font_registry.getItalic(Display.getCurrent().getSystemFont()
					.getFontData()[0].getName());
		}
		return null;
	}

	public Color getBackground(Object element, int columnIndex) {
		return null;
	}

	public Color getForeground(Object element, int columnIndex) {
		if (element instanceof TempTreeObject) {
			return Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
		}
		if (element instanceof TreeObject) {
			if (((TreeObject)element).getXmppItem().getDiscoStatus() == DiscoStatus.DISCOVERED_WITH_ERROR)
				return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
		}
		
		if (element instanceof TreeObject) {
			if (((TreeObject)element).getXmppItem() instanceof IFunction)
				return Display.getCurrent().getSystemColor(SWT.COLOR_BLUE);
		}
		return null;
	}
}