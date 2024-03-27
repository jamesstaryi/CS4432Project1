import java.util.Scanner;

public class main {
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        bufferPool bp = new bufferPool();
        bp.initialize(5);
        System.out.println("The program is ready for the next command");
        String input = scanner.nextLine();
        if (input.startsWith("GET ")) {
            // Extract the number
            String[] parts = input.split(" ");
            if (parts.length == 2) {
                try {
                    int number = Integer.parseInt(parts[1]);
                    // Call bp.GET() method with the extracted number
                    System.out.println(bp.GET(number));
                }
                catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please provide a number after 'GET'.");
                }
            }
        }
        scanner.close();
    }
}