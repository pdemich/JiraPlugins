package com.demich.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.inject.Inject;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.io.IOException;

import java.net.URI;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;

@Scanned
public class CustomFieldManager extends HttpServlet{
	
	@ComponentImport
    private final UserManager userManager;
	@ComponentImport
	private final LoginUriProvider loginUriProvider;
	@ComponentImport
	private final TemplateRenderer renderer;
	
	@Inject
    public CustomFieldManager(UserManager userManager, LoginUriProvider loginUriProvider, TemplateRenderer renderer) {
    	
    	this.userManager = userManager;
    	this.loginUriProvider = loginUriProvider;
    	this.renderer = renderer;
    	
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
        
        resp.setContentType("text/html;charset=utf-8");
        renderer.render("admin.vm", resp.getWriter());
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