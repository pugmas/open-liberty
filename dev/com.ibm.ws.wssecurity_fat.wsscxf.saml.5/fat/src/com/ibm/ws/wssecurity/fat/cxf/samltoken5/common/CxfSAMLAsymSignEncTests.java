/*******************************************************************************
 * Copyright (c) 2021 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package com.ibm.ws.wssecurity.fat.cxf.samltoken5.common;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.gargoylesoftware.htmlunit.WebClient;
import com.ibm.ws.security.saml20.fat.commonTest.SAMLCommonTest;
import com.ibm.ws.security.saml20.fat.commonTest.SAMLCommonTestHelpers;
import com.ibm.ws.security.saml20.fat.commonTest.SAMLConstants;
import com.ibm.ws.security.saml20.fat.commonTest.SAMLTestSettings;

import componenttest.annotation.SkipForRepeat;

import componenttest.annotation.AllowedFFDC;
import componenttest.annotation.ExpectedFFDC;
import componenttest.custom.junit.runner.FATRunner;
import componenttest.custom.junit.runner.Mode;
import componenttest.custom.junit.runner.Mode.TestMode;
import componenttest.topology.impl.LibertyServerWrapper;
import static componenttest.annotation.SkipForRepeat.EE8_FEATURES;
import static componenttest.annotation.SkipForRepeat.EE9_FEATURES;
import static componenttest.annotation.SkipForRepeat.NO_MODIFICATION;
import componenttest.rules.repeater.EmptyAction;
import componenttest.rules.repeater.EE8FeatureReplacementAction;


/**
 * The testcases in this class were ported from tWAS' test SamlWebSSOTests.
 * If a tWAS test is not applicable, it will be noted in the comments below.
 * If a tWAS test fits better into another test class, it will be noted
 * which test project/class it now resides in.
 * In general, these tests perform a simple IdP initiated SAML Web SSO, using
 * httpunit to simulate browser requests. In this scenario, a Web client
 * accesses a static Web page on IdP and obtains a a SAML HTTP-POST link
 * to an application installed on a WebSphere SP. When the Web client
 * invokes the SP application, it is redirected to a TFIM IdP which issues
 * a login challenge to the Web client. The Web Client fills in the login
 * form and after a successful login, receives a SAML 2.0 token from the
 * TFIM IdP. The client invokes the SP application by sending the SAML
 * 2.0 token in the HTTP POST request.
 */

@SkipForRepeat({ EE9_FEATURES })
@LibertyServerWrapper
@Mode(TestMode.FULL)
@RunWith(FATRunner.class)
public class CxfSAMLAsymSignEncTests extends SAMLCommonTest {

    private static final Class<?> thisClass = CxfSAMLAsymSignEncTests.class;
    protected static String servicePort = null;
    protected static String serviceSecurePort = null;
    protected static CXFSAMLCommonUtils commonUtils = new CXFSAMLCommonUtils();

    /**
     * TestDescription:
     * 
     * This test invokes a jax-ws cxf service client, which invokes
     * a jax-ws cxf SAML web service.
     * Service client passes a flag with a value of null to indicate that a
     * user/password should be included in the service context. This is NOT
     * needed or used as we've specified SAML in the policy. Unlike using
     * usernametoken which doesn't require it either, in this case, it won't
     * even be looked at.
     * Test should succeed in accessing the server side service.
     * 
     */
    
    @SkipForRepeat({ EE8_FEATURES })
    @Test
    public void testSAMLCXFSignedSupportingTokens_AsymmmetricEE7Only() throws Exception {
        
    	WebClient webClient = SAMLCommonTestHelpers.getWebClient();

        SAMLTestSettings updatedTestSettings = testSettings.copyTestSettings();
        updatedTestSettings.updatePartnerInSettings("sp1", true);
        updatedTestSettings.setCXFSettings(_testName, null, servicePort, null, null, null, "SAMLAsymSignService",
                "SAMLAsymSignPort", "", "False", null, null);

        genericSAML(_testName, webClient, updatedTestSettings, standardFlow, helpers.setDefaultGoodSAMLCXFExpectations(null, flowType, updatedTestSettings, SAMLConstants.CXF_SAML_TOKEN_ASYM_SIGN_SERVICE));

    }

