package cz.peinlich.escrow;

import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.Wallet;
import cz.peinlich.configuration.EscrowConfiguration;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigInteger;

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
    Merchant merchant;

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
     *
     * im using this faucet to get some coins: http://testnet.mojocoin.com/
     */
    @Test
    public void synchronizeWithNetwork(){
        DateTime start = new DateTime();
        logger.info("Syncing buyer. Started at {}", start);
        buyer.start();
        DateTime buyerComplete = new DateTime();
        logger.info("Buyer synced. It took {}.", new Duration(start,buyerComplete));

        logger.info("Syncing merchant.");
        merchant.start();
        DateTime merchantComplete = new DateTime();
        logger.info("merchant synced. It took {}.", new Duration(buyerComplete,merchantComplete));

        logger.info("Syncing Escrow");
        escrow.start();
        DateTime escrowComplete = new DateTime();
        logger.info("Escrow synced. It took {}.", new Duration(merchantComplete,escrowComplete));

        logger.info("Sync complete. Alltogether it took {}.", new Duration(start,escrowComplete));
    }


    /**
     * This case is the thing! it creates escrow transaction and later merchant sends
     * transaction to himself that spends the escrow transaction.
     */
    @Test
    public void buyermerchantAgreeScenario(){
        buyer.start();
        merchant.start();
        escrow.start();


        logger.info("merchant balance: {}", merchant.kit.wallet().getBalance());

        Wallet wallet = buyer.kit.wallet();
        BigInteger balance = wallet.getBalance();

        if (BigInteger.ZERO.equals(balance)){
            logger.info("No balance on buyer");
            logger.info("Using wallet: {}", wallet);
            throw new RuntimeException("No balance on buyer");
        }

        market.match(buyer, merchant);

    }


    /**
     * Use this case to send all merchants funds back to buyer.
     */
    @Test
    public void sendSendersFundsBackToBuyer(){
        buyer.start();
        merchant.start();


        Wallet wallet = merchant.kit.wallet();
        BigInteger balance = wallet.getBalance();

        if (BigInteger.ZERO.equals(balance)){
            logger.info("not much to send, try again later?");
            logger.info("No balance on merchant");
            logger.info("Using wallet: {}", wallet);
            throw new RuntimeException("No balance on merchant");
        }

        final BigInteger amountToSend = balance.subtract(Transaction.REFERENCE_DEFAULT_MIN_TX_FEE);
        wallet.sendCoins(merchant.kit.peerGroup(),buyer.kit.wallet().getKeys().get(0).toAddress(merchant.kit.params()),amountToSend);

    }
}
