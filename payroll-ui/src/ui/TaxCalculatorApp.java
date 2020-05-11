package ui;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Scanner;

import javax.management.OperationsException;

import org.wirabumi.common.ContractException;
import org.wirabumi.payroll.Salary;
import org.wirabumi.payroll.SalaryBuilder;
import org.wirabumi.tax.TaxCalculationType;
import org.wirabumi.tax.TaxDimension;

@SuppressWarnings("ucd")
public class TaxCalculatorApp {

    public static void main(String[] args) throws ContractException, OperationsException {
	System.out.println("welcome to tax calculator");
	
	Scanner scanner = new Scanner(System.in);
	try {
	    TaxDimension taxDimension = collectTaskDimensionFromUser(scanner);
	    BigDecimal basicPay = collectSalaryMeasurementFromUser(scanner);
	    
	    SalaryBuilder salaryBuilder = new SalaryBuilder(taxDimension);
	    salaryBuilder.recurringPay(basicPay);

	    Salary zaien = salaryBuilder.build();

	    zaien.recalculateIncomeTax();

	    DecimalFormat df = new DecimalFormat("#,###.00");
	    System.out.println(String.format("basic pay %s, hence standard income tax: %s",
		    df.format(zaien.getRecurringPay()), df.format(zaien.getIncomeTax())));

	} finally {
	    scanner.close();
	}
		
	System.out.println("thank you, bye...");

    }

    private static BigDecimal collectSalaryMeasurementFromUser(Scanner scanner) {
	System.out.println("type your basic pay: ");
	String basicPay = scanner.next();
	return new BigDecimal(basicPay);
    }
    
    private static TaxDimension collectTaskDimensionFromUser(Scanner scanner) throws ContractException {
	
	System.out.println("are married? Y/N");
	String isMarried = scanner.next();

	System.out.println("how many child do you have for tax calculation ? 0 to 3");
	int numberofChild = scanner.nextInt();

	System.out.println("type your tax id (NPWP), or left it blank if you don't have it:");
	String npwp = scanner.next();
		
	System.out.println("are your income tax paid by your employer? Y/N");
	String isPaidByEmployer = scanner.next();
	
	return new TaxDimension(
		isMarried.equalsIgnoreCase("Y") ? true : false,
		numberofChild,
		npwp,
		isPaidByEmployer.equalsIgnoreCase("Y") ? true : false,
		TaxCalculationType.Standard);
	
    }

}
