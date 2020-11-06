/*******************************************************************************
 * Copyright (c) 2020 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package test.wssecfvt.x509sig;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 2.6.2
 * 2012-12-10T17:58:03.292-06:00
 * Generated source version: 2.6.2
 * 
 */
@WebServiceClient(name = "X509XmlStrService2", 
                  wsdlLocation = "X509StrTypes.wsdl",
                  targetNamespace = "http://x509sig.wssecfvt.test") 
public class X509XmlStrService2 extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://x509sig.wssecfvt.test", "X509XmlStrService2");
    public final static QName UrnX509Str2 = new QName("http://x509sig.wssecfvt.test", "UrnX509Str2");
    static {
        URL url = X509XmlSigService5.class.getResource("X509StrTypes.wsdl");
        if (url == null) {
            java.util.logging.Logger.getLogger(X509XmlStrService2.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}", "X509StrTypes.wsdl");
        }       
        WSDL_LOCATION = url;
    }

    public X509XmlStrService2(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public X509XmlStrService2(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public X509XmlStrService2() {
        super(WSDL_LOCATION, SERVICE);
    }
    
    /**
     *
     * @return
     *     returns X509XmlSig
     */
    @WebEndpoint(name = "UrnX509Str2")
    public X509XmlSig getUrnX509Str2() {
        return super.getPort(UrnX509Str2, X509XmlSig.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns X509XmlSig
     */
    @WebEndpoint(name = "UrnX509Str2")
    public X509XmlSig getUrnX509Str2(WebServiceFeature... features) {
        return super.getPort(UrnX509Str2, X509XmlSig.class, features);
    }

}

