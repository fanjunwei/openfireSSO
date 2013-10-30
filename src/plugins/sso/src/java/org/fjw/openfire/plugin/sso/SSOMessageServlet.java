package org.fjw.openfire.plugin.sso;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jivesoftware.admin.AuthCheckFilter;
import org.jivesoftware.openfire.SessionManager;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.openfire.user.UserManager;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;

import com.alibaba.fastjson.JSON;

public class SSOMessageServlet extends HttpServlet {

	private XMPPServer server;
	private SessionManager sessionManager;
	private UserManager userManager;

	public SSOMessageServlet() {

	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		server = XMPPServer.getInstance();
		sessionManager = server.getSessionManager();
		userManager = server.getUserManager();
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
		MessageResultObject outobj = new MessageResultObject();
		outobj.message = null;
		outobj.success = true;
		try {
			HttpParmDecry parmdecry = new HttpParmDecry(request);
			String msg = parmdecry.getParameter("msg");
			Enumeration<String> allnames = request.getParameterNames();
			while (allnames.hasMoreElements()) {
				String name = allnames.nextElement();

				if (name.startsWith("jid")) {
					String index = "0";
					int search = name.indexOf("_");
					if (search != -1) {
						index = name.substring(search + 1, name.length());
					}
					String jid = parmdecry.getParameter(name);
					MessageResultObjectItem res = sendMessage(jid, msg, index);
					if (!res.success) {
						outobj.success = false;
					}
					outobj.results.add(res);

				}
			}
			if (outobj.success) {
				outobj.status = "200";
			} else {
				outobj.status = "201";
			}
		} catch (ServerDisableException ex) {

			outobj.success = false;
			outobj.status = "501";
			outobj.message = "ServerID错误或不可用";
		}

		response.setCharacterEncoding("utf-8");
		response.setContentType("application/json");
		String json = JSON.toJSONString(outobj);
		response.getWriter().print(json);
	}

	private MessageResultObjectItem sendMessage(String jid, String msg,
			String index) {
		MessageResultObjectItem outobj = new MessageResultObjectItem();
		outobj.jid = jid;
		outobj.index = index;
		if (jid != null && userManager.isRegisteredUser(jid) == true) {

			try {
				User user = userManager.getUser(jid);
				if (server.getPresenceManager().isAvailable(user)) {
					sessionManager.sendServerMessage(new JID(jid), null, msg);
				} else {
					// 离线消息
					Message message = new Message();
					message.setFrom(new JID(server.getServerInfo()
							.getXMPPDomain()));
					message.setTo(new JID(jid));
					message.setBody(msg);
					server.getOfflineMessageStrategy().storeOffline(message);
				}
				outobj.success = true;
				outobj.status = "200";
				outobj.message = null;
			} catch (Throwable ex) {
				outobj.success = false;
				outobj.status = "500";
				outobj.message = ex.getMessage();
			}
		} else {
			outobj.success = false;
			outobj.status = "404";
			outobj.message = "无此用户";
		}
		return outobj;
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
