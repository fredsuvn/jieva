package xyz.srclab.common.provider;

public class ProviderNotFoundException extends RuntimeException {

    private final String providerName;

    public ProviderNotFoundException(String providerName) {
        super("Provider: " + providerName);
        this.providerName = providerName;
    }

    public String getProviderName() {
        return providerName;
    }
}
