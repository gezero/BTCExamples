package cz.peinlich.escrow;

import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.Wallet;
import com.google.bitcoin.kits.WalletAppKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.security.SignatureException;
import java.util.List;

/**
 * User: George
 * Date: 30.11.13
 * Time: 8:47
 */
@Component
public class Buyer implements CanSignTransactions {
    @Autowired
    Escrow escrow;
    @Autowired
    @Qualifier("buyerWallet")
    WalletAppKit kit;

    public Transaction createDepositTransaction(byte[] sellerPublicKey, byte[] escrowPublicKey) {
        challengeEscrow(escrowPublicKey);
        byte[] buyerPublicKey = generateNewPublicKey();
        return buildDepositTransaction(buyerPublicKey,sellerPublicKey,escrowPublicKey);
    }

    private Transaction buildDepositTransaction(byte[] buyerPublicKey, byte[] sellerPublicKey, byte[] escrowPublicKey) {
        throw new UnsupportedOperationException("will get here");
    }

    @Override
    public byte[] generateNewPublicKey() {
        //TODO: this part is only getting already used key, in general it should create new address and use its key
        Wallet wallet = kit.wallet();
        List<ECKey> keys = wallet.getKeys();
        ECKey ecKey = keys.get(0);
        return ecKey.getPubKey();
    }

    private void challengeEscrow(byte[] escrowPublicKey) {
        String nonce = "random nonce should this be";
        String signature = escrow.pleaseSignNonce(escrowPublicKey, nonce);
        ECKey key = new ECKey(null, escrowPublicKey, true);
        try {
            key.verifyMessage(nonce, signature);
        } catch (SignatureException e) {
            throw new RuntimeException("Signature was not verified");
        }
    }

    public void start() {
        kit.startAndWait();
    }
}
