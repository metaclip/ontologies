![](http://metaclip.predictia.es/logo.png)

# METACLIP Project overview

The objective of _METAdata for CLImate Products_ (METACLIP) is to encode the metadata required to ensure the traceability and reproducibility of any kind climate product (data files, plots, maps...), thus requiring a comprehensive framework to track the operations undertaken through often complex data workflows. According to the terminology used in the project, the main elements in a climate data workflow are:

 1. _Data sources_ . The provenance of the input data need to be clearly identified (source, version, model documentation etc.). These applies to any type of data source (observations, operative/retrospective forecasts, reanalysis, climate projections...). Links to [[doc](http://metaclip.predictia.es/datasource/doc/)][[OWL file](http://metaclip.predictia.es/datasource/datasource.owl)][[Visual schema](https://docs.google.com/presentation/d/1CQyxVIj501N7VylMR9i_T_XwFDId6MDNvpPnuaLXgnI/present#slide=id.p)]
 2. _Transformations_. Any operations transforming the original data source that do not entail a second dataset (may entail a different _subset_ of the same dataset though; e.g. temporal/spatial aggregation, ensemble means, spatial interpolation/regridding, calculation of climate indices and anomalies ...). Links to [[doc](http://metaclip.predictia.es/datasource/doc/)][[OWL file](http://metaclip.predictia.es/datasource/datasource.owl)][[Visual schema](https://docs.google.com/presentation/d/1CQyxVIj501N7VylMR9i_T_XwFDId6MDNvpPnuaLXgnI/present#slide=id.p)]
 3. _Calibration_ (bias correction, downscaling, other forms of statistical adjustment). Links to [[doc](http://metaclip.predictia.es/calibration/doc/)][[OWL file](http://metaclip.predictia.es/calibration/calibration.owl)][[Visual Schema](https://docs.google.com/presentation/d/1Nai3mbwMlhsedS7-HWu-wFyua4-RUHmAieTtgy_DBMs/present#slide=id.p)]
 5. _Validation/Verification_ (bias, RMSE, AUC ...) Links to [[doc](http://metaclip.predictia.es/verification/doc/)][[OWL file](http://metaclip.predictia.es/verification/verification.owl)][[Visual schema](https://docs.google.com/presentation/d/1-H-d5X2kdA6-0nqnCd5yLs_V1DeTzuTGw2g3bLhtj7I/present#slide=id.p)]
 6. _Outcomes_ (map, QQ-plot, time series, plumes...) [In construction]

The main blocks of the climate data workflow (represented in the figure below) are represented by different classes and properties encoded in [RDF](https://www.w3.org/RDF/) ontologies stored in the different directories of this repo.

***

<!--
<img src="http://meteo.unican.es/work/QA4Seas/workflow_schema6.png" alt="" width="950" height="550">
-->
**Example Figure**. *Schematic representation of a data workflow to generate a verification map (Area under the ROC Curve, based on tercile categories) of a seasonal forecasting system (ECMWF System-4) of mean JJA global temperature. The verifying reference is the ECMWF ERA-Interim reanalysis. All the necessary metadata for the reconstruction of the figure is encoded in RDF (Resource Description Framework) and embedded in the final outcome (in this case a jpeg file, but any other type may serve as well). See [this demo](http://demo.predictia.es/qa4seas/metadata/) for a graphical representation of the metadata schema associated with this figure.*  
![](http://metaclip.predictia.es/workflow_schema3.png)

***


## Key strengths

The main advantages of The Climate Ontology approach are next summarised:
 
 * Flexible and powerful, allowing easy encoding of any conceptual framework.
 * Adds _domain-specific semantics_ to the schema, based on expert knowledge and climate community support. Thus the metadata is granular and accessible to non-experts.
 * Extensible and reusable, importing from already existing standard vocabularies. For instance, the [sequence](http://www.sequenceontology.org/) ontology is used to provide a secuencial aspect to the nodes in the graph describing the data workflow. The [PROV](https://www.w3.org/TR/prov-o/) ontology is used to characterize Climate Data provenance, etc.
 * Allows for a user-friendly _visual representation_ with different levels of granularity depending on user needs/expertise ([see a demo for the above figure](http://demo.predictia.es/qa4seas/metadata/)).
 * Straightforward serialization to many other formats convenient for other applications (Turtle, XML, JSON, HTML ...).

## Linked International Projects and Initiatives

The Climate Ontology Project is aligned with currently on-going initiatives facing the problems of data provenance and metadata encoding of climate products:

 * The [COST Action VALUE](http://www.value-cost.eu/), providing a European Network for a comprehensive validation and development of statistical downscaling methods.
 * The [QA4Seas Project](https://earth.bsc.es/qa4seas/wiki/doku.php) (Quality Assurance for Multi-model Seasonal Forecast Products), aimed at developing a strategy for the evaluation and quality control (EQC) of the multi-model seasonal forecasts provided by the Copernicus Climate Change Service [(C3S)](https://climate.copernicus.eu/about-c3s) to respond to the needs identified among a wide range of stakeholders.
<!--
 * The [EURO-CORDEX](http://www.euro-cordex.net/) Coordinated Downscaling Experiment 

### Comments, suggestions?
We will be happy to hear them, please drop a ticket!
-->
