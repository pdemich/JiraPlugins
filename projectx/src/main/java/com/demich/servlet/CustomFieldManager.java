package com.demich.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.net.URI;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;

public class CustomFieldManager extends HttpServlet{
 
    private final UserManager userManager;
	private final LoginUriProvider loginUriProvider;
	
    public CustomFieldManager(UserManager userManager, LoginUriProvider loginUriProvider) {
    	
    	this.userManager = userManager;
    	this.loginUriProvider = loginUriProvider;
    	
	}
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
    	UserKey userKey = userManager.getRemoteUser(req).getUserKey();
        if (userKey == null || !userManager.isSystemAdmin(userKey))
        {
          redirectToLogin(req, resp);
          return;
        }
    }

	private void redirectToLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		resp.sendRedirect(loginUriProvider.getLoginUri(getUri(req)).toASCIIString());
		
	}

	private URI getUri(HttpServletRequest req) {
		
		StringBuffer builder = req.getRequestURL();
		  if (req.getQueryString() != null)
		  {
		    builder.append("?");
		    builder.append(req.getQueryString());
		  }
		  return URI.create(builder.toString());
	
	}

}