package es.predictia.metaclip.visualizer.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NodeConfig {

	private Boolean visible;
	private Boolean collpased;
	private Boolean sticky;
	private Double weight;
	
}
