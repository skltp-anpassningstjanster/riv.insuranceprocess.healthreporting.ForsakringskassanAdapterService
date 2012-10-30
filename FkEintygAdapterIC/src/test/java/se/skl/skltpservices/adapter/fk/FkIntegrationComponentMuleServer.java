package se.skl.skltpservices.adapter.fk;

import org.soitoolkit.commons.mule.test.StandaloneMuleServer;


public class FkIntegrationComponentMuleServer {


	public static final String MULE_SERVER_ID   = "FkIntegrationComponent";
 
	public static final String MULE_CONFIG      = "FkIntegrationComponent-config.xml,FkIntegrationComponent-teststubs-and-services-config.xml";

	public static void main(String[] args) throws Exception {
		
		StandaloneMuleServer muleServer = new StandaloneMuleServer(MULE_SERVER_ID, MULE_CONFIG);
 
		muleServer.run();
	}

}