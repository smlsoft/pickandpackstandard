package utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class _global {

    public static final String PREFIX_FILE_CONFIG = "SMLConfig";

    public static final boolean USE_LOCAL = false; // true = use config, false = use provider file config

    public static String _providerCode = "";
    public static String _databaseServer = "192.168.64.47"; //64.47 , 65.19
    public static String _databaseName = "";
    public static String _databaseUserCode = "postgres";
    public static String _databaseUserPassword = "sml";

    public static String FILE_CONFIG(String providerCode) {
        if (USE_LOCAL) {
            return "";
        }
        return PREFIX_FILE_CONFIG + providerCode.toUpperCase() + ".xml";
    }
    
     public String _readXmlFile(String xmlName) {
        String __readLine = "";
        try {
            // Reader __input = new InputStreamReader(new FileInputStream(xmlName));
            //     BufferedReader __in = new BufferedReader(__input);
            String __tempDir = System.getProperty("java.io.tmpdir");
            BufferedReader __in = new BufferedReader(new InputStreamReader(new FileInputStream(__tempDir + "/" + xmlName), "UTF8"));
            char[] __cBuf = new char[65536];
            StringBuilder __stringBuf = new StringBuilder();
            int __readThisTime = 0;
            while (__readThisTime != -1) {
                try {
                    __readThisTime = __in.read(__cBuf, 0, 65536);
                    __stringBuf.append(__cBuf, 0, __readThisTime);
                } catch (Exception __ex) {
                }
            } // end while
            __readLine = __stringBuf.toString();
            __in.close();
        } catch (Exception __ex) {
            System.out.println("_readXmlFile:" + __ex.getMessage());
            __readLine = __ex.getMessage();
        }
        return __readLine;
    }
}
