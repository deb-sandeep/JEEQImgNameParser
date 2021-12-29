package com.sandy.jeecoach.util;

public abstract class QID {
    
    protected QID() {}

    public abstract void incrementQuestionNumber() ;
    
    public abstract String getQRefPart() ;
    
    public abstract String getFilePartName() ;
}
