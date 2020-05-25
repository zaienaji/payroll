package org.wirabumi.payroll;

import java.math.BigDecimal;

import org.wirabumi.common.ContractException;
import org.wirabumi.tax.TaxCalculator;
import org.wirabumi.tax.TaxCalculatorFactory;
import org.wirabumi.tax.TaxDimension;

public class SalaryBuilder {
    
    private final SalaryFactory salaryFactory;
    private final TaxCalculatorFactory taxCalculatorFactory;
    
    private final TaxDimension taxDimension;
    
    //optional field
    private BigDecimal recurringPay;
    private BigDecimal nonRecurringPay;
    
    private BigDecimal accumulatedPrevRecurringPay;
    private BigDecimal accumulatedPrevNonRecurringPay;
    
    private BigDecimal taxPaidInAdvance;
    
    public SalaryBuilder(TaxDimension taxDimension, SalaryFactory salaryFactory, TaxCalculatorFactory taxCalculatorFactory) {
	super();
	this.taxDimension = taxDimension;
	
	this.salaryFactory = salaryFactory;
	this.taxCalculatorFactory = taxCalculatorFactory;
	
	init();
    }
    
    private void init() {
	recurringPay = BigDecimal.ZERO;
	nonRecurringPay = BigDecimal.ZERO;
	accumulatedPrevRecurringPay = BigDecimal.ZERO;
	accumulatedPrevNonRecurringPay = BigDecimal.ZERO;
	taxPaidInAdvance = BigDecimal.ZERO;
    }

    public SalaryBuilder recurringPay(BigDecimal recurringPay) {
	this.recurringPay = recurringPay;
	return this;
    }
    
    public SalaryBuilder nonRecurringPay(BigDecimal nonRecurringPay) {
	this.nonRecurringPay = nonRecurringPay;
	return this;
    }
    
    public SalaryBuilder accumulatedPrevRecurringPay(BigDecimal accumulatedPrevRecurringPay) {
	this.accumulatedPrevRecurringPay = accumulatedPrevRecurringPay;
	return this;
    }
    
    public SalaryBuilder accumulatedPrevNonRecurringPay(BigDecimal accumulatedPrevNonRecurringPay) {
	this.accumulatedPrevNonRecurringPay = accumulatedPrevNonRecurringPay;
	return this;
    }
    
    public SalaryBuilder taxPaidInAdvance(BigDecimal taxPaidInAdvance) {
	this.taxPaidInAdvance = taxPaidInAdvance;
	return this;
    }

    public Salary build() throws ContractException {
	
	TaxCalculator taxCalculator = taxCalculatorFactory.create(this.taxDimension);
	Salary salary = salaryFactory.create(this.taxDimension, taxCalculator);
	
	salary.setRecurringPay(this.recurringPay);
	salary.setNonRecurringPay(this.nonRecurringPay);
	salary.setAccumulatedPrevRecurringPay(this.accumulatedPrevRecurringPay);
	salary.setAccumulatedPrevNonRecurringPay(this.accumulatedPrevNonRecurringPay);
	salary.setTaxPaidInAdvance(this.taxPaidInAdvance);
	
	return salary;
    }

}
