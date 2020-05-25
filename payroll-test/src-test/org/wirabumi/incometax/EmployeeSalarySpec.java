package org.wirabumi.incometax;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;

import javax.management.OperationsException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.wirabumi.common.ContractException;
import org.wirabumi.payroll.Salary;
import org.wirabumi.payroll.SalaryBuilder;
import org.wirabumi.payroll.SalaryFactory;
import org.wirabumi.tax.TaxCalculationType;
import org.wirabumi.tax.TaxCalculatorFactory;
import org.wirabumi.tax.TaxDimension;

class EmployeeSalarySpec {
    
    private SalaryFactory salaryFactory;
    private TaxCalculatorFactory taxCalculatorFactory;
    
    @BeforeEach
    public void setupEach() {
	salaryFactory = new SalaryFactory();
	taxCalculatorFactory = new TaxCalculatorFactory();
    }
    
    private static Stream<Arguments> provideNetTaxDataWithoutNpwp() {
	return Stream.of(
		// 	     basic pay	non recurring	expected NPWP
		Arguments.of(17000000,	0, 		1636000, null),
		Arguments.of(17000000,	0, 		1636000, ""));

    }
    
    @ParameterizedTest
    @MethodSource("provideNetTaxDataWithoutNpwp")
    public void net_income_tax_without_npwp(
	    int basicPay,
	    int nonRecurringPay,
	    long expectedIncomeTax,
	    String npwp) 
		    throws ContractException, OperationsException {

	TaxDimension taxDimension = new TaxDimension(true, 3, npwp, true, TaxCalculationType.Standard);
	SalaryBuilder salaryBuilder = new SalaryBuilder(taxDimension, salaryFactory, taxCalculatorFactory);
	salaryBuilder
		.recurringPay(new BigDecimal(basicPay))
		.nonRecurringPay(new BigDecimal(nonRecurringPay));
	Salary salary = salaryBuilder.build();
		
	salary.recalculateIncomeTax();
	
	long actualIncomeTax = salary.getIncomeTax().setScale(-3, RoundingMode.UP).longValue();
	assertEquals(expectedIncomeTax, actualIncomeTax);
	
	BigDecimal taxAllowance = salary.getTaxAllowance();
	long taxAllowanceWithPenalty = taxAllowance.multiply(new BigDecimal(1.2)).setScale(-3, RoundingMode.UP).longValue();
	assertEquals(expectedIncomeTax, taxAllowanceWithPenalty);

    }

    
    private static Stream<Arguments> provideNetTaxDataWithNpwp() {
	return Stream.of(
		// 	     basic pay	non recurring	expected NPWP
		Arguments.of(17000000,	0, 		1363000, "some npwp"),
		Arguments.of(10000000,	10000000, 	275000,  "some npwp"));

    }
    
    @ParameterizedTest
    @MethodSource("provideNetTaxDataWithNpwp")
    public void net_income_tax_with_npwp(
	    int basicPay,
	    int nonRecurringPay,
	    long expectedIncomeTax,
	    String npwp) 
		    throws ContractException, OperationsException {

	TaxDimension taxDimension = new TaxDimension(true, 3, npwp, true, TaxCalculationType.Standard);
	SalaryBuilder salaryBuilder = new SalaryBuilder(taxDimension, salaryFactory, taxCalculatorFactory);
	salaryBuilder
		.recurringPay(new BigDecimal(basicPay))
		.nonRecurringPay(new BigDecimal(nonRecurringPay));
	Salary salary = salaryBuilder.build();
		
	salary.recalculateIncomeTax();
	
	long actualIncomeTax = salary.getIncomeTax().setScale(-3, RoundingMode.UP).longValue();
	assertEquals(expectedIncomeTax, actualIncomeTax);

    }
    
    private static Stream<Arguments> provideGrossTaxData() {
	return Stream.of(
		// 	     basic pay	non recurring	expected NPWP
		Arguments.of(18363000,	0, 		1363000, "some npwp"),
		Arguments.of(17000000,	0, 		1159000, "some npwp"),
		Arguments.of(10000000,	10000000, 	234000,  "some npwp"),
		Arguments.of(18363000,	0,		1636000, null),
		Arguments.of(17000000,	0,		1390000, ""));
    }
    
