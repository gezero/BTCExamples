package bitsurance;

import com.google.bitcoin.core.NetworkParameters;

/**
 * User: George
 * Date: 28.10.13
 * Time: 7:17
 */
public class BitsuranceNetworkParameters {
    private NetworkParameters networkParameters;
    private String filePrefix;

    public BitsuranceNetworkParameters(NetworkParameters networkParameters, String filePrefix) {
        this.networkParameters = networkParameters;
        this.filePrefix = filePrefix;
    }

    public NetworkParameters getNetworkParameters() {
        return networkParameters;
    }

    public String getFilePrefix() {
        return filePrefix;
    }
}
