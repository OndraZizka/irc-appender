package net.sf.ircappender.app;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * a simple test application
 *
 * @author hendrik
 *
 */
public class TestApplication {
	/**
	 * initializes log4j with a custom properties file.
	 *
	 * @param filename log4j.properties
	 * @throws IOException 
	 */
	public static void init() throws IOException {
		InputStream propsFile = TestApplication.class.getResourceAsStream("log4j.properties");
		Properties props = new Properties();
		props.load(propsFile);
		PropertyConfigurator.configure(props);
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		init();
		Logger logger = Logger.getLogger(TestApplication.class);
		logger.fatal("fatal");
		logger.error("error");
		logger.warn("warn");
		logger.info("info");
		logger.debug("debug");
		Thread.sleep(20000);
		LogManager.shutdown();
	}

}
