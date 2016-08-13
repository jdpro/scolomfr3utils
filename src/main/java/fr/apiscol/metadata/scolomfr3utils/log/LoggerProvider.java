package fr.apiscol.metadata.scolomfr3utils.log;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class LoggerProvider {
	public static Logger getLogger(Class<?> callingClass) {
		BasicConfigurator.configure();
		return Logger.getLogger(callingClass);
	}
}
