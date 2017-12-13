/* Copyright 2017, Reinhard Herzog (Fraunhofer IOSB)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */

package nato.ivct.commander;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;

import org.slf4j.LoggerFactory;

import de.fraunhofer.iosb.messaginghelpers.PropertyBasedClientSetup;
import nato.ivct.commander.CmdQuitListener.OnQuitListener;
import nato.ivct.commander.CmdSetLogLevel.LogLevel;
import nato.ivct.commander.CmdStartTestResultListener.OnResultListener;
import nato.ivct.commander.CmdTcStatusListener.OnTcStatusListener;

/*
 * The Factory is used to create Command objects to be executed by a user interface.
 * Before any Command objects can be created, the factory object need to be initialized.
 * The Factory is also the container for all properties and JMS elements. The life cycle is managed by the 
 * caller of the factory
 */
public class Factory {

	public static Properties props = null;
	public static final String IVCT_CONF = "IVCT_CONF";
	public static final String IVCT_TS_HOME_ID = "IVCT_TS_HOME_ID";
	public static final String IVCT_SUT_HOME_ID = "IVCT_SUT_HOME_ID";
	public static final String IVCT_BADGE_HOME_ID = "IVCT_BADGE_HOME_ID";
	public static final String RTI_ID = "RTI_ID";
	public static final String PROPERTY_IVCTCOMMANDER_QUEUE = "ivctcommander.queue";
	private static MessageProducer producer = null;
	private static int cmdCounter = 0;

	public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Factory.class);

	static public PropertyBasedClientSetup jmsHelper = null;

	/*
	 * Factory has to be initialized before any commands are being created.
	 */
	public static void initialize() {
		if (props == null) {
			String home = System.getenv(IVCT_CONF);
			props = new Properties();

			if (home == null) {
				LOGGER.error("Environment Variable <<IVCT_CONF>> not set");
				System.exit(-1);
			}
			try {
				props.load(new FileInputStream(home + "/IVCT.properties"));
				jmsHelper = new PropertyBasedClientSetup(props);

			} catch (final Exception e) {
				LOGGER.warn("no properties file IVCT.properties found");
				props.setProperty(IVCT_TS_HOME_ID, "C:/MSG134/DemoFolders/IVCTtestSuites");
				props.setProperty(IVCT_SUT_HOME_ID, "C:/MSG134/DemoFolders/IVCTsut");
				props.setProperty(IVCT_BADGE_HOME_ID, "C:/MSG134/DemoFolders/Badges");
				props.setProperty(RTI_ID, "pRTI");
				try {
					props.store(new FileOutputStream("IVCT.properties"), "IVCT Properties File");
				} catch (IOException e1) {
					LOGGER.error("Unable to write IVCT.properties file. Please verify settings!");
					e1.printStackTrace();
				}
				LOGGER.warn("New IVCT.properties file has been created with default values. Please verify settings!");
				LOGGER.warn(props.toString());
			}
			jmsHelper.parseProperties();
			jmsHelper.initConnection();
			jmsHelper.initSession();
			producer = jmsHelper.setupTopicProducer(props.getProperty(PROPERTY_IVCTCOMMANDER_QUEUE, "commands"));
		} // otherwise consider to be already initialized
	}

	public static String readWholeFile(final String filename) {
		BufferedReader br = null;
		String everything = null;

		File myFile = new File(filename);
		if (myFile.isFile() == false) {
			return everything;
		}

		try {
			br = new BufferedReader(new FileReader(filename));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			everything = sb.toString();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
				}
			}
		}

		return everything;
	}

	public static String replaceMacro(String inString) {
		StringBuilder env = new StringBuilder();
		StringBuilder out = new StringBuilder();
		int len;

		env.setLength(512);
		out.setLength(512);
		out.setLength(0);
		len = inString.length();

		// Loop through inString character by character
		for (int i = 0, l = 0; i < len; i++, l++) {
			boolean gotClosing = false;
			boolean gotEnv = false;
			if (i < len - 1) {
				// Check if we have "$(" in inString
				if (inString.charAt(i) == '$' && inString.charAt(i + 1) == '(') {
					gotClosing = false;
					for (int j = i + 2, k = 0; j < len; j++, k++) {
						if (inString.charAt(j) == ')') {
							String b = null;
							gotClosing = true;
							gotEnv = true;
							env.setLength(k);
							if (env.length() > 0) {
								b = Factory.props.getProperty(env.toString());
							} else {
								LOGGER.error("LineUtil:replaceMacro: Missing environment variable ");
							}
							if (b != null) {
								out.append(b);
								l += b.length() - 1;
								i = j;
							} else {
								LOGGER.error(
										"LineUtil:replaceMacro: Environment variable not found: " + env.toString());
								String s = inString.subSequence(i, i + k + 3).toString();
								out.append(s);
								l += j - i;
								i = j;
							}
							break;
						}
						env.setCharAt(k, inString.charAt(j));
					}
					if (gotClosing == false) {
						LOGGER.error("LineUtil:replaceMacro: Missing closing bracket");
					}
				}
			}
			if (gotEnv) {
				gotEnv = false;
				continue;
			}
			out.insert(l, inString.charAt(i));
			out.setLength(l + 1);
		}

		return out.toString();
	}

	public static void sendToJms(final String userCommand) {
		Message message = jmsHelper.createTextMessage(userCommand);
		try {
			producer.send(message);
		} catch (JMSException e) {
			LOGGER.error("could not send command: " + userCommand);
			e.printStackTrace();
		}
	}

	// factory methods for commands

	public static CmdListSuT createCmdListSut() {
		initialize();
		return new CmdListSuT();
	}

	public static CmdListBadges createCmdListBadges() {
		initialize();
		return new CmdListBadges();
	}

	public static CmdStartTc createCmdStartTc(String _sut, String _badge, String _tc, String _runFolder) {
		initialize();
		return new CmdStartTc(_sut, _badge, _tc, _runFolder);
	}

	public static CmdSetLogLevel createCmdSetLogLevel(LogLevel level) {
		initialize();
		return new CmdSetLogLevel(level);
	}

	public static CmdQuit createCmdQuit() {
		initialize();
		return new CmdQuit();
	}

	public static CmdQuitListener createCmdQuitListener(OnQuitListener listener) {
		initialize();
		return new CmdQuitListener(listener);
	}

	public static CmdStartTestResultListener createCmdStartTestResultListener(OnResultListener listener) {
		initialize();
		return new CmdStartTestResultListener(listener);
	}

	public static CmdTcStatusListener createCmdTcStatusListener(OnTcStatusListener listener) {
		initialize();
		return new CmdTcStatusListener(listener);
	}

	public static CmdSendTcStatus createCmdSendTcStatus() {
		initialize();
		return new CmdSendTcStatus();
	}

	public static int getCmdCounter() {
		return cmdCounter;
	}

	public static int newCmdCount() {
		return ++cmdCounter;
	}

}
