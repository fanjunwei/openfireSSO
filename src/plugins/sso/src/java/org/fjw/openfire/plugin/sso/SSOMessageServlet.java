package org.fjw.openfire.plugin.sso;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fjw.openfire.plugin.SSOPlugin;
import org.jivesoftware.admin.AuthCheckFilter;
import org.jivesoftware.openfire.SessionManager;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.xmpp.packet.JID;

import com.alibaba.fastjson.JSON;

public class SSOMessageServlet extends HttpServlet {


	private XMPPServer server;
	private SessionManager sessionManager;
	private SSOPlugin plugin;

	public SSOMessageServlet() {

	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		server = XMPPServer.getInstance();
		sessionManager = server.getSessionManager();
		plugin = (SSOPlugin) XMPPServer.getInstance().getPluginManager()
				.getPlugin("sso");
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
		ResultObject outobj = new ResultObject();
		HttpParmDecry parmdecry = new HttpParmDecry(request);
		String jid = parmdecry.getParameter("jid");
		String msg = parmdecry.getParameter("msg");

		try {
			plugin.getPresence(jid);
			try {
				sessionManager.sendServerMessage(new JID(jid), null, msg);
				outobj.success = true;
				outobj.status = "200";
				outobj.message = null;
			} catch (Exception ex) {
				outobj.success = false;
				outobj.status = "500";
				outobj.message = ex.getMessage();
			}
		} catch (UserNotFoundException e) {
			outobj.success = false;
			outobj.status = "404";
			outobj.message = "无此用户";
		} catch (IllegalArgumentException e) {
			outobj.success = false;
			outobj.status = "404";
			outobj.message = "无此用户";
		}

		response.setCharacterEncoding("utf-8");
		response.setContentType("application/json");
		String json = JSON.toJSONString(outobj);
		response.getWriter().print(json);
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
