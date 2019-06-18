import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Blocked {
	private static final String URL = "url.txt";
	private static final String NAME = "nome.txt";
	private static ArrayList<String> URLAux = new ArrayList<String>();
	private static ArrayList<String> NAMEAux = new ArrayList<String>();

	
	public boolean isURLBlocked(String aux) throws IOException {
		
		
		for(String s : URLAux) {
			if(aux.contains(s)) {
				return true;
			}
		}
		return false;
		
	}
	public boolean isNAMEBlocked(String aux) throws IOException {
		for(String s : NAMEAux) {
			if(aux.contains(s)) {
				return true;
			}
		}
		return false;		
	}
	
	
	static {
		File url = new File(URL);
		File nome = new File(NAME);
		FileReader fr = null;
		BufferedReader br = null;

		try {
			if (!url.exists()) {
			url.createNewFile();
			}
			fr = new FileReader(url);
			br = new BufferedReader(fr);
			
			String linha = null;

			while ((linha = br.readLine()) != null) {
			
				URLAux.add(linha);
					

			}
			if (!nome.exists()) {
				nome.createNewFile();
			}
				fr = new FileReader(nome);
				br = new BufferedReader(fr);
				linha = null;

				while ((linha = br.readLine()) != null) {
				
					NAMEAux.add(linha);
						

				}
			
			

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
				fr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
