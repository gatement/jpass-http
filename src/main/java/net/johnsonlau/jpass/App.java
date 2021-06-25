package net.johnsonlau.jpass;

import net.johnsonlau.jpass.impl.MyPassLog;
import net.johnsonlau.jpass.lib.PassServer;
import net.johnsonlau.jpass.lib.conf.PassSettings;

public class App {
    public static void main(String[] args) {
        PassSettings settings = new PassSettings();
        settings.setProxyPort(Integer.parseInt(System.getProperty("proxyPort", "8118")));
        settings.setLocalListening(Boolean.parseBoolean(System.getProperty("localListening", "true")));

        final Thread thread = new Thread(new PassServer(settings, new MyPassLog()), "ProxyThread");
        thread.run();

        // to be stopped 
        //thread.start();
        //try {
        //	Thread.sleep(10000);
        //} catch (InterruptedException ex) {
        //}
        //thread.interrupt();
    }
}
