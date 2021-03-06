package us.kbase.kbasenetworks.integrated;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import us.kbase.kbasenetworks.NetworksAPI;
import us.kbase.kbasenetworks.NetworksUtil;
import us.kbase.kbasenetworks.adaptor.Adaptor;
import us.kbase.kbasenetworks.adaptor.AdaptorException;
import us.kbase.kbasenetworks.adaptor.jdbc.GenericAdaptorFactory;
import us.kbase.kbasenetworks.core.Dataset;
import us.kbase.kbasenetworks.core.Edge;
import us.kbase.kbasenetworks.core.Entity;
import us.kbase.kbasenetworks.core.EntityType;
import us.kbase.kbasenetworks.core.Network;
import us.kbase.kbasenetworks.core.Node;
import us.kbase.kbasenetworks.core.NodeType;
import us.kbase.kbasenetworks.core.Taxon;

public class IntegratedNetworksAPITest {

    NetworksAPI api;

    String genomeId1 = "kb|g.21765";
    String genes1 = "kb|g.21765.CDS.543";
    List<String> edgeTypes1 = Arrays.asList(NodeType.EDGE_GENE_CLUSTER);


    String genomeId2 = "kb|g.20848";
    List genes2 = Arrays.asList("kb|g.20848.CDS.1454", "kb|g.20848.CDS.868", "kb|g.20848.CDS.1671", "kb|g.20848.CDS.2554", "kb|g.20848.CDS.1031",
            "kb|g.20848.regulon.33", "kb|g.20848.regulon.2", "kb|g.20848.regulon.54", "kb|g.20848.regulon.29", "kb|g.20848.regulon.48", "kb|g.20848.regulon.169", "kb|g.20848.regulon.171"
            , "kb|bicluster.110");
    List edgeTypes2 = Arrays.asList(NodeType.EDGE_GENE_CLUSTER);


    String genomeId3 = "kb|g.20848";
    String geneIds3 = "kb|g.20848.CDS.1671";
    List edgeTypes3 = Arrays.asList(NodeType.EDGE_GENE_GENE);


    List<String> datasetsIntegrate = Arrays.asList(
            "kb|netdataset.regprecise.301",
            "kb|netdataset.modelseed.5");
    List<String> genesIntegrate = Arrays.asList(
            "kb|g.21765.CDS.3832",
            "kb|g.21765.CDS.1709",
            "kb|g.21765.CDS.71");

    @Before
    public void run() throws AdaptorException {
        api = NetworksAPI.getNetworksAPI();
    }


    @Test
    public void test_buildIntegratedNetwork() throws AdaptorException {

        /*Adaptor adaptor = new GenericAdaptorFactory("regprecise.config").buildAdaptor();
        Dataset regp = adaptor.getDataset("kb|netdataset.regprecise.301");

        adaptor = new ModelSEEDAdaptorFactory().buildAdaptor();

        Dataset seed = adaptor.getDataset("kb|netdataset.modelseed.5");

        List<Dataset> datasets = new ArrayList<Dataset>();
        datasets.add(regp);
        datasets.add(seed);*/

        //NetworksUtil.printDatasets("", datasets);
        System.out.println("test_buildIntegratedNetwork");
        System.out.println(datasetsIntegrate.get(0) + "\t" + datasetsIntegrate.get(1));
        Network network = api.buildInternalNetwork(datasetsIntegrate, Entity.toEntities(genesIntegrate), edgeTypes2);//Dataset.toDatasetIds(datasets)

        NetworksUtil.printNetwork(network);
    }

    @Test
    public void test_buildInternalNetwork() throws AdaptorException {
        Taxon taxon = new Taxon(genomeId2);

        List<Dataset> datasets = api.getDatasets(taxon);
        NetworksUtil.printDatasets("", datasets);
        Network network = api.buildInternalNetwork(Dataset.toDatasetIds(datasets), Entity.toEntities(genes2), edgeTypes2);

        NetworksUtil.printNetwork(network);
        //NetworksUtil.visualizeNetwork(network.getGraph());
    }

    @Test
    public void test_buildFirstNeighborNetwork() throws AdaptorException {
        Taxon taxon = new Taxon(genomeId1);

        List<Dataset> datasets = api.getDatasets(taxon);

        // To avoid too many interactions...
        NetworksUtil.removeDataset(datasets, "kb|netdataset.ppi.1");
        NetworksUtil.removeDataset(datasets, "kb|netdataset.ppi.3");
        NetworksUtil.removeDataset(datasets, "kb|netdataset.ppi.5");
        NetworksUtil.removeDataset(datasets, "kb|netdataset.ppi.9");

        NetworksUtil.printDatasets(genomeId1, datasets);

        Network network =
                api.buildFirstNeighborNetwork(Dataset.toDatasetIds(datasets), new Entity(genes1, EntityType.GENE), edgeTypes1);

        NetworksUtil.printNetwork(network);
        //NetworksUtil.visualizeNetwork(network.getGraph());
    }

    @Test
    public void testAdaptor_buildFirstNeighborNetwork() throws AdaptorException {
        Adaptor adaptor = new GenericAdaptorFactory("regprecise.config").buildAdaptor();

        Taxon taxon = new Taxon(genomeId3);
        List<Dataset> datasets = adaptor.getDatasets(taxon);
        NetworksUtil.printDatasets(genomeId3, datasets);

        Network network =
                adaptor.buildFirstNeighborNetwork(datasets.get(0), new Entity(geneIds3, EntityType.GENE), edgeTypes3);

        NetworksUtil.printNetwork(network);
        //NetworksUtil.visualizeNetwork(network.getGraph());
        for (Edge edge : network.getGraph().getEdges()) {
            System.out.println("Edge: " + edge.getName());
            Node node = network.getGraph().getSource(edge);
            System.out.println("\tSource node: " + (node != null ? node.getName() : "null"));
            node = network.getGraph().getDest(edge);
            System.out.println("\tDestination node: " + (node != null ? node.getName() : "null"));
            for (Node node1 : network.getGraph().getIncidentVertices(edge)) {
                System.out.println("\tIncident node: " + (node1 != null ? node1.getName() : "null"));
            }
        }
    }

}
