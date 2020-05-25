package org.wirabumi.payroll;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.management.OperationsException;

import org.wirabumi.common.ContractException;
import org.wirabumi.tax.TaxCalculator;
import org.wirabumi.tax.TaxDimension;

public class YearEndSalary extends Salary {

    public YearEndSalary(TaxDimension taxDimension, TaxCalculator taxCalculator)
	    throws ContractException {
	super(taxDimension, taxCalculator);
    }

    @Override
    BigDecimal calculateIncomeTax() throws OperationsException, ContractException {
	BigDecimal incomeTax = taxCalculator.calculateIncomeTax(taxDimension, grossIncome());
	return incomeTax.subtract(getTaxPaidInAdvance());
    }
    
    @Override
    public void recalculateIncomeTax() throws OperationsException, ContractException {
	BigDecimal yearlyIncomeTax = calculateIncomeTax();
	incomeTax = yearlyIncomeTax.setScale(-3, RoundingMode.UP);
	isCalculated = true;
    }

    @Override
    BigDecimal grossIncome() {
	return getRecurringPay()
		.add(getNonRecurringPay())
		.add(getAccumulatedPrevRecurringPay())
		.add(getAccumulatedPrevNonRecurringPay());
    }
    
    //TODO override calculateTaxAllowance
    
}
