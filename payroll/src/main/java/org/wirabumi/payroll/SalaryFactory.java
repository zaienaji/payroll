package org.wirabumi.payroll;

import org.wirabumi.common.ContractException;
import org.wirabumi.tax.TaxCalculator;
import org.wirabumi.tax.TaxDimension;

public class SalaryFactory {
    
    public Salary create(TaxDimension taxDimension, TaxCalculator taxCalculator) throws ContractException {
	switch (taxDimension.getCalculationType()) {
	case Standard:
	    return new StandardSalary(taxDimension, taxCalculator); 
	case NonRecurringOnly:
	    return new NonRecurringOnlySalary(taxDimension, taxCalculator);
	case YearEnd:
	    return new YearEndSalary(taxDimension, taxCalculator);

	default:
	    throw new IllegalArgumentException("unsupported tax calculation type " + taxDimension.getCalculationType());
	}
	
    }

}
