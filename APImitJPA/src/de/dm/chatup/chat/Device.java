package de.dm.chatup.chat;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Device  {
	
	@Id
	String deviceID;
	@ManyToOne
	@JoinColumn(name="userid")
	Contact besitzer;
	
	
	public Device() {
		
	}
	
	public Device(String deviceID, Contact besitzer) {
		this.deviceID = deviceID;
		this.besitzer = besitzer;
	}
	
	public de.dm.chatup.network.Network.Device toNetworkDevice() {
		de.dm.chatup.network.Network.Device device = new de.dm.chatup.network.Network.Device();
		device.deviceID = deviceID;
		device.besitzer = besitzer.toNetworkContact();
		return device;
	}

}
