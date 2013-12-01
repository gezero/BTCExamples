package cz.peinlich.escrow;

import com.google.bitcoin.core.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.PublicKey;

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
        byte[] escrowPublicKey = escrow.generateNewPublicKey();
        byte[] sellerPublicKey = seller.generateNewPublicKey();
        Transaction depositTransaction = buyer.createDepositTransaction(sellerPublicKey, escrowPublicKey);

/*
        if (noFeeForMarket(depositTransaction)){
            Transaction returnTransaction = createReturnDepositToBuyerTransaction(depositTransaction,buyer);
            buyer.returnDeposit(returnTransaction);
            return;
        }
*/

        seller.createSpendingTransaction(depositTransaction, escrowPublicKey);


    }

}
