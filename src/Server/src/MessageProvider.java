import com.sun.org.apache.xpath.internal.operations.Bool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Message provider
 * Created by PD on 10.04.2017.
 */
public class MessageProvider implements MessageProviderInterface{

    private BufferedReader socketReader;
    private PrintWriter socketWriter;

    MessageProvider(Socket clientSocket) {
        try {
            this.socketReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.socketWriter = new PrintWriter(clientSocket.getOutputStream());
        } catch (IOException e) {

        }
    }

    @Override
    public Message getMessage() {
        try {
            String data = socketReader.readLine();
            return processMessage(data);
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    @Override
    public boolean sendPoll() {
        sendMessage(new Message("poll","poll"));
        return true;
    }

    @Override
    public boolean replyPoll() {
        sendMessage(new Message("poll", true));
        return true;
    }

    @Override
    public boolean sendMessage(Message message) {
        String data = String.valueOf(message.getContent());
        socketWriter.println(message.getHeader()+'&'+data);
        socketWriter.flush();
        return true;
    }

    @Override
    public Message processMessage(String data) {
        Message message = new Message();
        String splittedData[] = data.split("&");
        message.setHeader(splittedData[0]);
        switch (message.getHeader()){
            case "boolean":
                message.setBoolContent(Boolean.valueOf(splittedData[1]));
                break;
            case "number":
                message.setNumberContent(Double.valueOf(splittedData[1]));
                break;
            case "coordinates":
                String values[] = splittedData[1].split("%");
                ArrayList<Float> al = new ArrayList<>();
                al.add(Float.valueOf(values[0]));
                al.add(Float.valueOf(values[1]));
                message.setCoordinatesContent(al);
                break;
            default:
                message.setStringContent(splittedData[1]);
        }
        return message;
    }
}
