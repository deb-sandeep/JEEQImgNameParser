package com.sandy.jeecoach.util;

public abstract class QID {
    
    protected String[] qIdParts = null ;
    
    protected QID( String[] parts ) {
        this.qIdParts = parts ;
    }

    public abstract void parseQID() ;
    
    public String getQRefPart() {
        
        StringBuilder sb = new StringBuilder() ;
        for( String part : qIdParts ) {
            sb.append( part + "/" ) ;
        }
        sb.deleteCharAt( sb.length()-1 ) ;
        
        return sb.toString() ;
    }
}
