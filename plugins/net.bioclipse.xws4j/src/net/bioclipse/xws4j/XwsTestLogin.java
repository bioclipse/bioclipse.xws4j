package net.bioclipse.xws4j;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;

import net.bioclipse.usermanager.ITestAccountLogin;
import net.bioclipse.xws.client.Client;
import net.bioclipse.xws.client.IExecutionPipe;
import net.bioclipse.xws.exceptions.XmppException;

/**
 * This class tests if its possibly to login to XMPP with the properties
 * provided in a hash map.
 * 
 * @author klasjonsson
 *
 */
public class XwsTestLogin implements ITestAccountLogin {

    private Logger logger = Logger.getLogger( this.getClass() );
    private XwsLoginOutListener listener;
    
    public XwsTestLogin() { 
        try {
            listener = XwsLoginOutListener.getInstance();
        } catch ( InstantiationException e ) {
            /* If we end up here the listener hasn't been instantiated yet, 
             * but this is done when the plug-in is registered in the usermanger
             * => if not done when calling this method then something is done 
             * wrong some where else....*/
            logger.error( "Tried to log in to OpenTox before the listener" +
                    " was instantiated, should be impossibly: "+e.getMessage());
        }
        
    }
    
    /**
     * Tries to login and if is succeeded it logs out immediately.
     * @param A hash map containing the properties that are to be tested 
     */
    public boolean login( HashMap<String, String> myProperites ) {
        boolean succsess = false;
        String clientJID = myProperites.get( "Jabber ID" );
        String pwd = myProperites.get( "Password" );
        String host = myProperites.get( "Resource" );
        String portStr = myProperites.get( "Port" );
        try {
            int port = Integer.parseInt( portStr );

            IExecutionPipe executionPipe = new IExecutionPipe() {
                public void exec(Runnable r) {
                    // Eclipse specific code to inject Runnables in the GUI thread
                    Display.getDefault().asyncExec(r);
                }
            };

            Client client = new Client( clientJID, pwd, host, port, 
                                        executionPipe );
            client.connect();
            succsess = client.isConnected();
            client.disconnect();
        } catch ( XmppException e ) {
            logger.error( "Something went wrong with the XMPP-client: " + 
                    e.getMessage() );
        } catch (NumberFormatException e) {
            logger.error( "Could not parse the integer for the port:" + 
                    e.getMessage() );
            return false;
        }

        return succsess;
    }
    
    /**
     * A method for identify the test-class. I.e. it returns the name of the 
     * plug-in.
     * 
     * @return The name of the plug-in, i.e. "XMPP"
     */
    public String getAccountType() {
        return listener.getAccountType();
    }

}
