package asmcup.sandbox;

import java.awt.Font;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.text.PlainDocument;

import asmcup.compiler.Compiler;

public class CodeEditor extends JFrame {
	protected final Sandbox sandbox;
	protected JEditorPane editor;
	protected Menu menu;
	protected byte[] ram = new byte[256];
	protected File currentFile;
	
	public CodeEditor(Sandbox sandbox) {
		this.sandbox = sandbox;
		this.editor = new JEditorPane();
		this.menu = new Menu();
		
		setTitle("Code Editor");
		setSize(400, 400);
		setContentPane(new JScrollPane(editor));
		setJMenuBar(menu);
		
		new DefaultContextMenu().add(editor);
		editor.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		editor.getDocument().putProperty(PlainDocument.tabSizeAttribute, 2);
	}
	
	public void openFile() {
		if (currentFile == null) {
			currentFile = findFileOpen();
		}
		
		try {
			String text = Utils.readAsString(currentFile);
			
			if (text != null) {
				editor.setText(text);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void closeFile() {
		editor.setText("");
	}
	
	public void closeEditor() {
		setVisible(false);
	}
	
	public boolean compile() {
		try {
			Compiler compiler = new Compiler();
			ram = compiler.compile(editor.getText());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	public void flash() {
		synchronized (sandbox.getWorld()) {
			sandbox.getRobot().flash(ram);
		}
	}
	
	public void compileAndFlash() {
		if (compile()) {
			flash();
		}
	}
	
	public void checkSyntax() {
		
	}
	
	public File findFileSave() {
		return Utils.findFileSave(sandbox.getFrame(), "asm", "Source File");
	}
	
	public File findFileOpen() {
		return Utils.findFileOpen(sandbox.getFrame(), "asm", "Source File");
	}
	
	public void saveFile() {
		if (currentFile == null) {
			currentFile = findFileSave();
		}
		
		save(currentFile);
	}
	
	public void save(File file) {
		if (file == null) {
			return;
		}
		
		try {
			Utils.write(file, editor.getText());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveFileAs() {
		save(findFileSave());
	}
	
	public void saveROM() {
		
	}
	
	protected class Menu extends JMenuBar {
		public Menu() {
			addFileMenu();
			addCompileMenu();
		}
		
		protected AbstractAction item(String label, ActionListener f) {
			return new AbstractAction(label) {
				public void actionPerformed(ActionEvent e) {
					f.actionPerformed(e);
				}
			};
		}
		
		protected void addFileMenu() {
			JMenu menu = new JMenu("File");
			menu.add(item("New Code", (e) -> { closeFile(); }));
			menu.add(item("Open Code...", (e) -> { openFile(); }));
			menu.addSeparator();
			menu.add(item("Save Code", (e) -> { saveFile(); }));
			menu.add(item("Save Code As...", (e) -> { saveFileAs(); }));
			menu.add(item("Save ROM", (e) -> { saveROM(); }));
			menu.addSeparator();
			menu.add(item("Close Editor", (e) -> { closeEditor(); }));
			add(menu);
		}
		
		protected void addCompileMenu() {
			JMenu menu = new JMenu("Tools");
			menu.add(item("Compile & Flash", (e) -> { compileAndFlash(); }));
			menu.add(item("Compile", (e) -> { compile(); }));
			menu.add(item("Flash", (e) -> { flash(); }));
			menu.addSeparator();
			menu.add(item("Check Syntax", (e) -> { checkSyntax(); }));
			add(menu);
		}
	}
}