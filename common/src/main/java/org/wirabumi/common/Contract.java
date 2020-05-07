package org.wirabumi.common;

public class Contract {

    public static void require(boolean precondition) throws ContractException {
	require(precondition, "");
    }

    public static void require(Boolean precondition, String message) throws ContractException {

	if (!precondition)
	    throw new ContractException(message);

    }

}
