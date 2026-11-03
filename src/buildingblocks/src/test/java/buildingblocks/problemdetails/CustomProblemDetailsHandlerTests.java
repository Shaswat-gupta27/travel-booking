package buildingblocks.problemdetails;

import buildingblocks.core.exception.ConflictException;
import buildingblocks.core.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationDeniedException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomProblemDetailsHandlerTests {
  private final CustomProblemDetailsHandler handler = new CustomProblemDetailsHandler(new MockEnvironment());

  private HttpStatus statusOf(Exception exception) {
    ResponseEntity<ProblemDetail> response = handler.handleAllExceptions(exception, null);
    return HttpStatus.valueOf(response.getStatusCode().value());
  }

  @Test
  void should_answer_403_when_a_role_check_fails() {
    assertEquals(HttpStatus.FORBIDDEN, statusOf(new AuthorizationDeniedException("Access Denied", new AuthorizationDecision(false))));
    assertEquals(HttpStatus.FORBIDDEN, statusOf(new AccessDeniedException("Access Denied")));
  }

  @Test
  void should_keep_the_other_mappings() {
    assertEquals(HttpStatus.NOT_FOUND, statusOf(new NotFoundException("missing")));
    assertEquals(HttpStatus.CONFLICT, statusOf(new ConflictException("exists")));
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, statusOf(new IllegalStateException("boom")));
  }
}
