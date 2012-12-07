package us.kbase.networks.adaptor.modelseed;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;

import us.kbase.CDMI.CDMI_API;
import us.kbase.CDMI.CDMI_EntityAPI;
import us.kbase.CDMI.fields_Model;
import us.kbase.CDMI.fields_Subsystem;
import us.kbase.CDMI.tuple_118;
import us.kbase.CDMI.tuple_131;
import us.kbase.CDMI.tuple_132;
import us.kbase.CDMI.tuple_136;
import us.kbase.CDMI.tuple_51;
import us.kbase.CDMI.tuple_83;
import us.kbase.networks.adaptor.Adaptor;
import us.kbase.networks.adaptor.AdaptorException;
import us.kbase.networks.adaptor.regprecise.RegPreciseAdaptor;
import us.kbase.networks.core.Dataset;
import us.kbase.networks.core.DatasetSource;
import us.kbase.networks.core.Edge;
import us.kbase.networks.core.EdgeType;
import us.kbase.networks.core.Entity;
import us.kbase.networks.core.EntityType;
import us.kbase.networks.core.Network;
import us.kbase.networks.core.NetworkType;
import us.kbase.networks.core.Node;
import us.kbase.networks.core.Taxon;

public class ModelSEEDAdaptor implements Adaptor {

	private CDMI_EntityAPI cdmi;
	private int uniqueIndex = 0;


	public ModelSEEDAdaptor() throws AdaptorException {
		super();
		try {
			this.cdmi = new CDMI_EntityAPI("http://bio-data-1.mcs.anl.gov/services/cdmi_api");
		} catch (MalformedURLException e) {
			throw new AdaptorException("Unable to initialize CDMI_EntityAPI", e);
		}
	}

