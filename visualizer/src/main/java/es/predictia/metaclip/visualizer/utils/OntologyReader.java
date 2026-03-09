package es.predictia.metaclip.visualizer.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.ext.com.google.common.collect.ImmutableMap;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.GraphUtil;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_Literal;
import org.apache.jena.graph.Node_URI;
import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import es.predictia.metaclip.visualizer.model.MetaclipClass;
import es.predictia.metaclip.visualizer.model.MetaclipGraph;
import es.predictia.metaclip.visualizer.model.MetaclipNode;
import es.predictia.metaclip.visualizer.model.MetaclipRelationship;

public class OntologyReader {

	public static final String METACLIP_BASE_URI = "http://metaclip.predictia.es/";

	// TODO move to config file
	// uri, prefix and boolean to show it in the class hierarchy
	public static final Map<String,Pair<String,Boolean>> MODEL_URIS = ImmutableMap.<String,Pair<String,Boolean>>builder()
		.put(METACLIP_BASE_URI+"datasource/datasource.owl#",Pair.of("ds",true))
		.put(METACLIP_BASE_URI+"verification/verification.owl#",Pair.of("vr",true))
		.put(METACLIP_BASE_URI+"calibration/calibration.owl#",Pair.of("cl",true))
		.put(METACLIP_BASE_URI+"graphical_output/graphical_output.owl#",Pair.of("gp",true))
		.put("http://www.w3.org/ns/prov#",Pair.of("prov",true))
		.put("http://www.w3.org/2000/01/rdf-schema#",Pair.of("rdf",false))
		.put("http://www.opengis.net/ont/geosparql",Pair.of("geo",true))
		.put("http://www.w3.org/2002/07/owl",Pair.of("owl",false))		
	.build();
	
	// TODO move to config file
	private static final Map<String,String> GROUPS = ImmutableMap.<String,String>builder()
		.put("ds:ENSO","datasource")
		.put("ds:MultiDecadalSimulationDataset","datasource")
		.put("ds:Argument","command-argument")
		.put("ds:DatasetSubset","datasource")
		.put("ds:Initialization","datasource-details")
		.put("ds:TemporalResolution","datasource-details")
		.put("ds:PCA","transformation")
		.put("ds:Project","datasource")
		.put("ds:DatasetVersion","datasource")
		.put("ds:Longitude","datasource-details")
		.put("ds:Realization","datasource-details")
		.put("ds:Member","datasource-details")
		.put("ds:ObservationalDataset","datasource")
		.put("ds:AnomalyCalculation","transformation")
		.put("ds:ClimateIndex","transformation")
		.put("ds:Dataset","datasource")
		.put("ds:ValidationTime","datasource-details")
		.put("ds:ArgumentValue","command-argument")
		.put("ds:SeasonalOperationalDataset","datasource")
		.put("ds:ETCCDI","transformation")
		.put("ds:Dimension","datasource-details")
		.put("ds:Aggregation","transformation")
		.put("ds:TemporalInstant","datasource-details")
		.put("ds:NAO","datasource-details")
		.put("ds:Regridding","transformation")
		.put("ds:ModellingCenter","datasource")
		.put("ds:Step","step")
		.put("ds:Latitude","datasource-details")
		.put("ds:Climatology","transformation")
		.put("ds:Command","command-name")
		.put("ds:VariableStandardDefinition","datasource-details")
		.put("ds:ReanalysisDataset","datasource")
		.put("ds:TemporalPeriod","datasource-details")
		.put("ds:Package","command-name")
		.put("ds:ENSOregion","datasource-details")
		.put("ds:Transformation","transformation")
		.put("ds:SeasonalHindcastDataset","datasource")
		.put("ds:Variable","datasource")
		.put("ds:SpatialExtent","datasource-details")
		.put("vr:Resolution","verification")
		.put("vr:Accuracy","verification")
		.put("vr:Ensemble","verification")
		.put("vr:MulticategoryProbability","verification")
		.put("vr:Association","verification")
		.put("vr:Discrimination","verification")
		.put("vr:SkillScore","verification")
		.put("vr:ForecastRepresentation","verification")
		.put("vr:Reliability","verification")
		.put("vr:Verification","verification")
		.put("vr:Bias","verification")
		.put("vr:ContinuousProbability","verification")
		.put("vr:BinaryProbability","verification")
		.put("vr:QualityAspect","verification")
		.put("vr:DeterministicContinuous","verification")
		.put("cl:ProbabilityScaling","")
		.put("cl:ESD-MOS","calibration")
		.put("cl:Analogs","calibration")
		.put("cl:EnsembleRecalibration","calibration")
		.put("cl:LinearScaling","calibration")
		.put("cl:QQMapping","calibration")
		.put("cl:ESD","calibration")
		.put("cl:TransferFunctions","calibration")
		.put("cl:CCA","calibration")
		.put("cl:BiasAdjustment","calibration")
		.put("cl:VarianceInflation","calibration")
		.put("cl:ESD-PP","calibration")
		.put("cl:Calibration","calibration")
		.put("cl:WeatherTyping","calibration")
		.put("gp:GraphicalProduct","graphical")
		.put("gp:ChartPoints","graphical")
		.put("gp:ChartRaster","graphical")
		.put("gp:Chart","graphical")
		.put("gp:ObservationalUncertainty","graphical")
		.put("gp:SamplingUncertainty","graphical")
		.put("gp:SpatialChartAxis","graphical")
		.put("gp:ProbabilityChartAxis","graphical")
		.put("gp:ChartLines","graphical")
		.put("gp:ChartLayer","graphical")
		.put("gp:MagnitudeChartAxis","graphical")
		.put("gp:TextLayer","graphical")
		.put("gp:ForecastSystemUncertainty","graphical")
		.put("gp:MapLayer","graphical")
		.put("gp:Layer","graphical")
		.put("gp:MapPoints","graphical")
		.put("gp:ChartPolygons","graphical")
		.put("gp:TimeChartAxis","graphical")
		.put("gp:TransformationUncertainty","graphical")
		.put("gp:Mask","graphical")
		.put("gp:Map","graphical")
		.put("gp:MapRaster","graphical")
		.put("gp:Uncertainty","graphical")
		.put("gp:ChartAxis","graphical")
		.put("gp:MapPolygons","graphical")
		.put("gp:MapLines","graphical")
	.build();
	
