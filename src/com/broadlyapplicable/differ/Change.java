package com.broadlyapplicable.differ;

/**
 *
 * @author dbell
 */
public class Change implements Comparable{

    @Override
    public int compareTo(Object o) {
        Change c = (Change) o;
        if (c.getLineNumber() == this.lineNumber) {
            return 0;
        }
        return (c.getLineNumber() > this.lineNumber) ? -1 : 1;
    }
    
    private final String line;
    private final int lineNumber;
    private final boolean added;
    private final boolean removed;
    
    public Change(String aLine, int aLineNumber, boolean isAdded, boolean isRemoved) {
        this.line = aLine;
        this.lineNumber = aLineNumber;
        this.added = isAdded;
        this.removed = isRemoved;
    }

    public String getLine() {
        return line;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public boolean isAdded() {
        return added;
    }

    public boolean isRemoved() {
        return removed;
    }
    
    
}
