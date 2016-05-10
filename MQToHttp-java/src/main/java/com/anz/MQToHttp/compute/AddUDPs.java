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
		
		// Create LocalEnvironment/Destination
		MbElement destination = outAssembly.getLocalEnvironment().getRootElement()
				.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "Destination","");
				
		// Create LocalEnvironement/Destination/HTTP
		MbElement http = destination.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "HTTP","");
		
		// Create LocalEnvironement/Destination/MQ/DestinationData
		MbElement destinationData = destination.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "MQ","")
				.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "DestinationData","");
		
		
		// Set HTTP Method
		MbElement requestMethod = http.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "RequestLine", "")		
				.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "Method", "")	;
		requestMethod.setValue(getUserDefinedAttribute("HTTP_METHOD"));	
		
		// Set HTTP URL
		MbElement requestURL = http.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "RequestURL", "");
		requestURL.setValue(getUserDefinedAttribute("HTTP_URL"));
				
		// Create Local Environment Output Queue element
		MbElement outputQ = destinationData.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "queueName", "");
		
		// Create Local Environment Output Queue Manager element
		MbElement outputQMgr = destinationData.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "queueManagerName", "");
		
		// Set output Queue Name to User Defined Property: outputQueue
		outputQ.setValue((String) getUserDefinedAttribute("OUTPUT_QUEUE"));
		
		// Set output Queue Manager Name to User Defined Property: outputQueue
		outputQMgr.setValue((String) getUserDefinedAttribute("OUTPUT_QUEUE_MGR"));
		
		// Create Content-type HTTP Input Header
		MbElement contentType = outAssembly.getMessage().getRootElement()
				.getFirstElementByPath("/BLOB")
				.createElementBefore(MbElement.TYPE_NAME_VALUE, "HTTPInputHeader", "")
				.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "Content-Type", "");
		contentType.setValue("application/json");
				
	}

	@Override
	public TransformType getTransformationType() {
		// TODO Auto-generated method stub
		return TransformType.MQ_MQ;
	}
}
