package cz.peinlich.escrow;

import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.crypto.TransactionSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * User: George
 * Date: 30.11.13
 * Time: 8:59
 */
@Component
public class Market {
    @Autowired
    Escrow escrow;

    public void match(Buyer buyer, Seller seller) {
        ECKey escrowPublicKey = escrow.generateNewPublicKey();
        ECKey sellerPublicKey = seller.generateNewPublicKey();
        Transaction depositTransaction = buyer.createDepositTransaction(sellerPublicKey, escrowPublicKey);


        TransactionAndSignature spendingTransactionAndSignature = seller.createSpendingTransaction(depositTransaction, escrowPublicKey);

        buyer.addSignature(depositTransaction,spendingTransactionAndSignature);


    }



}
