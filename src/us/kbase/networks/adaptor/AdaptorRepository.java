package us.kbase.networks.adaptor;

import java.util.List;
import java.util.Vector;

import us.kbase.networks.adaptor.jdbc.GenericAdaptorFactory;
import us.kbase.networks.adaptor.modelseed.ModelSEEDAdaptorFactory;

public class AdaptorRepository {
	
	
	private static AdaptorRepository repository;
	private List<Adaptor> adaptors = new Vector<Adaptor>();
	
	private AdaptorRepository() throws AdaptorException
	{
		// Register all adaptors; property file in the future
		
		// microbial adaptors
/*		
		registerAdaptor(new ModelSEEDAdaptorFactory());
		registerAdaptor(new GenericAdaptorFactory("regprecise.config"));
		registerAdaptor(new GenericAdaptorFactory("cmonkey.config"));
		registerAdaptor(new GenericAdaptorFactory("mak.config"));
		registerAdaptor(new GenericAdaptorFactory("ppi.config"));
*/		
		// plant adaptors
		registerAdaptor(new GenericAdaptorFactory("plant-cc.config"));		
		registerAdaptor(new GenericAdaptorFactory("plant-cn.config"));
		registerAdaptor(new GenericAdaptorFactory("plant-fn.config"));
		registerAdaptor(new GenericAdaptorFactory("plant-gp.config"));
		registerAdaptor(new GenericAdaptorFactory("plant-ppip.config"));
		registerAdaptor(new GenericAdaptorFactory("plant-rn.config"));
		registerAdaptor(new GenericAdaptorFactory("plant-ppi-ga.config"));
		// communities adaptors
/*		
		registerAdaptor(new GenericAdaptorFactory("community-cc.config"));
		registerAdaptor(new GenericAdaptorFactory("community-cn.config"));
*/		
		
	}
	
	public static AdaptorRepository getAdaptorRepository() throws AdaptorException
	{
		if(repository == null)
		{
			repository = new AdaptorRepository();
		}
		return repository;
	}
	
	private void registerAdaptor(AdaptorFactory factory) throws AdaptorException
	{
		adaptors.add(factory.buildAdaptor());
	}
	
	public List<Adaptor> getDataAdaptors()
	{
		return adaptors;
	}
}
