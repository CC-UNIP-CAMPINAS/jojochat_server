package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

import model.entities.Arquivo;

public class FileUtils {
	
	public static String conversorDeUnidade(File arquivo) {
		DecimalFormat df = new DecimalFormat("#.00");
		if(arquivo.length() <= 1024) {
			String resultado = String.valueOf(arquivo.length());
			return resultado+"B";
		}
		if(arquivo.length() > 1024 && arquivo.length() <= 1048576) {
			double calculo = (arquivo.length()/1024.0);
			String resultado = String.valueOf(df.format(calculo));
			return resultado+"kB";
		}
		if(arquivo.length() > 1048576) {
			double calculo = (arquivo.length()/1048576.0);
			String resultado = String.valueOf(df.format(calculo));
			return resultado+"MB";
		}
		return null;
	}
	
	public static boolean isImg(File arquivo) {
		if(arquivo.getName().contains(".png") || arquivo.getName().contains(".jpg")) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public static String gravaArquivo(Arquivo arquivo, String destino) throws IOException{
		criaDiretorio(destino);
	    destino += File.separatorChar+arquivo.getLocalizacaoRemetente().getName();
	    FileOutputStream fos = new FileOutputStream(destino);
        fos.write(arquivo.getConteudo());
        fos.close();
        return destino;
	}
	
	public static byte[] fileToBytes(File arquivo) throws IOException {
		FileInputStream fis;
        try {
        	byte[] bFile = new byte[(int) arquivo.length()];
			fis = new FileInputStream(arquivo);
			fis.read(bFile);
	        fis.close();
	        return bFile;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean criaDiretorio(String caminho) {
		File diretorio = new File(caminho);
		if(!diretorio.exists()) {
			diretorio.mkdirs();
			return false;
		}
		else {
			return true;
		}
	}
	
	public static boolean verificaArquivo(File arquivo) {
		if(arquivo.exists()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public static String getCaminhoArquivos(){
		if (IdentificadorSoUtils.sistema().equals("linux")){
			 return System.getProperty("user.home")+File.separatorChar+"Documents"+File.separatorChar+"JOJO_DATA"+ File.separatorChar+"Arquivos";
		}
		return System.getProperty("user.home")+File.separatorChar+"Documents"+File.separatorChar+"JOJO_DATA"+ File.separatorChar+"Arquivos";
	}
}
