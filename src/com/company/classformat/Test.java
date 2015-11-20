package com.company.classformat;

public class Test {

	public static void main(String[] args) {
		char[] buffer = new char[]{'a','b','c','d'};
		System.out.println(new String(buffer,1,2));
		char[] temp = new char[3];
		System.arraycopy(buffer, 1, temp, 0, 3);
		System.out.println(temp);
	}
	public void showCharLength(){
		System.out.println((int)Character.MAX_VALUE);
		System.out.println(Byte.SIZE);
		System.out.println(Character.SIZE);
	}
	public void showCharVal(){
		System.out.println((int)'a');
		System.out.println((int)'z');
		System.out.println((int)'A');
		System.out.println((int)'Z');
		System.out.println((int)'_');
	}
}
