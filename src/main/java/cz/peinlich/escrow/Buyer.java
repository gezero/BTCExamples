package cz.peinlich.escrow;

import com.google.bitcoin.core.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.security.PublicKey;

/**
 * User: George
 * Date: 30.11.13
 * Time: 8:47
 */
@Component
public class Buyer implements CanSignTransactions{
    @Autowired
    Escrow escrow;
    private String nonce = "random nonce should this be";

    public Transaction createDepositTransaction(PublicKey sellerPublicKey, PublicKey escrowPublicKey) {
        challengeEscrow(escrowPublicKey);
        throw new UnsupportedOperationException("will get here");
    }

    @Override
    public PublicKey generateNewPublicKey() {
        return null;  //TODO: this is default template
    }


    private void challengeEscrow(PublicKey escrowPublicKey) {
        String signature = escrow.pleaseSignNonce(escrowPublicKey, nonce);
        checkSignature(signature, escrowPublicKey);
    }

    private void checkSignature(String nonce, PublicKey escrowPublicKey) {
        throw new UnsupportedOperationException("will get here sooner or later");
    }
}
