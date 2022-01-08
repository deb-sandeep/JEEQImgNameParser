package com.sandy.jeecoach.util;

import static com.sandy.jeecoach.util.JEEQuestionImage.LCT ;
import static com.sandy.jeecoach.util.JEEQuestionImage.MCA ;
import static com.sandy.jeecoach.util.JEEQuestionImage.MMT ;
import static com.sandy.jeecoach.util.JEEQuestionImage.NT ;
import static com.sandy.jeecoach.util.JEEQuestionImage.SCA ;
import static com.sandy.jeecoach.util.JEEQuestionImage.getInt ;

import java.util.Arrays ;
import java.util.List ;

import lombok.EqualsAndHashCode ;
import lombok.Getter ;
import lombok.Setter ;

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
@EqualsAndHashCode(callSuper = false)
public class PearsonQID extends QID {
    
    public static final String VSAT = "VSAT" ;
    public static final String SAT  = "SAT" ;
    public static final String ETQ  = "ETQ" ;
    public static final String CA   = "CA" ;
    public static final String AT   = "AT" ;
    
    public static String[] SECTION_IDS = { VSAT, SAT, ETQ, CA, AT } ;
    public static List<String> SECTION_SEQ = Arrays.asList( VSAT, SAT, ETQ, CA, AT ) ;
    
    @Getter @Setter private String sectionId = null ;
    
    // For CA and AT there are subsections, 1, 2, 3 etc. This variable 
    // represents this subsection. For some sections we do not have a subsection
    // in which case this will remain as 0
    @Getter private int subSectionNumber = -1 ;
    
    @Getter private int questionNumber = -1 ;
    
    private ValidationHelper validator = new ValidationHelper() ;
    
    PearsonQID( JEEQuestionImage qImg, String[] parts ) {
        super( qImg ) ;
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
    
    public int getProjectedTime() {
        
        int projectedTime = 0 ;
        
        switch( parent.getQuestionType() ) {
            case SCA:
            case LCT:
                projectedTime = 120 ;
                if( sectionId.equals( VSAT ) || sectionId.equals( SAT ) ) {
                    projectedTime = 60 ;
                }
                break ;
                
            case MCA:
            case MMT:
                projectedTime = 240 ;
                if( sectionId.equals( VSAT ) || sectionId.equals( SAT ) ) {
                    projectedTime = 180 ;
                }
                break ;
                
            case NT:
                projectedTime = 180 ;
                if( sectionId.equals( VSAT ) || sectionId.equals( SAT ) ) {
                    projectedTime = 120 ;
                }
                break ;
        }
        return projectedTime ;
    }
    
    public int getDifficultyLevel() {
        
        int difficultyLevel = 3 ;
        switch( sectionId ) {
            case VSAT:
            case SAT:
                difficultyLevel = 2 ;
                break ;
                
            case ETQ:
                difficultyLevel = 5 ;
                break ;
                
            case CA:
            case AT:
                switch( this.subSectionNumber ) {
                    case 1:
                        difficultyLevel = 2 ;
                        break ;
                        
                    case 2:
                        difficultyLevel = 3 ;
                        break ;
                        
                    case 3:
                        difficultyLevel = 5 ;
                        break ;
                }
        }
        return difficultyLevel ;
    }
}
