package ext.gdqs.dataobject;

public class ProjectRequest {
	private String gdqsName;
	private Object modelObject;
	private String nsiURI;
	private String folderBasePath;

	public Object getModelObject() {
		return modelObject;
	}
	public void setModelObject(Object modelObject) {
		this.modelObject = modelObject;
	}
	public String getGdqsName() {
		return gdqsName;
	}
	public void setGdqsName(String gdqsName) {
		this.gdqsName = gdqsName;
	}
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
}
