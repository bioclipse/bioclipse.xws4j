package net.bioclipse.xws4j.actions;

import net.bioclipse.xws4j.XwsConsole;
import net.bioclipse.xws4j.Activator;
import net.bioclipse.xws4j.PluginLogger;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

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
//dispose()
//init()
//selectionChanged()
//getMenu()
//run()
public class ActionPulldown implements IWorkbenchWindowPulldownDelegate {
	private static MenuManager pulldownMenuManager = null;
	private static IAction theAction = null;
	
	public ActionPulldown() {
	}

	public void run(IAction action) {
		XwsConsole.show();
	}

	public Menu getMenu(Control parent) {
		if (pulldownMenuManager == null) {
			// we have to create a new menu first!
			pulldownMenuManager = new MenuManager();
			pulldownMenuManager.createContextMenu(parent);
			
			// ... and all the other features
			fillMenu(pulldownMenuManager);
		}
		
		// update toggle connection action description and icon!
		ToggleConnectionAction.getStatic().update();
		
		return pulldownMenuManager.getMenu();
	}
	
	private void fillMenu(MenuManager manager) {
		
		// TODO: extend with additional actions to
		// - show queued processes
		// ...

		MenuManager options = new MenuManager("Options");
		
		manager.add(ToggleConnectionAction.getStatic());		
		manager.add(new Separator());
		options.add(DebugModeAction.getStatic());
		manager.add(options);
		manager.add(new Separator());
		manager.add(ShowConsoleAction.getStatic());
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// keep link to action
		theAction = action;
	}

	public void dispose() {
		if (pulldownMenuManager != null) {
			pulldownMenuManager.dispose();
			pulldownMenuManager= null;
		}
		theAction = null;
	}

	public static void setStatusConnected(boolean connected) {
		if(theAction == null)
			PluginLogger.log("ActionPulldown.setStatusConnected() - " +
					"Error, could not update connection icon: toolbar action not available.");
		else {	
			if (connected == true) {
				theAction.setImageDescriptor(Activator.getImageDescriptor("icons/png/connected.png"));
			} else {
				theAction.setImageDescriptor(Activator.getImageDescriptor("icons/png/disconnected.png"));
			}
		}
	}

	public void init(IWorkbenchWindow window) {
	}
}
