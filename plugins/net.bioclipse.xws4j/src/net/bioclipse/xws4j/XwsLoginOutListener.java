/* *****************************************************************************
 *Copyright (c) 2013 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 ******************************************************************************/
package net.bioclipse.xws4j;

import java.util.List;

import javax.security.auth.login.LoginException;

import org.apache.log4j.Logger;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;

import net.bioclipse.usermanager.IUserManagerListener;
import net.bioclipse.usermanager.UserManagerEvent;
import net.bioclipse.usermanager.business.IUserManager;
import net.bioclipse.xws.client.Client;
import net.bioclipse.xws.client.ClientFactory;
import net.bioclipse.xws.client.IExecutionPipe;
import net.bioclipse.xws.exceptions.XmppException;
import net.sf.xws4j.Activator;
import net.sf.xws4j.exceptions.Xws4jException;
import net.sf.xws4j.preferences.PreferenceConstants;

/**
 * This listener handles login and logout events for XMPP.
 * 
 * @author Klas Jšnsson (klas.joensson@gmail.com)
 *
 */
public class XwsLoginOutListener implements IUserManagerListener {

    private static String myAccountType = "";
    private IUserManager userManager;
    private static final Logger logger = 
            Logger.getLogger(XwsLoginOutListener.class);
    private static XwsLoginOutListener thisListener = null;

    /**
     * Instantiate an OpenToxLogInOutListener. This method can only be used if 
     * the OpenToxLogInOutListener has been instantiated with
     *  getInstance(IUserManager userManager) first.
     * 
     * @return An OpenToxLogInOutListener
     * @throws InstantiationException If it hasn't been instantiated before.
     */
    public static XwsLoginOutListener getInstance() 
            throws InstantiationException {
        if (thisListener == null) 
            throw new InstantiationException( "Are you sure it has been " +
                    "instantiatedone before?" );
        
        return thisListener;
    }
    /**
     * Instantiate an OpenToxLogInOutListener with help of the user manager.
     * 
     * @param userManager The user manger used by this session 
     * @return An OpenToxLogInOutListener
     */
    public static XwsLoginOutListener getInstance(IUserManager 
                                                      userManager) {
        if (thisListener == null) 
            thisListener = new XwsLoginOutListener( userManager );
        
        return thisListener;
    }
    /**
     * The constructor.
     * 
     * @param userManager The user manager that are in use for the moment
     */
    private XwsLoginOutListener(IUserManager userManager) {
        myAccountType = getAccountType();
        this.userManager = userManager;
    }

    /**
     * The method that execute an userManagerEvent.
     * @param event The userManagerEvent to be executed
     * @return True if the execution succeeded
     */
    public boolean receiveUserManagerEvent( UserManagerEvent event ) {
        logger.debug( "XwsLoginOutListener received an event: " + event);
        boolean eventSucceeded = false;
        Client client;
        try {
            switch (event) {
                case LOGIN:
                    client = createClient();
                    client.connect();
                    eventSucceeded = client.isConnected();
                    break;

                case LOGOUT:
                    client = getClient();
                    client.disconnect();
                    eventSucceeded = !client.isConnected();
                    break;

                case UPDATE:
                    client = getClient();
                    if (client.isConnected())
                        client.disconnect();
                    client.connect();
                    eventSucceeded = client.isConnected();                    
                    break;

                default:
                    break;
            }
        } catch ( LoginException e1 ) {
            logger.error( e1 );
        } catch ( XmppException e1 ) {
            logger.error( e1 );
        }

        return eventSucceeded;
    }

    /**
     * Returns the account type. This should be the same as the variable name 
     * that is set accountType in the extension point.
     *       
     * @return The name of this accountType
     */
    public String getAccountType() {
        if (myAccountType.isEmpty())
            // TODO When the variable is initiated here it should get its name 
            // from the extension-point some how...
            myAccountType = "XMPP";

        return myAccountType;
    }
        
    /**
     * Tries to get a XMPP-client. If it can't find any it tries to create one, 
     * if that also fails it returns <code>null</code>.
     * 
     * @return A XMPP-client or null
     */
    private Client getClient(){
        Client client = null;
        try {
            client = Activator.getDefaultClientCurator().getDefaultClient();
        } catch ( Xws4jException e ) {
            /* There was some trouble getting a client. Probably 'cos there's
             * non, so let's create one. */
            try {
                client = createClient();
            } catch ( XmppException e1 ) {
                logger.error( e1.getMessage() );
            } catch ( LoginException e1 ) {
                logger.error( e1.getMessage() );
            }
        }
       
        return client;
    }

    /**
     * Creates a new XMPP-client with help of data from Bioclises user manager.
     * If it's not able to create one for some reasons, e.g. there's no user
     * logged in, it throws an exception.
     * 
     * @return A XMPP-client or null
     * @throws LoginException, XmppException
     */
    private Client createClient() throws XmppException, LoginException {
        Client client = null;
        String server, jid, pwd, resource, portStr;
        if ( userManager.isLoggedIn() ) {
            List<String> otssoAccounts = userManager
                    .getAccountIdsByAccountTypeName( getAccountType() );
            if (otssoAccounts.size() > 0) {
                String account = otssoAccounts.get(0);
                /* TODO Server often the domain name of the Jabber id. Idea:
                 * make it not required and if not filled in obtain it from the 
                 * Jabber id...*/
                jid = userManager.getProperty(account, "Jabber ID");
                server = userManager.getProperty(account, "Server");
                pwd = userManager.getProperty(account, "Password"); 
                resource = userManager.getProperty(account, "Resource");
                portStr = userManager.getProperty(account, "Port");
                
                IPreferenceStore preferences = Activator.getDefault().
                        getPreferenceStore();
                preferences.setValue( PreferenceConstants.P_STRING_JID, jid );
                preferences.setValue( PreferenceConstants.P_STRING_SERVER, 
                                      server );
                preferences.setValue( PreferenceConstants.P_STRING_PASSWORD, 
                                      pwd );
                preferences.setValue( PreferenceConstants.P_STRING_RESOURCE, 
                                      resource );
                preferences.setValue( PreferenceConstants.P_STRING_SERVERPORT, 
                                      portStr );
                
                try {
                    client = Activator.getDefaultClientCurator().
                            getDefaultClient();
                } catch ( Xws4jException e ) {
                    /* If we end up here then setting the values in preference 
                     * store, did not help either. So let's create a client 
                     * ourself. The XwsManager and some other functions do not
                     * know about this client, so they will not work properly.*/
                    IExecutionPipe executionPipe = new IExecutionPipe() {
                        public void exec(Runnable r) {
                            /* Eclipse specific code to inject Runnables in the
                             * GUI thread */
                            Display.getDefault().asyncExec(r);
                        }
                    };
                    int port = Integer.parseInt( portStr );
                    
                    try {
                        client = ClientFactory.createClient( jid, pwd, server, 
                                                             port, 
                                                             executionPipe );
                    } catch ( XmppException e1 ) {
                        /* If we end up in here then we are in real trouble, so
                         * lets throw that exception and let someone else 
                         * handle it. */
                        throw e1;
                    }
                }
            }
        } else {
            throw new LoginException( "You have to be logged in to create an " +
            		"XMPP-client" );
        }
           
        return client;
    }
    
}
