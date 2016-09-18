package fr.apiscol.metadata.scolomfr3utils.xsd;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.xml.validation.Validator;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Test;

import fr.apiscol.metadata.scolomfr3utils.TestAppender;
import fr.apiscol.metadata.scolomfr3utils.version.SchemaVersion;

/**
 * Test xsd validation with different kinds of files
 */
public class ValidatorLoaderTest {

	private static final SchemaVersion WRONG_SCOLOMFR_VERSION = new SchemaVersion(2, 1);
	private static final String WRONG_VERSION_MESSAGE = String
			.format(ValidatorLoader.NO_XSD_FILE_PATH_PROVIDED_IN_CONFIGURATION_FOR_VERSION, WRONG_SCOLOMFR_VERSION);
	private static final SchemaVersion RIGHT_SCOLOMFR_VERSION = new SchemaVersion(3, 0);

	@Test
	public void testXsdLoadingFailureWithWrongVersion() {
		final TestAppender appender = new TestAppender();
		final Logger logger = Logger.getRootLogger();
		logger.addAppender(appender);
		ValidatorLoader validatorLoader = new ValidatorLoader();
		validatorLoader.loadXsd(WRONG_SCOLOMFR_VERSION);
		final List<LoggingEvent> log = appender.getLog();
		assertTrue("There is 1 message", log.size() == 1);
		final LoggingEvent firstLogEntry = log.get(0);
		assertTrue("Message should be " + WRONG_VERSION_MESSAGE,
				firstLogEntry.getMessage().equals(WRONG_VERSION_MESSAGE));
		assertTrue("Logging level of success message should be ERROR and not " + firstLogEntry.getLevel(),
				firstLogEntry.getLevel() == Level.ERROR);
	}

	@Test
	public void testXsdLoadingSuccessWithRightVersion() {
		final TestAppender appender = new TestAppender();
		final Logger logger = Logger.getRootLogger();
		logger.addAppender(appender);
		ValidatorLoader validatorLoader = new ValidatorLoader();
		Validator xsd = validatorLoader.loadXsd(RIGHT_SCOLOMFR_VERSION);
		assertFalse("xsd should not be null", xsd == null);
	}

}