	private static final Function<String,String> COMPRESS_CLASS_NAME = s -> {
		for(Map.Entry<String,Pair<String,Boolean>> entry : MODEL_URIS.entrySet()){
			if(s.indexOf(entry.getKey())==0){
				return s.replace(entry.getKey(),entry.getValue().getLeft()+":");
			}
		}
		return s;
	};

	private static final Function<String,String> DECOMPRESS_CLASS_NAME = s -> {
		for(Map.Entry<String,Pair<String,Boolean>> entry : MODEL_URIS.entrySet()){
			String prefix = entry.getValue().getKey();
			if(!prefix.endsWith(":")) prefix += ":";
			if(s.indexOf(prefix)==0){
				return s.replace(entry.getValue().getLeft()+":",entry.getKey());
			}
		}
		return s;
	};
	
	private static final Map<String, OntModel> MODELS = new HashMap<>();
	
	public static Optional<MetaclipNode> find(MetaclipGraph metaclipGraph, Graph graph, Node_URI node){
		Optional<MetaclipNode> metaclipNode = metaclipGraph.getNodes().stream().filter(s ->{
			return s.getId().equals(node.getURI());
		}).findAny();
		if(metaclipNode.isPresent()){
			return metaclipNode;
		}else{
			// looking for the node as an individual in the corresponding model
			Optional<OntModel> model = model(node.getNameSpace());
			if(model.isPresent()){
				Individual individual = model.get().getIndividual(node.getURI());
				if(individual != null){
					MetaclipNode newMetaclipNode = create(graph,individual);
					properties(newMetaclipNode,metaclipGraph,individual,graph);
					metaclipGraph.addNode(newMetaclipNode);
					return Optional.of(newMetaclipNode);
				}
			}
		}
		return Optional.empty();
	}
	