    @SkipForRepeat({ NO_MODIFICATION })
    @AllowedFFDC(value = { "java.net.MalformedURLException" }, repeatAction = { EE8FeatureReplacementAction.ID })
    @Test
    public void testSAMLCXFSignedSupportingTokens_AsymmmetricEE8Only() throws Exception {

    	if (testSAMLServer2 == null) {
            //1 server reconfig
    		testSAMLServer.reconfigServer(buildSPServerName("server_2_in_1_AsymSignEnc_wss4j.xml"), _testName, SAMLConstants.NO_EXTRA_MSGS, SAMLConstants.JUNIT_REPORTING);
    	} else {
    		//2 servers reconfig
    		testSAMLServer2.reconfigServer("server_2_AsymSignEnc_wss4j.xml", _testName, SAMLConstants.NO_EXTRA_MSGS, SAMLConstants.JUNIT_REPORTING);
    		testSAMLServer.reconfigServer("server_1_AsymSignEnc_wss4j.xml", _testName, SAMLConstants.NO_EXTRA_MSGS, SAMLConstants.JUNIT_REPORTING);
    	} 
    	
        WebClient webClient = SAMLCommonTestHelpers.getWebClient();

        SAMLTestSettings updatedTestSettings = testSettings.copyTestSettings();
        updatedTestSettings.updatePartnerInSettings("sp1", true);
        updatedTestSettings.setCXFSettings(_testName, null, servicePort, null, null, null, "SAMLAsymSignService",
                "SAMLAsymSignPort", "", "False", null, null);

        genericSAML(_testName, webClient, updatedTestSettings, standardFlow, helpers.setDefaultGoodSAMLCXFExpectations(null, flowType, updatedTestSettings, SAMLConstants.CXF_SAML_TOKEN_ASYM_SIGN_SERVICE));

    }
    
    /*
     * In the test, the server side policy requires that the request be signed.
     * The client uses a policy that does not specify that the request be signed
     * The test should fail since the policy can not be satisfied
     */
    
    @SkipForRepeat({ EE8_FEATURES })
    @ExpectedFFDC(value = { "org.apache.ws.security.WSSecurityException" }, repeatAction = { EmptyAction.ID })
    @Test
    public void testSAMLCXFSignedSupportingTokens_Asymmmetric_ClientNotSignedEE7Only() throws Exception {
        
    	WebClient webClient = SAMLCommonTestHelpers.getWebClient();

        SAMLTestSettings updatedTestSettings = testSettings.copyTestSettings();
        updatedTestSettings.updatePartnerInSettings("sp1", true);
        updatedTestSettings.setCXFSettings(_testName, null, servicePort, null, null, null, "SAMLAsymSignService",
                "SAMLAsymSignPort", "", "False", null, commonUtils.processClientWsdl("ClientAsymOmitSign.wsdl", servicePort));

        genericSAML(_testName, webClient, updatedTestSettings, standardFlow, helpers.setErrorSAMLCXFExpectations(null, flowType, updatedTestSettings, SAMLConstants.CXF_SAML_TOKEN_SYM_SIGN_SERVICE_CLIENT_NOT_SIGN));

    }
    
