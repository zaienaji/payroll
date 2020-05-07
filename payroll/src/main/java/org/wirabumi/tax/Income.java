package org.wirabumi.tax;

import java.math.BigDecimal;

public class Income {
    
    private final BigDecimal recurring;
    private final BigDecimal nonRecurring;
    
    private final BigDecimal prevRecurring;
    private final BigDecimal prevNonRecurring;
    
    
    public Income(BigDecimal recurring, BigDecimal nonRecurring, BigDecimal prevRecurring,
	    BigDecimal prevNonRecurring) {
	super();
	this.recurring = recurring;
	this.nonRecurring = nonRecurring;
	this.prevRecurring = prevRecurring;
	this.prevNonRecurring = prevNonRecurring;
    }


    public BigDecimal getRecurring() {
        return recurring;
    }


    public BigDecimal getNonRecurring() {
        return nonRecurring;
    }


    public BigDecimal getPrevRecurring() {
        return prevRecurring;
    }


    public BigDecimal getPrevNonRecurring() {
        return prevNonRecurring;
    }
    
}
