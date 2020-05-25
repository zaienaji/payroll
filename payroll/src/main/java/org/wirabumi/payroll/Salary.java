package org.wirabumi.payroll;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.management.OperationsException;

import org.wirabumi.common.Contract;
import org.wirabumi.common.ContractException;
import org.wirabumi.tax.TaxCalculator;
import org.wirabumi.tax.TaxDimension;

public abstract class Salary {

    protected final TaxDimension taxDimension;
    protected final TaxCalculator taxCalculator;
    protected BigDecimal incomeTax;
    protected boolean isCalculated;

    private BigDecimal recurringPay;
    private BigDecimal nonRecurringPay;
    
    private BigDecimal accumulatedPrevRecurringPay;
    private BigDecimal accumulatedPrevNonRecurringPay;
    
    private BigDecimal taxPaidInAdvance;
    
    private BigDecimal taxAllowance;
    
    protected static final BigDecimal MONTHS_IN_A_YEAR = new BigDecimal(12);
    
    public Salary(TaxDimension taxDimension, TaxCalculator taxCalculator) throws ContractException {

	Contract.require(taxDimension != null, "taxDimention is a mandatory field");

	this.taxDimension = taxDimension;
	this.taxCalculator = taxCalculator;

	init();
    }

    private void init() {
	recurringPay = BigDecimal.ZERO;
	nonRecurringPay = BigDecimal.ZERO;
	
	accumulatedPrevRecurringPay = BigDecimal.ZERO;
	accumulatedPrevNonRecurringPay = BigDecimal.ZERO;
	
	taxPaidInAdvance = BigDecimal.ZERO;
	incomeTax = BigDecimal.ZERO;
	isCalculated = false;
	taxAllowance = BigDecimal.ZERO;

    }
    
    public BigDecimal getTaxAllowance() {
        return taxAllowance;
    }

    public BigDecimal getRecurringPay() {
	return recurringPay;
    }

    public void setRecurringPay(BigDecimal basicPay) {
	this.recurringPay = basicPay;
    }

    public BigDecimal getNonRecurringPay() {
	return nonRecurringPay;
    }

    public void setNonRecurringPay(BigDecimal nonRecurringPay) {
	this.nonRecurringPay = nonRecurringPay;
    }

    public BigDecimal getIncomeTax() {
	return incomeTax;
    }

    public boolean isIncomeTaxCalculated() {
	return isCalculated;
    }
    
    public BigDecimal getTaxPaidInAdvance() {
        return taxPaidInAdvance;
    }

    public void setTaxPaidInAdvance(BigDecimal taxPaidInAdvance) {
        this.taxPaidInAdvance = taxPaidInAdvance;
    }
    
    public BigDecimal getAccumulatedPrevRecurringPay() {
        return accumulatedPrevRecurringPay;
    }

    public void setAccumulatedPrevRecurringPay(BigDecimal accumulatedPrevRecurringPay) {
        this.accumulatedPrevRecurringPay = accumulatedPrevRecurringPay;
    }

    public BigDecimal getAccumulatedPrevNonRecurringPay() {
        return accumulatedPrevNonRecurringPay;
    }

    public void setAccumulatedPrevNonRecurringPay(BigDecimal accumulatedPrevNonRecurringPay) {
        this.accumulatedPrevNonRecurringPay = accumulatedPrevNonRecurringPay;
    }

    public void recalculateIncomeTax() throws OperationsException, ContractException {
	
	incomeTax = calculateIncomeTax();
	taxAllowance = calculateTaxAllowance();
	isCalculated = true;

    }
    
    BigDecimal calculateTaxAllowance() throws OperationsException, ContractException {
	BigDecimal yearlyTaxAllowance = taxCalculator.calculateTaxAllowance(taxDimension, grossIncome());
	return yearlyTaxAllowance.divide(MONTHS_IN_A_YEAR, -3, RoundingMode.UP);
    }
    
    abstract BigDecimal calculateIncomeTax() throws OperationsException, ContractException;
    
    abstract BigDecimal grossIncome();

}
