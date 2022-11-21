import java.util.ArrayList;
import java.util.Scanner;

public class BankProgram {
    int count=0;
    ArrayList<String> FileRecords;
    public void InitiateBank(){
        Scanner scan = new Scanner(System.in);
        char runAgain = 'Y';
        while (runAgain =='Y'){
            System.out.println("\nBanking Program for MADT2022 Group C");
            if(count<1){
                count+= 1;
            }else{
                System.out.println(" Once again. \n");
            }
            if(count<2){
                count+= 1;
            }else{
                System.out.print("1 : Start Again\n");
                System.out.print("2 : Exit\n\n");
                System.out.println("You Select : ");
            }
            mainMenu();

            System.out.println("\nDo u want to run your Program Again \nY = yes\nN = No\n");
            System.out.println("You Select : ");
            runAgain =(scan.next()).charAt(0);
            if(Character.isLowerCase(runAgain )){
                runAgain =Character.toUpperCase(runAgain);
            }
        }
    }
    private void mainMenu() {
        String areYouNew = """
            
            *-------------------------------------------------------*
            | 1 : Create an Account.                                |
            | 2 : Existing User.                                    |
            | 3 : Exit                                              |
            *-------------------------------------------------------*
            Provide the # of your option:""";
        String mainOptions;
        Transactions transactions = new Transactions();
        Scanner scan = new Scanner(System.in);
        int accountNo = 0;

        do {
            System.out.println(areYouNew);
            mainOptions = scan.next();
            switch (mainOptions) {
                case "1":
                    double initialBalance;
                    System.out.println("Enter Account Name:");
                    scan.nextLine();
                    String name= scan.nextLine();
                    System.out.println("Enter your Account's Opening Balance:");
                    initialBalance = scan.nextDouble();
                    transactions.accountInfo(name, accountNo, "opening", initialBalance);
                    accountNo = accountNo+1;
                case "2":
                    this.existingUserData();
                case "3":
                    System.out.println("*-------------------------------------------------------*");
                    System.out.println("*------------------------Thank you!---------------------*");
                    System.out.println("*-------------------------------------------------------*");
                    return;
                default:
                    error();
            }
        } while (mainOptions != "3");
        scan.close();
    }
    private void existingUserData(){
        String operationMenu = """
           
           *-------------------------------------------------------*
           | Choose the # for the chosen OPERATION:                |
           *-------------------------------------------------------*
           | 1 : Transaction (Deposit/Withdraw/Transfer)           |
           | 2 : View Existing Account Information                 |
           | 3 : Pay Utility Bills                                 |
           | 4 : Exit                                              |
           *-------------------------------------------------------*
           Provide the # of your Operation:""";
        String transactionMenu = """
           
           *-------------------------------------------------------*
           | Choose the # for the chosen TRANSACTION?              |
           *-------------------------------------------------------*
           | 1 : Deposit                                           |
           | 2 : Withdraw                                          |
           | 3 : Transfer                                          |
           *-------------------------------------------------------*
           Provide the # of your Transaction:""";

        String operationOption, transactionOption, operation;
        Transactions transaction = new Transactions();
        Scanner scan = new Scanner(System.in);
        double amount;
        int accountNo;
        do {
            System.out.println(operationMenu);
            operationOption = scan.next();

            switch (operationOption) {
                case "1" -> { //Ask for Transaction?
                    System.out.println(transactionMenu);
                    transactionOption = scan.next();
                    if (transactionOption.equalsIgnoreCase("1"))
                        operation = "deposit";
                    else if (transactionOption.equalsIgnoreCase("2"))
                        operation = "withdraw";
                    else if (transactionOption.equalsIgnoreCase("3")) {
                        operation = "transfer";
                    } else {
                        operation = "Invalid option";
                    }
                    System.out.println("\nPlease enter your Account Number:");
                    accountNo = scan.nextInt();
                    System.out.println("\nPlease enter Amount:");
                    amount = scan.nextDouble();
                    transaction.accountInfo("", accountNo, operation, amount);
                }
                case "2" -> { //View Account Information
                    System.out.println("Account Number:");
                    accountNo = scan.nextInt();
                    operation = "accountInfo";
                    transaction.accountInfo("", accountNo, operation, 0);
                }
                case "3" -> { //Pay Utility Bills
                    operation = "billpayment";
                    System.out.println("\nEnter your Account Number:");
                    accountNo = scan.nextInt();
                    System.out.println("\nEnter bill amount:");
                    amount = scan.nextDouble();
                    transaction.accountInfo("", accountNo, operation, amount);
                }
                case "4" -> {
                    System.out.println("*-------------------------------------------------------*");
                    System.out.println("*------------------------Thank you!---------------------*");
                    System.out.println("*-------------------------------------------------------*");

                    return;
                }
                default -> error();
            }
        }while (operationOption != "4");
        scan.close();
    }

    public static void error() {
        System.out.println("*-------------------------------------------------------*");
        System.out.println("| You might have chosen wrong option or                 |");
        System.out.println("| a technical problem was encountered.                  |");
        System.out.println("| Please restart the Banking Program.                   |");
        System.out.println("*-------------------------------------------------------*");
    }
}
