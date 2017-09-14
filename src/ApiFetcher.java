

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class ApiFetcher {

    public static final String VALIDATION_FIELDS_QUERY = "https://backend-challenge-winter-2017.herokuapp.com/customers.json";
    protected static JSONObject performValidationFieldsQuery(int pageNum) {
        String result = getValidationFieldsString(pageNum);
        JSONObject object = stringToJsonArray(result);
        return object;
    }
    private static String getValidationFieldsString(int pageNum) {
        try {
            URL url = new URL(VALIDATION_FIELDS_QUERY + "?page=" + Integer.toString(pageNum));
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            while(line != null) {
                stringBuilder.append(line).append("\n");
                line = bufferedReader.readLine();
            }
            return stringBuilder.toString();
        } catch (MalformedURLException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    private static JSONObject stringToJsonArray (String queryResult) {
        JSONObject object;
        try {
            object = (JSONObject) new JSONTokener(queryResult).nextValue();

        } catch (JSONException e) {
            return null;
        }
        return object;
    }
}
