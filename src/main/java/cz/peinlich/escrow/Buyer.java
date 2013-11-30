package cz.peinlich.escrow;

import com.google.bitcoin.core.Transaction;
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
    public Transaction createDepositTransaction(PublicKey sellerPublicKey, PublicKey escrowPublicKey) {
        throw new UnsupportedOperationException("will get here");
    }

    @Override
    public PublicKey generateNewPublicKey() {
        return null;  //TODO: this is default template
    }
}
