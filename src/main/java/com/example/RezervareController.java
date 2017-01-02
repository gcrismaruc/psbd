package com.example;

import com.fasterxml.jackson.core.JsonParser;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.internal.OracleTypes;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Gheorghe on 12/4/2016.
 */
@Controller
public class RezervareController {

    @RequestMapping(value = {"/availableFlight"})
    public void handleCurse(HttpServletRequest request, HttpServletResponse response) throws IOException, ParseException, ParseException {

        Connection connection = DataBaseConnector.getInstance().getConnection();
        CallableStatement statement = null;

        PrintWriter outWriter = response.getWriter();
        JSONArray list = new JSONArray();
        JSONObject json = new JSONObject();

        int avionID = Integer.parseInt(request.getParameter("avionID"));
        String dateOnRequest = request.getParameter("dataPlecare").split("T")[0];

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = format.parse(dateOnRequest);

        String dateForDB = (new SimpleDateFormat("dd-MMM-yy").format(date)).toString().toUpperCase();
        try {
            String sql = "{call getcursaby_departuredate (?, ?, ?)}";
            statement = connection.prepareCall(sql);

            statement.setInt(1, avionID);
            statement.setString(2, dateForDB);
            statement.registerOutParameter(3, OracleTypes.CURSOR);

            statement.execute();

            System.out.println("aicici");
            ResultSet result = ((OracleCallableStatement) statement).getCursor(3);
            while (result.next()) {
                System.out.println("---------------------------------------------");
                json = new JSONObject();
                json.put("id_avion", result.getString("avion_id"));
                json.put("oras_plecare", result.getString("oras_plecare"));
                json.put("oras_sosire", result.getString("oras_sosrire"));
                json.put("pret", result.getString("pret"));
                json.put("nr_loc_ec", result.getString("nr_locuri_ec"));
                json.put("nr_loc_bs", result.getString("nr_locuri_bs"));


                // e ora sau partida?
                // n-ai probleme momentan
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

        @RequestMapping(value = {"/saveBooking"})
        public void handleBooking(HttpServletRequest request, HttpServletResponse response) throws IOException, ParseException, net.minidev.json.parser.ParseException, NoSuchAlgorithmException {

            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(request.getParameter("rezervare"));


            System.out.println(json);

            String numeRezervare = json.getAsString("nume");
            String prenumeRezervare = json.getAsString("prenume");
            int cnpRezervare = (int)json.getAsNumber("cnp");
            int numarLocuri = (int)json.getAsNumber("nrLoc");

            addRezervare(numeRezervare, prenumeRezervare, cnpRezervare, numarLocuri, response);

        }



        private void addRezervare(String nume, String prenume, int cnp, int nrLocuri, HttpServletResponse response) throws NoSuchAlgorithmException {

            Connection connection = DataBaseConnector.getInstance().getConnection();
            CallableStatement statement = null;


            try {
                String sql = "{call addrezervare (?, ?, ?, ?, ?, ?, ?)}";
                statement = connection.prepareCall(sql);

                statement.setInt(1,  (int)(System.currentTimeMillis() % 100000));

                statement.setString(2, nume);
                statement.setString(3, prenume);
                statement.setInt(4, cnp);
                statement.setInt(5, nrLocuri);

                //is paid....trebuie modificata
                statement.setString(6, "0");
                statement.setInt(7, 2616);


                statement.execute();
                response.setStatus(HttpServletResponse.SC_OK);

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
