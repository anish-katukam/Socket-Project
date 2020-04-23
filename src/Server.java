import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {

    static String[][] users = new String[100][4]; //username, ipv4-address, port, state
    static int userCount = 0;
    static int currentClientHandler = 0;
    static String[][] usersInDHT;
    static boolean DHTexists = false;

    public static void main(String[] args) {
        int serverPortNumber = Integer.parseInt(args[0]);
        int[] ports = new int[495]; //ports[0] = 6000, ports[498] = 6498. Port 6499 is for the greeting port.
        clientHandler[] clientHandlers = new clientHandler[100];

        try {
            ServerSocket serverSocket = new ServerSocket(serverPortNumber);
            while (true) {
                Socket clientBeingGreetedSocket = serverSocket.accept();
                PrintWriter out = new PrintWriter(clientBeingGreetedSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientBeingGreetedSocket.getInputStream()));

                int targetPort = 6004 + (5 * (currentClientHandler));

                out.println(targetPort);

                Socket targetSocket = new Socket(clientBeingGreetedSocket.getInetAddress(), targetPort);

                clientHandlers[currentClientHandler] = new clientHandler(targetSocket);
                clientHandlers[currentClientHandler].start();

                currentClientHandler++;
                System.out.println("Current Clients: " + currentClientHandler);
            }
        } catch (Exception e) {
        }

    }

    public static class clientHandler extends Thread {
        public Socket clientSocket;
        public PrintWriter out;
        public BufferedReader in;

        public clientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                System.out.println("A client has connected at port " + clientSocket.getPort());

                String inputLine;

                while ((inputLine = in.readLine()) != null) {
//                    System.out.println("The input: " + inputLine);

                    if (inputLine.length() >= 8 && inputLine.substring(0, 8).equals("register")) {
                        if (!register(inputLine)) {
                            System.out.println("FAILURE");
                            out.println("FAILURE");
                        } else {
                            System.out.println("SUCCESS");
                            out.println("SUCCESS");
                        }
                    }

                    if (inputLine.length() >= 9 && inputLine.substring(0, 9).equals("setup-dht")) {
                        if (!setupDht(inputLine)) {
                            System.out.println("FAILURE");
                            out.println("FAILURE");
                        } else {
                            System.out.println("SUCCESS");
                            out.println("SUCCESS");
                        }
                    }

                    if (inputLine.length() >= 12 && inputLine.substring(0, 12).equals("dht-complete")) {
                        if (!dhtComplete(inputLine)) {
                            System.out.println("FAILURE");
                            out.println("FAILURE");
                        } else {
                            System.out.println("SUCCESS");
                            out.println("SUCCESS");
                        }
                    }

                    if (inputLine.length() >= 9 && inputLine.substring(0, 9).equals("query-dht")) {
                        if (!queryDht(inputLine)) {
                            System.out.println("FAILURE");
                            out.println("FAILURE");
                        } else {
                            System.out.println("SUCCESS");
                            out.println("SUCCESS");
                        }
                    }

                    if (inputLine.length() >= 9 && inputLine.substring(0, 9).equals("leave-dht")) {
                        if (!leaveDht(inputLine)) {
                            System.out.println("FAILURE");
                            out.println("FAILURE");
                        } else {
                            System.out.println("SUCCESS");
                            out.println("SUCCESS");
                        }
                    }

                    if (inputLine.length() >= 11 && inputLine.substring(0, 11).equals("dht-rebuilt")) {
                        if (!dhtRebuilt(inputLine)) {
                            System.out.println("FAILURE");
                            out.println("FAILURE");
                        } else {
                            System.out.println("SUCCESS");
                            out.println("SUCCESS");
                        }
                    }

                    if (inputLine.length() >= 10 && inputLine.substring(0, 10).equals("deregister")) {
                        if (!deregister(inputLine)) {
                            System.out.println("FAILURE");
                            out.println("FAILURE");
                        } else {
                            System.out.println("SUCCESS");
                            out.println("SUCCESS");
                        }
                    }

                    if (inputLine.length() >= 12 && inputLine.substring(0, 12).equals("teardown-dht")) {
                        if (!teardownDht(inputLine)) {
                            System.out.println("FAILURE");
                            out.println("FAILURE");
                        } else {
                            System.out.println("SUCCESS");
                            out.println("SUCCESS");
                        }
                    }

                    if (inputLine.length() >= 17 && inputLine.substring(0, 17).equals("teardown-complete")) {
                        if (!teardownComplete(inputLine)) {
                            System.out.println("FAILURE");
                            out.println("FAILURE");
                        } else {
                            System.out.println("SUCCESS");
                            out.println("SUCCESS");
                        }
                    }
                }

            } catch (Exception e) {
                System.out.println(e.toString());
                System.out.println(e.getStackTrace()[0].getLineNumber());
            }
        }

        public boolean register(String inputLine) {
            String[] parsedInput = inputLine.split(" ");
            if (parsedInput.length != 4) {
                return false;
            }
            if (!verifyUsername(parsedInput[1])) {
                return false;
            }
            String IP = parsedInput[2];
            try {
                if (IP == null || IP.isEmpty()) {
                    return false;
                }

                String[] parts = IP.split("\\.");
                if (parts.length != 4) {
                    return false;
                }

                for (String s : parts) {
                    int i = Integer.parseInt(s);
                    if ((i < 0) || (i > 255)) {
                        return false;
                    }
                }
                if (IP.endsWith(".")) {
                    return false;
                }

            } catch (NumberFormatException nfe) {
                return false;
            }

            users[userCount][0] = parsedInput[1];
            users[userCount][1] = parsedInput[2];
            users[userCount][2] = Integer.toString(6004 + (5 * (findByUsername(parsedInput[1]))));
            System.out.println("The identifier of the connected client is " + users[userCount][2]);
            users[userCount][3] = "Free";
            userCount++;
            return true;
        }

        public boolean setupDht(String inputLine) {

            String[] parsedInput = inputLine.split(" ");

            int n = Integer.parseInt(parsedInput[1]);
            String username = parsedInput[2];

            if (parsedInput.length != 3) {
                return false;
            }

            if (findByUsername(username) == -1 || n < 2 || userCount < n) {
                return false;
            }

            if (DHTexists) {
                return false;
            }

            users[findByUsername(username)][3] = "Leader";
            usersInDHT = new String[n][4];

            usersInDHT[0][0] = username;
            usersInDHT[0][1] = users[findByUsername(username)][1];
            usersInDHT[0][2] = users[findByUsername(username)][2];
            usersInDHT[0][3] = users[findByUsername(username)][3];


            int usersAddedToDHT = 1;
            for (int i = 0; usersAddedToDHT < n; i++) {
                if (users[i][3].equals("Free")) {
                    usersInDHT[usersAddedToDHT][0] = users[i][0];
                    usersInDHT[usersAddedToDHT][1] = users[i][1];
                    usersInDHT[usersAddedToDHT][2] = users[i][2];
                    usersInDHT[usersAddedToDHT][3] = "InDHT";
                    users[i][3] = "InDHT";
                    usersAddedToDHT++;
                }
            }

            System.out.println("DHT Details: ");
            for (int i = 0; i < usersInDHT.length; i++) {
                System.out.println(usersInDHT[i][0]);
                System.out.println(usersInDHT[i][2]);
            }

            try {

                Socket leaderSocket = new Socket(usersInDHT[0][1], Integer.parseInt(usersInDHT[0][2]) - 4);


                out = new PrintWriter(leaderSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(leaderSocket.getInputStream()));

                for (int i = 0; i < usersInDHT.length; i++) {
                    out.println(usersInDHT[i][2]);
                    System.out.println(usersInDHT[i][2]);
                }
                out.println(-1);



            } catch (Exception e) {
                System.out.println(e.toString());
                System.out.println(e.getStackTrace()[0].getLineNumber());
            }
            return true;

        }

        public boolean dhtComplete(String inputLine) {
            return false;
        }

        public boolean queryDht(String inputLine) {
            return false;
        }

        public boolean leaveDht(String inputLine) {
            return false;
        }

        public boolean dhtRebuilt(String inputLine) {
            return false;
        }

        public boolean deregister(String inputLine) {
            return false;
        }

        public boolean teardownDht(String inputLine) {
            return false;
        }

        public boolean teardownComplete(String inputLine) {
            return false;
        }

        public boolean verifyUsername(String username) {
            for (int i = 0; i < users[0].length - 1; i++) {
                if (username.equals(users[i][0])) {
                    return false;
                }
            }
            return true;
        }

        public int findByUsername(String username) {
            for (int i = 0; i < users.length; i++) {
                if (username.equals(users[i][0])) {
                    return i;
                }
            }
            return -1;
        }


    }

    public static int getPortofDHTUser(int index) {
        return Integer.parseInt(usersInDHT[index][2]);
    }

}
