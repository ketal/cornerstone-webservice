package com.github.ketal.webservice.crypto;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.github.ketal.webservice.crypto.BCryptHashEncoderDecoderTest;

public class BCryptHashEncoderDecoderTest {

    private final static Logger logger = LogManager.getLogger(BCryptHashEncoderDecoderTest.class);
    
    private final static String TEST_STRING = "BCryptEncoderDecoderTest";
    
    @Test
    public void encodeDecodeTest() {
        String encodedString = BCryptHashEncoderDecoder.encode(TEST_STRING);
        logger.debug("Encoded String: {}->{}", encodedString.length(), encodedString);
        Assert.assertTrue(BCryptHashEncoderDecoder.matches(TEST_STRING, encodedString));
    }
}
