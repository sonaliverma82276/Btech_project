package com.sqlquery.project;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;

class simplifyjoin {

  public static Vector<String[]> main(String[] args,String query,String regex,String sp) {
    final String queryString = query; 
    return deriveAlias(queryString,regex,sp);
  }

  private static Vector<String[]> deriveAlias(String queryString,String regex,String sp) {
    String query = queryString.replace('\n', ' ');
    final Pattern pattern = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
    final Matcher matcher = pattern.matcher(query);
    Vector<String[]> arr_alias=new Vector<>();
    while (matcher.find()) {
        String alias=query.substring(matcher.start(),matcher.end());
        if(sp.equals("null")) {
          String[] cur={alias};
          arr_alias.add(cur);
        } 
        else
        arr_alias.add(alias.split(sp));
    }
    
    return arr_alias;
  }
}
