package org.wirabumi.incometax;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;

import javax.management.OperationsException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.wirabumi.common.ContractException;
import org.wirabumi.payroll.Salary;
import org.wirabumi.payroll.SalaryBuilder;
import org.wirabumi.tax.TaxCalculationType;
import org.wirabumi.tax.TaxDimension;

class EmployeeSalarySpec {
    
    private static Stream<Arguments> provideNetTaxData() {
	return Stream.of(
		// 	     basic pay	non recurring	expected NPWP
		Arguments.of(17000000,	0, 		1363000, "some npwp"),
		Arguments.of(10000000,	10000000, 	275000,  "some npwp"),
		Arguments.of(17000000,	0, 		1636000, null),
		Arguments.of(17000000,	0, 		1636000, ""));

    }
    
    @ParameterizedTest
    @MethodSource("provideNetTaxData")
    public void net_income_tax(
	    int basicPay,
	    int nonRecurringPay,
	    long expectedIncomeTax,
	    String npwp) 
		    throws ContractException, OperationsException {

	TaxDimension taxDimension = new TaxDimension(true, 3, npwp, true, TaxCalculationType.Standard);
	SalaryBuilder salaryBuilder = new SalaryBuilder(taxDimension);
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
	SalaryBuilder salaryBuilder = new SalaryBuilder(taxDimension);
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
    }
    
    @ParameterizedTest
    @MethodSource("provideNonStandardTaxData")
    public void non_standard_income_tax(
	    int basicPay,
	    int nonRecurringPay,
	    long expectedIncomeTax,
	    String npwp,
	    boolean isPaidByEmployeer,
	    int taxPaidInAdvance,
	    TaxCalculationType taxCalculationType) 
		    throws ContractException, OperationsException {

	TaxDimension taxDimension = new TaxDimension(true, 3, npwp, isPaidByEmployeer, taxCalculationType);
	SalaryBuilder salaryBuilder = new SalaryBuilder(taxDimension);
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
	SalaryBuilder salaryBuilder = new SalaryBuilder(taxDimension);
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
	SalaryBuilder salaryBuilder = new SalaryBuilder(taxDimension);
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
