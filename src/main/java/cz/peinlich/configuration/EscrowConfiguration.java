package cz.peinlich.configuration;

import cz.peinlich.configuration.TestNetworkConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * User: George
 * Date: 30.11.13
 * Time: 9:37
 */
@Configuration
@Import(TestNetworkConfiguration.class)
@ComponentScan(basePackages = {"cz.peinlich.escrow"})
public class EscrowConfiguration {
}
