package ext.gdqs.dataobject;

public class ProjectRequest extends GenericRequest{
	private String nsiURI;
	private String folderBasePath;
	private String projStatus;

	public String getNsiURI() {
		return nsiURI;
	}
	public void setNsiURI(String nsiURI) {
		this.nsiURI = nsiURI;
	}
	public String getFolderBasePath() {
		return folderBasePath;
	}
	public void setFolderBasePath(String folderBasePath) {
		this.folderBasePath = folderBasePath;
	}
	public String getProjStatus() {
		return projStatus;
	}
	public void setProjStatus(String projStatus) {
		this.projStatus = projStatus;
	}
}
