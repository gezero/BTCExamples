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

/**
 * User: George
 * Date: 30.11.13
 * Time: 8:47
 */
@Component
public class Seller implements CanSignTransactions {
    public static final Logger logger = LoggerFactory.getLogger(Seller.class);
    @Autowired
    Escrow escrow;
    @Autowired
    @Qualifier("sellerWallet")
    WalletAppKit kit;
    @Autowired
    @Qualifier("testNetReturnAddress")
    String returnAddress;
    String nonce = "testNonceString, should be randomly generated I guess";

    @Override
    public ECKey generateNewPublicKey() {
        //TODO: this part is only getting already used key, in general it should create new address and use its key
        Wallet wallet = kit.wallet();
        List<ECKey> keys = wallet.getKeys();
        ECKey ecKey = keys.get(0);
        return new ECKey(null, ecKey.getPubKey());
    }

    public TransactionAndSignature createSpendingTransaction(Transaction depositTransaction, ECKey escrowPublicKey) {
        challengeEscrow(escrowPublicKey);

        Wallet wallet = kit.wallet();
        List<ECKey> keys = wallet.getKeys();
        ECKey ecKey = keys.get(0);

        ECKey dummyKey = new ECKey(null, BigInteger.ZERO);

        Transaction spendingTransaction = new Transaction(kit.params());

        TransactionOutput output = depositTransaction.getOutput(0);


        BigInteger value = output.getValue().subtract(Transaction.REFERENCE_DEFAULT_MIN_TX_FEE);
        Address address = getReturnAddress();
        spendingTransaction.addOutput(value, address);


//        TransactionInput input = new TransactionInput(kit.params(),depositTransaction,scriptBytes,new TransactionOutPoint(kit.params(),0,depositTransaction));
        spendingTransaction.addInput(output);

        try {
            TransactionSignature signature = spendingTransaction.calculateSignature(0, ecKey, output.getScriptPubKey(), Transaction.SigHash.ALL, true);
//            spendingTransaction.getInput(0).setScriptSig(ScriptBuilder.createMultiSigInputScript(Lists.newArrayList(signature,signature)));
            return new TransactionAndSignature(spendingTransaction, signature);
        } catch (ScriptException e) {
            throw new RuntimeException("Script is not correctly set up");
        }
    }

    private Address getReturnAddress() {
        Wallet wallet = kit.wallet();
        ECKey ecKey = wallet.getKeys().get(0);
        Address address;
        logger.info("using address {}", ecKey.getPubKeyHash());
        address = new Address(kit.params(), ecKey.getPubKeyHash());
        return address;
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
