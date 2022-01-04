package com.sandy.jeecoach.util;

import java.util.Arrays ;
import java.util.List ;

import lombok.Data ;
import lombok.EqualsAndHashCode ;

import static com.sandy.jeecoach.util.JEEQuestionImage.* ;

/**
 * For Pearson, questions are arranged in sections. The sections are as follows:
 * 
 * 1. Very Short Answer Type Questions [VSAT]
 * 2. Short Answer Type Questions      [SAT]
 * 3. Essay Type Questions             [ETQ]
 * 4. Concept Application Ln           [CAL_n]
 * 5. Assessment Test n                [AT_n]
 *
 * CA and AT can have more sections each identified by a numeral.
 * 
 * Each section has questions whose ids are integers. These ids can continue
 * their series across following section or start a new series.
 * 
 * Hence the question ID will have two parts
 * <section identifier>_<question number>
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PearsonQID extends QID {
    
    public static String VSAT = "VSAT" ;
    public static String SAT  = "SAT" ;
    public static String ETQ  = "ETQ" ;
    public static String CA   = "CA" ;
    public static String AT   = "AT" ;
    
    public static String[] SECTION_IDS = { VSAT, SAT, ETQ, CA, AT } ;
    public static List<String> SECTION_SEQ = Arrays.asList( VSAT, SAT, ETQ, CA, AT ) ;
    
    private String sectionId = null ;
    
    // For CA and AT there are subsections, 1, 2, 3 etc. This variable 
    // represents this subsection. For some sections we do not have a subsection
    // in which case this will remain as 0
    private int subSectionNumber = -1 ;
    
    private int questionNumber = -1 ;
    
    private ValidationHelper validator = new ValidationHelper() ;
    
    PearsonQID( String[] parts ) {
        parseQID( parts ) ;
    }

    private void parseQID( String[] qIdParts ) {
        
        if( qIdParts.length < 2 || qIdParts.length > 3 ) {
            throw new IllegalArgumentException( 
                    "Invalid number of PF qID segments. Should be 2-3" ) ;
        }
        
        this.sectionId = qIdParts[0].trim() ;
        validator.validatePFSectionId( this.sectionId ) ;
        
        extractAttributes( qIdParts ) ;
    }
    
    private void extractAttributes( String[] qIdParts ) {
        
        if( this.sectionId.equals( AT ) || 
            this.sectionId.equals( CA ) ) {
            
            if( qIdParts.length != 3 ) {
                throw new IllegalArgumentException( 
                    "For PF section AT and CA three segments are required" ) ;
            }
            
            subSectionNumber = getInt( "Section number", qIdParts[1].trim() ) ;
            questionNumber = getInt( "Question number", qIdParts[2].trim() ) ;
        }
        else {
            if( qIdParts.length != 2 ) {
                throw new IllegalArgumentException( 
                    "For PF sections except AT, CA there are two segments" ) ;
            }
            
            questionNumber = getInt( "Question number", qIdParts[1].trim() ) ;
        }
    }

    @Override
    public void incrementQuestionNumber() {
        this.questionNumber += 1 ;
    }

    @Override
    public String getQRefPart() {
        
        StringBuilder sb = new StringBuilder( this.sectionId ) ;
        if( this.subSectionNumber != -1 ) {
            sb.append( "/" + this.subSectionNumber ) ;
        }
        sb.append( "/" + this.questionNumber ) ;
        
        return sb.toString() ;
    }

    @Override
    public String getFilePartName() {
        
        StringBuilder sb = new StringBuilder( this.sectionId ) ;
        if( this.subSectionNumber != -1 ) {
            sb.append( "_" + this.subSectionNumber ) ;
        }
        sb.append( "_" + this.questionNumber ) ;
        
        return sb.toString() ;
    }


    @Override
    public int compareTo( QID o ) {
        if( !this.getClass().getName().equals( o.getClass().getName() ) ) {
            return this.getClass().getName().compareTo( o.getClass().getName() ) ;
        }
        
        PearsonQID qid = ( PearsonQID )o ;
        if( getSecSeq() != qid.getSecSeq() ) {
            return getSecSeq() - qid.getSecSeq() ;
        }
        
        if( subSectionNumber != qid.subSectionNumber ) {
            return subSectionNumber - qid.subSectionNumber ;
        }
        
        return questionNumber - qid.questionNumber ;
    }
    
    public int getSecSeq() {
        return SECTION_SEQ.indexOf( sectionId ) ;
    }
}
