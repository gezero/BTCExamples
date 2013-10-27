package bitsurance;

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
    public Bitsurance bitsurance(){
        return new Bitsurance();
    }
}
