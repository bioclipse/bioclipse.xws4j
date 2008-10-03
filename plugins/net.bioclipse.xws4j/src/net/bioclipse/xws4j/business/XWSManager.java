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

import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.xws.client.Client;
import net.bioclipse.xws4j.Activator;
import net.bioclipse.xws4j.exceptions.Xws4jException;


public class XWSManager implements IXWSManager{

	public String getNamespace() {
		return "xws";
	}

	public Client getDefaultClient() throws BioclipseException,
			InvocationTargetException, Xws4jException {
		
		return Activator.getDefaultClientCurator().getDefaultClient();
	}

}
