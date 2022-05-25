package models;

import java.io.Serializable;

public class Host implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String address;
	private String alias;
	private String master;
	
	public Host() { }
	
	public Host(String address, String alias, String master) {
		super();
		this.address = address;
		this.alias = alias;
		this.master = master;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	

	public String getMaster() {
		return master;
	}

	public void setMaster(String master) {
		this.master = master;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
