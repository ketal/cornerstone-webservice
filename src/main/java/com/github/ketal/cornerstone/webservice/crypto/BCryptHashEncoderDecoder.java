package com.github.ketal.cornerstone.webservice.crypto;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

public class BCryptHashEncoderDecoder {

    private static final Logger logger = LogManager.getLogger(BCryptHashEncoderDecoder.class);

    //The default log_rounds is 10, and the valid range is 4 to 30.
    private static final int ITERATION_COUNT = 12;

    private BCryptHashEncoderDecoder() {
        throw new IllegalStateException("BCryptHashEncoderDecoder class");
    }
    
    public static String encode(CharSequence rawPassword) {
        return encode(rawPassword, ITERATION_COUNT);
    }
        
    public static String encode(CharSequence rawPassword, int iterations) {
        String salt = BCrypt.gensalt(iterations);
        long startTime = System.nanoTime();
        String hash = BCrypt.hashpw(rawPassword.toString(), salt);
        long endTime = System.nanoTime();
        logger.debug("Encode time: {}", TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS));
        return hash;
    }

    public static boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (encodedPassword == null || encodedPassword.length() == 0) {
            logger.warn("Empty encoded password");
            return false;
        }

        long startTime = System.nanoTime();
        boolean passwordCheck = BCrypt.checkpw(rawPassword.toString(), encodedPassword);
        long endTime = System.nanoTime();
        logger.debug("Encode time: {}", TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS));
        return passwordCheck;
    }
}