    @SkipForRepeat({ NO_MODIFICATION })
    @AllowedFFDC(value = { "java.net.MalformedURLException" }, repeatAction = { EE8FeatureReplacementAction.ID })
    @Test
    public void testSAMLCXFSignedSupportingTokens_Asymmmetric_ClientNotSignedEE8Only() throws Exception {

    	if (testSAMLServer2 == null) {
            //1 server reconfig
    		testSAMLServer.reconfigServer(buildSPServerName("server_2_in_1_AsymSignEnc_wss4j.xml"), _testName, SAMLConstants.NO_EXTRA_MSGS, SAMLConstants.JUNIT_REPORTING);
    	} else {
    		//2 servers reconfig
    		testSAMLServer2.reconfigServer("server_2_AsymSignEnc_wss4j.xml", _testName, SAMLConstants.NO_EXTRA_MSGS, SAMLConstants.JUNIT_REPORTING);
    		testSAMLServer.reconfigServer("server_1_AsymSignEnc_wss4j.xml", _testName, SAMLConstants.NO_EXTRA_MSGS, SAMLConstants.JUNIT_REPORTING);
    	} 
    	
        WebClient webClient = SAMLCommonTestHelpers.getWebClient();

        SAMLTestSettings updatedTestSettings = testSettings.copyTestSettings();
        updatedTestSettings.updatePartnerInSettings("sp1", true);
        updatedTestSettings.setCXFSettings(_testName, null, servicePort, null, null, null, "SAMLAsymSignService",
                "SAMLAsymSignPort", "", "False", null, commonUtils.processClientWsdl("ClientAsymOmitSign.wsdl", servicePort));

        //@AV999, the client sends a signed supporting token even though the policy says supporting token? is this just with saml token or any supporting token? TODO
        genericSAML(_testName, webClient, updatedTestSettings, standardFlow, helpers.setDefaultGoodSAMLCXFExpectations(null, flowType, updatedTestSettings, SAMLConstants.CXF_SAML_TOKEN_ASYM_SIGN_SERVICE));

    }

    @SkipForRepeat({ EE8_FEATURES })
    @Test
    public void testSAMLCXFEncryptedSupportingTokens_AsymmmetricEE7Only() throws Exception {
        
    	WebClient webClient = SAMLCommonTestHelpers.getWebClient();

        SAMLTestSettings updatedTestSettings = testSettings.copyTestSettings();
        updatedTestSettings.updatePartnerInSettings("sp1", true);
        updatedTestSettings.setCXFSettings(_testName, null, servicePort, null, null, null, "SAMLAsymEncrService",
                "SAMLAsymEncrPort", "", "False", null, null);

        genericSAML(_testName, webClient, updatedTestSettings, standardFlow, helpers.setDefaultGoodSAMLCXFExpectations(null, flowType, updatedTestSettings, SAMLConstants.CXF_SAML_TOKEN_ASYM_ENCR_SERVICE));

    }

    @SkipForRepeat({ NO_MODIFICATION })
    @AllowedFFDC(value = { "java.net.MalformedURLException" }, repeatAction = { EE8FeatureReplacementAction.ID })
    @Test
    public void testSAMLCXFEncryptedSupportingTokens_AsymmmetricEE8Only() throws Exception {
    
    	if (testSAMLServer2 == null) {
            //1 server reconfig
    		testSAMLServer.reconfigServer(buildSPServerName("server_2_in_1_AsymSignEnc_wss4j.xml"), _testName, SAMLConstants.NO_EXTRA_MSGS, SAMLConstants.JUNIT_REPORTING);
    	} else {
    		//2 servers reconfig
    		testSAMLServer2.reconfigServer("server_2_AsymSignEnc_wss4j.xml", _testName, SAMLConstants.NO_EXTRA_MSGS, SAMLConstants.JUNIT_REPORTING);
    		testSAMLServer.reconfigServer("server_1_AsymSignEnc_wss4j.xml", _testName, SAMLConstants.NO_EXTRA_MSGS, SAMLConstants.JUNIT_REPORTING);
    	} 
    	
        WebClient webClient = SAMLCommonTestHelpers.getWebClient();

        SAMLTestSettings updatedTestSettings = testSettings.copyTestSettings();
        updatedTestSettings.updatePartnerInSettings("sp1", true);
        updatedTestSettings.setCXFSettings(_testName, null, servicePort, null, null, null, "SAMLAsymEncrService",
                "SAMLAsymEncrPort", "", "False", null, null);

        genericSAML(_testName, webClient, updatedTestSettings, standardFlow, helpers.setDefaultGoodSAMLCXFExpectations(null, flowType, updatedTestSettings, SAMLConstants.CXF_SAML_TOKEN_ASYM_ENCR_SERVICE));

    }
    
