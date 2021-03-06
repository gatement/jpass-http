package net.johnsonlau.jpass.lib;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Base64;

public class PassSocketHandler extends Thread {

    private Socket socket;

    public PassSocketHandler(Socket socket) {
        this.socket = socket;
        //PassServer.log.info("Creating connection, connection count up to = " + Integer.valueOf(PassServer.connectionCount.incrementAndGet()));
    }

    @Override
    public void run() {
        OutputStream clientOutput = null;
        InputStream clientInput = null;
        Socket proxySocket = null;
        InputStream proxyInput = null;
        OutputStream proxyOutput = null;
        try {
            clientInput = socket.getInputStream();
            clientOutput = socket.getOutputStream();
            StringBuilder headStr = new StringBuilder();

            int inputByte = clientInput.read();
            while (inputByte != -1) {
                headStr.append((char) inputByte);

                // Finish receiving HTTP headers
                if (headStr.length() > 4
                        && headStr.substring(headStr.length() - 4, headStr.length()).equals("\r\n\r\n")) {

                    //PassServer.log.info(headStr.toString());
                    String[] headLines = headStr.toString().split("\r\n");

                    // Extract HTTP method and target server:
                    //   Example1: CONNECT www.example.com:443 HTTP/1.1
                    //   Example2: POST http://www.example.com/a/b/c HTTP/1.1
                    String[] firstLine = headLines[0].split(" ");

                    // 1. get httpMethod
                    String httpMethod = firstLine[0];

                    // 2. get targetHost, targetPort
                    String hostLine = firstLine[1];
                    String targetHost = "";
                    int targetPort = 80;
                    if (hostLine.toLowerCase().startsWith("http")) {
                        String[] host = hostLine.split("://")[1].split("/")[0].split(":");
                        targetHost = host[0];
                        if (host.length > 1) {
                            targetPort = Integer.valueOf(host[1]);
                        }
                    } else {
                        String[] host = hostLine.split(":");
                        targetHost = host[0];
                        if (host.length > 1) {
                            targetPort = Integer.valueOf(host[1]);
                        }
                    }
                    // Connect target server
                    PassServer.log.info("Connect target " + targetHost + ":" + String.valueOf(targetPort));

                    // 3. create proxy channel
                    // Connect remote server directly
                    proxySocket = new Socket(targetHost, targetPort);
                    proxyInput = proxySocket.getInputStream();
                    proxyOutput = proxySocket.getOutputStream();

                    // 4. response CONNECT or transmit to targetHost
                    // Process HTTP Method CONNECT
                    if ("CONNECT".equalsIgnoreCase(httpMethod)) {
                        // For HTTPS request, consume the initiative HTTP request and send back response
                        clientOutput.write("HTTP/1.1 200 Connection Established\r\n\r\n".getBytes());
                        clientOutput.flush();
                    } else {
                        // For HTTP request, transmit the initiative HTTP request
                        proxyOutput.write(headStr.toString().getBytes());
                        proxyOutput.flush();
                    }

                    break;
                }

                inputByte = clientInput.read();
            }

            // 5. do the following transmission
            // New thread continue sending data to target server
            new PassStreamingThread(clientInput, proxyOutput).start();

            // Receive target response
            byte[] data = new byte[65536]; // 64KB
            int readCount = proxyInput.read(data);
            while (readCount != -1) {
                clientOutput.write(data, 0, readCount);
                clientOutput.flush();
                readCount = proxyInput.read(data);
            }
        } catch (SocketException ex) {
            // peer closed the socket
        } catch (Exception ex) {
            PassServer.log.info("exception: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            if (proxyInput != null) {
                try {
                    proxyOutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (proxyOutput != null) {
                try {
                    proxyOutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (proxySocket != null) {
                try {
                    proxySocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (clientInput != null) {
                try {
                    clientInput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (clientOutput != null) {
                try {
                    clientOutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //PassServer.log.info("Closed connection, connection count down to = " + Integer.valueOf(PassServer.connectionCount.decrementAndGet()));
        }
    }
}
