package com.thinkgem.jeesite.modules.sys.utils;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Properties;

public class PropertyUtils {

    private static final String DEFAULT_FILE = "/jeesite.properties";

    public static String getProperty(String key) {
        Properties prop = new Properties();
        InputStream in = PropertyUtils.class.getResourceAsStream(DEFAULT_FILE);
        try {
            prop.load(in);
            return prop.getProperty(key);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void writeData(String key, String value) {
        Properties prop = new Properties();
        InputStream fis = null;
        OutputStream fos = null;
        try {
            java.net.URL url = PropertyUtils.class.getResource(DEFAULT_FILE);
            File file = new File(url.toURI());
            if (!file.exists())
                file.createNewFile();
            fis = new FileInputStream(file);
            prop.load(fis);
            fis.close();//一定要在修改值之前关闭fis
            fos = new FileOutputStream(file);
            prop.setProperty(key, value);
            prop.store(fos, "Update '" + key + "' value");
            fos.close();

        } catch (IOException e) {
            System.err.println("Visit " + DEFAULT_FILE + " for updating "
                    + value + " value error");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        PropertyUtils.writeData("web.view.index", "microsoft");
        System.out.println(PropertyUtils.getProperty("web.view.index"));
    }

}

