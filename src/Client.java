import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {

    static int identifier;
    static int rightTargetIP;
    static int rightTargetPort;
    static int dhtIdentifier;

    public static void main(String[] args) {
        String serverIPAddress = args[0];
        int serverPortNumber = Integer.parseInt(args[1]);

        try {
            Socket greetSocket = new Socket(serverIPAddress, serverPortNumber);
            BufferedReader in = new BufferedReader(new InputStreamReader(greetSocket.getInputStream()));

            int targetPort = Integer.parseInt(in.readLine());
            identifier = targetPort;

            ServerSocket socket = new ServerSocket(targetPort);
            Socket targetSocket = socket.accept();

            new serverHandler(targetSocket).start();
            new leaderListener().start();


        } catch (Exception e) {
            System.out.println(e.toString());
            System.out.println(e.getStackTrace()[0].getLineNumber());
        }
    }

    public static class serverHandler extends Thread {
        public Socket serverCommunicateSocket;
        public PrintWriter out;
        public BufferedReader in;

        public serverHandler(Socket socket) {
            serverCommunicateSocket = socket;
        }

        public void run() {
            try {
                out = new PrintWriter(serverCommunicateSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(serverCommunicateSocket.getInputStream()));

                Scanner scanner = new Scanner(System.in);
                String userInput;

                String serverInput;

                while (true) {
                    userInput = scanner.nextLine();
                    out.println(userInput);
                    serverInput = in.readLine();
                    System.out.println(serverInput);
                }

            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
    }

    public static class leftListener extends Thread {
        public Socket leftSocket;
        public PrintWriter out;
        public BufferedReader in;

        public void run() {
            System.out.println("Running leftListener at + " + (identifier - 2) + ". My identifier is " + identifier);
            try {
                ServerSocket listener = new ServerSocket(identifier - 2);
                leftSocket = listener.accept();
                String leftInput;

                out = new PrintWriter(leftSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(leftSocket.getInputStream()));


                while (true) {
                    System.out.println("Connected to left neighbor");
                    out.println("HEY THERE BUDDY");
                }

            } catch (Exception e) {
                System.out.println(e.toString());
                System.out.println(e.getStackTrace()[0].getLineNumber());
            }
        }
    }

    public static class rightWriter extends Thread {
        public Socket rightSocket;
        public PrintWriter out;
        public BufferedReader in;

        String rightIP;
        int rightPort;

        public rightWriter(String rightIP, int rightPort) {
            this.rightIP = rightIP;
            this.rightPort = rightPort;
        }

        public void run() {
            System.out.println("Running rightWriter. My identifier is " + identifier);
            try {
                rightSocket = new Socket(rightIP, rightPort);
                String rightInput;

                out = new PrintWriter(rightSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(rightSocket.getInputStream()));

                while (true) {
                    rightInput = in.readLine();
                    System.out.println(rightInput);
                }

            } catch (Exception e) {
                System.out.println(e.toString());
                System.out.println(e.getStackTrace()[0].getLineNumber());
            }
        }
    }

    public static class leaderListener extends Thread {
        public Socket leaderSocket;
        public PrintWriter out;
        public BufferedReader in;
        public ArrayList<Integer> targetPorts = new ArrayList<>();

        public void run() {

            try {
                ServerSocket leaderListener = new ServerSocket(identifier - 4);
                leaderSocket = leaderListener.accept();

                out = new PrintWriter(leaderSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(leaderSocket.getInputStream()));


                String serverPortInput = in.readLine();
                while (!serverPortInput.equals("-1")) {
                    targetPorts.add(Integer.parseInt(serverPortInput));
                    System.out.println(Integer.parseInt(serverPortInput));
                    serverPortInput = in.readLine();
                }

                for (int i = 0; i < targetPorts.size(); i++) {

                }



            } catch (Exception e) {
                System.out.println(e.toString());
                System.out.println(e.getStackTrace()[0].getLineNumber());
            }
        }
    }

    public static class leaderConnector extends Thread {
        public Socket connectorSocket;
        public PrintWriter out;
        public BufferedReader in;

        String targetIP;
        int targetPort;

        public leaderConnector(String targetIP, int targetPort) {
            this.targetIP = targetIP;
            this.targetPort = targetPort;
        }

        public void run() {
            try {
                connectorSocket = new Socket(targetIP, targetPort);
                String leaderOutput = "";

                out = new PrintWriter(connectorSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(connectorSocket.getInputStream()));

                while (true) {
                    out.println(leaderOutput);
                }

            } catch (Exception e) {
                System.out.println();
            }
        }
    }
}
