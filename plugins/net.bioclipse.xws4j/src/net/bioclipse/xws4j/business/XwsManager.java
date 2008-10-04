package net.bioclipse.xws4j.business;

import net.bioclipse.xws.client.Client;
import net.bioclipse.xws.client.XmppItem;
import net.bioclipse.xws.client.adhoc.Function;
import net.bioclipse.xws.client.adhoc.Service;
import net.bioclipse.xws.exceptions.XmppException;
import net.bioclipse.xws4j.Activator;
import net.bioclipse.xws4j.exceptions.Xws4jException;

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
 * @author Johannes Wagener, Ola Spjuth
 */
public class XwsManager implements IXwsManager {
	
	public String getNamespace() {
		return "xws";
	}
		
    public Client getDefaultClient() throws Xws4jException {
    	return Activator.getDefaultClientCurator().getDefaultClient();
    }

    public String getStatus() {
    	Client client;
    	try {
    		client = Activator.getDefaultClientCurator().getDefaultClient();
    	} catch (Exception e) {
    		return e.toString();
    	}
    	return client.toString();
    }

    public void connect() throws Xws4jException, XmppException {
    	Client client;
   		client = Activator.getDefaultClientCurator().getDefaultClient();
   		client.connect();
    }

    public void disconnect() throws Xws4jException, XmppException {
    	Client client;
   		client = Activator.getDefaultClientCurator().getDefaultClient();
   		client.disconnect();
    }

    public boolean isConnected() throws Xws4jException {
    	Client client;
   		client = Activator.getDefaultClientCurator().getDefaultClient();
   		return client.isConnected();
    }

    public XmppItem getXmppItem(String jid, String node) throws Xws4jException {
    	Client client;
   		client = Activator.getDefaultClientCurator().getDefaultClient();
   		return client.getXmppItem(jid, node);
    }

    public Service getService(String service_jid) throws Xws4jException {
    	Client client;
		client = Activator.getDefaultClientCurator().getDefaultClient();
		return client.getService(service_jid);
    }

    public Function getFunction(String service_jid, String function_name) throws Xws4jException {
    	Client client;
		client = Activator.getDefaultClientCurator().getDefaultClient();
		return client.getFunction(service_jid, function_name);
    }
    
/*	public Client getDefaultClient() throws Xws4jException {

		DefaultClientCurator clientcurator = Activator.getDefaultClientCurator();

		if (clientcurator.isClientConnected() == true)
			return Activator.getDefaultClientCurator().getDefaultClient();
		else {
			return null;
		}

	}*/

/*	public void connect(){

		DefaultClientCurator clientcurator = Activator.getDefaultClientCurator();

		if (clientcurator.isClientConnected() == true)
			clientcurator.disconnectClient();
		else {
			try {
				clientcurator.connectClient();
			} catch (Exception e) {
				e.printStackTrace();
				clientcurator.disconnectClient();
			}
		}

	}

	public String listServices(String server) throws BioclipseException, InvocationTargetException, Xws4jException, XmppException, XwsException, InterruptedException{

		String ret="";

//		Service x = getDefaultClient().getService("xws.ayena.de");
		Service x = getDefaultClient().getService(server);

		XmppItem x2 = x.discoverSync(10000);

		Items items = x2.getItems();
		List<XmppItem> items_list = items.getList();
		Iterator<XmppItem> it = items_list.iterator();
		while (it.hasNext()) {
			XmppItem i = it.next();
			System.out.println(" * " + i.getJid() + " " + i.getNode() + " - " + i.getDescription());
		}

		if (x2 instanceof Service) {
			Service service=(Service)x2;
			System.out.println("Functions:");
			Functions functions = service.getFunctions();
			List<Function> functions_list = functions.getList();
			Iterator<Function> it2 = functions_list.iterator();
			while (it2.hasNext()) {
				Function f = it2.next();
				ret=ret+"\n  * " + f.getJid() + " " + f.getNode() + " - " + f.getDescription();
				System.out.println(" * " + f.getJid() + " " + f.getNode() + " - " + f.getDescription());

			}
		}

		return ret;

	}*/
}
