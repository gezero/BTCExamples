package cz.peinlich.escrow;

import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.ScriptException;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.Wallet;
import com.google.bitcoin.crypto.TransactionSignature;
import com.google.bitcoin.kits.WalletAppKit;
import com.google.bitcoin.script.Script;
import com.google.bitcoin.script.ScriptBuilder;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.SignatureException;
import java.util.Arrays;
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

    public Transaction createDepositTransaction(ECKey sellerPublicKey, ECKey escrowPublicKey) {
        challengeEscrow(escrowPublicKey);
        ECKey buyerPublicKey = generateNewPublicKey();
        return buildDepositTransaction(buyerPublicKey, sellerPublicKey, escrowPublicKey);
    }

    private Transaction buildDepositTransaction(ECKey buyerPublicKey, ECKey sellerPublicKey, ECKey escrowPublicKey) {
        Wallet wallet = kit.wallet();

        Transaction transaction = new Transaction(kit.params());
        BigInteger value = wallet.getBalance().subtract(Transaction.REFERENCE_DEFAULT_MIN_TX_FEE);
        Script script = ScriptBuilder.createMultiSigOutputScript(2, Arrays.asList(buyerPublicKey, sellerPublicKey, escrowPublicKey));
        transaction.addOutput(value, script);

//        Wallet.SendResult sendResult = wallet.sendCoins(kit.peerGroup(), Wallet.SendRequest.forTx(transaction));
//        return sendResult.tx;  TODO: use this in the final test
        return transaction;
    }

    @Override
    public ECKey generateNewPublicKey() {
        //TODO: this part is only getting already used key, in general it should create new address and use its key
        Wallet wallet = kit.wallet();
        List<ECKey> keys = wallet.getKeys();
        ECKey ecKey = keys.get(0);
        return new ECKey(null, ecKey.getPubKey());
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

    public void addSignature(Transaction depositTransaction, TransactionAndSignature spendingTransactionAndSignature) {
        Wallet wallet = kit.wallet();
        List<ECKey> keys = wallet.getKeys();
        ECKey ecKey = keys.get(0);

        Transaction spendingTransaction = spendingTransactionAndSignature.getTransaction();

        spendingTransaction.getInput(0).getScriptBytes();
        try {
            TransactionSignature transactionSignature = spendingTransaction.calculateSignature(0, ecKey, depositTransaction.getOutput(0).getScriptPubKey(), Transaction.SigHash.ALL, true);
            spendingTransaction.getInput(0).setScriptSig(ScriptBuilder.createMultiSigInputScript(Lists.newArrayList(transactionSignature, spendingTransactionAndSignature.getSignature())));

        } catch (ScriptException e) {
            throw new RuntimeException("Script is not build properly");
        }

//        Wallet.SendResult sendResult = wallet.sendCoins(kit.peerGroup(), Wallet.SendRequest.forTx(spendingTransaction));

    }
}
