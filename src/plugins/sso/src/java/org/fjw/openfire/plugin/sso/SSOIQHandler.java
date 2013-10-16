package org.fjw.openfire.plugin.sso;

import java.util.UUID;

import org.dom4j.Namespace;
import org.dom4j.tree.DefaultElement;
import org.dom4j.tree.DefaultText;
import org.fjw.openfire.plugin.SSOPlugin;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.openfire.handler.IQHandler;
import org.xmpp.packet.IQ;
import org.xmpp.packet.IQ.Type;
import org.xmpp.packet.Presence;

public class SSOIQHandler extends IQHandler {
	private IQHandlerInfo info;
	SSOPlugin mPlugin;

	public SSOIQHandler(SSOPlugin plugin) {
		super("SSOPlugin");
		info = new IQHandlerInfo("token", "com:sso");
		mPlugin = plugin;
	}

	@Override
	public IQHandlerInfo getInfo() {
		return info;
	}

	@Override
	public IQ handleIQ(IQ iq) throws UnauthorizedException {
		System.out.println("SSOIQHandler" + iq.toXML());
		try {
			String uuid = UUID.randomUUID().toString();
			
			IQ resiq = new IQ();
			resiq.setType(Type.result);
			resiq.setTo(iq.getFrom());
			Namespace namespace = new Namespace("", "com:sso");
			DefaultElement element = new DefaultElement("token", namespace);
			DefaultText body = new DefaultText(uuid);
			element.add(body);
			resiq.setChildElement(element);
			Presence presence = mPlugin.getPresence(iq.getFrom().toString());
			presence.deleteExtension("token", "com:sso");
			presence.addChildElement("token", "com:sso").addText(MD5Helper.MD5(uuid));
			return resiq;
		} catch (Throwable ex) {

			System.out.println(ex.getMessage());
			StackTraceElement[] elements = ex.getStackTrace();
			for (int i = 0; i < elements.length; i++) {
				StackTraceElement e = elements[i];
				System.out.println(e.getClassName() + ":" + e.getLineNumber());
			}

		}
		return null;
	}

}
