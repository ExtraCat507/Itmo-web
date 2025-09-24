package org.xtracat;


import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class URLParams {

    private final HashMap<String, Double> data = new HashMap<>();

    public Logger logger = Main.logger;

    public URLParams(String params) throws ValidationException {
        if (params == null || params.isEmpty()) {
            throw new ValidationException("Missing query string");
        }
        String[] arr = params.split("&");
        logger.info(Arrays.toString(arr));
        for (String s : arr) {
            String[] items = s.split("=");
            try {
                data.put(items[0], Double.parseDouble(items[1]));
            } catch (NumberFormatException e) {
                throw new ValidationException("Unsupported data type in: " + items[0]);
            }
        }
        logger.info("Success on parsing");
    }


    public Number get(String field) {
        logger.info("get:" + field);
        return data.get(field);
    }

    public void validate(String field, ValidationExpression expression) throws ValidationException {
        try {
            Number value = data.get(field);
            if (value == null) throw new ValidationException("No argument - " + field);
            if (!expression.evaluate(value)) {
                throw new ValidationException(field + " failed validation");
            }
        } catch (Exception e) {
            throw new ValidationException("Something went wrong with validation " + field);
        }
    }


}
