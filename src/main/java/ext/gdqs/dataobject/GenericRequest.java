package ext.gdqs.dataobject;

public class GenericRequest {
	private String type;
	private String gdqsName;
	private Object modelObject;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getGdqsName() {
		return gdqsName;
	}

	public void setGdqsName(String gdqsName) {
		this.gdqsName = gdqsName;
	}

	public Object getModelObject() {
		return modelObject;
	}

	public void setModelObject(Object modelObject) {
		this.modelObject = modelObject;
	}
}
