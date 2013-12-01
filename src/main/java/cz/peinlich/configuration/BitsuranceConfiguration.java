package cz.peinlich.configuration;

import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.kits.WalletAppKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.io.File;

/**
 * User: George
 * Date: 27.10.13
 * Time: 7:29
 */

@Configuration
@Import(TestNetworkConfiguration.class)
@ComponentScan(basePackages = {"cz.peinlich.bitsurance"})
public class BitsuranceConfiguration {

    @Autowired
    NetworkParameters networkParameters;

    @Autowired
    @Qualifier("workingDirectory")
    String workingDirectory;

    @Bean
    public String bitsuranceFilePrefix() {
        return "forwarding-service-testnet";
    }

    @Bean
    public WalletAppKit walletAppKit() {
        return new WalletAppKit(networkParameters, new File(workingDirectory), bitsuranceFilePrefix()) {
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
