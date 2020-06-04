package framework;

import com.jayway.jsonpath.JsonPath;
import java.io.File;
import java.io.IOException;

public class TestData {

   public static String getProperty(String key) {
        return getValue ("testdata/properties.json", key);
    }

   public static long getLong (String json, String key) {
       long result = 0;

       try {
           String path = "$." + key;
           result = JsonPath.read(json, path);
       } catch (Exception e) {TestSetup.lastException = e;}

       return result;
   }

    public static String getString (String json, String key) {
        String result = "";

        try {
            String path = "$." + key;
            result = JsonPath.read(json, path);
        } catch (Exception e) {TestSetup.lastException = e;}

        return result;
    }

   private static String getValue (String fName, String key) {
        String result = "";
        File file = new File(fName);

        try {
            String path = "$." + key;
            result = JsonPath.read(file, path);
        } catch (Exception e) {TestSetup.lastException = e;}

        return result;
    }

    public static String getSuiteProperty(String key, String suiteParameter, String defaultValue) {
        String result = System.getenv(key);
        if (result == null || result.isEmpty()) result = suiteParameter;
        if (result == null || result.isEmpty()) result = TestData.getProperty(key);
        if (result == null || result.isEmpty()) result = defaultValue;
        return result;
    }

    public static String getPet(String key, String node) {
        int size;
        String result = "";
        File file = new File("testdata/pets.json");

        try {
            size = JsonPath.read(file, "$.length()");
            for (int i=0; i<size; i++) {
                String path = "$.[" + i + "].name";
                if (node.equals(JsonPath.read(file, path))) {
                    path = "$.[" + i + "]." + key;
                    result = JsonPath.read(file, path);
                    break;
                }
            }
        } catch (IOException e) {TestSetup.lastException = e;}

        return result;
    }

}
