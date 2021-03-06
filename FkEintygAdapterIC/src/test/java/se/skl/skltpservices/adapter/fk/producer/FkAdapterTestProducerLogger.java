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
package se.skl.skltpservices.adapter.fk.producer;

import static org.junit.Assert.assertNotNull;
import static se.skl.skltpservices.adapter.fk.FkIntegrationComponentMuleServer.X_VP_INSTANCE_ID;
import static se.skl.skltpservices.adapter.fk.FkIntegrationComponentMuleServer.X_VP_SENDER_ID;

import java.util.Map;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FkAdapterTestProducerLogger extends AbstractMessageTransformer {

	private static final Logger log = LoggerFactory.getLogger(FkAdapterTestProducerLogger.class);
	
	private static String latestSenderId = null;
	private static String latestVpInstanceId = null;

	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {
		
		@SuppressWarnings("unchecked")
		Map<String, Object> httpHeaders = (Map<String, Object>)message.getInboundProperty("http.headers");
		
		//Sender
		String vpSenderId = (String)httpHeaders.get(X_VP_SENDER_ID);
		String vpInstanceId = (String)httpHeaders.get(X_VP_INSTANCE_ID);
		assertNotNull(vpInstanceId);
		assertNotNull(vpSenderId);
		
		log.info("Test producer called with {}: {}", X_VP_SENDER_ID, vpSenderId);
		latestSenderId = vpSenderId;
		
		log.info("Test producer called with {}: {}", X_VP_INSTANCE_ID, vpInstanceId);
		latestVpInstanceId = vpInstanceId;
		
		return message;
	}
	
	public static String getLatestSenderId() {
		return latestSenderId;
	}

	public static String getLatestVpInstanceId() {
		return latestVpInstanceId;
	}
	
	public static void resetTestProducerLoggerStaticVariables(){
		latestSenderId = null;
		latestVpInstanceId = null;
	}
}
