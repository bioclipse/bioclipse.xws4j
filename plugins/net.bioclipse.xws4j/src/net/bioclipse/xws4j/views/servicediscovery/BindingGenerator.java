package net.bioclipse.xws4j.views.servicediscovery;

import java.io.File;

import net.bioclipse.xws.binding.BindingManager;
import net.bioclipse.xws.binding.exceptions.XwsBindingException;
import net.bioclipse.xws.client.adhoc.IFunction;
import net.bioclipse.xws.client.adhoc.IoSchemata;
import net.bioclipse.xws4j.Activator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressConstants;

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
public class BindingGenerator {
	public static void createBinding(final IFunction function) {
		final String job_title = "Creating a Java binding for function '" +
		function.getName() + "' of XMPP Service " + function.getJid();
		
		File files[] = BindingManager.getBindingFiles(function, Activator.getDefaultBindingDefinitions());
		if (files.length > 0) {
			String newline = System.getProperty("line.separator");
			// it seems bindings for this function already exist. delete them and create a binding or abort?
			boolean replace = MessageDialog.openConfirm(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() ,
                    "XMPP Service Discovery",
                    "A binding for function '" +
            		function.getName() + "' of XMPP Service " + function.getJid() +
            		" exists already." + newline + 
            		newline + "Replace old binding with a new binding?");
			if (!replace) {
				return;
			}

			for (int i = 0; i < files.length; i++) {
				deleteFile(files[i]);
			}
		}
		
		Job job = new Job(job_title) {
			
			protected IStatus run(IProgressMonitor monitor) {
				IoSchemata ioschemata = null;
				// set a friendly icon and keep the job in list when it is finished
				setProperty(IProgressConstants.ICON_PROPERTY, Activator.getImageDescriptor("icons/png/add2.png"));			
				
				monitor.beginTask("Requesting input/output XML Schemata.", 3);
				
				try {
					ioschemata = function.getIoSchemataSync(60000);
				} catch (Exception e) {
					String errormsg = "Error, could not request input/output XML Schemata: " + e.getMessage();
					monitor.setTaskName(errormsg);
					setProperty(IProgressConstants.KEEP_PROPERTY, Boolean.TRUE);
					return new Status(Status.ERROR,
							job_title,
							errormsg); 
				}
				
				monitor.worked(1);
				if (monitor.isCanceled())
					return Status.CANCEL_STATUS;
				
				monitor.setTaskName("Compiling the binding.");
				
				try {
					BindingManager.getIoFactory(ioschemata, Activator.getDefaultBindingDefinitions());
				} catch (XwsBindingException e) {
					String errormsg = "Error, could not compile input/output XML Schemata: " + e.getMessage();
					monitor.setTaskName(errormsg);
					setProperty(IProgressConstants.KEEP_PROPERTY, Boolean.TRUE);
					return new Status(Status.ERROR,
							job_title,
							errormsg); 
				}
				
				monitor.worked(1);
				
				Activator.updateProjectExplorer();
				
				monitor.worked(1);
				
				monitor.done();
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();
	}
	
	public static boolean deleteFile(File path) {
		if( path.exists() ) {
			if (path.isDirectory()) {
				File[] files = path.listFiles();
				for(int i=0; i<files.length; i++) {
					if(files[i].isDirectory()) {
						deleteFile(files[i]);
					} else {
						files[i].delete();
					}
				}
			}
		}
		return(path.delete());
	} 
}