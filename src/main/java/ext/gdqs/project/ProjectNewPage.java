package ext.gdqs.project;

import java.io.IOException;
import java.util.Properties;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.StringValidator;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.catalog.impl.CatalogImpl;
import org.geoserver.web.GeoServerSecuredPage;
import org.geoserver.web.wicket.URIValidator;
import org.geoserver.web.wicket.XMLNameValidator;

import ext.gdqs.dataobject.ProjectRequest;
import ext.gdqs.util.ProjectHelper;

public class ProjectNewPage extends GeoServerSecuredPage {
	Form form;
	TextField nsFieldUri;
	
	private String selected = "No Status";

	@SuppressWarnings({ "serial", "unchecked" })
	public ProjectNewPage() throws IOException{
		add(new Label("projectlistlabel","This page will list the projects the user is working on"));
		WorkspaceInfo wsInfo = getCatalog().getFactory().createWorkspace();
		
		final Properties gdqsProps = new Properties();
		gdqsProps.load(getClass().getClassLoader().getResourceAsStream("gdqs.properties"));		

		//Dropdown choice for project status
		final DropDownChoice<String> projStatus = new DropDownChoice<String>(
				"projstatus",new PropertyModel<String>(this,"selected"),ProjectHelper.PROJ_STATUS
		);
		
		form = new Form("form", new CompoundPropertyModel(wsInfo)){
				@Override
				protected void onSubmit(){
					ProjectRequest newProj = new ProjectRequest();
					newProj.setModelObject(form.getModelObject());
					newProj.setNsiURI(nsFieldUri.getDefaultModelObjectAsString());
					newProj.setFolderBasePath(gdqsProps.getProperty("filesystempath"));
					newProj.setProjStatus(selected);

					String projMessage;
					try {
						projMessage = ProjectHelper.createProject(newProj);
						LOGGER.info("Project create message>>>" + projMessage + "<<<");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					doReturn(ProjectListPage.class);
			}
				
		};	
		add(form);

		form.add(projStatus);
		
		TextField<String> projNameTF = new TextField<String>("name");
		projNameTF.setRequired(true);
		projNameTF.add(new XMLNameValidator());
		projNameTF.add(new StringValidator(){
			@Override
			protected void onValidate(IValidatable<String> valid) {
				if (CatalogImpl.DEFAULT.equals(valid.getValue())){
					error(valid,"defaultWSError");
				}	
			}
		});
		form.add(projNameTF.setRequired(true));
		
		nsFieldUri = new TextField("uri", new Model());
		nsFieldUri.setRequired(true);
		nsFieldUri.add(new URIValidator());
		form.add(nsFieldUri);
		
		SubmitLink subLink = new SubmitLink("submit",form);
		form.add(subLink);
		
		AjaxLink canLink = new AjaxLink("cancel"){
			@Override
			public void onClick(AjaxRequestTarget arg0) {
				doReturn(ProjectListPage.class);
			};
		};
		projNameTF.add(canLink);
		
	}
}
