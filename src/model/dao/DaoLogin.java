package model.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import model.entities.Usuario;
import utils.DbUtils;

public class DaoLogin {
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
				Usuario user = new Usuario(rs.getInt("id"), rs.getString("nome_de_exibicao"), rs.getString("usuario"));
				results.add(user);
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
}
