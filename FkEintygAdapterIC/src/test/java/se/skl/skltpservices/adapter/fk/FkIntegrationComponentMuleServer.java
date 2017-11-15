/**
 * Copyright (c) 2014 Inera AB, <http://inera.se/>
 *
 * This file is part of SKLTP.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package se.skl.skltpservices.adapter.fk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soitoolkit.commons.mule.test.StandaloneMuleServer;
import org.soitoolkit.commons.mule.util.RecursiveResourceBundle;

public class FkIntegrationComponentMuleServer {

	private static final Logger logger = LoggerFactory.getLogger(FkIntegrationComponentMuleServer.class);

	private static final RecursiveResourceBundle rb = new RecursiveResourceBundle("FkIntegrationComponent-config");

	public static final String MULE_SERVER_ID = "FkIntegrationComponent";
	
	public static final String X_VP_SENDER_ID = "x-vp-sender-id";
	public static final String X_VP_INSTANCE_ID = "x-vp-instance-id";

	public static void main(String[] args) throws Exception {

		
		// Configure the mule-server:
        //
        // Arg #1: The name of the Mule Server
        //
        // Arg #2: Start teststub-services if true
        //         Note: Actually enables the spring-beans-profile "soitoolkit-teststubs" in the file "src/main/app/FkIntegrationComponent-common.xml"
        //
        // Arg #3: Start services if true 
        //         Note: Actually loads all *-service.xml files that are specified in the file "src/main/app/mule-deploy.properties"
        //
		
		StandaloneMuleServer muleServer = new StandaloneMuleServer(MULE_SERVER_ID, true, true);

		// Start the server
		muleServer.run();
	}

	/**
	 * Address based on usage of the servlet-transport and a config-property for
	 * the URI-part
	 * 
	 * @param serviceUrlPropertyName
	 * @return
	 */
	public static String getAddress(String serviceUrlPropertyName) {

		String url = rb.getString(serviceUrlPropertyName);

		logger.info("URL: {}", url);
		return url;

	}

}