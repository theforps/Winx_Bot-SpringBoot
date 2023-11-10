package testBot.testBot.scripts;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Consts {

    public static JSONObject JSON;

    static {
        try {
            JSON = new JSONObject(new String(Files.readAllBytes(Paths.get("src/main/resources/data.json")), StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
