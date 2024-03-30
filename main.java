import java.util.Scanner;

public class main {
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        bufferPool bp = new bufferPool();
        bp.initialize(3);
        System.out.println("The program is ready for the next command");
        String input = scanner.nextLine();
        while(!input.equals("QUIT")){
            if (input.startsWith("GET ")) {
                String[] parts = input.split(" ");
                if (parts.length == 2) {
                    try {
                        int number = Integer.parseInt(parts[1]);
                        System.out.println(bp.GET(number));
                    }
                    catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please provide a number after 'GET'.");
                    }
                }
            }
            else if(input.startsWith("SET ")) {
                String[] parts = input.split(" ", 3);
                if (parts.length == 3) {
                    try {
                        int number = Integer.parseInt(parts[1]);
                        String str = parts[2].replaceAll("\"", "");
                        System.out.println(bp.SET(number, str));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please provide a number after 'SET'.");
                    }
                } else {
                    System.out.println("Invalid input format. Please provide a number and a string enclosed in double quotes after 'SET'.");
                }
            }
            input = scanner.nextLine();
        }
        scanner.close();
    }
}