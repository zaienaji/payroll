package org.wirabumi.tax;

import java.math.BigDecimal;
import java.util.function.Function;

import javax.management.OperationsException;

import org.wirabumi.common.ContractException;
import org.wirabumi.payroll.algorithm.BinarySearch;

public class NetTaxCalculator extends TaxCalculator {
    
    private final static BigDecimal EPSILON = new BigDecimal(1000);

    private final BinarySearch binarySearch;
    
    public NetTaxCalculator(BinarySearch binarySearch) {
	super();
	this.binarySearch = binarySearch;
    }

    @Override
    BigDecimal incomeTax(BigDecimal taxableIncome) 
	    throws OperationsException, ContractException{
	
	return searchNetIncomeTax(taxableIncome); 
    }
    
    private BigDecimal searchNetIncomeTax(BigDecimal taxableIncome)
	    throws ContractException, OperationsException {

	Function<BigDecimal, BigDecimal> map = t -> progressiveIncomeTax(t.add(taxableIncome));
	Function<BigDecimal, Boolean> evaluate = t -> t.subtract(map.apply(t)).abs().compareTo(EPSILON) <= 0;
	
	binarySearch.doSearch(map, evaluate);

	if (binarySearch.isFound())
	    return binarySearch.getResult();

	throw new OperationsException("net tax calculator can not find income tax allowance");

    }
    
}
