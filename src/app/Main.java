package app;

import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class Main {
    public static void main(String[] args) throws Exception {
        try {
            // Instancia o ServerSocket ouvindo a porta 12345
            ServerSocket servidor = new ServerSocket(12345);
            System.out.println("Servidor ouvindo a porta 12345");
            while (true) {
                // o método accept() bloqueia a execução até que
                // o servidor receba um pedido de conexão
                Socket cliente = servidor.accept();
                System.out.println("Cliente conectado: " + cliente.getInetAddress().getHostAddress());
                ObjectOutputStream saida = new ObjectOutputStream(cliente.getOutputStream());
                saida.flush();
                saida.writeObject(new Date());
                System.out.println("Enviou");
                saida.close();
                System.out.println("Saida fechou");
                cliente.close();
                System.out.println("cliente fechou");
            }
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        } finally {
            
        }
    }
}