	@Override
	public List<Dataset> getDatasets() throws AdaptorException {
		List<Dataset> datasets = new ArrayList<Dataset>();

		try {
			Map<String, fields_Model> models = cdmi.all_entities_Model(0, Integer.MAX_VALUE, Arrays.asList("id", "name"));

			for (String id : models.keySet()) {
				String name = models.get(id).name;
				List<tuple_132> genomeIds = cdmi.get_relationship_Models(Arrays.asList(id), new ArrayList<String>(), 
						new ArrayList<String>(), Arrays.asList("id"));
				List<Taxon> taxons = new ArrayList<Taxon>();
				for (tuple_132 tuple : genomeIds) {
					taxons.add(new Taxon(tuple.e_3.id));
				}
				datasets.add(new Dataset(getDatasetId(id), name, "Subsystems for " + name + " genome.", NetworkType.METABOLIC_SUBSYSTEM, 
						DatasetSource.MODELSEED, taxons));
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
			throw new AdaptorException("Error while getting models", e);
		}

		return datasets;
	}
	
	public List<Dataset> getDatasets(Entity entity) throws AdaptorException {
		List<Dataset> datasets = new ArrayList<Dataset>();
		
		if (entity.getType() == EntityType.GENE) {
			List<tuple_136> genomeIds;
			try {
				genomeIds = cdmi.get_relationship_IsOwnedBy(Arrays.asList(entity.getId()), new ArrayList<String>(), 
						new ArrayList<String>(), Arrays.asList("id"));
			} catch (Exception e) {
				throw new AdaptorException("Error while accessing CDMI", e);
			}
			if (genomeIds.size() == 1) {
				Taxon et = new Taxon(genomeIds.get(0).e_3.id);
				datasets.addAll(getDatasets(et));
			}
		}
		
		return datasets;
	}

	@Override
	public List<Dataset> getDatasets(NetworkType networkType) throws AdaptorException {
		List<Dataset> datasets = new ArrayList<Dataset>();
		if(networkType == NetworkType.METABOLIC_SUBSYSTEM)
		{
			datasets.addAll(getDatasets());
		}		
		return datasets;
	}

	@Override
	public List<Dataset> getDatasets(DatasetSource datasetSource) throws AdaptorException {
		List<Dataset> datasets = new ArrayList<Dataset>();
		if(datasetSource == DatasetSource.MODELSEED)
		{
			datasets.addAll(getDatasets());
		}		
		return datasets;
	}

	@Override
	public List<Dataset> getDatasets(Taxon taxon) throws AdaptorException {
		List<Dataset> datasets = new ArrayList<Dataset>();

		List<tuple_131> modelId;
		try {
			modelId = cdmi.get_relationship_IsModeledBy(Arrays.asList(taxon.getGenomeId()), new ArrayList<String>(), 
					new ArrayList<String>(), Arrays.asList("id", "name"));
		} catch (Exception e) {
			throw new AdaptorException("Error while accessing CDMI", e);
		}
		if (modelId.size() == 1) {
			String name = modelId.get(0).e_3.name;
			datasets.add(new Dataset(getDatasetId(taxon.getGenomeId()), name, "Subsystems for " + name + " genome.", NetworkType.METABOLIC_SUBSYSTEM, 
					DatasetSource.MODELSEED, Arrays.asList(taxon)));
		}

		return datasets;
	}

	@Override
	public List<Dataset> getDatasets(NetworkType networkType,
			DatasetSource datasetSource, Taxon taxon) throws AdaptorException {
		List<Dataset> datasets = new ArrayList<Dataset>();

		if(networkType == NetworkType.METABOLIC_SUBSYSTEM)		
			if(datasetSource == DatasetSource.MODELSEED)
			{
				datasets.addAll(getDatasets(taxon));
			}		
		return datasets;	
	}

	@Override
	public boolean hasDataset(Dataset dataset) throws AdaptorException {
		return dataset.getDatasetSource() == DatasetSource.MODELSEED;
	}

	@Override
	public Network buildNetwork(Dataset dataset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Network buildNetwork(Dataset dataset, List<EdgeType> edgeTypes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Network buildFirstNeighborNetwork(Dataset dataset, String geneId) throws AdaptorException {
		return buildFirstNeighborNetwork(dataset, geneId, Arrays.asList(EdgeType.GENE_CLUSTER));
	}

	@Override
	public Network buildFirstNeighborNetwork(Dataset dataset, String geneId, List<EdgeType> edgeTypes) throws AdaptorException {
		Graph<Node, Edge> graph = new SparseMultigraph<Node, Edge>();
		Network network = new Network(getNetworkId(), "", graph);	
		Node geneNode = Node.buildGeneNode(getNodeId(), geneId, new Entity(geneId, EntityType.GENE));
		graph.addVertex(geneNode);
		if( !edgeTypes.contains(EdgeType.GENE_CLUSTER) )
		{
			return network;
		}

		for (fields_Subsystem fs : getSubsystemFieldsForGene(geneId)) {
			Node ssNode = Node.buildClusterNode(getNodeId(), fs.id, new Entity(fs.id, EntityType.SUBSYSTEM));
			graph.addVertex(ssNode);
			Edge edge = new Edge(getEdgeId(), "Member of subsystem", dataset);
			graph.addEdge(edge, ssNode, geneNode, edu.uci.ics.jung.graph.util.EdgeType.DIRECTED);
		}

		return network;
	}

	private Collection<fields_Subsystem> getSubsystemFieldsForGene(String geneId) throws AdaptorException {
		Map<String, fields_Subsystem> fs = new HashMap<String, fields_Subsystem>();
		try {
			List<tuple_118> tps = cdmi.get_relationship_HasFunctional(Arrays.asList(geneId), new ArrayList<String>(),
					new ArrayList<String>(), Arrays.asList("id"));
			for (tuple_118 tp : tps) {
				List<tuple_83> tps2 = cdmi.get_relationship_IsIncludedIn(Arrays.asList(tp.e_3.id), 
						new ArrayList<String>(), new ArrayList<String>(), Arrays.asList("id")); 
				for (tuple_83 tp2 : tps2) {
					fs.put(tp2.e_3.id, tp2.e_3);
				}
			}
		} catch (Exception e) {
			throw new AdaptorException("Error getting subsystem fields for " + geneId, e);
		}
		return fs.values();
	}

	@Override
	public Network buildInternalNetwork(Dataset dataset, List<String> geneIds) throws AdaptorException {
		return buildInternalNetwork(dataset, geneIds, Arrays.asList(EdgeType.GENE_CLUSTER));
	}

	@Override
	public Network buildInternalNetwork(Dataset dataset, List<String> geneIds,
			List<EdgeType> edgeTypes) throws AdaptorException {
		Graph<Node, Edge> graph = new SparseMultigraph<Node, Edge>();
		Network network = new Network(getNetworkId(), "", graph);	
		if( !edgeTypes.contains(EdgeType.GENE_CLUSTER) )
		{
			return network;
		}

		// find out what subsystems genes are in
		Map<String,List<Node>> ssToGeneNodes = new HashMap<String,List<Node>>();

		for (String geneId : geneIds) {
			Node geneNode = Node.buildGeneNode(getNodeId(), geneId, new Entity(geneId, EntityType.GENE));
			graph.addVertex(geneNode);

			for (fields_Subsystem fs : getSubsystemFieldsForGene(geneId)) {
				List<Node> geneNodes = ssToGeneNodes.get(fs.id);
				if (geneNodes == null) {
					geneNodes = new ArrayList<Node>();
					ssToGeneNodes.put(fs.id,geneNodes);
				}
				geneNodes.add(geneNode);
			}
		}

		// build edges between genes in same subsystems; but don't build duplicate edges
		Map<Node, Set<Node>> genePairs = new HashMap<Node, Set<Node>>();
		
		for (String ss : ssToGeneNodes.keySet()) {
			for (Node node1 : ssToGeneNodes.get(ss)) {
				if (! genePairs.containsKey(node1)) {genePairs.put(node1, new HashSet<Node>());}
				for (Node node2 : ssToGeneNodes.get(ss)) {
					if (! genePairs.containsKey(node2)) {genePairs.put(node2, new HashSet<Node>());}
					if (node1 == node2) {continue;}
					if (genePairs.get(node1).contains(node2) || genePairs.get(node2).contains(node1)) {continue;}
					Edge edge = new Edge(getEdgeId(), "Member of same subsystem", dataset);
					graph.addEdge(edge, node1, node2, edu.uci.ics.jung.graph.util.EdgeType.UNDIRECTED);
					genePairs.get(node1).add(node2);
				}
			}
		}
		return network;
	}

	private String getDatasetId(String id) {
		return RegPreciseAdaptor.DATASET_ID_PREFIX + id;
	}		

	private String getNodeId() {
		return RegPreciseAdaptor.NODE_ID_PREFIX + (uniqueIndex ++);
	}

	private String getEdgeId() {
		return RegPreciseAdaptor.EDGE_ID_PREFIX + (uniqueIndex++);
	}

	private String getNetworkId() {
		return RegPreciseAdaptor.NETWORK_ID_PREFIX + (uniqueIndex++);
	}	
}
