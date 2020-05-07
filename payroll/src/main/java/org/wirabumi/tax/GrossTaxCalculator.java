package org.wirabumi.tax;

import java.math.BigDecimal;

public class GrossTaxCalculator extends TaxCalculator {

    @Override
    BigDecimal incomeTax(BigDecimal taxableIncome) {
	return progressiveIncomeTax(taxableIncome); 
	
    }

}
