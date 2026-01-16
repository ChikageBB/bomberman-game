package com.bomberman.server;

import com.bomberman.protocol.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable{

    private Socket socket;
    private GameServer server;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean running;

    public ClientHandler(Socket socket, GameServer gameServer) {
        this.socket = socket;
        this.server = gameServer;
        this.running = true;
    }


    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            while (running) {
                Message message = (Message) in.readObject();
                server.handleMessage(message, this);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Client disconnected: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    public void sendMessage(Message message) {
        try {
            if (out != null) {
                out.writeObject(message);
                out.reset();
                out.flush();
            }
        } catch (IOException e) {
            System.out.println("Error sending message: " + e.getMessage());
            cleanup();
        }
    }

    private void cleanup() {
        running = false;
        server.removeClient(this);
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
