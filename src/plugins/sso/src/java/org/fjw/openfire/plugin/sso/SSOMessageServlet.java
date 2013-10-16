package org.fjw.openfire.plugin.sso;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jivesoftware.admin.AuthCheckFilter;
import org.jivesoftware.openfire.SessionManager;
import org.jivesoftware.openfire.XMPPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.JID;

public class SSOMessageServlet extends HttpServlet {

	private static final Logger Log = LoggerFactory
			.getLogger(SSOMessageServlet.class);

	private XMPPServer server;
	private SessionManager sessionManager;

	public SSOMessageServlet() {

	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		server = XMPPServer.getInstance();
		sessionManager = server.getSessionManager();
		AuthCheckFilter.addExclude("sso/message");
	}

	@Override
	public void destroy() {
		super.destroy();
		AuthCheckFilter.removeExclude("sso/message");
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		String jid = request.getParameter("jid");
		String msg = request.getParameter("msg");
		out.println(jid);
		out.println(msg);
		sessionManager.sendServerMessage(new JID(jid), null, msg);
		out.println("ok");
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
