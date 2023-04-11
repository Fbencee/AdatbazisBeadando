import java.lang.Class;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Scanner;

public class Main {
    private static Connection connection;

    public static void main(String[] args) {
        DriverReg();

        Scanner sc = new Scanner(System.in);
        System.out.println("Please give me a username and a password!");
        System.out.println("username:");
        String username= sc.next();
        System.out.println("password");
        String password = sc.next();

        if (username.isEmpty() || username.isEmpty()) {
            connection = ConnectToDataBase("H22_TP0M8Y", "TP0M8Y");
        }
        else {
            connection = ConnectToDataBase(username, password);
        }

    }

    public static void DriverReg() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            System.out.println("Driver is registered successfully!");
        }catch (Exception e){
            System.err.println(e.getMessage());
        }
    }

    public static Connection ConnectToDataBase(String username, String password){

        String url = "jdbc:oracle:thin:@193.6.5.58:1521:XE";

        try{
             Connection connection = DriverManager.getConnection(url,username,password);
             System.out.println("Successful connection!");
             return connection;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static void Disconnect() {
        if (connection != null) {
            try{
                connection.close();
                System.out.println("Successful disconnection.");
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
    }

    public static void CreateTables(){
        try {

            Statement statement = connection.createStatement();
            String crStadion = "CREATE TABLE Stadion( StadionID int PRIMARY KEY, Nev varchar(30), Varos varchar(30), Cim varchar(20), Ferohely int NOT NULL);";
            statement.executeUpdate(crStadion);
            String crCsapat = "CREATE TABLE Csapat(CsapatID int PRIMARY KEY,Edzo varchar(20),CsNev varchar(30),Varos varchar(30),StadionID int NOT NULL,FOREIGN KEY(StadionID) REFERENCES Stadion(STADIONID));";
            statement.executeUpdate(crCsapat);
            String crMerkozes = "CREATE TABLE Merkozes(MerkozesID int PRIMARY KEY,HazaiGolokSzama int,VendegGolokSzama int,Idopont date NOT NULL,StadionID int NOT NULL,FOREIGN KEY(StadionID) REFERENCES Stadion(StadionID),GolKulonbseg AS (ABS(HazaiGolokSzama-VendegGolokSzama)));";
            statement.executeUpdate(crMerkozes);
            String crLabdarugo = "CREATE TABLE Labdarugo(LabdarugoID int PRIMARY KEY,LNev varchar(20),SzuletesiIdo date not null,Poszt varchar(30),Mezszam int,PreferaltLab varchar(10),CsapatID int NOT NULL,FOREIGN KEY(CsapatID) REFERENCES Csapat(CsapatID));";
            statement.executeUpdate(crLabdarugo);
            String crStatisztika = "CREATE TABLE Statisztika(StatisztikaID int PRIMARY KEY,JatekPerc int,SargaLap int,PirosLap int,Gol int,Golpassz int,LabdarugoID int NOT NULL,FOREIGN KEY(LabdarugoID) REFERENCES Labdarugo(LabdarugoID));";
            statement.executeUpdate(crStatisztika);
            String crLigak ="CREATE TABLE Ligak(LigaID int PRIMARY KEY,Liga varchar(40));";
            statement.executeUpdate(crLigak);
            String crCSL = "CREATE TABLE CS_L(CsapatID int NOT NULL,FOREIGN KEY(CsapatID) REFERENCES Csapat(CsapatID),LigaID int NOT NULL,FOREIGN KEY(LigaID) REFERENCES Ligak(LigaID));";
            statement.executeUpdate(crCSL);
            String crJatszik = "CREATE TABLE Jatszik(MerkozesID int NOT NULL,FOREIGN KEY(MerkozesID) REFERENCES Merkozes(MerkozesID),CsapatID int NOT NULL,FOREIGN KEY(CsapatID) REFERENCES Csapat(CsapatID));";
            statement.executeUpdate(crJatszik);
            System.out.println("All table was created successfully.");

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static void InsertData(){
        try{

        Statement stmt = connection.createStatement();
        String insertCmd;

        insertCmd = "INSERT INTO Stadion VALUES(1,'Old Trafford', 'Manchester', 'Sir Matt Busby Way',76000)";
        stmt.executeUpdate(insertCmd);
        insertCmd = "INSERT INTO Stadion VALUES(2,'Etihad Stadion', 'Manchester', 'Rowsley St',55097);";
        stmt.executeUpdate(insertCmd);
        insertCmd = "INSERT INTO Stadion VALUES(3,'Anfield Stadion', 'Liverpool', 'Anfield Rd',53394);";
        stmt.executeUpdate(insertCmd);
        insertCmd = "INSERT INTO Csapat VALUES(101,'Ole Gunnar Solskjaer', 'Manchester United FC', 'Manchester',1);";
        stmt.executeUpdate(insertCmd);
        insertCmd ="INSERT INTO Csapat VALUES(102,'Josep Guardiola', 'Manchester City FC', 'Manchester',2);";
        stmt.executeUpdate(insertCmd);
        insertCmd ="INSERT INTO Csapat VALUES(103,'Jürgen Klopp', 'Liverpool FC', 'Liverpool',3);";
        stmt.executeUpdate(insertCmd);

        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