    /*
     * In the test, the server side policy requires that the request be encrypted.
     * The client uses a policy that does not specify that the request be encrypted
     * The test should fail since the policy can not be satisfied
     */
    
    @SkipForRepeat({ EE8_FEATURES })
    @ExpectedFFDC(value = { "org.apache.ws.security.WSSecurityException" }, repeatAction = { EmptyAction.ID })
    @Test
    public void testSAMLCXFEncryptedSupportingTokens_Asymmmetric_ClientNotEncryptedEE7Only() throws Exception {
        
    	WebClient webClient = SAMLCommonTestHelpers.getWebClient();

        SAMLTestSettings updatedTestSettings = testSettings.copyTestSettings();
        updatedTestSettings.updatePartnerInSettings("sp1", true);
        updatedTestSettings.setCXFSettings(_testName, null, servicePort, null, null, null, "SAMLAsymEncrService",
                "SAMLAsymEncrPort", "", "False", null, commonUtils.processClientWsdl("ClientAsymOmitEncr.wsdl", servicePort));

        genericSAML(_testName, webClient, updatedTestSettings, standardFlow, helpers.setDefaultGoodSAMLCXFExpectations(null, flowType, updatedTestSettings, SAMLConstants.CXF_SAML_TOKEN_SYM_ENCR_SERVICE_CLIENT_NOT_ENCR));

    }

    @SkipForRepeat({ NO_MODIFICATION })
    @AllowedFFDC(value = { "java.net.MalformedURLException" }, repeatAction = { EE8FeatureReplacementAction.ID }) 
    @Test
    public void testSAMLCXFEncryptedSupportingTokens_Asymmmetric_ClientNotEncryptedEE8Only() throws Exception {

    	if (testSAMLServer2 == null) {
            //1 server reconfig
    		testSAMLServer.reconfigServer(buildSPServerName("server_2_in_1_AsymSignEnc_wss4j.xml"), _testName, SAMLConstants.NO_EXTRA_MSGS, SAMLConstants.JUNIT_REPORTING);
    	} else {
    		//2 servers reconfig
    	    testSAMLServer2.reconfigServer("server_2_AsymSignEnc_wss4j.xml", _testName, SAMLConstants.NO_EXTRA_MSGS, SAMLConstants.JUNIT_REPORTING);
    		testSAMLServer.reconfigServer("server_1_AsymSignEnc_wss4j.xml", _testName, SAMLConstants.NO_EXTRA_MSGS, SAMLConstants.JUNIT_REPORTING);
    	} 
    	
        WebClient webClient = SAMLCommonTestHelpers.getWebClient();

        SAMLTestSettings updatedTestSettings = testSettings.copyTestSettings();
        updatedTestSettings.updatePartnerInSettings("sp1", true);
        updatedTestSettings.setCXFSettings(_testName, null, servicePort, null, null, null, "SAMLAsymEncrService",
                "SAMLAsymEncrPort", "", "False", null, commonUtils.processClientWsdl("ClientAsymOmitEncr.wsdl", servicePort));

        genericSAML(_testName, webClient, updatedTestSettings, standardFlow, helpers.setDefaultGoodSAMLCXFExpectations(null, flowType, updatedTestSettings, SAMLConstants.CXF_SAML_TOKEN_SYM_ENCR_SERVICE_CLIENT_NOT_ENCR));

    }
    
    @SkipForRepeat({ EE8_FEATURES })
    @Test
    public void testSAMLCXFSignedEncryptedSupportingTokens_AsymmmetricEE7Only() throws Exception {

        WebClient webClient = SAMLCommonTestHelpers.getWebClient();

        SAMLTestSettings updatedTestSettings = testSettings.copyTestSettings();
        updatedTestSettings.updatePartnerInSettings("sp1", true);
        updatedTestSettings.setCXFSettings(_testName, null, servicePort, null, null, null, "SAMLAsymSignEncrService",
                "SAMLAsymSignEncrPort", "", "False", null, null);

        genericSAML(_testName, webClient, updatedTestSettings, standardFlow, helpers.setDefaultGoodSAMLCXFExpectations(null, flowType, updatedTestSettings, SAMLConstants.CXF_SAML_TOKEN_ASYM_SIGN_ENCR_SERVICE));

    }

