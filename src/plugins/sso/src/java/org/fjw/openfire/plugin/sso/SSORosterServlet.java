package org.fjw.openfire.plugin.sso;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jivesoftware.admin.AuthCheckFilter;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.roster.Roster;
import org.jivesoftware.openfire.roster.RosterItem;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.openfire.user.UserNotFoundException;

import com.alibaba.fastjson.JSON;

public class SSORosterServlet extends HttpServlet {

	private XMPPServer server;
	private UserManager userManager;

	public SSORosterServlet() {

	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		server = XMPPServer.getInstance();
		userManager = server.getUserManager();
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
		RosterResultObject result = new RosterResultObject();
		try {
			HttpParmDecry parmdecry = new HttpParmDecry(request);
			String jid = parmdecry.getParameter("jid");
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
				result.rosters = null;
			}
		} catch (ServerDisableException ex) {
			result.success = false;
			result.status = "501";
			result.message = "ServerID错误或不可用";
			result.rosters = null;
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
