package com.kshrd.autopilot.util;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.ByteArrayOutputStream;

public class SSHUtil {
    public static void sshExecCommand(String command) throws JSchException, InterruptedException {
        // setup for ssh
        String username = "root";
        String hostname = "178.128.48.96";
        int port = 22;
        Session session = null;
        ChannelExec channel = null;
        try {
            session = new JSch().getSession(username, hostname, port);
            session.setPassword("#KSHRD2023");
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
            channel.setOutputStream(responseStream);
            channel.connect();

            while (channel.isConnected()) {
                Thread.sleep(100);
            }

            String responseString = responseStream.toString();
            System.out.println(responseString);
        } finally {
            if (session != null) {
                session.disconnect();
            }
            if (channel != null) {
                channel.disconnect();
            }
        }
    }

    public static void sshExecCommandController(String command) throws JSchException, InterruptedException {
        // setup for ssh
        String username = "root";
        String hostname = "167.71.220.235";
        int port = 22;
        Session session = null;
        ChannelExec channel = null;
        try {
            session = new JSch().getSession(username, hostname, port);
            session.setPassword("#KSHRD2023");
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
            channel.setOutputStream(responseStream);
            channel.connect();

            while (channel.isConnected()) {
                Thread.sleep(100);
            }

            String responseString = responseStream.toString();
            System.out.println(responseString);
        } finally {
            if (session != null) {
                session.disconnect();
            }
            if (channel != null) {
                channel.disconnect();
            }
        }
    }
}
