package cz.peinlich.escrow;

import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.SignatureException;

/**
 * User: George
 * Date: 30.11.13
 * Time: 8:47
 */
@Component
public class Buyer implements CanSignTransactions{
    @Autowired
    Escrow escrow;

    public Transaction createDepositTransaction(byte[] sellerPublicKey, byte[] escrowPublicKey) {
        challengeEscrow(escrowPublicKey);
        throw new UnsupportedOperationException("will get here");
    }

    @Override
    public byte[] generateNewPublicKey() {
        throw new UnsupportedOperationException("Should get to this");
    }


    private void challengeEscrow(byte[] escrowPublicKey) {
        String nonce = "random nonce should this be";
        String signature = escrow.pleaseSignNonce(escrowPublicKey, nonce);
        ECKey key = new ECKey(null, escrowPublicKey,true);
        try {
            key.verifyMessage(nonce, signature);
        } catch (SignatureException e) {
            throw new RuntimeException("Signature was not verified");
        }
    }

}
