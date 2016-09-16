package fr.apiscol.metadata.scolomfr3utils.log;

import java.util.Enumeration;

import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class LoggerProvider {

	private LoggerProvider(){
		
	}

	public static Logger getLogger(Class<?> callingClass) {
		if (!log4JisConfigured()) {
			BasicConfigurator.configure();
		}

		Logger logger = Logger.getLogger(callingClass);
		return logger;
	}

	/**
	 * Returns true if it appears that log4j have been previously configured.
	 * This code checks to see if there are any appenders defined for log4j
	 * which is the definitive way to tell if log4j is already initialized
	 * 
	 * @return true if log4j have been previously configured
	 */
	public static boolean log4JisConfigured() {
		Enumeration<Appender> appenders = Logger.getRootLogger().getAllAppenders();
		if (appenders.hasMoreElements()) {
			return true;
		} else {
			@SuppressWarnings("unchecked")
			Enumeration<Appender> loggers = LogManager.getCurrentLoggers();
			while (loggers.hasMoreElements()) {
				Logger c = (Logger) loggers.nextElement();
				if (c.getAllAppenders().hasMoreElements())
					return true;
			}
		}
		return false;
	}

}
