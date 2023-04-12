import java.io.FileWriter;
import java.io.IOException;
import java.lang.Class;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AdatbazisBeadando {
    private static Connection connection;

    public static void main(String[] args) throws SQLException, ParseException {
        DriverReg();

        Scanner sc = new Scanner(System.in);
        System.out.println("Please give me a username and a password!");
        System.out.println("username:");
        String username = sc.next();
        System.out.println("password:");
        String password = sc.next();


        connection = ConnectToDataBase(username, password);

        ShowActions();

        boolean ok = false;
        do {
            System.out.println("Choose a number:");
            int number = Integer.parseInt(sc.next());
            switch (number) {
                case 0:
                    CreateTables();
                    break;
                case 1:
                    DeleteTables();
                    break;
                case 2:
                    InsertData();
                    break;
                case 3:
                    WriteOut(SelectAllFromMerkozes());
                    break;
                case 4:
                    WriteOut(SelectAllFromStadion());
                    break;
                case 5:
                    System.out.println("Please give me the data which you would like to insert!");
                    System.out.println("ID:");
                    int merkozesID = sc.nextInt();
                    System.out.println("Home Goals:");
                    int hazaiG = sc.nextInt();
                    System.out.println("Away Goals:");
                    int vendegG = sc.nextInt();
                    System.out.println("Date(format:9/28/2021):");
                    java.util.Date utilDate = new SimpleDateFormat("dd/MM/yyyy").parse(sc.next());
                    java.sql.Date sqlDate = new Date(utilDate.getTime());
                    System.out.println(sqlDate);
                    System.out.println("Stadium ID:");
                    int stadionID = sc.nextInt();
                    DataInsertToMerkozes(merkozesID, hazaiG, vendegG, sqlDate, stadionID);
                    break;
                case 6:
                    GetTableNames();
                    break;
                case 7:
                    ShowActions();
                    break;
                case 8:
                    Disconnect();
                    ok = true;
                    break;
                case 9:
                    WriteToTxt(ConvertRsToList(SelectAllFromStadion()));
                    break;
                default:
                    System.out.println("That action not exists.");
                    break;
            }

        } while (!ok);

    }

    public static void ShowActions() {
        System.out.println("There is the actions you can choose:");
        System.out.println("0 : Create the tables.");
        System.out.println("1 : Delete the tables.");
        System.out.println("2 : Insert data to the tables.");
        System.out.println("3 : Select data from Merkozes table.");
        System.out.println("4 : Select data from Stadion table.");
        System.out.println("5 : Insert a new data row to the Merkozes table");
        System.out.println("6 : List all the tables in the database.");
        System.out.println("7 : Show the actions again.");
        System.out.println("8 : Leave the application.");
    }

    public static void DriverReg() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            System.out.println("Driver is registered successfully!");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static Connection ConnectToDataBase(String username, String password) {

        String url = "jdbc:oracle:thin:@193.6.5.58:1521:XE";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("Successful connection!");
            return connection;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static void Disconnect() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Successful disconnection.");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void CreateTables() {
        try {


            Statement statement = connection.createStatement();

            String crStadion = "CREATE TABLE Stadion ( StadionID int PRIMARY KEY, Nev varchar(30), Varos varchar(30), Cim varchar(20), Ferohely int NOT NULL)";
            statement.executeUpdate(crStadion);
            String crCsapat = "CREATE TABLE Csapat ( CsapatID int PRIMARY KEY,Edzo varchar(20),CsNev varchar(30),Varos varchar(30),StadionID int NOT NULL, FOREIGN KEY(StadionID) REFERENCES Stadion(StadionID) )";
            statement.executeUpdate(crCsapat);
            String crMerkozes = "CREATE TABLE Merkozes (MerkozesID int PRIMARY KEY,HazaiGolokSzama int,VendegGolokSzama int,Idopont date NOT NULL,StadionID int NOT NULL,FOREIGN KEY(StadionID) REFERENCES Stadion(StadionID),GolKulonbseg AS (ABS(HazaiGolokSzama-VendegGolokSzama)))";
            statement.executeUpdate(crMerkozes);
            String crLabdarugo = "CREATE TABLE Labdarugo (LabdarugoID int PRIMARY KEY,LNev varchar(20),SzuletesiIdo date not null,Poszt varchar(30),Mezszam int,PreferaltLab varchar(10),CsapatID int NOT NULL,FOREIGN KEY(CsapatID) REFERENCES Csapat(CsapatID))";
            statement.executeUpdate(crLabdarugo);
            String crStatisztika = "CREATE TABLE Statisztika (StatisztikaID int PRIMARY KEY,JatekPerc int,SargaLap int,PirosLap int,Gol int,Golpassz int,LabdarugoID int NOT NULL,FOREIGN KEY(LabdarugoID) REFERENCES Labdarugo(LabdarugoID))";
            statement.executeUpdate(crStatisztika);
            String crLigak = "CREATE TABLE Ligak (LigaID int PRIMARY KEY,Liga varchar(40))";
            statement.executeUpdate(crLigak);
            String crCSL = "CREATE TABLE CSL (CsapatID int NOT NULL,FOREIGN KEY(CsapatID) REFERENCES Csapat(CsapatID),LigaID int NOT NULL,FOREIGN KEY(LigaID) REFERENCES Ligak(LigaID))";
            statement.executeUpdate(crCSL);
            String crJatszik = "CREATE TABLE Jatszik (MerkozesID int NOT NULL,FOREIGN KEY(MerkozesID) REFERENCES Merkozes(MerkozesID),CsapatID int NOT NULL,FOREIGN KEY(CsapatID) REFERENCES Csapat(CsapatID))";
            statement.executeUpdate(crJatszik);
            System.out.println("All table was created successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public static void InsertData() {
        try {

            Statement stmt = connection.createStatement();
            String insertCmd;

            insertCmd = "INSERT INTO Stadion VALUES ( 1,'Old Trafford', 'Manchester', 'Sir Matt Busby Way',76000)";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO Stadion VALUES ( 2,'Etihad Stadion', 'Manchester', 'Rowsley St',55097)";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO Stadion VALUES ( 3,'Anfield Stadion', 'Liverpool', 'Anfield Rd',53394)";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO Csapat VALUES ( 101,'Ole Gunnar Solskjaer', 'Manchester United FC', 'Manchester',1)";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO Csapat VALUES ( 102,'Josep Guardiola', 'Manchester City FC', 'Manchester',2)";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO Csapat VALUES ( 103,'Jürgen Klopp', 'Liverpool FC', 'Liverpool',3)";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO Ligak VALUES ( 301,'Premier League')";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO Ligak VALUES ( 302,'Football League Chamiponship')";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO Ligak VALUES ( 303,'Football League One' )";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO Labdarugo VALUES ( 401,'Jamie Vardy', TO_DATE('1987-11-1','YYYY-MM-DD) , 'ST', 9, 'jobb', 102 )";
            stmt.executeUpdate(insertCmd);
            /*insertCmd = "INSERT INTO Labdarugo VALUES ( 402,'Youri Tielemans', TO_DATE('1997-7-5','YYYY-MM-DD), 'CM', 8, 'jobb', 101)";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO CSL VALUES ( 101,301)";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO CSL VALUES ( 102,301)";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO CSL VALUES ( 103,302)";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO Labdarugo VALUES ( 403,'Tyrone Mings', TO_DATE('1993-3-13','YYYY-MM-DD), 'CB', 5, 'jobb', 102)";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO Labdarugo VALUES ( 404,'John McGinn', TO_DATE('1994-10-18','YYYY-MM-DD), 'CDM', 7, 'bal', 103)";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO Merkozes ( MerkozesID, HazaiGolokSzama, VendegGolokSzama, Idopont,StadionID) VALUES(501, 4, 1, TO_DATE('2021-9-28','YYYY-MM-DD), 2)";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO Merkozes ( MerkozesID, HazaiGolokSzama, VendegGolokSzama, Idopont,StadionID) VALUES(502, 2, 2, TO_DATE('2021-10-11','YYYY-MM-DD), 2)";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO Merkozes ( MerkozesID, HazaiGolokSzama, VendegGolokSzama, Idopont,StadionID) VALUES(503, 6, 0, TO_DATE('2021-9-17','YYYY-MM-DD), 3)";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO Statisztika VALUES ( 601, 540, 5, 0, 10, 3, 401)";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO Statisztika VALUES ( 602, 367, 2, 0, 1, 7, 402)";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO Statisztika VALUES ( 603, 256, 7, 1, 1, 0, 403)";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO Jatszik VALUES ( 501, 102)";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO Jatszik VALUES ( 501, 101)";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO Jatszik VALUES ( 502, 103)";
            stmt.executeUpdate(insertCmd);*/
            System.out.println("Successful data insert.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void DataInsertToMerkozes(int merkozesId, int hazaiG, int vendegG, Date idopont, int stadionId) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO Merkozes ( MerkozesID, HazaiGolokSzama, VendegGolokSzama, Idopont,StadionID) VALUES (?,?,?,?,?)");

        stmt.setInt(1, merkozesId);
        stmt.setInt(2, hazaiG);
        stmt.setInt(3, vendegG);
        stmt.setDate(4, idopont);
        stmt.setInt(5, stadionId);
        System.out.println("Successful insert.");
    }

    public static void DeleteTables() throws SQLException {
        Statement stmt = connection.createStatement();

        String dropCmd;
        dropCmd = "DROP TABLE Jatszik";
        stmt.executeUpdate(dropCmd);
        dropCmd = "DROP TABLE CSL";
        stmt.executeUpdate(dropCmd);
        dropCmd = "DROP TABLE Ligak";
        stmt.executeUpdate(dropCmd);
        dropCmd = "DROP TABLE Statisztika";
        stmt.executeUpdate(dropCmd);
        dropCmd = "DROP TABLE Labdarugo";
        stmt.executeUpdate(dropCmd);
        dropCmd = "DROP TABLE Merkozes";
        stmt.executeUpdate(dropCmd);
        dropCmd = "DROP TABLE Csapat";
        stmt.executeUpdate(dropCmd);
        dropCmd = "DROP TABLE Stadion";
        stmt.executeUpdate(dropCmd);
        System.out.println("Successful deletion.");
    }

    public static ResultSet SelectAllFromStadion() throws SQLException {
        Statement stmt = connection.createStatement();

        String cmd = "Select * FROM Stadion";
        ResultSet rs = stmt.executeQuery(cmd);

        System.out.println("Successful select.");
        return rs;
    }

    public static ResultSet SelectAllFromMerkozes() throws SQLException {
        Statement stmt = connection.createStatement();

        String queryCmd = "SELECT * FROM Merkozes";
        ResultSet rs = stmt.executeQuery(queryCmd);

        WriteOut(rs);
        rs.close();
        stmt.close();
        System.out.println("Successful select.");
        return rs;
    }

    public static ResultSet SelectAllFromCsapat() throws SQLException {
        Statement stmt = connection.createStatement();

        String queryCmd = "SELECT * FROM Csapat";
        ResultSet rs = stmt.executeQuery(queryCmd);

        WriteOut(rs);
        rs.close();
        stmt.close();
        System.out.println("Successful select.");
        return rs;
    }

    public static ResultSet SelectAllFromLabdarugo() throws SQLException {
        Statement stmt = connection.createStatement();

        String queryCmd = "SELECT * FROM Labdarugo";
        ResultSet rs = stmt.executeQuery(queryCmd);

        WriteOut(rs);
        rs.close();
        stmt.close();
        System.out.println("Successful select.");
        return rs;
    }

    public static ResultSet SelectAllFromStatisztika() throws SQLException {
        Statement stmt = connection.createStatement();

        String queryCmd = "SELECT * FROM Statisztika";
        ResultSet rs = stmt.executeQuery(queryCmd);

        WriteOut(rs);
        rs.close();
        stmt.close();
        System.out.println("Successful select.");
        return rs;
    }

    public static ResultSet SelectAllFromLigak() throws SQLException {
        Statement stmt = connection.createStatement();

        String queryCmd = "SELECT * FROM Ligak";
        ResultSet rs = stmt.executeQuery(queryCmd);

        WriteOut(rs);
        rs.close();
        stmt.close();
        System.out.println("Successful select.");
        return rs;
    }

    public static ResultSet SelectAllFromCsL() throws SQLException {
        Statement stmt = connection.createStatement();

        String queryCmd = "SELECT * FROM CSL";
        ResultSet rs = stmt.executeQuery(queryCmd);

        WriteOut(rs);
        rs.close();
        stmt.close();
        System.out.println("Successful select.");
        return rs;
    }

    public static ResultSet SelectAllFromJatszik() throws SQLException {
        Statement stmt = connection.createStatement();

        String queryCmd = "SELECT * FROM Jatszik";
        ResultSet rs = stmt.executeQuery(queryCmd);

        WriteOut(rs);
        rs.close();
        stmt.close();
        System.out.println("Successful select.");
        return rs;
    }

    public static void WriteOut(ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnsNumber = rsmd.getColumnCount();

        while (rs.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1) System.out.print(",  ");
                String columnValue = rs.getString(i);
                System.out.print(columnValue + " " + rsmd.getColumnName(i));
            }
            System.out.println("");
        }
    }

    public static void GetTableNames() throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet rs = meta.getTables(null, null, "%", null);
        ArrayList<String> v = new ArrayList<>();
        while (rs.next()) {
            v.add(rs.getString(3));
        }

        for (String tableName : v) {
            System.out.println(tableName);
        }
    }

    public static List<String> ConvertRsToList(ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        List<String> data = new ArrayList<>();
        boolean headerNeeded = true;

        while (rs.next()) {
            String record = "";
            if(headerNeeded) {
                for (int i = 1; i < columnsNumber; i++) {
                    record += rsmd.getColumnName(i) + ", ";
                }
                data.add(record);
                record = "";
            }
            for(int i = 1; i < columnsNumber; i++) {
                record += rs.getString(i) + ", ";
            }
            data.add(record);
            headerNeeded = false;
        }

        return data;
    }

    public static void WriteToTxt(List<String> data){
        try {
            FileWriter txtWriter = new FileWriter("data.txt");

            for (String oneData: data) {
                txtWriter.write(oneData + "\n");
            }

            txtWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("ERROR:");
            e.printStackTrace();
        }
    }

}
