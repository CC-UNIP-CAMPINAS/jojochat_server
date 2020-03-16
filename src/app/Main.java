package src.app;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Vector;

import entities.Connection;

public class Main {
	public static Vector<ClientHandler> clientesConectados = new Vector<>();
	static Vector<String> usuariosAtivos = new Vector<>();

	static int i = 0;

	public static void main(String[] args) throws IOException {
		try (Connection servidor = new Connection(12345)) {

			while (true) {
				Socket cliente = servidor.getConnection();

				ObjectInputStream objIns = new ObjectInputStream(cliente.getInputStream());
				ObjectOutputStream objOuts = new ObjectOutputStream(cliente.getOutputStream());

				ClientHandler cHandler = new ClientHandler(cliente, "cliente " + i, objIns, objOuts);
				Thread t = new Thread(cHandler);
				synchronized (clientesConectados) {
					clientesConectados.add(cHandler);
				}

				t.start();
				

				i++;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void clienteBroadcast() throws IOException {
		for (ClientHandler cliente : clientesConectados) {
			cliente.objOuts.writeObject(usuariosAtivos);
			cliente.objOuts.reset();		
		}	
	}

	public static boolean verificaLogin(String username) {
		for (String cliente : usuariosAtivos) {
			if (username.equals(cliente)) return false;
		}
		synchronized (usuariosAtivos) {
			usuariosAtivos.add(username);
		}
		return true;
	}
}

class ClientHandler implements Runnable {
	String nome;
	final ObjectInputStream objIns;
	final ObjectOutputStream objOuts;
	Socket cliente;

	public ClientHandler(Socket cliente, String nome, ObjectInputStream objIns, ObjectOutputStream objOuts) {
		this.objIns = objIns;
		this.objOuts = objOuts;
		this.nome = nome;
		this.cliente = cliente;
	}

	@Override
	public void run() {
		Vector request;
		String operacao;

		while (!this.cliente.isClosed()) {
			try {
				if (!this.cliente.isClosed()) {
					
					Object recebido = objIns.readObject();
					if (recebido instanceof Vector<?>) {
						request = (Vector<?>) recebido;
						operacao = (String) request.get(0);
						switch (operacao){
							case "login":
								Boolean resultado = Main.verificaLogin((String) request.get(1));
								this.nome = (String) request.get(1);
								this.objOuts.writeObject(resultado);	
						}
						Main.clienteBroadcast();
					}
					else {
						throw new SocketException();
					}
				}
			} catch (EOFException | SocketException e) {
				try {
					objIns.close();
					objOuts.close();
					this.cliente.close();
					System.out.println("Usuário desconectado: " + this.nome);
					Main.clientesConectados.remove(this);
					Main.usuariosAtivos.remove(this.nome);
					try {
						Main.clienteBroadcast();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} catch (IOException e2) {
					e.printStackTrace();
				}
				
			} catch (ClassNotFoundException ex) {
				System.out.println("Tipo de objeto não esperado");
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
