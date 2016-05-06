

/**
 * 
 */
package com.anz.MQToHttp.error;

import java.util.Calendar;

import org.apache.logging.log4j.Logger;

import com.anz.common.compute.ComputeInfo;
import com.anz.common.compute.impl.ComputeUtils;
import com.anz.common.dataaccess.models.iib.ErrorStatusCode;
import com.anz.common.dataaccess.models.iib.IFXCode;
import com.anz.common.domain.ErrorStatusCodeDomain;
import com.anz.common.domain.IFXCodeDomain;
import com.anz.common.error.ExceptionMessage;
import com.anz.common.transform.ITransformer;
import com.anz.common.transform.TransformUtils;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;

/**
 * @author sanketsw
 * 
 */
public class TransformHttpErrorResponse implements
		ITransformer<MbMessageAssembly, String> {

	

	@Override
	public String execute(MbMessageAssembly outAssembly, Logger logger, ComputeInfo metadata) throws Exception {
		String out = null;
		MbMessage inMessage = outAssembly.getMessage();
		String inputString = ComputeUtils.getStringFromBlob(inMessage);

		// Log the error
		logger.error(inputString);

		ExceptionMessage exceptionMessage = new ExceptionMessage();
		exceptionMessage.setTimestamp(Calendar.getInstance().getTime());
		exceptionMessage.setShortException(inputString);
		exceptionMessage.setBrokerAndServiceDetails(metadata);
		exceptionMessage.setStaticProperties();

		// Return the error after mapping errorCode from cache/database
		ErrorStatusCode errorCode = ErrorStatusCodeDomain.getInstance().getErrorCode(ErrorStatusCode.TimeoutException);
		
		// If error code cannot be mapped, then return the original error
		if (errorCode != null) {			
			exceptionMessage.setStatus(errorCode);
		}	

		out = TransformUtils.toJSON(exceptionMessage);
		logger.info("Error Status Code object {}", out);
		return out;
	}

}