    @SkipForRepeat({ NO_MODIFICATION })
    @AllowedFFDC(value = { "java.net.MalformedURLException" }, repeatAction = { EE8FeatureReplacementAction.ID })
    @Test
    public void testSAMLCXFSignedEncryptedSupportingTokens_AsymmmetricEE8Only() throws Exception {
    	
    	if (testSAMLServer2 == null) {
            //1 server reconfig
    		testSAMLServer.reconfigServer(buildSPServerName("server_2_in_1_AsymSignEnc_wss4j.xml"), _testName, SAMLConstants.NO_EXTRA_MSGS, SAMLConstants.JUNIT_REPORTING);
    	} else {
    		//2 servers reconfig
    	    testSAMLServer2.reconfigServer("server_2_AsymSignEnc_wss4j.xml", _testName, SAMLConstants.NO_EXTRA_MSGS, SAMLConstants.JUNIT_REPORTING);
    		testSAMLServer.reconfigServer("server_1_AsymSignEnc_wss4j.xml", _testName, SAMLConstants.NO_EXTRA_MSGS, SAMLConstants.JUNIT_REPORTING);
    	} 
    	
        WebClient webClient = SAMLCommonTestHelpers.getWebClient();

        SAMLTestSettings updatedTestSettings = testSettings.copyTestSettings();
        updatedTestSettings.updatePartnerInSettings("sp1", true);
        updatedTestSettings.setCXFSettings(_testName, null, servicePort, null, null, null, "SAMLAsymSignEncrService",
                "SAMLAsymSignEncrPort", "", "False", null, null);

        genericSAML(_testName, webClient, updatedTestSettings, standardFlow, helpers.setDefaultGoodSAMLCXFExpectations(null, flowType, updatedTestSettings, SAMLConstants.CXF_SAML_TOKEN_ASYM_SIGN_ENCR_SERVICE));

    }
    
    /*
     * In the test, the server side policy requires that the request be encrypted.
     * The client uses a policy that does not specify that the request be encrypted
     * The test should fail since the policy can not be satisfied
     */
    
    @SkipForRepeat({ EE8_FEATURES })
    @ExpectedFFDC(value = { "org.apache.ws.security.WSSecurityException" }, repeatAction = { EmptyAction.ID })
    @Test
    public void testSAMLCXFSignedEncryptedSupportingTokens_Asymmmetric_ClientNotEncryptedEE7Only() throws Exception {
        WebClient webClient = SAMLCommonTestHelpers.getWebClient();

        SAMLTestSettings updatedTestSettings = testSettings.copyTestSettings();
        updatedTestSettings.updatePartnerInSettings("sp1", true);
        updatedTestSettings.setCXFSettings(_testName, null, servicePort, null, null, null, "SAMLAsymSignEncrService",
                "SAMLAsymSignEncrPort", "", "False", null, commonUtils.processClientWsdl("ClientAsymOmitEncrKeepSign.wsdl", servicePort));

        genericSAML(_testName, webClient, updatedTestSettings, standardFlow, helpers.setDefaultGoodSAMLCXFExpectations(null, flowType, updatedTestSettings, SAMLConstants.CXF_SAML_TOKEN_SYM_SIGN_ENCR_SERVICE_CLIENT_NOT_SIGN_OR_ENCR));

    }

