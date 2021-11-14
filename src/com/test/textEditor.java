package com.test;

import javax.swing.*;
import javax.swing.undo.*;
import java.applet.Applet;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.desktop.AppEvent;
import java.awt.event.*;
import java.io.*;
import java.util.Hashtable;

class undoAbleTextArea extends TextArea implements StateEditable
    {
        private final static String KEY_STATE = "UndoableTextAreaKey";
        private boolean textChanged = false;
        private UndoManager undoManager;
        private StateEdit currentEdit;

        public undoAbleTextArea()
            {
                super();
                initUndoable();
            }
        public undoAbleTextArea(String str)
            {
                super(str);
                initUndoable();
            }
        public undoAbleTextArea(int rows, int columns)
            {
                super(rows,columns);
                initUndoable();
            }
        public undoAbleTextArea(String str, int rows, int columns)
            {
                super(str,rows,columns);
                initUndoable();
            }
        public undoAbleTextArea(String str, int rows, int columns, int scrollBars)
            {
                super(str,rows,columns,scrollBars);
                initUndoable();
            }
        public boolean undo()
            {
                try{
                    undoManager.undo();
                    return true;
                }
                catch(CannotUndoException e){
                    System.out.println("Error : cannot undo at the moment!!");
                    return false;
                }
            }
        public boolean redo()
        {
            try{
                undoManager.redo();
                return true;
            }
            catch(CannotRedoException e){
                System.out.println("Error : cannot redo the previous task!!");
                return false;
            }
        }

        public void storeState(Hashtable state)
            {
                state.put(KEY_STATE, getText());
            }

        public void restoreState(Hashtable state)
            {
                Object data = state.get(KEY_STATE);
                if(data!=null)
                    setText((String)data);
            }

        private void takeSnapshot()
            {
                if(textChanged)
                {
                    currentEdit.end();
                    undoManager.addEdit(currentEdit);
                    textChanged = false;
                    currentEdit = new StateEdit(this);
                }
            }
        private void initUndoable()
        {
            undoManager = new UndoManager();
            currentEdit = new StateEdit(this);
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if(e.isActionKey())
                        takeSnapshot();
                }
            });

            addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent fe) {
                    takeSnapshot();
                }
            });

            addTextListener(new TextListener() {
                @Override
                public void textValueChanged(TextEvent e) {
                    textChanged = true;
                    takeSnapshot();
                }
            });
        }
    }

    public class textEditor extends JFrame
    {
        boolean b = true;
        JFrame fm;
         FileDialog fd;
         Font f;
         int style = Font.PLAIN;
         int fSize = 16;
         undoAbleTextArea txt;
         String fileName, st, fn = "untitled", dn;
         Clipboard clip = getToolkit().getSystemClipboard();
         textEditor()
         {
             f = new Font("Calibri",style,fSize);
             setLayout(new GridLayout(1,1));
             txt = new undoAbleTextArea(80,25);

             txt.setFont(f);
             add(txt);
             MenuBar mb = new MenuBar();

             Menu fontMenu = new Menu("Font");
             MenuItem bold = new MenuItem("Bold", new MenuShortcut(KeyEvent.VK_B));
             MenuItem plain = new MenuItem("Plain", new MenuShortcut(KeyEvent.VK_P));
             MenuItem italic = new MenuItem("Italic", new MenuShortcut(KeyEvent.VK_I));

             fontMenu.add(bold);
             fontMenu.add(plain);
             fontMenu.add(italic);

             bold.addActionListener(new FM());
             plain.addActionListener(new FM());
             italic.addActionListener(new FM());

             Menu size = new Menu("Size");
             MenuItem s1,s2,s3,s4,s5,s6,s7,s8,s9,s10;

             s1 = new MenuItem("10");
             s2 = new MenuItem("12");
             s3 = new MenuItem("14");
             s4 = new MenuItem("16");
             s5 = new MenuItem("18");
             s6 = new MenuItem("20");
             s7 = new MenuItem("22");
             s8 = new MenuItem("24");
             s9 = new MenuItem("26");
             s10 = new MenuItem("28");

             size.add(s1);
             size.add(s2);
             size.add(s3);
             size.add(s4);
             size.add(s5);
             size.add(s6);
             size.add(s7);
             size.add(s8);
             size.add(s9);
             size.add(s10);

            s1.addActionListener(new Size());
            s2.addActionListener(new Size());
            s3.addActionListener(new Size());
            s4.addActionListener(new Size());
            s5.addActionListener(new Size());
            s6.addActionListener(new Size());
            s7.addActionListener(new Size());
            s8.addActionListener(new Size());
            s9.addActionListener(new Size());
            s10.addActionListener(new Size());

            size.addActionListener(new FM());
            fontMenu.add(size);

            Menu file = new Menu("File");
            MenuItem n = new MenuItem("New", new MenuShortcut(KeyEvent.VK_N));
            MenuItem o = new MenuItem("Open", new MenuShortcut(KeyEvent.VK_O));
            MenuItem s = new MenuItem("Save", new MenuShortcut(KeyEvent.VK_S));
            MenuItem e = new MenuItem("Exit", new MenuShortcut(KeyEvent.VK_W));

            n.addActionListener(new New());
            file.add(n);
            o.addActionListener(new Open());
            file.add(o);
            s.addActionListener(new Save());
            file.add(s);
            e.addActionListener(new Exit());
            file.add(e);
            addWindowListener(new Win());

            Menu edit = new Menu("Edit");
            MenuItem cut = new MenuItem("Cut", new MenuShortcut(KeyEvent.VK_X));
            MenuItem copy = new MenuItem("Copy", new MenuShortcut(KeyEvent.VK_C));
            MenuItem paste = new MenuItem("Paste", new MenuShortcut(KeyEvent.VK_V));
            cut.addActionListener(new Cut());
            edit.add(cut);
            copy.addActionListener(new Copy());
            edit.add(copy);
            paste.addActionListener(new Paste());
            edit.add(paste);

            Menu color = new Menu("Color");
            MenuItem bg = new MenuItem("BackGround");
            MenuItem fg = new MenuItem("ForeGround");
            bg.addActionListener(new BC());
            color.add(bg);
            fg.addActionListener(new BC());
            color.add(fg);



            mb.add(file);
            mb.add(edit);
            mb.add(fontMenu);
            mb.add(color);

            setMenuBar(mb);

            mylistener mylist = new mylistener();
            addWindowListener(mylist);
         }

         class mylistener extends WindowAdapter
         {
             public void windowClosing(WindowEvent we)
             {
                 if(!b)
                     System.exit(0);
             }
         }
         class New implements ActionListener
         {
             public void actionPerformed(ActionEvent ae)
             {
                 txt.setText(" ");
                 setTitle("Untitled* - MyPAD");
                 fn = "Untitled";
             }
         }
         class Open implements ActionListener
         {
             public void actionPerformed(ActionEvent ae)
             {
                 FileDialog fd = new FileDialog(textEditor.this, "Select");
                 fd.show();
                 if((fn=fd.getFile())!=null)
                 {
                     fileName = fd.getDirectory()+fd.getFile();
                     dn = fd.getDirectory();
                     setTitle(fileName);
                     readFile();
                 }
                 txt.requestFocus();
             }
         }
         class Save implements ActionListener
         {
             public void actionPerformed(ActionEvent ae)
             {
                 FileDialog fd = new FileDialog(textEditor.this, "Save File");
                 fd.setFile(fn);
                 fd.setDirectory(dn);
                 fd.show();

                 if(fd.getFile()!=null)
                 {
                     fileName = fd.getDirectory()+fd.getFile();
                     setTitle(fileName);
                     writeFile();
                     txt.requestFocus();
                 }
             }
         }
         class Exit implements ActionListener
         {
             public void actionPerformed(ActionEvent ae)
             {
                 System.exit(0);
             }
         }
         void readFile()
         {
             BufferedReader d;
             StringBuffer sb = new StringBuffer();
             try{
                 d = new BufferedReader(new FileReader(fileName));
                 String line;
                 while((line=d.readLine())!=null)
                     sb.append(line+"\n");
                 txt.setText(sb.toString());
                 d.close();
             }
             catch(FileNotFoundException e) {
                 System.out.println("Error : File not found");
             }
             catch(IOException e){ }
         }
         public void writeFile()
         {
             try{
                 DataOutputStream d = new DataOutputStream(new FileOutputStream(fileName));
                 String line = txt.getText();
                 BufferedReader br = new BufferedReader(new StringReader(line));
                 while((line=br.readLine())!=null)
                     d.writeBytes(line+"\n");
                 d.close();
             }
             catch (Exception e){
                 System.out.println("Error : File not found");
             }
         }
         class Cut implements ActionListener
         {
             public void actionPerformed(ActionEvent ae)
             {
                 String sel = txt.getSelectedText();
                 StringSelection ss = new StringSelection(sel);
                 clip.setContents(ss,ss);
                 txt.replaceRange("",txt.getSelectionStart(),txt.getSelectionEnd());
             }
         }
         class Copy implements ActionListener
         {
             public void actionPerformed(ActionEvent ae)
             {
                 String sel = txt.getSelectedText();
                 StringSelection ss = new StringSelection(sel);
                 clip.setContents(ss,ss);
             }
         }
         class Paste implements ActionListener
         {
             public void actionPerformed(ActionEvent ae)
             {
                 Transferable cliptran = clip.getContents(textEditor.this);
                 try
                 {
                     String sel = (String)cliptran.getTransferData(DataFlavor.stringFlavor);

                     txt.replaceRange(sel,txt.getSelectionStart(),txt.getSelectionEnd());
                 }
                 catch(Exception e){
                     System.out.println("Not starting Flavour");
                 }
             }
         }
         class  Win extends WindowAdapter
         {
            public void windowClosing(WindowEvent we)
            {
                if(!b)
                    System.exit(0);
            }
         }
         class Size implements ActionListener
         {
             public void actionPerformed(ActionEvent ae)
             {
                 int style = f.getStyle();
                 String w = ae.getActionCommand();
                 if(w=="10")
                 {
                     f = new Font("Calibri",style,16);
                     txt.setFont(f);
                     fSize = f.getSize();
                     repaint();
                 }
                 if(w=="12")
                 {
                     f = new Font("Calibri",style,18);
                     txt.setFont(f);
                     fSize = f.getSize();
                     repaint();
                 }
                 if(w=="14")
                 {
                     f = new Font("Calibri",style,20);
                     txt.setFont(f);
                     fSize = f.getSize();
                     repaint();
                 }
                 if(w=="16")
                 {
                     f = new Font("Calibri",style,22);
                     txt.setFont(f);
                     fSize = f.getSize();
                     repaint();
                 }
                 if(w=="18")
                 {
                     f = new Font("Calibri",style,24);
                     txt.setFont(f);
                     fSize = f.getSize();
                     repaint();
                 }
                 if(w=="20")
                 {
                     f = new Font("Calibri",style,26);
                     txt.setFont(f);
                     fSize = f.getSize();
                     repaint();
                 }
                 if(w=="22")
                 {
                     f = new Font("Calibri",style,28);
                     txt.setFont(f);
                     fSize = f.getSize();
                     repaint();
                 }
                 if(w=="24")
                 {
                     f = new Font("Calibri",style,30);
                     txt.setFont(f);
                     fSize = f.getSize();
                     repaint();
                 }
                 if(w=="26")
                 {
                     f = new Font("Calibri",style,32);
                     txt.setFont(f);
                     fSize = f.getSize();
                     repaint();
                 }
                 if(w=="28")
                 {
                     f = new Font("Calibri",style,34);
                     txt.setFont(f);
                     fSize = f.getSize();
                     repaint();
                 }
             }
         }

         class FM extends Applet implements ActionListener
         {
             public void actionPerformed(ActionEvent ae)
             {
                 String b = ae.getActionCommand();
                 if(b=="Bold")
                 {
                     f = new Font("Calibri",Font.BOLD,fSize);
                     style = f.getStyle();
                     txt.setFont(f);
                 }
                 if(b=="Plain")
                 {
                     f = new Font("Calibri",Font.PLAIN,fSize);
                     style = f.getStyle();
                     txt.setFont(f);
                 }
                 if(b=="Italic")
                 {
                     f = new Font("Calibri",Font.ITALIC,fSize);
                     style = f.getStyle();
                     txt.setFont(f);
                 }
                 repaint();
             }
         }

         class BC implements ActionListener
         {
             public void actionPerformed(ActionEvent ae)
             {
                 st = ae.getActionCommand();
                 JFrame jf = new JFrame("JColorChooser");
                 colorChooser c = new colorChooser();
                 c.setBounds(300,150,500,300);
                 c.setVisible(true);
             }
         }
         class colorChooser extends JFrame
         {
             Button ok;
             JColorChooser jcc;
             public colorChooser(){
                 setTitle("JColorChooser");
                 jcc = new JColorChooser();
                 JPanel content = (JPanel)getContentPane();
                 content.setLayout(new BorderLayout());
                 content.add(jcc,"Center");
                 ok = new Button("Ok");
                 content.add(ok,"South");
                 ok.addActionListener(new B());
             }
             class B implements ActionListener
             {
                 @Override
                 public void actionPerformed(ActionEvent e)
                 {
                    System.out.println("Color Chosen Is :"+jcc.getColor().toString());
                    if(st.equals("BackGround"))
                        txt.setBackground(jcc.getColor());
                    if(st.equals("ForeGround"))
                        txt.setForeground(jcc.getColor());
                    setVisible(false);
                 }
             }
         }

         public static void main(String args[])
         {
             JFrame fm = new textEditor();
             fm.setBounds(250,50,1200,950);
             fm.setTitle("Untitled* - MyPAD");
             fm.setVisible(true);
             fm.show();
         }
    }