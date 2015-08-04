package ext.gdqs.store;

import java.util.Iterator;
import java.util.Set;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.StringValidator;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.impl.CatalogImpl;
import org.geoserver.web.GeoServerSecuredPage;
import org.geoserver.web.wicket.XMLNameValidator;

import ext.gdqs.project.ProjectListPage;
import ext.gdqs.util.ProjectHelper;

public class StoreNewPage extends GeoServerSecuredPage{
	Form storeform;
	
	@SuppressWarnings("unchecked")
	public StoreNewPage(final PageParameters params) {
		final DataStoreInfo dsinfo = getCatalog().getFactory().createDataStore();
		
		storeform = new Form("storeform", new CompoundPropertyModel(dsinfo)){
			@Override
			protected void onSubmit(){
				
				dsinfo.getMetadata().put("GDQSStore", true);
				dsinfo.setName("TestStore");
				
				doReturn(ProjectListPage.class);
			}
		};
		
		add(storeform);
		
		Set<String> keys = params.keySet();
		Iterator<String> keyiter = keys.iterator();
		while(keyiter.hasNext()) System.out.println("Param key: " + keyiter.next());

		//storeform.add(new Label("name",""));
		storeform.add(new Label("gdqsproject",params.getString(ProjectHelper.PROJECT_MARKER_KEY)));

		TextField<String> storeName = new TextField<String>("name");
		storeName.setRequired(true);
		storeName.add(new XMLNameValidator());
		storeName.add(new StringValidator(){
			@Override
			protected void onValidate(IValidatable<String> valid) {
				if (CatalogImpl.DEFAULT.equals(valid.getValue())){
					error(valid,"defaultWSError");
				}	
			}
		});
		storeform.add(storeName.setRequired(true));
		
		SubmitLink subLink = new SubmitLink("submit",storeform);
		storeform.add(subLink);
	}

}