    @ParameterizedTest
    @MethodSource("provideGrossTaxData")
    public void gross_income_tax(
	    int basicPay,
	    int nonRecurringPay,
	    long expectedIncomeTax,
	    String npwp) 
		    throws ContractException, OperationsException {

	TaxDimension taxDimension = new TaxDimension(true, 3, npwp, false, TaxCalculationType.Standard);
	SalaryBuilder salaryBuilder = new SalaryBuilder(taxDimension, salaryFactory, taxCalculatorFactory);
	salaryBuilder
		.recurringPay(new BigDecimal(basicPay))
		.nonRecurringPay(new BigDecimal(nonRecurringPay));
	Salary salary = salaryBuilder.build();
	
	salary.recalculateIncomeTax();

	long actualIncomeTax = salary.getIncomeTax().setScale(-3, RoundingMode.UP).longValue();
	assertEquals(expectedIncomeTax, actualIncomeTax);

    }
    
    private static Stream<Arguments> provideNonStandardTaxData() {
	return Stream.of(
		// 	     basic pay	non recurring	expected NPWP 		paid by employer  paid in advance  Tax calculation type
		Arguments.of(10000000,	5000000, 	21000,   "some npwp",	false,		  175000, 	   TaxCalculationType.NonRecurringOnly),
		Arguments.of(120000000,	0,		175000,	 "some npwp",	false,		  1925000,	   TaxCalculationType.YearEnd));
	
	//TODO add test case: paid by employer Y 
    }
    
    @ParameterizedTest
    @MethodSource("provideNonStandardTaxData")
    public void non_standard_income_tax(
	    int basicPay,
	    int nonRecurringPay,
	    long expectedIncomeTax,
	    String npwp,
	    boolean isPaidByEmployer,
	    int taxPaidInAdvance,
	    TaxCalculationType taxCalculationType) 
		    throws ContractException, OperationsException {

	TaxDimension taxDimension = new TaxDimension(true, 3, npwp, isPaidByEmployer, taxCalculationType);
	SalaryBuilder salaryBuilder = new SalaryBuilder(taxDimension, salaryFactory, taxCalculatorFactory);
	salaryBuilder
		.recurringPay(new BigDecimal(basicPay))
		.nonRecurringPay(new BigDecimal(nonRecurringPay))
		.taxPaidInAdvance(new BigDecimal(taxPaidInAdvance));
	Salary salary = salaryBuilder.build();
	
	salary.recalculateIncomeTax();

	long actualIncomeTax = salary.getIncomeTax().setScale(-3, RoundingMode.UP).longValue();
	assertEquals(expectedIncomeTax, actualIncomeTax);

    }

    @Test
    public void gross_bonus_this_month_with_basic_pay_from_previous_month()
	    throws ContractException, OperationsException {
	BigDecimal prevBasicPay = new BigDecimal(10000000);
	BigDecimal prevIncomeTax = new BigDecimal(175000);
	BigDecimal currentBonus = new BigDecimal(5000000);

	TaxDimension taxDimension = new TaxDimension(true, 3, "some npwp", false, TaxCalculationType.NonRecurringOnly);
	SalaryBuilder salaryBuilder = new SalaryBuilder(taxDimension, salaryFactory, taxCalculatorFactory);
	salaryBuilder
		.recurringPay(prevBasicPay)
		.nonRecurringPay(currentBonus)
		.taxPaidInAdvance(prevIncomeTax);
	Salary salary = salaryBuilder.build();
	
	salary.recalculateIncomeTax();

	long actualIncomeTax = salary.getIncomeTax().setScale(-3, RoundingMode.UP).longValue();
	assertEquals(21000, actualIncomeTax);
    }

    @Test
    public void gross_bonus_this_month_with_another_paid_bonus_this_month_and_basic_pay_from_previous_month()
	    throws ContractException, OperationsException {
	BigDecimal prevBasicPay = new BigDecimal(10000000);
	BigDecimal prevNonRecurringPay = new BigDecimal(5000000);
	BigDecimal taxInAdvance = new BigDecimal(196000);

	BigDecimal currentBonus = new BigDecimal(10000000);

	TaxDimension taxDimension = new TaxDimension(true, 3, "some npwp", false, TaxCalculationType.NonRecurringOnly);
	SalaryBuilder salaryBuilder = new SalaryBuilder(taxDimension, salaryFactory, taxCalculatorFactory);
	salaryBuilder
		.recurringPay(prevBasicPay)
		.nonRecurringPay(prevNonRecurringPay.add(currentBonus))
		.taxPaidInAdvance(taxInAdvance);
	Salary salary = salaryBuilder.build();
	
	salary.recalculateIncomeTax();

	long actualIncomeTax = salary.getIncomeTax().setScale(-3, RoundingMode.UP).longValue();
	assertEquals(100000, actualIncomeTax);

    }

}
