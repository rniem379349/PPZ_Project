package pl.locationbasedgame;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.InputStream;
import java.util.Properties;

public class CommunicationService extends Service {

    private String TAG = "COMMSERVICE";
    private SocketHandler socketHandler;
    private IBinder binder = new ServerBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AssetManager assetManager = getBaseContext().getAssets();
                    InputStream inputStream = assetManager.open("connection.properties");
                    Properties properties = new Properties();
                    properties.load(inputStream);
                    socketHandler = new SocketHandler(properties.getProperty("IP"), Integer.valueOf(properties.getProperty("port")));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "PROPERTIES FAILURE");
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    void closeConnection() {
        socketHandler.closeSocket();
    }

    void sendLoginRequestToServer(String name, String password, LoginFragment fragment) {
        Authenticator authenticator = new Authenticator();
        authenticator.setCaller(fragment);
        authenticator.setHandler(socketHandler);
        authenticator.execute(name, password);
    }

    void sendRegisterRequestToServer(String name, String password, String mail, RegisterFragment fragment) {
        Registrator registrator = new Registrator();
        registrator.setCaller(fragment);
        registrator.setHandler(socketHandler);
        registrator.execute(name, password, mail);
    }

    void createNewLobby(LobbyTask manager, LobbyTaskCallback context) {
        manager.set(socketHandler, context, -1);
        manager.execute('C');
    }

    void joinExistingLobby(LobbyTask manager, LobbyTaskCallback context, int id) {
        manager.set(socketHandler, context, id);
        manager.execute('J');
    }

    class ServerBinder extends Binder {
        CommunicationService getService() {
            return CommunicationService.this;
        }
    }
}