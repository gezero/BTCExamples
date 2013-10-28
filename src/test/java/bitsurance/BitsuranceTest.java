package bitsurance;

import bitsurance.configuraion.BitsuranceConfiguration;
import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.AddressFormatException;
import com.google.bitcoin.core.NetworkParameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * User: George
 * Date: 27.10.13
 * Time: 7:42
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {BitsuranceConfiguration.class})
public class BitsuranceTest {

    @Autowired
    Bitsurance bitsurance;
    @Autowired
    BitsuranceNetworkParameters bitsuranceNetworkParameters;
    @Autowired
    @Qualifier("workingDirectory")
    String workingDirectory;
    @Autowired
    @Qualifier("testNetReturnAddress")
    String returnAddress;

    @Test
    public void firstTestNetTest() throws AddressFormatException {

        NetworkParameters params = bitsuranceNetworkParameters.getNetworkParameters();
        Address forwardingAddress = new Address(params, returnAddress);

        bitsurance.start();

        assertThat(bitsurance.test(), is(1));
    }
}
