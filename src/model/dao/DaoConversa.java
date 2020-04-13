package model.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import model.entities.Mensagem;
import utils.ConversorDataUtils;
import utils.DbUtils;

public class DaoConversa {
	private static PreparedStatement st = null;
	private static ResultSet rs = null;

	public static Vector<Object> verificaLoginDataBase(String usuario, String senha) {
		Vector<Object> results = new Vector<>();
		try {	
			st = DbUtils.getConnection().prepareStatement("select * from users where usuario = ? and senha = ?");
			st.setString(1, usuario);
			st.setString(2, senha);
			rs = st.executeQuery();
			while (rs.next()) {
				results.add(true);
				results.add(rs.getString("nome_de_exibicao"));
				results.add(rs.getString("usuario"));
				return results;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			
		} finally {
			DbUtils.closeConnection();
			DbUtils.fechaResultSet(rs);
			DbUtils.fechaStatement(st);
		}
		results.add(false);
		return results;
	}

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
}
