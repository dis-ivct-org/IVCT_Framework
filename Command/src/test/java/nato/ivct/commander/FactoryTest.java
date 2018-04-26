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

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import nato.ivct.commander.CmdListSuT.SutDescription;
import nato.ivct.commander.CmdSetLogLevel.LogLevel;
import nato.ivct.commander.CmdStartTestResultListener.OnResultListener;
import nato.ivct.commander.CmdStartTestResultListener.TcResult;

public class FactoryTest {
	@Test
	public void testCreateCmdListBadgesMethod() {
		CmdListBadges lb = Factory.createCmdListBadges();
		assertTrue("Factory Test createCmdListBadges should return CmdListBadges", lb != null);
		lb.execute();
		assertTrue("Some Badges should be found", lb.badgeMap.size() > 0);
	}

	@Test
	public void testCreateCmdListSutMethod() {
		CmdListSuT cl = Factory.createCmdListSut();
		assertTrue("Factory Test createCmdListSut should return CmdListSut", cl != null);
		cl.execute();
		assertTrue("Some SuT's should be found", cl.sutMap.size() > 0);
	}

	@Test
	public void testCreateCmdQuitMethod() {
		CmdQuit qc = Factory.createCmdQuit();
		assertTrue("Factory Test createCmdQuit should return CmdQuit", qc != null);
		qc.execute();
	}

	@Test
	public void testCmdSendTcStatus() {
		CmdSendTcStatus cmd = Factory.createCmdSendTcStatus();
		assertTrue("Factory Test createCmdQuit should return CmdQuit", cmd != null);
		cmd.execute();
	}

	@Test
	public void testCmdSendTcVerdict() {
		CmdSendTcVerdict cmd = Factory.createCmdSendTcVerdict("sut", "sutDir", "testScheduleName", "testcase", "verdict", "verdictText");
		assertTrue("Factory Test createCmdQuit should return CmdQuit", cmd != null);
		cmd.execute();
	}

	@Test
	public void testCreateCmdSetLogLevelMethod() {
		CmdSetLogLevel sll = Factory.createCmdSetLogLevel(LogLevel.DEBUG);
		assertTrue("Factory Test createCmdSetLogLevel should return CmdSetLogLevel", sll != null);
		sll.setLogLevel(LogLevel.TRACE);
		sll.execute();
		sll.setLogLevel(LogLevel.DEBUG);
		sll.execute();
		sll.setLogLevel(LogLevel.INFO);
		sll.execute();
		sll.setLogLevel(LogLevel.WARNING);
		sll.execute();
		sll.setLogLevel(LogLevel.ERROR);
		sll.execute();
	}

	@Test
	public void testCreateCmdStartTcMethod() {
		CmdStartTc stc = Factory.createCmdStartTc("hw_iosb", "HelloWorld-1.0.0", "some.test.case", "c:/tmp");
		assertTrue("Factory Test createCmdStartTc should return CmdStartTc", stc != null);
		stc.execute();
		assertTrue("Get SuT name", stc.getSut().contentEquals("hw_iosb"));
		assertTrue("Get Badge name", stc.getBadge().contentEquals("HelloWorld-1.0.0"));
		stc = Factory.createCmdStartTc("xyz", "xyz-1.0.0", "some.test.case", "c:/tmp");
		assertTrue("Factory Test createCmdStartTc should return CmdStartTc", stc != null);
		stc.execute();
		assertTrue("Get SuT name", stc.getSut() != null);
		assertTrue("Get Badge name", stc.getBadge() != null);
	}

	@Test
	public void testCreateCmdStartTestResultListenerMethod() {
		class OnResultListenerTest implements OnResultListener {

			@Override
			public void onResult(TcResult result) {
				assertTrue(result.sutName != null);
				assertTrue(result.sutDir != null);
				assertTrue(result.testScheduleName != null);
				assertTrue(result.testcase != null);
				assertTrue(result.verdict != null);
				assertTrue(result.verdictText != null);
			}

		}
		OnResultListener testListener = new OnResultListenerTest();
		assertTrue("Factory Test createCmdStartTestResultListener should return CmdStartTestResultListener",
				Factory.createCmdStartTestResultListener(testListener) != null);
	}

	@Test
	public void testHelloWorldFederate() {
		CmdListSuT cmd = Factory.createCmdListSut();
		cmd.execute();
		assertTrue("should have a list of SuTs", cmd.sutMap != null);
		assertTrue("hw_iosb should exist", cmd.sutMap.containsKey("hw_iosb"));
		SutDescription hw = cmd.sutMap.get("hw_iosb");
		assertTrue(hw.vendor.equals("Fraunhofer IOSB"));
	}
}
