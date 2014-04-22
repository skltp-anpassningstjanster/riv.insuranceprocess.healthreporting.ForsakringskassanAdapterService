/**
 * Copyright (c) 2013 Center for eHalsa i samverkan (CeHis).
 * 							<http://cehis.se/>
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
package se.skl.skltpservices.adapter.common.processor;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.config.i18n.Message;
import org.mule.config.i18n.MessageFactory;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CheckSenderIdTransformer responsible to extract senderIpAdress and verify against whitelist.
 * 
 */
public class CheckSenderIpTransformer extends AbstractMessageTransformer{
	
	private static final Logger log = LoggerFactory.getLogger(CheckSenderIpTransformer.class);

	private String whiteList;
	
	private String senderIpAdressHttpHeader;
	
	private boolean enableIpAdressControl;

	public void setWhiteList(final String whiteList) {
		this.whiteList = whiteList;
	}

	public void setSenderIpAdressHttpHeader(String senderIpAdressHttpHeader) {
		this.senderIpAdressHttpHeader = senderIpAdressHttpHeader;
	}
	
	public void setEnableIpAdressControl(boolean enableIpAdressControl) {
		this.enableIpAdressControl = enableIpAdressControl;
	}

    /**
     * Message aware transformer that extracts senderIp
     */
    @Override
    public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {
    	
    	if(enableIpAdressControl){
    		/*
    		 * Extract sender ip adress to session scope to be able to log in EventLogger.
    		 */
    		String senderIpAdress = extractSenderIpAdress(message);
    		
    		log.debug("Extracted sender ip adress {}, check if its valid against whitelist", senderIpAdress);

    		if(!FkAdapterUtil.isCallerOnWhiteList(senderIpAdress, whiteList)){
    			log.error("Not a valid ip adress! Sender ip adress {} was not found in whitelist", senderIpAdress);
    			throw transformerException("FKADAPT002 Caller was not on the white list of accepted IP-addresses. IP-address: " + senderIpAdress);
    		}
    	}    
        return message;
    }

    /*
     * Extract sender ip adress from configured FkAdapterUtil.SENDER_IP_ADRESS_HTTP_HEADER in
     * FkIntegrationComponent-config.properties. In case no ip adress is provided fall back to let Mule extract ip
     * adress.
     */
	private String extractSenderIpAdress(MuleMessage message) {
		String senderIpAdress = (String)message.getInboundProperty(senderIpAdressHttpHeader);
		if(senderIpAdress == null){
			senderIpAdress = FkAdapterUtil.extractIpAddress(message);
		}
		return senderIpAdress;
	}
	
	private TransformerException transformerException(String msg) {
		Message errorMsg = MessageFactory.createStaticMessage(msg);
		return new TransformerException(errorMsg);
	}
}
