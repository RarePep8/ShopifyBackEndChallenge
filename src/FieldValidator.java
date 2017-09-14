import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FieldValidator {
    public static void main (String[] args){
        JSONObject result = buildInvalidCustomerList();
        System.out.println(result);
    }
    public static JSONObject buildInvalidCustomerList() {
        try {
            int currentPage = 1;
            int currentIndex = 0;
            JSONObject resultObject = ApiFetcher.performValidationFieldsQuery(currentPage);
            JSONObject pagination = (JSONObject) resultObject.get("pagination");
            JSONArray validations = (JSONArray) resultObject.get("validations");
            JSONArray customers = (JSONArray) resultObject.get("customers");
            int totalNumOfResults = pagination.getInt("total");
            int resultsPerPage = pagination.getInt("per_page");
            int lastPageNum = (int)Math.ceil((double)totalNumOfResults / (double)resultsPerPage);
            JSONArray invalidCustomers = new JSONArray();
            while(currentPage < lastPageNum || (currentPage == lastPageNum && currentIndex < (totalNumOfResults % resultsPerPage))) {
                JSONObject customer = (JSONObject) customers.get(currentIndex);
                boolean valid = true;
                JSONObject invalidCustomer = new JSONObject();
                JSONArray invalidFields = new JSONArray();
                for(int i = 0 ; i < validations.length() ; i++) {
                    JSONObject validation = (JSONObject) validations.get(i);
                    if(!validate(customer, validation)) {
                        valid = false;
                        invalidFields.put((String) validation.keys().next());
                    }
                }
                if(!valid) {
                    invalidCustomer.put("id", customer.get("id"));
                    invalidCustomer.put("invalid_fields", invalidFields);
                    invalidCustomers.put(invalidCustomer);
                }


                if(currentIndex == resultsPerPage - 1) {
                    currentIndex = 0;
                    currentPage++;
                    resultObject = ApiFetcher.performValidationFieldsQuery(currentPage);
                    customers = (JSONArray) resultObject.get("customers");
                } else {
                    currentIndex++;
                }
            }

            JSONObject result = new JSONObject();
            result.put("invalid_customers", invalidCustomers);
            return result;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    private static boolean validate(JSONObject customer, JSONObject validation) {
        boolean valid = true;
        try {
            String validationName = (String) validation.keys().next();
            JSONObject validationFields = (JSONObject) validation.get(validationName);
            if (validationFields.has("required")  && validationFields.getBoolean("required")) {
                if(validationFields.has("type")) {
                    String type = validationFields.getString("type");
                    if(type.equals("string") && !(customer.get(validationName) instanceof String)) {
                        valid = false;
                    } else if (type.equals("boolean") && !(customer.get(validationName) instanceof Boolean)) {
                        valid = false;
                    } else if (type.equals("number") && !(customer.get(validationName) instanceof Integer)) { // Assuming number means int
                        valid = false;
                    }
                }
                if(validationFields.has("length")) {
                    JSONObject lengthObject = (JSONObject) validationFields.get("length");
                    Object customerFieldObject = customer.get(validationName);
                    if(customerFieldObject.equals(null)) {
                        valid = false;
                    } else {
                        String customerField = (String) customerFieldObject;
                        int customerFieldLength = customerField.length();
                        if (lengthObject.has("min") && customerFieldLength < (Integer) lengthObject.get("min")) {
                            valid = false;
                        } else if (lengthObject.has("max") && customerFieldLength > (Integer) lengthObject.get("max")) {
                            valid = false;
                        }
                    }
                }

            }

        } catch (JSONException e) {
            valid = false;
        }
        return valid;
    }
}
