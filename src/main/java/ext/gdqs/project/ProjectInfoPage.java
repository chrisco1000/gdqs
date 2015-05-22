package ext.gdqs.project;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.validation.validator.MinimumValidator;
import org.apache.wicket.validation.validator.UrlValidator;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.NamespaceInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.config.ContactInfo;
import org.geoserver.config.GeoServer;
import org.geoserver.config.GeoServerInfo;
import org.geoserver.config.ServiceInfo;
import org.geoserver.config.SettingsInfo;
import org.geoserver.config.impl.ServiceInfoImpl;
import org.geoserver.ows.util.OwsUtils;
import org.geoserver.web.ComponentAuthorizer;
import org.geoserver.web.GeoServerApplication;
import org.geoserver.web.GeoServerBasePage;
import org.geoserver.web.GeoServerSecuredPage;
import org.geoserver.web.admin.ContactPanel;
import org.geoserver.web.admin.GlobalSettingsPage;
import org.geoserver.web.data.namespace.NamespaceDetachableModel;
import org.geoserver.web.data.settings.SettingsPluginPanelInfo;
import org.geoserver.web.data.workspace.WorkspaceDetachableModel;
import org.geoserver.web.data.workspace.WorkspacePage;
import org.geoserver.web.services.BaseServiceAdminPage;
import org.geoserver.web.services.ServiceMenuPageInfo;
import org.geoserver.web.util.MapModel;
import org.geoserver.web.wicket.GeoServerDialog;
import org.geoserver.web.wicket.HelpLink;
import org.geoserver.web.wicket.ParamResourceModel;
import org.geoserver.web.wicket.URIValidator;
import org.geoserver.web.wicket.XMLNameValidator;
import org.geotools.util.logging.Logging;

import ext.gdqs.util.ProjectHelper;

@SuppressWarnings("rawtypes")
public class ProjectInfoPage extends GeoServerSecuredPage {
	
    private static final Logger LOGGER = Logging.getLogger("org.geoserver.web.data.workspace");
    
	IModel wsModel;
    IModel nsModel;
    //boolean defaultWs;

    GeoServerDialog dialog;
    WorkspaceInfo wsi;
    
    //MetadataMap keys for ProjectInfo table
    //private static final String KEY_
    
    /**
     * Uses a "name" parameter to locate the workspace
     * @param parameters
     */
    public ProjectInfoPage(PageParameters parameters) {
        String wsName = parameters.getString("name");
        wsi = getCatalog().getWorkspaceByName(wsName);
        
        if(wsi == null) {
            error(new ParamResourceModel("WorkspaceEditPage.notFound", this, wsName).getString());
            doReturn(WorkspacePage.class);
            return;
        }
        
        init(wsi);
    }
    
    public ProjectInfoPage(WorkspaceInfo ws) {
        init(ws);
    }
    
    @SuppressWarnings("unchecked")
	private void init(WorkspaceInfo ws) {
        wsModel = new WorkspaceDetachableModel( ws );

        NamespaceInfo ns = getCatalog().getNamespaceByPrefix( ws.getName() );
        nsModel = new NamespaceDetachableModel(ns);
        
        Form form = new Form( "form", new CompoundPropertyModel( nsModel ) ) {
			private static final long serialVersionUID = 1L;

			protected void onSubmit() {
                //Nothing to do yet
            }
        };
        add(form);
        
        //check for full admin, we don't allow workspace admins to change all settings
        boolean isFullAdmin = isAuthenticatedAsAdmin();
        
        TextField name = new TextField("name", new PropertyModel(wsModel, "name"));
        name.setRequired(true);
        name.setEnabled(isFullAdmin);

        name.add(new XMLNameValidator());
        form.add(name);
        TextField uri = new TextField("uri", new PropertyModel(nsModel, "uRI"), String.class);
        uri.setRequired(true);
        uri.add(new URIValidator());
        form.add(uri);
        
        // Map of values for the table
        Map<String,String> values = ProjectHelper.getProjectTableData(wsModel);
        
        Set<String> metaKeys = values.keySet();
        Iterator<String> metaIter = metaKeys.iterator();
        while(metaIter.hasNext()){
        	String key = metaIter.next();
        	//LOGGER.info("Key: " + key + " " + values.get(key));

    		add(new Label(key,ProjectHelper.PROJECT_FIELD_LABELS.get(key)));
    		add(new Label(key + "_val",values.get(key)));
        }
		
        add(dialog = new GeoServerDialog("dialog"));

        //local settings
        form.add(new HelpLink("settingsHelp").setDialog(dialog));

        add(new DownloadLink("qgisdownload", new AbstractReadOnlyModel(){
                    private static final long serialVersionUID = 1L;
                    @Override
                    public File getObject()
                    {
                        File qgisProject;
                        try{
                        	qgisProject = ProjectHelper.createQgisProject(wsModel);
                        }
                        catch (IOException e){
                            throw new RuntimeException(e);
                        }
                        return qgisProject;
                    }
                }));

        SubmitLink submit = new SubmitLink("save");
        form.add(submit);
        form.setDefaultButton(submit);
        form.add(new BookmarkablePageLink("cancel", WorkspacePage.class));     
    }
}
