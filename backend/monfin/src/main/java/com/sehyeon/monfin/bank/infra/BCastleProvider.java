package com.sehyeon.monfin.bank.infra;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * 
 */
@Configuration // Spring auto detects this file on boot
public class BCastleProvider {

    // PC means After Spring boots and this class is detected,create this method
    @PostConstruct
    public void addBouncyCastleProvider() {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }
    
}
