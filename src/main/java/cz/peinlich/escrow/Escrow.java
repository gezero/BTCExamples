package cz.peinlich.escrow;

import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.kits.WalletAppKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: George
 * Date: 30.11.13
 * Time: 8:47
 */
@Component
public class Escrow implements CanSignTransactions {

    @Autowired
    @Qualifier("escrowWallet")
    WalletAppKit kit;

    Map<byte[], ECKey> keys = new HashMap<byte[], ECKey>();


    public String pleaseSignNonce(byte[] escrowPublicKey, String nonce) {
        ECKey privateKey = keys.get(escrowPublicKey);

        return sign(nonce,privateKey);
    }

    private String sign(String nonce, ECKey privateKey) {
        checkNotNull(privateKey);
        return privateKey.signMessage(nonce);
    }

    @Override
    public byte[] generateNewPublicKey() {
        ECKey privateKey = new ECKey();
        byte[] publicKey = privateKey.getPubKey();
        keys.put(publicKey,privateKey);
        return publicKey;
    }

    public void start() {
        kit.startAndWait();
    }
}
