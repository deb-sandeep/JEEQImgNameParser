package com.sandy.jeecoach.util ;

import static com.sandy.jeecoach.util.JEEBookCode.* ;

import java.io.File ;
import java.util.Arrays ;
import java.util.List ;

import lombok.Data ;
import lombok.EqualsAndHashCode ;

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

@Data
@EqualsAndHashCode( callSuper = false )
public class JEEQuestionImage extends AbstractQuestion<JEEQuestionImage> 
    implements Comparable<JEEQuestionImage>{
    
    public static final String SCA = "SCA" ;
    public static final String MCA = "MCA" ;
    public static final String MMT = "MMT" ;
    public static final String NT  = "NT"  ;
    public static final String LCT = "LCT" ;
    
    static List<String> Q_TYPE_SEQ = Arrays.asList( SCA, MCA, NT, LCT, MMT ) ;
    static List<String> SUB_SEQ   = Arrays.asList( "P", "C", "M" ) ;
    
    private boolean isLCTContext = false ;
    
    private String subjectCode  = null ;  // 0
    private int    standard     = 0 ;     // 1
    private String bookCode     = null ;  // 2
    private int    chapterNum   = 0 ;     // 3
    private String questionType = null ;  // 4
    private int    lctSequence  = -1 ;    // 5 <optional>
    private QID    qId          = null ;  // 5/6 -> last-1
    private int    partNumber   = -1 ;    // Last
    
    private File imgFile = null ;
    private ValidationHelper validator = new ValidationHelper() ;
    
    public JEEQuestionImage( File file ) {
        
        this.imgFile = file ;
        parseFileName( this.imgFile.getName() ) ;
    }
    
    public JEEQuestionImage getClone() {
        File file = new File( imgFile.getParent(), getFileName() ) ;
        return new JEEQuestionImage( file ) ;
    }
    
    private void parseFileName( String fileName ) 
        throws IllegalArgumentException {
        
        validator.checkNullFile( this.imgFile ) ;
        
        String fName = fileName ;
        
        fName = stripFileExtension( fName ) ;
        fName = collectPartNumber( fName ) ;
        
        // Tokenize the remaining string into parts
        String[] parts = fName.split( "_" ) ;
        
        validator.checkMinimumPreambleParts( parts ) ;

        this.subjectCode = parts[0].trim() ;
        validator.validateSubjectCode( this.subjectCode ) ;
        
        this.standard = getInt( "Standard", parts[1] ) ;
        
        this.bookCode = parts[2].trim() ;
        validator.validateBookCode( this.bookCode ) ;
        
        this.chapterNum = getInt( "Chapter Number", parts[3] ) ;
        
        this.questionType = parts[4].trim() ;
        validator.validateQuestionType( this.questionType ) ;
        
        String[] qIdParts = null ;
        if( this.questionType.equals( LCT ) ) {
            
            this.lctSequence = getInt( "LCT sequence", parts[5] ) ;
            
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
    
    public static int getInt( String field, String intStr ) {
        intStr = intStr.trim() ;
        int val = 0 ;
        try {
            val = Integer.parseInt( intStr ) ;
        }
        catch( Exception e ) {
            throw new IllegalArgumentException( intStr + " is not an int value." ) ;
        }
        return val ; 
    }
    
    private void parseBookSpecificQuestionId( String[] qIdParts ) {
        if( this.bookCode.equals( PEARSON_IIT_FOUNDATION ) ) {
            this.qId = new PearsonQID( this, qIdParts ) ;
        }
        else if( this.bookCode.equals( MTG_REASONING ) ) {
            this.qId = new MTGReasoningQID( this, qIdParts ) ;
        }
        else {
            throw new IllegalArgumentException( 
                    "Book " + this.bookCode + " not recognized." ) ;
        }
    }
    
    public String getFileName() {
        StringBuilder sb = new StringBuilder() ;
        sb.append( this.subjectCode ).append( "_" )
          .append( this.standard ).append( "_" )
          .append( this.bookCode ).append( "_" )
          .append( this.chapterNum ).append( "_" )
          .append( this.questionType ).append( "_" ) ;
        
        if( !isLCT() ) {
            sb.append( this.qId.getFilePartName() ) ;
        }
        else {
            sb.append( this.lctSequence ) ;
            if( !isLCTContext ) {
                sb.append( "_" )
                  .append( this.qId.getFilePartName() ) ;
            }
        }
        
        if( this.partNumber != -1 ) {
            sb.append( "(" + this.partNumber + ")" ) ;
        }
        sb.append( ".png" ) ;
        
        return sb.toString() ;
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
    
    public String getQRef() {
        
        StringBuilder sb = new StringBuilder() ;
        sb.append( subjectCode )
          .append( "/" )
          .append( standard )
          .append( "/" )
          .append( bookCode )
          .append( "/" )
          .append( chapterNum )
          .append( "/" )
          .append( questionType )
          .append( "/" ) ;
        
        if( lctSequence != -1 ) {
            sb.append( lctSequence )
              .append( "/" ) ;
        }
        
        if( qId != null ) {
            sb.append( qId.getQRefPart() ) ;
        }
          
        return sb.toString() ;
    }
    
    public String getLCTCtxQRef() {
        if( !isLCT() ) {
            throw new IllegalStateException( "Not an LCT question image." ) ;
        }
        
        StringBuilder sb = new StringBuilder() ;
        sb.append( subjectCode )
          .append( "/" )
          .append( standard )
          .append( "/" )
          .append( bookCode )
          .append( "/" )
          .append( chapterNum )
          .append( "/" )
          .append( questionType )
          .append( "/" )
          .append( lctSequence )
          .append( "/" ) ;
        return sb.toString() ;
    }
    
    public boolean isPart() {
        return this.partNumber != -1 ;
    }
    
    public boolean isLCT() {
        return this.questionType.equals( LCT ) ;
    }
    
    @Override
    public JEEQuestionImage nextQuestion() {
        JEEQuestionImage q = this.getClone() ;
        if( q.isPart() ) {
            q.partNumber++ ;
            if( q.partNumber > 2 ) {
                q.partNumber = -1 ;
                q.getQId().incrementQuestionNumber() ;
            }
        }
        else if( !q.isLCTContext ) {
            q.getQId().incrementQuestionNumber() ;
        }
        return q ;
    }
    
    @Override
    public int compareTo( JEEQuestionImage img ) {
        if( getSubjectSeq() != img.getSubjectSeq() ) {
            return getSubjectSeq() - img.getSubjectSeq() ;
        }
        
        if( standard != img.standard ) {
            return standard - img.standard ;
        }
        
        if( !bookCode.equals( img.bookCode ) ) {
            return bookCode.compareTo( img.bookCode ) ;
        }
        
        if( chapterNum != img.chapterNum ) {
            return chapterNum - img.chapterNum ;
        }
        
        if( getQTypeSeq() != img.getQTypeSeq() ) {
            return getQTypeSeq() - img.getQTypeSeq() ;
        }
        
        if( isLCT() && img.isLCT() ) {
            if( lctSequence != img.lctSequence ) {
                return lctSequence - img.lctSequence ;
            }
            else if( this.qId == null ) {
                return -1 ;
            }
            else if( img.qId == null ) {
                return 1 ;
            }
        }
        
        if( qId != null && img.qId != null ) {
            if( qId.compareTo( img.qId ) != 0 ) {
                return qId.compareTo( img.qId ) ;
            }
        }
        
        if( partNumber != -1 && img.partNumber != -1 ) {
            return partNumber - img.partNumber ;
        }

        return 0 ;
    }
    
    private int getSubjectSeq() {
        return SUB_SEQ.indexOf( subjectCode ) ;
    }
    
    private int getQTypeSeq() {
        return Q_TYPE_SEQ.indexOf( questionType ) ;
    }
    
    // Returns a guesstimate of projected solve time in seconds
    public int getProjectedTime() {

        if( this.qId != null ) {
            return qId.getProjectedTime() ;
        }
        return 0 ;
    }
    
    public int getDifficultyLevel() {
        if( this.qId != null ) {
            return qId.getDifficultyLevel() ;
        }
        return 3 ;
    }
    
    public String getNextQuestionType() {
        int index = getQTypeSeq() ;
        index++ ;
        if( index > Q_TYPE_SEQ.size()-1 ) {
            index = 0 ;
        }
        return Q_TYPE_SEQ.get( index ) ;
    }
    
    public String getNextSectionName() {
        return this.qId.getNextSectionName() ;
    }
    
    public static void main( String[] args ) {
        
        String[] ids = {
            "P_6_PF_1_LCT_1.png"
        } ;
        
        JEEQuestionImage q = null ;
        for( String id : ids ) {
            File file = new File( id ) ;
            q = new JEEQuestionImage( file ) ;
            System.out.println( q.getQRef() + " :: " + q.getFileName() ) ;
            System.out.println( "\t" + id.equals( q.getFileName() ) ) ; 

            q = (JEEQuestionImage)q.nextQuestion() ;
            System.out.println( q.getQRef() + " :: " + q.getFileName() ) ;
            System.out.println( "\t" + id.equals( q.getFileName() ) ) ; 
        }
    }
}


