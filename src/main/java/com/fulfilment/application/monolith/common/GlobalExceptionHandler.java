package com.fulfilment.application.monolith.common;

import jakarta.persistence.OptimisticLockException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

  private static final Logger LOGGER = Logger.getLogger(GlobalExceptionHandler.class);

  @Override
  public Response toResponse(Exception exception) {
    LOGGER.error("Unhandled exception caught: ", exception);

    if (exception instanceof WebApplicationException) {
      return ((WebApplicationException) exception).getResponse();
    }

    if (exception instanceof OptimisticLockException || exception.getCause() instanceof OptimisticLockException) {
      return Response.status(Response.Status.CONFLICT)
          .entity(new ErrorResponse("Conflict", "The resource was modified by another user. Please retry."))
          .build();
    }

    if (exception instanceof IllegalArgumentException) {
      return Response.status(Response.Status.BAD_REQUEST)
          .entity(new ErrorResponse("Bad Request", exception.getMessage()))
          .build();
    }

    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
        .entity(new ErrorResponse("Internal Server Error", "An unexpected error occurred."))
        .build();
  }

  public static class ErrorResponse {
    public String type;
    public String message;

    public ErrorResponse(String type, String message) {
      this.type = type;
      this.message = message;
    }
  }
}
