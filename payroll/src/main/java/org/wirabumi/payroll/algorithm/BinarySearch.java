package org.wirabumi.payroll.algorithm;

import java.math.BigDecimal;
import java.util.function.Function;

import javax.management.OperationsException;

import org.wirabumi.common.Contract;
import org.wirabumi.common.ContractException;

public class BinarySearch {

    private BigDecimal result;
    private boolean isFound;
    
    private Function<BigDecimal, BigDecimal> map;
    private Function<BigDecimal, Boolean> evaluate;
    
    private final static int MAX_ITERATION = 50;
    
    
    private int gapSign(BigDecimal middle) {
	return middle.subtract(map.apply(middle)).signum();
    }
    
    private BigDecimal calculateMiddleBound(BigDecimal min, BigDecimal max) {
	return max.subtract(min).divide(new BigDecimal(2)).add(min);
    }

    public BigDecimal getResult() {
	return result;
    }

    public boolean isFound() {
	return isFound;
    }
    
    public void doSearch(Function<BigDecimal, BigDecimal> map, Function<BigDecimal, Boolean> evaluate) throws ContractException, OperationsException {
	
	Contract.require(map!=null, "map is a mandatory field");
	Contract.require(evaluate!=null, "evaluate is a mandatory field");
	
	this.map = map;
	this.evaluate = evaluate;
	
	BigDecimal minSearchRange = calculateMinSearchRange();
	this.result = binarySearch(minSearchRange, calculateMaxSearch(minSearchRange));
	this.isFound = true;
    }

    private BigDecimal calculateMinSearchRange() throws OperationsException {
	BigDecimal minSearchRange = BigDecimal.ONE;
	
	for (int i=0; i<MAX_ITERATION ; i++) {
	    
	    if (map.apply(minSearchRange).signum() != 0)
		return minSearchRange;
	    
	    minSearchRange = minSearchRange.multiply(BigDecimal.TEN).negate();
	}
    
	throw new OperationsException(String.format("can nof find upperbound until %d iteration", MAX_ITERATION));
    }

    private BigDecimal calculateMaxSearch(BigDecimal min) throws OperationsException {

	BigDecimal incomeTax = map.apply(min);
	BigDecimal minGap = min.subtract(incomeTax);
	
	BigDecimal max = incomeTax;

	for (int i = 1; i<MAX_ITERATION; i++) {

	    max = max.multiply((new BigDecimal(i)).negate());
	    BigDecimal maxGap = max.subtract(map.apply(max));
	    
	    if (maxGap.signum() == 0)
		continue;

	    if (minGap.signum() != maxGap.signum())
		return max;
	}
	
	throw new OperationsException(String.format("can nof find upperbound until %d iteration", MAX_ITERATION));
    }

    private BigDecimal binarySearch(BigDecimal min, BigDecimal max) {
	BigDecimal middle = calculateMiddleBound(min, max);

	if (evaluate.apply(middle))
	    return middle;

	if (gapSign(min) != gapSign(middle))
	    return binarySearch(min, middle);
	
	return binarySearch(middle, max);
    }

}
