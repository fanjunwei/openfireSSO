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

import com.alibaba.fastjson.JSON;

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
		AuthCheckFilter.addExclude("sso/roster");
	}

	@Override
	public void destroy() {
		super.destroy();
		AuthCheckFilter.removeExclude("sso/roster");
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		HttpParmDecry parmdecry = new HttpParmDecry(request);
		String jid = parmdecry.getParameter("jid");
		RosterResultObject result = new RosterResultObject();
		try {
			Roster cachedRoster = userManager.getUser(jid).getRoster();
			result.success = true;
			result.status = "200";
			result.message = null;
			for (RosterItem item : cachedRoster.getRosterItems()) {
				RosterResultObjectItem ritem = new RosterResultObjectItem();
				ritem.nickname = item.getNickname();
				ritem.jid = item.getJid().toString();
				result.rosters.add(ritem);

			}
		} catch (UserNotFoundException e) {
			result.success = false;
			result.status = "404";
			result.message = "无此用户";
			result.rosters=null;
		}
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/json");
		String json = JSON.toJSONString(result);
		response.getWriter().print(json);
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
