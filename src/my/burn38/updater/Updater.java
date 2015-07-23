package my.burn38.updater;

import java.net.URL;
import java.nio.file.Path;

public class Updater {

	private Updater instance = null;
	private String currentVersion = null;
	private boolean caseSensitive = false;
	private URL versionFile = null, fileToDownload = null;
	private Path whereToCopy = null;
	
	public Updater(String currentVersion, boolean caseSensitive, URL versionFile, URL fileToDownload, Path whereToCopyUpdate) {
		this.instance = this;
		this.currentVersion = currentVersion;
		this.caseSensitive = caseSensitive;
		this.versionFile = versionFile;
		this.fileToDownload = fileToDownload;
		this.whereToCopy = whereToCopyUpdate;
	}
	
	public String getCurrentVersion() {
		return currentVersion;
	}
	public boolean isCaseSensitive() {
		return caseSensitive;
	}
	public URL getVersionFileURL() {
		return versionFile;
	}
	public URL getFileToDownloadURL() {
		return fileToDownload;
	}
	public Path getWhereToCopyUpdatePath() {
		return whereToCopy;
	}
	public Updater getInstance() {
		return instance;
	}
	
}
