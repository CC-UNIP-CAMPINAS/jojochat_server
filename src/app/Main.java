package app;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Vector;

import model.dao.DaoConversa;
import model.dao.DaoLogin;
import model.entities.Connection;
import model.entities.Mensagem;
import model.entities.Usuario;

public class Main {
	
	public static Vector<ClientHandler> clientsConectados = new Vector<>();
	static Vector<Usuario> usuariosAtivos = new Vector<>();

	public static void main(String[] args) throws IOException {
		try (Connection servidor = new Connection(12345)) {

			while (true) {
				Socket cliente = servidor.getConnection();

				ObjectInputStream objIns = new ObjectInputStream(cliente.getInputStream());
				ObjectOutputStream objOuts = new ObjectOutputStream(cliente.getOutputStream());

				ClientHandler cHandler = new ClientHandler(cliente, objIns, objOuts);
				Thread t = new Thread(cHandler);
				t.start();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	//
	//#################Métodos para funcionamento do servidor#################//
	//
	public static void clienteBroadcast() throws IOException {
		for (ClientHandler cliente : clientsConectados) {

			Vector<Object> vetorBroadcast = new Vector<>();
			vetorBroadcast.add("broadcast");
			vetorBroadcast.add(usuariosAtivos);

			cliente.objOuts.writeObject(vetorBroadcast);
			cliente.objOuts.reset();
		}
	}

	public static Vector<Object> validaLogin(String username, String senha) throws IOException {
		Vector<Object> resultado = DaoLogin.verificaLoginDataBase(username, senha);
		if ((Boolean) resultado.get(0)) {
			for (Usuario usuario : usuariosAtivos) {
				if (usuario.getUsuario().equals(username)) {
					resultado.set(0, false);
					System.out.println("enviando " + resultado);
					return resultado;
				}
			}

			synchronized (usuariosAtivos) {
				usuariosAtivos.add((Usuario) resultado.get(1));
			}
		}
		System.out.println("enviando " + resultado);
		return resultado;
	}

	public static void realizaLogin(Vector<?> request, ClientHandler cliente) throws IOException {
		request = Main.validaLogin((String) request.get(1), (String) request.get(2));
		if ((Boolean) request.get(0)) {
			cliente.usuario = (Usuario) request.get(1);
			synchronized (Main.clientsConectados) {
				Main.clientsConectados.add(cliente);
			}
		}
		cliente.objOuts.writeObject(request);
		cliente.objOuts.reset();
		if ((Boolean) request.get(0))
			Main.clienteBroadcast();
	}

	public static void repassaMensagem(Vector<?> request) throws IOException {
		
		Mensagem mensagem = (Mensagem) request.get(1);
		DaoConversa.guardaMensagem(mensagem);
		
		Usuario destinatario = mensagem.getDestinatario();
		for (ClientHandler cliente : Main.clientsConectados) {
			if (cliente.usuario.equals(destinatario)) {
				cliente.objOuts.writeObject(request);
				cliente.objOuts.reset();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void repassaHistorico(Vector<?> request) throws IOException {
		Usuario usuarioRequisitante = (Usuario) request.get(1);
		request = DaoConversa.buscaHistorico((Vector<Object>) request);
		
		for (ClientHandler cliente : Main.clientsConectados) {
			if (cliente.usuario.equals(usuarioRequisitante)) {
				cliente.objOuts.writeObject(request);
				cliente.objOuts.reset();
			}
		}	
	}
}
//
//#################Thread#################//
//
class ClientHandler implements Runnable {
	Usuario usuario;
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

		while (true) {
			try {
				if (!cliente.isClosed()) {
					Object recebido = objIns.readObject();
					if (recebido instanceof Vector<?>) {
						request = (Vector<?>) recebido;
						operacao = (String) request.get(0);
						switch (operacao) {
						case "login":
							Main.realizaLogin(request, this);
							break;
						case "mensagem":
							Main.repassaMensagem(request);
							break;
							
						case "historico":
							Main.repassaHistorico(request);
							break;	
						}
					}
				}
			} catch (ClassNotFoundException ex) {
				System.out.println("Tipo de objeto não esperado");
			} catch (SocketException | EOFException e) {
				try {
					cliente.close();
					System.out.println("Usuário desconectado: " + this.usuario.getUsuario());
					Main.clientsConectados.remove(this);
					Main.usuariosAtivos.remove(this.usuario);
					Main.clienteBroadcast();
				}
				catch (IOException e2) {
					e2.printStackTrace();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
