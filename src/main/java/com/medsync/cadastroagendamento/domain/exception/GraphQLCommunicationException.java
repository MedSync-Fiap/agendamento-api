package com.medsync.cadastroagendamento.domain.exception;

public class GraphQLCommunicationException extends InfrastructureException {
    
    public static final String ERROR_CODE = "GRAPHQL_COMMUNICATION_ERROR";
    
    public GraphQLCommunicationException(String message) {
        super(message, ERROR_CODE);
    }
    
    public GraphQLCommunicationException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }
    
    public GraphQLCommunicationException(String message, Object... parameters) {
        super(message, ERROR_CODE, parameters);
    }
    
    public GraphQLCommunicationException(String message, Throwable cause, Object... parameters) {
        super(message, cause, ERROR_CODE, parameters);
    }
}
