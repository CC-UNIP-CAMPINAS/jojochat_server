package app;

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
	public static void main(String[] args) throws IOException {
		try (Connection servidor = new Connection(12345)) {

			while (true) {
				Socket cliente = servidor.getConnection();

				ObjectInputStream objIns = new ObjectInputStream(cliente.getInputStream());
				ObjectOutputStream objOuts = new ObjectOutputStream(cliente.getOutputStream());

				ClientHandler cHandler = new ClientHandler(cliente, objIns, objOuts);
				Thread t = new Thread(cHandler);
				synchronized (clientesConectados) {
					clientesConectados.add(cHandler);
				}

				t.start();
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

	public static boolean verificaLogin(String username) throws IOException {
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

	public ClientHandler(Socket cliente, ObjectInputStream objIns, ObjectOutputStream objOuts) {
		this.objIns = objIns;
		this.objOuts = objOuts;
		this.cliente = cliente;
	}

	@Override
	public void run() {
		Vector<?> request;
		String operacao;
		Boolean resultado = false;

		while (true) {
			try {
				if (!cliente.isClosed()) {	
					Object recebido = objIns.readObject();
					if (recebido instanceof Vector<?>) {
						request = (Vector<?>) recebido;
						operacao = (String) request.get(0);
						switch (operacao){
							case "login":
								resultado = Main.verificaLogin((String) request.get(1));
								System.out.println("enviando " + resultado);
								this.nome = (String) request.get(1);
								this.objOuts.writeObject(resultado);	
						}
						if (resultado) Main.clienteBroadcast();
					}
				}
			} catch (EOFException | SocketException e) {
				try {
					this.cliente.close();
					System.out.println("Usuário desconectado: " + this.nome);
					Main.clientesConectados.remove(this);
					Main.usuariosAtivos.remove(this.nome);
					Main.clienteBroadcast();
					System.out.println(e.getMessage());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (ClassNotFoundException ex) {
				System.out.println("Tipo de objeto não esperado");
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
