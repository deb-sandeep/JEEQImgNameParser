package com.sandy.jeecoach.util;

import java.io.File ;

public class ValidationHelper {

    public void checkNullFile( File file ) {
        assertCondition( file == null, 
                         "File specified is null." ) ;
    }

    public void checkMinimumPreambleParts( String[] parts ) {
        assertCondition( parts != null && parts.length < 5,
                         "File name should have at least five segments." ) ;
    }

    public void validateSubjectCode( String subjectCode ) {
        assertCondition( subjectCode == null ||
                         subjectCode.trim().equals( "" ) || 
                         !JEEQuestionImage.SUB_SEQ.contains( subjectCode ),
                         "Invalid subject code." ) ;
    }

    public void validateBookCode( String bookCode ) {
        if( bookCode == null || bookCode.trim().equals( "" ) ) {
            assertCondition( true, "Book code empty or null." ) ;
        }
        
        if( !JEEBookCode.isValidBookCode( bookCode ) ) {
            assertCondition( "Invalid book code." ) ;
        }
    }

    public void validateQuestionType( String questionType ) {
        assertCondition( questionType == null ||
                         questionType.trim().equals( "" ) || 
                         !JEEQuestionImage.Q_TYPE_SEQ.contains( questionType ),
                         "Invalid question type." ) ;
    }
    
    private void assertCondition( String message ) {
        throw new IllegalArgumentException( message ) ;
    }
    
    private void assertCondition( boolean flag, String message ) {
        if( flag ) {
            throw new IllegalArgumentException( message ) ;
        }
    }

    public void validatePFSectionId( String sectionId ) {
        assertCondition( sectionId.trim().equals( "" ) || 
                         !PearsonQID.SECTION_SEQ.contains( sectionId ),
                         "Invalid PF section ID." ) ;
    }
}
