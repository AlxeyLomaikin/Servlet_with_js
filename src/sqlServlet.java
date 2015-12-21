/**
 * Created by AlexL on 15.12.2015.
 */
import javax.servlet.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Map;

public class sqlServlet extends GenericServlet {
    private final String tab = "&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp";
    private Connection con;

    //list of all DatabaseNames and name of selected DB
    private ArrayList<String> dbNames = new ArrayList<>();
    private String curDatabase = "";

    //Result of execution of query
    private ArrayList<String> queryResult = new ArrayList<>();

    @Override
    public void init() {
        try{
            Class.forName("com.mysql.jdbc.Driver");

            //get DBases names
            con = DriverManager.getConnection("jdbc:mysql://localhost/", "root", "smosh4071");
            DatabaseMetaData dbMeta = con.getMetaData();
            ResultSet resSc = dbMeta.getCatalogs();
            while (resSc.next()) {
                dbNames.add(resSc.getString("TABLE_CAT"));
            }
            resSc.close();
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        try{
            if (con!=null)con.close();
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void service(ServletRequest servletRequest,
                        ServletResponse servletResponse) throws ServletException, IOException {
        sendSqlForm(servletRequest, servletResponse);
    }


    private void sendSqlForm(ServletRequest servletRequest,
                             ServletResponse servletResponse) throws ServletException, IOException{
        PrintWriter w = servletResponse.getWriter();
        w.println("<HTML>");
        w.println("<HEAD>");
        w.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"first.css\">");
        w.println("<TITLE>SQL Servlet</TITLE>");
        w.println("<script src=\"/scripts/validate.js\"></script>");
        w.println("<script src=\"/scripts/change_data.js\"></script>");
        w.println("</HEAD>");
        w.println("<BODY>");

        String sql = servletRequest.getParameter("query");
        String use = servletRequest.getParameter("use");
        String selectedTable = servletRequest.getParameter("selectedTab");
        String del_id = servletRequest.getParameter("del_id");
        String edit_id = servletRequest.getParameter("edit_id");
        String add = servletRequest.getParameter("add");

        boolean isTableSelected = ( (selectedTable!=null) && (!selectedTable.equals("")) );
        boolean hasQuery = ( (sql!=null) && (!sql.equals("")) );
        boolean needExecute = ( !(!curDatabase.equals("") && isTableSelected) && hasQuery );

        if (use!=null)
            useDB("use "+ use + ";", w);

        if (needExecute ) {
            switch (Query_Types.parseQuery(sql)){
                case Query_Types.SELECT_DATA:
                    break;
                default:
                    executeSql(sql.trim(), servletResponse);
                    break;
            }

        }

        boolean isDatabaseSelected = (!curDatabase.equals(""));

        w.println("<pre>Databases:</pre>");

        //links to change database
        for (int i=0;i<dbNames.size();i++){

            String href = "href=\"/sql?use=" + dbNames.get(i);
            if ( hasQuery )
                href+="&query=" + sql;
            href+="\"";

            if (dbNames.get(i).equals(curDatabase))
                w.print("<a class=\"active\">" + dbNames.get(i) + "</a>" + tab);
            else
                w.print("<a " + href + ">" + dbNames.get(i) + "</a>" + tab);
        }

        if ( isDatabaseSelected ) {
            ArrayList<String> tables = getTabNames(curDatabase);
            w.println("<p>Choose table:");
            w.println("<p><select name=\"selectedTab\" onchange=\"form_submit()\" form=sql_form>");
            w.println("<option> </option>");
            for (String table : tables) {
                if ( (selectedTable != null) && (table.equals(selectedTable)) )
                    w.println("<option selected>" + table + "</option>");
                else
                    w.println("<option>" + table + "</option>");
            }
            w.println("</select>");

            if ( isTableSelected ) {
                if (del_id != null && !del_id.equals(""))
                    executeUpdQuery("delete from " + selectedTable + " where id=\"" + del_id + "\";", w);
                else if (add !=null && add.equals("true")){
                    Map<String, String[]> params = servletRequest.getParameterMap();
                    ArrayList<String> record = new ArrayList<>();
                    ArrayList<String> columns = getColumns(selectedTable);
                    for (String key : params.keySet()) {
                        if (columns.contains(key))
                            record.add(params.get(key)[0]);
                    }
                    if ( columns.size() == record.size() + 1 ) {
                        String updQuery = "insert into doctor values (null, \"" + record.get(0) + "\", \""
                                + record.get(1) + "\", \"" + record.get(2) + "\", \"" + record.get(3) + "\");";
                        executeUpdQuery(updQuery, w);
                    } else queryResult.add("Wrong options!");
                }
                else if (edit_id != null && !edit_id.equals("")) {
                    Map<String, String[]> params = servletRequest.getParameterMap();
                    ArrayList<String> record = new ArrayList<>();
                    ArrayList<String> columns = getColumns(selectedTable);
                    for (String key : params.keySet()) {
                        if (columns.contains(key))
                            record.add(params.get(key)[0]);
                    }
                    if ( columns.size() == record.size() + 1 ) {
                        String updQuery = "update doctor set name=\""
                                + record.get(0) + "\", surname=\"" + record.get(1) + "\", occupation=\"" +
                                record.get(2) + "\", age=\"" + record.get(3) + "\" where id=\"" + edit_id + "\";";
                        executeUpdQuery(updQuery, w);
                    } else queryResult.add("Wrong options!");
                }
                printSelectedTable(selectedTable, w);
            }
        }

        w.println("<p>Please, type your request");
        w.println("<br><FORM onsubmit=\"return check_query()\" id=\"sql_form\" action=/sql method=service>");
        if ( isDatabaseSelected  && isTableSelected ) {
            w.println("<TEXTAREA disabled name=query id=\"sql_area\" " +
                    "onchange=\"check_query()\"  cols=90 rows=8>");
            w.println ("Not available when the table is selected");
        }
        else {
            w.println("<TEXTAREA name=query id=\"sql_area\" " +
                    "onchange=\"check_query())\" onfocus=\"check_query())\" cols=90 rows=8>");
        }
        if( hasQuery && !isTableSelected ) {
            w.print(sql);
        }

        w.println("</TEXTAREA>");
        if ( isDatabaseSelected && isTableSelected )
            w.println("<INPUT TYPE=submit disabled id=\"send\" value=execute>");
        else
            w.println("<INPUT TYPE=submit id=\"send\" value=execute>");
        w.println("</FORM>");

        w.println("<BR>");

        if ( !isDatabaseSelected )
            w.println ("Database not selected!");

        if ( needExecute && Query_Types.parseQuery(sql) == Query_Types.SELECT_DATA )
            if ( isDatabaseSelected )
                selectData(sql, w, false);
            else queryResult.add("Can't execute: database not selected!");

        for (int i =0; i<queryResult.size(); i++){
            w.println("<p>" + queryResult.get(i));
        }

        w.println("</BODY>");
        w.println("</HTML>");
        w.close();
        queryResult.clear();
    }


    private void executeSql(String sql, ServletResponse response) throws ServletException, IOException{
        PrintWriter w = response.getWriter();
        boolean isDatabaseSelected = (!curDatabase.equals(""));
        int query_type = Query_Types.parseQuery(sql);
        switch (query_type){
            case Query_Types.CREATE_DATABASE:
                createDB(sql, w);
                break;
            case Query_Types.DROP_DATABASE:
                dropDB(sql, w);
                break;
            case Query_Types.USE_DATABASE:
                useDB(sql, w);
                break;
            case Query_Types.SELECT_DATA:
                if ( isDatabaseSelected )
                    selectData(sql, w, false);
                else queryResult.add("Can't execute: database not selected!");
                break;
            case Query_Types.UPDATE_QUERY:
                if ( isDatabaseSelected )
                    executeUpdQuery(sql,w);
                else queryResult.add("Can't execute: database not selected!");
                break;
            default:
                queryResult.add("Incorrect request!");
                break;
        }
    }

    private ArrayList<String> getColumns (String tabName){
        ArrayList<String> colNames = new ArrayList<>();
        try{
            ResultSet rs = con.getMetaData().getColumns(null, null, tabName, null);
            while (rs.next()) {
                colNames.add(rs.getString("COLUMN_NAME"));
            }
            rs.close();
        }catch (SQLException ex){
            ex.printStackTrace();
        }
        return colNames;
    }

    private ArrayList<String> getTabNames(String dbName){
        ArrayList<String> tabNames = new ArrayList<>();
        try{
            ResultSet rs = con.getMetaData().getTables(dbName, null, null, null);
            while (rs.next()) {
                tabNames.add(rs.getString("TABLE_NAME"));
            }
            rs.close();
        }catch (SQLException ex){
            ex.printStackTrace();
        }
        return tabNames;
    }

    private void printSelectedTable(String tabName, PrintWriter w){
        selectData("select * from " + tabName + ";", w, true);
    }

    private void useDB(String sql, PrintWriter w){
        String dbName = sql.split(" ")[1];
        if (dbName.charAt(dbName.length()-1)==';')
            dbName = dbName.replace(";","");

        //if database exist and not selected at this moment
        if ( (!dbName.equals(curDatabase)) && (dbNames.contains(dbName)) )
            try{
                if (con!=null)
                    con.close();
                con = DriverManager.getConnection("jdbc:mysql://localhost/" + dbName, "root", "smosh4071");
                this.curDatabase = dbName;
                queryResult.add("Database was changed");
            }catch (SQLException ex){
                queryResult.add(ex.getMessage());
            }
        else if (!dbNames.contains(dbName)) {
            try{
                if (con!=null)
                    con.close();
                con = DriverManager.getConnection("jdbc:mysql://localhost/", "root", "smosh4071");
            }catch (SQLException ex){
                w.println(ex.getMessage());
            }
            this.curDatabase="";
            queryResult.add("Database with name `" + dbName + "` is not exist!");
        }
    }

    private void selectData (String sql, PrintWriter w, boolean isEditable){
        if (con!=null) {
            Statement st = null;
            ResultSet rs = null;
            try {
                st = con.createStatement();
                rs = st.executeQuery(sql);
                w.append("<p><table>");

                if ( !isEditable )
                    w.append("<caption><b>Result of selection :</b></caption>");

                int colCount = rs.getMetaData().getColumnCount();
                w.append("<tr class=\"header\">");
                for (int i = 1; i <= colCount; i++) {
                    w.append("<th>");
                    w.append(rs.getMetaData().getColumnName(i));
                    w.append("</th>");
                }

                if ( isEditable ){
                    w.append("<th>change data</th>");
                }

                w.append("</tr>");


                while (rs.next()) {
                    w.print("<tr id=\"tr" + rs.getString("id") + "\">");
                    for (int i = 1; i <= colCount; i++) {
                        String td = "<td>" + rs.getString(i) + "</td>";
                        w.append(td);
                    }

                    if ( isEditable ){
                        w.print("<td><input type=\"button\" value=edit onclick=\"highlight_tr(this); edit_record(" +
                                rs.getString("id") + ")\"></input>&nbsp&nbsp");
                        w.print("<input type=button value=delete onclick=\"del_record("
                                + rs.getString("id") + ")\"></input></td>");
                    }

                    w.append("</tr>");
                }

                if ( isEditable ){
                    w.print ("<tr>");
                    for (int i = 1; i <= colCount; i++)
                        w.println("<td> </td>");
                    w.print("<td><input type=button value=\"add record\" onclick=\"highlight_tr(this); add_record(this)\"></input></td>");
                    w.print("</tr>");
                }

                w.append("</table>");
                w.flush();
            } catch (SQLException ex) {
                queryResult.add(ex.getMessage());
            }finally{
                try {
                    if (st != null) st.close();
                    if (rs != null) rs.close();
                }catch(SQLException ex){
                    queryResult.add(ex.getMessage());
                }
            }
        }
        else queryResult.add("Connection error");
    }

    private void executeUpdQuery(String sql, PrintWriter w) {
        if (con != null) {
            Statement st = null;
            try {
                st = con.createStatement();
                st.executeUpdate(sql);
            } catch (SQLException ex) {
                queryResult.add(ex.getMessage());
            }finally{
                try{
                    if (st!=null) st.close();
                }catch (SQLException ex){
                    queryResult.add(ex.getMessage());
                }
            }
        }
        else queryResult.add("Connection error");
    }

    private void dropDB(String sql, PrintWriter w){
        if (con != null) {
            Statement statement = null;
            try {
                statement = con.createStatement();
                String dbName = sql.split(" ")[2];
                if (dbName.charAt(dbName.length() - 1) == ';') {
                    dbName = dbName.replace(";", "");
                } else
                    sql+=";";
                statement.executeUpdate(sql);
                dbNames.remove(dbName);
                queryResult.add("database `"+dbName+"` was successfully droped!");
            } catch (SQLException ex) {
                queryResult.add(ex.getMessage());
            } finally {
                try {
                    if (statement != null) statement.close();
                } catch (SQLException ex) {
                    queryResult.add(ex.getMessage());
                }
            }
        }
        else queryResult.add("connection error");
    }

    private void createDB(String sql, PrintWriter w) {
        if (con != null) {
            Statement statement = null;
            try {
                statement = con.createStatement();
                String dbName = sql.split(" ")[2];
                if (dbName.charAt(dbName.length() - 1) == ';') {
                    dbName = dbName.replace(";", "");
                }
                else
                    sql+=";";
                statement.executeUpdate(sql);
                dbNames.add(dbName);
                queryResult.add("database `"+dbName+"` was successfully created");
            } catch (SQLException ex) {
                queryResult.add(ex.getMessage());
            } finally {
                try {
                    if (statement != null) statement.close();
                } catch (SQLException ex) {
                    queryResult.add(ex.getMessage());
                }
            }
        }
        else queryResult.add("connection error");
    }

}

