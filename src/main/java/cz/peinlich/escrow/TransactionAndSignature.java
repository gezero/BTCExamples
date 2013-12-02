package cz.peinlich.escrow;

import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.crypto.TransactionSignature;

/**
 * User: George
 * Date: 2.12.13
 * Time: 8:10
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