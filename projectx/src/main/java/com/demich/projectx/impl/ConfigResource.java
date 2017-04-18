package com.demich.projectx.impl;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.layout.field.EditableFieldLayout;
import com.atlassian.jira.issue.fields.layout.field.FieldLayout;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutManager;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

import java.util.Set;

import javax.inject.Inject;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;

@Path("/")
@Scanned
public class ConfigResource
{
    @ComponentImport
    private final UserManager userManager;
    @ComponentImport
    private final PluginSettingsFactory pluginSettingsFactory;
    @ComponentImport
    private final TransactionTemplate transactionTemplate;
    
    private ProjectManager projectManager;
	private FieldLayoutManager fieldLayoutManager;


    @Inject
    public ConfigResource(UserManager userManager, PluginSettingsFactory pluginSettingsFactory,
                          TransactionTemplate transactionTemplate)
    {
        this.userManager = userManager;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.transactionTemplate = transactionTemplate;
        this.projectManager = ComponentAccessor.getProjectManager();
    	this.fieldLayoutManager = ComponentAccessor.getFieldLayoutManager();
    }
    
    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static final class Config
    {
      @XmlElement private String name;
      @XmlElement private int time;
            
      public String getName()
      {
        return name;
      }
            
      public void setName(String name)
      {
        this.name = name;
      }
            
      public int getTime()
      {
        return time;
      }
            
      public void setTime(int time)
      {
        this.time = time;
      }
    }
    
    @SuppressWarnings("deprecation")
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@Context HttpServletRequest request)
    {
      String username = userManager.getRemoteUsername(request);
      if (username == null || !userManager.isSystemAdmin(username))
      {
        return Response.status(Status.UNAUTHORIZED).build();
      }

      return Response.ok(transactionTemplate.execute(new TransactionCallback()
      {
        public Object doInTransaction()
        {
          PluginSettings settings = pluginSettingsFactory.createGlobalSettings();
          Config config = new Config();
          config.setName((String) settings.get(Config.class.getName() + ".name"));
                    
          String time = (String) settings.get(Config.class.getName() + ".time");
          if (time != null)
          {
            config.setTime(Integer.parseInt(time));
          }
          return config;
        }
      })).build();
    }
    
    @SuppressWarnings("deprecation")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response put(final Config config, @Context HttpServletRequest request)
    {
      String username = userManager.getRemoteUsername(request);
      if (username == null || !userManager.isSystemAdmin(username))
      {
        return Response.status(Status.UNAUTHORIZED).build();
      }

      transactionTemplate.execute(new TransactionCallback()
      {
        public Object doInTransaction()
        {
          PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
          pluginSettings.put(Config.class.getName() + ".name", config.getName());
          pluginSettings.put(Config.class.getName()  +".time", Integer.toString(config.getTime()));
          return null;
        }
      });
      return Response.noContent().build();
    }
    private void hideFieldForProject(String projectKey, String fieldId) {
    	
       	Project project = projectManager.getProjectObjByKeyIgnoreCase(projectKey);
    	Set<FieldLayout> associatedLayouts = fieldLayoutManager.getUniqueFieldLayouts(project);
    	
    	for(FieldLayout layout : associatedLayouts){	
    		EditableFieldLayout tempEFL =	fieldLayoutManager.getEditableFieldLayout(layout.getId());
    		tempEFL.hide(layout.getFieldLayoutItem(fieldId));
    		fieldLayoutManager.storeEditableFieldLayout(tempEFL);
    	}
    }
    
    private void showFieldForProject(String projectKey, String fieldId) {
    	
    	Project project = projectManager.getProjectObjByKeyIgnoreCase(projectKey);
    	Set<FieldLayout> associatedLayouts = fieldLayoutManager.getUniqueFieldLayouts(project);
    	
    	for(FieldLayout layout : associatedLayouts){	
    		EditableFieldLayout tempEFL =	fieldLayoutManager.getEditableFieldLayout(layout.getId());
    		tempEFL.show(layout.getFieldLayoutItem(fieldId));
    		fieldLayoutManager.storeEditableFieldLayout(tempEFL);
    	}
    }
    
}
