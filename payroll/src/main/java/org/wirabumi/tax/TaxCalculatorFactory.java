package org.wirabumi.tax;

import org.wirabumi.payroll.algorithm.BinarySearch;

public class TaxCalculatorFactory {

    public TaxCalculator create(TaxDimension taxDimension) {
	return new NoNpwpTaxCalculator(
		getTaxCalculatorByDimension(taxDimension), 
		taxDimension.getNpwp());
    }

    private TaxCalculator getTaxCalculatorByDimension(TaxDimension taxDimension) {
	if (taxDimension.isPaidByEmployer())
	    return new NetTaxCalculator(new BinarySearch());
	else
	    return new GrossTaxCalculator();
    }
}
