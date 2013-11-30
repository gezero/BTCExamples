package cz.peinlich.escrow;

import cz.peinlich.configuration.EscrowConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
    Market market;


    @Test
    public void buyerSellerAgreeScenario(){

        market.match(buyer,seller);

    }
}
