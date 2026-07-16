package com.settle.pg;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * 모든 생성 소켓에서 TLS 1.2만 활성화하는 JDK 7 호환 소켓 팩토리입니다.
 */
public final class Tls12SocketFactory extends SSLSocketFactory {
    private static final String[] TLS_12_ONLY = new String[] { "TLSv1.2" };
    private final SSLSocketFactory delegate;

    public Tls12SocketFactory(SSLSocketFactory delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate must not be null");
        }
        this.delegate = delegate;
    }

    public String[] getDefaultCipherSuites() {
        return delegate.getDefaultCipherSuites();
    }

    public String[] getSupportedCipherSuites() {
        return delegate.getSupportedCipherSuites();
    }

    public Socket createSocket() throws IOException {
        return enableTls12(delegate.createSocket());
    }

    public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
            throws IOException {
        return enableTls12(delegate.createSocket(socket, host, port, autoClose));
    }

    public Socket createSocket(String host, int port)
            throws IOException, UnknownHostException {
        return enableTls12(delegate.createSocket(host, port));
    }

    public Socket createSocket(String host, int port, InetAddress localHost, int localPort)
            throws IOException, UnknownHostException {
        return enableTls12(delegate.createSocket(host, port, localHost, localPort));
    }

    public Socket createSocket(InetAddress host, int port) throws IOException {
        return enableTls12(delegate.createSocket(host, port));
    }

    public Socket createSocket(InetAddress address, int port,
            InetAddress localAddress, int localPort) throws IOException {
        return enableTls12(delegate.createSocket(address, port, localAddress, localPort));
    }

    private Socket enableTls12(Socket socket) {
        if (!(socket instanceof SSLSocket)) {
            throw new IllegalStateException("Expected SSLSocket but got "
                    + socket.getClass().getName());
        }
        ((SSLSocket) socket).setEnabledProtocols(TLS_12_ONLY);
        return socket;
    }
}
