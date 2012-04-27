package jaseimov.client.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;

/**
 *
 * @author santi
 */
public class FileFunctions {

 private File ProgramHomeFolder;
 private File ProgramFolder;
 private File ProgramPresetsFolder;

 private Class cl;

    public void setClass(Class c){cl=c;}

    public void ScanFolder(){
        Collection<File> all = new ArrayList<File>();
        addFilesRecursively(new File("."), all);
        System.out.println("[FileFunctions]:"+all);
    }

    private void addFilesRecursively(File file, Collection<File> all) {
        final File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                all.add(child);
                addFilesRecursively(child, all);
            }
        }
    }

    public String CurDir(){
        String currentDir = new File(".").getAbsolutePath();
        return currentDir;
    }

    public String HomeDir(){
        return System.getProperty("user.home");
    }

    public String ProgramHomeDir(){
        return HomeDir()+"/jaseimov";
    }

    public String ProgramCurvesDir(){
        return ProgramHomeDir()+"/curves";
    }

    public String ProgramPresetsDir(){
        return ProgramHomeDir()+"/presets";
    }


    public String ProgramModelsPresetsDir(){
        System.out.println("[FileFunctions]:"+ProgramPresetsDir()+"/models");
        return ProgramPresetsDir()+"/models";
    }

    public String ProgramJoystickPresetsDir(){
        System.out.println("[FileFunctions]:"+ProgramPresetsDir()+"/joystick");
        return ProgramPresetsDir()+"/joystick";
    }

    public String GetParent(String path, String separator){
        int i=path.lastIndexOf(separator);
        char p2[] = null;
        return path.substring(0, i);
    }

    public String AppDir(){
        //gets the executable folder (has to be initialized with a class)
        // see the MainFrame source code
        URL p=getUrlOfClass(cl);

        String p1=p.getPath();
        
        for(int i=0;i<5;i++){
            p1=GetParent(p1,"/");
            //System.out.println(p1);
        }
        
        ProgramFolder=new File(p1);
        return p1;
    }


    public boolean DirExists(String file){
        File f=new File(file);
        return f.isDirectory();
    }

    public boolean FileExists(String file){
        File f=new File(file);
        return f.isFile();
    }

    public void CheckJaseimovFolder(){
        // checks if the /home/user/.jaseimov folder exists, if not create it
        ProgramFolder=new File(AppDir()+"/jaseimov_settings"); //source folder
        ProgramPresetsFolder=new File(ProgramHomeDir()); //target folder

        if (ProgramPresetsFolder.isDirectory()==false){
            try {
                ProgramPresetsFolder.mkdirs();
                System.out.println("[FileFunctions]:Program preset folder"+ProgramPresetsFolder.getAbsolutePath());
                System.out.println("[FileFunctions]:Program folder"+ProgramFolder.getAbsolutePath());
                copyDirectory(ProgramFolder,ProgramPresetsFolder);
                System.out.println("[FileFunctions]:Directory created: "+ProgramPresetsFolder.getAbsolutePath());
            } catch (IOException ex) {
                Logger.getLogger(FileFunctions.class.getName()).log(Level.SEVERE, null, ex);
            }
            

        }else{
            System.out.println("[FileFunctions]:Directory found: "+ProgramPresetsFolder.getAbsolutePath());
            String [] filenames=ProgramPresetsFolder.list();
            if (filenames.length>0){
                for (int i=0;i<=filenames.length-1;i++){
                    System.out.println("[FileFunctions]:Content:"+filenames[i]);
                }
            }else{
                try {
                copyDirectory(ProgramFolder,ProgramPresetsFolder);
                } catch (IOException ex) {
                    System.out.println("[FileFunctions]:Impossible to copy: "+
                            ProgramFolder.getAbsolutePath() +
                            " to: "+ProgramPresetsFolder);
                }
            }
        }

        
    }

    public void PrintFile(String path, ArrayList<String> content){

        File f= new File(path);
        try {
            Writer output = new BufferedWriter(new FileWriter(f));

            for (int i=0;i< content.size();i++){
                output.write(content.get(i).toString());
            }

        } catch (IOException ex) {
            Logger.getLogger(FileFunctions.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void CreatePresetDefaults(){

        ArrayList<String> srv = new ArrayList<String>();
        ArrayList<String> as1 = new ArrayList<String>();
        ArrayList<String> ps2 = new ArrayList<String>();

        //server test file
        srv.add("name=test server");
        srv.add("ip=127.0.0.1");
        srv.add("port=1099");
        srv.add("# tics in the encoder wheel");
        srv.add("ticsinthewheel=12");
        srv.add("# convertion value for the acceleration");
        srv.add("acc=0.01 ");
        PrintFile(ProgramFolder+"/ServerTest.aseimov",srv);

        //Aseimov1 file
    }

    public ArrayList<String> OpenFile(String filename){
        /*Read a file and returnan array with the content*/
        String inpt = null;
        int c=0;
        ArrayList<String> arr = new ArrayList<String>();

        //if(filename.exists()==false){return null;}

        //System.out.println("Opening file:" + filename);
        try { //try to read the file
	   FileReader fr = new FileReader(filename);
           BufferedReader br = new BufferedReader(fr);
           inpt = new String();
           while ((inpt = br.readLine()) != null) {
              arr.add(inpt);
              c++;
           }
        } catch (IOException e) {// catch possible io errors from readLine()
           System.out.println("[FileFunctions]:IOException error.");
        }

        return arr;
    }

    public void SaveFile(ArrayList<String> content, File file){
        System.out.println("[FileFunctions]:size:"+content.size());
        try {
            if (content.size()>0){
                BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsolutePath()));

                for (int i=0;i<content.size();i++){
                    bw.write(content.get(i).toCharArray());
                    bw.newLine();
                }
                
                bw.close();
            }

        } catch (IOException ex) {
            Logger.getLogger(FileFunctions.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public URL getUrlOfClass(Class c) {
        if(c==null) {
            throw new NullPointerException();
        }

        String className = c.getName();
        String resourceName = className.replace('.', '/') + ".class";
        ClassLoader classLoader = c.getClassLoader();

        if(classLoader==null) {
            classLoader = ClassLoader.getSystemClassLoader();
        }

        URL url = classLoader.getResource(resourceName);

        return url;
  }

    public void copyDirectory(File sourceLocation , File targetLocation) throws IOException {

        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }

            String[] children = sourceLocation.list();
            for (int i=0; i<children.length; i++) {
                copyDirectory(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {

            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
    }


    public File OpenDialog(String title){
        final JFileChooser fc = new JFileChooser();
        File file=null;

        fc.setName(title);
        fc.setDialogTitle(title);
        fc.setFileHidingEnabled(true);
        int result=fc.showOpenDialog(fc);
        file=fc.getSelectedFile();

        // Determine which button was clicked to close the dialog

            if (result == JFileChooser.APPROVE_OPTION){
            // Approve (Open or Save) was clicked
                return file;
            }

        return null;
    }
    
 public double[][] convertStreamToVec(InputStream is)  { 
        /* 
        * To convert the InputStream to String we use the 
        * Reader.read(char[] buffer) method. We iterate until the 
        * Reader return -1 which means there's no more data to 
        * read. We use the StringWriter class to produce the string. 
        */
        if (is != null) { 
            Writer writer = new StringWriter(); 
            
            try {                               
                try { // get the stream                
                    int n;                    
                    char[] buffer = new char[1024]; 
                    Reader reader; 
                    reader = new BufferedReader( 
                    new InputStreamReader(is, "UTF-8"));
                    while ((n = reader.read(buffer)) != -1) { 
                        writer.write(buffer, 0, n); 
                    }
                } catch (IOException ex) {
                    Logger.getLogger(FileFunctions.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            } finally { 
                try { // close the stream
                    is.close();
                } catch (IOException ex) {
                    Logger.getLogger(FileFunctions.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            // convert string to a vector
            String s= writer.toString(); 
            String[] arr=s.split(System.getProperty("line.separator"));
            String[] ln = new String[2];
            double[][] vec= new double[arr.length][2];
            
            for(int i=0;i<arr.length;i++){
                ln= arr[i].split(",");
                vec[i][0]=Double.parseDouble(ln[0]);
                vec[i][1]=Double.parseDouble(ln[1]);
                //System.out.println(i+": " + vec[i][0] + ", " +vec[i][1]);
            }
            
            return vec;
        } else {         
            return null; 
        } 
    }     
    
    public String[] convertStreamToString(InputStream is)  { 
        /* 
        * To convert the InputStream to String we use the 
        * Reader.read(char[] buffer) method. We iterate until the 
        * Reader return -1 which means there's no more data to 
        * read. We use the StringWriter class to produce the string. 
        */
        if (is != null) { 
            Writer writer = new StringWriter(); 
            
            try {                               
                try { // get the stream                
                    int n;                    
                    char[] buffer = new char[1024]; 
                    Reader reader; 
                    reader = new BufferedReader( 
                    new InputStreamReader(is, "UTF-8"));
                    while ((n = reader.read(buffer)) != -1) { 
                        writer.write(buffer, 0, n); 
                    }
                } catch (IOException ex) {
                    Logger.getLogger(FileFunctions.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            } finally { 
                try { // close the stream
                    is.close();
                } catch (IOException ex) {
                    Logger.getLogger(FileFunctions.class.getName()).log(Level.SEVERE, null, ex);
                }
            } 
            
            // convert the string into an array
            String s= writer.toString(); 
            return s.split(System.getProperty("line.separator"));
            
        } else {         
            return null; 
        } 
    } 

}
