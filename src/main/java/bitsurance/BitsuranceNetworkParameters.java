package bitsurance;

import com.google.bitcoin.core.NetworkParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * User: George
 * Date: 28.10.13
 * Time: 7:17
 */
public class BitsuranceNetworkParameters {
    @Autowired
    private NetworkParameters networkParameters;
    @Autowired
    @Qualifier("networkFilePrefix")
    private String filePrefix;
    @Autowired
    @Qualifier("workingDirectory")
    private String workingDirectory;

    public NetworkParameters getNetworkParameters() {
        return networkParameters;
    }

    public String getFilePrefix() {
        return filePrefix;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }
}
