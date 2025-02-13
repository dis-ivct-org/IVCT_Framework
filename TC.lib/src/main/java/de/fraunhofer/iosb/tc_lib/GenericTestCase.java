/*******************************************************************************
 * Copyright (C) Her Majesty the Queen in Right of Canada, 
 * as represented by the Minister of National Defence, 2018
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package de.fraunhofer.iosb.tc_lib;

import org.slf4j.Logger;
import org.slf4j.MDC;

import nato.ivct.commander.CmdSendTcStatus;
import nato.ivct.commander.Factory;

public abstract class GenericTestCase {

    private CmdSendTcStatus statusCmd = Factory.createCmdSendTcStatus();

    public void sendTcStatus (String status, int percent) {
        statusCmd.setStatus(status);
        statusCmd.setPercentFinshed(percent);
        statusCmd.setTcName(tcName);
        statusCmd.setSutName(sutName);
        statusCmd.execute();
    }

    private String tcName = null;
    private String sutName = null;
    private String settingsDesignator;
    private String federationName;
    private String sutFederateName;


    /**
     * @param tcParamJson a JSON string containing values to use in the testcase
     * @param logger The {@link Logger} to use
     * @return the IVCT base model to use in the test cases
     * @throws TcInconclusive if test is inconclusive
     */
    protected abstract BaseModel getBaseModel(final String tcParamJson, final Logger logger) throws TcInconclusive;


    /**
     * @param logger The {@link Logger} to use
     */
    protected abstract void logTestPurpose(final Logger logger);


    /**
     * @param logger The {@link Logger} to use
     * @throws TcInconclusive if test is inconclusive
     * @throws TcFailed if test case failed
     */
    protected abstract void performTest(final Logger logger) throws TcInconclusive, TcFailed;


    /**
     * @param logger The {@link Logger} to use
     * @throws TcInconclusive if test is inconclusive
     */
    protected abstract void preambleAction(final Logger logger) throws TcInconclusive;


    /**
     * @param logger The {@link Logger} to use
     * @throws TcInconclusive if test is inconclusive
     */
    protected abstract void postambleAction(final Logger logger) throws TcInconclusive;


    /**
     * @param tcParamJson test case parameters
     * @param logger The {@link Logger} to use
     * @return the verdict
     */
    public IVCT_Verdict execute(final String tcParamJson, final Logger logger) {

        BaseModel baseModel = null;
        IVCT_Verdict ivct_Verdict = new IVCT_Verdict();
        MDC.put("testcase", this.getClass().getSimpleName());

        try {
            baseModel = getBaseModel(tcParamJson, logger);
        } catch (TcInconclusive e) {
            String s = "getBaseModel unsuccessful";
            if (!e.getMessage() .isEmpty()) {
                s = e.getMessage();
            }
            logger.info("TC INCONCLUSIVE {}", s);
            ivct_Verdict.verdict = IVCT_Verdict.Verdict.INCONCLUSIVE;
            ivct_Verdict.text = s;
            return ivct_Verdict;
        }

        sendTcStatus("initiated", 0);

        logTestPurpose(logger);

        // preamble block
        try {
            // Test case phase
            logger.info("TEST CASE PREAMBLE");

            // Publish interaction / object classes
            // Subscribe interaction / object classes

            this.preambleAction(logger);
        }
        catch (final TcInconclusive ex) {
            if (baseModel != null) {
                baseModel.terminateRti();
            }
            logger.info("TC INCONCLUSIVE " + ex.getMessage());
            ivct_Verdict.verdict = IVCT_Verdict.Verdict.INCONCLUSIVE;
            ivct_Verdict.text = ex.getMessage();
            return ivct_Verdict;
        }

        sendTcStatus("started", 0);

        //test body block
        try {
            // Test case phase
            logger.info("TEST CASE BODY");

            // PERFORM TEST
            this.performTest(logger);

        }
        catch (final TcInconclusive ex) {
            if (baseModel != null) {
                baseModel.terminateRti();
            }
            logger.info("TC INCONCLUSIVE " + ex.getMessage());
            ivct_Verdict.verdict = IVCT_Verdict.Verdict.INCONCLUSIVE;
            ivct_Verdict.text = ex.getMessage();
            return ivct_Verdict;
        }
        catch (final TcFailed ex) {
            if (baseModel != null) {
                baseModel.terminateRti();
            }
            logger.info("TC FAILED " + ex.getMessage());
            ivct_Verdict.verdict = IVCT_Verdict.Verdict.FAILED;
            ivct_Verdict.text = ex.getMessage();
            return ivct_Verdict;
        }

        sendTcStatus("done", 99);

        // postamble block
        try {
            // Test case phase
            logger.info("TEST CASE POSTAMBLE");
            this.postambleAction(logger);
            logger.info("TC PASSED");
        }
        catch (final TcInconclusive ex) {
            if (baseModel != null) {
                baseModel.terminateRti();
            }
            logger.info("TC INCONCLUSIVE " + ex.getMessage());
            ivct_Verdict.verdict = IVCT_Verdict.Verdict.INCONCLUSIVE;
            ivct_Verdict.text = ex.getMessage();
            return ivct_Verdict;
        }

        sendTcStatus("finished", 100);

        ivct_Verdict.verdict = IVCT_Verdict.Verdict.PASSED;
        return ivct_Verdict;
    }


    /**
     * Returns the name of the fully qualified class name of the test case
     *
     * @return Test Case Name
     */
    public String getTcName() {
        return tcName;
    }


    public void setTcName(String tcName) {
        this.tcName = tcName;
    }


    /**
     * Returns the name of the System under Test. This may be different from the federate name.
     *
     * @return SutName
     */
    public String getSutName() {
        return sutName;
    }


    /**
     * Set the name of the System under Test.
     *
     * @param sutName Name of the system under test
     */
    public void setSutName(String sutName) {
        this.sutName = sutName;
    }

    /**
     * Set the connection string to be used to connect to the RTI
     *
     * @param settingsDesignator Connection String
     */
    public void setSettingsDesignator(String settingsDesignator) {
        this.settingsDesignator = settingsDesignator;
    }


    public void setFederationName(String federationName) {
        this.federationName = federationName;
    }


    /**
     * Set the name for the SuT federate which is being used in the HLA federation. This value will be set by test case engine
     * and may be used by the test case logic to identify the federate to be tested.
     *
     * @param sutFederateName Federate name of the sut
     */
    public void setSutFederateName(String sutFederateName) {
        this.sutFederateName = sutFederateName;
    }


    /**
     * Returns the federate name of the SuT
     *
     * @return Federate Name of the SuT
     */
    public String getSutFederateName () {
        return this.sutFederateName;
    }
}


