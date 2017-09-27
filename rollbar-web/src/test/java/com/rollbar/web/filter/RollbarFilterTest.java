package com.rollbar.web.filter;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rollbar.notifier.Rollbar;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

public class RollbarFilterTest {

  static final Throwable ERROR = new RuntimeException("Something went wrong");

  @Rule
  public MockitoRule rule = MockitoJUnit.rule();

  @Mock
  Rollbar rollbar;

  @Mock
  ServletRequest request;

  @Mock
  ServletResponse response;

  @Mock
  FilterChain chain;

  RollbarFilter sut;

  @Before
  public void setUp() throws Exception {
    doThrow(ERROR).when(chain).doFilter(request, response);

    sut = new RollbarFilter(rollbar);
  }

  @Test
  public void shouldLogError() throws Exception {
    try {
      sut.doFilter(request, response, chain);
    } catch (Exception e) {
      if(!e.equals(ERROR)) {
        fail();
      }
    }
    verify(rollbar).error(ERROR);
  }

  @Test
  public void shouldSwallowException() throws Exception {
    doThrow(new RuntimeException("Error sending to Rollbar")).when(rollbar).
        error(any(Throwable.class));

    try {
      sut.doFilter(request, response, chain);
    } catch (Exception e) {
      if(!e.equals(ERROR)) {
        fail();
      }
    }
  }
}