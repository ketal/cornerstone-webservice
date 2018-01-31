package com.github.ketal.cornerstone.webservice.crypto;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BCryptHashEncoderDecoderTest {

    private final static Logger logger = LogManager.getLogger(BCryptHashEncoderDecoderTest.class);
    
    private final static String TEST_STRING = "BCryptEncoderDecoderTest";
    
    @Test
    public void encodeDecodeTest() {
        String encodedString = BCryptHashEncoderDecoder.encode(TEST_STRING);
        logger.debug("Encoded String: {}->{}", encodedString.length(), encodedString);
        Assertions.assertTrue(BCryptHashEncoderDecoder.matches(TEST_STRING, encodedString));
    }
}
