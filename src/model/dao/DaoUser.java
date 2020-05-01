package model.dao;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import model.entities.Usuario;
import utils.DbUtils;
import utils.FileUtils;

public class DaoUser {

	public static Vector<Usuario> getUsuarios() throws IOException {
		Vector<Usuario> usuariosRegistrados = new Vector<>();
		PreparedStatement st = null;
		ResultSet rs = null;
		Connection conn = DbUtils.getConnection();
		try {	
			st = conn.prepareStatement("select * from users");
			rs = st.executeQuery();
			while (rs.next()) {
				Usuario user = new Usuario(rs.getInt("id"), rs.getString("nome_de_exibicao"), rs.getString("usuario"), FileUtils.fileToBytes(new File(rs.getString("img_profile"))));
				usuariosRegistrados.add(user);
			}
			return usuariosRegistrados;
		} catch (SQLException e) {
			e.printStackTrace();
			
		} finally {
			DbUtils.closeConnection(conn);
			DbUtils.fechaResultSet(rs);
			DbUtils.fechaStatement(st);
		}
		return usuariosRegistrados;
	}
}
