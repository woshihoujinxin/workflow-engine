package com.workflow.engine.core.common.utils;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class OCRUtil {
	private final static String LANG_OPTION = "-l";
	private final static String EOL = System.getProperty("line.separator");

	/**
	 * @param imageFile
	 *            传入的图像文件
	 * @param imageFormat
	 *            传入的图像格式
	 * @return 识别后的字符串
	 */
	public static String recognizeText(File imageFile) throws Exception {
		/**
		 * 设置输出文件的保存的文件目录
		 */
		File outputFile = new File(imageFile.getParentFile(), "output");
		StringBuffer strB = new StringBuffer();
		List<String> cmd = new ArrayList<String>();
		
		if(OSUtil.isLinux()){
			cmd.add("tesseract");
		}
		if(OSUtil.isWindows()){
			cmd.add(ConfigUtils.getStringByKey("ocrPath"));
		}
		
		cmd.add("");
		cmd.add(outputFile.getName());
		cmd.add(LANG_OPTION);
		// cmd.add("chi_sim");
		cmd.add("eng");

		ProcessBuilder pb = new ProcessBuilder();
		/**
		 * Sets this process builder's working directory.
		 */
		pb.directory(imageFile.getParentFile());
		cmd.set(1, imageFile.getPath());
		pb.command(cmd);
		pb.redirectErrorStream(true);
		Process process = pb.start();
		// tesseract.exe 1.jpg 1 -l chi_sim
		// Runtime.getRuntime().exec("tesseract.exe 1.jpg 1 -l chi_sim");
		/**
		 * the exit value of the process. By convention, 0 indicates normal
		 * termination.
		 */
		int w = process.waitFor();
		if (w == 0) // 0代表正常退出
		{
			BufferedReader in = new BufferedReader(
					new InputStreamReader(new FileInputStream(outputFile.getAbsolutePath() + ".txt"), "UTF-8"));
			String str;

			while ((str = in.readLine()) != null) {
				strB.append(str).append(EOL);
			}
			in.close();
		} else {
			String msg;
			switch (w) {
			case 1:
				msg = "Errors accessing files. There may be spaces in your image's filename.";
				break;
			case 29:
				msg = "Cannot recognize the image or its selected region.";
				break;
			case 31:
				msg = "Unsupported image format.";
				break;
			default:
				msg = "解析失敗";
			}
			FileUtils.moveFileToDirectory(imageFile, new File(ConfigUtils.getStringByKey("captchaDirError")), true);
			throw new RuntimeException(msg);
		}
		new File(outputFile.getAbsolutePath() + ".txt").delete();
		return strB.toString().replaceAll("[^0-9a-zA-Z]", "");
	}

	public static void main(String[] args) {
		try {
			// System.out.println(recognizeText(new File("D:\\code.jpg")));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
