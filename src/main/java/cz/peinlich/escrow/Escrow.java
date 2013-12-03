package cz.peinlich.escrow;

import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.kits.WalletAppKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This entity represents third party that will resolve disputes.
 *
 * User: George
 * Date: 30.11.13
 * Time: 8:47
 */
@Component
public class Escrow {

    @Autowired
    @Qualifier("escrowWallet")
    WalletAppKit kit;


    /**
     * List of keys that we dispatched.
     */
    Map<ECKey, ECKey> keys = new HashMap<ECKey, ECKey>();

    /**
     *  This method signs a message using previously dispatched key.
     */
    public String signMessage(ECKey escrowPublicKey, String message) {
        ECKey privateKey = keys.get(escrowPublicKey);
        return sign(message,privateKey);
    }

    /**
     *  The actual process of signing message
     */
    private String sign(String message, ECKey privateKey) {
        checkNotNull(privateKey);
        return privateKey.signMessage(message);
    }

    /**
     *  generates new public key that could be used in transactions
     */
    public ECKey generateNewPublicKey() {
        ECKey privateKey = new ECKey();
        byte[] publicKey = privateKey.getPubKey();
        keys.put(new ECKey(null,publicKey),privateKey);
        return new ECKey(null,publicKey);
    }

    /**
     * Starts a wallet, probably not needed in this example yet.
     */
    public void start() {
        kit.startAndWait();
    }
}
