package app;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Vector;

import entities.Connection;

public class Main {
	public static Vector<ClientHandler> clientesConectados = new Vector<>();
	static ArrayList<String> usuariosAtivos = new ArrayList<>();

	static int i = 0;

	public static void main(String[] args) throws IOException {
		try (Connection servidor = new Connection(12345)) {

			while (true) {
				Socket cliente = servidor.getConnection();

				ObjectInputStream objIns = new ObjectInputStream(cliente.getInputStream());
				ObjectOutputStream objOuts = new ObjectOutputStream(cliente.getOutputStream());

				ClientHandler cHandler = new ClientHandler(cliente, "cliente " + i, objIns, objOuts);
				Thread t = new Thread(cHandler);
				clientesConectados.add(cHandler);
				usuariosAtivos.add(cHandler.nome);
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
		}
		
		
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

		String mensagem;
		while (true) {
			try {
				if (!this.cliente.isClosed()) {
					Main.clienteBroadcast();

					for (ClientHandler cliente : Main.clientesConectados) {
						
						System.out.println(cliente);
					}
					
					Object recebido = objIns.readObject();
					if (recebido instanceof String) {
						mensagem = (String) recebido;
						System.out.println(mensagem);
					}
				}
			} catch (EOFException | SocketException e) {
				System.out.println("Usuário desconectado: " + this.nome);
				Main.clientesConectados.remove(this);
				Main.usuariosAtivos.remove(this.nome);
				try {
					Main.clienteBroadcast();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} catch (ClassNotFoundException ex) {
				System.out.println("Tipo de objeto não esperado");
			} catch (IOException ex) {
				ex.printStackTrace();
			} finally {
				try {
					this.cliente.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
