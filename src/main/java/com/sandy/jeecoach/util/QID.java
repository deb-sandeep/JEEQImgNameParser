package com.sandy.jeecoach.util;

public abstract class QID implements Comparable<QID>{
    
    protected QID() {}

    public abstract void incrementQuestionNumber() ;
    
    public abstract String getQRefPart() ;
    
    public abstract String getFilePartName() ;
}
