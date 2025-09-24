package org.xtracat;

import com.fastcgi.FCGIInterface;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.logging.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    private static final String HTTP_RESPONSE = """
            HTTP/1.1 200 OK
            Content-Type: application/json;charset=UTF-8
            Content-Length: %d
            
            %s
            """;
    private static final String HTTP_ERROR = """
            HTTP/1.1 400 Bad Request
            Content-Type: application/json;charset=UTF-8
            Content-Length: %d
            
            %s
            """;
    private static final String RESULT_JSON = """
            {
                "time": "%s",
                "now": "%s",
                "result": %b,
                "x": %d,
                "y": "%f",
                "r": %d
            }
            """;
    private static final String ERROR_JSON = """
            {
                "now": "%s",
                "reason": "%s"
            }
            """;


    public static final Logger logger = Logger.getLogger(Main.class.getName());

    static {
        try {
            FileHandler fh = new FileHandler("app.log", true); // true = дописывать в конец
            fh.setFormatter(new SimpleFormatter());           // простой текстовый формат
            logger.addHandler(fh);
            logger.setUseParentHandlers(false);               // отключаем вывод в консоль
        } catch (IOException e) {
            throw new RuntimeException("Не удалось настроить логирование", e);
        }
    }


    public static void main(String[] args) {
        int status = 0;

        logger.info("Server is up");

        while (new FCGIInterface().FCGIaccept() >= 0) {
            var startTime = Instant.now();
            String req = System.getProperty("QUERY_STRING");
            logger.info("Got request: " + req);
            try {
                URLParams params = new URLParams(req);
                params.validate("x", value -> {
                    Double[] myIntArray = {-3d, -2d, -1d, 0d, 1d, 2d, 3d, 4d, 5d};
                    return Arrays.asList(myIntArray).contains(value);
                });
                params.validate("y", value -> {
                    try {
                        double y = value.doubleValue();
                        return y > -5 && y < 5;
                    } catch (Exception e) {
                        return false;
                    }
                });
                params.validate("r", value -> {
                    Double[] myIntArray = {1d, 2d, 3d, 4d, 5d};
                    return Arrays.asList(myIntArray).contains(value);
                });

                boolean result = calculate(params.get("x").doubleValue(), params.get("y").doubleValue(), params.get("r").doubleValue());
                var endTime = Instant.now();
                logger.info("OK, result is: " + result);

                String json = String.format(RESULT_JSON, ChronoUnit.NANOS.between(startTime, endTime), LocalDateTime.now(), result,params.get("x").intValue(), params.get("y").doubleValue(), params.get("r").intValue());
                String response = String.format(HTTP_RESPONSE,json.getBytes(StandardCharsets.UTF_8).length ,json);
                System.out.println(response);

            } catch (ValidationException e) {
                logger.info("Validation exception: " + e.getMessage());
                String json = String.format(ERROR_JSON, LocalDateTime.now(), e.getMessage());
                String response = String.format(HTTP_ERROR,json.getBytes(StandardCharsets.UTF_8).length,json);
                System.out.println(response);
            }
        }
    }

    private static boolean calculate(double x, double y, double r) {
        // 1) Четверть круга (верхний левый квадрант)
        if (x <= 0 && y >= 0) {
            return x * x + y * y <= r * r;
        }

        // 2) Прямоугольник (нижняя левая часть)
        if (x <= 0 && y <= 0) {
            return x >= -r / 2 && y >= -r;
        }

        // 3) Треугольник (верхний правый квадрант)
        if (x >= 0 && y >= 0) {
            return x <= r / 2 && y <= r / 2 && y <= (-x + r / 2);
        }

        // если ни одно условие не подходит
        return false;
    }

}


