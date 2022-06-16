package fr.loferga.champions;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {
	
	public static void main(String[] args) {
		Scanner scn = new Scanner(System.in);
		String cmd = "";
		while (!cmd.equals("stop")) {
			cmd = scn.nextLine();
			while (!interpretor(cmd)) {
				System.out.println("Invalid Command");
				cmd = scn.nextLine();
			}
		}
		scn.close();
	}
	
	private static boolean interpretor(String command) {
		String [] segments = command.split(" ");
		String cmd = segments[0];
		switch (cmd) {
		case "open":
			try {
				Desktop.getDesktop().edit(new File("Champions.txt"));
				System.out.println("Champions.txt opened");
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		case "newtab":
			newtab(buildFromTo(segments, 1, segments.length));
			return true;
		case "add":
			add(buildFromTo(segments, 1, segments.length));
			return true;
		case "purchase":
			purchase(buildFromTo(segments, 1, segments.length));
			return true;
		case "refund":
			refund(buildFromTo(segments, 1, segments.length));
			return true;
		case "check":
			check(buildFromTo(segments, 1, segments.length-1), Integer.valueOf(segments[segments.length-1]));
			return true;
		case "uncheck":
			uncheck(buildFromTo(segments, 1, segments.length-1), Integer.valueOf(segments[segments.length-1]));
			return true;
		case "remove":
			remove(buildFromTo(segments, 1, segments.length));
			return true;
		case "deletetab":
			deletetab(Integer.valueOf(segments[segments.length-1]));
			return true;
		case "next":
			printNext(Integer.valueOf(segments[segments.length-1]));
			return true;
		}
		return false;
	}
	
	private static String buildFromTo(String[] segments, int from, int to) {
		StringBuilder strb = new StringBuilder();
		for (int i = from; i < to; i++) {
			if (i == to-1)
				strb.append(segments[i]);
			else strb.append(segments[i] + " ");
		}
		return strb.toString();
	}
	
	private static void newtab(String entry) {
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(new File("Champions.txt")), StandardCharsets.UTF_8));
			StringBuilder newFileContent = new StringBuilder();
			int tabCount = 0;
			String line = r.readLine();
			while (line.startsWith("[")) {
				newFileContent.append(line + System.lineSeparator());
				line = r.readLine();
				tabCount++;
			}
			newFileContent.append('[' + entry + ']' + System.lineSeparator());
			while (line != null && line != "") {
				String newLine = line.substring(0, tabCount*2);
				if (newLine.charAt(0) == 'O')
					newLine += "  ";
				else
					newLine += "O ";
				newLine += line.substring(tabCount*2);
				newFileContent.append(newLine + System.lineSeparator());
				line = r.readLine();
			}
			r.close();
			FileWriter w = new FileWriter("Champions.txt");
			w.write(newFileContent.toString());
			w.close();
			System.out.println("\"" + entry + "\" tab created");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void add(String entry) {
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(new File("Champions.txt")), StandardCharsets.UTF_8));
			StringBuilder newFileContent = new StringBuilder();
			int tabCount = 0;
			String line = r.readLine();
			while (line != null && (line.startsWith("[") || (line.length()>tabCount*2 && line.substring(tabCount*2).compareTo(entry) < 0))) {
				newFileContent.append(line + System.lineSeparator());
				if (line.startsWith("[")) tabCount++;
				line = r.readLine();
			}
			newFileContent.append("O " + "  ".repeat(tabCount-1) + entry + System.lineSeparator());
			if (line != null) newFileContent.append(line + System.lineSeparator());
			while (r.ready()) {
				line = r.readLine();
				newFileContent.append(line + System.lineSeparator());
			}
			r.close();
			FileWriter w = new FileWriter("Champions.txt");
			w.write(newFileContent.toString());
			w.close();
			System.out.println("\"" + entry + "\" added");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void purchase(String entry) {
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(new File("Champions.txt")), StandardCharsets.UTF_8));
			StringBuilder newFileContent = new StringBuilder();
			int tabCount = 0;
			boolean exist = false;
			boolean owned = true;
			String line = r.readLine();
			while (line != null && line != "") {
				String newLine = line;
				if (line.startsWith("[")) tabCount++;
				else if (line.endsWith(entry)) {
					exist = true;
					if (line.startsWith("O")) {
						owned = false;
						newLine = "X ";
						for (int i = 1; i < tabCount; i++) {
							if (line.charAt(i*2) == ' ')
								newLine += "O ";
							else newLine += line.substring(i*2, i*2+2);
						}
						newLine += line.substring(tabCount*2);
					}
				}
				newFileContent.append(newLine + System.lineSeparator());
				line = r.readLine();
			}
			r.close();
			if (exist) {
				if (!owned) {
					FileWriter w = new FileWriter("Champions.txt");
					w.write(newFileContent.toString());
					w.close();
					System.out.println("\"" + entry + "\" purchased");
				} else System.out.println("\"" + entry + "\" is already owned!");
			} else System.out.println("\"" + entry + "\" doesn't exist, create it by typing \nadd <entryName>");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void refund(String entry) {
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(new File("Champions.txt")), StandardCharsets.UTF_8));
			StringBuilder newFileContent = new StringBuilder();
			int tabCount = 0;
			boolean exist = false;
			boolean owned = false;
			String line = r.readLine();
			while (line != null && line != "") {
				String newLine = line;
				if (line.startsWith("[")) tabCount++;
				else if (line.endsWith(entry)) {
					exist = true;
					if (line.startsWith("X")) {
						owned = true;
						newLine = "O ";
						for (int i = 1; i < tabCount; i++) {
							if (line.charAt(i*2) == 'O')
								newLine += "  ";
							else newLine += line.substring(i*2, i*2+2);
						}
						newLine += line.substring(tabCount*2);
					}
				}
				newFileContent.append(newLine + System.lineSeparator());
				line = r.readLine();
			}
			r.close();
			if (exist) {
				if (owned) {
					FileWriter w = new FileWriter("Champions.txt");
					w.write(newFileContent.toString());
					w.close();
					System.out.println("\"" + entry + "\" refunded");
				} else System.out.println("\"" + entry + "\" is not owned!");
			} else System.out.println("\"" + entry + "\" doesn't exist, create it by typing \nadd <entryName>");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void check(String entry, int tab) {
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(new File("Champions.txt")), StandardCharsets.UTF_8));
			StringBuilder newFileContent = new StringBuilder();
			int tabCount = 0;
			String tabName = null;
			boolean exist = false;
			boolean owned = false;
			boolean checked = true;
			String line = r.readLine();
			while (line != null && line != "") {
				String newLine = line;
				if (line.startsWith("[")) {
					if (tabCount == tab)
						tabName = line;
					tabCount ++;
				}
				else if (line.endsWith(entry)) {
					exist = true;
					if (line.startsWith("X")) {
						owned = true;
						newLine = "X ";
						for (int i = 1; i < tabCount; i++) {
							if (i == tab) {
								if (line.charAt(i*2) == 'O') {
									checked = false;
									newLine += "X ";
								}
							} else newLine += line.substring(i*2, i*2+2);
						}
						newLine += line.substring(tabCount*2);
					}
				}
				newFileContent.append(newLine + System.lineSeparator());
				line = r.readLine();
			}
			r.close();
			if (exist) {
				if (owned) {
					if (!checked) {
						FileWriter w = new FileWriter("Champions.txt");
						w.write(newFileContent.toString());
						w.close();
						System.out.println("\"" + entry + "\" checked in " + tabName);
					} else System.out.println("\"" + entry + "\" is already checked!");
				} else System.out.println("\"" + entry + "\" is not owned!");
			} else System.out.println("\"" + entry + "\" doesn't exist, create it by typing \nadd <entryName>");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void uncheck(String entry, int tab) {
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(new File("Champions.txt")), StandardCharsets.UTF_8));
			StringBuilder newFileContent = new StringBuilder();
			int tabCount = 0;
			String tabName = null;
			boolean exist = false;
			boolean owned = false;
			boolean checked = false;
			String line = r.readLine();
			while (line != null && line != "") {
				String newLine = line;
				if (line.startsWith("[")) {
					if (tabCount == tab)
						tabName = line;
					tabCount ++;
				}
				else if (line.endsWith(entry)) {
					exist = true;
					if (line.startsWith("X")) {
						owned = true;
						newLine = "X ";
						for (int i = 1; i < tabCount; i++) {
							if (i == tab) {
								if (line.charAt(i*2) == 'X') {
									checked = true;
									newLine += "O ";
								}
							} else newLine += line.substring(i*2, i*2+2);
						}
						newLine += line.substring(tabCount*2);
					}
				}
				newFileContent.append(newLine + System.lineSeparator());
				line = r.readLine();
			}
			r.close();
			if (exist) {
				if (owned) {
					if (checked) {
						FileWriter w = new FileWriter("Champions.txt");
						w.write(newFileContent.toString());
						w.close();
						System.out.println("\"" + entry + "\" unchecked in " + tabName);
					} else System.out.println("\"" + entry + "\" is already checked!");
				} else System.out.println("\"" + entry + "\" is not owned!");
			} else System.out.println("\"" + entry + "\" doesn't exist, create it by typing \nadd <entryName>");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void remove(String entry) {
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(new File("Champions.txt")), StandardCharsets.UTF_8));
			StringBuilder newFileContent = new StringBuilder();
			boolean exist = false;
			String line = r.readLine();
			while (r.ready()) {
				if (line.endsWith(entry)) exist = true;
				else newFileContent.append(line + System.lineSeparator());
				line = r.readLine();
			}
			if (line.endsWith(entry)) exist = true;
			else newFileContent.append(line + System.lineSeparator());
			r.close();
			if (exist) {
				FileWriter w = new FileWriter("Champions.txt");
				w.write(newFileContent.toString());
				w.close();
				System.out.println("\"" + entry + "\" removed");
			} else System.out.println("\"" + entry + "\" doesn't exist, create it by typing \nadd <entryName>");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void deletetab(int tab) {
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(new File("Champions.txt")), StandardCharsets.UTF_8));
			StringBuilder newFileContent = new StringBuilder();
			String tabName = null;
			int tabCount = 0;
			boolean exist = false;
			String line = r.readLine();
			while (line.startsWith("[")) {
				if (tabCount == tab) {
					exist = true;
					tabName = line;
				}
				else newFileContent.append(line + System.lineSeparator());
				tabCount ++;
				line = r.readLine();
			}
			while (line != null && line != "") {
				String newLine = line.substring(0, tab*2);
				newLine += line.substring(tab*2+2);
				newFileContent.append(newLine + System.lineSeparator());
				line = r.readLine();
			}
			r.close();
			if (exist) {
				FileWriter w = new FileWriter("Champions.txt");
				w.write(newFileContent.toString());
				w.close();
				System.out.println("\"" + tabName + "\" tab deleted");
			} else System.out.println("tab n°" + tab + " doesn't exist, there is only " + tabCount + "tabs, create it by typing \nnewtab <tabName>");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void printNext(int tab) {
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(new File("Champions.txt")), StandardCharsets.UTF_8));
			boolean exist = false;
			String next = null;
			int tabCount = 0;
			String tabName = null;
			String line = r.readLine();
			while (line != null && line != "" && next == null) {
				if (line.startsWith("[")) {
					if (tabCount == tab) {
						exist = true;
						tabName = line;
					}
					tabCount++;
				}
				else if (line.charAt(tab*2) == 'O')
					next = line.substring(tabCount*2);
				line = r.readLine();
			}
			r.close();
			if (exist) {
				if (next != null)
					System.out.println("next Champion in " + tabName + " is \"" + next + "\"");
				else System.out.println("all champions are checked in " + tabName + "!");
			} else System.out.println("tab n°" + tab + " doesn't exist, there is only " + tabCount + "tabs, create it by typing \nnewtab <tabName>");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}