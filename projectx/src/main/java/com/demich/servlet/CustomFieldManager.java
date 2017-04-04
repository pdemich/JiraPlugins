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
    	
    }

}