package org.panopticode.supplement.git;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class GitChurnParser {

	public Map<String, FileInfo> parse(String buffer) {
		try (BufferedReader reader = new BufferedReader(
				new StringReader(buffer))) {
			return parse(reader);
		} catch (IOException ioe) {
			throw new RuntimeException("Failed to read input", ioe);
		}
	}

	public Map<String, FileInfo> parse(File file) {
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			return parse(reader);
		} catch (IOException ioe) {
			throw new RuntimeException("Failed to read input", ioe);
		}
	}

	public Map<String, FileInfo> parse(InputStream is) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(
				is))) {
			return parse(reader);
		} catch (IOException ioe) {
			throw new RuntimeException("Failed to read input", ioe);
		}
	}

	private Map<String, FileInfo> parse(BufferedReader reader)
			throws IOException {
		String line;
		Map<String, FileInfo> filenameVsInfo = new HashMap<>();
		int lineCounter = 0;
		try {
			while ((line = reader.readLine()) != null) {
				lineCounter++;
				if ("".equals(line.trim())) {
					//System.out.println("Skipping empty line");
					continue;
				}
				if (line.matches("^--.*")) {
					//System.out.println("Skipping header");
					continue;
				}
				if (line.matches("^-.*")) {
					processBinaryEntry(line, filenameVsInfo);
					continue;
				}
				processTextEntry(line, filenameVsInfo);
			}
		} catch (NoSuchElementException | NumberFormatException e) {
			throw new GitChurnParseException("Parsing failed at line " + lineCounter, e);
		}
		return filenameVsInfo;
	}

	private void processTextEntry(String line, Map<String, FileInfo> filenameVsInfo) {
		StringTokenizer tokenizer = new StringTokenizer(line);
		int added = Integer.parseInt(tokenizer.nextToken());
		int deleted = Integer.parseInt(tokenizer.nextToken());
		String filePath = tokenizer.nextToken();
		addOrUpdateFileInfo(filenameVsInfo, added, deleted, filePath);
//		System.out.println(String.format("%d added, %d removed, %s", added,
//				deleted, filePath));
	}

	private void processBinaryEntry(String line, Map<String, FileInfo> filenameVsInfo) {
		StringTokenizer tokenizer = new StringTokenizer(line);
		tokenizer.nextToken(); // added dash
		tokenizer.nextToken(); // removed dash
		String filePath = tokenizer.nextToken();
		addOrUpdateFileInfo(filenameVsInfo, 0, 0, filePath);
//		System.out.println(String.format("%s modified", filePath));
	}

	private void addOrUpdateFileInfo(Map<String, FileInfo> filePathVsInfo, int added,
			int deleted, String filePath) {
		filePath = filePath.intern();
		FileInfo fileInfo = filePathVsInfo.get(filePath);
		if(fileInfo==null) {
			fileInfo = new FileInfo(filePath);
			filePathVsInfo.put(filePath, fileInfo);
		}
		fileInfo.addChange(added, deleted);
	}

	public class FileInfo {
		private String filePath;
		private int added;
		private int removed;
		private int numOfChanges;

		public FileInfo(String filePath) {
			this.filePath = filePath;
		}

		void addChange(int added, int removed) {
			this.added += added;
			this.removed += removed;
			this.numOfChanges++;
		}

		public String getFilePath() {
			return filePath;
		}

		public int getAdded() {
			return added;
		}

		public int getRemoved() {
			return removed;
		}

		public int getNumOfChanges() {
			return numOfChanges;
		}

		@Override
		public String toString() {
			return "FileInfo [filePath=" + filePath + ", added=" + added
					+ ", removed=" + removed + ", numOfChanges=" + numOfChanges
					+ "]";
		}

	
	}
}
