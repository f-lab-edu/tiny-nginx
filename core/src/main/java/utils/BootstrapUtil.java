package utils;

import proxy.ProxyBootstrapConfig;

import java.io.File;

public class BootstrapUtil {
    public static ProxyBootstrapConfig loadConfiguration(String configuationPath) {
        File fileHandler = new File(configuationPath);

        if (!fileHandler.isFile() && !fileHandler.exists()) {
            throw new IllegalArgumentException(String.format("You can not load configuration file from : %s", configuationPath));
        }

        return YamlUtil.toObject(fileHandler, ProxyBootstrapConfig.class);
    }
}
