/*
 * Copyright 2017, Reinhard Herzog (Fraunhofer IOSB) Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package de.fraunhofer.iosb.ivct.logsink;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.net.JMSTopicSink;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import nato.ivct.commander.CmdLogMsgListener.LogMsg;
import nato.ivct.commander.CmdLogMsgListener.OnLogMsgListener;
import nato.ivct.commander.CmdQuitListener.OnQuitListener;
import nato.ivct.commander.CmdStartTcListener.OnStartTestCaseListener;
import nato.ivct.commander.CmdStartTcListener.TcInfo;
import nato.ivct.commander.CmdStartTestResultListener.OnResultListener;
import nato.ivct.commander.CmdStartTestResultListener.TcResult;
import nato.ivct.commander.Factory;
import nato.ivct.commander.SutPathsFiles;


public class JMSLogSink implements OnResultListener, OnQuitListener, OnStartTestCaseListener, OnLogMsgListener {

    static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");

    private final Logger logger = (Logger) LoggerFactory.getLogger(JMSTopicSink.class);
    // private Logger log;
    private final Map<String, FileAppender<ILoggingEvent>> appenderMap = new HashMap<>();
    private final Map<String, String>                      tcLogMap    = new HashMap<>();
    private final ReportEngine                             reportEngine;


    public JMSLogSink(ReportEngine reportEngine) {
        this.reportEngine = reportEngine;
    }


    protected Object lookup(Context ctx, String name) throws NamingException {
        try {
            return ctx.lookup(name);
        }
        catch (final NameNotFoundException e) {
            logger.error("Could not find name [" + name + "].");
            throw e;
        }
    }


    /**
     * Finds or creates a logger for the test case called tcName with an appender
     * writing to file named <tcName>.log
     *
     * @param tcName
     * @return
     */
    private Logger getTestCaseLogger(String tcName, String sutName, String tcLogDir) {
        FileAppender<ILoggingEvent> fileAppender = appenderMap.get(tcName);
        if (fileAppender == null) {
            logger.debug("create new Appender for " + tcName);
            final LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
            final PatternLayoutEncoder ple = new PatternLayoutEncoder();

            //            ple.setPattern("%date %level [%logger{36}] [%file:%line] %X{testcase}: %msg%n");
            ple.setPattern("[%level] %msg%n");
            ple.setContext(lc);
            ple.start();
            final LocalDateTime ldt = LocalDateTime.now();
            final String formattedMM = String.format("%02d", ldt.getMonthValue());
            final String formatteddd = String.format("%02d", ldt.getDayOfMonth());
            final String formattedhh = String.format("%02d", ldt.getHour());
            final String formattedmm = String.format("%02d", ldt.getMinute());
            final String formattedss = String.format("%02d", ldt.getSecond());
            fileAppender = new FileAppender<>();
            final Date date = new Date();
            SimpleDateFormat sdf;
            sdf = new SimpleDateFormat("ZZZ");
            final String tcLogName = tcName + "-" + ldt.getYear() + "-" + formattedMM + "-" + formatteddd + "T" + formattedhh + formattedmm + formattedss + sdf.format(date) + ".log";
            fileAppender.setFile(tcLogDir + '/' + tcLogName);
            fileAppender.setEncoder(ple);
            fileAppender.setContext(lc);
            fileAppender.start();
            appenderMap.put(tcName, fileAppender);
            tcLogMap.put(tcName, tcLogName);
        }

        final Logger logger = (Logger) LoggerFactory.getLogger(tcName);
        logger.addAppender(fileAppender);
        //set logging level to finest possible
        logger.setLevel(Level.TRACE);

        return logger;
    }


    @Override
    public void onQuit() {
        reportEngine.onQuit();
        System.exit(0);
    }


    /** {@inheritDoc} */
    @Override
    public void onResult(TcResult result) {
        String tcLogName = tcLogMap.get(result.testcase);
        if (tcLogName == null) {
            tcLogName = "test case log file not found";
        }
        else {
            tcLogMap.remove(result.testcase);
        }

        final FileAppender<ILoggingEvent> fileAppender = appenderMap.get(result.testcase);
        if (fileAppender != null) {
            fileAppender.stop();
            appenderMap.remove(result.testcase);
        }

        reportEngine.onResult(result, tcLogName);
    }


    @Override
    public void onStartTestCase(TcInfo info) {
        logger.info("Test Case changed to :" + info.testCaseId);
    }


    @Override
    public void onLogMsg(LogMsg msg) {
        if (msg.tc != null) {
            final SutPathsFiles sutPathsFiles = Factory.getSutPathsFiles();
            final String tcLogDir = sutPathsFiles.getSutLogPathName(msg.sut, msg.badge);
            final Logger log = getTestCaseLogger(msg.tc, msg.sut, tcLogDir);
            final String ds = dateFormatter.format(new Date(msg.time));
            switch (msg.level) {
                case "TRACE":
                    log.trace(ds + ": " + msg.txt);
                    break;
                case "DEBUG":
                    log.debug(ds + ": " + msg.txt);
                    break;
                case "INFO":
                    log.info(ds + ": " + msg.txt);
                    break;
                case "WARN":
                    log.warn(ds + ": " + msg.txt);
                    break;
                case "ERROR":
                    log.error(ds + ": " + msg.txt);
                    break;
            }
        }

    }

}
