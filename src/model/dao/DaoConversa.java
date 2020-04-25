package model.dao;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Vector;

import app.Main;
import model.entities.Arquivo;
import model.entities.Conversa;
import model.entities.Mensagem;
import model.entities.Usuario;
import utils.ConversorDataUtils;
import utils.DbUtils;

public class DaoConversa {
	private static PreparedStatement st = null;
	private static ResultSet rs = null;

	public static void guardaMensagem(Mensagem mensagem, Conversa conversa) {
			guardaUltimaMensagem(mensagem, conversa, null, null);
			try {	
				st = DbUtils.getConnection().prepareStatement("INSERT INTO mensagens(destinatario, remetente, data, mensagem) VALUES (?, ?, ?, ?)");
				st.setInt(1, mensagem.getDestinatario().getId());
				st.setInt(2, mensagem.getRemetente().getId());
				st.setTimestamp(3, new java.sql.Timestamp(ConversorDataUtils.getDateTimeToDate(mensagem.getDateTime()).getTime()));
				st.setString(4, mensagem.getMensagem());
				st.execute();
			} catch (SQLException e) {
				e.printStackTrace();
				
			} finally {
				DbUtils.closeConnection();
				DbUtils.fechaResultSet(rs);
				DbUtils.fechaStatement(st);
			}	
	}
	
	public static void guardaMensagemComArquivo(Mensagem mensagem, Conversa conversa, String caminhoArquivoServidor, String caminhoArquivoRemetente){
		guardaUltimaMensagem(mensagem, conversa, caminhoArquivoServidor, caminhoArquivoRemetente);
		try {	
			st = DbUtils.getConnection().prepareStatement("INSERT INTO mensagens(destinatario, remetente, data, mensagem, enderecoArquivoServidor, enderecoArquivoRemetente) VALUES (?, ?, ?, ?, ?, ?)");
			st.setInt(1, mensagem.getDestinatario().getId());
			st.setInt(2, mensagem.getRemetente().getId());
			st.setTimestamp(3, new java.sql.Timestamp(ConversorDataUtils.getDateTimeToDate(mensagem.getDateTime()).getTime()));
			st.setString(4, mensagem.getMensagem());
			st.setString(5, caminhoArquivoServidor);
			st.setString(6, caminhoArquivoRemetente);
			st.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			
		} finally {
			DbUtils.closeConnection();
			DbUtils.fechaResultSet(rs);
			DbUtils.fechaStatement(st);
		}	
	}
	
	public static void guardaUltimaMensagem(Mensagem mensagem, Conversa conversa, String caminhoArquivoServidor, String caminhoArquivoRemetente) {
		if(caminhoArquivoServidor == null) {
			try {	
				if(conversa == null) {
					st = DbUtils.getConnection().prepareStatement("REPLACE INTO conversas(usuario1, usuario2, ultimaMensagem, data) VALUES (?, ?, ?, ?)");
					st.setInt(1, mensagem.getDestinatario().getId());
					st.setInt(2, mensagem.getRemetente().getId());
					st.setString(3, mensagem.getMensagem());
					st.setTimestamp(4, new java.sql.Timestamp(ConversorDataUtils.getDateTimeToDate(mensagem.getDateTime()).getTime()));			
				}
				else {
					st = DbUtils.getConnection().prepareStatement("REPLACE INTO conversas(id, usuario1, usuario2, ultimaMensagem, data) VALUES (?, ?, ?, ?, ?)");
					st.setInt(1, conversa.getId());
					st.setInt(2, mensagem.getDestinatario().getId());
					st.setInt(3, mensagem.getRemetente().getId());
					st.setString(4, mensagem.getMensagem());
					st.setTimestamp(5, new java.sql.Timestamp(ConversorDataUtils.getDateTimeToDate(mensagem.getDateTime()).getTime()));	
				}
				st.execute();
			} catch (SQLException e) {
				e.printStackTrace();
				
			} finally {
				DbUtils.closeConnection();
				DbUtils.fechaResultSet(rs);
				DbUtils.fechaStatement(st);
			}	
		}
		else {
			try {	
				if(conversa == null) {
					st = DbUtils.getConnection().prepareStatement("REPLACE INTO conversas(usuario1, usuario2, ultimaMensagem, data, enderecoArquivoServidor, enderecoArquivoRemetente) VALUES (?, ?, ?, ?, ?, ?)");
					st.setInt(1, mensagem.getDestinatario().getId());
					st.setInt(2, mensagem.getRemetente().getId());
					st.setString(3, mensagem.getMensagem());
					st.setTimestamp(4, new java.sql.Timestamp(ConversorDataUtils.getDateTimeToDate(mensagem.getDateTime()).getTime()));			
					st.setString(5, caminhoArquivoServidor);
					st.setString(6, caminhoArquivoRemetente);
				}
				else {
					st = DbUtils.getConnection().prepareStatement("REPLACE INTO conversas(id, usuario1, usuario2, ultimaMensagem, data, enderecoArquivoServidor, enderecoArquivoRemetente) VALUES (?, ?, ?, ?, ?, ?, ?)");
					st.setInt(1, conversa.getId());
					st.setInt(2, mensagem.getDestinatario().getId());
					st.setInt(3, mensagem.getRemetente().getId());
					st.setString(4, mensagem.getMensagem());
					st.setTimestamp(5, new java.sql.Timestamp(ConversorDataUtils.getDateTimeToDate(mensagem.getDateTime()).getTime()));	
					st.setString(6, caminhoArquivoServidor);
					st.setString(7, caminhoArquivoRemetente);
				}
				st.execute();
			} catch (SQLException e) {
				e.printStackTrace();
				
			} finally {
				DbUtils.closeConnection();
				DbUtils.fechaResultSet(rs);
				DbUtils.fechaStatement(st);
			}	
		}
	}
	
