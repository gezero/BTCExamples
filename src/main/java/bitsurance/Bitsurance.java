package bitsurance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * User: George
 * Date: 27.10.13
 * Time: 7:28
 */
@Component
public class Bitsurance {
    @Autowired
    BitsuranceNetworkParameters bitsuranceNetworkParameters;

    private static final Logger logger = LoggerFactory.getLogger(Bitsurance.class);

    public int test() {
        logger.info(bitsuranceNetworkParameters.getFilePrefix());
        return 1;
    }
}
