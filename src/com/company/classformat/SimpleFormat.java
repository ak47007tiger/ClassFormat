package com.company.classformat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class SimpleFormat {

	public static void main(String[] args) throws IOException {
		SimpleFormat format = new SimpleFormat();
		File filesDir = new File(System.getProperty("user.dir"), "files");
		File classFile = new File(filesDir, "SearchMedActivity.java");
		File outFile = new File(filesDir, "SearchMedActivity_format.java");
		format.format(classFile, "utf-8", style_windows, outFile);
	}

	public static final int style_windows = 0;

	/**
	 * @param classFile
	 * @throws IOException
	 */
	public void format(File classFile, String charSet, int style, File outFile)
			throws IOException {
		FileInputStream ins;
		OutputStream os;
		InputStreamReader isr = null;
		OutputStreamWriter writer = null;
		try {
			ins = new FileInputStream(classFile);
			isr = new InputStreamReader(ins, charSet);
			int size = (int) classFile.length();
			char[] buffer = new char[size];
			isr.read(buffer);
			Format format = new WindowStyleFormat(buffer);
			format.collectV();
			os = new FileOutputStream(outFile);
			writer = new OutputStreamWriter(os, charSet);
			writer.write(format.getOutBuffer());
		} finally {
			if (null != isr) {
				isr.close();
			}
			if (null != writer) {
				writer.close();
			}
		}
	}

	interface Format {
		/**
		 * collect v and move it to top
		 */
		void collectV();

		/**
		 * sort v static is head of member final is head of static
		 */
		void sortV();

		char[] getOutBuffer();
	}

	class WindowStyleFormat implements Format {
		boolean mCollected = false;
		char[] mBufferIn;
		char[] mBufferOut;
		ArrayList<VariableInfo> mVariableInfos = new ArrayList<SimpleFormat.VariableInfo>();

		public WindowStyleFormat(char[] buffer) {
			super();
			this.mBufferIn = buffer;
		}

		@Override
		public void collectV() {
			int bodyStartIndex;
			int bodyEndIndex;
			// find class body start
			int curCharIndex = 0;
			while (mBufferIn[curCharIndex] != '{') {
				curCharIndex++;
			}
			bodyStartIndex = curCharIndex;

			// find class body end
			curCharIndex = mBufferIn.length - 1;
			while (mBufferIn[curCharIndex] != '}') {
				curCharIndex--;
			}
			bodyEndIndex = curCharIndex;

			// find all variables
			ArrayList<VariableInfo> variableInfos = new ArrayList<SimpleFormat.VariableInfo>();
			curCharIndex = bodyStartIndex + 1;
			while (curCharIndex < bodyEndIndex) {
				// maybe it is a start of a variable
				if (isVariableChar(mBufferIn[curCharIndex])) {
					boolean isConfirmVariable = true;
					int variableStartIndex = curCharIndex;
					// find the end of variable
					curCharIndex++;
					while (mBufferIn[curCharIndex] != ';') {
						// it is something like x{x{}} or x{x{}};
						if (mBufferIn[curCharIndex] == '{') {
							curCharIndex++;
							int leftCount = 1;
							// jump something like x{x{}}
							while (leftCount != 0) {
								switch (mBufferIn[curCharIndex]) {
								case '{':
									leftCount++;
									break;
								case '}':
									leftCount--;
									break;
								}
								curCharIndex++;
							}
							// curCharIndex is last index of } + 1
							int remeberIndex = curCharIndex;
							// find next char is not space
							while (' ' == mBufferIn[curCharIndex]) {
								curCharIndex++;
							}
							// it is like x{x{}};
							if (';' == mBufferIn[curCharIndex]) {
								isConfirmVariable = true;
							} else {
								isConfirmVariable = false;
							}
							curCharIndex = remeberIndex;
							// jump the while of find the end of variable
							// to find next variable
							break;
						}
						curCharIndex++;
					}
					if (isConfirmVariable) {
						// find first \r before start
						int enterIndex = variableStartIndex;
						while ('\r' != mBufferIn[enterIndex]) {
							enterIndex--;
						}
						VariableInfo info = new VariableInfo();
						info.start = enterIndex;
						info.end = curCharIndex;
						variableInfos.add(info);
					}
				}
				curCharIndex++;
			}
			mBufferOut = new char[mBufferIn.length];
			System.arraycopy(mBufferIn, 0, mBufferOut, 0, mBufferIn.length);
			for (VariableInfo info : variableInfos) {
				moveChars(mBufferOut, bodyStartIndex + 1, info.start, info.end);
			}
			mCollected = true;
		}

		void moveChars(char[] buffer, int insert, int start, int end) {
			if (end < start) {
				new IllegalArgumentException("start should <= end");
			}

			if (start < insert && insert <= end) {
				new IllegalArgumentException("it is not a move action");
			}
			if (start == insert) {
				return;
			}

			int newEnd = insert + (end - start);
			char[] temp = new char[end + 1 - start];
			// copy origin to temp
			System.arraycopy(buffer, start, temp, 0, temp.length);
			System.out.println(temp);
			int lastMoveIndex;
			int firstMoveIndex;
			int curMoveIndex;
			// insert before origin
			if (newEnd < start) {
				lastMoveIndex = start - 1;
				firstMoveIndex = insert;
				curMoveIndex = lastMoveIndex;
				while (curMoveIndex >= firstMoveIndex) {
					buffer[curMoveIndex + temp.length] = buffer[curMoveIndex];
					curMoveIndex--;
				}
				System.arraycopy(temp, 0, buffer, insert, temp.length);
			}
			// insert after origin
			if (insert > end) {
				lastMoveIndex = insert + temp.length;
				firstMoveIndex = end;
				curMoveIndex = firstMoveIndex;
				while (curMoveIndex <= lastMoveIndex) {
					buffer[curMoveIndex - temp.length] = buffer[curMoveIndex];
					curMoveIndex++;
				}
				System.arraycopy(temp, 0, buffer, insert, temp.length);
			}
		}

		@Override
		public void sortV() {
			if (!mCollected) {
				collectV();
			}

		}

		@Override
		public char[] getOutBuffer() {
			return this.mBufferOut;
		}

	}

	class VariableInfo {
		int start;
		int end;
	}

	public boolean isVariableChar(char c) {
		return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || '_' == c
				|| '@' == c;
	}
}
