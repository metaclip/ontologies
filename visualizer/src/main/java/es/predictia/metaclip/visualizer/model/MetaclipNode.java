package es.predictia.metaclip.visualizer.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import lombok.Data;

@Data
public class MetaclipNode {

	private String id;
	private String label;
	private String group;
	private Collection<String> classes = new HashSet<>();
	private Map<String,String> properties = new HashMap<>();
	
	public void addProperty(String value,String key){
		if(properties == null){
			properties = new HashMap<>();
		}
		properties.put(value,key);
	}
	
}
