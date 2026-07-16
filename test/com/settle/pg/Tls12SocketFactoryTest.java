package com.settle.pg;

import java.net.Socket;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;

public final class Tls12SocketFactoryTest {
    public static void main(String[] args) throws Exception {
        SSLContext context = SSLContext.getInstance("TLSv1.2");
        context.init(null, null, null);

        Tls12SocketFactory factory = new Tls12SocketFactory(context.getSocketFactory());
        Socket socket = factory.createSocket();
        try {
            String[] enabled = ((SSLSocket) socket).getEnabledProtocols();
            if (enabled.length != 1 || !"TLSv1.2".equals(enabled[0])) {
                throw new AssertionError("Expected TLSv1.2 only");
            }
            System.out.println("TLS enabled protocols=TLSv1.2");
            System.out.println("TLS TEST PASSED");
        } finally {
            socket.close();
        }
    }
}
