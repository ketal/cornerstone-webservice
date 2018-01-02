package com.github.ketal.webservice.crypto;


import java.security.GeneralSecurityException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.github.ketal.webservice.crypto.PBKDF2HashEncoderDecoder;

public class PBKDF2HashEncoderDecoderTest {

    private final static Logger logger = LogManager.getLogger(PBKDF2HashEncoderDecoderTest.class);
    
    private final static String TEST_STRING = "PBKDF2HashEncoderDecoderTest";
    
    @Test
    public void encodeDecodeTest() throws GeneralSecurityException {
        String encodedString = PBKDF2HashEncoderDecoder.encode(TEST_STRING);
        logger.debug("Encoded String: {}->{}", encodedString.length(), encodedString);
        Assert.assertTrue(PBKDF2HashEncoderDecoder.matches(TEST_STRING, encodedString));
    }
    
    @Test
    public void encodeDecodeWithIterationCountTest() throws GeneralSecurityException {
        String encodedString = PBKDF2HashEncoderDecoder.encode(TEST_STRING, 100000);
        logger.debug("Encoded String: {}", encodedString);
        Assert.assertTrue(PBKDF2HashEncoderDecoder.matches(TEST_STRING, encodedString));
    }
}
