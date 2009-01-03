package net.bioclipse.xws4j.views.servicediscovery;

import net.bioclipse.xws4j.Activator;
import net.bioclipse.xws4j.PluginLogger;
import net.bioclipse.xws4j.XwsConsole;
import net.bioclipse.xws4j.preferences.PreferenceConstants;

import net.bioclipse.xws.xmpp.XmppTools;
import net.bioclipse.xws.client.Client;
import net.bioclipse.xws.client.listeners.IDiscoListener;
import net.bioclipse.xws.client.IXmppItem;
import net.bioclipse.xws.client.disco.DiscoStatus;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
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
public class ServiceDiscoveryView extends ViewPart implements IDiscoListener {
	
	public static final String ID_SERVICEDISCOVIEW = "net.bioclipse.xws4j.servicediscoveryview";
	
	private static ServiceDiscoveryView viewpart = null;
	private static boolean connected = false;

	private Text text_address;
	private TreeViewer viewer;
	private Action action_foward, action_reverse, action_cancel,
					action_reload, action_home, action_bind;
	private Button button_go;
	private ProgressBar progressbar;
	private IXmppItem current_xmppitem = null;
	private TreeViewerContentProvider contentprovider;
	
	private static final String OFFLINE_STAT		= "Not connected to XMPP server. An active connection to a XMPP server is required to use XMPP Service Discovery.";
	private static final String ONLINE_READY		= "Ready.";
	private static final String ONLINE_DISCOVERING	= "Discovering: ";
	private static final String ONLINE_CANCELED		= "Discovery was canceled.";
	private static final String ONLINE_DISCOERROR	= "Error on discovering: ";
	
	class NameSorter extends ViewerSorter {
	}

	public ServiceDiscoveryView() {
	}
	
	public static void setStatusConnected(boolean connected) {
		ServiceDiscoveryView.connected = connected;
		if (viewpart != null) {
			viewpart.text_address.setEnabled(connected);
			viewpart.viewer.getTree().setEnabled(connected);
			viewpart.button_go.setEnabled(connected);
			viewpart.action_home.setEnabled(connected);
			
			updateNavigation();
		}
	}
	
	private static void updateNavigation() {
		if (viewpart != null) {
			if (!connected) {
				viewpart.action_foward.setEnabled(false);
		   		viewpart.action_reverse.setEnabled(false);
		   		viewpart.action_cancel.setEnabled(false);
		   		viewpart.action_reload.setEnabled(false);
				viewpart.action_bind.setEnabled(false);
					
				viewpart.setContentDescription(OFFLINE_STAT);
				viewpart.progressbar.setVisible(false);
		   	} else {
		   		// depends on disco status...
		   		
		   		
		   		IXmppItem current_xi = viewpart.current_xmppitem;
		   		
		   		if (current_xi == null) {
		   			viewpart.action_cancel.setEnabled(false);
		   			viewpart.setContentDescription(ONLINE_READY);
		   			viewpart.progressbar.setVisible(false);	
		   		} else {
		   			DiscoStatus status = current_xi.getDiscoStatus();
		   			
		   			String item_text = current_xi.getJid();
		   			if (!current_xi.getNode().equals(""))
		   				item_text = item_text + " Node: " + current_xi.getNode();

		   			if (status == DiscoStatus.NOT_DISCOVERED) {
			   			viewpart.setContentDescription(ONLINE_DISCOVERING + item_text);
			   			viewpart.progressbar.setVisible(true);
			   			viewpart.action_cancel.setEnabled(true);
			   			viewpart.action_reload.setEnabled(false);
			   		} else if (status == DiscoStatus.DISCOVERED) {
			   			viewpart.setContentDescription(ONLINE_READY);
			   			viewpart.progressbar.setVisible(false);
			   			viewpart.action_cancel.setEnabled(false);
			   			viewpart.action_reload.setEnabled(true);
			   		} else if (status == DiscoStatus.DISCOVERED_WITH_ERROR) {
			   			viewpart.setContentDescription(ONLINE_DISCOERROR + item_text);
			   			viewpart.progressbar.setVisible(false);
			   			viewpart.action_cancel.setEnabled(false);
			   			viewpart.action_reload.setEnabled(true);
			   		}
		   		}
		   	}
			//viewpart.getViewSite().getActionBars().updateActionBars();
		}
	}
	
	public static void show() {
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchPage wbPage = wb.getActiveWorkbenchWindow().getActivePage(); 
		if (wbPage != null) {
			try {
				wbPage.showView(ID_SERVICEDISCOVIEW);
			} catch (PartInitException e) {
				PluginLogger.log("ServiceDiscoveryView.show() - PartInitException: " + e.getMessage());
			}
		}
	}
	
