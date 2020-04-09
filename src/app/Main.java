package app;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import model.dao.DaoLogin;
import model.entities.Connection;
import model.entities.Usuario;

public class Main {
	public static Vector<ClientHandler> clientesConectados = new Vector<>();
	static Vector<Usuario> usuariosAtivos = new Vector<>();
	public PreparedStatement ps = null;
	public ResultSet rs = null;
	
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

	public static void clienteBroadcast() throws IOException {
		for (ClientHandler cliente : clientesConectados) {
			
			Vector<Object> vetorBroadcast = new Vector<>();
			vetorBroadcast.add("broadcast");
			vetorBroadcast.add(usuariosAtivos);
					
			cliente.objOuts.writeObject(vetorBroadcast);
			cliente.objOuts.reset();		
		}	
	}

	public static Vector<Object> verificaLogin(String username, String senha) throws IOException {
		Vector<Object> resultado = DaoLogin.verificaLoginDataBase(username, senha);
		if((Boolean) resultado.get(0)) {
			for (Usuario usuario : usuariosAtivos) {
				if (usuario.getUsuario().equals(username)) {
					resultado.set(0, false);
					System.out.println("enviando " + resultado);
					return resultado;
				}
			}
			
			synchronized (usuariosAtivos) {
				usuariosAtivos.add(new Usuario((String) resultado.get(1), username));
			}
		}
		System.out.println("enviando " + resultado);
		return resultado;
	}
}

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
		Usuario destinatario;

		while (true) {
			try {
				if (!cliente.isClosed()) {	
					Object recebido = objIns.readObject();
					if (recebido instanceof Vector<?>) {
						request = (Vector<?>) recebido;
						operacao = (String) request.get(0);
						switch (operacao){
							case "login":
								request = Main.verificaLogin((String) request.get(1), (String) request.get(2));
								if((Boolean)request.get(0)) {
									usuario = new Usuario((String) request.get(1), (String) request.get(2));
									synchronized (Main.clientesConectados) {
										Main.clientesConectados.add(this);
									}
								}
								this.objOuts.writeObject(request);	
								this.objOuts.reset();
								if ((Boolean) request.get(0)) Main.clienteBroadcast();
								break;
							case "mensagem":
								destinatario = (Usuario) request.get(1);
								for (ClientHandler cliente : Main.clientesConectados) {
									if(cliente.usuario.equals(destinatario)) {
										cliente.objOuts.writeObject(request);
										cliente.objOuts.reset();
									}
								}
								break;	
						}
					}
				}
			} catch (EOFException | SocketException e) {
				try {
					this.cliente.close();
					System.out.println("Usuário desconectado: " + this.usuario.getUsuario());
					Main.clientesConectados.remove(this);
					Main.usuariosAtivos.remove(this.usuario);
					Main.clienteBroadcast();
				} catch (IOException e1) {
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