    @SkipForRepeat({ NO_MODIFICATION })
    @AllowedFFDC(value = { "java.net.MalformedURLException" }, repeatAction = { EE8FeatureReplacementAction.ID })
    @Test
    public void testSAMLCXFSignedEncryptedSupportingTokens_Asymmmetric_ClientNotEncryptedEE8Only() throws Exception {

    	if (testSAMLServer2 == null) {
            //1 server reconfig
    		testSAMLServer.reconfigServer(buildSPServerName("server_2_in_1_AsymSignEnc_wss4j.xml"), _testName, SAMLConstants.NO_EXTRA_MSGS, SAMLConstants.JUNIT_REPORTING);
    	} else {
    		//2 servers reconfig
    		testSAMLServer2.reconfigServer("server_2_AsymSignEnc_wss4j.xml", _testName, SAMLConstants.NO_EXTRA_MSGS, SAMLConstants.JUNIT_REPORTING);
    		testSAMLServer.reconfigServer("server_1_AsymSignEnc_wss4j.xml", _testName, SAMLConstants.NO_EXTRA_MSGS, SAMLConstants.JUNIT_REPORTING);
    	}
    	
        WebClient webClient = SAMLCommonTestHelpers.getWebClient();

        SAMLTestSettings updatedTestSettings = testSettings.copyTestSettings();
        updatedTestSettings.updatePartnerInSettings("sp1", true);
        updatedTestSettings.setCXFSettings(_testName, null, servicePort, null, null, null, "SAMLAsymSignEncrService",
                "SAMLAsymSignEncrPort", "", "False", null, commonUtils.processClientWsdl("ClientAsymOmitEncrKeepSign.wsdl", servicePort));

        genericSAML(_testName, webClient, updatedTestSettings, standardFlow, helpers.setDefaultGoodSAMLCXFExpectations(null, flowType, updatedTestSettings, SAMLConstants.CXF_SAML_TOKEN_SYM_SIGN_ENCR_SERVICE_CLIENT_NOT_SIGN_OR_ENCR));

    }
    
    /*
     * In the test, the server side policy requires that the request be encrypted.
     * The client uses a policy that does not specify that the request be encrypted
     * The test should fail since the policy can not be satisfied
     */
    
    @SkipForRepeat({ EE8_FEATURES })
    @ExpectedFFDC(value = { "org.apache.ws.security.WSSecurityException" }, repeatAction = { EmptyAction.ID })
    @Test
    public void testSAMLCXFSignedEncryptedSupportingTokens_Asymmmetric_ClientNotSignedEE7Only() throws Exception {
        
    	WebClient webClient = SAMLCommonTestHelpers.getWebClient();

        SAMLTestSettings updatedTestSettings = testSettings.copyTestSettings();
        updatedTestSettings.updatePartnerInSettings("sp1", true);
        updatedTestSettings.setCXFSettings(_testName, null, servicePort, null, null, null, "SAMLAsymSignEncrService",
                "SAMLAsymSignEncrPort", "", "False", null, commonUtils.processClientWsdl("ClientAsymOmitSignKeepEncr.wsdl", servicePort));

        genericSAML(_testName, webClient, updatedTestSettings, standardFlow, helpers.setDefaultGoodSAMLCXFExpectations(null, flowType, updatedTestSettings, SAMLConstants.CXF_SAML_TOKEN_SYM_SIGN_ENCR_SERVICE_CLIENT_NOT_SIGN_OR_ENCR));

    }

