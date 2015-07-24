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
	private String appName = null;
	private boolean hasUpdated = false;
	private boolean isUpdated = false;
	
	public Updater(String localVersion, boolean caseSensitive, URL remoteUpdateURL, File localUpdateFile){
		System.out.println("Creating new updater, please wait...");
		this.instance = this;
		this.localVersion = localVersion;
		this.caseSensitive = caseSensitive;
		this.remoteUpdateURL = remoteUpdateURL;
		this.localUpdateFile = localUpdateFile;
		System.out.println("New updater was created: ");
		String remoteVersion = "`NULL`"; try {fetchRemoteVersion();remoteVersion=this.remoteVersion;} catch (IOException ex){remoteVersion = "VERSION_NOT_FOUND";ex.printStackTrace();};
		System.out.println("VERSION: "+localVersion+" -> "+remoteVersion);
		System.out.println("FILE: "+remoteUpdateURL.toString()+" -> "+localUpdateFile.getPath());
		appName = "["+appName+"] ";
		if (shouldUpdate(remoteVersion)) {
			System.out.println(appName+" updating to "+remoteVersion);
			update();
		} else {
			System.out.println(appName+" is up to date");
			isUpdated = true;
		}
	}
	
	public boolean hasUpdated() {
		return hasUpdated;
	}
	public boolean isUptoDate() {
		return isUpdated;
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
				String update_link = getLine(dlFile, "update");
				this.remoteUpdateURL = new URL(update_link);
				URL versionURL = new URL(version_link);
				dlFile.delete();
				dlFile = downloadFile("plugin.yml", versionURL);
			} else System.out.println("ERROR: FILE NOT YML");
		} else System.out.println("ERROR: FILE NOT DOWNLOADED");
		
		this.remoteVersion = getLine(dlFile, "version");
		this.appName = getLine(dlFile, "name");
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
					String url = line.substring(str.length()+": ".length());
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
	
	public String getHigherVersion(String oldVersion, String newVersion) {
		String v_old = oldVersion; String v_new = newVersion;
		String letters = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVv";
		for (int i = 0; i < letters.length(); i++) {
			if (oldVersion.contains(Character.toString(letters.charAt(i)))) oldVersion.replaceAll(Character.toString(letters.charAt(i)), Integer.toString(i));
			if (newVersion.contains(Character.toString(letters.charAt(i)))) newVersion.replaceAll(Character.toString(letters.charAt(i)), Integer.toString(i));
		}
		double oldV=1.0D, newV=0.0D;
		try {
			oldV= Double.parseDouble(oldVersion);
			newV= Double.parseDouble(newVersion);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (oldV > newV || oldV == newV) return v_old;
		else if (oldV < newV) return v_new;
		else return v_old;
	}
	public boolean shouldUpdate(String newVersion) {
		if (getHigherVersion(this.localVersion, newVersion) == newVersion) return true;
		else return false;
	}
	public void update() {
		if (this.localUpdateFile.isDirectory()) this.localUpdateFile = new File(this.localUpdateFile, (this.appName+".jar").replaceAll("[", "").replaceAll("]", ""));
		File updateVer = downloadFile(this.localUpdateFile.getName(), this.remoteUpdateURL);
		try {
			Files.move(updateVer.toPath(), this.localUpdateFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			hasUpdated = true;
			System.out.println(appName+" updated from "+localVersion+" to "+remoteVersion);
		} catch (IOException e) {
			System.out.println(appName+" Update downloaded but couldn't be installed, trying to move it to: /manual_update/"+appName.replaceAll("[", "").replaceAll("]", "")+".jar");
			try {
				Files.move(updateVer.toPath(), new File(this.localUpdateFile.toPath()+"/manual_update/"+(appName.replaceAll("[", "").replaceAll("]", ""))+".jar").toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException ex) {
				System.out.println(appName+" Update couldn't be moved... aborting update, printing stacktrace:");
				ex.printStackTrace();
			}
		}
	}
}
