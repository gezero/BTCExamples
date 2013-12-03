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

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This entity represents person that has bitcoins and wants to spend them. Current implementation
 * does not challenge merchant. We trust that the entity that we are using for trading will give us
 * proper merchant key. We wanted to eliminate buyer talking with merchant directly.
 *
 *
 * User: George
 * Date: 30.11.13
 * Time: 8:47
 */
@Component
public class Buyer {
    @Autowired
    Escrow escrow;

    @Autowired
    @Qualifier("buyerWallet")
    WalletAppKit kit;

    /** Creates spending transaction that will send bitcoins to intermediate state. From that state there will be
     * 2 keys needed to spend the bitcoins onward. The transaction is sent to blockchain. Note that the transaction
     * is not complete before it is send. The bitcoinj client is filling the additional information to that the
     * transaction needs jut before it is send to the blockchain.
     */
    public Transaction createDepositTransaction(ECKey merchantPublicKey, ECKey escrowPublicKey) {
        challengeEscrow(escrowPublicKey);
        ECKey buyerPublicKey = getPublicKeyToPutInTransactions();
        Transaction transaction = buildDepositTransaction(buyerPublicKey, merchantPublicKey, escrowPublicKey);

        Wallet wallet = kit.wallet();
        Wallet.SendResult sendResult = checkNotNull(wallet.sendCoins(kit.peerGroup(), Wallet.SendRequest.forTx(transaction)));
        return sendResult.tx;
    }

    /** The actual creating of the spending part of the deposit transaction. The rest of the transaction will be
     *  added using the bitcoinj client later.
     */
    private Transaction buildDepositTransaction(ECKey buyerPublicKey, ECKey merchantPublicKey, ECKey escrowPublicKey) {
        Wallet wallet = kit.wallet();
        Transaction transaction = new Transaction(kit.params());
        BigInteger value = wallet.getBalance().subtract(Transaction.REFERENCE_DEFAULT_MIN_TX_FEE);
        Script script = ScriptBuilder.createMultiSigOutputScript(2, Arrays.asList(buyerPublicKey, merchantPublicKey, escrowPublicKey));
        transaction.addOutput(value, script);
        return transaction;
    }

    /**
     *  This method returns new key that will be used in the transaction signing.
     */
    public ECKey getPublicKeyToPutInTransactions() {
        //TODO: this part is only getting already used key, in general it should create new address and use its key
        Wallet wallet = kit.wallet();
        List<ECKey> keys = wallet.getKeys();
        ECKey ecKey = keys.get(0);
        return new ECKey(null, ecKey.getPubKey());
    }

    /**
     * We want to check, that we indeed have the escrow key. Similar thing could be done for the merchant if there would
     * be some channel to the merchant.
     *
     */
    private void challengeEscrow(ECKey escrowPublicKey) {
        String nonce = "random nonce should this be";
        String signature = escrow.signMessage(escrowPublicKey, nonce);
        try {
            escrowPublicKey.verifyMessage(nonce, signature);
        } catch (SignatureException e) {
            throw new RuntimeException("Signature was not verified");
        }
    }

    /**
     *  Yeah, this method init the kit for this entity. Among other things it waits for the blockchain to sync.
     */
    public void start() {
        kit.startAndWait();
    }

    /**
     * This method creates second signature for the spending transaction and broadcasts it to the chain.
     *
     */
    public void addSignature(Transaction depositTransaction, Transaction spendingTransaction, TransactionSignature otherPartySignature) {
        Wallet wallet = kit.wallet();
        List<ECKey> keys = wallet.getKeys();
        ECKey ecKey = keys.get(0);

        spendingTransaction.getInput(0).getScriptBytes();
        try {
            TransactionSignature transactionSignature = spendingTransaction.calculateSignature(0, ecKey, depositTransaction.getOutput(0).getScriptPubKey(), Transaction.SigHash.ALL, true);
            spendingTransaction.getInput(0).setScriptSig(ScriptBuilder.createMultiSigInputScript(Lists.newArrayList(transactionSignature, otherPartySignature)));

        } catch (ScriptException e) {
            throw new RuntimeException("Script is not build properly");
        }

        wallet.sendCoins(kit.peerGroup(), Wallet.SendRequest.forTx(spendingTransaction));

    }
}
