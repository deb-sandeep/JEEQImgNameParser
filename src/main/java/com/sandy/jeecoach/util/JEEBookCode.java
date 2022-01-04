package com.sandy.jeecoach.util;

import java.util.Arrays ;
import java.util.List ;

public class JEEBookCode {

    public static final String PEARSON_IIT_FOUNDATION = "PF" ;
    public static final String MTG_REASONING          = "MR" ;
    
    public static final List<String> BOOK_CD_LIST = Arrays.asList( 
        PEARSON_IIT_FOUNDATION, 
        MTG_REASONING 
    ) ;
    
    public static boolean isValidBookCode( String bookCode ) {
        return BOOK_CD_LIST.contains( bookCode ) ;
    }
}
