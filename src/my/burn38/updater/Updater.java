package my.burn38.updater;

import java.net.URL;
import java.nio.file.Path;

public class Updater {

	private Updater instance = null;
	private String localVersion = null, remoteVersion = null;
	private boolean caseSensitive = false;
	private URL remoteVersionURL = null, remoteUpdateURL = null;
	private Path localUpdatePath = null;
	
	public Updater(String localVersion, boolean caseSensitive, URL remoteVersionURL, URL remoteUpdateURL, Path localUpdatePath) {
		this.instance = this;
		this.localVersion = localVersion;
		this.caseSensitive = caseSensitive;
		this.remoteVersionURL = remoteVersionURL;
		this.remoteUpdateURL = remoteUpdateURL;
		this.localUpdatePath = localUpdatePath;
	}
	
	public String getLocalVersion() {
		return localVersion;
	}
	public String getRemoteVersion() {
		return remoteVersion;
	}
	public URL getRemoteVersionURL() {
		return remoteVersionURL;
	}
	public URL getRemoteUpdateURL() {
		return remoteUpdateURL;
	}
	public boolean isCaseSensitive() {
		return caseSensitive;
	}
	public Path getLocalUpdatePath() {
		return localUpdatePath;
	}
	public Updater getInstance() {
		return instance;
	}
	
	public void fetchRemoteVersion() {
		
	}
	
}
