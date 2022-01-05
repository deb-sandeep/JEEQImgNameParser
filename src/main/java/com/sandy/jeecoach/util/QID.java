package com.sandy.jeecoach.util;

public abstract class QID implements Comparable<QID>{
    
    protected JEEQuestionImage parent = null ;
    
    protected QID( JEEQuestionImage qImg ){
        this.parent = qImg ;
    }

    public abstract void incrementQuestionNumber() ;
    
    public abstract String getQRefPart() ;
    
    public abstract String getFilePartName() ;
    
    public abstract int getProjectedTime() ;
    
    public abstract int getDifficultyLevel() ;
}
