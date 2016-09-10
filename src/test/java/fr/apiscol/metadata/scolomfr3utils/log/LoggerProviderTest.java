package fr.apiscol.metadata.scolomfr3utils.log;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Test;

import fr.apiscol.metadata.scolomfr3utils.TestAppender;

/**
 * Test the logger provider (wrapper for log4j)
 */
public class LoggerProviderTest {

	private static final String WARNING_MESSAGE = "Don't try to publish learning object metadata without it.";
	private static final String INFO_MESSAGE = "Scolomfrutils is very useful";

	@Test
	public void testNoPrefLabelForMissingressource() throws Exception {
		final TestAppender appender = new TestAppender();
		final Logger rootLogger = Logger.getRootLogger();
		rootLogger.addAppender(appender);
		assertTrue("Log4j should be configured", LoggerProvider.log4JisConfigured());
		Logger logger = LoggerProvider.getLogger(this.getClass());
		logger.info(INFO_MESSAGE);
		logger.warn(WARNING_MESSAGE);
		final List<LoggingEvent> log = appender.getLog();
		assertTrue("There are 2 messages", log.size() == 2);
		final LoggingEvent firstLogEntry = log.get(0);
		final LoggingEvent secondLogEntry = log.get(1);
		assertTrue("Fist message is :" + INFO_MESSAGE, firstLogEntry.getMessage().equals(INFO_MESSAGE));
		assertTrue("Logging level of first message should be INFO and not " + firstLogEntry.getLevel(),
				firstLogEntry.getLevel() == Level.INFO);
		assertTrue("Second message is :" + WARNING_MESSAGE, secondLogEntry.getMessage().equals(WARNING_MESSAGE));
		assertTrue("Logging level of second message should be WARNING and not " + secondLogEntry.getLevel(),
				secondLogEntry.getLevel() == Level.WARN);

	}

}
