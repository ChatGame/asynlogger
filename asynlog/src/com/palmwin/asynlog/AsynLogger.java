package com.palmwin.asynlog;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AsynLogger implements Runnable {

	private static AsynLogger instance;
	private static String path;
	private Pattern logFileNamePattern = Pattern
			.compile("chatgame_(\\d+)\\.log");
	private static String nameFormat = "chatgame_%d.log";
	private SimpleDateFormat dateTimeFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss.SSS:");
	private static int max_size = 20 * 1000;
	private static int max_files = 1;
	private boolean running = true;
	private List<Message> listMessages = new ArrayList<Message>();

	public static AsynLogger getInstance() {
		if (instance != null) {
			return instance;
		}
		synchronized (AsynLogger.class) {
			if (instance != null) {
				return instance;
			}
			instance = new AsynLogger();
			return instance;
		}
	}

	private AsynLogger() {
		new Thread(this).start();
	}

	@Override
	public void run() {
		while (true) {

			if (listMessages.size() == 0) {
				synchronized (this) {
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			if (running) {
				List<Message> messages = new ArrayList<Message>();
				synchronized (listMessages) {
					messages.addAll(listMessages);
				}
				listMessages.clear();
				writeLog(messages);
				messages.clear();
			}else{
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}

	public static void setPath(String path) {
		AsynLogger.path = path;
	}

	public static void setMax_size(int max_size) {
		AsynLogger.max_size = max_size;
	}

	public static void setMax_files(int max_files) {
		AsynLogger.max_files = max_files;
	}

	private File getLogFile() {
		if (path == null) {
			return null;
		}
		File path = new File(AsynLogger.path);
		if (!path.exists()) {
			if (!path.mkdirs()) {
				return null;
			}
		}
		File[] logFiles = path.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String filename) {
				return logFileNamePattern.matcher(filename).matches();
			}
		});
		List<File> listLogFiles = new ArrayList<File>();
		for (File logFile : logFiles) {
			listLogFiles.add(logFile);
		}
		Collections.sort(listLogFiles, new Comparator<File>() {

			@Override
			public int compare(File lhs, File rhs) {
				// Filename chatgame_123.log id=123
				return getFileId(rhs.getName()) - getFileId(lhs.getName());
			}

		});
		ensureLogFileCount(listLogFiles, max_files);
		if (listLogFiles.size() == 0) {
			return new File(path, String.format(nameFormat, 0));
		} else {
			File ret = listLogFiles.get(0);
			if (ret.length() >= max_size) {
				ensureLogFileCount(listLogFiles, max_files - 1);
				return new File(path, String.format(nameFormat,
						getFileId(ret.getName()) + 1));
			} else {
				return ret;
			}
		}
	}

	private void ensureLogFileCount(List<File> files, int count) {
		if (files.size() > count) {
			File oldest = files.get(files.size() - 1);
			System.out.println("delete " + oldest.getName());
			oldest.delete();
		}
	}

	private int getFileId(String fileName) {
		Matcher m = logFileNamePattern.matcher(fileName);
		if (m.matches()) {
			return Integer.parseInt(m.group(1));
		} else {
			System.out.println(fileName + " no match");
			return -1;
		}
	}

	private void writeLog(List<Message> messages) {
		File logFile = getLogFile();
		if (logFile == null) {
			return;
		}
		try {
			FileWriter fw = new FileWriter(logFile, true);
			for (Message msg : messages) {
				fw.write(dateTimeFormat.format(new Date(msg.getTimestamp())));
				fw.write(msg.getContent());
				fw.write("\n");
			}
			fw.close();
		} catch (IOException e) {
			System.out.println("write log error:" + e.getMessage());
			e.printStackTrace();
		}
	}

	public void log(String msg) {
		synchronized (listMessages) {
			listMessages.add(new Message(msg, System.currentTimeMillis()));
			synchronized (this) {
				this.notify();
			}
		}
	}

	public void clearLog() {
		File path = new File(AsynLogger.path);
		if (path.exists()) {
			File[] files = path.listFiles();
			for (File file : files) {
				file.delete();
			}
		}
	}

	public void pause() {
		running = false;
	}

	public void resume() {
		running = true;
		synchronized (this) {
			notify();
		}
	}
}
