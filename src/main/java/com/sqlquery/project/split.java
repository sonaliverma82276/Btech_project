package com.sqlquery.project;

import java.util.*;

import org.apache.commons.lang3.tuple.Pair;


public class split {
    public static int j=0;
    public static String main(String[] args,String curquery){

        curquery=ProjectApplication.makeSingleLine(curquery);
        curquery=curquery.toLowerCase();

        // handle concat negative case
        if(ProjectApplication.find(curquery,"concat",0)!=-1) return "null";

        Vector<String[]> arr_alias=simplifyjoin.main(null,curquery,"[\\w]+ AS+ ([a-zA-Z]+)"," "); 
        
        Map<String,String> map_alias=new HashMap<String,String>(); 
        Map<String,String> map_alias_rev=new HashMap<String,String>(); 
        Map<String,Vector<String[]>> map_select_col=new HashMap<String,Vector<String[]>>();
        int cur_a=ProjectApplication.find(curquery,"from ",0);
        String main_table_name=ProjectApplication.getnextword(curquery, cur_a).getKey().split(" ")[0];
        
        for (String[] b : arr_alias) {
            map_alias.put(b[0],b[2]);
            map_alias_rev.put(b[2], b[0]);
            map_select_col.put(b[0],simplifyjoin.main(null,curquery.substring(0,ProjectApplication.find(curquery,"from",0)),b[2]+"\\.+([\\w|\\*]+)"," "));

        }

        int join_word=ProjectApplication.find(curquery, "join", 0);

        int group_pos=ProjectApplication.find(curquery, "group", 0);
        int order_pos=ProjectApplication.find(curquery, "order", 0);
        int where_pos=Math.min(Math.min(group_pos==-1?curquery.length():group_pos, order_pos==-1?curquery.length():order_pos), ProjectApplication.find(curquery, "where", 0));
        if(where_pos==-1) where_pos=curquery.length();
        int from_pos = ProjectApplication.find(curquery," from ",0);

        // negative case - mixed query
        if( join_word!=-1 && curquery.substring(from_pos, join_word).indexOf(",", 0)!=-1) return "null";

        Vector<String[]> conditions;
        Vector<String[]> attributes = simplifyjoin.main(null,curquery.substring(0,from_pos),"[\\w]+(\\.)[\\w]+"," ");
        if(join_word!=-1)
        conditions=simplifyjoin.main(null,curquery.substring(0, where_pos),"[\\w]+.[\\w]+(\\s*)=(\\s*)[\\w]+.[\\w]+","=");
        else
        conditions=simplifyjoin.main(null,curquery.substring(0, curquery.length()),"[\\w]+.[\\w]+(\\s*)=(\\s*)[\\w]+.[\\w]+","=");
        Vector<String> tables= new Vector<>();
        
        for(String[] c:conditions){
            String[] s1 = c[0].split("\\.");
            String[] s2 = c[1].split("\\.");
            s2[0]=s2[0].trim();
            if(!tables.contains(s1[0])){
                tables.add(s1[0]);
                }
            if(!tables.contains(s2[0])){
                tables.add(s2[0]);
                }
        }
        for(String[] s:attributes){
            String[] split = s[0].split("\\.");
            if(!tables.contains(split[0])){
            tables.add(split[0]);
            }
        }

        if(tables.size()<3) {
            int pos=j;
            j++; 
            return "CREATE TEMPORARY TABLE temp_table"+pos+" AS "+curquery+";\n";
        } 

        Boolean has_alias = false;
        if(!tables.contains(main_table_name)){
             has_alias=true;
        }
        Map<String,String> table= new HashMap<String,String>();

        
        if(has_alias){
        for (String a : tables) { 
            String keyword= " "+a+" ";
            int alias_pos = ProjectApplication.find(curquery,keyword,0); 
            if(alias_pos==-1)
             {
                keyword=" "+a+",";
                alias_pos = ProjectApplication.find(curquery,keyword,0);
             }
            alias_pos-=keyword.length();     
            int start_pos=-1;
            int end_pos=alias_pos-2;
            while(curquery.charAt(end_pos)!=' '){
                end_pos--;
            }
            start_pos=end_pos;
            String table_name = curquery.substring(start_pos+1,alias_pos);
            table_name=table_name.trim();
            alias_pos=end_pos;

            end_pos--;
            String temp = "as";

            if(table_name.equals(temp)){
                while(curquery.charAt(end_pos)!=' '){
                    end_pos--;
                }
                start_pos=end_pos;
                table_name = curquery.substring(start_pos+1,alias_pos);
            }

            table_name=table_name.trim();
            table.put(table_name,table_name);
            map_alias.put(table_name,a);
            map_alias_rev.put(a, table_name);
            map_select_col.put(table_name,simplifyjoin.main(null,curquery.substring(0,ProjectApplication.find(curquery,"from",0)),a+"\\.+([\\w|\\*]+)"," "));
        }
    }
    else{
        for(String a:tables){
        table.put(a,a);
        map_alias.put(a,a);
        map_alias_rev.put(a,a);
        map_select_col.put(a,simplifyjoin.main(null,curquery.substring(0,ProjectApplication.find(curquery,"from",0)),a+"\\.+([\\w|\\*]+)"," "));
        }
    }  

        String join_query_part;
        Vector<String> single_join=new Vector<>();
        if(join_word!=-1) {

            // negative case for multiple conditions in one join 
            if(conditions.size()>simplifyjoin.main(null,curquery.substring(0,where_pos),"join","null").size()) return "null";
           
            if(has_alias){
            join_query_part=curquery.substring(ProjectApplication.find(curquery," "+map_alias.get(main_table_name)+" ",0));
            }
            else
            join_query_part=curquery.substring(ProjectApplication.find(curquery," "+main_table_name+" ",0));
            int start=0;

            for(String[] a:conditions){
                int end=ProjectApplication.find(join_query_part,a[1],start); 
                single_join.add(join_query_part.substring(start,end));
                start=end+1;
            }
        } 
        else {
            conditions=simplifyjoin.main(null,curquery.substring(where_pos),"[\\w]+.[\\w]+(\\s*)=(\\s*)[\\w]+.[\\w]+","=");
            Set<Pair<String, String>> alias_pair_set = new HashSet<Pair<String, String>>();
           
            for(String[] a:conditions){ 
                String b=a[0],c=a[1];
                if(ProjectApplication.find(a[1],map_alias.get(main_table_name)+".",0)==-1){ b=a[1]; c=a[0];} 
                String cur_alias=b.substring(0,b.indexOf('.'));
                String cur_alias_c=c.substring(0,c.indexOf('.'));
                String cur_table=map_alias_rev.get(cur_alias);
                String cur_table_c=map_alias_rev.get(cur_alias_c);
                if(has_alias) {
                    // negative case for multiple conditions in one join (in without join word query)
                    if(alias_pair_set.contains(Pair.of(cur_alias,cur_alias_c))||alias_pair_set.contains(Pair.of(cur_alias_c,cur_alias))) return "null";
               
                    single_join.add("join "+cur_table+" as "+cur_alias+" on "+b+"="+c);
                    alias_pair_set.add(Pair.of(cur_alias,cur_alias_c));
                }
                else {
                    // negative case for multiple conditions in one join without alias (in without join word query)
                    if(alias_pair_set.contains(Pair.of(cur_table,cur_table_c))||alias_pair_set.contains(Pair.of(cur_table_c,cur_table))) return "null";
               
                    single_join.add("join "+cur_table+" on "+b+"="+c);
                    alias_pair_set.add(Pair.of(cur_table,cur_table_c));
                }
            }
        } 

        for(String[] d: arr_alias){
            table.put(d[0],d[0]);
        }

        Vector<String> split_query = new Vector<String>();
        int index=0;
        String result="";
        for(String join:single_join){ 
            String join_keywords = "Select ";

            String[] c = conditions.get(index);
            String first=c[0];
            String second=c[1];

            String[] s1 = c[0].split("\\.");

            String[] s2 = c[1].split("\\.");

            String t1 = s1[0];
            String t2 = s2[0];

            t2=t2.trim(); t1=t1.trim();

            String[] b=conditions.get(index);

            String cur=map_alias.get(table.get(map_alias_rev.get(t1)));
            while(!(cur+"."+s1[1]).equals(b[0])) {
                b[0]=cur+"."+s1[1];
                cur=map_alias.get(table.get(map_alias_rev.get(cur)));
            }
            cur=map_alias.get(table.get(map_alias_rev.get(t2)));
            while(!(cur+"."+s2[1]).equals(b[1])) {
                b[1]=cur+"."+s2[1];
                cur=map_alias.get(table.get(map_alias_rev.get(cur)));
            }
                conditions.set(index,b);

                String[] d = conditions.get(index);
                String[] split1 = d[0].split("\\.");
    
                String[] split2 = d[1].split("\\.");
    
                String table_alias1 = split1[0];
                String table_alias2 = split2[0];
                table_alias2=table_alias2.trim();

            String table_name1 =table.get(map_alias_rev.get(table_alias1)); 
            String table_name2 =table.get(map_alias_rev.get(table_alias2)); 


            for(String[] attr:map_select_col.get(table_name1)){ 
            join_keywords+=attr[0];
            join_keywords+=",";
            }

            for(String[] attr:map_select_col.get(table_name2)){ 
            join_keywords+=attr[0];
            join_keywords+=",";
            }
            join_keywords=join_keywords.substring(0,join_keywords.length()-1);
            join_keywords+=(" into temp_table"+j);
            join_keywords+=" from";
            join_keywords+=" ";

            if(join.contains(" "+table_name1+" ")){
                if(has_alias){
                join_keywords+=table_name2;
                join_keywords+=" as ";
                join_keywords+=map_alias.get(table_name2);
                }else{
                join_keywords+=table_name2;  
                }
                }else{
                if(has_alias){
                join_keywords+=table_name1;
                join_keywords+=" as ";
                join_keywords+=map_alias.get(table_name1);
                }
                else{
                    join_keywords+=table_name1;  
                }
                }
                join_keywords+=" ";
                
                join = join.replace(first, map_alias.get(table.get(map_alias_rev.get(table_alias1)))+"."+split1[1]);
                join = join.replace(second, map_alias.get(table.get(map_alias_rev.get(table_alias2)))+"."+split2[1]);
                join_keywords+=(join+";<br><br>");
                split_query.add(join_keywords);
                map_alias.put("temp_table"+j,"t"+j);
                map_alias_rev.put("t"+j,"temp_table"+j);
                result+=join_keywords;

                table.put(table_name1,"temp_table"+j);
                table.put(table_name2,"temp_table"+j);
                table.put("temp_table"+j,"temp_table"+j);

                Vector<String[]> attribute1=new Vector<>();
                Vector<String[]> attribute=new Vector<>();
                for(String[] attr:map_select_col.get(table_name1)){ 
                    String[] split_old=attr[0].split("\\."); 
                    attr[0]= map_alias.get(table.get(table_name1))+"."+split_old[1];
                    attribute1.add(attr);
                    attribute.add(attr);
                }
                map_select_col.replace(table_name1,attribute1);

                Vector<String[]> attribute2=new Vector<>();
                for(String[] attr:map_select_col.get(table_name2)){ 
                    String[] split_old=attr[0].split("\\."); 
                    attr[0]= map_alias.get(table.get(table_name2))+"."+split_old[1]; 
                    attribute2.add(attr);
                    attribute.add(attr);
                }
                map_select_col.replace(table_name2,attribute2);
                map_select_col.put("temp_table"+j,attribute);
            j++; index++;

        }

        // add rest of the query
        if(where_pos!=curquery.length() && join_word!=-1) {
            result+= "Select temp_table"+(j-1)+".* from temp_table"+(j-1)+" where "+curquery.substring(where_pos,curquery.length());
            Vector<String[]> where_conditions=simplifyjoin.main(null,curquery.substring(where_pos, curquery.length()),"\\w+\\.[\\w]+(\\s*)","null");
            
            for(String[] d:where_conditions){  
                for(String b:d){
                    String[] split = b.split("\\.");
                    String table_alias = split[0].trim();
                    String table_name =map_alias_rev.get(table_alias); 
                    String cur=map_alias.get(table.get(map_alias_rev.get(table_alias)));

                    while(!(cur).equals(table_name)) {
                        table_name=cur;
                        cur=map_alias.get(table.get(map_alias_rev.get(cur)));
                    }
                    result = result.replace(b, table_name+"."+split[1]);
                }
               
            }
        }

        return result+"<br>";
    }
}
