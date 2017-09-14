import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FieldValidator {
    public static void main (String[] args){
        buildInvalidCustomerList();
    }
    public static JSONObject buildInvalidCustomerList() {
        try {
            int currentPage = 1;
            int currentIndex = 0;
            JSONObject resultObject = ApiFetcher.performValidationFieldsQuery(currentPage);
            JSONObject pagination = (JSONObject) resultObject.get("pagination");
            JSONArray validations = (JSONArray) resultObject.get("validations");
            System.out.println(validations.toString());
            JSONArray customers = (JSONArray) resultObject.get("customers");
            int totalNumOfResults = pagination.getInt("total");
            int resultsPerPage = pagination.getInt("per_page");
            int lastPageNum = (int)Math.ceil((double)totalNumOfResults / (double)resultsPerPage);
            while(currentPage <= lastPageNum) {
                JSONObject customer = (JSONObject) customers.get(currentIndex);
                for(int i = 0 ; i < validations.length() ; i++) {
                    JSONObject validation = (JSONObject) validations.get(i);
                }


                if(currentIndex == resultsPerPage - 1) {
                    currentIndex = 0;
                    currentPage++;
                    resultObject = ApiFetcher.performValidationFieldsQuery(currentPage);
                    customers = (JSONArray) resultObject.get("customers");
                }
            }

        } catch (JSONException e) {

        }

        return null;
    }
}
