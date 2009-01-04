package net.bioclipse.xws4j.views.servicediscovery;

import net.bioclipse.xws4j.Activator;
import net.bioclipse.xws4j.PluginLogger;
import net.bioclipse.xws4j.XwsConsole;
import net.bioclipse.xws4j.preferences.PreferenceConstants;

import net.bioclipse.xws.client.adhoc.IFunction;

import net.bioclipse.xws.client.Client;
import net.bioclipse.xws.xmpp.XmppTools;
import net.bioclipse.xws.client.IXmppItem;
import net.bioclipse.xws.client.disco.DiscoStatus;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import java.util.LinkedList;

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
public class ServiceDiscoveryView extends ViewPart {
	
	public static final String ID_SERVICEDISCOVIEW = "net.bioclipse.xws4j.servicediscoveryview";
	
	private static ServiceDiscoveryView viewpart = null;
	private static boolean connected = false;

	private Text text_address;
	private TreeViewer viewer;
	private Action action_foward, action_reverse, action_cancel,
					action_reload, action_home, action_bind;
	private Button button_go;
	private ProgressBar progressbar;
	private TreeObject current_treeobject = null, current_firstleveltreeobject = null;
	private TreeViewerContentProvider contentprovider;
	private LinkedList<TreeObject> visited_treeobjects = new LinkedList<TreeObject>();
	
	private static final String OFFLINE_STAT		= "Not connected to XMPP server. An active connection to a XMPP server is required to use XMPP Service Discovery.";
	private static final String ONLINE_READY		= "Ready.";
	private static final String ONLINE_DISCOVERING	= "Discovering: ";
	private static final String ONLINE_CANCELED		= "Discovery was canceled.";
	private static final String ONLINE_DISCOERROR	= "Error on discovering: ";
	
	class NameSorter extends ViewerSorter {
	}

	public static void setStatusConnected(boolean connected) {
		ServiceDiscoveryView.connected = connected;
		if (viewpart != null) {
			viewpart.text_address.setEnabled(connected);
			viewpart.viewer.getTree().setEnabled(connected);
			viewpart.button_go.setEnabled(connected);
			viewpart.action_home.setEnabled(connected);
			
			viewpart.updateNavigation();
		}
	}
	
