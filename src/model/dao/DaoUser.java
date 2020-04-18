package model.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import model.entities.Usuario;
import utils.DbUtils;

public class DaoUser {
	private static PreparedStatement st = null;
	private static ResultSet rs = null;

	public static Vector<Usuario> getUsuarios() {
		Vector<Usuario> usuariosRegistrados = new Vector<>();
		try {	
			st = DbUtils.getConnection().prepareStatement("select * from users");
			rs = st.executeQuery();
			while (rs.next()) {
				Usuario user = new Usuario(rs.getInt("id"), rs.getString("nome_de_exibicao"), rs.getString("usuario"));
				usuariosRegistrados.add(user);
			}
			return usuariosRegistrados;
		} catch (SQLException e) {
			e.printStackTrace();
			
		} finally {
			DbUtils.closeConnection();
			DbUtils.fechaResultSet(rs);
			DbUtils.fechaStatement(st);
		}
		return usuariosRegistrados;
	}
}
