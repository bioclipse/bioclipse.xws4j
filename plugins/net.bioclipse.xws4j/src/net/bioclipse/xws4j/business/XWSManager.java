/*******************************************************************************
 * Copyright (c) 2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ola Spjuth
 *     
 ******************************************************************************/
package net.bioclipse.xws4j.business;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.xws.client.Client;
import net.bioclipse.xws.client.XmppItem;
import net.bioclipse.xws.client.adhoc.Function;
import net.bioclipse.xws.client.adhoc.Service;
import net.bioclipse.xws.client.disco.Functions;
import net.bioclipse.xws.client.disco.Items;
import net.bioclipse.xws.exceptions.XmppException;
import net.bioclipse.xws.exceptions.XwsException;
import net.bioclipse.xws4j.Activator;
import net.bioclipse.xws4j.DefaultClientCurator;
import net.bioclipse.xws4j.exceptions.Xws4jException;


public class XWSManager implements IXWSManager{

	public String getNamespace() {
		return "xws";
	}

	public Client getDefaultClient() throws BioclipseException,
	InvocationTargetException, Xws4jException {

		DefaultClientCurator clientcurator = Activator.getDefaultClientCurator();

		if (clientcurator.isClientConnected() == true)
			return Activator.getDefaultClientCurator().getDefaultClient();
		else {
			return null;
		}

	}

	public void connect(){

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

	}


}
