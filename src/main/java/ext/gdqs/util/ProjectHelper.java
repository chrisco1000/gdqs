package ext.gdqs.util;

import java.io.File;

import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.NamespaceInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.web.GeoServerApplication;

import ext.gdqs.dataobject.ProjectRequest;

public class ProjectHelper {
	public static final String PROJECT_MARKER = "GDQSProject";
	
	public static String createProject(ProjectRequest req){
		String message = "";
		
		GeoServerApplication gsa = GeoServerApplication.get();
		Catalog cat = gsa.getCatalog();
		
		WorkspaceInfo wsi = (WorkspaceInfo) req.getModelObject();

		if (cat.getWorkspaceByName(wsi.getName()) != null){
			message = "Project exists: " + wsi.getName();
		} else {
			wsi.getMetadata().put("GDQSProject", true);
			
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
}
