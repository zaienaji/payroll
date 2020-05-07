package org.wirabumi.payroll;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.management.OperationsException;

import org.wirabumi.common.ContractException;
import org.wirabumi.tax.TaxCalculator;
import org.wirabumi.tax.TaxDimension;

public class StandardSalary extends Salary {
    
    public StandardSalary(TaxDimension taxDimension, TaxCalculator taxCalculator)
	    throws ContractException {
	super(taxDimension, taxCalculator);
    }

    @Override
    BigDecimal calculateIncomeTax() throws OperationsException, ContractException {
	
	BigDecimal grossIncome = getRecurringPay().multiply(MONTHS_IN_A_YEAR).add(getNonRecurringPay());
	BigDecimal yearlyIncomeTax = taxCalculator.calculateIncomeTax(taxDimension, grossIncome);
	
	return yearlyIncomeTax.divide(MONTHS_IN_A_YEAR, -3, RoundingMode.UP);
    }

}
