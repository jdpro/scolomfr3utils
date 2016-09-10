package fr.apiscol.metadata.scolomfr3utils;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Test;

/**
 * Test xsd validation with different kinds of files
 */
public class AppITest extends LoggerAwareTest {

	@Test
	public void testXSDValidationSuccess() throws Exception {
		final TestAppender appender = new TestAppender();
		final Logger logger = Logger.getRootLogger();
		logger.addAppender(appender);
		String[] args = { "-f", "src/test/data/3.0/any/valid/echanges.xml", "--check-xsd", "-v", "3.0" };
		App.main(args);

		final List<LoggingEvent> log = appender.getLog();
		assertTrue("There are 4 messages", log.size() == 4);
		final LoggingEvent secondLogEntry = log.get(1);
		assertTrue("Second message is 'XSD validation success'",
				secondLogEntry.getMessage().equals("XSD validation success"));
		assertTrue("Logging level of success message should be INFO and not "+secondLogEntry.getLevel(), secondLogEntry.getLevel() == Level.INFO);

	}
	@Test
	public void testXSDValidationFailure() throws Exception {
		final TestAppender appender = new TestAppender();
		final Logger logger = Logger.getRootLogger();
		logger.addAppender(appender);
		String[] args = { "-f", "src/test/data/3.0/1/invalid/double-general.xml", "--check-xsd", "-v", "3.0" };
		App.main(args);

		final List<LoggingEvent> log = appender.getLog();
		assertTrue("There are more than 6 messages", log.size() >6);
		final LoggingEvent secondLogEntry = log.get(1);
		assertTrue("Second message is 'XSD validation failure'",
				secondLogEntry.getMessage().equals("XSD validation failure"));
		assertTrue("Logging level of failure message should be ERROR and not "+secondLogEntry.getLevel(), secondLogEntry.getLevel() == Level.ERROR);

	}

	
}
