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

import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.Recorded;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.business.IBioclipseManager;
import net.bioclipse.xws.client.Client;
import net.bioclipse.xws4j.exceptions.Xws4jException;

public interface IXWSManager extends IBioclipseManager{

    /**
     * Run MetaPrint2D on a molecule
     * @param molecule The IMolecule to run Metaprint2D on.
     * @throws BioclipseException 
     * @throws InvocationTargetException 
     * @throws Xws4jException 
     */
    @PublishedMethod( methodSummary = "Returns the default XWS client" )
    @Recorded
    public Client getDefaultClient() throws BioclipseException, InvocationTargetException, Xws4jException;
}
