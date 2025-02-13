package de.fraunhofer.iosb.testrunner;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;

import de.fraunhofer.iosb.tc_lib.GenericTestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import ch.qos.logback.classic.Level;
import de.fraunhofer.iosb.messaginghelpers.LogConfigurationHelper;
import de.fraunhofer.iosb.tc_lib.AbstractTestCase;
import de.fraunhofer.iosb.tc_lib.IVCT_Verdict;
import nato.ivct.commander.CmdHeartbeatSend;
import nato.ivct.commander.CmdHeartbeatSend.OnCmdHeartbeatSend;
import nato.ivct.commander.CmdListTestSuites;
import nato.ivct.commander.CmdListTestSuites.TestSuiteDescription;
import nato.ivct.commander.CmdQuitListener;
import nato.ivct.commander.CmdQuitListener.OnQuitListener;
import nato.ivct.commander.CmdSendTcVerdict;
import nato.ivct.commander.CmdSetLogLevel.LogLevel;
import nato.ivct.commander.CmdSetLogLevelListener;
import nato.ivct.commander.CmdSetLogLevelListener.OnSetLogLevelListener;
import nato.ivct.commander.CmdStartTcListener;
import nato.ivct.commander.CmdStartTcListener.OnStartTestCaseListener;
import nato.ivct.commander.CmdStartTcListener.TcInfo;
import nato.ivct.commander.Factory;

/**
 * Testrunner that listens for certain commands to start and stop test cases.
 *
 * @author Manfred Schenk (Fraunhofer IOSB)
 * @author Reinhard Herzog (Fraunhofer IOSB)
 */
public class TestEngine extends TestRunner implements OnSetLogLevelListener, OnQuitListener, OnStartTestCaseListener, OnCmdHeartbeatSend {

	private static Logger logger = LoggerFactory.getLogger(TestEngine.class);

	public String logLevelId = Level.INFO.toString();
	public String testCaseId = "no test case is running";

	private CmdListTestSuites testSuites;
	private HashMap<String, URLClassLoader> classLoaders = new HashMap<String, URLClassLoader>();

	/**
	 * Main entry point from the command line.
	 *
	 * @param args The command line arguments
	 */
	public static void main(final String[] args) {
		@SuppressWarnings("unused")
		final TestEngine runner = new TestEngine();
	}