	public static Optional<Node_URI> type(Graph graph,Node_URI node){
		List<Node> properties = GraphUtil.listPredicates(graph, node, Node.ANY).toList();
		for(Node property : properties){
			if(!(property instanceof Node_URI)){
				continue;
			}
			List<Node> propertieValues = GraphUtil.listObjects(graph, node, property).toList();
			for(Node propertyValue : propertieValues){
				if(!RDF.type.asNode().equals(property)) continue;
				if(propertyValue instanceof Node_Literal){
					return Optional.of((Node_URI)NodeFactory.createURI(propertyValue.getLiteralValue().toString()));
				}else if(propertyValue instanceof Node_URI){
					return Optional.of((Node_URI)NodeFactory.createURI(propertyValue.getURI()));
				}
			}
		}
		return Optional.empty();
	}
	
	public static MetaclipNode create(Graph graph, Node_URI node){
		MetaclipNode newNode = new MetaclipNode();
		newNode.setId(node.getLocalName());

		// guess if it is not an individual
		if(!model(node.getNameSpace()).isPresent()){
			// add parent classes
			Optional<Node_URI> typeNode = type(graph, node);
			if(typeNode.isPresent()){
				LinkedHashSet<String> parents = new LinkedHashSet<>();
				parents.add(typeNode.get().getURI());
				OntologyReader.parentClasses((Node_URI)NodeFactory.createURI(typeNode.get().getURI()), parents);
				if(!parents.isEmpty()){
					newNode.setClasses(parents.stream().map(COMPRESS_CLASS_NAME).collect(Collectors.toCollection(LinkedHashSet::new)));
				}
				String group = COMPRESS_CLASS_NAME.apply(typeNode.get().getURI());
				if(GROUPS.containsKey(group)){
					group = GROUPS.get(group);
				}
				newNode.setGroup(group);
			}
		}
		return newNode;
	}
	
	public static void annotations(MetaclipGraph metaclipGraph){
		Set<String> usedClasses = new HashSet<String>();
		for(MetaclipNode node : metaclipGraph.getNodes()){
			for(String clazz : node.getClasses()){
				usedClasses.add(clazz);
			}
		}
		for(String usedClass : usedClasses){
			Optional<OntClass> ontClass = clazz((Node_URI)NodeFactory.createURI(usedClass));
			MetaclipClass metaclipClass = new MetaclipClass();
			metaclipClass.setId(usedClass);
			if(ontClass.isPresent()){
				StmtIterator iterator = ontClass.get().listProperties();
				while(iterator.hasNext()){
					Statement statement = iterator.next();
					Triple triple = statement.asTriple();
					if(RDF.type.asNode().equals(triple.getPredicate())) continue;
					if(triple.getPredicate().getLocalName().equals("subClassOf")) continue;
					if(triple.getObject() instanceof Node_URI){
						metaclipClass.addAnnotation(triple.getPredicate().toString(),triple.getObject().getNameSpace());
					}else if(triple.getObject() instanceof Node_Literal){
						metaclipClass.addAnnotation(triple.getPredicate().toString(),triple.getObject().getLiteralValue().toString());
					}
				}
			}
			String prefix = usedClass;
			if(MODEL_URIS.containsKey(usedClass)){
				prefix = MODEL_URIS.get(usedClass).getLeft();
			}
			metaclipGraph.addClass(prefix,metaclipClass);
		}
	}
	
	public static void prefixes(MetaclipGraph metaclipGraph){
		for(Map.Entry<String,Pair<String,Boolean>> entry : MODEL_URIS.entrySet()){
			metaclipGraph.addPrefixes(entry.getValue().getLeft(),entry.getKey());
		}		
	}
	
	public static MetaclipNode create(Graph graph, Individual individual){
		MetaclipNode newNode = create(graph,(Node_URI)individual.asNode());
		if(CollectionUtils.isEmpty(newNode.getClasses())){
			// add parent classes
			Node_URI typeNode = (Node_URI)individual.getRDFType().asNode();
			Set<String> parents = new LinkedHashSet<>();
			parents.add(typeNode.getURI());
			OntologyReader.parentClasses((Node_URI)NodeFactory.createURI(typeNode.getURI()), parents);
			if(!parents.isEmpty()){
				newNode.setClasses(parents.stream().map(COMPRESS_CLASS_NAME).collect(Collectors.toCollection(LinkedHashSet::new)));
			}
			newNode.setGroup(typeNode.getLocalName());
		}
		return newNode;
	}
	
