package org.wirabumi.tax;

import org.wirabumi.payroll.algorithm.BinarySearch;

public class TaxCalculatorFactory {

    public TaxCalculator create(TaxDimension taxDimension) {
	
	TaxCalculator taxCalculator;

	if (taxDimension.isPaidByEmployeer())
	    taxCalculator = new NetTaxCalculator(new BinarySearch());
	else
	    taxCalculator = new GrossTaxCalculator();
	
	return new NoNpwpTaxCalculator(taxCalculator, taxDimension.getNpwp());
	
    }
}
