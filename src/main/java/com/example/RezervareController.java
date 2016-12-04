package com.example;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.internal.OracleTypes;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Gheorghe on 12/4/2016.
 */
@Controller
public class RezervareController {

    @RequestMapping(value = {"/incercare"})
    public void handleCurse(HttpServletResponse response) throws IOException {

        Connection connection = DataBaseConnector.getInstance().getConnection();
        CallableStatement statement = null;

        PrintWriter outWriter = response.getWriter();
        JSONArray list = new JSONArray();
        JSONObject json = new JSONObject();

        try {
            String sql = "{call getcursaby_departuredate (?, ?, ?)}";
            statement = connection.prepareCall(sql);

            statement.setInt(1, 7);
            statement.setString(2, "12-JAN-17");
            statement.registerOutParameter(3, OracleTypes.CURSOR);

            statement.execute();

            ResultSet result = ((OracleCallableStatement)statement).getCursor(3);
            while (result.next()) {
                //System.out.println(result.getString("oras_plecare"));
                json = new JSONObject();
                json.put("id_avion", result.getString("avion_id"));
                json.put("oras_plecare", result.getString("oras_plecare"));
                json.put("oras_sosire", result.getString("oras_sosrire"));
                json.put("pret", result.getString("pret"));
                json.put("nr_loc_ec", result.getString("nr_locuri_ec"));
                json.put("nr_loc_bs", result.getString("nr_locuri_bs"));

                System.out.println(json);
                list.add(json);
            }

            outWriter.write(list.toJSONString());
            result.close();

            statement.close();
        } catch (SQLException se) {
            // Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            // Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            // finally block used to close resources
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException se2) {
            }
        }
    }
}
