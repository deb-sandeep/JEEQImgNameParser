package com.sandy.jeecoach.util;

import lombok.Data ;
import lombok.EqualsAndHashCode ;

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
    
    private String sectionId = null ;
    
    // For CA and AT there are subsections, 1, 2, 3 etc. This variable 
    // represents this subsection. For some sections we do not have a subsection
    // in which case this will remain as 0
    private int subSectionNumber = -1 ;
    
    private int questionNumber = -1 ;
    
    PearsonQID( String[] parts ) {
        parseQID( parts ) ;
    }

    private void parseQID( String[] qIdParts ) {
        if( qIdParts.length < 2 || qIdParts.length > 3 ) {
            
            throw new IllegalArgumentException( "\nPearson IIT foundation " +
            "has question id in 2 or 3 segments. Section ID + Question #.\n" + 
            "Supplied qIdParts have " + qIdParts.length + " segments." ) ;
        }
        
        this.sectionId = qIdParts[0].trim() ;
        extractAttributes( qIdParts ) ;
    }
    
    private void extractAttributes( String[] qIdParts ) {
        
        boolean isSectionIdValid = false ;
        for( String validId : SECTION_IDS ) {
            if( this.sectionId.equals( validId ) ) {
                isSectionIdValid = true ;
            }
        }
        
        if( !isSectionIdValid ) {
            throw new IllegalArgumentException( 
                "Invalid Pearson section id " + this.sectionId ) ;
        }
        
        if( this.sectionId.equals( AT ) || 
            this.sectionId.equals( CA ) ) {
            
            if( qIdParts.length != 3 ) {
                throw new IllegalArgumentException( "\nPearson IIT foundation " +
                    "has question id for AT, CA in 3 segments.\n" + 
                    "Supplied qIdParts have " + qIdParts.length + " segments." ) ;
            }
            
            subSectionNumber = Integer.parseInt( qIdParts[1].trim() ) ;
            questionNumber = Integer.parseInt( qIdParts[2].trim() ) ;
        }
        else {
            if( qIdParts.length != 2 ) {
                throw new IllegalArgumentException( "\nPearson IIT foundation " +
                    "has question id for VSAT, SAT, ETQ in 2 segments.\n" + 
                    "Supplied qIdParts have " + qIdParts.length + " segments." ) ;
            }
            
            questionNumber = Integer.parseInt( qIdParts[1].trim() ) ;
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
}
