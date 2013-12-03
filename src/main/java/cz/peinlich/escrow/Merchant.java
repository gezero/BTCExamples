package cz.peinlich.escrow;

import com.google.bitcoin.core.*;
import com.google.bitcoin.crypto.TransactionSignature;
import com.google.bitcoin.kits.WalletAppKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.SignatureException;
import java.util.List;

/** This entity represents someone who wants to sell his precious goods (like money) for bitcoins
 *
 * User: George
 * Date: 30.11.13
 * Time: 8:47
 */
@Component
public class Merchant  {
    public static final Logger logger = LoggerFactory.getLogger(Merchant.class);
    @Autowired
    Escrow escrow;
    @Autowired
    @Qualifier("merchantWallet")
    WalletAppKit kit;
    @Autowired

    /**
     * returns key that we want to use in the transaction.
     */
    public ECKey getPublicKeyToPutInTransactions() {
        //TODO: this part is only getting already used key, in general it should create new address and use its key
        ECKey ecKey = getPrivateKeyForSigning();
        return new ECKey(null, ecKey.getPubKey());
    }

    /**
     * When the depositing transaction is already created, we want to spend the money.
     */
    public TransactionAndSignature createSpendingTransaction(Transaction depositTransaction, ECKey escrowPublicKey) {
        challengeEscrow(escrowPublicKey);
        checkThatOursKeyIsUsed(depositTransaction);

        // When we checked that the deposit transaction is ok
        // We start by creating new transaction
        Transaction spendingTransaction = new Transaction(kit.params());

        // We want to spend the deposit transaction. So we will put the proper output as input input into our transaction
        TransactionOutput output = depositTransaction.getOutput(0);
        spendingTransaction.addInput(output);

        // We want to spend as much as possible, but we pay the standard transaction fee
        BigInteger value = output.getValue().subtract(Transaction.REFERENCE_DEFAULT_MIN_TX_FEE);

        //The spending is done to our address
        Address address = getReturnAddress();
        spendingTransaction.addOutput(value, address);

        try {
            // We create signature of the transaction
            ECKey ecKey = getPrivateKeyForSigning();
            TransactionSignature signature = spendingTransaction.calculateSignature(0, ecKey, output.getScriptPubKey(), Transaction.SigHash.ALL, false);
            return new TransactionAndSignature(spendingTransaction, signature);
        } catch (ScriptException e) {
            throw new RuntimeException("Script is not correctly set up");
        }
    }

    /**
     * This method gets the private key that we want to use to sign the transaction. It has to be tha same key that was
     * previously put into the deposit transaction.
     */
    private ECKey getPrivateKeyForSigning() {
        Wallet wallet = kit.wallet();
        List<ECKey> keys = wallet.getKeys();
        return keys.get(0);
    }

    private void checkThatOursKeyIsUsed(Transaction depositTransaction) {
        //To change body of created methods use File | Settings | File Templates.
    }

    private Address getReturnAddress() {
        Wallet wallet = kit.wallet();
        ECKey ecKey = wallet.getKeys().get(0);
        logger.info("Using address {}", ecKey.getPubKeyHash());
        return ecKey.toAddress(kit.params());
    }

    private void challengeEscrow(ECKey escrowPublicKey) {
        String nonce = "random nonce should this be";
        String signature = escrow.signMessage(escrowPublicKey, nonce);
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
