package com.demich.servlet;

import org.apache.velocity.VelocityContext;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.List;
import java.util.ArrayList;

import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.jira.util.velocity.VelocityRequestContext;
import com.atlassian.jira.web.FieldVisibilityManager;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.fields.NavigableField;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.component.ComponentAccessor;

@Scanned
public class CustFieldManager extends HttpServlet{
	
	@ComponentImport
    private final UserManager userManager;
	@ComponentImport
	private final LoginUriProvider loginUriProvider;
	@ComponentImport
	private final TemplateRenderer renderer;
	@ComponentImport
	private final FieldVisibilityManager fieldVisibilityManager;
	
	private CustomFieldManager cfieldmanager;
	
	private ProjectManager projectManager;
	
	
	
	
	@Inject
    public CustFieldManager(UserManager userManager, LoginUriProvider loginUriProvider, TemplateRenderer renderer, 
    		FieldVisibilityManager fieldVisibilityManager) {
    	
    	this.userManager = userManager;
    	this.loginUriProvider = loginUriProvider;
    	this.renderer = renderer;
    	this.fieldVisibilityManager = fieldVisibilityManager;
    	this.cfieldmanager = ComponentAccessor.getCustomFieldManager();
    	this.projectManager = ComponentAccessor.getProjectManager();
    	
	}
    
    @SuppressWarnings("deprecation")
	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
    	String username = userManager.getRemoteUsername(req);
    	
        if (username == null || !userManager.isSystemAdmin(username))
        {
          redirectToLogin(req, resp);
          return;
        }
        
        List<Project> projects = projectManager.getProjects();
        List<CustomField> fields = cfieldmanager.getCustomFieldObjects();
        Map<String,Object> hiddenFieldMapping = new HashMap<String,Object>();
        ArrayList<NavigableField> names = new ArrayList<NavigableField>(); 
        
        for(Project project : projects ) {
        	ArrayList<Pair<Long,Boolean>> temp = new ArrayList<Pair<Long,Boolean>>();
        	for(CustomField field : fields) {
    			temp.add(new Pair<Long, Boolean>(project.getId(), fieldVisibilityManager.isFieldHiddenInAllSchemes(project.getId(), field.getId())));	
    		}
        	hiddenFieldMapping.put(project.getName(), temp);
        }
        
        
		for(CustomField field : fields) {
			
			names.add(field);
		}
		Boolean wynik = fieldVisibilityManager.isFieldHiddenInAllSchemes(10100L, "customfield_10100");	
		
     // Create the Velocity Context
        Map<String,Object> context = new HashMap<String,Object>();
        context.put("username", username);
        context.put("fieldnames", names);
        context.put("projects", projects);
        context.put("hfmapping", hiddenFieldMapping);
        
        resp.setContentType("text/html;charset=utf-8");
        renderer.render("admin.vm", context, resp.getWriter());
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