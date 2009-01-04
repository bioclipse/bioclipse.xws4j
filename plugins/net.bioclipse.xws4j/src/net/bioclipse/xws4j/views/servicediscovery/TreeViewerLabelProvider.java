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
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import net.bioclipse.xws.client.adhoc.IFunction;

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

	FontRegistry registry = new FontRegistry();
	
	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 0) {
			String imageKey = ISharedImages.IMG_OBJ_FOLDER;
			return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
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
			return registry.getItalic(Display.getCurrent().getSystemFont()
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
		if (columnIndex == 0 && element instanceof TreeObject) {
			if (((TreeObject)element).getXmppItem() instanceof IFunction)
				return Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE);
		}
		return null;
	}
}