package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Screening;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;


public class SimpleChatServer
{
	
	private static SimpleServer server;
    public static String password;
    public static void main( String[] args ) throws IOException
    {
        Scanner scanner = new Scanner(System.in);

        // Prompt the user to enter a string
        System.out.print("Enter a my SQL password: \n");

        // Read the input string
        password = scanner.nextLine();
        scanner.close();
        Path currentPath = Paths.get("");
        String currentWorkingDir = currentPath.toAbsolutePath().toString();
        System.out.println("Current working directory: " + currentWorkingDir);

        server = new SimpleServer(3000);
        System.out.println("server is listening");
        server.listen();
    }
}
