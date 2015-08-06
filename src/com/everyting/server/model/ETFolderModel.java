package com.everyting.server.model;

import java.io.InputStream;
import java.io.Serializable;

public class ETFolderModel implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String parent;
	private ETFolderModel parentFolder;
	private InputStream fileContent;
	private boolean isDirectory;
	private String path;

	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public ETFolderModel getParentFolder() {
		return parentFolder;
	}
	public void setParentFolder(ETFolderModel parentFolder) {
		this.parentFolder = parentFolder;
	}
	public String getUID() {
		return id;
	}
	public void setUID(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isDirectory() {
		return isDirectory;
	}
	public void setDirectory(boolean isDirectory) {
		this.isDirectory = isDirectory;
	}
	public InputStream getFileContent() {
		return fileContent;
	}
	public void setFileContent(InputStream fileContent) {
		this.fileContent = fileContent;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
}
