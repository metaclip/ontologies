package es.predictia.metaclip.visualizer.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MetaclipGraph {

	private Collection<MetaclipNode> nodes;
	
	private Collection<MetaclipRelationship> relationships;
	
	private Map<String,MetaclipClass> classes;
	
	private Map<String,String> prefixes;
	
	public Collection<MetaclipNode> getNodes(){
		if(nodes == null){
			nodes = new ArrayList<>();
		}
		return nodes;
	}
	
	public void addNode(MetaclipNode node){
		getNodes().add(node);
	}
	
	public Collection<MetaclipRelationship> getRelationships(){
		if(relationships == null){
			relationships = new ArrayList<>();
		}
		return relationships;
	}
	
	public void addRelationship(MetaclipRelationship relationships){
		getRelationships().add(relationships);
	}
	
	public Map<String,MetaclipClass> getClasses(){
		if(classes == null){
			classes = new HashMap<>();
		}
		return classes;
	}
	
	public void addClass(String prefix,MetaclipClass clazz){
		getClasses().put(prefix,clazz);
	}
	
	public Map<String,String> getPrefixes(){
		if(prefixes == null){
			prefixes = new HashMap<>();
		}
		return prefixes;
	}
	
	public void addPrefixes(String prefix,String clazz){
		getPrefixes().put(prefix,clazz);
	}
	
}
