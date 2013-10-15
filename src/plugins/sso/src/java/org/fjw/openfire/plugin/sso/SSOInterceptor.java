package org.fjw.openfire.plugin.sso;

import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.session.Session;
import org.xmpp.packet.Packet;

public class SSOInterceptor implements PacketInterceptor {

	public SSOInterceptor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void interceptPacket(Packet packet, Session session,  
            boolean incoming, boolean processed) throws PacketRejectedException {
		//packet.addExtension(extension)
		System.out.println("SSO Intercept"+packet.getClass().toString());
		System.out.println("SSO Intercept"+packet.toXML());
	}

}
