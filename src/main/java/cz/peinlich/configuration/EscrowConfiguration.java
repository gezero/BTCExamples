package cz.peinlich.configuration;

import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.kits.WalletAppKit;
import cz.peinlich.configuration.TestNetworkConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.io.File;

/**
 * User: George
 * Date: 30.11.13
 * Time: 9:37
 */
@Configuration
@Import(TestNetworkConfiguration.class)
@ComponentScan(basePackages = {"cz.peinlich.escrow"})
public class EscrowConfiguration {
    @Autowired
    NetworkParameters networkParameters;

    @Autowired
    @Qualifier("workingDirectory")
    String workingDirectory;

    @Bean
    public String buyerWalletPrefix() {
        return "escrow-buyer";
    }

    @Bean
    public String sellerWalletPrefix() {
        return "escrow-seller";
    }

    @Bean
    public String escrowWalletPrefix() {
        return "escrow-escrow";
    }


    @Bean
    public WalletAppKit buyerWallet() {
        return new WalletAppKit(networkParameters, new File(workingDirectory), buyerWalletPrefix()) {
            @Override
            protected void onSetupCompleted() {
                // This is called in a background thread after startAndWait is called, as setting up various objects
                // can do disk and network IO that may cause UI jank/stuttering in wallet apps if it were to be done
                // on the main thread.
                if (wallet().getKeychainSize() < 1)
                    wallet().addKey(new ECKey());
            }
        };
    }

    @Bean
    public WalletAppKit sellerWallet() {
        return new WalletAppKit(networkParameters, new File(workingDirectory), sellerWalletPrefix()) {
            @Override
            protected void onSetupCompleted() {
                // This is called in a background thread after startAndWait is called, as setting up various objects
                // can do disk and network IO that may cause UI jank/stuttering in wallet apps if it were to be done
                // on the main thread.
                if (wallet().getKeychainSize() < 1)
                    wallet().addKey(new ECKey());
            }
        };
    }

    @Bean
    public WalletAppKit escrowWallet() {
        return new WalletAppKit(networkParameters, new File(workingDirectory), escrowWalletPrefix()) {
            @Override
            protected void onSetupCompleted() {
                // This is called in a background thread after startAndWait is called, as setting up various objects
                // can do disk and network IO that may cause UI jank/stuttering in wallet apps if it were to be done
                // on the main thread.
                if (wallet().getKeychainSize() < 1)
                    wallet().addKey(new ECKey());
            }
        };
    }
}
