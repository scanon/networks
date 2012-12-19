package us.kbase.networks.adaptor.regprecise;

import java.util.Arrays;
import java.util.List;

import us.kbase.networks.NetworksUtil;
import us.kbase.networks.adaptor.Adaptor;
import us.kbase.networks.adaptor.AdaptorException;
import us.kbase.networks.core.Dataset;
import us.kbase.networks.core.DatasetSource;
import us.kbase.networks.core.EdgeType;
import us.kbase.networks.core.Entity;
import us.kbase.networks.core.EntityType;
import us.kbase.networks.core.Network;
import us.kbase.networks.core.NetworkType;
import us.kbase.networks.core.Taxon;

public class RegPreciseAdaptorDemo {
	
	Adaptor adaptor;

	Entity gene =  Entity.toEntity("kb|g.20848.CDS.2811");
	List<Entity> genes = Entity.toEntities( 
		Arrays.asList(
			new String[]{					
				"kb|g.20848.CDS.1671", 
				"kb|g.20848.CDS.1454", 
				"kb|g.20848.CDS.2811"} ));
	
	Entity regulon = Entity.toEntity("kb|g.9677.regulon.0");
	
	Dataset goodDataset = new Dataset("kb|netdataset.kb|g.20848.regulome.0", "", "", null, null, (Taxon) null);
	Dataset badDataset  = new Dataset("kb|netdataset.kb|g.20848.regulome.1", "", "", null, null, (Taxon) null);
	
	Taxon goodTaxon = new Taxon("kb|g.19732");
	Taxon badTaxon  = new Taxon("kb|g.QQQQQ");
	
	
	private void run() throws AdaptorException{
		adaptor = new RegPreciseAdaptorFactory().buildAdaptor();
		
//		test_getDatasets1();
//		test_getDatasets2();
//		test_getDatasets3();
//		test_getDatasets4();

		
//		test_hasDataset();
		
//		test_buildFirstNeighborNetwork1();
//		test_buildFirstNeighborNetwork2();
//		test_buildFirstNeighborNetwork3();
		test_buildFirstNeighborNetwork4();
		
//		test_buildInternalNetwork();
	}

	private void test_buildInternalNetwork() throws AdaptorException {
		Network network = adaptor.buildInternalNetwork(goodDataset, genes, Arrays.asList(EdgeType.GENE_GENE));
		
		NetworksUtil.printNetwork(network);
		NetworksUtil.visualizeNetwork(network.getGraph());	
	}

	private void test_buildFirstNeighborNetwork4() throws AdaptorException {
		Network network = adaptor.buildFirstNeighborNetwork(goodDataset, regulon, Arrays.asList(EdgeType.GENE_GENE,EdgeType.GENE_CLUSTER));
		
		NetworksUtil.printNetwork(network);
		NetworksUtil.visualizeNetwork(network.getGraph());		
	}
	
	
	private void test_buildFirstNeighborNetwork3() throws AdaptorException {
		Network network = adaptor.buildFirstNeighborNetwork(goodDataset, gene, Arrays.asList(EdgeType.GENE_GENE,EdgeType.GENE_CLUSTER));
		
		NetworksUtil.printNetwork(network);
		NetworksUtil.visualizeNetwork(network.getGraph());		
	}

	private void test_buildFirstNeighborNetwork2() throws AdaptorException {
		Network network = adaptor.buildFirstNeighborNetwork(goodDataset, gene, Arrays.asList(EdgeType.GENE_GENE));
		
		NetworksUtil.printNetwork(network);
		NetworksUtil.visualizeNetwork(network.getGraph());
		
	}

	private void test_buildFirstNeighborNetwork1() throws AdaptorException {
		Network network = adaptor.buildFirstNeighborNetwork(goodDataset, gene, Arrays.asList(EdgeType.GENE_CLUSTER));
		
		NetworksUtil.printNetwork(network);
		NetworksUtil.visualizeNetwork(network.getGraph());		
	}


	private void test_hasDataset() throws AdaptorException {
				
		System.out.println("Has good dataset: " + adaptor.hasDataset(goodDataset.getId()));		
		System.out.println("Has bad  dataset: " + adaptor.hasDataset(badDataset.getId()));		
	}
	
	
	private void test_getDatasets4() throws AdaptorException {
		List<Dataset> datasets;
		
		datasets = adaptor.getDatasets(goodTaxon);		
		NetworksUtil.printDatasets("good taxon " + goodTaxon.getGenomeId(), datasets);
		
		datasets = adaptor.getDatasets(badTaxon);		
		NetworksUtil.printDatasets("bad taxon " + badTaxon.getGenomeId(), datasets);		
	}

	private void test_getDatasets3() throws AdaptorException {
		List<Dataset> datasets;
		
		datasets = adaptor.getDatasets(DatasetSource.REGPRECISE);		
		NetworksUtil.printDatasets(DatasetSource.REGPRECISE.getName(), datasets);
		
		datasets = adaptor.getDatasets(DatasetSource.AGRIS);		
		NetworksUtil.printDatasets(DatasetSource.AGRIS.getName(), datasets);
		
		datasets = adaptor.getDatasets(DatasetSource.CMONKEY);		
		NetworksUtil.printDatasets(DatasetSource.CMONKEY.getName(), datasets);
		
		//...
	}


	private void test_getDatasets2() throws AdaptorException {
		List<Dataset> datasets;
		
		datasets = adaptor.getDatasets(NetworkType.REGULATORY_NETWORK);		
		NetworksUtil.printDatasets(NetworkType.REGULATORY_NETWORK.getName(), datasets);
		
		datasets = adaptor.getDatasets(NetworkType.FUNCTIONAL_ASSOCIATION);		
		NetworksUtil.printDatasets(NetworkType.FUNCTIONAL_ASSOCIATION.getName(), datasets);
		
		datasets = adaptor.getDatasets(NetworkType.METABOLIC_SUBSYSTEM);		
		NetworksUtil.printDatasets(NetworkType.METABOLIC_SUBSYSTEM.getName(), datasets);
		
		datasets = adaptor.getDatasets(NetworkType.PROT_PROT_INTERACTION);		
		NetworksUtil.printDatasets(NetworkType.PROT_PROT_INTERACTION.getName(), datasets);
	}


	private void test_getDatasets1() throws AdaptorException {
		List<Dataset> datasets = adaptor.getDatasets();
		NetworksUtil.printDatasets("", datasets);
	}
	
	public static void main(String[] args) throws AdaptorException {
		new RegPreciseAdaptorDemo().run();
	}
}
