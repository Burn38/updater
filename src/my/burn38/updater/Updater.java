package my.burn38.updater;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Updater {

	private Updater instance = null;
	private String localVersion = null, remoteVersion = null;
	private boolean caseSensitive = false;
	private URL remoteUpdateURL = null;
	private File localUpdateFile = null;
	
	public Updater(String localVersion, boolean caseSensitive, URL remoteUpdateURL, File localUpdateFile){
		System.out.println("Creating new updater, please wait...");
		this.instance = this;
		this.localVersion = localVersion;
		this.caseSensitive = caseSensitive;
		this.remoteUpdateURL = remoteUpdateURL;
		this.localUpdateFile = localUpdateFile;
		System.out.println("New updater was created: ");
		String remoteVersion = "`NULL`"; try {fetchRemoteVersion();} catch (IOException ex){remoteVersion = "VERSION_NOT_FOUND";ex.printStackTrace();}; 
		System.out.println("VERSION: "+localVersion+" -> "+remoteVersion);
		System.out.println("FILE: "+remoteUpdateURL.toString()+" -> "+localUpdateFile.getPath());
	}
	
	public static void main(String[] args) {
		
	}
	
	public String getLocalVersion() {
		return localVersion;
	}
	public String getRemoteVersion() {
		return remoteVersion;
	}
	public URL getRemoteUpdateURL() {
		return remoteUpdateURL;
	}
	public boolean isCaseSensitive() {
		return caseSensitive;
	}
	public File getLocalUpdateFile() {
		return localUpdateFile;
	}
	public Updater getInstance() {
		return instance;
	}
	
	public File downloadFile(String filename, URL url) {
		File toWrite = new File(filename);
		String file_name = filename.substring(0, filename.lastIndexOf("."));
		String file_extension = "."+filename.substring(filename.lastIndexOf("."));
		try {
			toWrite = File.createTempFile(file_name, file_extension);
		} catch (IOException e) {
			e.printStackTrace();
		}
		 //Code to download
		  try {
			Files.copy(url.openStream(), toWrite.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		 return new File(toWrite.getAbsolutePath());
	}
	
	public void fetchRemoteVersion() throws IOException {
		String nurl = ""; String remoteVersionURL = this.remoteUpdateURL.toString(); String suffix = "";
		nurl = remoteVersionURL;
		if (nurl.contains("dropbox.com") && !nurl.contains("?dl=")) suffix = "?dl=1";
		nurl = nurl+suffix;
		System.out.println("NURL: "+nurl);
		URL remoteVerURL = new URL(nurl);
		File dlFile = downloadFile("links.yml",remoteVerURL);
		if (dlFile != null) {
			if (dlFile.getCanonicalFile().getName().endsWith(".yml")) {
				
				System.out.println((dlFile == null ? "(NULL_FILE)" : "") +"VERSION FILE URL GOTTEN: "+getLine(dlFile, "version"));
				String version_link = getLine(dlFile, "version");
				URL versionURL = new URL(version_link);
				dlFile.delete();
				dlFile = downloadFile("plugin.yml", versionURL);
			} else System.out.println("ERROR: FILE NOT YML");
		} else System.out.println("ERROR: FILE NOT DOWNLOADED");
		
		this.remoteVersion = getLine(dlFile, "version");
		System.out.println(this.remoteVersion);
		dlFile.delete();
	}
	
	public String getLine(File file, String str) {
		try {
			FileInputStream fis = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.startsWith(str+": ")) {
					br.close();
					fis.close();
					String url = line.substring("version: ".length());
					url = url.endsWith("\"") ? url.substring(0, url.length()-1) : url;
					url = url.startsWith("\"") ? url.substring(1, url.length()) : url;
					return url;
				}
			}
			br.close();	
			fis.close();
			return "line not found";
		} catch (IOException e) {
			e.printStackTrace();
			return "error while reading file";
		}
	}
}
