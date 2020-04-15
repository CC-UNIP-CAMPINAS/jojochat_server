package model.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Vector;

import model.entities.Mensagem;
import model.entities.Usuario;
import utils.ConversorDataUtils;
import utils.DbUtils;

public class DaoConversa {
	private static PreparedStatement st = null;
	private static ResultSet rs = null;

	public static void guardaMensagem(Mensagem mensagem) {
		try {	
			st = DbUtils.getConnection().prepareStatement("INSERT INTO conversas(destinatario, remetente, data, mensagem) VALUES (?, ?, ?, ?)");
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
	
	public static Vector<Object> buscaHistorico(Vector<Object> request) {
		try {
			Usuario remetente = (Usuario) request.get(1);
			Usuario destinatario = (Usuario) request.get(2);
			
			st = DbUtils.getConnection().prepareStatement("select * from conversas where (destinatario = ? and remetente = ?) or (destinatario = ? and remetente = ?);");
			st.setInt(1, destinatario.getId());
			st.setInt(2, remetente.getId());
			st.setInt(3, remetente.getId());
			st.setInt(4, destinatario.getId());

			rs = st.executeQuery();
			Vector<Mensagem> conversa = new Vector<Mensagem>();
			
			
			while(rs.next()) {
				if(remetente.getId() != rs.getInt("remetente")) {
					conversa.add(new Mensagem(rs.getString("mensagem"), destinatario, remetente, rs.getTimestamp("data").toLocalDateTime()));
				}
				else {
					conversa.add(new Mensagem(rs.getString("mensagem"), remetente, destinatario, rs.getTimestamp("data").toLocalDateTime()));
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
}
