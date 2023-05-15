package com.sqlquery.project;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder; 
import org.w3c.dom.Document; 
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File; 
import java.util.*;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Controller
public class homepage {
    

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/fileupload")
    public String fileuploadform() {
        return "fileuploadform";
    }

    @PostMapping("/")
    public String result(@RequestParam(name="query") String query, Model model) {
        String finalres="<p style='color:slategray; ' ><b> Your Query is :</b></p><p style='word-wrap: break-word;'>";
       
            if(ProjectApplication.is_join(query)==1) { 
                query=query.toLowerCase();
                split.j=0;
                finalres+=query+"</p><br> <p style='color:slategray;' ><b> Simplified Query:</b></p> <p style='word-wrap: break-word;' > ";
               
                Pair<Integer, Integer> paren_pos = ProjectApplication.solve_parenthesis(query);

                String result="";
                while(paren_pos!=null) {
                    String subquery=query.substring(paren_pos.getKey()+1,paren_pos.getValue());
                    String cur_res=split.main(null, subquery);

                    if(cur_res.equals("null")) { 
                        model.addAttribute("query", "<p style='color:red'>This Query cannot be processed for now!</p><br>");
                        return "home";
                    }
                    result+=cur_res+" <br> ";
                    int pos=split.j-1;
                    //for an alias of parenthesis
                    String alias_paren=ProjectApplication.getNextWord(query, paren_pos.getValue());
                    if( alias_paren!="" && !sql_keyword.is_keyword(alias_paren)) {
                        finalres="<p style='color:red'>This Query cannot be processed for now!</p><br>";
                        model.addAttribute("query", finalres);
                        return "home";
                    }
                    query=query.replace("("+subquery+")", "temp_table"+pos);
                    paren_pos=ProjectApplication.solve_parenthesis(query);
                } 
                String cur_res=split.main(null, query);
                if(cur_res.equals("null")) { 
                    finalres="<p style='color:red'>This Query cannot be processed for now!</p><br>";
                    model.addAttribute("query", finalres);
                    return "home";
                }
                result+=cur_res;
                finalres+=result+"</p>";
               
            }
            else finalres="<p style='color:slategray;' > Given query is not a join query</p>";
        model.addAttribute("query", finalres);
        return "home";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) throws IOException, ParserConfigurationException, SAXException {

        // try (ZipInputStream zipInputStream = new ZipInputStream(file.getInputStream())) {
        //     ZipEntry entry;
        //     while ((entry = zipInputStream.getNextEntry()) != null) {
        //         // Extract only directories
        //         if (entry.isDirectory()) {
        //             String directoryName = entry.getName();
        //             System.out.println("Extracted directory: " + directoryName);
        //             // Perform further processing on the directory as needed
        //         }
        //     }
        // }

        mergeXML merge=new mergeXML();
        mergeXML.main(null,zipextractor.extractDirectoryFromZip(file));
        File cur_file = new File( "D:\\Project_sqlquery\\project\\src\\main\\java\\com\\sqlquery\\project\\dbinstances.xml"); 
			DocumentBuilderFactory dbf 
				= DocumentBuilderFactory.newInstance(); 
			
			DocumentBuilder db = dbf.newDocumentBuilder(); 
			Document doc = db.parse(cur_file); 

			doc.getDocumentElement().normalize(); 

            NodeList list=doc.getElementsByTagName("select");

            System.out.println("List of Join queries : \n"); 
            int cur_cnt=0;
            String finalres="";
           for(int i=0;i<list.getLength();i++){
            String query=(list.item(i).getTextContent()).toLowerCase();
            String curres="";
            int count=1;
            if(ProjectApplication.is_join(query)==1) { 
                split.j=0;
                cur_cnt++;
                System.out.println(cur_cnt+")\n"+query+"\n-------------------------------------------\n Simplified Query\n-------------------------------------------\n");
                    finalres+="<p style='color:slategray; ' ><b>" +" Query is :</b></p><p style='word-wrap: break-word;'>";
       
                    if(ProjectApplication.is_join(query)==1) {  
                        count++;
                        query=query.toLowerCase();
                        split.j=0;
                        curres=query+"</p><br> <p style='color:slategray;' ><b> Simplified Query:</b></p> <p style='word-wrap: break-word;' > ";
                        
                        Pair<Integer, Integer> paren_pos = ProjectApplication.solve_parenthesis(query);

                        String result="";
                        while(paren_pos!=null) {
                            String subquery=query.substring(paren_pos.getKey()+1,paren_pos.getValue());
                            String cur_res=split.main(null, subquery);

                            if(cur_res.equals("null")) { 
                                curres="<p style='color:red'>This Query cannot be processed for now!</p><br>";
                                // model.addAttribute("query", "<p style='color:red'>This Query cannot be processed for now!</p><br>");
                                // return "home";
                            } 
                            result+=cur_res+" <br> ";
                            int pos=split.j-1;
                            //for an alias of parenthesis
                            String alias_paren=ProjectApplication.getNextWord(query, paren_pos.getValue());
                            if( alias_paren!="" && !sql_keyword.is_keyword(alias_paren)) {
                                curres="<p style='color:red'>This Query cannot be processed for now!</p><br>";
                                // model.addAttribute("query", "<p style='color:red'>This Query cannot be processed for now!</p><br>");
                                // return "home";
                            }
                            query=query.replace("("+subquery+")", "temp_table"+pos);
                            paren_pos=ProjectApplication.solve_parenthesis(query);
                        } 
                        String cur_res=split.main(null, query);
                        if(cur_res.equals("null")) { 
                            curres="<p style='color:red'>This Query cannot be processed for now!</p><br>";
                                // model.addAttribute("query", "<p style='color:red'>This Query cannot be processed for now!</p><br>");
                                // return "home";
                        }
                        
                        result+=cur_res;
                        curres+=result+"</p>";
                        
                    }
                    else curres="<p style='color:slategray;' > This query is not a join query</p>";
                    finalres+=curres;
            }
        }

        // Save the uploaded zip file to disk
        // File tempFile = File.createTempFile("temp", ".zip");
        // FileOutputStream fos = new FileOutputStream(tempFile);
        // fos.write(file.getBytes());
        // fos.close();

        // // Unzip the file and read each entry
        // FileInputStream fis = new FileInputStream(tempFile);
        // ZipInputStream zis = new ZipInputStream(fis);

        // ZipEntry entry;
        // while ((entry = zis.getNextEntry()) != null) {
        //     // Read the file contents
        //     ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //     byte[] buffer = new byte[1024];
        //     int count;
        //     while ((count = zis.read(buffer)) != -1) {
        //         baos.write(buffer, 0, count);
        //     }

        //     // Print the contents of the file
        //     String contents = baos.toString();
        //     // System.out.println(contents);

        //     // Close the ByteArrayOutputStream
        //     baos.close();
        // }

        // // Close the ZipInputStream and FileInputStream
        // zis.close();
        // fis.close();

        // // Delete the temporary file
        // tempFile.delete();
        model.addAttribute("query", finalres);
        return "fileuploadform";
    }

}
