package org.svomz.apps.finances.ports.adapters.web.views;

import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IExpressionEnhancingDialect;
import org.thymeleaf.extras.java8time.expression.Temporals;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by eric on 01/03/16.
 */
public class FinancesDialect extends AbstractDialect implements IExpressionEnhancingDialect {

  private static final String TEMPORAL_EVALUATION_VARIABLE_NAME = "financesUtils";

  @Override
  public Map<String, Object> getAdditionalExpressionObjects(IProcessingContext processingContext) {
    Map<String, Object> expressionObjects = new HashMap<>();
    expressionObjects.put(TEMPORAL_EVALUATION_VARIABLE_NAME, new FinancesUtils());
    return expressionObjects;
  }

  @Override
  public String getPrefix() {
    return "fin";
  }
}
