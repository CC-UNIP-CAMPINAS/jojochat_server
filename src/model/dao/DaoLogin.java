package model.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import app.Main;
import model.entities.FormularioCadastro;
import model.entities.Usuario;
import utils.DbUtils;
import utils.FileUtils;

public class DaoLogin {
	
	public static Vector<Object> verificaLoginDataBase(String usuario, String senha) {
		Vector<Object> results = new Vector<>();
		PreparedStatement st = null;
		ResultSet rs = null;
		Connection conn = DbUtils.getConnection();
		try {	
			st = conn.prepareStatement("select * from users where usuario = ? and senha = ?");
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
			DbUtils.closeConnection(conn);
			DbUtils.fechaResultSet(rs);
			DbUtils.fechaStatement(st);
		}
		results.add(false);
		return results;
	}

	public static Vector<Object> criaCadastro(FormularioCadastro novoUsuario) throws IOException {
		for (Usuario users : Main.usuariosRegistrados) {
			if(users.getUsuario().equals(novoUsuario.getLogin())) {
				Vector<Object> resposta = new Vector<Object>();
				resposta.add(Boolean.FALSE);
				System.out.println("retorno falso");
				return resposta;
			}
		}
		PreparedStatement st = null;
		Connection conn = DbUtils.getConnection();
		try {	
			System.out.println("retorno true");
			st = conn.prepareStatement("INSERT INTO users (usuario, senha, nome_de_exibicao, img_profile) VALUES (?, ?, ?, ?);");
			st.setString(1, novoUsuario.getLogin());
			st.setString(2, novoUsuario.getSenha());
			st.setString(3, novoUsuario.getNomeUsuario());
			st.setString(4, FileUtils.gravaImagemPerfil(novoUsuario.getImgPerfil(), novoUsuario.getNomeArquivo().getName(), FileUtils.getCaminhoImagensPerfil()));
			st.execute();
			
		} catch (SQLException e) {
			e.printStackTrace();
			
		} finally {
			DbUtils.closeConnection(conn);
			DbUtils.fechaStatement(st);
		}
		Vector<Object> resposta = new Vector<Object>();
		resposta.add(Boolean.TRUE);
		return resposta;
	}
}