	private void updateNavigation() {
		if (!connected) {
			action_foward.setEnabled(false);
	   		action_reverse.setEnabled(false);
	   		action_cancel.setEnabled(false);
	   		action_reload.setEnabled(false);
			action_bind.setEnabled(false);
					
			setContentDescription(OFFLINE_STAT);
			progressbar.setVisible(false);
			return;
		}

		if (current_treeobject != null) {
			// depends on disco status...
			IXmppItem current_xi = current_treeobject.getXmppItem();
			DiscoStatus status = current_xi.getDiscoStatus();

			String item_text = current_xi.getJid();
			if (!current_xi.getNode().equals(""))
				item_text = item_text + " Node: " + current_xi.getNode();

			if (status == DiscoStatus.NOT_DISCOVERED) {
				setContentDescription(ONLINE_DISCOVERING + item_text);
				progressbar.setVisible(true);
				action_cancel.setEnabled(true);
				action_reload.setEnabled(false);
			} else if (status == DiscoStatus.DISCOVERED) {
				setContentDescription(ONLINE_READY);
				progressbar.setVisible(false);
				action_cancel.setEnabled(false);
				action_reload.setEnabled(true);
			} else if (status == DiscoStatus.DISCOVERED_WITH_ERROR) {
				setContentDescription(ONLINE_DISCOERROR + item_text);
				progressbar.setVisible(false);
				action_cancel.setEnabled(false);
				action_reload.setEnabled(true);
			}
	   	} else {
  			action_cancel.setEnabled(false);
   			setContentDescription(ONLINE_READY);
   			progressbar.setVisible(false);	
   		}
		
		if (!visited_treeobjects.isEmpty()) {
			action_foward.setEnabled(!visited_treeobjects.getLast().equals(current_firstleveltreeobject));
			action_reverse.setEnabled(!visited_treeobjects.getFirst().equals(current_firstleveltreeobject));
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
	
	protected void refresh(Object object) {
		updateNavigation();
		viewer.refresh(object);
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
					discoverNew(jid, "");
				}
			}
		});
		
		// the tree viewer
		Composite comp_treeviewer = new Composite(parent, SWT.NONE);
		comp_treeviewer.setLayoutData(new GridData(GridData.FILL_BOTH));
		contentprovider = new TreeViewerContentProvider(this);
		viewer = new TreeViewer(comp_treeviewer, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(contentprovider);
		viewer.setLabelProvider(new TreeViewerLabelProvider());
		viewer.setSorter(new NameSorter());
		viewer.setInput(getViewSite());
		viewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		viewer.getTree().setEnabled(false);
		viewer.getTree().setHeaderVisible(true);
		TreeColumn c1 = new TreeColumn(viewer.getTree(), SWT.LEFT),
					c2 = new TreeColumn(viewer.getTree(), SWT.LEFT),
					c3 = new TreeColumn(viewer.getTree(), SWT.LEFT);
		c1.setText("Name");
		c2.setText("Jabber-ID");
		c3.setText("Node");
		TreeColumnLayout layout = new TreeColumnLayout();
		layout.setColumnData( c1, new ColumnWeightData(50));
		layout.setColumnData( c2, new ColumnWeightData(25));
		layout.setColumnData( c3, new ColumnWeightData(25));
		comp_treeviewer.setLayout(layout);

		makeActions();
		contributeToActionBars();
		addViewerListeners();		
		
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
				if (!visited_treeobjects.isEmpty()) {
					int index = visited_treeobjects.indexOf(current_firstleveltreeobject);
					if (index < visited_treeobjects.size()-1) {
						current_firstleveltreeobject = visited_treeobjects.get(index+1);
						text_address.setText(current_firstleveltreeobject.getXmppItem().getJid());
						contentprovider.reset();
						contentprovider.addFirstLevelObject(current_firstleveltreeobject);
						viewer.refresh();
						updateNavigation();
					}
				}
			}
		};
		action_foward.setText("Forward");
		action_foward.setToolTipText("Forward");
		action_foward.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD));
		action_foward.setEnabled(false);
		
		action_reverse = new Action() {
			public void run() {
				if (!visited_treeobjects.isEmpty()) {
					int index = visited_treeobjects.indexOf(current_firstleveltreeobject);
					if (index > 0) {
						current_firstleveltreeobject = visited_treeobjects.get(index-1);
						text_address.setText(current_firstleveltreeobject.getXmppItem().getJid());
						contentprovider.reset();
						contentprovider.addFirstLevelObject(current_firstleveltreeobject);
						viewer.refresh();
						updateNavigation();
					}
				}
			}
		};
		action_reverse.setText("Back");
		action_reverse.setToolTipText("Back");
		action_reverse.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_BACK));
		action_reverse.setEnabled(false);
		
		action_cancel = new Action() {
			public void run() {
				current_treeobject = null;
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
				if (current_firstleveltreeobject != null) {
					IXmppItem xitem = current_firstleveltreeobject.getXmppItem();
					if (xitem != null)
						discoverNew(xitem.getJid(), xitem.getNode());
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
					discoverNew(jid, "");
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
	
	private void addViewerListeners() {
		ITreeViewerListener tvlistener = new ITreeViewerListener() {
			public void treeCollapsed(TreeExpansionEvent event) {
			}
			public void treeExpanded(TreeExpansionEvent event) {
				Object object = event.getElement();
				if (object != null && object instanceof TreeObject) {
					TreeObject treeobject = (TreeObject)object;
					discover(treeobject);
				}
			}
		};
		viewer.addTreeListener(tvlistener);
		
		IDoubleClickListener dblistener = new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				ISelection selection = event.getSelection();
				if (selection != null && selection instanceof IStructuredSelection) {
					Object object = ((IStructuredSelection)selection).getFirstElement();
					if (object != null && object instanceof TreeObject) {
						TreeObject treeobject = (TreeObject)object;
						IXmppItem xitem = treeobject.getXmppItem();
						
						if (xitem instanceof IFunction) {
							
							return;
						} else
							discover(treeobject);

						if (treeobject.hasChildren() == true)
							viewer.setExpandedState(treeobject, !viewer.getExpandedState(treeobject));
					}
				}
			}
		};
		viewer.addDoubleClickListener(dblistener);
	}
	
	private void addToHistory(TreeObject treeobject) {
		if (!visited_treeobjects.isEmpty()) {
			if (XmppTools.compareJids(
					visited_treeobjects.getLast().getXmppItem().getJid(),
					treeobject.getXmppItem().getJid()) == 0) {
				visited_treeobjects.remove(visited_treeobjects.getLast());
			}
		}
		visited_treeobjects.add(treeobject);
	}
	
	private void discoverNew(String jid, String node) {
		try {
			Client client = Activator.getDefaultClientCurator().getDefaultClient();
			IXmppItem xitem = client.getXmppItem(jid, node);
			current_firstleveltreeobject = new TreeObject(xitem, null, this, true, true);
			contentprovider.reset();
			contentprovider.addFirstLevelObject(current_firstleveltreeobject);
			addToHistory(current_firstleveltreeobject);
			viewer.refresh();
			current_treeobject = current_firstleveltreeobject;
			updateNavigation();
			viewer.expandToLevel(current_firstleveltreeobject, 1);
		} catch (Exception e) {
			viewpart.setContentDescription("Could not get default client: " + e);
			XwsConsole.writeToConsoleBlueT("Could not get default client: " + e);
		}
	}
	
	private void discover(TreeObject treeobject) {
		current_treeobject = treeobject;
		if (current_treeobject != null) {
			current_treeobject.discover(true);
			updateNavigation();
		}
	}
	
	protected void setErrorMessage(String message) {
		viewpart.setContentDescription(message);
	}
	
	public void setFocus() {
		if (viewpart != null)
			text_address.setFocus();
	}
}