package cz.peinlich.bitsurance;

import cz.peinlich.configuration.BitsuranceConfiguration;
import com.google.bitcoin.core.*;
import com.google.bitcoin.kits.WalletAppKit;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigInteger;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * User: George
 * Date: 27.10.13
 * Time: 7:42
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {BitsuranceConfiguration.class})
public class BitsuranceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(BitsuranceTest.class);
    @Autowired
    Bitsurance bitsurance;
    @Autowired
    NetworkParameters networkParameters;
    @Autowired
    @Qualifier("workingDirectory")
    String workingDirectory;
    @Autowired
    @Qualifier("testNetReturnAddress")
    String returnAddress;
    @Autowired
    WalletAppKit kit;
    Address forwardingAddress = null;

    final Object mon;

    public BitsuranceTest() {
        mon = this;
    }

    @Before
    public void createForwardingAddress() throws AddressFormatException {
        forwardingAddress = new Address(networkParameters, returnAddress);
    }

    @Test
    public void firstTestNetTest() throws AddressFormatException {
        bitsurance.start();
        assertThat(bitsurance.test(), is(1));
    }

    @Test
    public void firstWalletTest() throws InterruptedException {

        bitsurance.start();

        Wallet wallet = kit.wallet();

        LOGGER.info("Using wallet: {}", wallet);

        List<ECKey> keys = wallet.getKeys();
        LOGGER.info("Keys: {}", keys);

        wallet.addEventListener(new AbstractWalletEventListener() {
            @Override
            public void onCoinsReceived(Wallet w, Transaction tx, BigInteger prevBalance, BigInteger newBalance) {
                // Runs in the dedicated "user thread".
                //
                // The transaction "tx" can either be pending, or included into a block (we didn't see the broadcast).
                BigInteger value = tx.getValueSentToMe(w);
                LOGGER.info("Received tx for {}: {}", Utils.bitcoinValueToFriendlyString(value), tx);
                LOGGER.info("Transaction will be forwarded after it confirms.");
                // Wait until it's made it into the block chain (may run immediately if it's already there).
                //
                // For this dummy app of course, we could just forward the unconfirmed transaction. If it were
                // to be double spent, no harm done. Wallet.allowSpendingUnconfirmedTransactions() would have to
                // be called in onSetupCompleted() above. But we don't do that here to demonstrate the more common
                // case of waiting for a block.
                Futures.addCallback(tx.getConfidence().getDepthFuture(1), new FutureCallback<Transaction>() {
                    @Override
                    public void onSuccess(Transaction result) {
                        // "result" here is the same as "tx" above, but we use it anyway for clarity.
                        forwardCoins(result);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        // This kind of future can't fail, just rethrow in case something weird happens.
                        throw new RuntimeException(t);
                    }
                });
            }
        });
        synchronized (mon) {
            mon.wait();
        }
        LOGGER.info("Using wallet: {}", wallet);
    }

    private void forwardCoins(Transaction tx) {

        BigInteger value = tx.getValueSentToMe(kit.wallet());
        LOGGER.info("Forwarding {} BTC", Utils.bitcoinValueToFriendlyString(value));

        // Now send the coins back! Send with a small fee attached to ensure rapid confirmation.

        final BigInteger amountToSend = value.subtract(Transaction.REFERENCE_DEFAULT_MIN_TX_FEE);
        final Wallet.SendResult sendResult = kit.wallet().sendCoins(kit.peerGroup(), forwardingAddress, amountToSend);
        checkNotNull(sendResult);  // We should never try to send more coins than we have!
        LOGGER.info("Sending ...");

        // Register a callback that is invoked when the transaction has propagated across the network.
        // This shows a second style of registering ListenableFuture callbacks, it works when you don't
        // need access to the object the future returns.
        assert sendResult != null;
        sendResult.broadcastComplete.addListener(new Runnable() {
            @Override
            public void run() {
                // The wallet has changed now, it'll get auto saved shortly or when the app shuts down.
                LOGGER.info("Sent coins onwards! Transaction hash is {}", sendResult.tx.getHashAsString());
            }
        }, MoreExecutors.sameThreadExecutor());
        synchronized (mon) {
            mon.notifyAll();
        }
    }
}
