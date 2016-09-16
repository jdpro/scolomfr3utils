package fr.apiscol.metadata.scolomfr3utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.w3c.dom.DOMException;

import fr.apiscol.metadata.scolomfr3utils.command.MessageStatus;
import fr.apiscol.metadata.scolomfr3utils.log.LoggerProvider;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 * 
 * Entry point for command-line usage
 *
 */
public class App {

	private static final IScolomfr3Utils scolomfr3Utils = new Scolomfr3Utils();
	private static Logger logger;
	private static OptionSet options;
	private static File scolomfrFile;
	private static OptionParser parser;

	private App() {
	}

	/**
	 * Main method for command-line use of library
	 * parser.printHelpOn(System.out); has been removed for sonar compatibility
	 * 
	 * @param args
	 *            command line arguments
	 * @throws IOException If provided file is unreachable
	 */
	public static void main(String[] args) throws IOException {
		createParser();
		options = parser.parse(args);
		if (options.has("h")) {

			return;
		}
		loadXmlFile();
		initScolomfrUtils();
		launchCommand();
		displayResult();
	}

	private static void displayResult() {
		separator();
		getLogger().info("Validation result : " + (scolomfr3Utils.isValid() ? "success" : "failure").toUpperCase());
		if (!scolomfr3Utils.isValid()) {
			displayMessages(MessageStatus.FAILURE);
		}
		displayMessages(MessageStatus.WARNING);
	}

	private static void separator() {
		getLogger().info("-------------------------------------------------------");
	}

	private static void displayMessages(MessageStatus status) {
		List<String> messages = scolomfr3Utils.getMessages(status);
		if (messages.isEmpty()) {
			return;
		}
		separator();
		getLogger().info((status.toString() + " messages").toUpperCase());
		separator();
		Iterator<String> messagesIterator = messages.iterator();
		while (messagesIterator.hasNext()) {
			String message = messagesIterator.next();
			getLogger().info(message);
		}
	}

	private static void launchCommand() {
		Map<String, Pair<String, String>> commandLineOptions = Configuration.getInstance().commandLineOptions();
		Iterator<String> it = commandLineOptions.keySet().iterator();
		String methodName = null;
		while (it.hasNext()) {
			String option = it.next();
			if (!options.has(option)) {
				continue;
			}
			Pair<String, String> pair = commandLineOptions.get(option);
			methodName = pair.getLeft();
			try {
				Method methodToCall = scolomfr3Utils.getClass().getDeclaredMethod(methodName, new Class[0]);
				methodToCall.invoke(scolomfr3Utils);
				return;
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException e1) {
				getLogger().error(e1);
			} catch (InvocationTargetException e2) {
				getLogger().error(e2.getCause());
				getLogger().error(e2);
			}
		}
		if (null == methodName) {
			getLogger().error(
					"ScolomfrUtils invoked without any command or with unrecognized command. Use -h option to display help.");
		}

	}

	private static void loadXmlFile() {
		if (options.hasArgument("f")) {
			String filePath = (String) options.valueOf("f");
			scolomfrFile = new File(filePath);
			if (!scolomfrFile.exists()) {
				getLogger().error("File provided (" + filePath + ") does not exist");
			}
		}

	}

	private static void initScolomfrUtils() {
		if (options.hasArgument("v")) {
			String scolomfrVersion = (String) options.valueOf("v");
			scolomfr3Utils.setScolomfrVersion(scolomfrVersion);
		}
		if (scolomfrFile != null) {
			scolomfr3Utils.setScolomfrFile(scolomfrFile);
		}

	}

	private static OptionParser createParser() throws IOException {
		parser = new OptionParser();
		Map<String, Pair<String, String>> commandLineOptions = Configuration.getInstance().commandLineOptions();
		Iterator<String> it = commandLineOptions.keySet().iterator();
		while (it.hasNext()) {
			String option = it.next();

			Pair<String, String> pair = commandLineOptions.get(option);
			String methodName = pair.getLeft();
			String helpMessage = pair.getRight();
			try {
				scolomfr3Utils.getClass().getDeclaredMethod(methodName, new Class[0]);
				parser.accepts(option, helpMessage);
			} catch (NoSuchMethodException e) {
				getLogger().error("Method " + methodName + " from config file does not exist in "
						+ scolomfr3Utils.getClass().getCanonicalName());
				getLogger().error(e);
			} catch (SecurityException | DOMException e) {
				getLogger().error(e);
			}
		}
		parser.accepts("f", "Scolomfr xml file path").withRequiredArg();
		parser.accepts("v", "Scolomfr schema version").withRequiredArg();
		parser.accepts("h", "Display help");
		return parser;

	}

	static Logger getLogger() {
		if (logger == null) {
			logger = LoggerProvider.getLogger(App.class);
		}
		return logger;
	}

}
