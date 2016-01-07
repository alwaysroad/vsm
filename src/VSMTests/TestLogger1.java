package VSMTests;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import VSMLogger.VSMLogger;

public class TestLogger1 {
	private final Logger LOGGER;

	/*
	 * Logger
	 */
	public TestLogger1() throws IOException {
		LOGGER = VSMLogger.setup(TestLogger1.class.getName());
	}

	public void doSomeThingAndLog() {
		// ... more code

		// now we demo the logging

		doSomethingelse();

		// set the LogLevel to Severe, only severe Messages will be written
		LOGGER.setLevel(Level.SEVERE);
		LOGGER.severe("Info1 Log");
		LOGGER.warning("Info1 Log");
		LOGGER.info("Info1 Log");
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

		LOGGER.log(Level.SEVERE, "test1	" + TestLogger.class.getName());

	}

	public static void main(String[] args) throws IOException {
		TestLogger1 tester = new TestLogger1();

		tester.doSomeThingAndLog();
	}
}
