package app;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import entities.Connection;

public class Main extends Thread {
	public Socket conexao;
	public static ArrayList<Socket> conexoes = new ArrayList<>();
	public static ArrayList<String> clientesConectados = new ArrayList<>();

	public Main(Socket aConexao) {
		this.conexao = aConexao;

	}

	public static void main(String[] args) throws IOException {
		try (Connection servidor = new Connection(12345)) {
			boolean fechamento = true;

			while (fechamento) {
				Socket cliente = servidor.getConnection();
				Thread t = new Main(cliente);
				t.start();
				conexoes.add(cliente);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void run() {
		try (ObjectInputStream objIns = new ObjectInputStream(conexao.getInputStream());
				ObjectOutputStream objOut = new ObjectOutputStream(conexao.getOutputStream())) {

			for (Socket socket : conexoes) {
				clientesConectados.add(socket.toString());
			}
			objOut.writeObject(clientesConectados);

			String mensagem = null;
			while (true) {
				mensagem = (String) objIns.readObject();
				objOut.writeObject(clientesConectados);
				System.out.println(mensagem);
			}

		} catch (EOFException | SocketException e) {
			System.out.println("Usuário saiu: " + conexao.toString());
			conexoes.remove(conexao);

			for (Socket socket : conexoes) {
				clientesConectados.add(socket.toString());
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}