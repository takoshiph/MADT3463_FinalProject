import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Scanner;

public class Transactions {
    private String transactionType;
    private String accountName;
    private double amount;
    private long accountNum;
    private long transferAccountNum;
    private Date date;
    //Scanner sc = new Scanner(System.in); remove after modifying

    DecimalFormat decimalFormat = new DecimalFormat("$###,###,###,###.##");
    Scanner scan = new Scanner(System.in);

    public Transactions() {
    }
    public void accountInfo(String accountName, long accountNum, String transactionType, double amount) {
        this.accountName = accountName;
        this.accountNum = accountNum;
        this.transactionType = transactionType;
        this.amount = amount;
        date = new Date();
        operation();
    }
    public void storeTransferAccount(long transferAccount) {
        this.transferAccountNum = transferAccount;
    }
    private int findMaxId() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("AccountDetails.txt"));
            int rowcount = 0;
            while ((reader.readLine()) != null) {
                rowcount = rowcount + 1;
            }
            reader.close();
            // Logic for finding maximum Id
            return rowcount / 3;
        } catch (Exception e) {
            System.err.format("Exception occurred trying to read '%s'.", "AccountDetails.txt");
            e.printStackTrace();
        }
        return 0;
    }
    private void operation() {
        Path path = Paths.get("AccountDetails.txt");
        String notFound = "File not Found";

        switch (transactionType){
            case "opening":
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter("AccountDetails.txt", true));
                int userId = (findMaxId() + 1);
                writer.write(Integer.toString(userId) + "\n");
                writer.write(amount + "\n");
                writer.write(date + "\n");
                writer.write(accountName + "\n");
                writer.close();

                //Account opened
                System.out.println("\n");
                System.out.println("*-------------------------------------------------------*");
                System.out.println("  Congratulations " + accountName + "!");
                System.out.println("  Account has been successfully opened ");
                System.out.println("  with below details.");
                System.out.println("               User Name: " + accountName );
                System.out.println("               Account Number: " + userId );
                System.out.println("               Opening Balance: " + decimalFormat.format(amount));
                System.out.println("*-------------------------------------------------------*");
            }
            catch (IOException e) {
                //Catch input output exception
                System.err.println("Caught IOException: " + e.getMessage());
            } break;
            case "withdraw":
            case "deposit":
                //Check whether files exists storing account information
                if (Files.exists(path))
                    findAccountUpdate();
                else System.out.println(notFound);
                break;
            case "billpayment":
                if (Files.exists(path))
                    billPayment();
                else System.out.println(notFound);
                break;
            case "transfer":
                if (Files.exists(path))
                    transferToAccount();
                else System.out.println(notFound);
                break;
            case "accountInfo":
                if (Files.exists(path))
                    accountInfo();
                else System.out.println(notFound);
                break;
            default:
            System.out.println("You provided an invalid option. Please choose a correct one.");
        }
    }
    //To check the account existence ****NOT WORKING AS EXPECTED****
    public double checkIfAccountExists(long accountNum) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("AccountDetails.txt"));
            String line;
            String accountTmpNum = Long.toString(accountNum);
            int count = 1;
            while ((line = reader.readLine()) != null) {
                if (count > 1) {
                    accountTmpNum = line;
                }
                if ((line.equals(accountTmpNum)) && (count < 5)) {
                    double temp_amount = 0;
                    if (count == 2) {
                        temp_amount = Double.parseDouble(line);
                        return temp_amount;
                    }
                    count = count + 1;
                }
            }
        } catch (Exception e) {
        }
        return 0.0;
    }

    private void billPayment() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("AccountDetails.txt"));
            BufferedWriter writer = new BufferedWriter(new FileWriter("TempFile.txt", true));
            String line;
            String tempAccountNum = Long.toString(accountNum);
            int rowcount = 1;
            while ((line = reader.readLine()) != null) {
                if (rowcount > 1) {
                    tempAccountNum = line;
                }
                if ((line.equals(tempAccountNum)) && (rowcount < 4)) {
                    double temp_amount = 0;
                    if (rowcount == 1) {
                        writer.write(accountNum + "\n");
                    } else if (rowcount == 2) {
                        //subtract the bill amount from the balance
                        temp_amount = Double.parseDouble(line) - amount;
                        if (temp_amount < 0) {
                            System.out.println("You do not have sufficient balance \n");
                            writer.write(Double.toString(amount) + "\n");
                        } else {
                            System.out.println("*-------------------------------------------------------*");
                            System.out.println("  Successfully paid bill ");
                            System.out.println("  Your closing balance is: " + decimalFormat.format(temp_amount));
                            System.out.println("*-------------------------------------------------------*");

                            writer.write(Double.toString(temp_amount) + "\n");
                        }
                    } else if (rowcount == 3) {
                        writer.write(date + "\n");
                    }
                    rowcount += 1;
                } else {
                    writer.write(line + "\n");
                }
            }
            writer.close();
            reader.close();
            File permaFile = new File("AccountDetails.txt");
            permaFile.delete();
            File tempFile = new File("TempFile.txt");
            boolean b = tempFile.renameTo(permaFile);
            if (b) {
            } else {
                System.out.println("Updated has Error");
            }
        } catch (Exception e) {
            System.err.format("Exception occurred trying to read '%s'.", "AccountDetails.txt");
            e.printStackTrace();
        }
    }

    public void transferToAccount() {
        System.out.println("\nEnter the Recipient's Account Number:");
        long transferAccountNo = scan.nextInt();
        storeTransferAccount(transferAccountNo);

        try {
            double transferAccountBalance = this.checkIfAccountExists(transferAccountNum);
            double temp_amount = this.checkIfAccountExists(accountNum);
            if (this.checkIfAccountExists(transferAccountNum) == 0.0) {
                System.out.println("\nError: Transfer Account Number does not exist.\n");
            } else if (this.checkIfAccountExists(accountNum) == 0.0) {
                System.out.println("\nError: Account number does not exist. \n");
            } else {
                if (temp_amount > amount) {
                    //update both account balances
                    double transAccountBal = transferAccountBalance + amount;
                    double userAccountBal = temp_amount - amount;
                    this.updateAccountBalance(accountNum, amount, "withdraw");
                    this.updateAccountBalance(transferAccountNum, amount, "deposit");
                    System.out.println("*-------------------------------------------------------*");
                    System.out.println("|                  Successful operation                 |");
                    System.out.println("*-------------------------------------------------------*");
                    System.out.println("Closing balance for Account number: " + accountNum + " is " + userAccountBal);
                    System.out.println("Closing balance for Account number: " + transferAccountNum + " is " + transAccountBal + "\n");
                } else if (accountNum == transferAccountNum) {
                    System.out.println("\nError: Cannot transfer with the same account.\n");
                } else {
                    System.out.println("\nError: Insufficient balance.\n");
                }
            }
        } catch (Exception e) {
            System.err.format("Exception occurred trying to read '%s'.", "AccountDetails.txt");
            e.printStackTrace();
        }
    }


    private void accountInfo() {
        String commentBlockAccountInfo = """
            
            *-------------------------------------------------------*
            |                Your Account Information               |
            *-------------------------------------------------------*""";
        String line;
        String tempAccountNum = Long.toString(accountNum);
        int rowcount = 1;

        try {
            BufferedReader reader = new BufferedReader(new FileReader("AccountDetails.txt"));
            System.out.println(commentBlockAccountInfo);

            while ((line = reader.readLine()) != null) {
                if (rowcount > 1) {
                    tempAccountNum = line;
                }
                if ((line.equals(tempAccountNum)) && (rowcount < 5)) {
                    switch (rowcount) {
                        case 1:
                            System.out.println("Account Number: " + line);
                            break;
                        case 2:
                            System.out.println("Balance: " + line);
                            break;
                        case 3:
                            System.out.println("Date of opening: " + line);
                            break;
                        case 4:
                            System.out.println("Name of the account holder: " + line);
                            break;
                    }
                    rowcount += 1;
                }
            }
            reader.close();
        } catch (Exception e) {
            System.err.format("Exception occurred trying to read '%s'.", "AccountDetails.txt");
            e.printStackTrace();
        }
    }
    private void updateAccountBalance(long accountNum, double amount, String type) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("AccountDetails.txt"));
            BufferedWriter writer = new BufferedWriter(new FileWriter("TempFile.txt", true));
            String line;
            String tempAccountNum = Long.toString(accountNum);
            int rowcount = 1;

            while ((line = reader.readLine()) != null) {
                if (rowcount > 1) {
                    tempAccountNum = line;
                }
                if ((line.equals(tempAccountNum)) && (rowcount < 4)) {
                    double temp_amount = 0;
                    if (rowcount == 1) {
                        writer.write(accountNum + "\n");
                    } else if (rowcount == 2) {
                        if (type.equalsIgnoreCase("withdraw")) {
                            //subtracts the withdrawn amount
                            temp_amount = Double.parseDouble(line) - amount;
                        } else if (type.equalsIgnoreCase("deposit")) {
                            //add the deposit amount
                            temp_amount = amount + Double.parseDouble(line);
                        }
                        if (temp_amount < 0) {
                            System.out.println("Account has insufficient balance.\n");
                            writer.write(Double.toString(amount) + "\n");
                        } else {
                            writer.write(Double.toString(temp_amount) + "\n");
                        }
                    } else if (rowcount == 3) {
                        writer.write(date + "\n");
                    }
                    rowcount += 1;
                } else {
                    writer.write(line + "\n");
                }
            }
            writer.close();
            reader.close();
            File permaFile = new File("AccountDetails.txt");
            permaFile.delete();
            File tempFile = new File("TempFile.txt");
            boolean b = tempFile.renameTo(permaFile);
            if (b) {
            } else {
                System.out.println("Update has Error");
            }
        } catch (Exception e) {
            System.err.format("Exception occurred trying to read '%s'.", "AccountDetails.txt");
            e.printStackTrace();
        }
    }

    private void findAccountUpdate() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("AccountDetails.txt"));
            BufferedWriter writer = new BufferedWriter(new FileWriter("TempFile.txt", true));

            String line;
            String tempAccountNum = Long.toString(accountNum);

            int rowcount = 1;
            while ((line = reader.readLine()) != null) {
                if (rowcount > 1) {
                    tempAccountNum = line;
                }
                if ((line.equals(tempAccountNum)) && (rowcount < 4)) {
                    double temp_amount = 0;
                    if (rowcount == 1) {
                        writer.write(accountNum + "\n");
                    } else if (rowcount == 2) {
                        if (transactionType.equalsIgnoreCase("withdraw")) {
                            //subtracts the withdrawn amount
                            temp_amount = Double.parseDouble(line) - amount;
                        } else if (transactionType.equalsIgnoreCase("deposit")) {
                            //add the deposit amount
                            temp_amount = amount + Double.parseDouble(line);
                        }
                        if (temp_amount < 0) {
                            System.out.println("Account has insufficient balance.\n");
                            writer.write(Double.toString(amount) + "\n");
                        } else {
                            System.out.println("*-------------------------------------------------------*");
                            System.out.println("  Successful Transaction!");
                            System.out.println("  Your closing balance is: " + decimalFormat.format(temp_amount));
                            System.out.println("*-------------------------------------------------------*");
                            writer.write(Double.toString(temp_amount) + "\n");
                        }
                    } else if (rowcount == 3) {
                        writer.write(date + "\n");
                    }
                    rowcount = rowcount + 1;
                } else {
                    writer.write(line + "\n");
                }
            }
            writer.close();
            reader.close();
            File permaFile = new File("AccountDetails.txt");
            permaFile.delete();
            File tempFile = new File("TempFile.txt");
            boolean b = tempFile.renameTo(permaFile);
            if (b) {
            } else {
                System.out.println("Update has Error.");
            }
        } catch (Exception e) {
            System.err.format("Exception occurred trying to read '%s'.", "AccountDetails.txt");
            e.printStackTrace();
        }
    }
}