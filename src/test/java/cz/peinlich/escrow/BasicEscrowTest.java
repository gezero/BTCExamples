package cz.peinlich.escrow;

import cz.peinlich.configuration.EscrowConfiguration;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

/**
 * User: George
 * Date: 30.11.13
 * Time: 8:50
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {EscrowConfiguration.class})
public class BasicEscrowTest {
    private static final Logger logger = LoggerFactory.getLogger(BasicEscrowTest.class);

    @Autowired
    Buyer buyer;

    @Autowired
    Seller seller;

    @Autowired
    Escrow escrow;

    @Autowired
    Market market;

    /**
     * Run this test first, this will sync each actors blockchain info.
     * It will take forever, but it will be saved to the disk and need to be done "only once" for each actor :P
     *
     * After the sync finishes, the test stops and you can play with the other tests, if you will not run this one first
     * the sync will be done in some other test and you will just not know when it finished.
     *
     * There will be always some sync on the beginning of test that is syncing new blocks. You can check that everything is
     * synced by running this test for second time. It will end much much much faster.
     *
     *
     */
    @Test
    public void synchronizeWithNetwork(){
        DateTime start = new DateTime();
        logger.info("Syncing buyer. Started at {}", start);
        buyer.start();
        DateTime buyerComplete = new DateTime();
        logger.info("Buyer synced. It took {}.", new Duration(start,buyerComplete));

        logger.info("Syncing Seller.");
        seller.start();
        DateTime sellerComplete = new DateTime();
        logger.info("Seller synced. It took {}.", new Duration(buyerComplete,sellerComplete));

        logger.info("Syncing Escrow");
        escrow.start();
        DateTime escrowComplete = new DateTime();
        logger.info("Escrow synced. It took {}.", new Duration(sellerComplete,escrowComplete));

        logger.info("Sync complete. Alltogether it took {}.", new Duration(start,escrowComplete));
    }


    @Test
    public void buyerSellerAgreeScenario(){

        market.match(buyer,seller);

    }
}
