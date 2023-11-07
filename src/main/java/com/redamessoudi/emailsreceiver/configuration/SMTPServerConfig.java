package com.redamessoudi.emailsreceiver.configuration;

import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.springframework.context.annotation.Configuration;
import org.subethamail.smtp.auth.EasyAuthenticationHandlerFactory;
import org.subethamail.smtp.auth.UsernamePasswordValidator;
import org.subethamail.smtp.helper.SimpleMessageListener;
import org.subethamail.smtp.server.SMTPServer;

/**
 * Custom configuration of SubEtha SMTPServer
 *
 * @author Reda Messoudi
 */
@Configuration
public class SMTPServerConfig {

    private SMTPServer smtpServer;
    private final SimpleMessageListener marketingMsgListener;
    private final UsernamePasswordValidator authValidator;
    private final EasyAuthenticationHandlerFactory easyAuth;
    private SSLContext sslContext;

    public SMTPServerConfig(SimpleMessageListener marketingMsgListener) {
        authValidator = new SimpleAuthValidatorImpl();
        easyAuth = new EasyAuthenticationHandlerFactory(authValidator);
        this.marketingMsgListener = marketingMsgListener; 

        // jbui - addition to create a dummy keystore
        try{            
            InputStream keyStoreIS = this.getClass().getResourceAsStream("/keystore");
            char[] keyStorePassphrase = "subetha".toCharArray();
            KeyStore ksKeys = KeyStore.getInstance("PKCS12");
            ksKeys.load(keyStoreIS, keyStorePassphrase);

            // KeyManager decides which key material to use.
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ksKeys, keyStorePassphrase);


            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), null, null);
        } catch (Exception e) {
            System.out.println("error initializing sslContext");
            e.printStackTrace();
            
        }
    
       // jbui - and added TLS parameters and socket factory
        this.smtpServer = SMTPServer
                .port(4665)
                .simpleMessageListener(this.marketingMsgListener)
                .requireAuth(true)
                .requireTLS(true)
                .enableTLS(true)                  
                .startTlsSocketFactory(sslContext)              
                .authenticationHandlerFactory(easyAuth)
                .build();

       
        this.smtpServer.start();
  
  
    }
}
