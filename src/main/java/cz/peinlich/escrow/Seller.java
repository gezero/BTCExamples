package cz.peinlich.escrow;

import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.Wallet;
import com.google.bitcoin.kits.WalletAppKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
import java.util.List;

/**
 * User: George
 * Date: 30.11.13
 * Time: 8:47
 */
@Component
public class Seller implements CanSignTransactions {
    @Autowired
    Escrow escrow;

    @Autowired
    @Qualifier("sellerWallet")
    WalletAppKit kit;

    String nonce="testNonceString, should be randomly generated I guess";


    @Override
    public byte[] generateNewPublicKey() {
        //TODO: this part is only getting already used key, in general it should create new address and use its key
        Wallet wallet = kit.wallet();
        List<ECKey> keys = wallet.getKeys();
        ECKey ecKey = keys.get(0);
        return ecKey.getPubKey();
    }

    public void createSpendingTransaction(Transaction depositTransaction, byte[] escrowPublicKey) {
        challengeEscrow(escrowPublicKey);

        throw new UnsupportedOperationException("will get here sooner or later");
    }

    private void challengeEscrow(byte[] escrowPublicKey) {
        String signature = escrow.pleaseSignNonce(escrowPublicKey, nonce);
        checkSignature(signature, escrowPublicKey);
    }

    private void checkSignature(String nonce, byte[] escrowPublicKey) {
        throw new UnsupportedOperationException("will get here sooner or later");
    }

    public void start() {
        kit.startAndWait();
    }
}
