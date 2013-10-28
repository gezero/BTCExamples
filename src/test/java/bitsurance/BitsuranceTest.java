package bitsurance;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.AddressFormatException;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.kits.WalletAppKit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;

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
    String returnAddress;

    @Test
    public void firstTestNetTest() throws AddressFormatException {

        NetworkParameters params = bitsuranceNetworkParameters.getNetworkParameters();
        Address forwardingAddress = new Address(params, returnAddress);
        WalletAppKit kit = new WalletAppKit(params, new File("build/work"), bitsuranceNetworkParameters.getFilePrefix()) {
            @Override
            protected void onSetupCompleted() {
                // This is called in a background thread after startAndWait is called, as setting up various objects
                // can do disk and network IO that may cause UI jank/stuttering in wallet apps if it were to be done
                // on the main thread.
                if (wallet().getKeychainSize() < 1)
                    wallet().addKey(new ECKey());
            }
        };

        kit.startAndWait();


        assertThat(bitsurance.test(), is(1));
    }
}
