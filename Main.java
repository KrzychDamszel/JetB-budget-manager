package budget;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {

        FileWriter purchasesFile;
        FileWriter balanceFile;
        Scanner readPurchases;
        Scanner readBalance;
        try {
            purchasesFile = FileProcessing.createFileToWriting();
            balanceFile = FileProcessing.createFileOfBalance(true);
            readPurchases = FileProcessing.readPurchasesFromFile();
            readBalance = FileProcessing.readBalanceFromFile();
        } catch (IOException e) {
            System.out.println("I/O Error!");
            return;
        }
        final Scanner scanner = new Scanner(System.in);
        BigDecimal[] income = {BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)};
        List<Purchases> purchasesList = new ArrayList<>();

        while (true) {
            ShowMenu.mainMenu();
            try {
                int chooseMenu = Integer.parseInt(scanner.nextLine().trim());
                switch (chooseMenu) {
                    case 1:
                        MenuService.addIncome(income, scanner);
                        break;
                    case 2:
                        MenuService.addPurchase(purchasesList, scanner, income);
                        break;
                    case 3:
                        MenuService.showListOfPurchases(purchasesList, scanner);
                        break;
                    case 4:
                        MenuService.showBalance(income);
                        break;
                    case 5:
                        try {
                            MenuService.save(purchasesFile, balanceFile, purchasesList, income);
                        } catch (IOException e) {
                            System.out.println("I/O Error!");
                            scanner.close();
                            return;
                        }
                        break;
                    case 6:
                        MenuService.load(readPurchases, readBalance, purchasesList, income);
                        break;
                    case 7:
                        Analyzer.mainMenu(purchasesList, scanner);
                        break;
                    case 0:
                        scanner.close();
                        System.out.println("\nBye!");
                        return;
                    default:
                        System.out.println("Wrong choose!");
                }
            } catch (NumberFormatException e) {
                System.out.println("It's not a number!");
            }
        }
    }
}

class FileProcessing {
    static String path = ".\\";
    static String purchaseFile = "purchases.txt";
    static String balanceFile = "balance.txt";


    static FileWriter createFileToWriting() throws java.io.IOException {
        File filePath = new File(path, purchaseFile);
        return new FileWriter(filePath, true);
    }

    static FileWriter createFileOfBalance(boolean flag) throws java.io.IOException {
        File filePath = new File(path, balanceFile);
        return new FileWriter(filePath, flag);
    }

    static Scanner readPurchasesFromFile() throws java.io.IOException {
        File filePath = new File(path, purchaseFile);
        return new Scanner(filePath);
    }

    static Scanner readBalanceFromFile() throws java.io.IOException {
        File filePath = new File(path, balanceFile);
        return new Scanner(filePath);
    }

    static void startingBalance(BigDecimal[] income, Scanner readBalance) {
        if (readBalance.hasNextLine()) {
            income[0] = BigDecimal.valueOf(Double.parseDouble(readBalance.nextLine()));
        }
    }
}

class ShowMenu {

    static void mainMenu() {
        System.out.println(
                "\nChoose your action:\n" +
                        "1) Add income\n" +
                        "2) Add purchase\n" +
                        "3) Show list of purchases\n" +
                        "4) Balance\n" +
                        "5) Save\n" +
                        "6) Load\n" +
                        "7) Analyze (Sort)\n" +
                        "0) Exit"
        );
    }

    static Map<Integer, String> chooseTypeOfPurchaseMenu() {
        System.out.println(
                "\nChoose the type of purchase\n" +
                        "1) Food\n" +
                        "2) Clothes\n" +
                        "3) Entertainment\n" +
                        "4) Other\n" +
                        "5) Back"
        );

        Map<Integer, String> typeOfPurchase = new HashMap<>();
        typeOfPurchase.put(1, "Food");
        typeOfPurchase.put(2, "Clothes");
        typeOfPurchase.put(3, "Entertainment");
        typeOfPurchase.put(4, "Other");

        return typeOfPurchase;
    }

