package bitsurance;

import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.params.TestNet3Params;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * User: George
 * Date: 27.10.13
 * Time: 7:29
 */

@Configuration
public class BitsuranceConfiguration {
    @Bean
    public Bitsurance bitsurance() {
        return new Bitsurance();
    }

    @Bean
    public BitsuranceNetworkParameters testNetwork() {
        NetworkParameters params = TestNet3Params.get();
        String filePrefix = "forwarding-service-testnet";
        return new BitsuranceNetworkParameters(params, filePrefix);
    }
    @Bean
    public String testNetReturnAddress(){
        return "n3A5Gd7935JkAegvvZBxNujkKQZDwYmuMJ";
    }
}
