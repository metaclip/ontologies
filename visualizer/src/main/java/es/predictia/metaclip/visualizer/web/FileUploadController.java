package es.predictia.metaclip.visualizer.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.GraphUtil;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Node_URI;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import es.predictia.metaclip.visualizer.model.MetaclipGraph;
import es.predictia.metaclip.visualizer.model.MetaclipNode;
import es.predictia.metaclip.visualizer.utils.MetaclipExtractor;
import es.predictia.metaclip.visualizer.utils.OntologyReader; 


@Controller
@CrossOrigin
public class FileUploadController{

	@RequestMapping("/image")
	@ResponseBody
	public ResponseEntity<?> upload(@RequestParam(name="file",required=false) MultipartFile input){
		// File file = new File("/var/www/html/qa4seas/new/ex.png");
		try{
			Optional<String> metaclip = MetaclipExtractor.extract(input.getInputStream());
			//Optional<String> metaclip = MetaclipExtractor.extract(file);
			if(!metaclip.isPresent()){
				return new ResponseEntity<String>("{'error':'No metaclip info found'}",HttpStatus.BAD_REQUEST);
			}
			
			Dataset dataset = DatasetFactory.create();
			RDFDataMgr.read(dataset,IOUtils.toInputStream(metaclip.get(),"UTF-8"),RDFLanguages.JSONLD);
			// dataset = RDFDataMgr.loadDataset("http://localhost/qa4seas/new/ex.json",RDFLanguages.JSONLD);
			Graph graph = dataset.asDatasetGraph().getDefaultGraph();
			
			MetaclipGraph metaclipGraph = new MetaclipGraph();
			
			List<Node> subjects = GraphUtil.listSubjects(graph, Node.ANY, Node.ANY).toList();
			for(Node node : subjects){
				if(!(node instanceof Node_URI)){
					// subjects are always Node_URI
					LOGGER.warn("Non URI nodes are not supported: "+node.toString());
					continue;
				}
				Node_URI subjectNode = (Node_URI)node;
				Optional<MetaclipNode> metaclipNodeOpt = OntologyReader.find(metaclipGraph,graph,subjectNode);
				if(metaclipNodeOpt.isPresent()){
					LOGGER.warn("Found a duplicate subject node [ignoring]: "+subjectNode.getURI());
					continue;
				}				
				MetaclipNode metaclipNode = OntologyReader.create(graph,subjectNode);
				OntologyReader.properties(metaclipNode, metaclipGraph, subjectNode, graph);
				metaclipGraph.addNode(metaclipNode);
			}
			
			// add annotations
			OntologyReader.annotations(metaclipGraph);
			OntologyReader.prefixes(metaclipGraph);
			
			return new ResponseEntity<MetaclipGraph>(metaclipGraph,HttpStatus.OK);
		}catch(Exception e){
			LOGGER.warn("Problem extracting metaclip info: "+e.getMessage());
			return new ResponseEntity<String>("{'error':'No valid metaclip info found: "+e.getMessage()+"'}",HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping("/individuals")
	@ResponseBody
	public ResponseEntity<Collection<String>> indivuduals(@RequestParam(name="vocab") String vocab,@RequestParam(name="class") String className){
		if(!vocab.endsWith("#")) vocab = vocab+"#";
		Optional<OntModel> model = OntologyReader.model(vocab);
		Collection<String> result = new ArrayList<String>();
		if(model.isPresent()){
			OntClass clazz = model.get().getOntClass(vocab+className);
			if(clazz != null){
				ExtendedIterator<Individual> iterator = model.get().listIndividuals(clazz);
				while(iterator.hasNext()){
					Individual individual = iterator.next();
					result.add(individual.getLocalName());
				}
			}
		}
		return new ResponseEntity<Collection<String>>(result,HttpStatus.OK);
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadController.class);

}
