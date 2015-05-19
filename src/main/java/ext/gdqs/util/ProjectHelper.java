package ext.gdqs.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.model.IModel;
import org.apache.wicket.util.file.Files;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.MetadataMap;
import org.geoserver.catalog.NamespaceInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.web.GeoServerApplication;

import ext.gdqs.dataobject.ProjectRequest;

public class ProjectHelper {
	public static final String PROJECT_MARKER_KEY = "GDQSProject";
	public static final String PROJECT_STATUS_KEY = "ProjectStatus";
	
	public static final Map<String,String> PROJECT_FIELD_LABELS = new HashMap<String,String>(){
		private static final long serialVersionUID = 1L;
		{
			put(PROJECT_MARKER_KEY,"GDQS Project");
			put(PROJECT_STATUS_KEY,"Project Status");
		}
	};
	
	public static final List<String> PROJ_STATUS = Arrays.asList(new String[]{
			"Planning","In Work","Completed"
	});
	
	public static String createProject(ProjectRequest req){
		String message = "";
		
		GeoServerApplication gsa = GeoServerApplication.get();
		Catalog cat = gsa.getCatalog();
		
		WorkspaceInfo wsi = (WorkspaceInfo) req.getModelObject();

		if (cat.getWorkspaceByName(wsi.getName()) != null){
			message = "Project exists: " + wsi.getName();
		} else {
			wsi.getMetadata().put(PROJECT_MARKER_KEY, true);
			wsi.getMetadata().put(PROJECT_STATUS_KEY, req.getProjStatus());
			
			NamespaceInfo nsi = cat.getFactory().createNamespace();
			nsi.setPrefix(wsi.getName());
			nsi.setURI(req.getNsiURI());
			
			cat.add(wsi);
			cat.add(nsi);
		}
		
		createProjectFolder(req.getFolderBasePath(),wsi.getName());
		
		return message;
	}

	public static String createProjectFolder(String fspath, String projName){
		String message = "";
		
		//Create the project folder in the filesystem directory
		if (!fspath.substring(fspath.length()).equals("\\")) fspath = fspath + "\\";
		String newDir = fspath + projName;
		File projDir = new File(newDir);
		if (!projDir.exists()){
			try{
				projDir.mkdirs();
			} catch (SecurityException sec){
				message = "Unable to create folder structure: " + sec.toString();
			}
		}
		return message;
	}
	
	/***
	 * Create a QGIS project file for the Project
	 * @throws IOException 
	 * 
	 */
	public static File createQgisProject(IModel wsModel) throws IOException{
		WorkspaceInfo workspaceInfo = (WorkspaceInfo) wsModel.getObject();
		
		File qgisProject;
		
		qgisProject = File.createTempFile("GDQS_Project", ".qgs");

		StringBuffer qgis = new StringBuffer();
		qgis.append("<!DOCTYPE qgis PUBLIC 'http://mrcc.com/qgis.dtd' 'SYSTEM'>\n");
		qgis.append("<qgis projectname=\"\" version=\"2.8.1-Wien\">\n");
		qgis.append("<title>");
		qgis.append(workspaceInfo.getName());
		qgis.append("</title>\n");
		qgis.append("</qgis>\n");

        InputStream data = new ByteArrayInputStream(qgis.toString().getBytes());
        Files.writeTo(qgisProject, data);
        
        return qgisProject;
	}
	
	// Get the project metadata with keys
	public static Map<String,String> getProjectTableData(IModel wsModel){
		Map<String,String> metaMap = new HashMap<String,String>();
		
		WorkspaceInfo wsi = (WorkspaceInfo) wsModel.getObject();
		
		// Get the Project MetadataMap
		MetadataMap map = wsi.getMetadata();
		metaMap.put(PROJECT_MARKER_KEY,map.get(PROJECT_MARKER_KEY).toString());
		//metaMap.put(PROJECT_STATUS_KEY,map.get(PROJECT_STATUS_KEY).toString());
		
		return metaMap;
	}
}
