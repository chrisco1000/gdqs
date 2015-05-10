package ext.gdqs.project;

import static org.geoserver.web.data.workspace.WorkspaceProvider.DEFAULT;
import static org.geoserver.web.data.workspace.WorkspaceProvider.NAME;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import ext.gdqs.project.ProjectNewPage;
import ext.gdqs.provider.ProjectProvider;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.geoserver.catalog.MetadataMap;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.web.CatalogIconFactory;
import org.geoserver.web.GeoServerSecuredPage;
import org.geoserver.web.data.SelectionRemovalLink;
import org.geoserver.web.data.workspace.WorkspaceEditPage;
import org.geoserver.web.data.workspace.WorkspaceProvider;
import org.geoserver.web.spring.security.GeoServerSession;
import org.geoserver.web.wicket.GeoServerDialog;
import org.geoserver.web.wicket.GeoServerTablePanel;
import org.geoserver.web.wicket.Icon;
import org.geoserver.web.wicket.SimpleBookmarkableLink;
import org.geoserver.web.wicket.GeoServerDataProvider.Property;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;


/*
 * This is the main GDQS project list page for the logged in user.
 */
public class ProjectListPage extends GeoServerSecuredPage {
    //WorkspaceProvider provider = new WorkspaceProvider();
	ProjectProvider provider = new ProjectProvider();
    GeoServerTablePanel<WorkspaceInfo> table;
    GeoServerDialog dialog;
    SelectionRemovalLink removal;
    
	public ProjectListPage(){
		// Get the current username
		String currentUser = getUsername();
		add(new Label("projectlistlabel","This page will list the projects the user is working on: " + currentUser));
		
        // the middle table
        add(table = new GeoServerTablePanel<WorkspaceInfo>("table", provider, true) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            protected Component getComponentForProperty(String id, IModel itemModel,
                    Property<WorkspaceInfo> property) {

                if ( property == NAME ) {
                	return workspaceLink(id, itemModel);
                } else if (property == DEFAULT) {
                    if(getCatalog().getDefaultWorkspace().equals(itemModel.getObject()))
                        return new Icon(id, CatalogIconFactory.ENABLED_ICON);
                    else
                        return new Label(id, "");
                }
                
                throw new IllegalArgumentException("No such property "+ property.getName());
            }
            
            @Override
            protected void onSelectionUpdate(AjaxRequestTarget target) {
                removal.setEnabled(table.getSelection().size() > 0);
                target.addComponent(removal);    
            }
        });
        table.setOutputMarkupId(true);
        
        // the confirm dialog
        add(dialog = new GeoServerDialog("dialog"));
        setHeaderPanel(headerPanel());
        
	}

	private String getUsername(){
		String username = "Guest";
		final Authentication user = GeoServerSession.get().getAuthentication();
		final boolean anonymous = user == null || user instanceof AnonymousAuthenticationToken;
		
		if (!anonymous) username = user.getName();
		
		return username;
	}
    
    protected Component headerPanel() {
        Fragment header = new Fragment(HEADER_PANEL, "header", this);
        
        // the add button
        header.add(new BookmarkablePageLink("addNew", ProjectNewPage.class));
        
        // the removal button
        header.add(removal = new SelectionRemovalLink("removeSelected", table, dialog));
        removal.setOutputMarkupId(true);
        removal.setEnabled(false);

        //check for full admin, we don't allow workspace admins to add new workspaces
        //header.setEnabled(isAuthenticatedAsAdmin());
        return header;
    }
    
    Component workspaceLink(String id, final IModel itemModel) {
        IModel nameModel = NAME.getModel(itemModel);
        return new SimpleBookmarkableLink(id, ProjectInfoPage.class, nameModel,
                "name", (String) nameModel.getObject());
    }

}
