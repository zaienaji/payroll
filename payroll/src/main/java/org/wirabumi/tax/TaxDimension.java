package org.wirabumi.tax;

import org.wirabumi.common.Contract;
import org.wirabumi.common.ContractException;

public class TaxDimension {
    
    private final boolean isMarried;
    private final int numberOfChilds;
    private final String npwp;
    private final boolean isPaidByEmployer;
    private final TaxCalculationType calculationType;
    
    public TaxDimension(boolean isMarried, int numberOfChilds, String npwp, 
	    boolean isPaidByEmployer, TaxCalculationType taxCalculationType) throws ContractException {
	
	Contract.require(numberOfChilds>0, "negatif number of child is not allowed");
	Contract.require(taxCalculationType != null, "taxCalculationType is a mandatory field");
	
	this.isMarried = isMarried;
	this.numberOfChilds = numberOfChilds>3 ? 3 : numberOfChilds;
	this.npwp = npwp;
	this.isPaidByEmployer = isPaidByEmployer;
	this.calculationType = taxCalculationType;
    }

    public boolean isMarried() {
        return isMarried;
    }

    public int getNumberOfChilds() {
        return numberOfChilds;
    }

    public String getNpwp() {
        return npwp;
    }

    public boolean isPaidByEmployer() {
        return isPaidByEmployer;
    }

    public TaxCalculationType getCalculationType() {
        return calculationType;
    }
    
}
