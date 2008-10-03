 /*******************************************************************************
 * Copyright (c) 2007-2008 The Bioclipse Project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org—epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 * 
 * Contributors:
 *     Ola Spjuth
 *     
 ******************************************************************************/

package net.bioclipse.xws4j.business;

import net.bioclipse.xws4j.Activator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IExecutableExtensionFactory;

/**
 * 
 * @author ola
 */
public class XWSManagerFactory implements IExecutableExtension, 
                                              IExecutableExtensionFactory {

    private Object xwsManager;
    
    public void setInitializationData(IConfigurationElement config,
            String propertyName, Object data) throws CoreException {
        
        xwsManager = Activator.getDefault().getXWSManager();
        if(xwsManager==null) {
            xwsManager = new Object();
        }
    }

    public Object create() throws CoreException {
        return xwsManager;
    }
}