	public static void properties(MetaclipNode metaclipNode,MetaclipGraph metaclipGraph,Individual individual,Graph graph){
		StmtIterator iterator = individual.listProperties();
		while(iterator.hasNext()){
			Statement statement = iterator.next();
			Triple property = statement.asTriple();
			if(RDF.type.asNode().equals(property.getPredicate())) continue;
			if(property.getObject() instanceof Node_Literal){
				metaclipNode.addProperty(property.getPredicate().getLocalName(),property.getObject().getLiteralValue().toString());
			}else if(property.getObject() instanceof Node_URI){
				MetaclipRelationship relationship = new MetaclipRelationship();
				relationship.setType(property.getPredicate().getLocalName());
				Optional<MetaclipNode> objectNodeOpt = OntologyReader.find(metaclipGraph,graph,(Node_URI)property.getObject());
				MetaclipNode objectNode = null;
				if(!objectNodeOpt.isPresent()){
					objectNode = OntologyReader.create(graph,(Node_URI)property.getObject());
				}else{
					objectNode = objectNodeOpt.get();
				}
				relationship.setSource(property.getSubject().getURI());
				relationship.setTarget(objectNode.getId());
				metaclipGraph.addRelationship(relationship);
			}
		}
	}
	
	public static void properties(MetaclipNode metaclipNode,MetaclipGraph metaclipGraph,Node_URI node,Graph graph){
		// properties pointing to this subject
		List<Node> properties = GraphUtil.listPredicates(graph, node, Node.ANY).toList();
		for(Node property : properties){
			if(!(property instanceof Node_URI)){
				LOGGER.warn("Literal properties are not supported: "+property.toString());
				continue;
			}
			// values for this property
			List<Node> propertieValues = GraphUtil.listObjects(graph, node, property).toList();
			for(Node propertyValue : propertieValues){
				if(RDF.type.asNode().equals(property)) continue;
				if(propertyValue instanceof Node_Literal){
					metaclipNode.addProperty(COMPRESS_CLASS_NAME.apply(property.getURI()),propertyValue.getLiteralValue().toString());
				}else if(propertyValue instanceof Node_URI){
					MetaclipRelationship relationship = new MetaclipRelationship();
					relationship.setType(COMPRESS_CLASS_NAME.apply(property.getURI()));
					Optional<MetaclipNode> objectNodeOpt = OntologyReader.find(metaclipGraph,graph,(Node_URI)propertyValue);
					MetaclipNode objectNode = null;
					if(!objectNodeOpt.isPresent()){
						objectNode = OntologyReader.create(graph,(Node_URI)propertyValue);
					}else{
						objectNode = objectNodeOpt.get();
					}
					relationship.setSource(metaclipNode.getId());
					relationship.setTarget(objectNode.getId());
					metaclipGraph.addRelationship(relationship);
				}
			}
		}
	}

	public static void parentClasses(Node_URI node, Collection<String> parents){
		Optional<OntClass> clazzOpt = clazz(node);
		if(clazzOpt.isPresent()){
			OntClass clazz = clazzOpt.get();
			OntClass parent = clazz.getSuperClass();
			if(parent == null) return;
			if(!parent.equals(clazz)){
				parents.add(parent.getURI());
				parentClasses((Node_URI)parent.asNode(), parents);
			}
		}		
	}
	
	public static Optional<OntClass> clazz(Node_URI node){
		String uri = DECOMPRESS_CLASS_NAME.apply(node.getNameSpace());
		Optional<OntModel> model = model(uri);
		if(model.isPresent()){
			ExtendedIterator<OntClass> extendedIterator = model.get().listClasses();
			while(extendedIterator.hasNext()){
				OntClass clazz = extendedIterator.next();
				if(node.getLocalName().equals(clazz.getLocalName())){
					return Optional.of(clazz);
				}
			}
		}
		return Optional.empty();
	}
	
	public static Optional<OntModel> model(String uri){
		if(MODELS.isEmpty()){
			for(Map.Entry<String,Pair<String,Boolean>> entry : MODEL_URIS.entrySet()){
				if(!entry.getValue().getRight()) continue;
				OntModel ontModel = ModelFactory.createOntologyModel();
				ontModel.read(entry.getKey());
				if(!ontModel.isEmpty()){
					MODELS.put(entry.getKey(), ontModel);
				}
			}
		}
		return Optional.ofNullable(MODELS.get(uri));
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(OntologyReader.class);
}
