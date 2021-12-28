package com.sandy.jeecoach.util ;

import static com.sandy.jeecoach.util.JEEBookCode.PEARSON_IIT_FOUNDATION ;

import java.util.Arrays ;

import lombok.Data ;

// [P|M|C]_[6-12]_[bookId]_[chapterNumber]_[qType]_<LCT#>_{[qID]}<(n)>.png
//
// [subjectInd] - Subject indicator [P|M|C] 
// [standard]   - Standard [0-9]+
// [bookCode]   - Book code [A-Z0-9]+
// [chapterNum] - Chapter number [0-9]+
// [qType]      - Question type [SCA|MCA|MMT|NT|LCT]
// <LCT#>       - If qType is LCT, this will contain the sequence of LCT
// {qId}        - Based on the bookCode, this is overridden. This can have 
//                multiple parts
// (n)          - Part number


// Catering for LCT context - how?

@Data
public class JEEQuestion {
    
    public static final String SCA = "SCA" ;
    public static final String MCA = "MCA" ;
    public static final String MMT = "MMT" ;
    public static final String NT  = "NT"  ;
    public static final String LCT = "LCT" ;
    
    private String  fileName     = null ;
    private String  qRef         = null ;
    private boolean isLCTContext = false ;
    
    private String subjectCode  = null ;  // 0
    private int    standard     = 0 ;     // 1
    private String bookCode     = null ;  // 2
    private int    chapterNum   = 0 ;     // 3
    private String questionType = null ;  // 4
    private int    lctSequence  = -1 ;    // 5 <optional>
    private QID    qId          = null ;  // 5/6 -> last-1
    private int    partNumber   = -1 ;    // Last
    
    public JEEQuestion( String fileName ) {
        this.fileName = fileName ;
        parseFileName( fileName ) ;
        this.qRef = getQRef() ;
    }
    
    private void parseFileName( String fileName ) {
        
        String fName = fileName ;
        
        fName = stripFileExtension( fName ) ;
        fName = collectPartNumber( fName ) ;
        
        // Tokenize the remaining string into parts
        String[] parts = fName.split( "_" ) ;

        this.subjectCode  = parts[0].trim() ;
        this.standard     = getInt( parts[1] ) ;
        this.bookCode     = parts[2].trim() ;
        this.chapterNum   = getInt( parts[3] ) ;
        this.questionType = parts[4].trim() ;
        
        String[] qIdParts = null ;
        if( this.questionType.equals( LCT ) ) {
            this.lctSequence = getInt( parts[5] ) ;
            qIdParts = Arrays.copyOfRange( parts, 6, parts.length ) ;
            if( qIdParts == null || qIdParts.length == 0 ) {
                // This implies that this is a LCT context
                isLCTContext = true ;
            }
        }
        else {
            qIdParts = Arrays.copyOfRange( parts, 5, parts.length ) ;
        }
        
        if( !isLCTContext ) {
            // LCT contexts do not have a QID. QID comes for the questions
            // to which LCT context gets attached to.
            parseBookSpecificQuestionId( qIdParts ) ;
        }
    }
    
    private String stripFileExtension( String fileName ) {
        
        String fName = fileName ;
        
        if( fName.endsWith( ".png" ) ) {
            fName = fileName.substring( 0, fileName.length()-4 ) ;
        }
        return fName ;
    }
    
    private String collectPartNumber( String fileName ) {
        
        String fName = fileName ;
        
        if( fName.contains( "(" ) ) {
            int startIndex = fName.indexOf( "(" ) ;
            int endIndex   = fName.indexOf( ")", startIndex ) ;
            
            String partNumStr = fName.substring( startIndex+1, endIndex ) ;
            this.partNumber = Integer.parseInt( partNumStr ) ;
            
            fName = fName.substring( 0, startIndex ) ;
        }
        return fName ;
    }
    
    private void parseBookSpecificQuestionId( String[] qIdParts ) {
        if( this.bookCode.equals( PEARSON_IIT_FOUNDATION ) ) {
            this.qId = new PearsonQID( qIdParts ) ;
        }
        else {
            throw new IllegalArgumentException( 
                    "Book " + this.bookCode + " not recognized." ) ;
        }
        
        this.qId.parseQID() ;
    }
    
    private int getInt( String intStr ) {
        intStr = intStr.trim() ;
        return Integer.parseInt( intStr ) ;
    }
    
    public String getQRef() {
        
        if( isLCTContext ) {
            // LCT Contexts are not questions in themselves and hence do 
            // not have a question reference number.
            return null ;
        }
        
        if( this.qRef != null ) {
            return this.qRef ;
        }

        StringBuilder sb = new StringBuilder() ;
        sb.append( this.subjectCode )
          .append( "/" )
          .append( this.standard )
          .append( "/" )
          .append( this.bookCode )
          .append( "/" )
          .append( this.chapterNum )
          .append( "/" )
          .append( this.questionType )
          .append( "/" ) ;
        
        if( this.lctSequence != -1 ) {
            sb.append( this.lctSequence )
              .append( "/" ) ;
        }
        
        sb.append( this.qId.getQRefPart() ) ;
          
        return sb.toString() ;
    }
    
    public static void main( String[] args ) {
        
        String[] ids = {
            "P_6_PF_1_LCT_2_CA_2_23.png"
        } ;
        
        JEEQuestion q = null ;
        for( String id : ids ) {
            q = new JEEQuestion( id ) ;
            System.out.println( q.getQRef() ) ;
        }
    }
}


