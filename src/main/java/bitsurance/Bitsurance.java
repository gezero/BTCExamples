package bitsurance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * User: George
 * Date: 27.10.13
 * Time: 7:28
 */
@Component
public class Bitsurance {
    private static final Logger logger = LoggerFactory.getLogger(Bitsurance.class);

    public int test(){
        logger.info("testing");
        return 1;
    }
}
