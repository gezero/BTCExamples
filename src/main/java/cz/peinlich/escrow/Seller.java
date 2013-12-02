package cz.peinlich.escrow;

import com.google.bitcoin.core.*;
import com.google.bitcoin.kits.WalletAppKit;
import com.google.bitcoin.script.Script;
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
public class Seller implements CanSignTransactions {
    @Autowired
    Escrow escrow;

    @Autowired
    @Qualifier("sellerWallet")
    WalletAppKit kit;

    String nonce="testNonceString, should be randomly generated I guess";


    @Override
    public ECKey generateNewPublicKey() {
        //TODO: this part is only getting already used key, in general it should create new address and use its key
        Wallet wallet = kit.wallet();
        List<ECKey> keys = wallet.getKeys();
        ECKey ecKey = keys.get(0);
        return new ECKey(null,ecKey.getPubKey());
    }

    public void createSpendingTransaction(Transaction depositTransaction, ECKey escrowPublicKey) {
        challengeEscrow(escrowPublicKey);

        Transaction spendingTransaction = new Transaction(kit.params());

        TransactionOutput output = depositTransaction.getOutput(0);

        byte[] scriptBytes=null;
        TransactionInput input = new TransactionInput(kit.params(),depositTransaction,scriptBytes,new TransactionOutPoint(kit.params(),0,depositTransaction));
        spendingTransaction.addInput(input);

        throw new UnsupportedOperationException("will get here sooner or later");
    }

    private void challengeEscrow(ECKey escrowPublicKey) {
        String nonce = "random nonce should this be";
        String signature = escrow.pleaseSignNonce(escrowPublicKey, nonce);
        try {
            escrowPublicKey.verifyMessage(nonce, signature);
        } catch (SignatureException e) {
            throw new RuntimeException("Signature was not verified");
        }
    }

    public void start() {
        kit.startAndWait();
    }
}
