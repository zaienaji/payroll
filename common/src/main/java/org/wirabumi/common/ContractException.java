package org.wirabumi.common;

public class ContractException extends Exception {

    private static final long serialVersionUID = 1271872876918208978L;

    public ContractException() {
    }

    public ContractException(String message) {
	super(message);
    }

    public ContractException(String message, Exception inner) {
	super(message, inner);
    }

}