    @SkipForRepeat({ NO_MODIFICATION })
    @AllowedFFDC(value = { "java.net.MalformedURLException" }, repeatAction = { EE8FeatureReplacementAction.ID })
    @Test
    public void testSAMLCXFSignedEncryptedSupportingTokens_Asymmmetric_ClientNotSignedEE8Only() throws Exception {

    	if (testSAMLServer2 == null) {
            //1 server reconfig
    		testSAMLServer.reconfigServer(buildSPServerName("server_2_in_1_AsymSignEnc_wss4j.xml"), _testName, SAMLConstants.NO_EXTRA_MSGS, SAMLConstants.JUNIT_REPORTING);
    	} else {
    		//2 servers reconfig
    	    testSAMLServer2.reconfigServer("server_2_AsymSignEnc_wss4j.xml", _testName, SAMLConstants.NO_EXTRA_MSGS, SAMLConstants.JUNIT_REPORTING);
    		testSAMLServer.reconfigServer("server_1_AsymSignEnc_wss4j.xml", _testName, SAMLConstants.NO_EXTRA_MSGS, SAMLConstants.JUNIT_REPORTING);
    	} 
    	
        WebClient webClient = SAMLCommonTestHelpers.getWebClient();

        SAMLTestSettings updatedTestSettings = testSettings.copyTestSettings();
        updatedTestSettings.updatePartnerInSettings("sp1", true);
        updatedTestSettings.setCXFSettings(_testName, null, servicePort, null, null, null, "SAMLAsymSignEncrService",
                "SAMLAsymSignEncrPort", "", "False", null, commonUtils.processClientWsdl("ClientAsymOmitSignKeepEncr.wsdl", servicePort));

        //@AV999 TODO need to check whether the new runtime is correct in implementing the policy
        genericSAML(_testName, webClient, updatedTestSettings, standardFlow, helpers.setDefaultGoodSAMLCXFExpectations(null, flowType, updatedTestSettings, SAMLConstants.CXF_SAML_TOKEN_ASYM_SIGN_ENCR_SERVICE));

    }
    
    @SkipForRepeat({ EE8_FEATURES })
    @Test
    public void testSAMLCXFSignedEncryptedAsyncSupportingTokens_AsymmmetricEE7Only() throws Exception {
        WebClient webClient = SAMLCommonTestHelpers.getWebClient();

        SAMLTestSettings updatedTestSettings = testSettings.copyTestSettings();
        updatedTestSettings.updatePartnerInSettings("sp1", true);
        updatedTestSettings.setCXFSettings(_testName, null, servicePort, null, null, null, "SAMLAsyncX509Service",
                "SAMLAsyncX509Port", "", "False", null, null);

        genericSAML(_testName, webClient, updatedTestSettings, standardFlow, helpers.setDefaultGoodSAMLCXFExpectations(null, flowType, updatedTestSettings, SAMLConstants.CXF_SAML_TOKEN_ASYNC_ENCR_SERVICE));

    }
    
    @SkipForRepeat({ NO_MODIFICATION })
    @AllowedFFDC(value = { "java.net.MalformedURLException" }, repeatAction = { EE8FeatureReplacementAction.ID })
    @Test
    public void testSAMLCXFSignedEncryptedAsyncSupportingTokens_AsymmmetricEE8Only() throws Exception {

    	if (testSAMLServer2 == null) {
            //1 server reconfig
    		testSAMLServer.reconfigServer(buildSPServerName("server_2_in_1_AsymSignEnc_wss4j.xml"), _testName, SAMLConstants.NO_EXTRA_MSGS, SAMLConstants.JUNIT_REPORTING);
    	} else {
    		//2 servers reconfig
    		testSAMLServer2.reconfigServer("server_2_AsymSignEnc_wss4j.xml", _testName, SAMLConstants.NO_EXTRA_MSGS, SAMLConstants.JUNIT_REPORTING);
    		testSAMLServer.reconfigServer("server_1_AsymSignEnc_wss4j.xml", _testName, SAMLConstants.NO_EXTRA_MSGS, SAMLConstants.JUNIT_REPORTING);
    	} 
    	
        WebClient webClient = SAMLCommonTestHelpers.getWebClient();

        SAMLTestSettings updatedTestSettings = testSettings.copyTestSettings();
        updatedTestSettings.updatePartnerInSettings("sp1", true);
        updatedTestSettings.setCXFSettings(_testName, null, servicePort, null, null, null, "SAMLAsyncX509Service",
                "SAMLAsyncX509Port", "", "False", null, null);

        genericSAML(_testName, webClient, updatedTestSettings, standardFlow, helpers.setDefaultGoodSAMLCXFExpectations(null, flowType, updatedTestSettings, SAMLConstants.CXF_SAML_TOKEN_ASYNC_ENCR_SERVICE));

    }

}