    static Map<Integer, String> chooseTypeOfPurchaseShow() {
        System.out.println(
                "\nChoose the type of purchases\n" +
                        "1) Food\n" +
                        "2) Clothes\n" +
                        "3) Entertainment\n" +
                        "4) Other\n" +
                        "5) All\n" +
                        "6) Back"
        );

        Map<Integer, String> typeOfPurchaseShow = new HashMap<>();
        typeOfPurchaseShow.put(1, "Food");
        typeOfPurchaseShow.put(2, "Clothes");
        typeOfPurchaseShow.put(3, "Entertainment");
        typeOfPurchaseShow.put(4, "Other");
        typeOfPurchaseShow.put(5, "All");
        return typeOfPurchaseShow;
    }
}

class MenuService {

    static void addIncome(BigDecimal[] income, Scanner scanner) {
        boolean end = true;
        while (end) {
            System.out.println("\nEnter income:");
            try {
                income[0] = BigDecimal.valueOf(Integer
                        .parseInt(scanner.nextLine().trim()))
                        .setScale(2, RoundingMode.HALF_UP);
                System.out.println("Income was added!");
                end = false;
            } catch (NumberFormatException e) {
                System.out.println("It's not a number!");
            }
        }
    }

    static void addPurchase(List<Purchases> purchasesList, Scanner scanner, BigDecimal[] income) {
        String type = "";
        String item = "";
        BigDecimal price = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        while (true) {
            try {
                boolean end1 = true;
                while (end1) {
                    Map<Integer, String> typeOfPurchase = ShowMenu.chooseTypeOfPurchaseMenu();
                    int chooseType = Integer.parseInt(scanner.nextLine().trim());
                    switch (chooseType) {
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                            type = typeOfPurchase.get(chooseType);
                            end1 = false;
                            break;
                        case 5:
                            return;
                        default:
                            System.out.println("Wrong choose");
                    }
                }
                System.out.println("\nEnter purchase name:");
                item = scanner.nextLine().trim();
                System.out.println("Enter its price:");
                boolean end2 = true;
                while (end2) {
                    try {
                        double tempPrice = Double.parseDouble(scanner.nextLine().trim());
                        price = BigDecimal.valueOf(tempPrice)
                                .setScale(2, RoundingMode.HALF_UP);
                        income[0] = income[0].subtract(price).setScale(2, RoundingMode.HALF_UP);
                        end2 = false;
                    } catch (NumberFormatException e) {
                        System.out.println("It's not a number!");
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("It's not a number");
            }
            purchasesList.add(new Purchases(type, item, price));
            System.out.println("Purchase was added!");
        }
    }

    static void showListOfPurchases(List<Purchases> purchasesList, Scanner scanner) {
        if (purchasesList.size() == 0) {
            System.out.println("\nPurchase list is empty!");
            return;
        }

        String type;
        while (true) {
            try {
                Map<Integer, String> typeOfPurchases = ShowMenu.chooseTypeOfPurchaseShow();
                int chooseType = Integer.parseInt(scanner.nextLine().trim());
                switch (chooseType) {
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                        type = typeOfPurchases.get(chooseType);
                        showList(purchasesList, type);
                        break;
                    case 6:
                        return;
                    default:
                        System.out.println("Wrong choose!");
                }
            } catch (NumberFormatException e) {
                System.out.println("It's not a number!");
            }
        }
    }

    static void showList(List<Purchases> purchasesList, String type) {
        BigDecimal sum = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        if ("All".equals(type)) {
            System.out.println("\nAll:");
            for (Purchases elem : purchasesList) {
                System.out.printf("%s $%.2f\n", elem.getItem(), elem.getPrice());
                sum = sum.add(elem.getPrice().setScale(2, RoundingMode.HALF_UP));
            }
        } else {
            System.out.printf("\n%s:\n", type);
            for (Purchases elem : purchasesList) {
                if (type.equals(elem.getType())) {
                    System.out.printf("%s $%.2f\n", elem.getItem(), elem.getPrice());
                    sum = sum.add(elem.getPrice().setScale(2, RoundingMode.HALF_UP));
                }
            }
        }
        if (sum.doubleValue() == 0) {
            System.out.println("Purchase list is empty!");
        } else {
            System.out.printf("Total sum: $%.2f\n", sum.doubleValue());
        }
    }

    static void showBalance(BigDecimal[] income) {
        System.out.printf("\nBalance: $%.2f\n", income[0].doubleValue() > 0 ? income[0].doubleValue() : 0);
    }

    static void save(FileWriter purchasesFile, FileWriter balanceFile, List<Purchases> purchasesList, BigDecimal[] income)
            throws java.io.IOException {
        for (Purchases elem : purchasesList) {
            StringBuilder record = new StringBuilder();
            record.append(elem.getType()).append("|")
                    .append(elem.getItem()).append("|")
                    .append(elem.getPrice()).append("\n");
            purchasesFile.write(record.toString());
        }
        purchasesList.clear();
        purchasesFile.close();
        balanceFile.close();
        balanceFile = FileProcessing.createFileOfBalance(false);
        income[0] = income[0].doubleValue() > 0 ? income[0] : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        balanceFile.write(String.valueOf(income[0]));
        income[0] = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        balanceFile.close();
        System.out.println("\nPurchases were saved!");
    }

    static void load(Scanner readPurchases, Scanner readBalance, List<Purchases> purchasesList, BigDecimal[] income) {
        FileProcessing.startingBalance(income, readBalance);
        while (readPurchases.hasNextLine()) {
            String[] tokens = readPurchases.nextLine().split("\\|");
            String type = tokens[0];
            String item = tokens[1];
            BigDecimal price = BigDecimal.valueOf(Double.parseDouble(tokens[2]));
            purchasesList.add(new Purchases(type, item, price));
        }
        readBalance.close();
        readPurchases.close();
        System.out.println("\nPurchases were loaded!");
    }
}

class Analyzer {

    static void mainMenu(List<Purchases> purchasesList, Scanner scanner) {
        String analysis;
        int choose;
        Map<Integer, String> sort = new HashMap<>();
        sort.put(1, "all");
        sort.put(2, "byType");
        sort.put(3, "certainType");

        boolean end = true;
        while (end) {
            try {
                boolean end1 = true;
                while (end1) {
                    System.out.println(
                            "\nHow do you want to sort?\n" +
                                    "1) Sort all purchases\n" +
                                    "2) Sort by type\n" +
                                    "3) Sort certain type\n" +
                                    "4) Back"
                    );
                    choose = Integer.parseInt(scanner.nextLine());
                    switch (choose) {
                        case 1:
                        case 2:
                        case 3:
                            analysis = sort.get(choose);
                            analyzer(analysis, purchasesList, scanner);
                            break;
                        case 4:
                            end1 = false;
                            break;
                        default:
                            System.out.println("Wrong choose!");
                    }
                }
                end = false;
            } catch (NumberFormatException e) {
                System.out.println("It's not a number!");
            }
        }
    }

    static void analyzer(String analysis, List<Purchases> purchasesList, Scanner scanner) {
        switch (analysis) {
            case "all":
                all(purchasesList);
                break;
            case "byType":
                byType(purchasesList);
                break;
            case "certainType":
                certainType(purchasesList, scanner);
                break;
        }
    }

    static void all(List<Purchases> purchasesList) {
        if (purchasesList.size() == 0) {
            System.out.println("\nPurchase list is empty!");
            return;
        }

        BigDecimal sum = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        Collections.sort(purchasesList, Comparator.comparing(Purchases::getPrice).reversed());
        System.out.println("\nAll:");
        for (Purchases elem : purchasesList) {
            System.out.printf("%s $%.2f\n", elem.getItem(), elem.getPrice().doubleValue());
            sum = sum.add(elem.getPrice());
        }
        System.out.printf("Total: $%.2f\n", sum.doubleValue());
    }

    static void byType(List<Purchases> purchasesList) {
        BigDecimal[] sumOfTypes = {BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)};

        String[] types = {"Food", "Clothes", "Entertainment", "Other"};

        for (Purchases elem : purchasesList) {
            if (types[0].equals(elem.getType())) {
                sumOfTypes[0] = sumOfTypes[0].add(elem.getPrice());
            } else if (types[1].equals(elem.getType())) {
                sumOfTypes[1] = sumOfTypes[1].add(elem.getPrice());
            } else if (types[2].equals(elem.getType())) {
                sumOfTypes[2] = sumOfTypes[2].add(elem.getPrice());
            } else if (types[3].equals(elem.getType())) {
                sumOfTypes[3] = sumOfTypes[3].add(elem.getPrice());
            }
        }
        BigDecimal sum = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        for (BigDecimal elem : sumOfTypes) {
            sum = sum.add(elem);
        }
        LinkedHashMap<String, BigDecimal> sorted = sorting(types, sumOfTypes);
        System.out.println("\nTypes:");
        for (Map.Entry<String, BigDecimal> elem : sorted.entrySet()) {
            System.out.printf("%s - $%.2f\n", elem.getKey(), elem.getValue().doubleValue());
        }
        System.out.printf("Total sum: $%.2f\n", sum.doubleValue());
    }

    static LinkedHashMap<String, BigDecimal> sorting(String[] types, BigDecimal[] sumOfTypes) {
        Map<String, BigDecimal> tempMap = new HashMap<>();
        for (int i = 0; i < types.length; i++) {
            tempMap.put(types[i], sumOfTypes[i]);
        }
        return tempMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    static void certainType(List<Purchases> purchasesList, Scanner scanner) {
        String analysis = "";
        int choose;
        String[] types = {"Food", "Clothes", "Entertainment", "Other"};
        boolean end = true;
        while (end) {
            try {
                boolean end1 = true;
                while (end1) {
                    System.out.println(
                            "\nChoose the type of purchase\n" +
                                    "1) Food\n" +
                                    "2) Clothes\n" +
                                    "3) Entertainment\n" +
                                    "4) Other"
                    );
                    choose = Integer.parseInt(scanner.nextLine().trim());
                    switch (choose) {
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                            analysis = types[choose - 1];
                            end1 = false;
                            break;
                        default:
                            System.out.println("Wrong choose!");
                    }
                }
                Collections.sort(purchasesList, Comparator.comparing(Purchases::getPrice).reversed());
                List<Purchases> typeList = new ArrayList<>();
                for (Purchases elem : purchasesList) {
                    if (analysis.equals(elem.getType())) {
                        typeList.add(elem);
                    }
                }
                if (typeList.size() != 0) {
                    BigDecimal sum = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
                    System.out.printf("\n%s:\n", analysis);
                    for (Purchases elem : typeList) {
                            sum = sum.add(elem.getPrice().setScale(2, RoundingMode.HALF_UP));
                            System.out.printf("%s $%.2f\n", elem.getItem(), elem.getPrice().doubleValue());
                    }
                    System.out.printf("Total sum: $%.2f\n", sum.doubleValue());
                } else {
                    System.out.println("\nPurchase list is empty!");
                }
                end = false;
            } catch (NumberFormatException e) {
                System.out.println("It's not a number!");
            }
        }
    }
}

class Purchases {
    private String type;
    private String item;
    private BigDecimal price;

    Purchases(String type, String item, BigDecimal price) {
        this.type = type;
        this.item = item;
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public String getItem() {
        return item;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
