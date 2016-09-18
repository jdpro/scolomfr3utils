package fr.apiscol.metadata.scolomfr3utils;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Test;

import fr.apiscol.metadata.scolomfr3utils.version.SchemaVersionHandler;
import fr.apiscol.metadata.scolomfr3utils.version.VersionDetectionException;

/**
 * Test xsd validation with different kinds of files
 */
public class AppVersionDetectionITest extends LoggerAwareTest {

	private static final String VERSION_DETECTION_EXCEPTION_MESSAGE = VersionDetectionException.class.getName() + ": "
			+ SchemaVersionHandler.MISSING_METADATA_SCHEMA;

	@Test
	public void testValidationFailureWithoutVersion() throws Exception {
		// It's impossible to test a scolomfr document without metadataSchema
		// tag without providing the -v option
		final TestAppender appender = new TestAppender();
		final Logger logger = Logger.getRootLogger();
		logger.addAppender(appender);
		String[] args = { "-f", "src/test/data/3.0/version/valid/no-metadataschema.xml", "--check-all" };
		App.main(args);

		final List<LoggingEvent> log = appender.getLog();
		final LoggingEvent firstLogEntry = log.get(0);
		assertTrue(
				"First message should be \"" + VERSION_DETECTION_EXCEPTION_MESSAGE + "\" and not \""
						+ firstLogEntry.getMessage() + "\"",
				StringUtils.equalsIgnoreCase(VERSION_DETECTION_EXCEPTION_MESSAGE,
						firstLogEntry.getMessage().toString()));
		assertTrue("Logging level of first message should be ERROR and not " + firstLogEntry.getLevel(),
				firstLogEntry.getLevel() == Level.WARN);

	}

	@Test
	public void testValidationSuccessWithVersion() throws Exception {
		final TestAppender appender = new TestAppender();
		final Logger logger = Logger.getRootLogger();
		logger.addAppender(appender);
		String[] args = { "-f", "src/test/data/3.0/version/valid/no-metadataschema.xml", "--check-all", "-v", "3.0" };
		App.main(args);

		final List<LoggingEvent> log = appender.getLog();
		assertTrue("There are more than 2 messages", log.size() >= 2);
		final LoggingEvent lastLogEntry = log.get(log.size() - 1);
		String validationResultMessage = "Validation result : SUCCESS";
		assertTrue("Second message should be '" + validationResultMessage + "' and not '" + lastLogEntry.getMessage()
				+ "'", lastLogEntry.getMessage().equals(validationResultMessage));
		assertTrue("Logging level of failure message should be INFO and not " + lastLogEntry.getLevel(),
				lastLogEntry.getLevel() == Level.INFO);

	}

}
