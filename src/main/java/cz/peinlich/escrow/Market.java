package cz.peinlich.escrow;

import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This entity represents some intermediary actor that is responsible for connecting buyer and merchant
 * 
 * User: George
 * Date: 30.11.13
 * Time: 8:59
 */
@Component
public class Market {
    @Autowired
    Escrow escrow;

    /**
     * In this method the communication between buyer and merchant is done. It is expected that buyer already agreed
     * with merchant that the transaction will take place.
     */

    public void match(Buyer buyer, Merchant merchant) {

        //We asks actors to provide signing keys
        ECKey escrowPublicKey = escrow.generateNewPublicKey();
        ECKey merchantPublicKey = merchant.getPublicKeyToPutInTransactions();

        // Buyer needs to deposit money
        Transaction depositTransaction = buyer.createDepositTransaction(merchantPublicKey, escrowPublicKey);

        // When buyer deposited the money, merchant is supposed to send the goods and after that he can spend the money
        TransactionAndSignature spendingTransactionAndSignature = merchant.createSpendingTransaction(depositTransaction, escrowPublicKey);

        // To spend the money buyer has to agree, he needs to sign the spending transaction
        buyer.addSignature(depositTransaction, spendingTransactionAndSignature.getTransaction(), spendingTransactionAndSignature.getSignature());


    }



}
