package cz.peinlich.escrow;

import com.google.bitcoin.core.ECKey;

import java.security.PublicKey;

/**
 * User: George
 * Date: 30.11.13
 * Time: 9:05
 */
public interface CanSignTransactions {
    ECKey generateNewPublicKey();
}
