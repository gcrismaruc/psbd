package com.example;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.internal.OracleTypes;
import oracle.jdbc.oracore.OracleType;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gheorghe on 1/7/2017.
 */
@Controller
public class UpdateController {

    @RequestMapping(value = {"/editBooking"})
    public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException {


        int rezervareId = Integer.parseInt(request.getParameter("rezervareID"));

        JSONArray tickets = getTicketsInfo(rezervareId);
        JSONObject info = getRezervationInfo(rezervareId);

        info.put("tickets", tickets);


        response.getWriter().write(info.toJSONString());
        System.out.println(info.toJSONString());
    }

    @RequestMapping(value = {"/updateBooking"})
    public void handleUpdateBooking(HttpServletRequest request, HttpServletResponse response) throws IOException, ParseException {

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(request.getParameter("rezervare"));
        int rezervareId = Integer.parseInt(request.getParameter("rezervareID"));

        System.out.println(json);

        JSONArray tickets = (JSONArray) json.get("tickets");
        String numeRezervare = json.getAsString("nume");
        String prenumeRezervare = json.getAsString("prenume");
        int cnpRezervare = Integer.parseInt(json.getAsString("cnp"));

        updateRezervare(numeRezervare, prenumeRezervare, cnpRezervare, rezervareId);
        //update si biletele...
        updateBilete(tickets);

    }

    private void updateBilete(JSONArray tickets) {

        for( int i = 0; i < tickets.size(); i++) {
            JSONObject object = ((JSONObject)tickets.get(i));
            String nume = object.getAsString("nume");
            String prenume = object.getAsString("prenume");
            int cnp = Integer.parseInt(object.getAsString("cnp"));
            int id = Integer.parseInt(object.getAsString("id"));

            String tip = object.getAsString("tip");

            Connection connection = DataBaseConnector.getInstance().getConnection();
            CallableStatement statement = null;


            try {
                String sql = "{call updatebilet (?, ?, ?, ?)}";
                statement = connection.prepareCall(sql);

                statement.setInt(1,  id);
                statement.setString(2, nume);
                statement.setString(3, prenume);
                statement.setInt(4, cnp);


                statement.execute();

                statement.close();
            } catch (SQLException se) {
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

    private void updateRezervare(String nume, String prenume, int cnp, int id)
    {
        Connection connection = DataBaseConnector.getInstance().getConnection();
        CallableStatement statement = null;

        try {
            String sql = "{call updaterezervare (?, ?, ?, ?)}";

            statement = connection.prepareCall(sql);
            statement.setInt(1,  id);

            statement.setString(2, nume);
            statement.setString(3, prenume);
            statement.setInt(4, cnp);

            statement.execute();

            statement.close();

        } catch (SQLException se) {
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

    private JSONObject getRezervationInfo(int rezervareId){

        JSONObject info = new JSONObject();
        Connection connection = DataBaseConnector.getInstance().getConnection();
        CallableStatement statement = null;

        try {
            String sql = "{call GET_REZERVATIONINFO (?,?,?,?)}";
            statement = connection.prepareCall(sql);

            statement.setInt(1, rezervareId);
            statement.registerOutParameter(2, OracleTypes.VARCHAR);
            statement.registerOutParameter(3, OracleTypes.VARCHAR);
            statement.registerOutParameter(4, OracleTypes.VARCHAR);
            statement.execute();

            info.put("nume", statement.getString(2));
            info.put("prenume", statement.getString(3));
            info.put("cnp", statement.getString(4));
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
        return info;
    }

    private JSONArray getTicketsInfo(int rezervareId){

        JSONArray list = new JSONArray();
        JSONObject json = new JSONObject();

        Connection connection = DataBaseConnector.getInstance().getConnection();
        CallableStatement statement = null;

        try {
            String sql = "{call GET_BILETEINFO (?,?)}";
            statement = connection.prepareCall(sql);

            statement.setInt(1, rezervareId);
            statement.registerOutParameter(2, OracleTypes.CURSOR);

            statement.execute();

            ResultSet result = ((OracleCallableStatement) statement).getCursor(2);
            while (result.next()) {
                json = new JSONObject();
                json.put("nume", result.getString("nume"));
                json.put("prenume", result.getString("prenume"));
                json.put("cnp", result.getString("cnp"));
                json.put("id", result.getString("id"));

                list.add(json);
            }

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
        return list;
    }
}
