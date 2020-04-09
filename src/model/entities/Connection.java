package model.entities;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Connection implements AutoCloseable{
	private ServerSocket servidor;
	private int porta;
	
	public Connection(int port) {
		try {
			this.servidor = new ServerSocket(port);
			this.porta = port;
			System.out.println("Servidor inicializado na porta: " + port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ServerSocket getServidor() {
		return servidor;
	}
	
	public int getPorta() {
		return porta;
	}
	
	public Socket getConnection() {
		try {
			return getServidor().accept();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Nova conexão com o cliente " + getServidor().getInetAddress().getHostAddress());
		return null;
	}

	@Override
	public void close() throws Exception {
		servidor.close();	
	}

	
}
