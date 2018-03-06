package jensencharles.temperaturecontrolunit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import android.os.Bundle;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.os.Handler;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Socket socket;
    Handler updateConversationHandler;
    private static final int SERVER_PORT = 23;
    private static final String SERVER_IP = "192.168.137.180";
    private static final String PASSWORD = "temppass";
    private TextView status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        status = (TextView) findViewById(R.id.status);
        updateConversationHandler = new Handler();
        new Thread(new ClientThread()).start();
    }



    public void onClick_buttonOn(View view) {
        try {
            String packet = PASSWORD + ",Power";
            PrintWriter out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())),
                    true);
            out.println(packet);

            //Receive data from server
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch (UnknownHostException e) {
            status.setText("Can't Find TCU");
        }
        catch (IOException e) {
            status.setText("Communication Error");
        }
        catch (Exception e) {
            status.setText("Can't Find TCU");
        }
    }

    public void onClick_buttonOff(View view) {
        try {
            String packet = PASSWORD + ",Status";
            PrintWriter out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())),
                    true);
            out.println(packet);

            //Receive data from server
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch (UnknownHostException e) {
            status.setText("Can't Find TCU");
        }
        catch (IOException e) {
            status.setText("Communication Error ");
        }
        catch (Exception e) {
            status.setText("Can't Find TCU");
        }
    }

    class ClientThread implements Runnable {

        @Override
        public void run() {

            try {
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                socket = new Socket(serverAddr, SERVER_PORT);
                ServerResponseThread serverThread = new ServerResponseThread(socket);
                new Thread(serverThread).start();
            } catch (UnknownHostException e1) {
                status.setText("Can't Find TCU");
            } catch (IOException e1) {
                status.setText("Communication Error ");
            }
        }

    }

    class ServerResponseThread implements Runnable {
        private Socket clientSocket;
        private BufferedReader input;

        public ServerResponseThread(Socket clientSocket) {

            this.clientSocket = clientSocket;

            try {
                this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            }
            catch (IOException e) {
                status.setText("Error writing to Server");
            }
        }

        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    String read = input.readLine();
                    updateConversationHandler.post(new updateTextThread(read));
                }
                catch (IOException e) {
                    status.setText("Comm error read");
                }
            }
        }

    }

    class updateTextThread implements Runnable {
        private String server_response;

        public updateTextThread(String str) {
            this.server_response = str;
        }

        @Override
        public void run() {
            status.setText(server_response);
        }
    }
}
