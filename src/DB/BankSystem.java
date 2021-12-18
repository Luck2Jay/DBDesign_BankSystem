package DB;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class BankSystem {
	public static void main(String[] args) {
		boolean end = true;
		Scanner sc = new Scanner(System.in);
		int menu = 0;

		DBQuery.createTable();

		while (end != false) {
			showMenu();
			menu = sc.nextInt();

			switch (menu) {
			case 1: // find my account
				System.out.println("Enter your social security number");
				int resNum = sc.nextInt();
				System.out.println("Enter your name");
				String name = sc.next();
				System.out.println();

				ArrayList<Integer> accID = DBQuery.findaccID(name, resNum); // return one account or many account
				ArrayList<String> accKind = DBQuery.findaccKind(name, resNum); // return one account or many account

				Iterator iter = accID.iterator();
				Iterator iter2 = accKind.iterator();

				int i = 1;
				while (iter.hasNext()) {
					System.out.printf(i + ". accID : " + iter.next() + " accKind : " + iter2.next());
					System.out.println();
					i++;
				}
				break;

			case 2: // Open account

				System.out.println("Have you ever used this bank? answer Yes or No");
				String ans = sc.next();

				if (ans.equals("no")) {

					System.out.println("Enter your social security number");
					int res2 = sc.nextInt();
					System.out.println("Enter your name");
					String name2 = sc.next();
					System.out.print("Enter a kind of Account (free or saving) :  ");
					String accKind2 = sc.next();
					System.out.print("Enter card (yes or no) :  ");
					String card = sc.next();

					System.out.print("Enter BirthDay (yyyy mmdd) :  "); // cs Birthday xxxx xxxx0
					String date = sc.next();

					System.out.print("Enter balance :  ");
					int balance = sc.nextInt();
					System.out.print("Enter phone Number :  ");
					String phoneNum = sc.next();
					System.out.print("Enter E-Mail :  ");
					String email = sc.next();
					System.out.print("Enter your home Address :  ");
					String address = sc.next();
					System.out.print("Enter your Job :  ");
					String job = sc.next();

					DBQuery.openCustomer(res2, name2, address, phoneNum, job, date);
					DBQuery.openAccount(accKind2, card, name2, balance, phoneNum, email, address, job, res2);

					ArrayList<Integer> accID3 = DBQuery.findaccID(name2, res2); // return one account or many account
					ArrayList<String> accKind3 = DBQuery.findaccKind(name2, res2); // return one account or many account

					Iterator iter3 = accID3.iterator();
					Iterator iter4 = accKind3.iterator();

					int j = 1;
					while (iter3.hasNext()) {
						System.out.printf(j + ". accID : " + iter3.next() + " accKind : " + iter4.next());
						System.out.println();
						j++;
					}
				} else {
					System.out.println("Enter your social security number");
					int res2 = sc.nextInt();

					String[] cus = DBQuery.getStringCus(res2); // address, phone, job,Birth

					System.out.print("Enter card (yes or no) :  ");
					String card = sc.next();
					System.out.print("Enter a kind of Account (free or saving) :  ");
					String accKind2 = sc.next();
					System.out.print("Enter balance :  "); // cs Birthday xxxx xxxx0
					int balance = sc.nextInt();
					System.out.print("Enter E-Mail :  ");
					String email = sc.next();
					DBQuery.openAccount(accKind2, card, cus[4], balance, cus[1], email, cus[0], cus[2], res2);

					ArrayList<Integer> accID1 = DBQuery.findaccID(cus[4], res2); // return one account or many account
					ArrayList<String> accKind1 = DBQuery.findaccKind(cus[4], res2); // return one account or many
																					// account

					Iterator iter5 = accID1.iterator();
					Iterator iter6 = accKind1.iterator();

					int f = 1;
					while (iter5.hasNext()) {
						System.out.printf(f + ". accID : " + iter5.next() + " accKind : " + iter6.next());
						System.out.println();
						f++;
					}
				}

				break;

			case 3: // deposit
				System.out.println();
				System.out.println("Do you have Account : yes or no");
				String a = sc.next();
				if (a.equals("yes")) {
					System.out.print("Enter Account ID :  ");
					int accountID = sc.nextInt();
					System.out.print("Enter the amount want to save  :  ");
					int amount = sc.nextInt();
					System.out.print("Enter the purpose of deposit :  ");
					String content = sc.next();
					DBQuery.deposit(accountID, amount);

					String deposit = "deposit";
					DBQuery.insertAccHis(content, amount, accountID, deposit);
				} else {
					System.out.println("Open Acccount please");
				}
				break;
			case 4: // withdrawal
				System.out.println();
				System.out.println("Do you have Account : yes or no");
				String b = sc.next();
				if (b.equals("yes")) {
					System.out.print("Enter Account ID :  ");
					int accountID1 = sc.nextInt();
					System.out.print("Enter the amount want to withdrawal  :  ");
					int amount1 = sc.nextInt();
					if (DBQuery.accCheck(accountID1) >= amount1) {
						System.out.print("Enter the purpose of withdrawal :  ");
						String content = sc.next();
						DBQuery.withdraw(accountID1, amount1);

						String with = "Withdrawal";
						DBQuery.insertAccHis(content, amount1, accountID1, with);
						break;
					} else {
						System.out.println("withdrawal fail : account balance < withdrawal amount");
						break;
					}

				} else {
					System.out.println("Open Acccount please");
					break;
				}
			case 5: // Transfer
				System.out.println();
				System.out.println();
				System.out.println("Do you have Account : yes or no");
				String d = sc.next();
				if (d.equals("yes")) {

					String tKind = "Transfer";
					System.out.print("Enter Sender Account ID :  ");
					int sendID = sc.nextInt();

					int accba = DBQuery.accCheck(sendID);

					System.out.print("Enter the amount want to transfer  :  ");
					int transAmount = sc.nextInt();

					if (accba >= transAmount) {
						System.out.print("Enter the purpose of transfer :  ");
						String cont = sc.next();

						System.out.print("Enter Receiver Account ID :  ");
						int receiveID = sc.nextInt();
						DBQuery.transSender(sendID, transAmount);
						DBQuery.insertAccHis(cont, transAmount, sendID, tKind);
						DBQuery.transReceiver(receiveID, transAmount);
						DBQuery.insertAccHis(cont, transAmount, receiveID, tKind);
						break;
					} else {
						System.out.println("transfer fail : account balance < transfer amount");
						break;
					}

				} else {
					System.out.println("Open Acccount please");
					break;
				}
			case 6: // Acc History
				System.out.println();
				System.out.println();
				System.out.println("Do you have Account : yes or no");
				String c = sc.next();
				if (c.equals("yes")) {
					System.out.print("Enter Account ID :  ");
					int accountID = sc.nextInt();
					DBQuery.accHistory(accountID);
					break;
				} else {
					System.out.println("Open Acccount please");
					break;
				}
			case 7: // Find My Card
				System.out.println();
				System.out.println("Do you have Card? answer Yes or No");
				String yes = sc.next();
				if (yes.equals("yes")) {
					System.out.println("Enter your Social Security Number");
					int sSN = sc.nextInt();

					DBQuery.findcard(sSN);
					break;
				} else {
					break;
				}

			case 8:// Card Issuance
				System.out.println();
				System.out.println("Do you have Account? answer Yes or No");
				String yOrn = sc.next();
				System.out.println("Do you agree to open a card in your account? answer Yes or No");
				String yOrn2 = sc.next();

				if (yOrn.equals("yes") && yOrn.equals("yes")) {

					System.out.println("Enter your Account ID");
					int accid = sc.nextInt();
					System.out.println("Enter your Social Security Number");
					int ssN = sc.nextInt();

					System.out.print("Enter a kind of card  (prepaid or check) :  ");
					String cardKind = sc.next();
					int bal;
					if (cardKind.equals("prepaid")) {
						System.out.print("Enter balance :  ");
						bal = sc.nextInt();
						DBQuery.openPrepaidCard(accid, cardKind, ssN, bal);
					} else {
						DBQuery.openCheckCard(accid, "check", ssN);
					}

					break;
				} else {
					System.out.println("Open Acccount please");
					break;
				}

			case 9: // card pay
				System.out.println();
				System.out.println();
				System.out.println("Do you have Card : yes or no");
				String e = sc.next();
				System.out.println("kind of card : prepaid or check");
				String f = sc.next();
				if (e.equals("yes")) {
					if (f.equals("prepaid")) {
						System.out.print("Enter Card ID :  ");
						int cardid = sc.nextInt();
						System.out.print("Enter the price  :  ");
						int amou = sc.nextInt();

						DBQuery.prepaidPay(cardid, amou);

						break;
					} else if (f.equals("check")) {
						System.out.print("Enter Card ID :  ");
						int cardid = sc.nextInt();
						System.out.print("Enter the price  :  ");
						int amou = sc.nextInt();

						DBQuery.checkPay(cardid, amou);
						break;
					}

				} else {
					System.out.println("Open Acccount please");
					break;
				}

			}

		}

	}

	public static void showMenu() {
		System.out.println();
		System.out.println("Select menu");
		System.out.println("=======================");
		System.out.println("Account Service");
		System.out.println("1.Find My Account");
		System.out.println("2.Open Account");
		System.out.println("3.Deposit"); // input
		System.out.println("4.Withdrawal"); // output
		System.out.println("5.Transfer"); // transfer to other account
		System.out.println("6.Account History");
		System.out.println("=======================");
		System.out.println("Card Service");
		System.out.println("7.Find My Card Number & Card Balance");
		System.out.println("8.Card Issuance");
		System.out.println("9.Card Pay");
		System.out.println("=======================");
		System.out.println();

	}

}
