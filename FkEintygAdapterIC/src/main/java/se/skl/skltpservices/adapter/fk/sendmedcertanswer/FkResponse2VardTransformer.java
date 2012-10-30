package se.skl.skltpservices.adapter.fk.sendmedcertanswer;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.mule.api.ExceptionPayload;
import org.mule.api.MuleMessage;
import org.mule.api.routing.ResponseTimeoutException;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageAwareTransformer;
import org.mule.transport.NullPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.fk.vardgivare.sjukvard.taemotsvarresponder.v1.TaEmotSvarResponseType;
import se.skl.riv.insuranceprocess.healthreporting.sendmedicalcertificateanswerresponder.v1.SendMedicalCertificateAnswerResponseType;
import se.skl.riv.insuranceprocess.healthreporting.v2.ErrorIdEnum;
import se.skl.riv.insuranceprocess.healthreporting.v2.ResultCodeEnum;
import se.skl.riv.insuranceprocess.healthreporting.v2.ResultOfCall;

public class FkResponse2VardTransformer extends AbstractMessageAwareTransformer
{
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public FkResponse2VardTransformer()
    {
        super();
        registerSourceType(Object.class);
        setReturnClass(Object.class);
    }
    
	public Object transform(MuleMessage message, String outputEncoding) throws TransformerException {
		
		logger.info("Entering fk2vard send medical certificate answer transform");

		boolean faultDetected = false;
		boolean validationErrorDetected = false;
		
    	Object src = message.getPayload();
		try {						
			// Take care of any error message and send it back as a SOAP Fault!
			if (src instanceof NullPayload) {
			    // We got a null-payload, let's see if there is an exception-payload instead...
		        ExceptionPayload ep = message.getExceptionPayload();
		        if (ep != null) {
		            String errorMessage = ep.getCode() + ": " + ep.getMessage();

		            Throwable t = ep.getException();
		            if (t instanceof ResponseTimeoutException) {
		            	src = errorMessage + ". Timeout.";
		            } else {
		            	// Check if we have a validation error!
		            	if (errorMessage.contains("Validation error")) {
		            		validationErrorDetected = true;
			                src = ep.getMessage();
		            	} else {
			                src = errorMessage + ". Unknown error.";
		            	}
		            }
		            
		            // Remove the ExceptionPayload!
		            message.setExceptionPayload(null);
		        }				
	            faultDetected = true;
			} else if(!(src instanceof TaEmotSvarResponseType)) {
				src = "Payload type not supported: "+ message.getPayload().getClass();
				faultDetected = true;
			}

			StringBuffer result = new StringBuffer();
			
			// First create the content in the body, either a fault or the response
			if (faultDetected && !validationErrorDetected) {
				// Strip off xml processing instructions if any
				String payload = (String)src;
				if (payload.startsWith("<?")) {
					int pos = payload.indexOf("?>");
					payload = payload.substring(pos + 2);
				}

				createSoapFault(payload, result);
			} else {
	            // Create new JAXB object for the outgoing data
				SendMedicalCertificateAnswerResponseType outResponse = new SendMedicalCertificateAnswerResponseType(); 
	            ResultOfCall resultOfCall = new ResultOfCall();
            	outResponse.setResult(resultOfCall);					

	            if (validationErrorDetected) {
	            	resultOfCall.setResultCode(ResultCodeEnum.ERROR);
	            	resultOfCall.setErrorId(ErrorIdEnum.VALIDATION_ERROR);
	            	resultOfCall.setErrorText((String)src);
				} else {
					TaEmotSvarResponseType inResponse = (TaEmotSvarResponseType)src;
		            if (inResponse != null) {
		            	resultOfCall.setResultCode(ResultCodeEnum.OK);
		            } else {
		            	resultOfCall.setResultCode(ResultCodeEnum.ERROR);
		            	resultOfCall.setErrorId(ErrorIdEnum.TECHNICAL_ERROR);
		            }
				}
	            	            
				// Transform the JAXB object into a XML payload
	            StringWriter writer = new StringWriter();
	        	Marshaller marshaller = JAXBContext.newInstance(SendMedicalCertificateAnswerResponseType.class).createMarshaller();
	        	marshaller.marshal(new JAXBElement(new QName("urn:riv:insuranceprocess:healthreporting:SendMedicalCertificateAnswerResponder:1", "SendMedicalCertificateAnswerResponse"), SendMedicalCertificateAnswerResponseType.class, outResponse), writer);
				logger.debug("Extracted information: {}", writer.toString());
				String payload = (String)writer.toString();
				if (payload.startsWith("<?")) {
					int pos = payload.indexOf("?>");
					payload = payload.substring(pos + 2);
				}
				
				writer.close();
				result.append(payload);
			}

			// Done, return the string
			String resultStr = result.toString();
			logger.debug("Return SOAP Body: {}", resultStr);
			
			logger.info("Exiting fk2vard send medical certificate answer transform");
			return resultStr;

		} catch (Exception e) {
	        throw new TransformerException(this, e);
		}
	}
			
	private void createSoapFault(String errorText, StringBuffer result) {
		result.append("<?xml version='1.0' encoding='UTF-8'?>");
		result.append("<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">");
		result.append("<soap:Body>");
		result.append("<soap:Fault>");
		result.append("<faultcode>soap:Server</faultcode>");
		result.append("<faultstring>VP009 Exception when calling the service producer: " + errorText + "</faultstring>");
		result.append("</soap:Fault>");
		result.append("</soap:Body>");
		result.append("</soap:Envelope>");
	}
}