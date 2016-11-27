package  com.example;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import oracle.jdbc.internal.OracleCallableStatement;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import oracle.jdbc.internal.OracleTypes;


/**
 * Created by gcrismaruc on 11/24/2016.
 */


@Controller("testController")
public class Test {


    @RequestMapping(value = {"/test"})
    public ModelAndView handle(HttpServletRequest request, HttpServletResponse response){
        System.out.println(request.getMethod());

        return new ModelAndView("index");
    }

    @RequestMapping(value = {"/booking"}, method = RequestMethod.POST)
    public ModelAndView handleBooking(HttpServletRequest request, HttpServletResponse response){
        System.out.println(request.getMethod());

        return new ModelAndView("index");
    }

    @RequestMapping(value = {"/test/model"})
    public void test(HttpServletRequest request, HttpServletResponse response) throws IOException, ClassNotFoundException, SQLException {

        System.out.println("in test frate " + request.getMethod());

        Map<String,Object> model = new HashMap<>();


        JSONArray list = new JSONArray();
        JSONObject json = new JSONObject();

        Class.forName ("oracle.jdbc.driver.OracleDriver");

        Connection conn =
                DriverManager.getConnection ("jdbc:oracle:thin:@localhost:1521/orcl", "c##gcr", "1234");

        CallableStatement statement = null;

        try {
            String sql = "{call get_test (?)}";
            statement = conn.prepareCall(sql);

            statement.registerOutParameter(1, OracleTypes.CURSOR);
            statement.execute();
            ResultSet result = ((OracleCallableStatement)statement).getCursor(1);

            System.out.println("conn  " + result);
            while (result.next()) {

                json = new JSONObject();
                System.out.println(result.getDouble("id"));
                json.put("id", result.getDouble("id"));
                json.put("nume", result.getString("nume"));
                json.put("functie", result.getString("functie"));

                list.add(json);
            }

            response.getWriter().write(list.toJSONString());
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException se2) {
            }
        }
    }
}
