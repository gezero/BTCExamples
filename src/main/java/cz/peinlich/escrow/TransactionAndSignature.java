package cz.peinlich.escrow;

import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.crypto.TransactionSignature;

/**
 * Stupid java does not allow to return 2 things from a method.
 */
public class TransactionAndSignature{
    Transaction transaction;
    TransactionSignature signature;

    public TransactionAndSignature(Transaction transaction, TransactionSignature signature) {
        this.transaction = transaction;
        this.signature = signature;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public TransactionSignature getSignature() {
        return signature;
    }
}