package org.wirabumi.tax;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;

import javax.management.OperationsException;

import org.wirabumi.common.Contract;
import org.wirabumi.common.ContractException;

public abstract class TaxCalculator {
    
    private static final BigDecimal POSITION_COST_TARIFF = new BigDecimal("0.05");
    private static final BigDecimal MAX_POSITION_COST = new BigDecimal("6000000");
    private static final BigDecimal MIN_INCOME = BigDecimal.ZERO;
    private static final BigDecimal MAX_INCOME = new BigDecimal("10000000000"); //10^9 

    private static final BigDecimal TAXABLE_INCOME_LAYER1 = new BigDecimal("50000000");
    private static final BigDecimal TAXABLE_INCOME_LAYER2 = new BigDecimal("250000000");
    private static final BigDecimal TAXABLE_INCOME_LAYER3 = new BigDecimal("500000000");

    private static final BigDecimal TAX_RATE_LAYER1 = new BigDecimal("0.05");
    private static final BigDecimal TAX_RATE_LAYER2 = new BigDecimal("0.15");
    private static final BigDecimal TAX_RATE_LAYER3 = new BigDecimal("0.25");
    private static final BigDecimal TAX_RATE_LAYER4 = new BigDecimal("0.30");

    private static final HashMap<PtkpKey, BigDecimal> ptkp = new HashMap<PtkpKey, BigDecimal>();
    private static final LinkedList<TaxRateLayer> taxRateLayersDescending = new LinkedList<TaxRateLayer>();
    
    public TaxCalculator() {

	initPtkp();
	initTaxableIncomeLayer();

    }
   
    private void initTaxableIncomeLayer() {
	
	taxRateLayersDescending.clear();
	
	taxRateLayersDescending.addFirst(new TaxRateLayer(MIN_INCOME, TAXABLE_INCOME_LAYER1, TAX_RATE_LAYER1));
	taxRateLayersDescending.addFirst(new TaxRateLayer(TAXABLE_INCOME_LAYER1, TAXABLE_INCOME_LAYER2, TAX_RATE_LAYER2));
	taxRateLayersDescending.addFirst(new TaxRateLayer(TAXABLE_INCOME_LAYER2, TAXABLE_INCOME_LAYER3, TAX_RATE_LAYER3));
	taxRateLayersDescending.addFirst(new TaxRateLayer(TAXABLE_INCOME_LAYER3, MAX_INCOME, TAX_RATE_LAYER4));
	
    }

    private void initPtkp() {

	ptkp.clear();

	ptkp.put(new PtkpKey(true, 0), new BigDecimal("58500000"));
	ptkp.put(new PtkpKey(true, 1), new BigDecimal("63000000"));
	ptkp.put(new PtkpKey(true, 2), new BigDecimal("67500000"));
	ptkp.put(new PtkpKey(true, 3), new BigDecimal("72000000"));

	ptkp.put(new PtkpKey(false, 0), new BigDecimal("54000000"));
	ptkp.put(new PtkpKey(false, 1), new BigDecimal("58500000"));
	ptkp.put(new PtkpKey(false, 2), new BigDecimal("63000000"));
	ptkp.put(new PtkpKey(false, 3), new BigDecimal("67500000"));

    }

    private BigDecimal positionCost(BigDecimal grossIncome) {

	BigDecimal result = grossIncome.multiply(POSITION_COST_TARIFF);
	if (result.compareTo(MAX_POSITION_COST) < 0)
	    return result;

	return MAX_POSITION_COST;
    }

    private BigDecimal nonTaxableIncome(TaxDimension taxDimension) throws ContractException {

	Contract.require(ptkp.containsKey(new PtkpKey(taxDimension.isMarried(), taxDimension.getNumberOfChilds())), 
		"can nof find PTKP, isMarried: " + taxDimension.isMarried() 
		+ ", number of children " + taxDimension.getNumberOfChilds());

	return ptkp.get(new PtkpKey(taxDimension.isMarried(), taxDimension.getNumberOfChilds()));
    }

    BigDecimal progressiveIncomeTax(BigDecimal taxableIncome) {
	
	if (taxableIncome.compareTo(BigDecimal.ZERO)<=0)
	    return BigDecimal.ZERO;
	
	BigDecimal rest = taxableIncome;
	BigDecimal result = BigDecimal.ZERO;
	for(TaxRateLayer layer : taxRateLayersDescending) {
	    if (!layer.contain(rest))
		continue;
	    
	    result = result.add(rest.subtract(layer.min).multiply(layer.taxRate));
	    rest = layer.min;
	}
	
	return result;
    }
    
    BigDecimal calculateTaxableIncome(TaxDimension taxDimension,
	    BigDecimal grossIncome)
	    throws ContractException {

	return grossIncome
		.subtract(positionCost(grossIncome))
		.subtract(nonTaxableIncome(taxDimension));

    }
    
    abstract BigDecimal incomeTax(BigDecimal taxableIncome)
	    throws OperationsException, ContractException;

    public BigDecimal calculateIncomeTax(TaxDimension taxDimension, BigDecimal grossIncome)
	    throws OperationsException, ContractException{
	
	BigDecimal taxableIncome = calculateTaxableIncome(taxDimension, grossIncome);
	
	Contract.require(taxableIncome.compareTo(MAX_INCOME)<=0, 
		"taxable income reach max income supported in this tax calculator");
	
	return incomeTax(taxableIncome);
    }

    private class PtkpKey {

	private boolean isMarried;
	private int numberOfChilds;

	public PtkpKey(boolean isMarried, int numberOfChilds) {
	    this.isMarried = isMarried;
	    this.numberOfChilds = numberOfChilds;
	}

	@Override
	public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + (isMarried ? 1231 : 1237);
	    result = prime * result + numberOfChilds;
	    return result;
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj)
		return true;
	    if (obj == null)
		return false;
	    if (getClass() != obj.getClass())
		return false;
	    PtkpKey other = (PtkpKey) obj;
	    if (isMarried != other.isMarried)
		return false;
	    if (numberOfChilds != other.numberOfChilds)
		return false;
	    return true;
	}

    }
    
    private class TaxRateLayer{
	
	private BigDecimal min;
	private BigDecimal max;
	private BigDecimal taxRate;
	
	public TaxRateLayer(BigDecimal min, BigDecimal max, BigDecimal taxRate) {
	    super();
	    this.min = min;
	    this.max = max;
	    this.taxRate = taxRate;
	}
	
	public boolean contain(BigDecimal value) {
	    return value.compareTo(min)>0 && value.compareTo(max)<=0;
	}
    }

}
