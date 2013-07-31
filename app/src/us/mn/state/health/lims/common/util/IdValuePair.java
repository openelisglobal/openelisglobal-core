package us.mn.state.health.lims.common.util;

import java.io.Serializable;

/*
 * Nothing special, just for when id's and values should be encapsulated. 
 * 
 * N.B. This is very light weight, if you want to stick it in a hash and want to use something
 * other than identity of equals then over-ride equals and hash.
 */
public class IdValuePair implements Serializable{

	private static final long serialVersionUID = 1L;
	private String id;
	private String value;
	
	public IdValuePair( String id, String value){
		this.setId(id);
		this.setValue(value);
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}

