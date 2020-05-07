package org.wirabumi.tax;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.management.OperationsException;

import org.wirabumi.common.ContractException;

import com.google.common.base.Strings;

public class NoNpwpTaxCalculator extends TaxCalculatorDecorator {

    private static BigDecimal NO_NPWP_PENALTY_TARIFF = new BigDecimal("1.2");
    private final String npwp;

    public NoNpwpTaxCalculator(TaxCalculator wrappee, String npwp) {
	super(wrappee);
	
	this.npwp = npwp;
    }
    
    @Override
    public BigDecimal calculateIncomeTax(TaxDimension taxDimension, BigDecimal grossIncome)
	    throws OperationsException, ContractException {
	
	if (Strings.isNullOrEmpty(npwp)) {
	    BigDecimal taxableIncome = super.calculateIncomeTax(taxDimension, grossIncome);
	    BigDecimal incomeTax = taxableIncome
		    .multiply(NO_NPWP_PENALTY_TARIFF)
		    .setScale(-3, RoundingMode.UP);
	    
	    return incomeTax;
	}
	
	return super.calculateIncomeTax(taxDimension, grossIncome);
    }

}
