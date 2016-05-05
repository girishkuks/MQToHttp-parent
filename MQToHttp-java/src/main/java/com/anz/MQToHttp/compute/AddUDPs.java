/**
 * 
 */
package com.anz.MQToHttp.compute;

import org.apache.logging.log4j.LogManager;

import org.apache.logging.log4j.Logger;

import com.anz.MQToHttp.transform.PreTransformBLSample;

import com.anz.common.cache.impl.CacheHandlerFactory;
import com.anz.common.compute.TransformType;
import com.anz.common.compute.impl.CommonJavaCompute;
import com.anz.common.transform.ITransformer;
import com.ibm.broker.config.proxy.BrokerProxy;
import com.ibm.broker.config.proxy.ExecutionGroupProxy;
import com.ibm.broker.config.proxy.MessageFlowProxy;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.config.proxy.AttributeConstants;

/**
 * @author sanketsw & psamon
 *
 */
public class AddUDPs extends CommonJavaCompute {
	
	private static final Logger logger = LogManager.getLogger();

	/* (non-Javadoc)
	 * @see com.anz.common.compute.impl.CommonJsonJsonTransformCompute#getTransformer()
	 */
	@Override
	public void execute(MbMessageAssembly inAssembly,
			MbMessageAssembly outAssembly) throws Exception {
		
		logger.info("AddUDPs:execute()");
		
		// Set HTTP URL
		MbElement requestURL = outAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/Destination/HTTP/RequestURL");
		requestURL.setValue(getUserDefinedAttribute("HTTP_URL"));
		
		// Set HTTP Method
		MbElement requestMethod = outAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/Destination/HTTP/RequestLine/Method");
		requestURL.setValue(getUserDefinedAttribute("HTTP_METHOD"));
		
		// Get message root element
		MbElement root = outAssembly.getMessage().getRootElement();
		
		// Get Reply To Queue 
		MbElement replyToQ = root.getFirstElementByPath("/MQMD/ReplyToQ");
		logger.info("Original ReplyToQ = {}", replyToQ.getValueAsString());

		// Create Local Environment Output Queue element
		MbElement outputQ = outAssembly.getLocalEnvironment().getRootElement()
				.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "Destination","")
				.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "MQ", "")
				.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "DestinationData", "")
				.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "queueName", "");
		
		// Set output Queue Name to User Defined Property: outputQueue
		outputQ.setValue((String) getUserDefinedAttribute("OUTPUT_QUEUE"));
		logger.info("{} = {}", outputQ.getName(), outputQ.getValue());
				
	}

	@Override
	public TransformType getTransformationType() {
		// TODO Auto-generated method stub
		return TransformType.MQ_MQ;
	}
}
