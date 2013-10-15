package org.fjw.openfire.plugin;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.fjw.openfire.plugin.sso.SSOIQHandler;
import org.fjw.openfire.plugin.sso.SSOInterceptor;
import org.jivesoftware.openfire.PresenceManager;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.component.Component;
import org.xmpp.component.ComponentException;
import org.xmpp.component.ComponentManager;
import org.xmpp.component.ComponentManagerFactory;
import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;
import org.xmpp.packet.Presence;

public class SSOPlugin implements Plugin, Component {
	private static final String subdomain = "sso";
	private static final Logger Log = LoggerFactory.getLogger(SSOPlugin.class);
	private XMPPServer server;
	private PacketInterceptor pktInterceptor = null;
	private SSOIQHandler ssoIQHandler = null;
	private InterceptorManager icpManager = InterceptorManager.getInstance();

	private UserManager userManager;
	private PresenceManager presenceManager;
	private PluginManager pluginManager;
	private ComponentManager componentManager;
	private String hostname;
	private Map<String, Presence> probedPresence;
	private JID componentJID;

	@Override
	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		System.out.println("SSO 初始化…… ");
		server = XMPPServer.getInstance();
		pktInterceptor = new SSOInterceptor();
		icpManager.addInterceptor(pktInterceptor);
		ssoIQHandler = new SSOIQHandler(this);
		server.getIQRouter().addHandler(ssoIQHandler);

		pluginManager = manager;
		userManager = server.getUserManager();
		presenceManager = server.getPresenceManager();
		hostname = server.getServerInfo().getXMPPDomain();
		probedPresence = new ConcurrentHashMap<String, Presence>();
		componentJID = new JID(subdomain + "." + hostname);

		componentManager = ComponentManagerFactory.getComponentManager();
		try {
			componentManager.addComponent(subdomain, this);
		} catch (Exception e) {
			Log.error(e.getMessage(), e);
		}

	}

	@Override
	public void destroyPlugin() {

		System.out.println("SSO 卸载…… ");
		try {
			icpManager.removeInterceptor(pktInterceptor);
		} catch (Exception e) {
			Log.error(e.getMessage(), e);
		}
		try {
			server.getIQRouter().removeHandler(ssoIQHandler);
		} catch (Exception e) {
			Log.error(e.getMessage(), e);
		}

		try {
			componentManager.removeComponent(subdomain);
			componentManager = null;
		} catch (Exception e) {
			Log.error(e.getMessage(), e);
		}

	}

	@Override
	public void shutdown() {
	}

	@Override
	public void start() {
	}

	@Override
	public void initialize(JID arg0, ComponentManager arg1)
			throws ComponentException {
	}

	@Override
	public String getDescription() {
		return pluginManager.getDescription(this);
	}

	@Override
	public String getName() {
		return pluginManager.getName(this);
	}

	@Override
	public void processPacket(Packet packet) {
		if (packet instanceof Presence) {
			Presence presence = (Presence) packet;
			if (presence.isAvailable()
					|| presence.getType() == Presence.Type.unavailable
					|| presence.getType() == Presence.Type.error) {
				// Store answer of presence probes
				probedPresence.put(presence.getFrom().toString(), presence);
			}
		}
	}
	
	
    public Presence getPresence(String jid) throws UserNotFoundException {
        if (jid == null) {
            throw new UserNotFoundException("Target JID not found in request");
        }
        JID targetJID = new JID(jid);

        // Check that the sender is not requesting information of a remote server entity
        if (targetJID.getDomain() == null || XMPPServer.getInstance().isRemote(targetJID)) {
            throw new UserNotFoundException("Domain does not matches local server domain");
        }
        if (!hostname.equals(targetJID.getDomain())) {
            // Sender is requesting information about component presence, so we send a 
            // presence probe to the component.
            presenceManager.probePresence(componentJID, targetJID);

            // Wait 30 seconds until we get the probe presence result
            int count = 0;
            Presence presence = probedPresence.get(jid);
            while (presence == null) {
                if (count > 300) {
                    // After 30 seconds, timeout
                    throw new UserNotFoundException(
                            "Request for component presence has timed-out.");
                }
                try {
                    Thread.sleep(100);
                }
                catch (InterruptedException e) {
                    // don't care!
                }
                presence = probedPresence.get(jid);

                count++;
            }
            // Clean-up probe presence result
            probedPresence.remove(jid);
            // Return component presence
            return presence;
        }
        if (targetJID.getNode() == null ||
                !UserManager.getInstance().isRegisteredUser(targetJID.getNode())) {
            // Sender is requesting presence information of an anonymous user
            throw new UserNotFoundException("Username is null");
        }
        User user = userManager.getUser(targetJID.getNode());
        return presenceManager.getPresence(user);
    }


}
