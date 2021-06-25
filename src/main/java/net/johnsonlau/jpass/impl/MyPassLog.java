package net.johnsonlau.jpass.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.johnsonlau.jpass.lib.conf.PassLog;

public class MyPassLog extends PassLog {

    @Override
    public void info(String msg) {
        String dateString = "[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "] ";
        System.out.println(dateString + msg);
    }

}
