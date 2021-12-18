package DB;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;

public class DBQuery {

	public static void main(String[] args) {
	}

	private static final Connection con = getConnection();

	public static Connection getConnection() { // connect DB
		try {
			String driver = "com.mysql.cj.jdbc.Driver";
			String user = "root";
			String password = "cjdcnstkfkd1!";
			String url = "jdbc:mysql://localhost:3306/Bank";
			Class.forName(driver);

			Connection con = DriverManager.getConnection(url, user, password);
			return con;

		} catch (Exception e) {
			System.out.println("The Connection Fail");

			System.out.println(e.getMessage());
			return null;
		}
	}

	public static void createTable() { // createTable
		try {
			PreparedStatement createCustomer = con
					.prepareStatement("CREATE TABLE IF NOT EXISTS CUSTOMER(" + "csResNum INT NOT NULL PRIMARY KEY,"
							+ "csName VARCHAR(20)," + "csAddress VARCHAR(20)," + "csBirth DATE," // xxxx xxxx
							+ "csPhoneNum VARCHAR(20)," + "csJob VARCHAR(20))");

			PreparedStatement createAccount = con.prepareStatement("CREATE TABLE IF NOT EXISTS Account("
					+ "accID INT NOT NULL PRIMARY KEY," + "accKind VARCHAR(20)," // free
																					// xxxx,
																					// savings
																					// xxxxx
					+ "accBalance INT," + "accCard VARCHAR(10) NOT NULL DEFAULT 0," + "accBirth DATE,"
					+ "accCsNAme VARCHAR(20)," + "accPhone VARCHAR(20)," + "accEmail VARCHAR(40)," + "accResNum INT,"
					+ "FOREIGN KEY(accResNum) REFERENCES CUSTOMER(csResNum) ON DELETE CASCADE )");

			PreparedStatement createAccountTransaction = con.prepareStatement(
					"CREATE TABLE IF NOT EXISTS AccountHistory( transAccID INT,transDate Timestamp,transNum INT,"
							+ "transAccKind VARCHAR(20)," + "transContent VARCHAR(20)," + "transAmount INT,"
							+ "transBalance INT,transKind varchar(20),"
							+ "Primary KEY(transAccID,transDate,transNum), FOREIGN KEY(transAccID) REFERENCES ACCOUNT(accID))");

			PreparedStatement createCard = con.prepareStatement( // card는 체크카드와 선불카드
					"CREATE TABLE IF NOT EXISTS CARD(" + "cardID INT," + "cardBirth DATE," // make
					// 카드 고유 번호 , 카드 만든 날
							+ "cardDate Timestamp," // 카드 결제일자
							+ "cardKind VARCHAR(20)," // 선불, 체크
							+ "cardResNum INT," + "cardAccID INT," // check 카드만 존
							+ "cardBalance INT," // 선불은 자기가 갖고 있음 , 체크카드는 계좌 잔고,
							+ "FOREIGN KEY(cardResNum) REFERENCES CUSTOMER(csResNum) ON DELETE CASCADE ,"
							+ "PRIMARY KEY(cardID,cardDate) ,"
							+ "FOREIGN KEY(cardAccID) REFERENCES Account(accID) ON DELETE CASCADE )");

			createCustomer.execute();
			createAccount.execute();
			createAccountTransaction.execute();
			createCard.execute();

			System.out.println("Table successfully created");

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static int accCheck(int accid) {
		try {
			PreparedStatement sql1 = con.prepareStatement("SELECT accBalance FROM ACCOUNT WHERE accID = ?");

			sql1.setInt(1, accid);
			ResultSet rs = sql1.executeQuery();

			int[] balance = new int[3];

			while (rs.next()) {
				balance[0] = rs.getInt("accBalance");
			}
			return balance[0];
		} catch (Exception e) {
			return 0;
		}
	}

	public static ArrayList<Integer> findaccID(String name, int res) { // find my account

		try {
			PreparedStatement sql1 = con
					.prepareStatement("SELECT accID FROM ACCOUNT WHERE accResNum = ? and accCsName = ?");

			sql1.setInt(1, res);
			sql1.setString(2, name);

			ResultSet results = sql1.executeQuery();

			ArrayList<Integer> list = new ArrayList<Integer>();

			while (results.next()) {
				list.add(results.getInt("accID"));
			}
			return list;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
	}

	public static ArrayList<String> findaccKind(String name, int res) { // find my account kind

		try {
			PreparedStatement sql1 = con
					.prepareStatement("SELECT accID,accKind FROM ACCOUNT WHERE accResNum = ? and accCsName = ?");

			sql1.setInt(1, res);
			sql1.setString(2, name);

			ResultSet results = sql1.executeQuery();

			ArrayList<String> list = new ArrayList<String>();

			while (results.next()) {
				list.add(results.getString("accKind"));
			}
			return list;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
	}

	public static void openCustomer(int res, String name, String address, String phone, String job, String date) {
		try {
			PreparedStatement sql1 = con.prepareStatement("INSERT INTO CUSTOMER VALUES(?,?,?,?,?,?)");

			StringToDate st = new StringToDate();
			Date csbirth = st.transformDate(date);

			sql1.setInt(1, res);
			sql1.setString(2, name);
			sql1.setString(3, address);
			sql1.setDate(4, csbirth);
			sql1.setString(5, phone);
			sql1.setString(6, job);
			sql1.execute();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static String[] getStringCus(int res) { // CUstomer의
		try {
			PreparedStatement sql1 = con.prepareStatement(
					"SELECT csName,csAddress,csPhoneNum,csJob,csBirth  FROM CUSTOMER WHERE csResNum = " + res);

			String[] stCus = new String[5];
			ResultSet results = sql1.executeQuery();

			while (results.next()) {
				stCus[0] = results.getString("csAddress");
				stCus[1] = results.getString("csPhoneNum");
				stCus[2] = results.getString("csJob");
				stCus[3] = results.getString("csBirth");
				stCus[4] = results.getString("csName");
			}
			return stCus;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
	}

	public static void openAccount(String accKind, String card, String name, int balance, String phone, String email,
			String address, String job, int res) { // find my account
		// make account & customer
		try {

			PreparedStatement sql1 = con.prepareStatement("INSERT INTO ACCOUNT VALUES(?,?,?,?,?,?,?,?,?)");

			Date csbirth = new java.sql.Date(System.currentTimeMillis());

			if (accKind.equals("free")) {
				int a = (int) (Math.random() * (9999 - 1001 + 1) + 1001);
				sql1.setInt(1, a); // ** random number xxxx : free
			} else { // saving
				int b = (int) (Math.random() * (99999 - 10001 + 1) + 10001);
				sql1.setInt(1, b); // , xxxxx : saving
			}
			sql1.setString(2, accKind);
			sql1.setInt(3, balance);
			sql1.setString(4, card);
			sql1.setDate(5, csbirth);
			sql1.setString(6, name);
			sql1.setString(7, phone);
			sql1.setString(8, email);
			sql1.setInt(9, res);
			sql1.execute();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void openPrepaidCard(int accID, String cardKind, int resNum, int balance) {
		try {
			PreparedStatement sql1 = con.prepareStatement("INSERT INTO CARD VALUES(?,?,now(),?,?,?,?)");

			int random = (int) (Math.random() * (99999 - 10001 + 1) + 10001);
			Date date = new java.sql.Date(System.currentTimeMillis());

			sql1.setInt(1, random);
			sql1.setDate(2, date);
			sql1.setString(3, cardKind);
			sql1.setInt(4, resNum);
			sql1.setInt(5, accID);
			sql1.setInt(6, balance);
			System.out.println("Prepaid Card Issuance Success ");
			System.out.println("CARD IN : " + random + " card balance : " + balance);
			sql1.execute();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void openCheckCard(int accID, String cardKind, int resNum) {
		try {
			PreparedStatement sql = con.prepareStatement("SELECT accBalance FROM ACCOUNT WHERE accID = ?");
			sql.setInt(1, accID);

			int[] balance = new int[10];
			ResultSet results = sql.executeQuery();

			while (results.next()) {
				balance[0] = results.getInt("accBalance");
			}

			PreparedStatement sql1 = con.prepareStatement("INSERT INTO CARD VALUES(?,?,now(),?,?,?,?)");

			int random = (int) (Math.random() * (99999 - 10001 + 1) + 10001);
			Date date = new java.sql.Date(System.currentTimeMillis());

			sql1.setInt(1, random);
			sql1.setDate(2, date);
			sql1.setString(3, cardKind);
			sql1.setInt(4, resNum);
			sql1.setInt(5, accID);
			sql1.setInt(6, balance[0]);
			sql1.execute();
			System.out.println("Check Card Issuance Success ");

			System.out.println("CARD IN : " + random + " card balance : " + balance[0]);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void deposit(int accountID, int amount) { //
		try {
			PreparedStatement sql = con
					.prepareStatement("UPDATE  ACCOUNT SET accBalance = accBalance + ? WHERE accID = ?");
			sql.setInt(1, amount);
			sql.setInt(2, accountID);
			sql.execute();
			System.out.println("deposit success");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void withdraw(int accountID, int amount) { //
		try {
			PreparedStatement sql1 = con.prepareStatement("SELECT accBalance FROM ACCOUNT WHERE accID = ?");

			sql1.setInt(1, accountID);
			ResultSet rs = sql1.executeQuery();

			int[] balance = new int[3];

			while (rs.next()) {
				balance[0] = rs.getInt("accBalance");
			}

			if (amount <= balance[0]) {
				PreparedStatement sql = con
						.prepareStatement("UPDATE  ACCOUNT SET accBalance = accBalance - ? WHERE accID = ?");
				sql.setInt(1, amount);
				sql.setInt(2, accountID);
				sql.execute();
				System.out.println("Withdrawal success");
			} else {
				System.out.println("account balance < amount");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void transSender(int accountID, int amount) { //
		try {

			PreparedStatement sql = con
					.prepareStatement("UPDATE  ACCOUNT SET accBalance = accBalance - ? WHERE accID = ?");
			sql.setInt(1, amount);
			sql.setInt(2, accountID);
			sql.execute();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void transReceiver(int accountID, int amount) { //
		try {
			PreparedStatement sql = con
					.prepareStatement("UPDATE  ACCOUNT SET accBalance = accBalance + ? WHERE accID = ?");
			sql.setInt(1, amount);
			sql.setInt(2, accountID);
			sql.execute();
			System.out.println("transfer success");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void insertAccHis(String content, int amount, int accID, String tKind) {
		try {
			int b = (int) (Math.random() * (99999 - 10001 + 1) + 10001);

			PreparedStatement sql1 = con.prepareStatement("SELECT accKind, accBalance from account where accID = ?");
			PreparedStatement sql2 = con.prepareStatement("INSERT INTO accountHistory VALUES(?,now(),?,?,?,?,?,?)");

			sql1.setInt(1, accID);
			ResultSet results = sql1.executeQuery();

			String[] a = new String[3];
			int[] c = new int[3];

			while (results.next()) {
				a[0] = results.getString("accKind");
				c[0] = results.getInt("accBalance");
			}
			sql2.setInt(1, accID);
			sql2.setInt(2, b);
			sql2.setString(3, a[0]);
			sql2.setString(4, content);
			sql2.setInt(5, amount);
			sql2.setInt(6, c[0]);
			sql2.setString(7, tKind);

			sql2.execute();

			System.out.println("Update Account History Success");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void insertCheckAccHis(String content, int amount, int accID, String tKind) {
		try {
			int b = (int) (Math.random() * (99999 - 10001 + 1) + 10001);

			PreparedStatement sql1 = con.prepareStatement("SELECT accKind, accBalance from account where accID = ?");
			PreparedStatement sql2 = con.prepareStatement("INSERT INTO accountHistory VALUES(?,now(),?,?,?,?,?,?)");

			sql1.setInt(1, accID);
			ResultSet results = sql1.executeQuery();

			String[] a = new String[3];
			int[] c = new int[3];

			while (results.next()) {
				a[0] = results.getString("accKind");
				c[0] = results.getInt("accBalance");
			}
			sql2.setInt(1, accID);
			sql2.setInt(2, b);
			sql2.setString(3, a[0]);
			sql2.setString(4, content);
			sql2.setInt(5, amount);
			sql2.setInt(6, c[0]);
			sql2.setString(7, tKind);

			sql2.execute();

			System.out.println("Update Account History Success");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void accHistory(int accID) { // account history - accID

		try {
			System.out.println(accID + " Account History ");
			PreparedStatement sql1 = con.prepareStatement("SELECT * FROM ACCOUNTHistory WHERE transAccID = ? ");

			sql1.setInt(1, accID);

			ResultSet results = sql1.executeQuery();

			Timestamp[] date = new Timestamp[100];
			int[] tnum = new int[100];
			String[] aKind = new String[100];
			String[] cont = new String[100];
			int[] amount = new int[100];
			String[] tKind = new String[100];
			int[] balance = new int[100];

			int i = 0;
			while (results.next()) {
				date[i] = results.getTimestamp("transDate");
				tnum[i] = results.getInt("transNum");
				aKind[i] = results.getString("transAccKind");
				cont[i] = results.getString("transContent");
				amount[i] = results.getInt("transAmount");
				balance[i] = results.getInt("transBalance");
				tKind[i] = results.getString("transKind");
				System.out.print("Date : " + date[i] + " | transaction Number : " + tnum[i] + " | accountKind : "
						+ aKind[i] + " | Content : " + cont[i] + " | Transaction Amount : " + amount[i]
						+ " | Account Balance :" + balance[i] + " | Transaction Kind : " + tKind[i]);
				System.out.println();
				i++;
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void findcard(int res) { // find my account kind

		try {
			PreparedStatement sql1 = con
					.prepareStatement("SELECT cardID,cardKind,cardBalance FROM CARD WHERE CardResNum = ?");

			sql1.setInt(1, res);

			ResultSet results = sql1.executeQuery();
			int[] id = new int[5];
			String[] kind = new String[5];
			int[] bal = new int[5];
			int i = 1;
			while (results.next()) {

				id[i] = results.getInt("cardID");
				kind[i] = results.getString("cardKind");
				bal[i] = results.getInt("cardBalance");
				System.out.println(
						i + ". CARD ID : " + id[i] + " | Card Kind : " + kind[i] + " | Card Balance : " + bal[i]);
				i++;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void checkPay(int cardid, int price) { //
		try {
			PreparedStatement sql1 = con
					.prepareStatement("SELECT cardBirth,cardResNum,cardBalance,cardAccID FROM CARD WHERE cardID = ?");

			sql1.setInt(1, cardid);
			ResultSet results = sql1.executeQuery();

			int[] cardbalance = new int[3];
			Date[] birth = new Date[2];

			while (results.next()) {
				birth[0] = results.getDate("cardBirth");
				cardbalance[1] = results.getInt("cardResNum");
				cardbalance[2] = results.getInt("cardAccID");
			}

			PreparedStatement sql2 = con.prepareStatement("SELECT AccBalance FROM ACCOUNT WHERE accID = ?");

			sql2.setInt(1, cardbalance[2]);
			ResultSet results2 = sql2.executeQuery();

			int[] accbalance = new int[3];

			while (results2.next()) {
				accbalance[0] = results2.getInt("ACCBALANCE");
			}

			if (accbalance[0] >= price) {
				PreparedStatement sql3 = con
						.prepareStatement("UPDATE ACCOUNT SET accBalance = accBalance - ? WHERE accID = ?");
				sql3.setInt(1, price);
				sql3.setInt(2, cardbalance[2]);
				sql3.execute();

				PreparedStatement sql = con.prepareStatement("INSERT INTO CARD VALUES(?,?,now(),?,?,?,?)");
				sql.setInt(1, cardid);
				sql.setDate(2, birth[0]);
				sql.setString(3, "check");
				sql.setInt(4, cardbalance[1]);
				sql.setInt(5, cardbalance[2]);
				sql.setInt(6, accbalance[0] - price);
				sql.execute();

				insertCheckAccHis("check card pay", price, cardbalance[2], "card pay");

				System.out.println("check card pay success");
			} else {
				System.out.println("fail to pay");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void prepaidPay(int cardid, int price) { //
		try {

			PreparedStatement sql1 = con
					.prepareStatement("SELECT cardBirth,cardResNum,cardBalance,cardAccID FROM CARD WHERE cardID = ?");

			sql1.setInt(1, cardid);
			ResultSet results = sql1.executeQuery();

			int[] cardbalance = new int[3];
			Date[] birth = new Date[2];

			while (results.next()) {
				cardbalance[0] = results.getInt("cardBalance");
				birth[0] = results.getDate("cardBirth");
				cardbalance[1] = results.getInt("cardResNum");
				cardbalance[2] = results.getInt("cardAccID");
			}

			if (cardbalance[0] >= price) {
				PreparedStatement sql = con.prepareStatement("INSERT INTO CARD VALUES(?,?,now(),?,?,?,?)");
				sql.setInt(1, cardid);
				sql.setDate(2, birth[0]);
				sql.setString(3, "prepaid");
				sql.setInt(4, cardbalance[1]);
				sql.setInt(5, cardbalance[2]);
				sql.setInt(6, cardbalance[0] - price);
				sql.execute();
				System.out.println("prepaid card pay success");

			} else {
				System.out.println("fail to pay");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
