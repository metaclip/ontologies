package es.predictia.metaclip.visualizer.model;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class MetaclipClass {

	private String id;

	private Map<String,String> annotations = new HashMap<>();
	
	public void addAnnotation(String value,String key){
		if(annotations == null){
			annotations = new HashMap<>();
		}
		annotations.put(value,key);
	}
	
}
