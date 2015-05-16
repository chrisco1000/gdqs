package ext.gdqs.provider;

import java.util.ArrayList;
import java.util.List;

import org.geoserver.catalog.MetadataMap;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.web.data.workspace.WorkspaceProvider;

import ext.gdqs.util.ProjectHelper;

@SuppressWarnings("serial")
public class ProjectProvider extends WorkspaceProvider {

	public ProjectProvider(){
		super();
	}
	
	@Override
	protected List<WorkspaceInfo> getItems(){
		List<WorkspaceInfo> gdqsProjects = getCatalog().getWorkspaces();
		List<WorkspaceInfo> resultProjects = new ArrayList<WorkspaceInfo>();
		
		for (int ii = 0;ii < gdqsProjects.size();ii++){
			MetadataMap mm = gdqsProjects.get(ii).getMetadata();
			if (mm.containsKey(ProjectHelper.PROJECT_MARKER_KEY) && mm.get(ProjectHelper.PROJECT_MARKER_KEY).equals("true")){
				resultProjects.add(gdqsProjects.get(ii));
			}
		}
		return resultProjects;
	}
}
