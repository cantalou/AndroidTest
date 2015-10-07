package com.wy.test.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;

import android.content.Context;
import android.content.res.AssetManager;

public class FileUtil {
	/**
	 * 从assets目录复制文件到指定路径
	 *
	 * @param context
	 * @param srcFileName
	 *            复制的文件名
	 * @param targetDir
	 *            目标目录
	 * @param targetFileName
	 *            目标文件名
	 * @return
	 */
	public static boolean copyAssetsFile(Context context, String srcFileName, String targetDir, String targetFileName) {
		AssetManager asm = context.getAssets();
		FileOutputStream fos = null;
		DataInputStream dis = null;
		try {
			dis = new DataInputStream(asm.open(srcFileName));
			createDir(targetDir);
			File targetFile = new File(targetDir, targetFileName);
			if (targetFile.exists()) {
				targetFile.delete();
			}

			fos = new FileOutputStream(targetFile);
			byte[] buffer = new byte[8 *1024];
			int len = 0;
			while ((len = dis.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
				fos.flush();
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
				if (dis != null) {
					dis.close();
				}
			} catch (Exception e2) {
			}
		}

		return false;
	}

	/**
	 * 创建文件夹
	 */
	public static String createDir(String dir) {
		File f = new File(dir);
		if (!f.exists()) {
			f.mkdirs();
		}

		return dir;
	}
}
