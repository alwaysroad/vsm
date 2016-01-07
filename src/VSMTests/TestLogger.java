package VSMTests;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import VSMLogger.VSMLogger;

public class TestLogger {

	private final Logger LOGGER;

	/*
	 * Logger
	 */
	public TestLogger() throws IOException {
		LOGGER = VSMLogger.setup(TestLogger.class.getName());
	}

	public void doSomeThingAndLog() {
		// ... more code

		// now we demo the logging

		doSomethingelse();

		// set the LogLevel to Severe, only severe Messages will be written
		LOGGER.setLevel(Level.SEVERE);
		LOGGER.severe("Info Log");
		LOGGER.warning("Info Log");
		LOGGER.info("Info Log");
		LOGGER.finest("Really not important");

		// set the LogLevel to Info, severe, warning and info will be written
		// finest is still not written
		// LOGGER.setLevel(Level.INFO);
		// LOGGER.severe("Info Log");
		// LOGGER.warning("Info Log");
		// LOGGER.info("Info Log");
		// LOGGER.finest("Really not important");
	}

	private void doSomethingelse() {

		LOGGER.log(Level.SEVERE, "test");

	}

	public static void main(String[] args) throws IOException {
		TestLogger tester = new TestLogger();

		tester.doSomeThingAndLog();
	}
}
