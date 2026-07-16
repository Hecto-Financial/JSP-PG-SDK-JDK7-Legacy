package com.settle.pg;

import java.net.Socket;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;

public final class Tls12OrHigherSocketFactoryTest {
    public static void main(String[] args) throws Exception {
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, null, null);

        Tls12OrHigherSocketFactory factory =
                new Tls12OrHigherSocketFactory(context.getSocketFactory());
        Socket socket = factory.createSocket();
        try {
            String[] enabled = ((SSLSocket) socket).getEnabledProtocols();
            assertOnlyTls12OrHigher(enabled);
            System.out.println("TLS enabled protocols=" + join(enabled));
            System.out.println("TLS TEST PASSED");
        } finally {
            socket.close();
        }
    }

    private static void assertOnlyTls12OrHigher(String[] protocols) {
        if (protocols.length == 0) {
            throw new AssertionError("No TLS 1.2 or higher protocol is enabled");
        }
        for (int i = 0; i < protocols.length; i++) {
            if (!"TLSv1.2".equals(protocols[i]) && !"TLSv1.3".equals(protocols[i])) {
                throw new AssertionError("Unexpected protocol: " + protocols[i]);
            }
        }
    }

    private static String join(String[] values) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                result.append(',');
            }
            result.append(values[i]);
        }
        return result.toString();
    }
}
