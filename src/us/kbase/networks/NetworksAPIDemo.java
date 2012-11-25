package us.kbase.networks;

import java.awt.print.Printable;
import java.util.Arrays;
import java.util.List;

import us.kbase.networks.adaptor.Adaptor;
import us.kbase.networks.adaptor.AdaptorException;
import us.kbase.networks.adaptor.regprecise.RegPreciseAdaptorFactory;
import us.kbase.networks.core.Dataset;
import us.kbase.networks.core.Edge;
import us.kbase.networks.core.EdgeType;
import us.kbase.networks.core.Network;
import us.kbase.networks.core.Node;
import us.kbase.networks.core.Taxon;

public class NetworksAPIDemo {
	
	NetworksAPI api;	
	
	public void run() throws AdaptorException
	{
		api = NetworksAPI.getNetworksAPI();
		
		// Shewnalla: two adapters work together (RegPrecise, MAK)
//		testAdaptor_buildFirstNeighborNetwork(new RegPreciseAdaptorFactory().buildAdaptor(), 
//				"kb|g.20848", "kb|g.20848.CDS.1671", Arrays.asList(EdgeType.GENE_GENE));
		
		// Ecoli: three adaptors work together (ModelSEED, PPI, RegPrecise)
//		test_buildFirstNeighborNetwork("kb|g.21765", "kb|g.21765.CDS.543", Arrays.asList(EdgeType.GENE_CLUSTER));
		
		// Shewnalla: two adapters work together (RegPrecise, MAK)
		//test_buildFirstNeighborNetwork("kb|g.20848", "kb|g.20848.CDS.1671", Arrays.asList(EdgeType.GENE_GENE));

		// Shewnalla:
/*		
		test_buildInternalNetwork("kb|g.21765",
				Arrays.asList("kb|g.21765.CDS.543", "kb|g.21765.CDS.544", "kb|g.21765.CDS.545"),
				Arrays.asList(EdgeType.GENE_GENE));
*/				
		
		test_buildInternalNetwork("kb|g.20848",
				Arrays.asList("kb|g.20848.CDS.1671", "kb|g.20848.CDS.1454", "kb|g.20848.CDS.2811"),
				Arrays.asList(EdgeType.GENE_GENE));
		
		
		
	}
	
	private void test_buildInternalNetwork(String genomeId, List<String> geneIds, List<EdgeType> edgeTypes) throws AdaptorException {
		Taxon taxon = new Taxon(genomeId);
		
		List<Dataset> datasets = api.getDatasets(taxon);
		NetworksUtil.printDatasets("", datasets);
		Network network = api.buildInternalNetwork(datasets, geneIds, Arrays.asList(EdgeType.GENE_GENE));
		
		NetworksUtil.printNetwork(network);
		NetworksUtil.visualizeNetwork(network.getGraph());	
	}
	
	private void test_buildFirstNeighborNetwork(String genomeId, String geneId, List<EdgeType> edgeTypes) throws AdaptorException
	{
		Taxon taxon = new Taxon(genomeId);
		
		List<Dataset> datasets = api.getDatasets(taxon);

		// To avoid to many interactions...
		NetworksUtil.removeDataset(datasets, "kb|netdataset.ppi.1");
		NetworksUtil.removeDataset(datasets, "kb|netdataset.ppi.3");
		NetworksUtil.removeDataset(datasets, "kb|netdataset.ppi.5");
		NetworksUtil.removeDataset(datasets, "kb|netdataset.ppi.9");
		
		NetworksUtil.printDatasets(genomeId, datasets);
		
		Network network = 
			api.buildFirstNeighborNetwork(datasets, geneId, edgeTypes);
		
		NetworksUtil.printNetwork(network);
		NetworksUtil.visualizeNetwork(network.getGraph());	
	}
	
	private void testAdaptor_buildFirstNeighborNetwork(Adaptor adaptor,String genomeId, String geneId, List<EdgeType> edgeTypes) throws AdaptorException
	{
		Taxon taxon = new Taxon(genomeId);		
		List<Dataset> datasets = adaptor.getDatasets(taxon);	
		NetworksUtil.printDatasets(genomeId, datasets);
		
		Network network = 
			adaptor.buildFirstNeighborNetwork(datasets.get(0), geneId, edgeTypes);
		
		NetworksUtil.printNetwork(network);
		NetworksUtil.visualizeNetwork(network.getGraph());	
		for(Edge edge: network.getGraph().getEdges())
		{
			System.out.println("Edge: " + edge.getName());
			Node node = network.getGraph().getSource(edge);
			System.out.println("\tSource node: " + (node!=null? node.getName() : "null"));
			node = network.getGraph().getDest(edge);
			System.out.println("\tDestination node: " + (node!=null? node.getName() : "null"));
			for(Node node1: network.getGraph().getIncidentVertices(edge))
			{
				System.out.println("\tIncident node: " + (node1!=null? node1.getName() : "null"));
			}
		}
	}
	

	public static void main(String[] args) throws AdaptorException {
		new NetworksAPIDemo().run();
	}
	
}