package com.sqlquery.project;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class homepage {
    

    @GetMapping("/")
    public String home() {
        return "home";
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

}
