package com.sandy.jeecoach.util;

import static com.sandy.jeecoach.util.JEEQuestionImage.getInt ;

import lombok.EqualsAndHashCode ;
import lombok.Getter ;

/**
 * For MTG reasoning, questions are in straight sequence of question numbers.
 */
@EqualsAndHashCode(callSuper = false)
public class MTGReasoningQID extends QID {
    
    @Getter private int questionNumber = -1 ;
    
    MTGReasoningQID( JEEQuestionImage qImg, String[] parts ) {
        super( qImg ) ;
        parseQID( parts ) ;
    }

    private void parseQID( String[] qIdParts ) {
        
        if( qIdParts.length != 1 ) {
            throw new IllegalArgumentException( 
                    "Invalid number of MR qID segments. Should be 1" ) ;
        }
        
        questionNumber = getInt( "Question number", qIdParts[0].trim() ) ;
    }
    
    @Override
    public void incrementQuestionNumber() {
        this.questionNumber += 1 ;
    }

    @Override
    public String getQRefPart() {
        return "" + this.questionNumber ;
    }

    @Override
    public String getFilePartName() {
        return "" + this.questionNumber ;
    }

    @Override
    public int compareTo( QID o ) {
        if( !this.getClass().getName().equals( o.getClass().getName() ) ) {
            return this.getClass().getName().compareTo( o.getClass().getName() ) ;
        }
        MTGReasoningQID qid = ( MTGReasoningQID )o ;
        return questionNumber - qid.questionNumber ;
    }
    
    public int getProjectedTime() {
        return 120 ;
    }
    
    public int getDifficultyLevel() {
        return 3 ;
    }
}
