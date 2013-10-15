package org.fjw.openfire.plugin.sso;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fjw.openfire.plugin.SSOPlugin;
import org.jivesoftware.admin.AuthCheckFilter;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.roster.Roster;
import org.jivesoftware.openfire.roster.RosterItem;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SSORosterServlet extends HttpServlet {

	private static final Logger Log = LoggerFactory
			.getLogger(SSORosterServlet.class);

	private SSOPlugin plugin;
	private XMPPServer server;
	private UserManager userManager;

	public SSORosterServlet() {

	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		server = XMPPServer.getInstance();
		userManager = server.getUserManager();
		plugin = (SSOPlugin) XMPPServer.getInstance().getPluginManager()
				.getPlugin("sso");
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
		PrintWriter out = response.getWriter();
		try {
			Roster cachedRoster = userManager.getUser(jid).getRoster();
			for (RosterItem item : cachedRoster.getRosterItems()) {
				out.println(item.getJid());
				out.println(item.getNickname());
			} 
		} catch (UserNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
