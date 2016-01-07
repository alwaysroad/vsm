package VSMLogger;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import VSMConstants.VSMContant;

public class VSMLogger {
	static private FileHandler fileTxt;
	static private SimpleFormatter formatterTxt;

	static private FileHandler fileHTML;
	static private Formatter formatterHTML;

	/**
	 * Returns a logger instance for each class. Class specific logger
	 * 
	 * @param className
	 * @return
	 * @throws IOException
	 */
	static public Logger setup(String className) {

		Logger logger = Logger.getLogger(className);

		Logger rootLogger = Logger.getLogger("");
		Handler[] handlers = rootLogger.getHandlers();
		if (handlers[0] instanceof ConsoleHandler) {
			rootLogger.removeHandler(handlers[0]);
		}

		logger.setLevel(Level.INFO);
		try {
			fileTxt = new FileHandler(VSMContant.LOGGING + "/" + className
					+ ".txt", true);
			fileHTML = new FileHandler(className + ".html", true);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		formatterTxt = new SimpleFormatter();
		fileTxt.setFormatter(formatterTxt);
		logger.addHandler(fileTxt);

		formatterHTML = new VSMHtmlFormatter();
		fileHTML.setFormatter(formatterHTML);
		logger.addHandler(fileHTML);

		return logger;
	}
}
