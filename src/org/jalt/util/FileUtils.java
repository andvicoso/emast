package org.jalt.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jalt.infra.log.Log;
import org.jalt.model.model.ERG;
import org.jalt.model.problem.Problem;
import org.jalt.util.grid.ERGGridModelReader;
import org.jalt.util.grid.GridModelWriter;

/**
 * 
 * @author andvicoso
 */
public class FileUtils {
	private static final ERGGridModelReader reader = new ERGGridModelReader();

	public static File getLastModified(String path, Long lastModified) {
		return getLastModified(path, "", lastModified);
	}

	public static File getFromPreffix(String path, int preffix) {
		File root = new File(path);
		File[] list = root.listFiles();
		File file = null;

		if (list != null) {
			for (File f : list) {
				if (f.isDirectory()) {
					File inner = getFromPreffix(f.getAbsolutePath(), preffix);
					if (inner != null) {
						file = inner;
						break;
					}
				} else if (f.getName().startsWith(preffix + "")) {
					file = f;
				}
			}
		}

		return file;
	}

	public static File getLastModified(String path, String extension, Long lastModified) {
		File root = new File(path);
		File[] list = root.listFiles();
		File last = null;

		if (list != null) {
			for (File f : list) {
				if (f.isDirectory()) {
					last = getLastModified(f.getAbsolutePath(), lastModified);
				} else if (f.lastModified() > lastModified
						&& (extension.isEmpty() || f.getName().endsWith(extension))) {
					last = f;
					lastModified = last.lastModified();
				}
			}
		}

		return last;
	}

	public static boolean toObjectFile(Problem<?> pProblem, String pFilename, boolean pUnique) {
		boolean ret = true;
		String path = pFilename;
		String filename = getFilename(path);
		String dirPath = getDir(path);
		OutputStream fos = null;
		File dir = new File(dirPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		File file;
		do {
			file = new File(path);
			int uniqueId = Math.abs((int) System.currentTimeMillis()) % 100;
			path = dirPath + File.separator + uniqueId + "_" + filename;
		} while (pUnique && file.exists());

		try {
			fos = new BufferedOutputStream(new FileOutputStream(file));
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(pProblem);
			Log.info("Problem saved to file: " + file);
			oos.close();
		} catch (IOException ex) {
			ret = false;
			ex.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException ex) {
				ret = false;
				ex.printStackTrace();
			}
		}

		return ret;
	}

	public static Problem<?> fromObjectFile(String pFilename) {
		InputStream fos = null;
		try {
			fos = new BufferedInputStream(new FileInputStream(pFilename));
			ObjectInputStream oos = new ObjectInputStream(fos);
			return (Problem<?>) oos.readObject();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		return null;
	}

	public static String toFile(Problem<?> pProblem, String pFilename, boolean pUnique) {
		String path = pFilename;
		String filename = getFilename(path);
		String dirPath = getDir(path);
		File dir = new File(dirPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		File file;
		do {
			file = new File(path);
			int uniqueId = Math.abs((int) System.currentTimeMillis()) % 1000;
			path = dirPath + File.separator + uniqueId + "_" + filename;
		} while (pUnique && file.exists());

		try {
			final GridModelWriter writer = new GridModelWriter(path, pProblem);
			writer.write();
			Log.info("Problem saved to file: " + path);
		} catch (IOException ex) {
			path = null;
			ex.printStackTrace();
		}

		return path;
	}

	public static Problem<ERG> fromFile(String pFilename) {
		try {
			return reader.read(pFilename);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	private static String getDir(String filename) {
		String dirPath = "";
		int lastSep = filename.lastIndexOf(File.separator);
		if (lastSep > 0) {
			dirPath = filename.substring(0, lastSep);
		}
		return dirPath;
	}

	private static String getFilename(String path) {
		String filename = "";
		int lastSep = path.lastIndexOf(File.separator);
		if (lastSep > 0) {
			filename = path.substring(lastSep + 1);
		}
		return filename;
	}

	public static List<File> getAllFromDir(String path) {
		List<File> files;
		File root = new File(path);
		if (root.isDirectory()) {
			files = Arrays.asList(root.listFiles());
		} else {
			files = new ArrayList<File>();
		}

		return files;
	}
}
