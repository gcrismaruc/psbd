package com.example;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.internal.OracleTypes;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Gheorghe on 12/4/2016.
 */
@Controller
public class RezervareController {

    private static int PRET_BILET;

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

        int cursaID = 0;
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
                cursaID = Integer.parseInt(result.getString("id"));
                json.put("id_avion", result.getString("avion_id"));
                json.put("oras_plecare", result.getString("oras_plecare"));
                json.put("oras_sosire", result.getString("oras_sosrire"));

                PRET_BILET = Integer.parseInt(result.getString("pret"));

                json.put("pret", PRET_BILET);
                json.put("nr_loc_ec", result.getString("nr_locuri_ec"));
                json.put("nr_loc_bs", result.getString("nr_locuri_bs"));
                json.put("locuriOcupate", getLocuriOcupate(cursaID));


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

        private List<Integer> getLocuriOcupate(int cursID) {
            List<Integer> locuriOcupateE = new ArrayList<>();
            List<Integer> locuriOcupateB = new ArrayList<>();
            List<Integer> locuriOcupate = new ArrayList<>();
            Connection connection = DataBaseConnector.getInstance().getConnection();
            CallableStatement statement = null;

            try {
                String sql = "{call GETREZERVARIID (?, ?)}";

                statement = connection.prepareCall(sql);

                statement.setInt(1, cursID);
                statement.registerOutParameter(2, OracleTypes.CURSOR);

                statement.execute();
                ResultSet resultSet = ((OracleCallableStatement) statement).getCursor(2);

                while (resultSet.next()) {

                    int r_id = Integer.parseInt(resultSet.getString("id"));
                    try{
                        locuriOcupateB.addAll(getLocuri(r_id, "B"));
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                    try{
                        locuriOcupateE.addAll(getLocuri(r_id, "E"));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
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

            locuriOcupate.addAll(locuriOcupateE);
            locuriOcupate.addAll(locuriOcupateB);

            return locuriOcupate;
        }

        private List<Integer> getLocuri(int r_id, String tip){

            List<Integer> locuri = new ArrayList<>();
            Connection connection = DataBaseConnector.getInstance().getConnection();
            CallableStatement statement = null;

            try {
                String sql = "{call GETLOCURIFORREZERVAREID (?, ?, ?)}";

                statement = connection.prepareCall(sql);

                statement.setInt(1, r_id);
                statement.setString(2, tip);
                statement.registerOutParameter(3, OracleTypes.CURSOR);

                statement.execute();
                ResultSet resultSet = ((OracleCallableStatement) statement).getCursor(3);

                while (resultSet.next()) {

                    int loc = Integer.parseInt(resultSet.getString("loc"));
                    locuri.add(loc);
                }

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

            return locuri;
        }
        @RequestMapping(value = {"/saveBooking"})
        public void handleBooking(HttpServletRequest request, HttpServletResponse response) throws IOException, ParseException, net.minidev.json.parser.ParseException, NoSuchAlgorithmException {

            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(request.getParameter("rezervare"));

            System.out.println(json);

            JSONArray tickets = (JSONArray) json.get("tickets");
            String numeRezervare = json.getAsString("nume");
            String prenumeRezervare = json.getAsString("prenume");
            int cnpRezervare = (int)json.getAsNumber("cnp");
            int numarLocuri = (int)json.getAsNumber("nrLoc");
            String orasPlecare = json.getAsString("orasPlecare");
            String orasSosire = json.getAsString("orasSosire");


            String dateOnRequest = (String)json.getAsString("dataPlecare").split("T")[0];

            String dateOnRequest2;

            try{
                dateOnRequest2 = (String)json.getAsString("dataRetur").split("T")[0];
            } catch (Exception e){
                dateOnRequest2 = null;
            }

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = format.parse(dateOnRequest);

            String dateForDB = (new SimpleDateFormat("dd-MMM-yy").format(date)).toString().toUpperCase();
            String dataRetur = null;

            if(dateOnRequest2 != null){
                Date date1 = format.parse(dateOnRequest2);
                dataRetur = (new SimpleDateFormat("dd-MMM-yy").format(date1)).toString().toUpperCase();
            }

            //add rezervare dus
            int cursaID = getCursaId(orasPlecare, orasSosire, dateForDB);

            addRezervare(tickets, numeRezervare, prenumeRezervare, cnpRezervare, numarLocuri, cursaID, response);

            //add rezervare intors daca exista
            if(!dataRetur.equals(dateForDB)){
                cursaID = getCursaId(orasSosire, orasPlecare, dataRetur);
                addRezervare(tickets, numeRezervare, prenumeRezervare, cnpRezervare, numarLocuri, cursaID, response);
            }
        }

        private int getCursaId(String orasPlecare, String orasSosire, String dateForDB){
            int cursaID = 0;

            Connection connection = DataBaseConnector.getInstance().getConnection();
            CallableStatement statement = null;

            try {
                String sql = "{call GETCURSAID (?, ?, ?, ?)}";

                statement = connection.prepareCall(sql);

                statement.setString(1, orasPlecare);
                statement.setString(2, orasSosire);
                statement.setString(3, dateForDB);
                statement.registerOutParameter(4, OracleTypes.INTEGER);

                statement.execute();
                cursaID = ((OracleCallableStatement) statement).getInt(4);

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

            return cursaID;
        }


        private void addRezervare(JSONArray tickets, String nume, String prenume, int cnp, int nrLocuri, int cursaID, HttpServletResponse response) throws NoSuchAlgorithmException {

            Connection connection = DataBaseConnector.getInstance().getConnection();
            CallableStatement statement = null;

            try {
                String sql = "{call addrezervare (?, ?, ?, ?, ?, ?, ?, ?)}";

                statement = connection.prepareCall(sql);
                int idRezervare = (int)(System.currentTimeMillis() % 100000);
                statement.setInt(1,  idRezervare);

                statement.setString(2, nume);
                statement.setString(3, prenume);
                statement.setInt(4, cnp);
                statement.setInt(5, nrLocuri);

                //is paid....trebuie modificata
                statement.setString(6, "0");
                statement.setInt(7, cursaID);
                statement.setInt(8, PRET_BILET*tickets.size());


                statement.execute();
                response.setStatus(HttpServletResponse.SC_OK);

                statement.close();

                //adaug si biletele...
                int nrBileteReduse = addBilete(tickets, idRezervare, response) ;

                double suma = (double)(nrBileteReduse * PRET_BILET * 0.5 + (((JSONArray)tickets.get(0)).size()-nrBileteReduse)*PRET_BILET);

                updateRezervare(idRezervare, suma);
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

        private void updateRezervare(int rezervareid, double suma){
            Connection connection = DataBaseConnector.getInstance().getConnection();
            CallableStatement statement = null;

            try {
                String sql = "{call updateSuma (?, ?)}";
                statement = connection.prepareCall(sql);

                statement.setInt(1, rezervareid);
                statement.setDouble(2, suma);
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

        private int addBilete(JSONArray tickets, int rezervareId, HttpServletResponse response) {
            
            JSONArray jsonArray = (JSONArray) (tickets.get(0));
            int nrReduse = 0;
            for( int i = 0; i < tickets.size(); i++) {
                JSONObject object = ((JSONObject)jsonArray.get(i));
                String nume = object.getAsString("nume");
                String prenume = object.getAsString("prenume");
                int cnp = (int)object.getAsNumber("cnp");
                JSONObject locInfo = (JSONObject) object.get("loc");
                int loc = (int)locInfo.getAsNumber("loc");
                int reducere;

                try {
                    reducere = (((boolean)object.get("reducere")) == true)?1:0;
                    nrReduse++;
                } catch (Exception e){
                    reducere = 0;
                }

                String tip = locInfo.getAsString("tip").trim();

                Connection connection = DataBaseConnector.getInstance().getConnection();
                CallableStatement statement = null;


                try {
                    String sql = "{call add_tickets (?, ?, ?, ?, ?, ?, ?, ?)}";
                    statement = connection.prepareCall(sql);

                    statement.setInt(1,  (int)(System.currentTimeMillis() % 100000));
                    statement.setString(2, nume);
                    statement.setString(3, prenume);
                    statement.setInt(4, cnp);
                    statement.setLong(5, loc);
                    statement.setInt(6, reducere);
                    statement.setInt(7, rezervareId);
                    statement.setString(8,tip);

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

            return nrReduse;
        }
}