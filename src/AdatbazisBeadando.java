import java.lang.Class;
import java.sql.*;
import java.util.Scanner;

public class AdatbazisBeadando {
    private static Connection connection;

    public static void main(String[] args) throws SQLException {
        DriverReg();

        Scanner sc = new Scanner(System.in);
        System.out.println("Please give me a username and a password!");
        System.out.println("username:");
        String username = sc.next();
        System.out.println("password");
        String password = sc.next();

        if (username.isEmpty() || username.isEmpty()) {
            connection = ConnectToDataBase("H22_TP0M8Y", "TP0M8Y");
        } else {
            connection = ConnectToDataBase(username, password);
        }


        boolean ok = false;
        do {

            System.out.println("Choose a number between 0 and 5");
            int number = sc.nextInt();
            switch (number) {
                case 0:
                    DeleteTables();
                    break;
                case 1:
                    CreateTables();
                    break;
                case 2:
                    Disconnect();
                    ok=true;
                    break;
                case 3:
                    InsertData();
                    break;
                case 4:
                    Select();
                    break;
            }
        } while (!ok);

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
            String crMerkozes = "CREATE TABLE Merkozes(MerkozesID int PRIMARY KEY,HazaiGolokSzama int,VendegGolokSzama int,Idopont date NOT NULL,StadionID int NOT NULL,FOREIGN KEY(StadionID) REFERENCES Stadion(StadionID),GolKulonbseg AS (ABS(HazaiGolokSzama-VendegGolokSzama)))";
            statement.executeUpdate(crMerkozes);
            String crLabdarugo = "CREATE TABLE Labdarugo(LabdarugoID int PRIMARY KEY,LNev varchar(20),SzuletesiIdo date not null,Poszt varchar(30),Mezszam int,PreferaltLab varchar(10),CsapatID int NOT NULL,FOREIGN KEY(CsapatID) REFERENCES Csapat(CsapatID))";
            statement.executeUpdate(crLabdarugo);
            String crStatisztika = "CREATE TABLE Statisztika(StatisztikaID int PRIMARY KEY,JatekPerc int,SargaLap int,PirosLap int,Gol int,Golpassz int,LabdarugoID int NOT NULL,FOREIGN KEY(LabdarugoID) REFERENCES Labdarugo(LabdarugoID))";
            statement.executeUpdate(crStatisztika);
            String crLigak = "CREATE TABLE Ligak(LigaID int PRIMARY KEY,Liga varchar(40))";
            statement.executeUpdate(crLigak);
            String crCSL = "CREATE TABLE CSL(CsapatID int NOT NULL,FOREIGN KEY(CsapatID) REFERENCES Csapat(CsapatID),LigaID int NOT NULL,FOREIGN KEY(LigaID) REFERENCES Ligak(LigaID))";
            statement.executeUpdate(crCSL);
            String crJatszik = "CREATE TABLE Jatszik(MerkozesID int NOT NULL,FOREIGN KEY(MerkozesID) REFERENCES Merkozes(MerkozesID),CsapatID int NOT NULL,FOREIGN KEY(CsapatID) REFERENCES Csapat(CsapatID))";
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

            insertCmd = "INSERT INTO Stadion VALUES(1,'Old Trafford', 'Manchester', 'Sir Matt Busby Way',76000)";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO Stadion VALUES(2,'Etihad Stadion', 'Manchester', 'Rowsley St',55097)";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO Stadion VALUES(3,'Anfield Stadion', 'Liverpool', 'Anfield Rd',53394)";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO Csapat VALUES(101,'Ole Gunnar Solskjaer', 'Manchester United FC', 'Manchester',1)";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO Csapat VALUES(102,'Josep Guardiola', 'Manchester City FC', 'Manchester',2)";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO Csapat VALUES(103,'Jürgen Klopp', 'Liverpool FC', 'Liverpool',3)";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO Ligak VALUES(301,'Premier League')";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO Ligak VALUES(302,'Football League Chamiponship')";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO Ligak VALUES(303,'Football League One')";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO CSL VALUES(101,301)";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO CSL VALUES(102,301)";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO CSL VALUES(104,302)";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO Labdarugo VALUES(401,'Jamie Vardy', '1/11/1987', 'ST', 9, 'jobb', 110)";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO Labdarugo VALUES(402,'Youri Tielemans', '5/7/1997', 'CM', 8, 'jobb', 110)";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO Labdarugo VALUES(403,'Tyrone Mings', '3/13/1993', 'CB', 5, 'jobb', 109)";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO Labdarugo VALUES(404,'John McGinn', '10/18/1994', 'CDM', 7, 'bal', 109)";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO Merkozes ( MerkozesID, HazaiGolokSzama, VendegGolokSzama, Idopont,StadionID) VALUES(501, 4, 1, '9/28/2021', 2)";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO Merkozes ( MerkozesID, HazaiGolokSzama, VendegGolokSzama, Idopont,StadionID) VALUES(502, 2, 2, '11/10/2021', 2)";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO Merkozes ( MerkozesID, HazaiGolokSzama, VendegGolokSzama, Idopont,StadionID) VALUES(503, 6, 0, '9/17/2021', 3)";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO Statisztika VALUES(601, 540, 5, 0, 10, 3, 401)";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO Statisztika VALUES(602, 367, 2, 0, 1, 7, 402)";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO Statisztika VALUES(603, 256, 7, 1, 1, 0, 403)";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO Jatszik VALUES(501, 102)";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO Jatszik VALUES(501, 110)";
            stmt.executeUpdate(insertCmd);
            insertCmd = "INSERT INTO Jatszik VALUES(502, 102)";
            stmt.executeUpdate(insertCmd);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void DataInsertToMerkozes(int merkozesId, int hazaiG, int vendegG, Date idopont, int stadionId) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO Merkozes VALUES (?,?,?,?,?)");

        stmt.setInt(0, merkozesId);
        stmt.setInt(1, hazaiG);
        stmt.setInt(2, vendegG);
        stmt.setDate(3, idopont);
        stmt.setInt(4, stadionId);
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

    }

    public static void Select() throws SQLException {
        Statement stmt = connection.createStatement();

        String cmd= "Select * FROM Stadion";
        ResultSet rs = stmt.executeQuery(cmd);
    }
}

