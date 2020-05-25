package org.wirabumi.tax;

import java.math.BigDecimal;

import javax.management.OperationsException;

import org.wirabumi.common.ContractException;

public class GrossTaxCalculator extends TaxCalculator {

    @Override
    BigDecimal incomeTax(BigDecimal taxableIncome) {
	return progressiveIncomeTax(taxableIncome); 
	
    }

    @Override
    BigDecimal taxAllowance(BigDecimal taxableIncome) throws OperationsException, ContractException {
	return BigDecimal.ZERO;
    }

}
