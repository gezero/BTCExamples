package bitsurance.configuraion;

import bitsurance.BitsuranceNetworkParameters;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.params.TestNet3Params;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * User: George
 * Date: 28.10.13
 * Time: 8:26
 */
@Configuration
public class TestNetworkConfiguration {
    @Bean
    public BitsuranceNetworkParameters testNetwork() {
        return new BitsuranceNetworkParameters();
    }

    @Bean
    public NetworkParameters testNetworkParams() {
        return TestNet3Params.get();
    }

    @Bean
    public String networkFilePrefix() {
        return "forwarding-service-testnet";
    }

    @Bean
    public String testNetReturnAddress() {
        return "n3A5Gd7935JkAegvvZBxNujkKQZDwYmuMJ";
    }

    @Bean
    public String workingDirectory() {
        return "build/work";
    }
}
