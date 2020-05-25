package org.wirabumi.tax;

import java.math.BigDecimal;

import javax.management.OperationsException;

import org.wirabumi.common.ContractException;

public class TaxCalculatorDecorator extends TaxCalculator {
    
    private TaxCalculator wrappee;;
    
    public TaxCalculatorDecorator(TaxCalculator wrappee) {
	super();
	this.wrappee = wrappee;
    }

    @Override
    BigDecimal incomeTax(BigDecimal taxableIncome) throws OperationsException, ContractException {
	return wrappee.incomeTax(taxableIncome);
    }

    @Override
    BigDecimal taxAllowance(BigDecimal taxableIncome) throws OperationsException, ContractException {
	return wrappee.taxAllowance(taxableIncome);
    }

}
