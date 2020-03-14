package app;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Main extends Thread {
	public Socket conexao;

	public Main(Socket aConexao) {
		this.conexao = aConexao;
	}

	public static void main(String[] args) throws IOException {
		ServerSocket servidor = new ServerSocket(12345);
		System.out.println("Porta 12345 aberta!");
		boolean fechamento = true;

		while (fechamento) {
			Socket cliente = servidor.accept();
			System.out.println("Nova conexão com o cliente " + cliente.getInetAddress().getHostAddress());
			Thread t = new Main(cliente);
			t.start();
		}
		servidor.close();
	}

	public void run() {
		try {
			Scanner s = new Scanner(conexao.getInputStream());
			while (s.hasNextLine()) {
				System.out.println(s.nextLine());
			}

			System.out.println("Usuário saiu");

			s.close();
			conexao.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}