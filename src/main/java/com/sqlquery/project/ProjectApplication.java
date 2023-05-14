package com.sqlquery.project;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;

@SpringBootApplication
public class ProjectApplication {

	public static int find(String query,String word,int start){
        query=query.toLowerCase();
        query.replaceAll("( )+", " ");
        word=word.toLowerCase();
        int pos=query.indexOf(word, start);
        if(pos==-1) return -1;

        return pos+word.length();
    }
  
    public static String makeSingleLine(String str) {
        String modifiedStr = "";
        int i = 0;
        String curline = "";
        while (i < str.length()) {
            while (i < str.length() && str.charAt(i) != '\n') {
                curline += str.charAt(i);
                i++;
            }
            i++; 
            modifiedStr += curline.trim()+" ";
            curline = ""; 
        }
        return modifiedStr.trim(); 
    }

    public static String getNextWord(String str, int startPos) {
        while(startPos<str.length()&&str.charAt(startPos)==' ') startPos++;
        int nextSpace = str.indexOf(' ', startPos);
        if (nextSpace == -1) {
            return "";
        }
        String nextWord = str.substring(startPos, nextSpace);
        return nextWord;
    }

    public static Pair<String, Integer> getnextword(String query, int pos) {
        int n=query.length();
        String ans="";
        while(pos<n&&query.charAt(pos)==' ') pos++;
        int ok=0;
        while(pos<n){
            char a=query.charAt(pos);
            if(a==' '){
            ok++;
            }
            else if(a==',')
            {
                ans+=query.substring(pos, pos+1);
                return Pair.of(ans,pos);
            } else if(ok>3 && ans.contains(" as "))  
             return Pair.of(ans,pos);
             else if(ok>2&&!ans.contains(" as "))
             return Pair.of(ans,pos);
            ans+=query.substring(pos, pos+1);
            pos++;
        }
        return Pair.of(ans,pos);
    }

    public static int is_join(String query){
        if(find(query,"join",0)!=-1) return 1;

        Set<String> table_set = new HashSet<String>();
        int from_pos=find(query,"from",0);
        while(from_pos!=-1){
            Pair<String,Integer> tables=getnextword(query,from_pos);
            if(tables.getKey().indexOf(',')!=-1) return 1;

            table_set.add(tables.getKey());
            from_pos=find(query,"from",from_pos);
        }

        return table_set.size()>1?1:0;
    }

    public static Pair<Integer,Integer> solve_parenthesis(String s){
        s=s.toLowerCase();
        Stack<Integer> stack = new Stack<>();
        int t=0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                int cur=i;
                while(s.charAt(cur+1)==' ') cur++;
                if(!s.substring(cur+1,cur+8).equals("select ")) {
                    t++;
                    continue;
                }
                stack.push(i);
            } else if (s.charAt(i) == ')') {
                if(t>0) {
                    t--; continue;
                }
                return Pair.of(stack.pop(),i);
            }
        }
        return null;
    }
	public static void main(String[] args) {
		SpringApplication.run(ProjectApplication.class, args);
	}

}