	public void onDiscovered(IXmppItem i, DiscoStatus disco_status) {
		if (current_xmppitem != null) {
			if (XmppTools.compareJids(current_xmppitem.getJid(), i.getJid()) == 0) {
				current_xmppitem = i;
				TreeViewerContentProvider.ITreeObject firstlevelobject =
					new TreeObject(current_xmppitem, null);
				contentprovider.reset();
				contentprovider.addFirstLevelObject(firstlevelobject);
				viewer.refresh();
				updateNavigation();
			}
		}
	}

	public void createPartControl(Composite parent) {
		GridLayout gl = new GridLayout();
		gl.numColumns = 1;
		gl.marginWidth = 0;
		gl.marginHeight = 0;
		parent.setLayout(gl);
		
		// progress_bar
		progressbar = new ProgressBar(parent, SWT.HORIZONTAL | SWT.INDETERMINATE);
		GridData gd_progressbar = new GridData(GridData.FILL_HORIZONTAL);
		gd_progressbar.heightHint = 5;
		progressbar.setLayoutData(gd_progressbar);
		
		// text address control
		Composite comp_address = new Composite(parent, SWT.NONE);
		GridLayout gl_address = new GridLayout();
		gl_address.marginHeight = 0;
		gl_address.numColumns = 3;
		comp_address.setLayout(gl_address);
		comp_address.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Label label_address = new Label(comp_address, SWT.NONE);
		label_address.setText("JID:");
		text_address = new Text(comp_address, SWT.SINGLE | SWT.BORDER);
		text_address.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text_address.setEnabled(false);
		button_go = new Button(comp_address, SWT.PUSH);
		button_go.setText("Go");
		button_go.setEnabled(false);
		button_go.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				String jid = text_address.getText();
				if (!jid.equals("")) {
					discover(jid, "");
				}
			}
		});

						
		// the tree viewer
		contentprovider = new TreeViewerContentProvider(this);
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(contentprovider);
		viewer.setLabelProvider(new TreeViewerLabelProvider());
		viewer.setSorter(new NameSorter());
		viewer.setInput(getViewSite());
		viewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		viewer.getTree().setEnabled(false);

		makeActions();
		contributeToActionBars();
		
		viewpart = this;
		
		setStatusConnected(connected);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		IToolBarManager manager = bars.getToolBarManager();
		manager.add(action_reverse);
		manager.add(action_foward);
		manager.add(action_reload);
		manager.add(action_cancel);
		manager.add(action_home);
		manager.add(new Separator());
		manager.add(action_bind);
	}
	
	private void makeActions() {
		action_foward = new Action() {
			public void run() {
				//
			}
		};
		action_foward.setText("Forward");
		action_foward.setToolTipText("Forward");
		action_foward.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD));
		action_foward.setEnabled(false);
		
		action_reverse = new Action() {
			public void run() {
				//
			}
		};
		action_reverse.setText("Back");
		action_reverse.setToolTipText("Back");
		action_reverse.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_BACK));
		action_reverse.setEnabled(false);
		
		action_cancel = new Action() {
			public void run() {
				current_xmppitem = null;
				updateNavigation();
				viewpart.setContentDescription(ONLINE_CANCELED);
			}
		};
		action_cancel.setText("Cancel");
		action_cancel.setToolTipText("Cancel");
		action_cancel.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		action_cancel.setEnabled(false);
		
		action_reload = new Action() {
			public void run() {
				if (current_xmppitem != null) {
					String jid = current_xmppitem.getJid();
					if (!jid.equals("")) {
						text_address.setText(jid);
						discover(jid, "");
					}
				}
			}
		};
		action_reload.setText("Reload");
		action_reload.setToolTipText("Reload");
		action_reload.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_REDO));
		action_reload.setEnabled(false);

		action_home = new Action() {
			public void run() {
				IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();
				String jid = preferences.getString(PreferenceConstants.P_STRING_SERVER);
				if (!jid.equals("")) {
					text_address.setText(jid);
					discover(jid, "");
				}
			}
		};
		action_home.setText("Home");
		action_home.setToolTipText("Home");
		action_home.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_HOME_NAV));
		action_home.setEnabled(false);
		
		action_bind = new Action() {
			public void run() {
				//
			}
		};
		action_bind.setText("Bind");
		action_bind.setToolTipText("Create a bind for the selected function");
		action_bind.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_PRINT_EDIT));
		action_bind.setEnabled(false);
	}
	
	public void discover(String jid, String node) {
		try {
			Client client = Activator.getDefaultClientCurator().getDefaultClient();
			current_xmppitem = client.getXmppItem(jid, node);
			current_xmppitem.discoverAsync(viewpart);
			updateNavigation();
		} catch (Exception e) {
			XwsConsole.writeToConsoleBlueT("Could not get default client: " + e);
		}
	}
	
	public void setFocus() {
		if (viewpart != null)
			text_address.setFocus();
	}
}