	/**
	 * public constructor.
	 */
	public TestEngine() {

		// set heartbeat identifier
		myClassName = this.getClass().getSimpleName();

		// initialize the IVCT Commander Factory
		Factory.initialize();

		// Configure the logger
		LogConfigurationHelper.configureLogging();

		// start command listeners
		(new CmdSetLogLevelListener(this)).execute();
		(new CmdStartTcListener(this)).execute();
		(new CmdQuitListener(this)).execute();
		try {
			(new CmdHeartbeatSend(this)).execute();
		} catch (Exception e1) {
			logger.error("Could not start HeartbeatSend: " + e1.toString());
		}

		// get the test suite descriptions
		testSuites = new CmdListTestSuites();
		try {
			testSuites.execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @author hzg
	 * 
	 *         TestScheduleRunner executes a sequence of test cases
	 *
	 */
	private class TestScheduleRunner implements Runnable {
		TcInfo info;
		private TestRunner testRunner;

		TestScheduleRunner(final TcInfo info, final TestRunner testRunner) {
			this.info = info;
			this.testRunner = testRunner;
		}

		private File getCwd() {
			return new File("").getAbsoluteFile();
		}

		/**
		 * This method provides a way to set the current working directory which is not
		 * available as such in java.
		 *
		 * N.B. This method uses a trick to get the desired result
		 *
		 * @param directory_name name of directory to be the current directory
		 * @return true if successful
		 */
		private boolean setCurrentDirectory(String directory_name) {
			boolean result = false; // Boolean indicating whether directory was
									// set
			File directory; // Desired current working directory

			directory = new File(directory_name).getAbsoluteFile();
			if (directory.exists()) {
				directory.mkdirs();
				result = (System.setProperty("user.dir", directory.getAbsolutePath()) != null);
			}

			return result;
		}

		private void extendThreadClassLoader(final TestSuiteDescription testSuiteDescription) {
			URLClassLoader classLoader = classLoaders.get(testSuiteDescription.id);
			if (classLoader == null) {
				String ts_path = Factory.props.getProperty(Factory.IVCT_TS_DIST_HOME_ID);
				String lib_path = ts_path + "/" + testSuiteDescription.tsLibTimeFolder;
				File dir = new File(lib_path);
				File[] filesList = dir.listFiles();
				if (filesList == null) {
					logger.info("No files found in folder {}", dir.getPath());
					return;
				}

				URL[] urls = new URL[filesList.length];
				for (int i = 0; i < filesList.length; i++) {
					try {
						urls[i] = filesList[i].toURI().toURL();
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}
				classLoader = new URLClassLoader(urls, TestRunner.class.getClassLoader());
				classLoaders.put(testSuiteDescription.id, classLoader);
			}
			Thread.currentThread().setContextClassLoader(classLoader);
		}

		public void run() {
			logger.info("TestEngine:onMessageConsumer:run: " + info.testCaseId);
			MDC.put("sutName", info.sutName);
			MDC.put("sutDir", info.sutDir);
			MDC.put("badge", info.testSuiteId);
			MDC.put("testcase", info.testCaseId);

			TestSuiteDescription tsd = testSuites.getTestSuiteForTc(info.testCaseId);
			if (tsd == null) {
				logger.error("TestEngine:onMessageConsumer:run: unknown testsuite for testcase: " + info.testCaseId);
				return;
			}
			String runFolder = Factory.props.getProperty(Factory.IVCT_TS_DIST_HOME_ID) + '/' + tsd.tsRunTimeFolder;

			logger.info("TestEngine:onMessageConsumer:run: tsRunFolder is " + runFolder);
			if (setCurrentDirectory(runFolder)) {
				logger.info("TestEngine:onMessageConsumer:run: setCurrentDirectory true");
			}

			File f = getCwd();
			String tcDir = f.getAbsolutePath();
			logger.info("TestEngine:onMessageConsumer:run: TC DIR is " + tcDir);

			logger.info("TestEngine:onMessageConsumer:run: The test case class is: " + testCaseId);
			String[] testcases = info.testCaseId.split("\\s");
			IVCT_Verdict verdicts[] = new IVCT_Verdict[testcases.length];

			extendThreadClassLoader(tsd);

			int i = 0;
			for (final String classname : testcases) {
				GenericTestCase testCase = null;
				try {
					testCase = (GenericTestCase) Thread.currentThread().getContextClassLoader().loadClass(classname)
							.newInstance();
				} catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
					logger.error("Could not instantiate " + classname + " !", ex);
				}
				if (testCase == null) {
					verdicts[i] = new IVCT_Verdict();
					verdicts[i].verdict = IVCT_Verdict.Verdict.INCONCLUSIVE;
					verdicts[i].text = "Could not instantiate " + classname;
					i++;
					continue;
				}
				testCase.setSutName(info.sutName);
				testCase.setTcName(classname);
				testCase.setSettingsDesignator(info.settingsDesignator);
				testCase.setFederationName(info.federationName);
				testCase.setSutFederateName(info.sutFederateName);

				verdicts[i++] = testCase.execute(info.testCaseParam.toString(), logger);
			}

			// The JMSLogSink waits on this message!
			// The following pair of lines will cause the JMSLogSink to close the log file!
			MDC.put("tcStatus", "ended");
			logger.info("Test Case Ended");

			for (i = 0; i < testcases.length; i++) {
				new CmdSendTcVerdict(info.sutName, info.sutDir, info.testSuiteId, testcases[i],
						verdicts[i].verdict.name(), verdicts[i].text).execute();
			}
			MDC.put("tcStatus", "inactive");
		}

	}

	@Override
	public void onSetLogLevel(LogLevel level) {
		this.logLevelId = level.name();
		if (logger instanceof ch.qos.logback.classic.Logger) {
			ch.qos.logback.classic.Logger lo = (ch.qos.logback.classic.Logger) logger;
			switch (level) {
			case ERROR:
				logger.trace("TestEngine:onMessageConsumer:run: error");
				lo.setLevel(Level.ERROR);
				break;
			case WARNING:
				logger.trace("TestEngine:onMessageConsumer:run: warning");
				lo.setLevel(Level.WARN);
				break;
			case INFO:
				logger.trace("TestEngine:onMessageConsumer:run: info");
				lo.setLevel(Level.INFO);
				break;
			case DEBUG:
				logger.trace("TestEngine:onMessageConsumer:run: debug");
				lo.setLevel(Level.DEBUG);
				break;
			case TRACE:
				logger.trace("TestEngine:onMessageConsumer:run: trace");
				lo.setLevel(Level.TRACE);
				break;
			}
		}

	}

	@Override
	public void onQuit() {
		System.exit(0);
	}

	@Override
	public void onStartTestCase(TcInfo info) {
		this.testCaseId = new String(info.testCaseId);
		Thread th1 = new Thread(new TestScheduleRunner(info, this));
		th1.start();
	}
	
	
	/*  implement a heartbeat ,  brf 05.07.2019 (Fraunhofer IOSB)
     *  CmdHeartbeatSend will fetch all 5 Seconds the health state from  'here'
     *  and send all 5 Seconds a message to ActiveMQ
     *  So if the value for health is changed here, this will change the tenor 
     *  of the message  CmdHeartbeatSend  sends to ActiveMQ
     *  if this thread is stopped, CmdHeardbeatListen will give out an Alert-Status
     */
    
    
	@Override
	public String getMyClassName() {
        return myClassName;
    }
    

	@Override
    public boolean getMyHealth() {
        return health;
    }
	
}
