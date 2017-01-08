package com.example;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.internal.OracleTypes;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Gheorghe on 12/12/2016.
 */
@Controller
public class PaidControler {

    @RequestMapping(value = {"/getpaid"})
    public void handlePaid(HttpServletRequest request, HttpServletResponse response) throws IOException, ParseException, ParseException {

        Connection connection = DataBaseConnector.getInstance().getConnection();
        CallableStatement statement = null;

        PrintWriter outWriter = response.getWriter();
        JSONArray list = new JSONArray();
        JSONObject json = new JSONObject();


        String nume = request.getParameter("nume");
        String prenume = request.getParameter("prenume");
        double cnp = Double.parseDouble(request.getParameter("cnp"));

        try {
            String sql = "{call getnotpaid (?, ?, ?, ?)}";
            statement = connection.prepareCall(sql);

            statement.setString(1, nume);
            statement.setString(2, prenume);
            statement.setDouble(3, cnp);
            statement.registerOutParameter(4, OracleTypes.CURSOR);

            statement.execute();

            System.out.println("aicici");
            ResultSet result = ((OracleCallableStatement) statement).getCursor(4);
            while (result.next()) {
                System.out.println("---------------------------------------------");
                json = new JSONObject();
                json.put("id", result.getString("id"));
                json.put("oras_plecare", result.getString("oras_plecare"));
                json.put("oras_sosire", result.getString("oras_sosrire"));
                json.put("nrLocuri", result.getString("nr_locuri"));
                json.put("data_plecare", result.getString("d_plecare"));
                json.put("suma", result.getString("sumatotala"));

                System.out.println(json);
                list.add(json);
            }

            outWriter.write(list.toJSONString());
            result.close();

            statement.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException se2) {
            }
        }
    }

    @RequestMapping(value = {"/dopay"})
    public void handledopay(HttpServletRequest request, HttpServletResponse response) throws IOException, ParseException, ParseException {

        Connection connection = DataBaseConnector.getInstance().getConnection();
        CallableStatement statement = null;

        int id = Integer.parseInt(request.getParameter("rezervareID"));

        try {
            String sql = "{call dopay (?)}";
            statement = connection.prepareCall(sql);

            statement.setInt(1, id);

            statement.execute();

            response.setStatus(HttpServletResponse.SC_OK);
            statement.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException se2) {
            }
        }

    }

}
