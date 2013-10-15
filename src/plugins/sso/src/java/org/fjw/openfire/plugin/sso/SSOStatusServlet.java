package org.fjw.openfire.plugin.sso;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fjw.openfire.plugin.SSOPlugin;
import org.jivesoftware.admin.AuthCheckFilter;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.Presence;

public class SSOStatusServlet extends HttpServlet {

	private static final Logger Log = LoggerFactory
			.getLogger(SSOStatusServlet.class);

	private SSOPlugin plugin;
	private XMLPresenceProvider xmlProvider;

	public SSOStatusServlet() {

	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		plugin = (SSOPlugin) XMPPServer.getInstance().getPluginManager()
				.getPlugin("sso");
		xmlProvider = new XMLPresenceProvider();
		AuthCheckFilter.addExclude("sso/status");
	}

	@Override
	public void destroy() {
		super.destroy();
		AuthCheckFilter.removeExclude("sso/status");
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String jid = request.getParameter("jid");
		try {
			Presence presence = plugin.getPresence(jid);
			xmlProvider.sendInfo(request, response, presence);
		} catch (UserNotFoundException e) {
			xmlProvider.sendUserNotFound(request, response);
		} catch (IllegalArgumentException e) {
			xmlProvider.sendUserNotFound(request, response);
		}

	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
