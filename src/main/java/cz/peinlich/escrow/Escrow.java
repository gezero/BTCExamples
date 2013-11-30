package cz.peinlich.escrow;

import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: George
 * Date: 30.11.13
 * Time: 8:47
 */
@Component
public class Escrow implements CanSignTransactions {

    Map<PublicKey,PrivateKey> keys;


    public String pleaseSignNonce(PublicKey escrowPublicKey, String nonce) {
        PrivateKey privateKey = keys.get(escrowPublicKey);

        return sign(nonce,privateKey);
    }

    private String sign(String nonce, PrivateKey privateKey) {
        checkNotNull(privateKey);
        throw new UnsupportedOperationException("will come to this later");
    }

    @Override
    public PublicKey generateNewPublicKey() {

        PublicKey publicKey = null;
        PrivateKey privateKey = null;

        //TODO: we need to generate the keys

        checkNotNull(publicKey);
        checkNotNull(privateKey);

        keys.put(publicKey,privateKey);
        return publicKey;
    }
}