	public static Vector<Object> buscaHistorico(Vector<Object> request) throws IOException {
		try {
			Usuario remetente = (Usuario) request.get(1);
			Usuario destinatario = (Usuario) request.get(2);
			
			st = DbUtils.getConnection().prepareStatement("select * from mensagens where (destinatario = ? and remetente = ?) or (destinatario = ? and remetente = ?);");
			st.setInt(1, destinatario.getId());
			st.setInt(2, remetente.getId());
			st.setInt(3, remetente.getId());
			st.setInt(4, destinatario.getId());

			rs = st.executeQuery();
			Vector<Mensagem> conversa = new Vector<Mensagem>();
			
			
			while(rs.next()) {
				Arquivo arquivo = null;
				if(rs.getString("enderecoArquivoServidor") != null) {
					arquivo = new Arquivo(null, new File(rs.getString("enderecoArquivoRemetente")), new File(rs.getString("enderecoArquivoDestinatario")), new File(rs.getString("enderecoArquivoServidor")));
				}
				if(remetente.getId() != rs.getInt("remetente")) {
					conversa.add(new Mensagem(rs.getString("mensagem"), destinatario, remetente, rs.getTimestamp("data").toLocalDateTime(), arquivo));
				}
				else {
					conversa.add(new Mensagem(rs.getString("mensagem"), remetente, destinatario, rs.getTimestamp("data").toLocalDateTime(), arquivo));
				}
			}
			Collections.sort(conversa);
			
			request.clear();
			request.add("historico");
			request.add(conversa);
			
			return request;
			
		} catch (SQLException e) {
			e.printStackTrace();
			
		} finally {
			DbUtils.closeConnection();
			DbUtils.fechaResultSet(rs);
			DbUtils.fechaStatement(st);
		}
		return null;	
	}

	public static Vector<Object> buscaConversas(Vector<Object> request) throws IOException {
		try {
			Usuario requisitante = (Usuario) request.get(1);
			
			st = DbUtils.getConnection().prepareStatement("select * from conversas where (usuario1 = ? or usuario2 = ?);");
			st.setInt(1, requisitante.getId());
			st.setInt(2, requisitante.getId());

			rs = st.executeQuery();
			Vector<Conversa> conversas = new Vector<Conversa>();
			
			while(rs.next()) {
				Usuario temp = null;
				for (Usuario usuario : Main.usuariosRegistrados) {
					if(usuario.getId() == rs.getInt("usuario1") || usuario.getId() == rs.getInt("usuario2")) {
						if(!(usuario.equals(requisitante))) {
							temp = usuario;
						}
					}
				}
				
				if(rs.getString("enderecoArquivoServidor") == null) {
					conversas.add(new Conversa(rs.getInt("id"), new Mensagem(rs.getString("ultimaMensagem"), requisitante, temp, rs.getTimestamp("data").toLocalDateTime()), requisitante, temp));
				}
				else {
					File arquivoNoServidor = new File(rs.getString("enderecoArquivoServidor"));
					File arquivoNoRemetente = new File(rs.getString("enderecoArquivoRemetente"));
					conversas.add(new Conversa(rs.getInt("id"), new Mensagem(rs.getString("ultimaMensagem"), requisitante, temp, rs.getTimestamp("data").toLocalDateTime(), new Arquivo(null, arquivoNoRemetente, null, arquivoNoServidor)), requisitante, temp));
				}					
			}
			Collections.sort(conversas);
			
			request.clear();
			request.add("conversas");
			request.add(conversas);
			
			return request;
			
		} catch (SQLException e) {
			e.printStackTrace();
			
		} finally {
			DbUtils.closeConnection();
			DbUtils.fechaResultSet(rs);
			DbUtils.fechaStatement(st);
		}
		return null;	
	}
}
